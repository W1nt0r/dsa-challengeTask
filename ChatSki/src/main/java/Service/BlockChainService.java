package Service;

import org.ethereum.crypto.ECKey;
import org.spongycastle.util.encoders.Hex;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class BlockChainService {

    private final static String INFURA_TOKEN = "https://rinkeby.infura.io/v3/09637112a2034d23b2d9f1557aedac17";
    private final static String P_KEY = "073771C1FB6938E813CDDFF9783975918C47C4DF97B31548D1911AF2E28B426F";

    private Web3j web3j;
    private Credentials credentials;

    public BlockChainService() {
        web3j = Web3j.build(new HttpService(INFURA_TOKEN));
        loadCredentials();
    }

    public String getCredentialAddress() {
        return credentials.getAddress();
    }

    public synchronized List<Type> callConstant(Function function,
                                       String contractAddress)
            throws IOException, ExecutionException, InterruptedException {
        String encodedFunction = FunctionEncoder.encode(function);
        EthCall ethCall = web3j.ethCall(Transaction.createEthCallTransaction(
                credentials.getAddress(), contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST).sendAsync().get();

        if (ethCall.hasError()) {
            throw new IOException(ethCall.getError().getMessage());
        }
        return FunctionReturnDecoder.decode(ethCall.getValue(),
                function.getOutputParameters());
    }

    public synchronized void call(Function function, String contractAddress,
                     BigInteger gasPrice, BigInteger gasLimit)
            throws ExecutionException, InterruptedException, IOException {
        String signedMessage = createSignedMessage(function, contractAddress,
                gasPrice, gasLimit);

        EthSendTransaction sendTransaction =
                web3j.ethSendRawTransaction(signedMessage).sendAsync().get();
        if (sendTransaction.hasError()) {
            throw new IOException(sendTransaction.getError().getMessage());
        }

        Request<?, EthGetTransactionReceipt> request =
                web3j.ethGetTransactionReceipt(sendTransaction.getTransactionHash());

        System.out.println("Waiting for transaction receipt");

        EthGetTransactionReceipt receipt = null;
        while (receipt == null || receipt.getResult() == null) {
            CompletableFuture<EthGetTransactionReceipt> fu = request.sendAsync();
            receipt = fu.get();
        }

        if (!receipt.getResult().isStatusOK()) {
            throw new IOException("Putting to block-chain failed");
        }
        System.out.println("Transaction receipt");
    }

    private void loadCredentials() {
        ECKey key = ECKey.fromPrivate(Hex.decode(P_KEY));
        BigInteger privateKey = key.getPrivKey();
        ECKeyPair pair = new ECKeyPair(privateKey, Sign.publicKeyFromPrivate(privateKey));
        credentials = Credentials.create(pair);
    }

    private String createSignedMessage(Function function, String contractAddress,
                                       BigInteger gasPrice, BigInteger gasLimit)
            throws ExecutionException, InterruptedException {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                credentials.getAddress(), DefaultBlockParameterName.LATEST)
                .sendAsync().get();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        String encodedFunction = FunctionEncoder.encode(function);
        RawTransaction transaction = RawTransaction.createTransaction(nonce,
                gasPrice, gasLimit, contractAddress, BigInteger.ZERO,
                encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction,
                credentials);
        return Numeric.toHexString(signedMessage);
    }

}

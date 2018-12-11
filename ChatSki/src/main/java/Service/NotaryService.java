package Service;

import DomainObjects.NotaryMessage;
import org.ethereum.crypto.HashUtil;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class NotaryService {

    private final static String CONTRACT_ADDRESS = "0xf37d0aa19ec6a0340ccf09c4ba2cfb34069298b3";
    private final static BigInteger GAS_PRICE = BigInteger.valueOf(2_500_000_000L);
    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(500_000);
    private static final BlockChainService blockChain = new BlockChainService();

    public static void notarizeMessage(NotaryMessage message,
                                       Consumer<NotaryMessage> onSuccess,
                                       Consumer<Throwable> onError) {

        byte[] messageHash = HashUtil.sha3(message.toString().getBytes());
        System.out.println("Put message to BlockChain: " + Numeric.toHexString(messageHash));
        System.out.println("with wallet: " + blockChain.getCredentialAddress());

        Function function = new Function("notarizeMessage",
                Collections.singletonList(new Bytes32(messageHash)),
                new ArrayList<>());

        new Thread(() -> {
            try {
                blockChain.call(function, CONTRACT_ADDRESS, GAS_PRICE, GAS_LIMIT);
                onSuccess.accept(message);
            } catch (Exception e) {
                onError.accept(e);
            }
        }).start();
    }

    public static void verifyMessage(
            NotaryMessage message,
            BiConsumer<NotaryMessage, Boolean> onSuccess,
            Consumer<Throwable> onError) {

        byte[] messageHash = HashUtil.sha3(message.toString().getBytes());
        System.out.println("Verifying message: " + Numeric.toHexString(messageHash));
        String address = blockChain.getCredentialAddress();

        Function function = new Function("verifyMessage",
                Arrays.asList(new Address(address), new Bytes32(messageHash)),
                Collections.singletonList(TypeReference.create(Bool.class)));

        new Thread(() -> {
            try {
                List<Type> outputs = blockChain.callConstant(function, CONTRACT_ADDRESS);
                boolean accepted = (boolean) outputs.get(0).getValue();
                System.out.println("Verifying result: " + accepted);
                onSuccess.accept(message, accepted);
            } catch (Exception e) {
                onError.accept(e);
            }
        }).start();
    }
}

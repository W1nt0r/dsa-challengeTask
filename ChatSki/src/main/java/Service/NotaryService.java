package Service;

import DomainObjects.NotaryMessage;
import io.iconator.testonator.DeployedContract;
import io.iconator.testonator.FunctionBuilder;
import io.iconator.testonator.TestBlockchain;
import io.iconator.testonator.jsonrpc.EthJsonRpcImpl;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;

import static io.iconator.testonator.TestBlockchain.fromECPrivateKey;

public class NotaryService {

    private final static String ADDRESS = "0xf37d0aa19ec6a0340ccf09c4ba2cfb34069298b3";
    private final static String INFURA_TOKEN = "https://rinkeby.infura.io/v3/09637112a2034d23b2d9f1557aedac17";
    private final static String P_KEY = "073771C1FB6938E813CDDFF9783975918C47C4DF97B31548D1911AF2E28B426F";

    public static void notarizeMessage(NotaryMessage message){
        DeployedContract contract = new DeployedContract(null, ADDRESS, null, null,null);
        String messageHash = Hash.sha3(message.toString());

        try {
            TestBlockchain testBlockchain = TestBlockchain.runRemote(INFURA_TOKEN);
            Credentials cred = fromECPrivateKey(P_KEY);
            testBlockchain.call(cred, contract,
                    new FunctionBuilder("notarizeMessage")
                            .addInput("bytes32", messageHash));
        }
        catch (Exception e){

        }
    }

    public void verifyMessage(){

    }
}

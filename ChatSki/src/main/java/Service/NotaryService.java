package Service;

import DomainObjects.NotaryMessage;
import io.iconator.testonator.DeployedContract;
import io.iconator.testonator.Event;
import io.iconator.testonator.FunctionBuilder;
import io.iconator.testonator.TestBlockchain;
import org.ethereum.crypto.HashUtil;
import org.web3j.crypto.Credentials;

import java.util.List;


public class NotaryService {

    private final static String ADDRESS = "0xf37d0aa19ec6a0340ccf09c4ba2cfb34069298b3";
    private final static String INFURA_TOKEN = "https://rinkeby.infura.io/v3/09637112a2034d23b2d9f1557aedac17";
    private final static String P_KEY = "073771C1FB6938E813CDDFF9783975918C47C4DF97B31548D1911AF2E28B426F";

    public static void notarizeMessage(NotaryMessage message) {
        DeployedContract contract = new DeployedContract(null, ADDRESS, null, null, null);
        byte[] messageHash = HashUtil.sha3(message.toString().getBytes());

        try {
            TestBlockchain testBlockchain = TestBlockchain.runRemote(INFURA_TOKEN);
            Credentials cred = TestBlockchain.fromECPrivateKey(P_KEY);

            List<Event> events = testBlockchain.call(cred, contract,
                    new FunctionBuilder("notarizeMessage")
                            .addInput("bytes32", messageHash));
            int i;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean verifyMessage(NotaryMessage message) {
        boolean result = false;
        DeployedContract contract = new DeployedContract(null, ADDRESS, null, null, null);
        byte[] messageHash = HashUtil.sha3(message.toString().getBytes());
        Credentials credentials = TestBlockchain.fromECPrivateKey(P_KEY);
        String address = credentials.getAddress();
        try {
            TestBlockchain testBlockchain = TestBlockchain.runRemote(INFURA_TOKEN);
            List<Event> events = testBlockchain.call(credentials, contract, new FunctionBuilder("verifyMessage").addInput("address", address).addInput("bytes32", messageHash).outputs("bool"));

            int i=0;

            /*

            List<Event> events = blockchain.call(deployed,
               new FunctionBuilder("transfer")
                       .addInput("address", CREDENTIAL_1.getAddress())
                       .addInput("uint256", new BigInteger("10000"))
                       .outputs("bool"));

            Assert.assertEquals(1, events.size());
            Assert.assertEquals("Transfer", events.get(0).name());
            Assert.assertEquals(CREDENTIAL_0.getAddress(), events.get(0).values().get(0).getValue().toString());
            Assert.assertEquals(CREDENTIAL_1.getAddress(), events.get(0).values().get(1).getValue().toString());
            Assert.assertEquals("10000", events.get(0).values().get(2).getValue().toString());
             */
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return false;
        }
    }
}

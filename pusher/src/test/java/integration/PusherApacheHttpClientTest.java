package integration;

import com.github.pubnubjtools.pusher.Pusher;
import com.github.pubnubjtools.pusher.PusherBuilder;
import com.github.pubnubjtools.pusher.model.*;
import com.github.pubnubjtools.pusher.model.PubnubException;
import com.github.pubnubjtools.pusher.model.api.Messages;
import com.github.pubnubjtools.pusher.transport.httpclient.ApacheHttpClientTransport;
import org.apache.http.impl.client.HttpClients;
import com.pubnub.api.Pubnub;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class PusherApacheHttpClientTest {

    Pusher pusher = new PusherBuilder().buildSyncPusher(new ApacheHttpClientTransport(HttpClients.createDefault()));
    Credentials credentials = TestCredentials.CREDENTIALS;
    EncryptionParameters encryptionParameters = new EncryptionParameters("demo");

    // due to subscribing is not mission of pusher lets test our implementation via another library
    Pubnub pubnub_enc = new Pubnub(TestCredentials.PUBLISHER_KEY, TestCredentials.SUBSCRIBER_KEY, TestCredentials.SECRET_KEY, encryptionParameters.getCipherKey(), false);

    @Test
    public void testPushString() throws PubnubException {
        int payload = 666;
        String serializedPayload = "666";
        String channel = "java-unittest-" + Math.random();
        Message message = Messages.publish().unsecure(payload, serializedPayload, channel, credentials);
        printResult(pusher.push(message));
    }

    @Test
    public void testBindChannelToApns() throws PubnubException {
        String deviceToken = "3132333435363738313233343536373831323334353637383132333435363738";
        String channel = "java-unittest-" + Math.random();
        Message message = Messages.cloudBinding().bindToApns(deviceToken, channel, credentials);
        printResult(pusher.push(message));
    }

    @Test
    public void testPublishStringWithEncryption() throws InterruptedException, com.pubnub.api.PubnubException {
        String channel = "java-unittest-" + Math.random();
        final String sendMessage = "Test Message " + Math.random();
        final CountDownLatch latch = new CountDownLatch(2);

        PubnubTest.SubscribeCallback sbCb = new PubnubTest.SubscribeCallback(latch) {
            @Override
            public void connectCallback(String channel, Object something) {
                Message message = Messages.publish().encrypted(sendMessage, sendMessage, channel, credentials, encryptionParameters);
                try {
                    pusher.push(message);
                } catch (PubnubException e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        };

        pubnub_enc.subscribe(channel, sbCb);
        latch.await(100000, TimeUnit.SECONDS);

        assertEquals(sendMessage, sbCb.getResponse());
    }

    private void printResult(DeliveryResult result) {
        for (VerboseLevel level : VerboseLevel.values()) {
            String description = result.getDescription(level);
            System.out.println("" + level + ":" + description);
        }
    }

}

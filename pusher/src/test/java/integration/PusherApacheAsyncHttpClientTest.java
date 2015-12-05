package integration;

import com.ringcentral.pubnubjtools.pusher.AsyncPusher;
import com.ringcentral.pubnubjtools.pusher.PusherBuilder;
import com.ringcentral.pubnubjtools.pusher.model.*;
import com.ringcentral.pubnubjtools.pusher.model.api.Messages;
import com.ringcentral.pubnubjtools.pusher.transport.asynchttpclient.AsyncApacheHttpClientTransport;
import com.pubnub.api.Pubnub;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class PusherApacheAsyncHttpClientTest {

    AsyncPusher pusher = PusherBuilder.forTransport(new AsyncApacheHttpClientTransport()).build();
    Credentials credentials = TestCredentials.CREDENTIALS;
    EncryptionParameters encryptionParameters = new EncryptionParameters("demo");

    // due to subscribing is not mission of pusher lets test our implementation via another library
    Pubnub pubnub_enc = new Pubnub(TestCredentials.PUBLISHER_KEY, TestCredentials.SUBSCRIBER_KEY, TestCredentials.SECRET_KEY, encryptionParameters.getCipherKey(), false);

    @Test
    public void testPushString() throws PubnubException, ExecutionException, InterruptedException {
        int payload = 666;
        String serializedPayload = "666";
        String channel = "java-unittest-" + Math.random();
        Message message = Messages.publish().unsecure(payload, serializedPayload, channel, credentials);
        printResult(pusher.push(message).get());
    }

    @Test
    public void testBindChannelToApns() throws PubnubException, ExecutionException, InterruptedException {
        String deviceToken = "3132333435363738313233343536373831323334353637383132333435363738";
        String channel = "java-unittest-" + Math.random();
        Message message = Messages.cloudBinding().bindToApns(deviceToken, channel, credentials);
        printResult(pusher.push(message).get());
    }

    @Test
    public void testPublishStringWithEncryption() throws InterruptedException, com.pubnub.api.PubnubException {
        String channel = "java-unittest-" + Math.random();
        final String sendMessage = "Test Message " + Math.random();
        final CountDownLatch latch = new CountDownLatch(2);

        TestHelper.SubscribeCallback sbCb = new TestHelper.SubscribeCallback(latch) {
            @Override
            public void connectCallback(String channel, Object something) {
                Message message = Messages.publish().encrypted(sendMessage, sendMessage, channel, credentials, encryptionParameters);
                try {
                    printResult(pusher.push(message).get());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            }
        };

        pubnub_enc.subscribe(channel, sbCb);
        latch.await(10000, TimeUnit.SECONDS);

        assertEquals(sendMessage, sbCb.getResponse());
    }

    private void printResult(DeliveryResult result) {
        for (VerboseLevel level : VerboseLevel.values()) {
            String description = result.getDescription(level);
            System.out.println("" + level + ":" + description);
        }
    }

}

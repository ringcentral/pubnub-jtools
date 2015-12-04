package integration;

import com.github.pubnubjtools.pusher.Pusher;
import com.github.pubnubjtools.pusher.PusherBuilder;
import com.github.pubnubjtools.pusher.model.*;
import com.github.pubnubjtools.pusher.model.api.Messages;
import com.github.pubnubjtools.pusher.transport.urlconnection.HttpURLConnectionTransport;
import org.junit.Test;

public class PusherHttpUrlConnectionTest {

    Pusher pusher = new PusherBuilder().buildSyncPusher(new HttpURLConnectionTransport(10000, 15000));
    Credentials credentials = TestCredentials.CREDENTIALS;

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

    private void printResult(DeliveryResult result) {
        for (VerboseLevel level : VerboseLevel.values()) {
            String description = result.getDescription(level);
            System.out.println("" + level + ":" + description);
        }
    }

}

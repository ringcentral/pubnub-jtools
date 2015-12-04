package integration;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class PubnubTest {

    Pubnub pubnub = new Pubnub(TestCredentials.PUBLISHER_KEY, TestCredentials.SUBSCRIBER_KEY);
    Pubnub pubnub_enc = new Pubnub(TestCredentials.PUBLISHER_KEY, TestCredentials.SUBSCRIBER_KEY, TestCredentials.SECRET_KEY, "demo", false);

    public static class SubscribeCallback extends Callback {

        private CountDownLatch latch;

        private Object response;

        public SubscribeCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        public SubscribeCallback() {

        }

        public Object getResponse() {
            return response;
        }

        @Override
        public void successCallback(String channel, Object message) {
            response = message;
            if (latch != null)
                latch.countDown();
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            response = error;
            if (latch != null)
                latch.countDown();
        }
    }

    public static class PublishCallback extends Callback {

        private CountDownLatch latch;
        private int result = 0;

        public int getResult() {
            return result;
        }

        public PublishCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        public PublishCallback() {

        }

        public void successCallback(String channel, Object message) {
            JSONArray jsarr;
            try {
                jsarr = (JSONArray) message;
                result = (Integer) jsarr.get(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (latch != null)
                latch.countDown();
        }

        public void errorCallback(String channel, PubnubError error) {
            JSONArray jsarr;
            result = 0;
            if (latch != null)
                latch.countDown();
        }

    }

    @Test
    public void testPublishString() throws PubnubException, InterruptedException {
        String channel = "java-unittest-" + Math.random();
        final String sendMessage = "Test Message " + Math.random();

        final CountDownLatch latch = new CountDownLatch(2);

        final PublishCallback pbCb = new PublishCallback(latch);
        SubscribeCallback sbCb = new SubscribeCallback(latch) {
            @Override
            public void connectCallback(String channel, Object message) {
                pubnub.publish(channel, sendMessage, pbCb);
            }
        };

        pubnub.subscribe(channel, sbCb);

        latch.await(1000, TimeUnit.SECONDS);

        assertEquals(1, pbCb.getResult());
        assertEquals(sendMessage, sbCb.getResponse());
    }

    @Test
    public void testPublishJSONArray() throws PubnubException, InterruptedException {
        String channel = "java-unittest-" + Math.random();
        final JSONArray sendMessage = new JSONArray().put(1).put("Test");

        final CountDownLatch latch = new CountDownLatch(2);

        final PublishCallback pbCb = new PublishCallback(latch);
        SubscribeCallback sbCb = new SubscribeCallback(latch) {
            @Override
            public void connectCallback(String channel, Object message) {
                pubnub.publish(channel, sendMessage, pbCb);
            }
        };

        pubnub.subscribe(channel, sbCb);
        latch.await(10, TimeUnit.SECONDS);

        assertEquals(1, pbCb.getResult());
        assertEquals(sendMessage.toString(), sbCb.getResponse().toString());
    }

    @Test
    public void testPublishJSONObject() throws InterruptedException, PubnubException, JSONException {
        String channel = "java-unittest-" + Math.random();

        final JSONObject sendMessage;

        sendMessage = new JSONObject().put("1", "Test");

        final CountDownLatch latch = new CountDownLatch(2);

        final PublishCallback pbCb = new PublishCallback(latch);
        SubscribeCallback sbCb = new SubscribeCallback(latch) {
            @Override
            public void connectCallback(String channel, Object message) {
                pubnub.publish(channel, sendMessage, pbCb);
            }
        };

        pubnub.subscribe(channel, sbCb);

        latch.await(10, TimeUnit.SECONDS);

        assertEquals(1, pbCb.getResult());
        assertEquals(sendMessage.toString(), sbCb.getResponse().toString());
    }

    @Test
    public void testPublishStringWithEncryption() throws InterruptedException, PubnubException {
        String channel = "java-unittest-" + Math.random();
        final String sendMessage = "Test Message " + Math.random();

        final CountDownLatch latch = new CountDownLatch(2);

        final PublishCallback pbCb = new PublishCallback(latch);
        SubscribeCallback sbCb = new SubscribeCallback(latch) {
            @Override
            public void connectCallback(String channel, Object message) {
                pubnub_enc.publish(channel, sendMessage, pbCb);
            }
        };

        pubnub_enc.subscribe(channel, sbCb);
        latch.await(10, TimeUnit.SECONDS);

        assertEquals(1, pbCb.getResult());
        assertEquals(sendMessage, sbCb.getResponse());
    }

    @Test
    public void testPublishJSONArrayWithEncryption() throws PubnubException, InterruptedException {
        String channel = "java-unittest-" + Math.random();
        final JSONArray sendMessage = new JSONArray().put(1).put("Test");

        final CountDownLatch latch = new CountDownLatch(2);

        final PublishCallback pbCb = new PublishCallback(latch);
        SubscribeCallback sbCb = new SubscribeCallback(latch) {
            @Override
            public void connectCallback(String channel, Object message) {
                pubnub_enc.publish(channel, sendMessage, pbCb);
            }
        };

        pubnub_enc.subscribe(channel, sbCb);
        latch.await(10, TimeUnit.SECONDS);

        assertEquals(1, pbCb.getResult());
        assertEquals(sendMessage.toString(), sbCb.getResponse().toString());
    }

    @Test
    public void testPublishJSONObjectWithEncryption() throws InterruptedException, PubnubException, JSONException {
        String channel = "java-unittest-" + Math.random();

        final JSONObject sendMessage;

        sendMessage = new JSONObject().put("1", "Test");

        final CountDownLatch latch = new CountDownLatch(2);

        final PublishCallback pbCb = new PublishCallback(latch);
        SubscribeCallback sbCb = new SubscribeCallback(latch) {
            @Override
            public void connectCallback(String channel, Object message) {
                pubnub_enc.publish(channel, sendMessage, pbCb);
            }
        };

        pubnub_enc.subscribe(channel, sbCb);
        latch.await(10, TimeUnit.SECONDS);

        assertEquals(1, pbCb.getResult());
        assertEquals(sendMessage.toString(), sbCb.getResponse().toString());
    }

}

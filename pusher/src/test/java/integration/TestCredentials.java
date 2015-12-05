package integration;

import com.ringcentral.pubnubjtools.pusher.model.Credentials;

public class TestCredentials {

    public static String PUBLISHER_KEY = System.getProperty("publisherKey", "demo");
    public static String SUBSCRIBER_KEY = System.getProperty("subscriberKey", "demo");
    public static String SECRET_KEY = System.getProperty("secretKey", "demo");

    public static Credentials CREDENTIALS = new Credentials(PUBLISHER_KEY, SUBSCRIBER_KEY, SECRET_KEY);

}

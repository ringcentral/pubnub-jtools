package com.ringcentral.pubnubjtools.legacy;

import com.ringcentral.pubnubjtools.pusher.model.Credentials;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * PubNub Cryptography
 *
 */
public class PubnubCrypto {

    public static final String DEFAULT_INITIALIZATION_VECTOR = "0123456789012345";
    private static Charset UTF8 = Charset.forName("UTF-8");

    private final PaddedBufferedBlockCipher encryptCipher;
    private final PaddedBufferedBlockCipher decryptCipher;
    private final byte[] inputBuffer = new byte[16];
    private final byte[] outputBuffer = new byte[512];
    private final byte[] key;
    private final String cipherKey;

    public PubnubCrypto(String cipherKey, String initializationVector) throws Exception {
        this.cipherKey = cipherKey;

        byte[] bytes = this.cipherKey.getBytes(UTF8);
        key = new String(Hex.encode(sha256(bytes)), UTF8).substring(0, 32).toLowerCase().getBytes("UTF-8");
        encryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));

        decryptCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()));

        // create the initializationVector parameter
        ParametersWithIV parameterIV = new ParametersWithIV(new KeyParameter(key), initializationVector.getBytes("UTF-8"));

        encryptCipher.init(true, parameterIV);
        decryptCipher.init(false, parameterIV);
    }

    public String encrypt(String input) throws Exception {
        InputStream st = new ByteArrayInputStream(input.getBytes(UTF8));
        ByteArrayOutputStream ou = new ByteArrayOutputStream();
        CBCEncryptOrDecrypt(st, ou, true);
        return new String(Base64Encoder.encode(ou.toByteArray()));
    }

    private void CBCEncryptOrDecrypt(InputStream in, OutputStream out, boolean encrypt) throws Exception {
        PaddedBufferedBlockCipher cipher = (encrypt) ? encryptCipher : decryptCipher;
        int noBytesRead = 0; // number of bytes read from input
        int noBytesProcessed = 0; // number of bytes processed

        while ((noBytesRead = in.read(inputBuffer)) >= 0) {
            noBytesProcessed = cipher.processBytes(inputBuffer, 0, noBytesRead, outputBuffer, 0);
            out.write(outputBuffer, 0, noBytesProcessed);
        }
        noBytesProcessed = cipher.doFinal(outputBuffer, 0);
        out.write(outputBuffer, 0, noBytesProcessed);
        out.flush();
        in.close();
        out.close();
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                                  .digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * Get MD5
     *
     * @param input
     * @return byte[]
     */
    public static byte[] md5(String input) {
        MD5Digest digest = new MD5Digest();
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);
        digest.update(bytes, 0, bytes.length);
        byte[] md5 = new byte[digest.getDigestSize()];
        digest.doFinal(md5, 0);
        StringBuffer hex = new StringBuffer(md5.length * 2);
        for (int i = 0; i < md5.length; i++) {
            byte b = md5[i];
            if ((b & 0xFF) < 0x10) {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hexStringToByteArray(hex.toString());
    }

    /**
     * Get SHA256
     *
     * @param input
     * @return byte[]
     */
    public static byte[] sha256(byte[] input) {
        Digest digest = new SHA256Digest();
        byte[] resBuf = new byte[digest.getDigestSize()];
        byte[] bytes = input;
        digest.update(bytes, 0, bytes.length);
        digest.doFinal(resBuf, 0);
        return resBuf;
    }

    public static byte[] hexEncode(byte[] input) {
        return Hex.encode(input);
    }

    public static String urlEncode(String urlFragment) {
        try {
            return URLEncoder.encode(urlFragment, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static String singPayload(String payload, String channel, Credentials credentials) throws Exception {
        // Generate String to Sign
        String stringToSign = new StringBuffer()
                .append(credentials.getPublisherKey()).append('/')
                .append(credentials.getSubscriberKey()).append('/')
                .append(credentials.getSecretKey()).append('/')
                .append(channel).append('/')
                .append(payload).toString();

        // Generate signature
        byte[] md5SignatureBytes = md5(stringToSign);
        byte[] hexSignatureBytes = hexEncode(md5SignatureBytes);
        String signature = new String(hexSignatureBytes, UTF8);

        return signature;
    }

}

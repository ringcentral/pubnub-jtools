package com.ringcentral.pubnubjtools.pusher.model;

import com.ringcentral.pubnubjtools.legacy.PubnubCrypto;

public class EncryptionParameters implements Describable {

    private final String cipherKey;
    private final String initializationVector;

    public EncryptionParameters(String cipherKey) {
        this(cipherKey, PubnubCrypto.DEFAULT_INITIALIZATION_VECTOR);
    }

    public EncryptionParameters(String cipherKey, String initializationVector) {
        this.cipherKey = cipherKey;
        this.initializationVector = initializationVector;
    }

    public String getCipherKey() {
        return cipherKey;
    }

    public String getInitializationVector() {
        return initializationVector;
    }

    @Override
    public String toString() {
        return "EncryptionParameters{" +
                "cipherKey='" + cipherKey + '\'' +
                ", initializationVector='" + initializationVector + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EncryptionParameters that = (EncryptionParameters) o;

        if (cipherKey != null ? !cipherKey.equals(that.cipherKey) : that.cipherKey != null) return false;
        return !(initializationVector != null ? !initializationVector.equals(that.initializationVector) : that.initializationVector != null);

    }

    @Override
    public int hashCode() {
        int result = cipherKey != null ? cipherKey.hashCode() : 0;
        result = 31 * result + (initializationVector != null ? initializationVector.hashCode() : 0);
        return result;
    }

    @Override
    public String getTraceDescription() {
        return toString();
    }

    @Override
    public String getDebugDescription() {
        return toString();
    }

    @Override
    public String getInfoDescription() {
        return toString();
    }

}

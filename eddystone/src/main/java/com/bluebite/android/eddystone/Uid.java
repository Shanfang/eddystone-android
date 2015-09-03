package com.bluebite.android.eddystone;

public class Uid extends EddystoneObject {
    private String mNamespace;
    private String mInstance;

    public String getUid() {
        return mNamespace + mInstance;
    }

    public String getNamespace() {
        return mNamespace;
    }

    public String getInstance() {
        return mInstance;
    }

    protected Uid(String namespace, String instance, Beacon.SignalStrength signalStrength, String identifier) {
        super(signalStrength, namespace + instance + identifier);
        mNamespace = namespace;
        mInstance = instance;
    }


}

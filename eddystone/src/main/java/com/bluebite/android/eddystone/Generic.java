package com.bluebite.android.eddystone;

import android.net.Uri;

public class Generic extends EddystoneObject {

    private Uri mUrl;
    private String mNamespace;
    private String mInstance;

    public String getUid() {
        if (!mNamespace.isEmpty() && !mInstance.isEmpty()) {
            return mNamespace + mInstance;
        }
        return null;
    }

    public Uri getUrl() {
        return mUrl;
    }

    public String getNamespace() {
        return mNamespace;
    }

    public String getInstance() {
        return mInstance;
    }

    private Generic(Uri url, String namespace, String instance, Beacon.SignalStrength signalStrength, String identifier) {
        super(signalStrength, identifier);
        mUrl = url;
        mNamespace = namespace;
        mInstance = instance;
    }

    protected static Generic makeGeneric(Uri url, String namespace, String instance, Beacon.SignalStrength signalStrength, String identifier) {
        String urlString = "";
        String absoluteString = url.toString();
        if (!absoluteString.isEmpty()) {
            urlString = absoluteString;
        }

        String uid = "";
        if (!namespace.isEmpty() && !instance.isEmpty()) {
            uid = namespace + instance;
        }

        return new Generic(url, namespace, instance, signalStrength, urlString + uid + identifier);
    }
}

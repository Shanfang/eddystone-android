package com.bluebite.android.eddystone;

import android.net.Uri;

public class Url extends EddystoneObject {
    private Uri mUrl;

    public Uri getUrl() {
        return mUrl;
    }

    private Url(Uri url, Beacon.SignalStrength signalStrength, String identifier) {
        super(signalStrength, identifier);
        mUrl = url;
    }

    protected static Url makeUrl(Uri uri, Beacon.SignalStrength signalStrength, String identifier) {
        String urlString = "";
        String absoluteString = uri.toString();
        if (!absoluteString.isEmpty()) {
            urlString = absoluteString;
        }
        return new Url(uri, signalStrength, urlString + identifier);
    }
}

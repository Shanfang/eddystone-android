package com.bluebite.android.eddystone;

import android.net.Uri;

public class UrlFrame extends Frame {
    protected Uri mUrl;

    private UrlFrame(Uri url) {
        this.mUrl = url;
    }

    protected static UrlFrame frameWithBytes(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bytes[2] < schemePrefixes.length) {
            stringBuilder.append(schemePrefixes[bytes[2]]);
        }
        for (int i = 3; i < bytes.length; i++) {
            if (bytes[i] < urlEncodings.length) {
                stringBuilder.append(urlEncodings[bytes[i]]);
            } else {
                stringBuilder.append((char) bytes[i]);
            }
        }
        String urlString = stringBuilder.toString();
        if (!urlString.isEmpty()) {
            Uri url = Uri.parse(urlString).normalizeScheme();
            return new UrlFrame(url);
        } else {
            Global.log("Invalid URL frame");
        }

        return null;
    }

    protected final static String[] schemePrefixes = {
            "http://www.",
            "https:/www.",
            "http://",
            "https://"
    };

    protected final static String[] urlEncodings = {
            ".com/",
            ".org/",
            ".edu/",
            ".net/",
            ".info/",
            ".biz/",
            ".gov/",
            ".com",
            ".org",
            ".edu",
            ".net",
            ".info",
            ".biz",
            ".gov"
    };
}

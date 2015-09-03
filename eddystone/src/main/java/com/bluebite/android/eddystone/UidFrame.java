package com.bluebite.android.eddystone;

public class UidFrame extends Frame {
    protected String mNamespace;
    protected String mInstance;

    protected String getUid() {
        return mNamespace + mInstance;
    }

    private UidFrame(String instance, String namespace) {
        mInstance = instance;
        mNamespace = namespace;
    }

    protected static UidFrame frameWithBytes(byte[] bytes) {
        StringBuilder namespaceSB = new StringBuilder();
        StringBuilder instanceSB = new StringBuilder();

        for (int i = 2; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xff);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }

            if (i <= 11) {
                namespaceSB.append(hex);
            } else if (i <= 17) {
                instanceSB.append(hex);
            }
        }
        String namespace = namespaceSB.toString();
        String instance = instanceSB.toString();

        if (namespace.length() == 20 && instance.length() == 12) {
            return new UidFrame(namespace, instance);
        } else {
            Global.log("Invalid UID frame");
        }

        return null;
    }
}

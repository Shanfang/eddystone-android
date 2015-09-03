package com.bluebite.android.eddystone;

public class TlmFrame extends Frame {
    protected int mBatteryVolts;
    protected double mTemperature;
    protected int mAdvertisementCount;
    protected int mOnTime;

    private TlmFrame(int batteryVolts, double temperature, int advertisementCount, int onTime) {
        mBatteryVolts = batteryVolts;
        mTemperature = temperature;
        mAdvertisementCount = advertisementCount;
        mOnTime = onTime;
    }

    protected static TlmFrame frameWithBytes(byte[] bytes) {
        int batteryVolts = 0;
        double temperature = 0;
        int advertisementCount = 0;
        int onTime;

        if (bytes.length <= 2 || bytes[1] != 0) {
            Global.log("Invalid TLM version, only 0 is supported");
            return null;
        }

        if (bytes.length >= 4) {
            byte[] vBattBytes = {bytes[2], bytes[3]};
            batteryVolts = intFromBytes(vBattBytes);
        }

        if (bytes.length >= 6) {
            temperature = from88FixedPoint(bytes[4], bytes[5]);
        }

        if (bytes.length >= 10) {
            byte[] advCountBytes = {bytes[6], bytes[7], bytes[8], bytes[9]};
            advertisementCount = intFromBytes(advCountBytes);
        }

        if (bytes.length >= 14) {
            byte[] onTimeBytes = {bytes[10], bytes[11], bytes[12], bytes[13]};
            onTime = intFromBytes(onTimeBytes);
            return new TlmFrame(batteryVolts, temperature, advertisementCount, onTime);
        } else {
            Global.log("Invalid TLM frame");
        }

        return null;
    }

    private static double from88FixedPoint(byte byte1, byte byte2) {
        double xDouble = 0;

        double i = (double) byte1;
        if (byte1 < 0) {
            i += 256;
        }
        double f = (double) byte2;
        if (byte2 < 0) {
            f += 256;
        }

        if (i >= 256 / 2) {
            i -= 256;
        }

        xDouble += i;
        xDouble += (1.0 / 256.0) * f;

        return xDouble;
    }

    private static int intFromBytes(byte[] bytes) {
        int x = 0;

        for (int i = 0; i < bytes.length; i++) {
            int factor = bytes.length - (i + 1);
            int b = ((int) bytes[i]);
            if (b < 0) {
                b += 256;
            }
            x += b * Math.pow(256, factor);
        }
        return x;
    }
}

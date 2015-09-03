package com.bluebite.android.eddystone;

public abstract class EddystoneObject {
    private Beacon.SignalStrength mSignalStrength;
    private String mIdentifier;

    private Double mBattery = null;
    private Double mTemperature = null;
    private Integer mAdvertisementCount = null;
    private Integer mOnTime = null; // In Seconds

    public EddystoneObject(Beacon.SignalStrength signalStrength, String identifier) {
        mSignalStrength = signalStrength;
        mIdentifier = identifier;
    }

    protected void parseTlmFrame(TlmFrame frame) {
        mBattery = batteryLevelInPercent(frame.mBatteryVolts);
        mTemperature = frame.mTemperature;
        mAdvertisementCount = frame.mAdvertisementCount;
        mOnTime = frame.mOnTime / 10;
    }

    private Double batteryLevelInPercent(int batteryVolts) {
        double batteryLevel;

        if (batteryVolts >= 3000) {
            batteryLevel = 100;
        } else if (batteryVolts > 2900) {
            batteryLevel = 100 - ((3000 - (double) batteryVolts) * 58) / 100;
        } else if (batteryVolts > 2740) {
            batteryLevel = 42 - ((2900 - (double) batteryVolts) * 24) / 160;
        } else if (batteryVolts > 2440) {
            batteryLevel = 18 - ((2740 - (double) batteryVolts) * 12) / 300;
        } else if (batteryVolts > 2100) {
            batteryLevel = 6 - ((2440 - (double) batteryVolts) * 6) / 340;
        } else {
            batteryLevel = 0;
        }
        return batteryLevel;
    }

    public Beacon.SignalStrength getSignalStrength() {
        return mSignalStrength;
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public Double getBattery() {
        return mBattery;
    }

    public Double getTemperature() {
        return mTemperature;
    }

    public Integer getAdvertisementCount() {
        return mAdvertisementCount;
    }

    public Integer getOnTime() {
        return mOnTime;
    }

    public String getReadableOnTime() {
        int second = 1;
        int minute = second * 60;
        int hour = minute * 60;
        int day = hour * 24;

        int num = mOnTime;
        String unit = "day";

        if (num >= day) {
            num /= day;
        } else if (num >= hour) {
            num /= hour;
            unit = "hour";
        } else if (num >= minute) {
            num /= minute;
            unit = "minute";
        } else if (num >= second) {
            num /= second;
            unit = "second";
        } else {
            num = 0;
        }

        if (num > 1) {
            unit += "s";
        }

        if (num == 0) {
            return "now";
        } else {
            return num + " " + unit;
        }
    }

    public boolean equals(EddystoneObject o) {
        return this.mIdentifier.equals(o.mIdentifier);
    }
}

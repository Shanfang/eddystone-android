package com.bluebite.android.eddystone;

import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;

import com.bluebite.android.eddystone.Global.FrameType;

import java.util.ArrayList;

public class Beacon implements Comparable<Beacon>{
    protected static final ParcelUuid EDDYSTONE_SPEC = ParcelUuid.fromString("0000feaa-0000-1000-8000-00805f9b34fb");
    protected Frame[] mFrames = {null, null, null}; // 3 possible frame types 0-URL 1-UID 2-TLM
    private int mTxPower;
    private String mIdentifier;
    private SignalStrength mSignalStrength = SignalStrength.UNKNOWN;
    private ArrayList<Double> mRssiBuffer = new ArrayList<>();
    protected BeaconDelegate mBeaconDelegate = null;

    public String getIdentifier() {
        return mIdentifier;
    }

    public SignalStrength getSignalStrength() {
        return mSignalStrength;
    }



    @Override
    public int compareTo(@NonNull Beacon another) {
        return (int) (this.getDistance() - another.getDistance());
    }

    public enum SignalStrength {
        EXCELLENT,
        VERY_GOOD,
        GOOD,
        LOW,
        VERY_LOW,
        NO_SIGNAL,
        UNKNOWN
    }

    public double getRssi() {
        double totalRssi = 0;
        for (double rssi : mRssiBuffer) {
            totalRssi += rssi;
        }

        return totalRssi / mRssiBuffer.size();
    }

    public double getDistance() {
        return calculateAccuracy(mTxPower, getRssi());
    }

    private double calculateAccuracy(int txPower, double rssi) {
        if (rssi == 0) {
            return 0;
        }

        double ratio = rssi / txPower;
        if (ratio < 1) {
            return Math.pow(ratio, 10);
        } else {
            return 0.89976 * Math.pow(ratio, 7.7095) + 0.111;
        }
    }

    private Beacon(double rssi, int txPower, String identifier) {
        mTxPower = txPower;
        mIdentifier = identifier;

        updateRssi(rssi);
    }

    private boolean updateRssi(double rssi) {
        mRssiBuffer.add(0, rssi);
        if (mRssiBuffer.size() > 20) {
            mRssiBuffer.remove(20);
        }

        SignalStrength signalStrength = calculateSignalStrength(getDistance());
        if (signalStrength != mSignalStrength) {
            mSignalStrength = signalStrength;
            notifyChange();
        }

        return false;
    }

    private SignalStrength calculateSignalStrength(double distance) {
        if (distance <= 24999) {
            return SignalStrength.EXCELLENT;
        } else if (distance <= 49999) {
            return SignalStrength.VERY_GOOD;
        } else if (distance <= 74999) {
            return SignalStrength.GOOD;
        } else if (distance <= 99999) {
            return SignalStrength.LOW;
        } else {
            return SignalStrength.VERY_LOW;
        }
    }

    private void notifyChange() {
        if (mBeaconDelegate != null) {
            mBeaconDelegate.beaconDidChange();
        }
    }

    // Bytes
    protected static Beacon beaconFromScanResult(ScanResult scanResult) {
        ScanRecord scanRecord = scanResult.getScanRecord();
        if (scanRecord == null) {
            return null;
        }
        FrameType type;
        Integer txPower;
        double rssi = scanResult.getRssi();
        byte[] bytes = scanRecord.getServiceData(EDDYSTONE_SPEC);
        String identifier = scanResult.getDevice().getAddress();

        txPower = txPowerFromBytes(bytes);

        if (txPower != null) {
            Beacon beacon = new Beacon(rssi, txPower, identifier);
            beacon.parseScanResult(scanResult);
            return beacon;
        }

        return null;
    }

    protected void parseScanResult(ScanResult scanResult) {
        ScanRecord scanRecord = scanResult.getScanRecord();
        if (scanRecord == null) {
            return;
        }
        double rssi = scanResult.getRssi();
        byte[] bytes = scanRecord.getServiceData(EDDYSTONE_SPEC);
        updateRssi(rssi);
        FrameType type = frameTypeFromBytes(bytes);
        if (type != null) {
            switch (type) {
                case URL:
                    UrlFrame urlFrame = UrlFrame.frameWithBytes(bytes);
                    if (urlFrame != null) {
                        mFrames[0] = urlFrame;
                        Global.log("Parsed URL Frame with mUrl: " + urlFrame.mUrl);
                        notifyChange();
                    }
                    break;
                case UID:
                    UidFrame uidFrame = UidFrame.frameWithBytes(bytes);
                    if (uidFrame != null) {
                        mFrames[1] = uidFrame;
                        Global.log("Parsed UID Frame with uid: " + uidFrame.getUid());
                        notifyChange();
                    }
                    break;
                case TLM:
                    TlmFrame tlmFrame = TlmFrame.frameWithBytes(bytes);
                    if (tlmFrame != null) {
                        mFrames[2] = tlmFrame;
                        Global.log("Parsed TLM Frame with battery: " + tlmFrame.mBatteryVolts + " temperature: " + tlmFrame.mTemperature + " advertisement count: " + tlmFrame.mAdvertisementCount
                                + " on time: " + tlmFrame.mOnTime);
                        notifyChange();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static Integer txPowerFromBytes(byte[] bytes) {
        if (bytes.length >= 2) {
            FrameType type = frameTypeFromBytes(bytes);
            if (type == FrameType.UID || type == FrameType.URL) {
                return (int) bytes[1];
            }
        }
        return null;
    }

    private static FrameType frameTypeFromBytes(byte[] bytes) {
        if (bytes.length > 1) {
            switch (bytes[0]) {
                case 0:
                    return FrameType.UID;
                case 16:
                    return FrameType.URL;
                case 32:
                    return FrameType.TLM;
                default:
                    break;
            }
        }
        return null;
    }

}

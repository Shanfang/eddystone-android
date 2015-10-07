package com.bluebite.android.eddystone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Scanner implements BeaconDelegate {
    private static Scanner instance = null;
    private ScannerDelegate mScannerDelegate;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            final String address = result.getDevice().getAddress();
            Beacon beacon = mDiscoveredBeacons.get(address);
            if (beacon != null) {
                beacon.parseScanResult(result);// Updating
            } else {
                beacon = Beacon.beaconFromScanResult(result);
                if (beacon != null) {
                    beacon.mBeaconDelegate = Scanner.getInstance();
                    mDiscoveredBeacons.put(address, beacon);
                    notifyChange();
                }
            }
            Timer timer;
            if (mBeaconTimers.containsKey(address)) {
                timer = mBeaconTimers.get(address);
                timer.cancel();
            }
            timer = new Timer();
            mBeaconTimers.put(address, timer);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Global.log("Beacon Lost");
                    mDiscoveredBeacons.remove(address);
                    notifyChange();
                }
            }, 15000);

        }
    };
    private HashMap<String, Beacon> mDiscoveredBeacons = new HashMap<>();
    private HashMap<String, Timer> mBeaconTimers = new HashMap<>();

    protected Scanner() {

    }

    private static Scanner getInstance() {
        if (instance == null) {
            instance = new Scanner();
        }
        return instance;
    }

    public static void start(ScannerDelegate scannerDelegate) {
        Scanner scanner = Scanner.getInstance();
        scanner.mScannerDelegate = scannerDelegate;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (bluetoothAdapter.isEnabled()) {
                scanner.mBluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
                scanner.mBluetoothLeScanner.startScan(Collections.singletonList(scanner.getScanFilter()), scanner.getScanSettings(), scanner.mScanCallback);
            } else {
                Global.log("Bluetooth is off");
            }
        } else {
            Global.log("Bluetooth not supported on device");
        }
    }

    // Return array of nearby Url objects
    public static Url[] nearbyUrls() {
        ArrayList<Url> urls = new ArrayList<>();
        Beacon[] beacons = getBeacons();

        for (Beacon beacon : beacons) {
            UrlFrame urlFrame = (UrlFrame) beacon.mFrames[0];
            if (urlFrame != null) {
                Url url = Url.makeUrl(urlFrame.mUrl, beacon.getSignalStrength(), beacon.getIdentifier());
                TlmFrame tlmFrame = (TlmFrame) beacon.mFrames[2];
                if (tlmFrame != null) {
                    url.parseTlmFrame(tlmFrame);
                }
                urls.add(url);
            }
        }
        return urls.toArray(new Url[urls.size()]);
    }

    // Return array of nearby Uid objects
    public static Uid[] nearbyUids() {
        ArrayList<Uid> uids = new ArrayList<>();
        Beacon[] beacons = getBeacons();

        for (Beacon beacon : beacons) {
            UidFrame uidFrame = (UidFrame) beacon.mFrames[1];
            if (uidFrame != null) {
                Uid uid = new Uid(uidFrame.mNamespace, uidFrame.mInstance, beacon.getSignalStrength(), beacon.getIdentifier());
                TlmFrame tlmFrame = (TlmFrame) beacon.mFrames[2];
                if (tlmFrame != null) {
                    uid.parseTlmFrame(tlmFrame);
                }
                uids.add(uid);
            }
        }
        return uids.toArray(new Uid[uids.size()]);
    }

    // Returns an array of all nearby Eddystone Objects
    public static Generic[] nearby() {
        ArrayList<Generic> generics = new ArrayList<>();
        Beacon[] beacons = getBeacons();

        for (Beacon beacon : beacons) {
            Uri url = null;
            String namespace = null;
            String instance = null;

            UidFrame uidFrame = (UidFrame) beacon.mFrames[0];
            if (uidFrame != null) {
                namespace = uidFrame.mNamespace;
                instance = uidFrame.mInstance;
            }

            UrlFrame urlFrame = (UrlFrame) beacon.mFrames[1];
            if (urlFrame != null) {
                url = urlFrame.mUrl;
            }
            Generic generic = Generic.makeGeneric(url, namespace, instance, beacon.getSignalStrength(), beacon.getIdentifier());
            TlmFrame tlmFrame = (TlmFrame) beacon.mFrames[2];
            if (tlmFrame != null) {
                generic.parseTlmFrame(tlmFrame);
            }
            generics.add(generic);
        }

        return generics.toArray(new Generic[generics.size()]);
    }

    @Override
    public void beaconDidChange() {
        notifyChange();
    }

    private void notifyChange() {
        if (mScannerDelegate != null) {
            mScannerDelegate.eddytoneNearbyDidChange();
        }
    }

    private static Beacon[] getBeacons() {
        ArrayList<Beacon> orderedBeacons = new ArrayList<>();
        orderedBeacons.addAll(getInstance().mDiscoveredBeacons.values());
        Collections.sort(orderedBeacons);

        return orderedBeacons.toArray(new Beacon[orderedBeacons.size()]);
    }

    private ScanSettings getScanSettings() {
        ScanSettings.Builder builder = new ScanSettings.Builder();
        builder.setScanMode(2);
        builder.setReportDelay(0);
        return builder.build();
    }

    private ScanFilter getScanFilter() {
        ScanFilter.Builder builder = new ScanFilter.Builder();
        builder.setServiceUuid(Beacon.EDDYSTONE_SPEC);
        return builder.build();
    }
}

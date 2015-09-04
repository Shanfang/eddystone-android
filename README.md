# Eddystone-Android

## Installation

We use [jitPack](https://jitpack.io) to deploy our library.

In your gradle.build (Project) file:

```
allprojects {
    repositories {
        jcenter()
        maven { url "https://jitpack.io" } // Add this line
    }
}
```

In your gradle.build (app) file:

```
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.github.BlueBiteLLC:Eddystone-Android:-SNAPSHOT' // Add this line
}
```

## Scan for Eddystone Beacons

Implement the callback interface for updates on nearby beacons:

`public class MyActivity implements ScannerDelegate`

Start scanning for beacons in your Activity:

```
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Enable logging
    Global.logging = true;

    // Start scanning
    Scanner.start(this);
}
```

Retrieve an array of nearby Eddystone beacons in the overridden interface method:

```
@Override
public void eddytoneNearbyDidChange() {
    // Nearby Beacons have been updated

    // ONLY returns nearby Eddystone-URLs
    Url[] eddystoneUrls = Scanner.nearbyUrls();

    // ONLY returns nearby Eddystone-UIDs
    Uid[] eddystoneUids = Scanner.nearbyUids();

    // Returns both Eddystone-URLs and Eddystone-UIDs
    Generic[] genericBeacons = Scanner.nearby();
}
```

## Classes

### Url (Eddystone-URL)

Inherits from EddystoneObject

```
getUrl() // Returns Uri of encoded URL
```

### Uid (Eddystone-UID)

Inherits from EddystoneObject

```
getUid() // Returns String UID
getNamespace() // Returns String Namespace
getInstance() // Returns String Instance
```

### Generic (Both Eddystone-URL & Eddystone-UID)

Inherits from EddystoneObject

Generic methods can return `null`. Uid & Url methods are guaranteed to return non-null.

```
getUid() // Returns String UID
getUrl() // Returns Uri of encoded URL
getNamespace() // Returns String Namespace
getInstance() // Returns String Instance
```

### EddystoneObject

Contains Eddystone-TLM data.

```
getIdentifier() // Returns String indentifier
getSignalStrength() // Returns enum SignalStrength
getBattery() // Returns Double percentage remaining
getTemperature() // Returns Double temperature in Celsius
getAdvertisementCount() // Returns Integer packets sent
getOnTime() // Returns Integer uptime
getReadableOnTime() // Returns String of formatted up time
```

## Example App

In Android Studio:

1. File -> New -> Project from Version Control -> GitHub
2. Git Repository Url: `https://github.com/BlueBiteLLC/Eddystone-Android.git`
3. Run the app!

Note: Make sure to have Bluetooth Enabled when running the app.

## Author

Andres Santiago, andres@bluebite.com

## License

Eddystone is available under the MIT license. See the LICENSE file for more info.

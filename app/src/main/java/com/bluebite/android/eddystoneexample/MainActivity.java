package com.bluebite.android.eddystoneexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.bluebite.android.eddystone.Global;
import com.bluebite.android.eddystone.Scanner;
import com.bluebite.android.eddystone.ScannerDelegate;
import com.bluebite.android.eddystone.Url;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ScannerDelegate {
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<Url> mUrls = new ArrayList<>();
    private BeaconAdapter mBeaconAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ListView beaconListView = (ListView) findViewById(R.id.beaconListView);
        mBeaconAdapter = new BeaconAdapter(this, R.layout.beacon_list_item, mUrls);
        beaconListView.setAdapter(mBeaconAdapter);
        Global.logging = true;
        Scanner.start(this);
    }

    @Override
    public void eddytoneNearbyDidChange() {
        mUrls = Arrays.asList(Scanner.nearbyUrls());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBeaconAdapter.clear();
                mBeaconAdapter.addAll(mUrls);
            }
        });
        Log.i(TAG, Arrays.toString(Scanner.nearbyUrls()));
    }
}

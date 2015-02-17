package com.murraycole.heartrateapi;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by User on 2/16/15.
 */

/* This activity receives data from the handheld */
public class WearableDataListener extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {
    private static final String TAG = WearableDataListener.class.getSimpleName();

    private static final String SEND_KEY = "com.example.data";

    private GoogleApiClient mGoogleAPIClient;
    private TextView tv;
    private Button sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wearable_data);

        mGoogleAPIClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        tv = (TextView) findViewById(R.id.data_received_tv);
        sendBtn = (Button) findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SyncTask().execute();
            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        Wearable.DataApi.removeListener(mGoogleAPIClient, this);
        mGoogleAPIClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleAPIClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected");
        //add listener
        Wearable.DataApi.addListener(mGoogleAPIClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection Failed");

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "Received data");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                //Data item changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo("/data") == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    doAction(dataMap.getInt(SEND_KEY));
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }

    private void doAction(final int i){
        Log.d(TAG, "Received: " + i);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText("D:" + i);
            }
        });

        //Do stuff with info received from dataLayer
    }

    private class SyncTask extends AsyncTask <Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            WearableSendSync.sendSyncToDevice(mGoogleAPIClient);
            return null;
        }
    }


}

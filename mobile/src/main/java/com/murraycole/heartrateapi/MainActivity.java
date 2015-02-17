package com.murraycole.heartrateapi;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;

/* This class connects to Wearable API,
   then sends data to wearable
 */
public class MainActivity extends Activity {
    static GoogleApiClient mWearableClient;
    Button sendBtn;
    WearableDataLayer wearableDataLayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendBtn = (Button) findViewById(R.id.send_btn);
        wearableDataLayer = new WearableDataLayer();

        mWearableClient = WearableDataLayer.buildDataApiClient(this);
        mWearableClient.connect();

        Wearable.MessageApi.addListener(mWearableClient, wearableDataLayer);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WearableDataLayer.sendData(mWearableClient);
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        Wearable.MessageApi.removeListener(mWearableClient, wearableDataLayer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWearableClient.connect();
        Wearable.MessageApi.addListener(mWearableClient,wearableDataLayer);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static void syncRequest(){
        WearableDataLayer.sendData(mWearableClient);
    }

}

package app.akexorcist.bluetoothspp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

/**
 * Created by internship on 6/24/15.
 */
public class MyGraphActivity extends Activity {
    LinearLayout linearChart;

    BluetoothSPP bt;
    int chart_height;
    int count;

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygraph);
        linearChart = (LinearLayout)findViewById(R.id.linearChart);

        bt = new BluetoothSPP(this);
        count = 0;

        if(!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            finish();
        }

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                chart_height = Integer.valueOf(message);

                drawChart(chart_height);
                count++;
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_connection, menu);
            }

            public void onDeviceConnectionFailed() {

            }

            public void onDeviceConnected(String name, String address) {
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_disconnection, menu);
            }
        });


    }

    public void drawChart(int height) {
        View view = new View(this);
        view.setBackgroundColor(Color.parseColor("#80CCE6"));

        view.setLayoutParams(new LinearLayout.LayoutParams(20, height));
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams();

        params.setMargins(5, 0, 0, 0); // substitute parameters for left, top, right, bottom

        view.setLayoutParams(params);
        linearChart.addView(view);

        if (count == 25) {
            linearChart.removeAllViews();
            count = 0;
        }
    }

    public void drawChart(int count, int height) {
        System.out.println(count);

        for (int k = 1; k <= count; k++) {
            View view = new View(this);
            view.setBackgroundColor(Color.parseColor("#80CCE6"));

            view.setLayoutParams(new LinearLayout.LayoutParams(20, 400));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)view.getLayoutParams();

            params.setMargins(5, 0, 0, 0); // substitute parameters for left,top, right, bottom

            view.setLayoutParams(params);
            linearChart.addView(view);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_connection, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_android_connect) {
            bt.setDeviceTarget(BluetoothState.DEVICE_ANDROID);
			/*
			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
    			bt.disconnect();*/
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        } else if(id == R.id.menu_device_connect) {
            bt.setDeviceTarget(BluetoothState.DEVICE_OTHER);
			/*
			if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
    			bt.disconnect();*/
            Intent intent = new Intent(getApplicationContext(), DeviceList.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        } else if(id == R.id.menu_disconnect) {
            if(bt.getServiceState() == BluetoothState.STATE_CONNECTED)
                bt.disconnect();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if(!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            }
        }
    }



    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if(resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_ANDROID);
            } else {
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}

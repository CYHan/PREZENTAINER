
package com.puregodic.android.prezentainer;

import java.util.ArrayList;
import java.util.Set;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingBluetoothActivity extends AppCompatActivity {
    
    private BluetoothAdapter mBluetoothAdapter ;
    private BroadcastReceiver mBroadcastReceiver;
    private static final  int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "==SettingBTActivity==";   
    //Button btnSearch;
    private ListView listView;
    private TextView newlyFoundtextView;
    private Button btnSearch;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_bluetooth);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        listView = (ListView)findViewById(R.id.listView);
        newlyFoundtextView = (TextView)findViewById(R.id.newlyFoundtextView);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        
        
        btnSearch.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                mBluetoothAdapter.startDiscovery();
                btnSearch.setEnabled(false);
            }
        });
        
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
       
        listPairedDevices();
     // IntentFilter �̺�Ʈ�� ����͸� �� �� �ִ�.
        IntentFilter deviceFoundFilter = new IntentFilter(
                BluetoothDevice.ACTION_FOUND); // ���ο� ��⸦ ã���� ��
        IntentFilter deviveDiscoveryFinishedFilter = new IntentFilter(
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED); // Ž���� ��������
        IntentFilter stateChanged = new IntentFilter(
                BluetoothAdapter.ACTION_STATE_CHANGED); // ���°� �ٲ���� ��
        
        // ���ù��� intentfilter�� ����Ѵ�. �̴� �ش� �̺�Ʈ�� BroadcastReceiver�� �뺸�ϵ��� �䱸!
        registerReceiver(mBroadcastReceiver, deviceFoundFilter);
        registerReceiver(mBroadcastReceiver, deviveDiscoveryFinishedFilter);
        registerReceiver(mBroadcastReceiver, stateChanged);
        
        
        mBroadcastReceiver = new BroadcastReceiver() {
            
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(), "BROADCASTRECEIVER", Toast.LENGTH_SHORT).show();
                String action = intent.getAction();
                if(BluetoothDevice.ACTION_FOUND.equals(action)){
                    Toast.makeText(getApplicationContext(), "���ο��⸦ ã�ҽ��ϴ�.", Toast.LENGTH_SHORT).show();
                 // ���ο� ��⸦ ã���� ��...
                    BluetoothDevice devicesFound = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    listFoundDevices(devicesFound);
                    
                }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                    
                    btnSearch.setEnabled(true);
                }
                
            }
        };
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting_bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    // List Devices paired
    private void listPairedDevices() {
        // pair�� ������ Setȭ ��Ŵ
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                .getBondedDevices();
        ArrayList<String> pairedDeviceArrayList = new ArrayList<String>();

        // pair�� device�� list
        for (BluetoothDevice pairedDevice : pairedDevices) {
            pairedDeviceArrayList.add(pairedDevice.getName());
        }
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this, R.layout.list_items_setting_bt, pairedDeviceArrayList);
        listView.setAdapter(mAdapter);
    }

    // List Devices found
    private void listFoundDevices(BluetoothDevice device) {
        newlyFoundtextView.append(Html.fromHtml("<br><h1>" + device.getName() + "</h1>"));
        newlyFoundtextView.append(Html.fromHtml(" (" + device.getAddress() + ")\n"));
    }
}


package com.puregodic.android.prezentainer;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.puregodic.android.prezentainer.connecthelper.ConnecToPcHelper;
import com.puregodic.android.prezentainer.connecthelper.ConnectionActionPc;
import com.puregodic.android.prezentainer.service.AccessoryService;
import com.puregodic.android.prezentainer.service.ConnectionActionGear;

public class SettingActivity extends AppCompatActivity {

    private AccessoryService mAccessoryService = null;

    private Boolean isBound = false;
    private Boolean isGearConnect = false;
    private Boolean isPcConnect = false;
    private BluetoothAdapter mBluetoothAdapter;

    private ConnecToPcHelper mConnecToPcHelper;

    // ���� - Ÿ�̸� ������ �����ϴ� �迭
    private ArrayList<String> timeInterval;
    
    Button connectToGearBtn,connectToPcBtn,startBtn;
    CheckBox timerCheckBox;
    RadioGroup timerRadioGroup;
    
    private static final String TAG_BLUETOOTH = "==Bluetooth==";
    private static final  int REQUEST_ENABLE_BT = 1;
    //private static final  int PDIALOG_TIMEOUT_ID = 444;

    private BroadcastReceiver mBroadcastReceiver;
    private ProgressDialog pDialog;
    
   // private final IncomingHandler mHandler = new IncomingHandler(this);
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // Bind Service
        doBindService();

        startBtn = (Button)findViewById(R.id.startBtn);
        timerCheckBox = (CheckBox)findViewById(R.id.timerCheckBox);
        timerRadioGroup = (RadioGroup)findViewById(R.id.timerRadioGroup);
        connectToGearBtn = (Button)findViewById(R.id.connectToGearBtn);
        connectToPcBtn = (Button)findViewById(R.id.connectToPcBtn);
        
        startBtn.setEnabled(false);
        
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("����� ������ �����ϼ���...");
        pDialog.setCancelable(true);
        //mHandler.sendEmptyMessageDelayed(PDIALOG_TIMEOUT_ID, 5000);
        

        // ���� - Ÿ�̸Ӽ��� �����ڽ� ���̰� �ϱ�
        timerCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    timerRadioGroup.setVisibility(timerRadioGroup.VISIBLE);
                } else {
                    timerRadioGroup.setVisibility(timerRadioGroup.INVISIBLE);
                    timerRadioGroup.clearCheck();
                    timeInterval = new ArrayList<String>();
                    timeInterval.add(0, "0");
                }
            }
        });

        // ���� - �����׷쿡�� ���õ� �� ArrayList(timeInterval)�� �ֱ�
        timerRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadio5) {
                    timeInterval = new ArrayList<String>();
                    timeInterval.add("5");
                    Toast.makeText(SettingActivity.this,
                            String.valueOf("�ð����� : " + timeInterval.get(0)), Toast.LENGTH_LONG)
                            .show();
                }
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadio10) {
                    timeInterval = new ArrayList<String>();
                    timeInterval.add("10");
                    Toast.makeText(SettingActivity.this,
                            String.valueOf("�ð����� : " + timeInterval.get(0)), Toast.LENGTH_LONG)
                            .show();
                }
                if (timerRadioGroup.getCheckedRadioButtonId() == R.id.timerRadio15) {
                    timeInterval = new ArrayList<String>();
                    timeInterval.add("15");
                    Toast.makeText(SettingActivity.this,
                            String.valueOf("�ð����� : " + timeInterval.get(0)), Toast.LENGTH_LONG)
                            .show();
                }

            }
        });

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        isEnabledAdapter();
        super.onPostCreate(savedInstanceState);
    }
    
    @Override
    protected void onDestroy() {
        if(mAccessoryService != null)
        mAccessoryService.closeConnection();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        
        if(requestCode == REQUEST_ENABLE_BT){
            
            //OK ��ư�� ��������
            if(resultCode == RESULT_OK){
                Toast.makeText(getApplicationContext(), "��������� �׽��ϴ�.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), "��������� ���ӷ���.", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
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

    // Click Event Handler Call Back
    public void myOnClick(View v) {
        switch (v.getId()) {
        // �����ϱ�
            case R.id.connectToGearBtn: {
                startConnection();
                break;
            }
            case R.id.connectToPcBtn: {

                /*mConnecToPcHelper = new ConnecToPcHelper();
                mConnecToPcHelper.registerConnectionAction(getConnectionActionPc());
                mConnecToPcHelper.transferToPc();*/
                Intent i = new Intent(SettingActivity.this,SettingBluetoothActivity.class);
                startActivity(i);
                break;
            }
            case R.id.startBtn: {
                    if (timeInterval != null) {
                        sendDataToService(timeInterval.get(0));
                    } else {
                        sendDataToService("0");
                    }
                    Intent intent = new Intent(SettingActivity.this,StartActivity.class);
                    startActivity(intent);
                break;
            }
        }

    }

    private void doBindService() {

        Intent intent = new Intent(SettingActivity.this, AccessoryService.class); // Action
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);

    }

    private void startConnection() {
        if (isBound == true && mAccessoryService != null) {
            mAccessoryService.findPeers();
        }
    }

    private void sendDataToService(String mData) {
        if (isBound == true && mAccessoryService != null) {
            mAccessoryService.sendDataToGear(mData);
        } else {
            Toast.makeText(getApplicationContext(), "���� ������ Ȯ���ϼ���", Toast.LENGTH_SHORT).show();
        }
    }

    private void isEnabledAdapter() {

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            Toast.makeText(getApplicationContext(), "��������� �̹� �����ֽ��ϴ�.", Toast.LENGTH_SHORT).show();
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
    
    
 // AccessoryService���� �������̽� �޼ҵ� �����ϱ�
    private ConnectionActionGear getConnectionActionGear(){
        return new ConnectionActionGear() {
            
           

            @Override
            public void onConnectionActionRequest() {
                
                
                
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        showpDialog();
                        connectToGearBtn.setText("������ ��û�Ͽ����ϴ�.");
                        
                    }
                });
            }
            
            @Override
            public void onConnectionActionNoResponse() {
                
                
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        hidepDialog();
                        connectToGearBtn.setText("������ �ٽ� Ȯ���ϼ���.");
                        
                    }
                });
            }

            @Override
            public void onConnectionActionComplete() {
                
                isGearConnect = true;
                
                runOnUiThread( new Runnable() {
                    public void run() {
                        hidepDialog();
                        connectToGearBtn.setText("���� ����Ǿ����ϴ�.");
                        setEnabledStartBtn();
                    }
                });
                
            }
        };
        
    }
    
    // ConnecToPcHelper���� �������̽� �޼ҵ� �����ϱ�
    private ConnectionActionPc getConnectionActionPc(){
        return new ConnectionActionPc() {
            
            @Override
            public void onConnectionActionRequest() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onConnectionActionComplete() {
                isPcConnect = true;
                runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {

                        connectToPcBtn.setText("PC�� ����Ǿ����ϴ�.");
                        setEnabledStartBtn();
                    }
                });
                
            }
        };
    }
    
    private void showpDialog(){
        if(!pDialog.isShowing())
            pDialog.show();
    }
    
    private void hidepDialog(){
        if(pDialog != null)
        pDialog.dismiss();
    }
    
    private void setEnabledStartBtn(){
        if(isGearConnect && isPcConnect){
            startBtn.setEnabled(true);
        }
    }
    

    // ServiceConnection Interface
    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAccessoryService = ((AccessoryService.MyBinder)service).getService();
            mAccessoryService.registerConnectionAction(getConnectionActionGear());
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mAccessoryService = null;
            isBound = false;
        }
    };
    
/*    //sub class ���� �޸� ������ ������
static class IncomingHandler extends Handler{
    private final WeakReference<SettingActivity> mActivity;
    IncomingHandler(SettingActivity activity) {
        mActivity = new WeakReference<SettingActivity>(activity);
    }
    
    @Override
    public void handleMessage(Message msg) {
        if(msg.what == PDIALOG_TIMEOUT_ID){
            SettingActivity activity = mActivity.get();
            activity.pDialog.dismiss();
        }
        super.handleMessage(msg);
        }
    }*/
}

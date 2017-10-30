package com.test.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    private ToggleButton switch_bea;
    private static EditText etUUID;
    private static EditText etName;
    private static EditText etMajor;
    private static EditText etMinor;
    private static EditText etMP;
    private static Button start;
    private static Button stop;
    private static Button search;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private static Context mContext;

    public static final String TAG = "bleperipheral";
    private static boolean D = true;


    public static final String HEART_RATE_SERVICE = "00001895-0000-1000-8000-00805f9b34fb";
    static byte[] data_uuid  = new byte[16];
    static byte[] data_major = new byte[2];
    static byte[] data_minor = new byte[2];
    static byte[] data_power = new byte[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        mContext=this;
        initBle();
        initView();
        initData();
        initEvent();

    }

    protected void onResume() {
        super.onResume();
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }


    }


    private void initBle() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_LONG).show();
            finish();
        }

        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.bluetooth_not_supported, Toast.LENGTH_LONG).show();
            finish();
        }

        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
            Toast.makeText(this, "the device not support peripheral", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "the device not support peripheral");
            finish();
        }

    }

    private void initEvent() {
        switch_bea.setOnCheckedChangeListener(onCheckedChangeListener);
        start.setOnClickListener(onStartClickListener);
        stop.setOnClickListener(onStopClickListener);
        search.setOnClickListener(onSearchListener);
    }

    private void initData() {
        etMajor.setText("98");
        etMinor.setText("753");
        etMP.setText("59");
        etUUID.setText("c92a bdbe df54 4501 a3aa d7bd f1fd 2e1d");
        switch_bea.setBackgroundResource(R.mipmap.tooglebutton_off);
        switch_bea.setChecked(false);
    }

    private void initView() {
        switch_bea = (ToggleButton) findViewById(R.id.switch_bea);
        etUUID = (EditText) findViewById(R.id.etUUID);
        etName = (EditText) findViewById(R.id.etName);
        etMajor = (EditText) findViewById(R.id.etMajor);
        etMinor = (EditText) findViewById(R.id.etMinor);
        etMP = (EditText) findViewById(R.id.etMP);

        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);
        search = (Button) findViewById(R.id.search);
    }

    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            if (settingsInEffect != null) {
                Log.d(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel() + " mode=" + settingsInEffect.getMode()
                        + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.e(TAG, "onStartSuccess, settingInEffect is null");
            }
            Log.e(TAG, "onStartSuccess settingsInEffect" + settingsInEffect);

        }

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            if (D) Log.e(TAG, "onStartFailure errorCode" + errorCode);

            if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                if (D) {
                    Toast.makeText(mContext, R.string.advertise_failed_data_too_large, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
                }
            } else if (errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS) {
                if (D) {
                    Toast.makeText(mContext, R.string.advertise_failed_too_many_advertises, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to start advertising because no advertising instance is available.");
                }
            } else if (errorCode == ADVERTISE_FAILED_ALREADY_STARTED) {
                if (D) {
                    Toast.makeText(mContext, R.string.advertise_failed_already_started, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Failed to start advertising as the advertising is already started");
                }
            } else if (errorCode == ADVERTISE_FAILED_INTERNAL_ERROR) {
                if (D) {
                    Toast.makeText(mContext, R.string.advertise_failed_internal_error, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Operation failed due to an internal error");
                }
            } else if (errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED) {
                if (D) {
                    Toast.makeText(mContext, R.string.advertise_failed_feature_unsupported, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "This feature is not supported on this platform");
                }
            }
        }
    };

    /**
     * create AdvertiseSettings
     */
    public static AdvertiseSettings createAdvSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder mSettingsbuilder = new AdvertiseSettings.Builder();
        mSettingsbuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        mSettingsbuilder.setConnectable(connectable);
        mSettingsbuilder.setTimeout(timeoutMillis);
        mSettingsbuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        AdvertiseSettings mAdvertiseSettings = mSettingsbuilder.build();
        if (mAdvertiseSettings == null) {
            if (D) {
                Toast.makeText(mContext, "mAdvertiseSettings == null", Toast.LENGTH_LONG).show();
                Log.e(TAG, "mAdvertiseSettings == null");
            }
        }
        return mAdvertiseSettings;
    }

    private static byte[] getManuData() {

        byte[] data = new byte[24];
        data[0] = 0x02;
        data[1] = 0x15;
        String uuid = etUUID.getText().toString().trim().replace(" ", "");
        data_uuid = getHexData(uuid, uuid.length());

        String major = etMajor.getText().toString().trim().replace(" ", "");
        data_major = getIntData(major, major.length());

        String minor = etMinor.getText().toString().trim().replace(" ", "");
        data_minor = getIntData(minor, minor.length());

        String mp = etMP.getText().toString().trim().replace(" ", "");
        data_power = getIntData(mp, mp.length());

        System.arraycopy(data_uuid, 0, data, 2, data_uuid.length);
        System.arraycopy(data_major, 0, data, 18, data_major.length);
        System.arraycopy(data_minor, 0, data, 20, data_minor.length);
        System.arraycopy(data_power, 0, data, 22, data_power.length);

        return data;
    }

    private static byte[] getHexData(String st, int length) {
        byte[] data = new byte[length / 2 ];
        for (int i = 0; i < length / 2 ; i++) {
            String str = st.substring(i * 2, i * 2 + 2);
            //Log.i("Data", "str:" + str);
            long num = Long.parseLong(str, 16);

            data[i] = (byte) num;

        }
        return data;
    }

    private static byte[] getIntData(String st, int length) {
        byte[] data = new byte[2];
        long num = Long.parseLong(st);
       /* Log.i("Data", "num:" + num);*/

        if(num>255)
        {
            data[0] = (byte)(num/256);
            data[1] = (byte)(num%256);
        }else
        {
            data[0] = 0x00;
            data[1] = (byte)num;
        }


        return data;
    }


    /**
     * 字符串转换成十六进制字符串
     *
     * @param str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    public static AdvertiseData createAdvertiseData() {
         /*
        uuid    16byte   c91a bdbe df54 4501 a3aa d7bd f1fd 2e1d
        major   2 byte   0062
        minor   2 byte   02f1
        power   2 byte   c300
        */
        AdvertiseData.Builder mDataBuilder = new AdvertiseData.Builder();
        //mDataBuilder.addServiceUuid(ParcelUuid.fromString(HEART_RATE_SERVICE));
        mDataBuilder.addManufacturerData(0x4c, getManuData());

        AdvertiseData mAdvertiseData = mDataBuilder.build();
        if (mAdvertiseData == null) {
            if (D) {
                Toast.makeText(mContext, "mAdvertiseSettings == null", Toast.LENGTH_LONG).show();
                Log.e(TAG, "mAdvertiseSettings == null");
            }
        }

        return mAdvertiseData;
    }


    private void stopAdvertise() {
        if (mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
            /*mBluetoothLeAdvertiser = null;*/
        }
    }

    View.OnClickListener onStartClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mBluetoothLeAdvertiser.startAdvertising(createAdvSettings(true, 0), createAdvertiseData(), mAdvertiseCallback);
            if (D) {
                Toast.makeText(mContext, "Start Advertising", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Start Advertising");
            }
        }
    };

    View.OnClickListener onStopClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stopAdvertise();
            if (D) {
                Toast.makeText(mContext, "Stop Advertising", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Stop Advertising");
            }
        }
    };

    View.OnClickListener onSearchListener = new View.OnClickListener(){

        @Override
        public void onClick(View view) {
            startActivity(new Intent(MainActivity.this,SearchAcitivty.class));
        }
    };
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                switch_bea.setBackgroundResource(R.mipmap.tooglebutton_on);
                mBluetoothLeAdvertiser.startAdvertising(createAdvSettings(true, 0), createAdvertiseData(), mAdvertiseCallback);
                if (D) {
                    Toast.makeText(mContext, "Start Advertising", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Start Advertising");
                }
            } else {
                switch_bea.setBackgroundResource(R.mipmap.tooglebutton_off);
                stopAdvertise();
                if (D) {
                    Toast.makeText(mContext, "Stop Advertising", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Stop Advertising");
                }
            }
        }
    };


}

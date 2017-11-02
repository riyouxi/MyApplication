package com.test.ble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class SearchAcitivty extends AppCompatActivity implements BluetoothAdapter.LeScanCallback {

    public final static int ADDMACRESULT = 0x0010;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler = new Handler();
    private boolean mScanning = false;
    private Map<String,String> list_strs = new HashMap<>();
    private ListView mListView;
    private MacsListAdapter mMacAdapter;
    private EditText mOffsetEditText;
    private TextView mSerach;
    private PermissionUtil mPermissionUtil;

    private int offset =0;//从0开始取
    //是否需要检测权限
    public static boolean isRequireCheck = true;

    // 所需的全部权限
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPermissionUtil = new PermissionUtil(this, this);
        mListView = (ListView) findViewById(R.id.list_macs);
        mOffsetEditText = (EditText) findViewById(R.id.offset);
        mSerach = (TextView) findViewById(R.id.search);
        mSerach.setOnClickListener(searchListener);
        mMacAdapter = new MacsListAdapter(SearchAcitivty.this);
        mListView.setAdapter(mMacAdapter);
        mBluetoothAdapter =  BluetoothAdapter.getDefaultAdapter();

        if (isRequireCheck) {
            if (mPermissionUtil.lacksPermissions(PERMISSIONS)) {
                mPermissionUtil.requestPermissions(PERMISSIONS); // 请求权限
                isRequireCheck = true;
            } else {
                isRequireCheck = false;
            }
        }
        String s = "13FF00007D7E98590428a8b4";
        byte[] temp =hexStringToBytes(s);
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<temp.length;i++){
            sb.append(temp[i]);
        }
        Log.e("TAG",sb.length()+sb.toString());
        printHexString(temp);
    }

    public static void printHexString( byte[] b) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        Log.e("TAG",sb.toString());

    }


    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    View.OnClickListener searchListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            offset = Integer.valueOf(mOffsetEditText.getText().toString());
            mMacAdapter.clear();
            scanBle(true);
        }
    };

    private void scanBle(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(SearchAcitivty.this);
                }
            }, 60000);
            mScanning = true;
            mBluetoothAdapter.startLeScan(this);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(this);
        }
    }



    @Override
    public void onLeScan(BluetoothDevice bluetoothDevice, int rssi, byte[] bytes) {
        list_strs.put(bluetoothDevice.getAddress(),bluetoothDevice.getAddress());
        Log.i("TAG",bluetoothDevice.getAddress()+bluetoothDevice.getName());
//            byte[] temp = {0x7e,0x}

        addMac(rssi,bluetoothDevice.getName(),bluetoothDevice.getAddress(),bytes);
        spilte(bytes);

    }

    private String spilte(byte[]param){
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i<param.length;i++){
            sb.append(param[i]+",");
        }
        return sb.toString();
    }

    /**
     * 数组转成十六进制字符串
     * @param byte[]
     * @return HexString
     */
    public static String toHexString1(byte[] b){
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < b.length; ++i){
            buffer.append(toHexString1(b[i]));
        }
        return buffer.toString();
    }
    public static String toHexString1(byte b){
        String s = Integer.toHexString(b & 0xFF);
        if (s.length() == 1){
            return "0" + s;
        }else{
            return s;
        }
    }

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
    public void addMac(int rssi,String macName,String address,byte[]bytes){

        //byte[] temp = {13,ff,6,0,1,9,32,0,-8,17,9,-41,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        StringBuilder sb = new StringBuilder();
        byte[] head = new byte[2];
        System.arraycopy(bytes,offset,head,0,2);
        sb.append("\n包头:"+toHexString1(head));

        byte[] mac = new byte[6];
        System.arraycopy(bytes,offset+2,mac,0,6);
        sb.append("\nmac:"+toHexString1(mac));

        byte[] imei = new byte[8];
        System.arraycopy(bytes,offset+8,imei,0,8);
        sb.append("\nimei:"+toHexString1(imei));

        MacBean macBean = new MacBean();
        macBean.setMacName(macName);
        macBean.setData(spilte(bytes));
        macBean.setMacAddress(macName);
        macBean.setMacAddress(address);
        macBean.setNewContent(sb.toString());
        macBean.setRssi(rssi);
        mMacAdapter.addData(macBean);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.stopLeScan(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("TAG",keyCode+"");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == mPermissionUtil.PERMISSION_REQUEST_CODE && mPermissionUtil.hasAllPermissionsGranted(grantResults)) {
            isRequireCheck = false;
        } else {
            isRequireCheck = true;
            mPermissionUtil.showMissingPermissionDialog();
        }
    }
}

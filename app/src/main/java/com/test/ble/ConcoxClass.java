package com.test.ble;

import android.bluetooth.BluetoothDevice;
import android.util.Log;


public class ConcoxClass {

    static public  class Concox{
        public String name;
        public String model;
        public String proximityImei;
        public String bluetoothAddress;
        public int    alarm;
        public int    txPower;
        public int    rssi;
    }
    public static Concox fromScanData(BluetoothDevice device, int rssi, byte[] scanData) {

        int startByte = 2;
        boolean patternFound = false;

        while (startByte <= 4) {
            if (((int)scanData[startByte+2] & 0xff) == 0x7D &&
                    ((int)scanData[startByte+3] & 0xff) == 0x7E) {
           //         ((int)scanData[startByte+4] & 0xff) == 0x7D &&
           //         ((int)scanData[startByte+5] & 0xff) == 0x7E) {//&&
           //        ((int)scanData[startByte+6] & 0xff) == 0x54) {
                // yes!  This is an iBeacon
                patternFound = true;
                break;
            }
            startByte++;
        }


        if (patternFound == false) {
            // This is not an iBeacon
            return null;
        }

        /*Log.i("OTA", "fromScanData");*/
        Concox concox = new Concox();

        concox.rssi    = rssi;

        
        //  AirLocate:
        //  02 01 04 15 ff
        //  43 4f 58 42 54              FLAG     COXBT
        //  47 54 20 37 32 30           MODEL    GT 720
        //  31 32 33 34 35 36 37 38     IMEI     12345678
        //  00                          ALARM
        //  00                          TXPOWER  8

        //  AirLocate:
        //  13 FF 00 00 7D 7E
        //  AA BB CC DD EE FF           MAC
        //  47 54 20 37 32 30           IMEI
        //  00                          ALARM
        //  00                          TXPOWER  8

        startByte=6;
        byte[] nameBytes = new byte[6];
        System.arraycopy(scanData, startByte, nameBytes, 0, 6);
        String hexMac = bytesToHexString(nameBytes);
        StringBuilder smac = new StringBuilder();

        smac.append(hexMac.substring(0,2));
        smac.append("-");
        smac.append(hexMac.substring(2,4));
        smac.append("-");
        smac.append(hexMac.substring(5,6));
        smac.append("-");
        smac.append(hexMac.substring(6,8));
        smac.append("-");
        smac.append(hexMac.substring(8,10));
        smac.append("-");
        smac.append(hexMac.substring(10,12));


        concox.model=smac.toString();


     //   byte[] proximityMacBytes = new byte[6];
      //  System.arraycopy(scanData, startByte+10, proximityMacBytes, 0, 6);

      //  String hexMac = bytesToHexString(proximityMacBytes);

      //  StringBuilder smac = new StringBuilder();

     //   smac.append(hexMac.substring(0,2));
     //   smac.append("-");
     //   smac.append(hexMac.substring(2,4));
     //   smac.append("-");
     //   smac.append(hexMac.substring(6,8));


    //    concox.model=smac.toString();

        startByte=12;
        byte[] proximityImeiBytes = new byte[8];
        System.arraycopy(scanData, startByte, proximityImeiBytes, 0, 8);
        String hexString = bytesToHexString(proximityImeiBytes);
        StringBuilder sb = new StringBuilder();
        sb.append(hexString.substring(0,4));
        sb.append("-");
        sb.append(hexString.substring(4,8));
        sb.append("-");
        sb.append(hexString.substring(8,12));
        sb.append("-");
        sb.append(hexString.substring(12,16));
        concox.proximityImei = sb.toString();

        concox.alarm   = (int)scanData[startByte+21];
        concox.txPower = (int)scanData[startByte+22];
        if (device != null) {
            concox.bluetoothAddress = device.getAddress();
          /*  concox.name = device.getName();*/
        }

        return concox;
    }

    private static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}

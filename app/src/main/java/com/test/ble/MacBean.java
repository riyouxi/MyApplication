package com.test.ble;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/8/7.
 */

public class MacBean implements Serializable {
    private String macName;
    private String macAddress;
    private String Data;
    private String newContent;
    private int rssi;

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }

    public String getMacName() {
        return macName;
    }

    public void setMacName(String macName) {
        this.macName = macName;
    }


    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}

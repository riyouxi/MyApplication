package com.test.ble;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/7.
 */

public class MacsListAdapter extends BaseAdapter {
    private List<MacBean> mMacs;
    private Context mContext;

    public MacsListAdapter(Context context){
        mContext = context;
    }

    @Override
    public int getCount() {
        return mMacs == null ? 0 : mMacs.size();
    }

    @Override
    public Object getItem(int i) {
        return mMacs == null ? null : mMacs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final MacViewHolder macViewHolder;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.item_mac, null);
            macViewHolder = new MacViewHolder();
            macViewHolder.txt_mac_name = (TextView) view.findViewById(R.id.txt_mac_name);
            macViewHolder.txt_mac = (TextView) view.findViewById(R.id.txt_mac);
            macViewHolder.txt_oldByte = view.findViewById(R.id.old_byte);
            macViewHolder.txt_newByte = view.findViewById(R.id.new_byte);
            macViewHolder.txt_rssi = view.findViewById(R.id.rssi);
            view.setTag(macViewHolder);
        } else {
            macViewHolder = (MacViewHolder) view.getTag();
        }
        final MacBean macBean = mMacs.get(i);
        macViewHolder.txt_mac_name.setText("蓝牙名称："+macBean.getMacName());
        macViewHolder.txt_mac.setText("蓝牙地址："+macBean.getMacAddress());
        macViewHolder.txt_oldByte.setText("收到的蓝牙广播byte[]数据："+macBean.getData());
       // macViewHolder.txt_newByte.setText("解析出来的数据："+macBean.getNewContent());
        macViewHolder.txt_rssi.setText("rssi:"+macBean.getRssi());
        return view;
    }

    public void setData(List<MacBean> data){
        if (data != null){
            this.mMacs = data;
        }
    }

    public void addData(MacBean macBean){
        if (mMacs == null){
            mMacs = new ArrayList<>();
        }
        mMacs.add(macBean);
        notifyDataSetChanged();
    }

    public void clear(){
        if(mMacs!=null){
            mMacs.clear();
            notifyDataSetChanged();
        }

    }

    public class MacViewHolder{
        private TextView txt_mac_name;
        private TextView txt_mac;
        private TextView txt_oldByte;
        private TextView txt_newByte;
        private TextView txt_rssi;
    }

    public List<MacBean> getMacs(){
        return mMacs == null ? null :mMacs;
    }


}

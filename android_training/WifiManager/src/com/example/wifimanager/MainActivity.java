package com.example.wifimanager;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ����wifi -->��Ҫ����Ȩ�� _ android.permission.CHANGE_WIFI_STATE

        WifiManager WifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);

        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = String.format("\"%s\"", "wifi2014");
        wifiConfiguration.preSharedKey = String.format("\"%s\"", "mykeyllll");

        int netId = WifiManager.addNetwork(wifiConfiguration); // ���һ������id

        if (netId != -1) {
            WifiManager.enableNetwork(netId, false); // ͨ������id������wifi
        }

        // �鿴�ѱ���wifi�б� ��ҪȨ�� _ android.permission.ACCESS_WIFI_STATE

        List<WifiConfiguration> wifiConfigurationList = WifiManager.getConfiguredNetworks();

        if (wifiConfigurationList != null) {
            for (int i = 0; i < wifiConfigurationList.size(); i++) {
                WifiConfiguration config = wifiConfigurationList.get(i);
                System.out.println("i : " + i + "____________________");
                System.out.println(config.toString());
            }
        }

        // �鿴��ǰwifi

        System.out.println("------------------------------");
        WifiInfo wifiInfo = WifiManager.getConnectionInfo();
        System.out.println("BSSID : " + wifiInfo.getBSSID());

        // �鿴wifiɨ���ȵ�
        List<ScanResult> scanResultList = WifiManager.getScanResults();

        if (scanResultList != null) {

            for (ScanResult scanResult : scanResultList) {
                System.out.println("_____________________");
                System.out.println(scanResult.toString());
            }
        }

        // ��סWifi
        WifiManager.createMulticastLock("test");
        WifiManager.createWifiLock("");

    }
}
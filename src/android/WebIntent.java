package com.borismus.webintent;

import java.util.List;


import android.app.Activity;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.os.Parcelable;

import android.nfc.Tag;
import android.nfc.NdefRecord;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaResourceApi;
import org.apache.cordova.PluginResult;


/**
 * WebIntent is a PhoneGap plugin that bridges Android intents and web
 * applications:
 * 
 * 1. web apps can spawn intents that call native Android applications. 2.
 * (after setting up correct intent filters for PhoneGap applications), Android
 * intents can be handled by PhoneGap web applications.
 * 
 * @author boris@borismus.com
 * 
 */
public class WebIntent extends CordovaPlugin {

    private static final String TAG = HelloWorldNFCActivity.class.getName();
    private NfcAdapter nfcAdapter;
	private PendingIntent nfcPendingIntent;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
    }


    //public boolean execute(String action, JSONArray args, String callbackId) {
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if (action.equals("getNFCTag")) {
            if(message == "") {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "Tag not found!"));
            } else {
                //return new PluginResult(PluginResult.Status.OK, json);
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, message));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            message = GetTag(intent);
        }
    }

    @Override
	protected void onResume() {

		super.onResume();

		enableForegroundMode();
	}

	@Override
	protected void onPause() {

		super.onPause();

		disableForegroundMode();
	}

    public void enableForegroundMode() {

		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for all
		IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
		nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, writeTagFilters, null);
	}

    public void disableForegroundMode() {

		nfcAdapter.disableForegroundDispatch(this);
	}

    public String GetTag(Intent intent) {
        String nfcData = "";
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if(rawMsgs != null && rawMsgs.length > 0) {
            NdefMessage msg = ((NdefMessage)rawMsgs[0]);
            if(msg != null && msg.getRecords().length > 0) {
                NdefRecord relayRecord = msg.getRecords()[0];
                if(relayRecord != null) {
                    nfcData = new String(relayRecord.getPayload());
                }
            }
        }
        if(nfcData == "") {
            nfcData = intent.getAction();
        }
        return nfcData;
    }



}

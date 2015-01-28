package com.borismus.webintent;

import java.util.List;


import android.app.Activity;
import android.app.PendingIntent;
import android.util.Log;

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

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


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

    private String message = "";
    private static final String TAG = WebIntent.class.getName();
    private NfcAdapter nfcAdapter;
	private PendingIntent nfcPendingIntent;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this.cordova.getActivity());
        nfcPendingIntent = PendingIntent.getActivity(this.cordova.getActivity(), 0, new Intent(this.cordova.getActivity(), this.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
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
        message = GetTag(intent);
    }

    @Override
	public void onResume(boolean multitasking) {

		super.onResume(multitasking);

		enableForegroundMode();
	}

	@Override
	public void onPause(boolean multitasking) {

		super.onPause(multitasking);

		disableForegroundMode();
	}

    public void enableForegroundMode() {

		IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED); // filter for all
		IntentFilter[] writeTagFilters = new IntentFilter[] {tagDetected};
		nfcAdapter.enableForegroundDispatch(this.cordova.getActivity(), nfcPendingIntent, writeTagFilters, null);
	}

    public void disableForegroundMode() {

		nfcAdapter.disableForegroundDispatch(this.cordova.getActivity());
	}

    public String GetTag(Intent intent) {
        Log.d("WebIntentG_GetTag",intent.getAction());
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

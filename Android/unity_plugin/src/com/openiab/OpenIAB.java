package com.openiab;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import com.unity3d.player.UnityPlayer;
import org.onepf.oms.OpenIabHelper;
import org.onepf.oms.appstore.googleUtils.IabHelper;
import org.onepf.oms.appstore.googleUtils.IabResult;
import org.onepf.oms.appstore.googleUtils.Purchase;

import java.util.HashMap;

public class OpenIAB {

    public static final String TAG = "OpenIAB";
    private static final String EVENT_MANAGER = "OpenIABEventManager";
    private static final String BILLING_SUPPORTED_CALLBACK = "OnBillingSupported";
    private static final String BILLING_NOT_SUPPORTED_CALLBACK = "OnBillingNotSupported";
    private static final String PURCHASE_SUCCEEDED_CALLBACK = "OnPurchaseSucceeded";
    private static final String PURCHASE_FAILED_CALLBACK = "OnPurchaseFailed";

    public static final String STORE_GOOGLE = OpenIabHelper.NAME_GOOGLE;
    public static final String STORE_AMAZON = OpenIabHelper.NAME_AMAZON;
    public static final String STORE_TSTORE = OpenIabHelper.NAME_TSTORE;
    public static final String STORE_SAMSUNG = OpenIabHelper.NAME_SAMSUNG;
    public static final String STORE_YANDEX = "YandexPublicKey";

    // (arbitrary) request code for the purchase flow
    private static final int RC_REQUEST = 10001;

    private static OpenIAB _instance;
    private OpenIabHelper _helper;

    public OpenIabHelper getHelper() {
        return _helper;
    }

    public static OpenIAB instance() {
        if (_instance == null) {
            _instance = new OpenIAB();
        }
        return _instance;
    }

    public void init(HashMap<String, String> storeKeys) {
        _helper = new OpenIabHelper(UnityPlayer.currentActivity, storeKeys);
        createBroadcasts();

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        _helper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.e(TAG, "Problem setting up in-app billing: " + result);
                    UnityPlayer.UnitySendMessage(EVENT_MANAGER, BILLING_NOT_SUPPORTED_CALLBACK, result.getMessage());
                    return;
                }

                // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                UnityPlayer.UnitySendMessage(EVENT_MANAGER, BILLING_SUPPORTED_CALLBACK, "");

                // TODO:
                //_helper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        destroyBroadcasts();
    }

    public void purchase(final String productId) {
        Log.i("OpenIAB", "Starting purchase");
        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        _helper.launchPurchaseFlow(UnityPlayer.currentActivity, productId, RC_REQUEST,
                _purchaseFinishedListener, payload);
    }

    // TODO:
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();
        return true;
    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener _purchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
                Log.e(TAG, "Error purchasing: " + result);
                UnityPlayer.UnitySendMessage(EVENT_MANAGER, PURCHASE_FAILED_CALLBACK, result.getMessage());
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                Log.e(TAG, "Error purchasing. Authenticity verification failed.");
                UnityPlayer.UnitySendMessage(EVENT_MANAGER, PURCHASE_FAILED_CALLBACK, "Authenticity verification failed.");
                return;
            }

            Log.d(TAG, "Purchase successful.");
            UnityPlayer.UnitySendMessage(EVENT_MANAGER, PURCHASE_SUCCEEDED_CALLBACK, purchase.getSku());
        }
    };


    //TODO: how to implement automatically store specific broadcast services?

    private void createBroadcasts() {
        Log.d(TAG, "createBroadcasts");
        IntentFilter filter = new IntentFilter(YANDEX_STORE_ACTION_PURCHASE_STATE_CHANGED);
        UnityPlayer.currentActivity.registerReceiver(_billingReceiver, filter);
    }

    private void destroyBroadcasts() {
        Log.d(TAG, "destroyBroadcasts");
        try {
            UnityPlayer.currentActivity.unregisterReceiver(_billingReceiver);
        } catch (Exception ex) {
            Log.d(TAG, "destroyBroadcasts exception:\n" + ex.getMessage());
        }
    }

    // Yandex specific
    public static final String YANDEX_STORE_SERVICE = "com.yandex.store.service";
    public static final String YANDEX_STORE_ACTION_PURCHASE_STATE_CHANGED = YANDEX_STORE_SERVICE + ".PURCHASE_STATE_CHANGED";

    private BroadcastReceiver _billingReceiver = new BroadcastReceiver() {
        private static final String TAG = "YandexBillingReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive intent: " + intent);

            if (YANDEX_STORE_ACTION_PURCHASE_STATE_CHANGED.equals(action)) {
                purchaseStateChanged(intent);
            }
        }

        private void purchaseStateChanged(Intent data) {
            Log.d(TAG, "purchaseStateChanged intent: " + data);
            _helper.handleActivityResult(RC_REQUEST, Activity.RESULT_OK, data);
        }
    };
}

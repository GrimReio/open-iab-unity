package org.onepf.trivialdrive.tests.googleplay;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.vending.billing.IInAppBillingService;
import org.onepf.oms.Appstore;
import org.onepf.oms.AppstoreInAppBillingService;
import org.onepf.oms.OpenIabHelper;
import org.onepf.oms.appstore.GooglePlay;
import org.onepf.oms.appstore.googleUtils.IabHelper;
import org.onepf.oms.appstore.googleUtils.IabResult;
import org.onepf.oms.appstore.googleUtils.Purchase;
import org.onepf.trivialdrive.tests.AsyncAppstoreTest;

public class BillingPurchaseSuccessTest extends AsyncAppstoreTest {

    static final int RC_REQUEST = 10001;
    static final int RC_REQUEST_FALSE = 11111;

    final String _purchaseType;

    public BillingPurchaseSuccessTest(String purchaseType) {
        _purchaseType = purchaseType;
    }

    @Override
    public String getName() {
        return super.getName() + "." + _purchaseType;
    }

    @Override
    protected void runInternal(Context context) {
        final Context c = context;
        Appstore appstore = new GooglePlay(context, "");
        final AppstoreInAppBillingService gpBillingService = new IabHelper(context, "", appstore) {

            // Overrided to handle onActivityResult somehow
            @Override
            public void launchPurchaseFlow(Activity act, String sku, String itemType, int requestCode, OnIabPurchaseFinishedListener listener, String extraData) {
                super.launchPurchaseFlow(act, sku, itemType, requestCode, listener, extraData);
                // TODO: test this stuff also
                //handleActivityResult(RC_REQUEST, Activity.RESULT_OK, data);
                finish(true);
            }

            @Override
            protected IInAppBillingService getServiceFromBinder(IBinder service) {
                return new InAppBillingServiceBase() {
                    @Override
                    public Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type, String developerPayload) throws RemoteException {
                        Bundle buyIntentBundle = new Bundle();
                        buyIntentBundle.putInt(RESPONSE_CODE, BILLING_RESPONSE_RESULT_OK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(c, RC_REQUEST_FALSE, new Intent(), 0);
                        buyIntentBundle.putParcelable(RESPONSE_BUY_INTENT, pendingIntent);
                        return buyIntentBundle;
                    }
                };
            }
        };

        gpBillingService.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                gpBillingService.launchPurchaseFlow((Activity) c, "sku", _purchaseType, RC_REQUEST, new IabHelper.OnIabPurchaseFinishedListener() {
                    @Override
                    public void onIabPurchaseFinished(IabResult result, Purchase info) {
                        finish(result.isSuccess());
                        gpBillingService.dispose();
                    }
                }, "");
            }
        });
    }
}

package org.onepf.trivialdrivedemo.tests.googleplay;

import android.app.Activity;
import android.content.Context;
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
import org.onepf.trivialdrivedemo.tests.AsyncAppstoreTest;

public class BillingPurchaseFailTest extends AsyncAppstoreTest {

    static final int RC_REQUEST = 10001;

    final String _purchaseType;

    public BillingPurchaseFailTest(String purchaseType) {
        _purchaseType = purchaseType;
    }

    @Override
    public String getName() {
        return super.getName() + "." + _purchaseType;
    }

    @Override
    protected void runInternal(Context context) {
        Appstore appstore = new GooglePlay(context, "");
        final AppstoreInAppBillingService gpBillingService = new IabHelper(context, "", appstore) {
            @Override
            protected IInAppBillingService getServiceFromBinder(IBinder service) {
                return new InAppBillingServiceBase() {
                    @Override
                    public Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type, String developerPayload) throws RemoteException {
                        Bundle buyIntentBundle = new Bundle();
                        buyIntentBundle.putInt(RESPONSE_CODE, BILLING_RESPONSE_RESULT_ERROR);
                        return buyIntentBundle;
                    }
                };
            }
        };

        final Context c = context;
        gpBillingService.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                gpBillingService.launchPurchaseFlow((Activity) c, "sku",_purchaseType, RC_REQUEST, new IabHelper.OnIabPurchaseFinishedListener() {
                    @Override
                    public void onIabPurchaseFinished(IabResult result, Purchase info) {
                        finish(result.isFailure());
                        gpBillingService.dispose();
                    }
                }, "");
            }
        });
    }
}

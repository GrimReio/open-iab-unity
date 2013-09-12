package org.onepf.trivialdrivedemo.tests.googleplay;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.vending.billing.IInAppBillingService;
import org.onepf.oms.Appstore;
import org.onepf.oms.AppstoreInAppBillingService;
import org.onepf.oms.appstore.GooglePlay;
import org.onepf.oms.appstore.googleUtils.IabHelper;
import org.onepf.oms.appstore.googleUtils.IabResult;
import org.onepf.trivialdrivedemo.tests.AsyncAppstoreTest;

public class BillingStartSuccessTest extends AsyncAppstoreTest {
    @Override
    protected void runInternal(Context context) {
        Appstore appstore = new GooglePlay(context, "");
        final AppstoreInAppBillingService gpBillingService = new IabHelper(context, "", appstore) {
            @Override
            protected IInAppBillingService getServiceFromBinder(IBinder service) {
                return new InAppBillingServiceBase() {
                    @Override
                    public int isBillingSupported(int apiVersion, String packageName, String type) throws RemoteException {
                        return BILLING_RESPONSE_RESULT_OK;
                    }
                };
            }
        };

        gpBillingService.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                finish(result.isSuccess());
                gpBillingService.dispose();
            }
        });
    }
}

package org.onepf.oms;

import android.os.Bundle;
import android.os.RemoteException;

public class BillingBinder extends IOpenInAppBillingService.Stub {

    static final int RESULT_OK = 0;
    static final int RESULT_USER_CANCELED = 1;
    static final int RESULT_BILLING_UNAVAILABLE = 3;
    static final int RESULT_ITEM_UNAVAILABLE = 4;
    static final int RESULT_DEVELOPER_ERROR = 5;
    static final int RESULT_ERROR = 6;
    static final int RESULT_ITEM_ALREADY_OWNED = 7;
    static final int RESULT_ITEM_NOT_OWNED = 8;

    @Override
    public int isBillingSupported(int apiVersion, String packageName, String type) throws RemoteException {
        // TODO: perform some checks
        return RESULT_OK;
    }

    @Override
    public Bundle getSkuDetails(int apiVersion, String packageName, String type, Bundle skusBundle) throws RemoteException {
        return new Bundle();
    }

    @Override
    public Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type, String developerPayload) throws RemoteException {
        return new Bundle();
    }

    @Override
    public Bundle getPurchases(int apiVersion, String packageName, String type, String continuationToken) throws RemoteException {
        return new Bundle();
    }

    @Override
    public int consumePurchase(int apiVersion, String packageName, String purchaseToken) throws RemoteException {
        // TODO: perform some checks
        return RESULT_OK;
    }
}

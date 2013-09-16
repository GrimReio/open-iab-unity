package org.onepf.trivialdrive.tests.googleplay;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import com.android.vending.billing.IInAppBillingService;

public class InAppBillingServiceBase implements IInAppBillingService {
    @Override
    public int consumePurchase(int apiVersion, String packageName, String purchaseToken) throws RemoteException {
        return 0;
    }

    @Override
    public int isBillingSupported(int apiVersion, String packageName, String type) throws RemoteException {
        return 0;
    }

    @Override
    public Bundle getSkuDetails(int apiVersion, String packageName, String type, Bundle skusBundle) throws RemoteException {
        return null;
    }

    @Override
    public Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type, String developerPayload) throws RemoteException {
        return null;
    }

    @Override
    public Bundle getPurchases(int apiVersion, String packageName, String type, String continuationToken) throws RemoteException {
        return null;
    }

    @Override
    public IBinder asBinder() {
        return null;
    }
}

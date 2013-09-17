package org.onepf.oms;

import android.content.Intent;
import android.os.RemoteException;

public class AppstoreBinder extends IOpenAppstore.Stub {

    public static final String TAG = "OnePF_store";
    private static final String BILLING_BIND_INTENT = "org.onepf.oms.billing.BIND";

    @Override
    public String getAppstoreName() throws RemoteException {
        return "org.onepf.store";
    }

    @Override
    public boolean isPackageInstaller(String packageName) throws RemoteException {
        // TODO: read "installed" package list, dev keys, SKUs, etc from text file
        return true;
    }

    @Override
    public boolean isBillingAvailable(String packageName) throws RemoteException {
        return true;
    }

    @Override
    public int getPackageVersion(String packageName) throws RemoteException {
        // TODO: read version from text file
        return -1;
    }

    @Override
    public Intent getBillingServiceIntent() throws RemoteException {
        return new Intent(BILLING_BIND_INTENT);
    }

    @Override
    public Intent getProductPageIntent(String packageName) throws RemoteException {
        return null;
    }

    @Override
    public Intent getRateItPageIntent(String packageName) throws RemoteException {
        return null;
    }

    @Override
    public Intent getSameDeveloperPageIntent(String packageName) throws RemoteException {
        return null;
    }

    @Override
    public boolean areOutsideLinksAllowed() throws RemoteException {
        return false;
    }
}

package org.onepf.oms;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import org.onepf.oms.data.Application;
import org.onepf.oms.data.Database;

public class AppstoreBinder extends IOpenAppstore.Stub {

    private static final String BILLING_BIND_INTENT = "org.onepf.oms.billing.BIND";

    final Database _db;
    final Context _context;

    public AppstoreBinder(AppstoreService context, Database database) {
        _db = database;
        _context = context;
    }

    @Override
    public String getAppstoreName() throws RemoteException {
        return "org.onepf.store";
    }

    @Override
    public boolean isPackageInstaller(String packageName) throws RemoteException {
        Application app = _db.getApplication(packageName);
        return app != null && app.installed();
    }

    @Override
    public boolean isBillingAvailable(String packageName) throws RemoteException {
        Application app = _db.getApplication(packageName);
        return app != null && app.billingActive();
    }

    @Override
    public int getPackageVersion(String packageName) throws RemoteException {
        Application app = _db.getApplication(packageName);
        return app == null ? -1 : app.getVersion();
    }

    @Override
    public Intent getBillingServiceIntent() throws RemoteException {
        return new Intent(BILLING_BIND_INTENT);
    }

    @Override
    public Intent getProductPageIntent(String packageName) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Intent getRateItPageIntent(String packageName) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Intent getSameDeveloperPageIntent(String packageName) throws RemoteException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean areOutsideLinksAllowed() throws RemoteException {
        throw new UnsupportedOperationException("Not implemented");
    }
}

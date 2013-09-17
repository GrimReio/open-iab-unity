package org.onepf.oms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BillingService extends Service {

    BillingBinder _binder;
    BillingDatabase _database = new BillingDatabase();

    @Override
    public void onCreate() {
        super.onCreate();
        _binder = new BillingBinder(this, _database);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return _binder;
    }
}

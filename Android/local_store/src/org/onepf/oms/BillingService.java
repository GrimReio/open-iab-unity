package org.onepf.oms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BillingService extends Service {

    BillingBinder _binder = new BillingBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return _binder;
    }
}

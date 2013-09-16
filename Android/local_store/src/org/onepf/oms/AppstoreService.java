package org.onepf.oms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AppstoreService extends Service {

    AppstoreBinder _binder = new AppstoreBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return _binder;
    }
}

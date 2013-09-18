package org.onepf.oms;

import android.app.Service;

public abstract class ServiceBase extends Service {

    protected BillingApplication getBillingApplication() {
        return (BillingApplication) getApplication();
    }
}

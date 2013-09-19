package org.onepf.oms;

import android.content.Intent;
import android.os.RemoteException;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

public class BillingServiceTest extends ServiceTestCase<BillingService> {

    BillingBinder _binder;
    MockBillingApplication _app;

    final String _jsonConfig = "{\"applications\":[{\"packageName\":\"org.onepf.trivialdrive\",\"version\":\"1\",\"installed\":\"true\",\"billingActive\":\"true\",\"products\":[{\"productId\":\"sku_gas\",\"type\":\"inapp\",\"price\":\"50\",\"title\":\"GAS\",\"description\":\"car fuel\"},{\"productId\":\"sku_premium\",\"type\":\"inapp\"},{\"productId\":\"sku_infinite_gas\",\"type\":\"subs\"}],\"inventory\":[\"sku_premium\",\"sku_infinite_gas\"]}]}";

    public BillingServiceTest() {
        super(BillingService.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        _binder = null;
        _app = null;
    }

    private void start(String json) {
        _app = new MockBillingApplication(json);
        _app.onCreate();
        setApplication(_app);
        _binder = (BillingBinder) bindService(new Intent());
    }

    @MediumTest
    public void testIsBillingSupported() throws RemoteException {
        start(_jsonConfig);
        assertEquals(_binder.isBillingSupported(3, "org.onepf.trivialdrive", BillingBinder.ITEM_TYPE_INAPP), BillingBinder.RESULT_OK);
        assertEquals(_binder.isBillingSupported(3, "org.onepf.trivialdrive", BillingBinder.ITEM_TYPE_SUBS), BillingBinder.RESULT_OK);
        assertEquals(_binder.isBillingSupported(100500, "org.onepf.trivialdrive", BillingBinder.ITEM_TYPE_INAPP), BillingBinder.RESULT_OK);
        assertEquals(_binder.isBillingSupported(100500, "org.onepf.trivialdrive", BillingBinder.ITEM_TYPE_SUBS), BillingBinder.RESULT_OK);
        assertEquals(_binder.isBillingSupported(0, "org.onepf.trivialdrive", BillingBinder.ITEM_TYPE_INAPP), BillingBinder.RESULT_BILLING_UNAVAILABLE);
        assertEquals(_binder.isBillingSupported(0, "org.onepf.trivialdrive", BillingBinder.ITEM_TYPE_SUBS), BillingBinder.RESULT_BILLING_UNAVAILABLE);
        assertEquals(_binder.isBillingSupported(3, "wrong.app.package", BillingBinder.ITEM_TYPE_INAPP), BillingBinder.RESULT_BILLING_UNAVAILABLE);
        assertEquals(_binder.isBillingSupported(3, "wrong.app.package", BillingBinder.ITEM_TYPE_INAPP), BillingBinder.RESULT_BILLING_UNAVAILABLE);
        assertEquals(_binder.isBillingSupported(0, "org.onepf.trivialdrive", "UNKNOWN_TYPE"), BillingBinder.RESULT_BILLING_UNAVAILABLE);
    }

}

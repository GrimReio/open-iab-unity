package org.onepf.trivialdrive.tests.googleplay;

import android.content.Context;
import org.onepf.oms.Appstore;
import org.onepf.oms.appstore.GooglePlay;
import org.onepf.trivialdrive.tests.AppstoreTest;

public class BillingServiceNotNullTest extends AppstoreTest {

    @Override
    protected void runInternal(Context context) {
        Appstore appstore = new GooglePlay(context, "");
        assertTrue(appstore.getInAppBillingService() != null);
    }
}

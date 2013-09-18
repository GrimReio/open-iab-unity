package org.onepf.oms.data;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onepf.oms.AppstoreBinder;
import org.onepf.oms.BillingBinder;

import java.util.ArrayList;

public class Database {

    long _orderid = 0;

    ArrayList<Application> _appList = new ArrayList<Application>();

    public Database() {
    }

    public Database(String json) throws JSONException {
        JSONObject o = new JSONObject(json);
        JSONArray applicationList = o.getJSONArray("applications");
        for (int i = 0; i < applicationList.length(); ++i) {
            JSONObject app = (JSONObject) applicationList.get(i);
            _appList.add(new Application(app.toString()));
        }
    }

    String nextOrderId() {
        return Long.toString(_orderid++);
    }

    // TODO: implement
    String generateToken() {
        return "no_token";
    }

    Application getApplication(String packageName) {
        for (Application app : _appList) {
            if (app.getPackageName().equals(packageName)) {
                return app;
            }
        }
        return null;
    }

    public SkuDetails getSkuDetails(String packageName, String sku) {
        Application app = getApplication(packageName);
        return app == null ? null : app.getSkuDetails(sku);
    }

    // returns null if failed
    public Purchase purchase(String packageName, String sku, String developerPayload) {
        Application app = getApplication(packageName);
        if (app == null) {
            return null;
        }
        SkuDetails skuDetails = app.getSkuDetails(sku);
        if (skuDetails == null) {
            return null;
        }
        return new Purchase(nextOrderId(), packageName, sku, System.currentTimeMillis(), BillingBinder.PURCHASE_STATE_PURCHASED, developerPayload, generateToken());
    }


}

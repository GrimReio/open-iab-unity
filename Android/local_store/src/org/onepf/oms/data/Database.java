package org.onepf.oms.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.onepf.oms.BillingBinder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

public class Database {

    long _orderid = 0;

    ArrayList<Application> _appList = new ArrayList<Application>();
    ArrayList<Purchase> _purchaseHistory = new ArrayList<Purchase>();

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

    String generateToken(String packageName, String sku) {
        return packageName +"."+ sku +"."+ UUID.randomUUID();
    }

    public Application getApplication(String packageName) {
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
        Purchase purchase = new Purchase(nextOrderId(), packageName, sku, System.currentTimeMillis(), BillingBinder.PURCHASE_STATE_PURCHASED,
                developerPayload, generateToken(packageName, sku));
        if (purchase != null) {
            _purchaseHistory.add(purchase);
        }
        return purchase;
    }

    public int consume(String packageName, String purchaseToken) {
        for (int i = _purchaseHistory.size()-1; i >= 0; --i) {
            if (_purchaseHistory.get(i).getToken().equals(purchaseToken)) {
                _purchaseHistory.remove(i);
                return BillingBinder.RESULT_OK;
            }
        }
        return BillingBinder.RESULT_ITEM_NOT_OWNED;
    }
}

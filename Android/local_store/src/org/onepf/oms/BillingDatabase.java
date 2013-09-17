package org.onepf.oms;

import org.onepf.oms.dto.Purchase;
import org.onepf.oms.dto.SkuDetails;
import java.util.HashMap;

public class BillingDatabase {

    long _orderid = 0;

    HashMap<String, SkuDetails> _skuMap = new HashMap<String, SkuDetails>();

    public BillingDatabase() {
        // TODO: read json data from file
    }

    String nextOrderId() {
        return Long.toString(_orderid++);
    }

    // TODO: implement
    String generateToken() {
        return "";
    }

    public boolean hasSku(String sku) {
        return _skuMap.containsKey(sku);
    }

    public SkuDetails getSkuDetails(String sku) {
        return _skuMap.get(sku);
    }

    // returns null if failed
    public Purchase purchase(String packageName, String sku, String developerPayload) {
        return new Purchase(nextOrderId(), packageName, sku, System.currentTimeMillis(), BillingBinder.PURCHASE_STATE_PURCHASED, developerPayload, generateToken());
    }


}

package org.onepf.oms.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Application {

    String _packageName;
    int _version;
    ArrayList<SkuDetails> _productList = new ArrayList<SkuDetails>();

    public Application(String name, int version) {
        _packageName = name;
        _version = version;
    }

    public Application(String json) throws JSONException {
        JSONObject o = new JSONObject(json);
        _packageName = o.optString("packageName");
        _version = o.optInt("version");
        JSONArray products = o.getJSONArray("products");
        for (int i = 0; i < products.length(); ++i) {
            _productList.add(new SkuDetails(products.get(i).toString()));
        }
    }

    public String getPackageName() {
        return _packageName;
    }

    public int getVersion() {
        return _version;
    }

    public SkuDetails getSkuDetails(String sku) {
        for (SkuDetails product : _productList) {
            if (product.getSku().equals(sku)) {
                return product;
            }
        }
        return null;
    }
}

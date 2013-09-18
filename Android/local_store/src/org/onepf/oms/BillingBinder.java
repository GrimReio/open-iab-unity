package org.onepf.oms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import org.onepf.oms.data.Database;
import org.onepf.oms.data.Purchase;
import org.onepf.oms.data.SkuDetails;

import java.util.ArrayList;

public class BillingBinder extends IOpenInAppBillingService.Stub {

    // Response result codes
    public static final int RESULT_OK = 0;
    public static final int RESULT_USER_CANCELED = 1;
    public static final int RESULT_BILLING_UNAVAILABLE = 3;
    public static final int RESULT_ITEM_UNAVAILABLE = 4;
    public static final int RESULT_DEVELOPER_ERROR = 5;
    public static final int RESULT_ERROR = 6;
    public static final int RESULT_ITEM_ALREADY_OWNED = 7;
    public static final int RESULT_ITEM_NOT_OWNED = 8;

    // Keys for the responses
    public static final String RESPONSE_CODE = "RESPONSE_CODE";
    public static final String DETAILS_LIST = "DETAILS_LIST";
    public static final String BUY_INTENT = "BUY_INTENT";
    public static final String INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    public static final String INAPP_DATA_SIGNATURE = "INAPP_DATA_SIGNATURE";
    public static final String INAPP_PURCHASE_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    public static final String INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    public static final String INAPP_DATA_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
    public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";

    // Param keys
    public static final String ITEM_ID_LIST = "ITEM_ID_LIST";
    public static final String ITEM_TYPE_LIST = "ITEM_TYPE_LIST";

    // Item types
    public static final String ITEM_TYPE_INAPP = "inapp";
    public static final String ITEM_TYPE_SUBS = "subs";

    // Purchase states
    public static final int PURCHASE_STATE_PURCHASED = 0;
    public static final int PURCHASE_STATE_CANCELED = 1;
    public static final int PURCHASE_STATE_REFUNDED = 2;

    final Database _db;
    final Context _context;

    public BillingBinder(Context context, Database database) {
        _db = database;
        _context = context;
    }

    @Override
    public int isBillingSupported(int apiVersion, String packageName, String type) throws RemoteException {
        // TODO: perform some checks
        return RESULT_OK;
    }

    /**
     * Provides details of a list of SKUs
     * Given a list of SKUs of a valid type in the skusBundle, this returns a bundle
     * with a list JSON strings containing the productId, price, title and description.
     * This API can be called with a maximum of 20 SKUs.
     *
     * @param apiVersion  billing API version that the Third-party is using
     * @param packageName the package name of the calling app
     * @param skusBundle  bundle containing a StringArrayList of SKUs with key "ITEM_ID_LIST"
     * @return Bundle containing the following key-value pairs
     *         "RESPONSE_CODE" with int value, RESULT_OK(0) if success, other response codes on
     *         failure as listed above.
     *         "DETAILS_LIST" with a StringArrayList containing purchase information
     *         in JSON format similar to:
     *         '{ "productId" : "exampleSku", "type" : "inapp", "price" : "$5.00",
     *         "title : "Example Title", "description" : "This is an example description" }'
     */
    @Override
    public Bundle getSkuDetails(int apiVersion, String packageName, String type, Bundle skusBundle) throws RemoteException {
        Bundle result = new Bundle();

        if (!skusBundle.containsKey(ITEM_ID_LIST)) {
            result.putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
            return result;
        }

        ArrayList<String> itemIdList = skusBundle.getStringArrayList(ITEM_ID_LIST);

        if (itemIdList == null || itemIdList.size() <= 0 || itemIdList.size() >= 20) {
            result.putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
            return result;
        }

        ArrayList<String> detailsList = new ArrayList<String>();
        for (String itemId : itemIdList) {
            SkuDetails skuDetails = _db.getSkuDetails(packageName, itemId);
            if (skuDetails != null) {
                detailsList.add(skuDetails.toJson());
            }
        }

        if (detailsList.size() <= 0) {
            result.putInt(RESPONSE_CODE, RESULT_ITEM_UNAVAILABLE);
        } else {
            result.putInt(RESPONSE_CODE, RESULT_OK);
            result.putStringArrayList(DETAILS_LIST, detailsList);
        }

        return result;
    }

    /**
     * Returns a pending intent to launch the purchase flow for an in-app item by providing a SKU,
     * the type, a unique purchase token and an optional developer payload.
     * @param apiVersion billing API version that the app is using
     * @param packageName package name of the calling app
     * @param sku the SKU of the in-app item as published in the developer console
     * @param type the type of the in-app item ("inapp" for one-time purchases
     *        and "subs" for subscription).
     * @param developerPayload optional argument to be sent back with the purchase information
     * @return Bundle containing the following key-value pairs
     *         "RESPONSE_CODE" with int value, RESULT_OK(0) if success, other response codes on
     *              failure as listed above.
     *         "BUY_INTENT" - PendingIntent to start the purchase flow
     *
     * The Pending intent should be launched with startIntentSenderForResult. When purchase flow
     * has completed, the onActivityResult() will give a resultCode of OK or CANCELED.
     * If the purchase is successful, the result data will contain the following key-value pairs
     *         "RESPONSE_CODE" with int value, RESULT_OK(0) if success, other response codes on
     *              failure as listed above.
     *         "INAPP_PURCHASE_DATA" - String in JSON format similar to
     *              '{"orderId":"12999763169054705758.1371079406387615",
     *                "packageName":"com.example.app",
     *                "productId":"exampleSku",
     *                "purchaseTime":1345678900000,
     *                "purchaseToken" : "122333444455555",
     *                "developerPayload":"example developer payload" }'
     *         "INAPP_DATA_SIGNATURE" - String containing the signature of the purchase data that
     *                                  was signed with the private key of the developer
     *                                  TODO: change this to app-specific keys.
     */
    @Override
    public Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type, String developerPayload) throws RemoteException {
        Bundle result = new Bundle();

        PendingIntent pendingIntent;
        Intent intent = new Intent(_context, PurchaseActivity.class);

        Purchase purchase = _db.purchase(packageName, sku, developerPayload);
        if (purchase == null) {
            SkuDetails skuDetails = _db.getSkuDetails(packageName, sku);
            if (skuDetails == null) {
                intent.putExtra(RESPONSE_CODE, RESULT_ITEM_UNAVAILABLE);
            } else if (!skuDetails.getType().equals(type)) {
                intent.putExtra(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
            } else {
                intent.putExtra(RESPONSE_CODE, RESULT_ERROR);
            }
        } else {
            intent.putExtra(RESPONSE_CODE, RESULT_OK);
            intent.putExtra(INAPP_PURCHASE_DATA, purchase.toJson());
            // TODO: create signature properly!
            intent.putExtra(INAPP_DATA_SIGNATURE, "no_signature");
        }

        pendingIntent = PendingIntent.getActivity(_context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        result.putParcelable(BUY_INTENT, pendingIntent);
        return result;
    }

    // TODO: implement with continuation token
    @Override
    public Bundle getPurchases(int apiVersion, String packageName, String type, String continuationToken) throws RemoteException {
        return new Bundle();
    }

    @Override
    public int consumePurchase(int apiVersion, String packageName, String purchaseToken) throws RemoteException {
        // TODO: perform some checks
        return RESULT_OK;
    }
}

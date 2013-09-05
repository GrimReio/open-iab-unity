using System.Collections.Generic;
using UnityEngine;
using OpenIabPlugin;

public class OpenIABGame : MonoBehaviour {
#if UNITY_ANDROID
    private const int BUTTON_WIDTH = 450;
    private const int BUTTON_HEIGHT = 80;
    private const int LABEL_HEIGHT = 20;
    private const int OFFSET = 10;
    private const int TANK_MAX = 4;
    private const string TANK_SAVE_KEY = "tank";
    private const string DISTANCE_SAVE_KEY = "distance";
    
    private const string SKU_GAS = "sku_gas";
    private const string SKU_PREMIUM = "sku_premium";
    private const string SKU_INFINITE_GAS = "sku_infinite_gas";

    int _tank = TANK_MAX;
    int _distance = 0;
    bool _processingPayment = false;
    bool _isPremium = false;
    bool _subscribedToInfiniteGas = false;

    [SerializeField]
    Car _car = null;

    private void Awake() {
        OpenIABEventManager.billingSupportedEvent += OnBillingSupported;
        OpenIABEventManager.billingNotSupportedEvent += OnBillingNotSupported;
        OpenIABEventManager.queryInventorySucceededEvent += OnQueryInventorySucceeded;
        OpenIABEventManager.queryInventoryFailedEvent += OnQueryInventoryFailed;
        OpenIABEventManager.purchaseSucceededEvent += OnPurchaseSucceded;
        OpenIABEventManager.purchaseFailedEvent += OnPurchaseFailed;
        OpenIABEventManager.consumePurchaseSucceededEvent += OnConsumePurchaseSucceeded;
        OpenIABEventManager.consumePurchaseFailedEvent += OnConsumePurchaseFailed;
    }

    private void Start() {
        LoadData();
        OpenIAB.init(new Dictionary<string, string> {
            {OpenIAB.STORE_GOOGLE, ""},
            {OpenIAB.STORE_AMAZON, ""},
            {OpenIAB.STORE_TSTORE, ""},
            {OpenIAB.STORE_SAMSUNG, ""},
            {OpenIAB.STORE_YANDEX, ""}
        });
    }

    private void OnDestroy() {
        OpenIABEventManager.billingSupportedEvent -= OnBillingSupported;
        OpenIABEventManager.billingNotSupportedEvent -= OnBillingNotSupported;
        OpenIABEventManager.queryInventorySucceededEvent -= OnQueryInventorySucceeded;
        OpenIABEventManager.queryInventoryFailedEvent -= OnQueryInventoryFailed;
        OpenIABEventManager.purchaseSucceededEvent -= OnPurchaseSucceded;
        OpenIABEventManager.purchaseFailedEvent -= OnPurchaseFailed;
        OpenIABEventManager.consumePurchaseSucceededEvent -= OnConsumePurchaseSucceeded;
        OpenIABEventManager.consumePurchaseFailedEvent -= OnConsumePurchaseFailed;
    }

    // Verifies the developer payload of a purchase.
    bool VerifyDeveloperPayload(string developerPayload) {
        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         * 
         * WARNING: Locally generating a random string when starting a purchase and 
         * verifying it here might seem like a good approach, but this will fail in the 
         * case where the user purchases an item on one device and then uses your app on 
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         * 
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         * 
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on 
         *    one device work on other devices owned by the user).
         * 
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */
        return true;
    }

    private void OnBillingSupported() {
        Debug.Log("Billing is supported");
        OpenIAB.queryInventory();
    }

    private void OnBillingNotSupported(string error) {
        Debug.Log("Billing not supported: " + error);
        _car.SetRegular();
    }

    private void OnQueryInventorySucceeded(Inventory inventory) {
        Debug.Log("Query inventory succeeded: " + inventory);
        /*
        * Check for items we own. Notice that for each purchase, we check
        * the developer payload to see if it's correct! See
        * verifyDeveloperPayload().
        */
        // Do we have the premium upgrade?
        Purchase premiumPurchase = inventory.GetPurchase(SKU_PREMIUM);
        _isPremium = (premiumPurchase != null && VerifyDeveloperPayload(premiumPurchase.DeveloperPayload));
        Debug.Log("User is " + (_isPremium ? "PREMIUM" : "NOT PREMIUM"));
        if (_isPremium) {
            _car.SetPremium();
        } else {
            _car.SetRegular();
        }

        // Do we have the infinite gas plan?
        Purchase infiniteGasPurchase = inventory.GetPurchase(SKU_INFINITE_GAS);
        _subscribedToInfiniteGas = (infiniteGasPurchase != null && VerifyDeveloperPayload(infiniteGasPurchase.DeveloperPayload));
        Debug.Log("User " + (_subscribedToInfiniteGas ? "HAS" : "DOES NOT HAVE") + " infinite gas subscription.");
        if (_subscribedToInfiniteGas) _tank = TANK_MAX;

        // Check for gas delivery -- if we own gas, we should fill up the tank immediately
        Purchase gasPurchase = inventory.GetPurchase(SKU_GAS);
        if (gasPurchase != null && VerifyDeveloperPayload(gasPurchase.DeveloperPayload)) {
            Debug.Log("We have gas. Consuming it.");
            OpenIAB.consumeProduct(inventory.GetPurchase(SKU_GAS));
        }
    }

    private void OnQueryInventoryFailed(string error) {
        Debug.Log("Query inventory failed: " + error);
    }

    private void OnPurchaseSucceded(Purchase purchase) {
        Debug.Log("Purchase succeded: " + purchase.Sku + "; Payload: " + purchase.DeveloperPayload);
        if (!VerifyDeveloperPayload(purchase.DeveloperPayload)) {
            return;
        }
        switch (purchase.Sku) {
            case SKU_GAS:
                OpenIAB.consumeProduct(purchase);
                break;
            case SKU_PREMIUM:
                _isPremium = true;
                _car.SetPremium();
                break;
            case SKU_INFINITE_GAS:
                _subscribedToInfiniteGas = true;
                _tank = TANK_MAX;
                break;
            default:
                Debug.LogWarning("Unknown SKU: " + purchase.Sku);
                break;
        }
        _processingPayment = false;
    }

    private void OnPurchaseFailed(string error) {
        Debug.Log("Purchase failed: " + error);
        _processingPayment = false;
    }

    private void OnConsumePurchaseSucceeded(Purchase purchase) {
        Debug.Log("Consume purchase succeded: " + purchase.ToString());
        // TODO: implement SKU check if needed
        _tank = _tank == TANK_MAX ? TANK_MAX : _tank + 1;
        SaveData();
        _processingPayment = false;
    }

    private void OnConsumePurchaseFailed(string error) {
        Debug.Log("Consume purchase failed: " + error);
        _processingPayment = false;
    }

    private void OnGUI() {
        GUI.skin.box.alignment = TextAnchor.MiddleCenter;
        int offset = OFFSET;
        if (_subscribedToInfiniteGas)
            GUI.Label(new Rect(Screen.width/2-BUTTON_WIDTH/2, offset, BUTTON_WIDTH, LABEL_HEIGHT), string.Format("You drove {0} miles.", _distance));
        else
            GUI.Label(new Rect(Screen.width/2-BUTTON_WIDTH/2, offset, BUTTON_WIDTH, LABEL_HEIGHT), string.Format("You drove {0} miles. Gas: {1}", _distance, _tank));
        offset += OFFSET+LABEL_HEIGHT;
        
        // Drive button
        if (_tank <= 0) {
            GUI.Box(new Rect(Screen.width/2-BUTTON_WIDTH/2, offset, BUTTON_WIDTH, BUTTON_HEIGHT), "OUT OF GAS");
        } else {
            if (GUI.Button(new Rect(Screen.width/2-BUTTON_WIDTH/2, offset, BUTTON_WIDTH, BUTTON_HEIGHT), "DRIVE")) {
                _distance += 10;
                if (!_subscribedToInfiniteGas)
                    --_tank;
                SaveData();
                Debug.Log("Vrooom. Tank is now " + _tank);
            }
        }
        offset += OFFSET+BUTTON_HEIGHT;
        
        // Buy gas button
        if (!_subscribedToInfiniteGas && _tank < TANK_MAX)
            ShowBuyButton("BUY GAS", SKU_GAS, "PAYLOAD", ref offset);

        if (!_subscribedToInfiniteGas)
            ShowBuyButton("SUBSCRIBE TO INFINITE GAS", SKU_INFINITE_GAS, "PAYLOAD", ref offset);

        if (!_isPremium)
            ShowBuyButton("UPGRADE CAR", SKU_PREMIUM, "PAYLOAD", ref offset);
    }

    private void ShowBuyButton(string title, string sku, string payload, ref int offset) {
        if (_processingPayment) {
            GUI.Box(new Rect(Screen.width/2-BUTTON_WIDTH/2, offset, BUTTON_WIDTH, BUTTON_HEIGHT), title);
        } else {
            if (GUI.Button(new Rect(Screen.width/2-BUTTON_WIDTH/2, offset, BUTTON_WIDTH, BUTTON_HEIGHT), title)) {
                _processingPayment = true;
                OpenIAB.purchaseProduct(sku, payload);
            }
        }
        offset += OFFSET+BUTTON_HEIGHT;
    }

    private void SaveData() {
        PlayerPrefs.SetInt(TANK_SAVE_KEY, _tank);
        PlayerPrefs.SetInt(DISTANCE_SAVE_KEY, _distance);
    }

    private void LoadData() {
        _tank = PlayerPrefs.GetInt(TANK_SAVE_KEY, TANK_MAX);
        _distance = PlayerPrefs.GetInt(DISTANCE_SAVE_KEY, 0);
    }
#endif
}
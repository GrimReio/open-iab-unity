using System.Collections.Generic;
using UnityEngine;

public class OpenIABGame : MonoBehaviour {
#if UNITY_ANDROID
    private const int BUTTON_WIDTH = 450;
    private const int BUTTON_HEIGHT = 100;
    private const int OFFSET = 15;
    private const int TANK_MAX = 4;
    private const string TANK_SAVE_KEY = "tank";
    private const string DISTANCE_SAVE_KEY = "distance";
    private const string SKU_GAS = "sku_gas";

    int _tank = TANK_MAX;
    int _distance = 0;
    bool _processingPayment = false;

    private void Awake() {
        LoadData();
        OpenIAB.init(new Dictionary<string, string> {
            {OpenIAB.STORE_GOOGLE, ""},
            {OpenIAB.STORE_AMAZON, ""},
            {OpenIAB.STORE_TSTORE, ""},
            {OpenIAB.STORE_SAMSUNG, ""},
            {OpenIAB.STORE_YANDEX, ""}
        });
    }

    private void OnEnable() {
        OpenIABEventManager.billingSupportedEvent += OnBillingSupported;
        OpenIABEventManager.billingNotSupportedEvent += OnBillingNotSupported;
        OpenIABEventManager.purchaseSucceededEvent += OnPurchaseSucceded;
        OpenIABEventManager.purchaseFailedEvent += OnPurchaseFailed;
    }

    private void OnDisable() {
        OpenIABEventManager.billingSupportedEvent -= OnBillingSupported;
        OpenIABEventManager.billingNotSupportedEvent -= OnBillingNotSupported;
        OpenIABEventManager.purchaseSucceededEvent -= OnPurchaseSucceded;
        OpenIABEventManager.purchaseFailedEvent -= OnPurchaseFailed;
    }

    private void OnPurchaseSucceded(string productId) {
        Debug.Log("Purchase succeded: " + productId);
        switch (productId) {
            case SKU_GAS:
                _tank += 1;
                SaveData();
                break;
        }
        _processingPayment = false;
    }

    private void OnPurchaseFailed(string error) {
        Debug.Log("Purchase failed: " + error);
        _processingPayment = false;
    }

    private void OnBillingSupported() {
        Debug.Log("Billing is supported");
    }

    private void OnBillingNotSupported(string error) {
        Debug.Log("Billing not supported: " + error);
    }

    private void OnGUI() {
        GUI.skin.box.alignment = TextAnchor.MiddleCenter;
        GUI.Label(new Rect(Screen.width/2-BUTTON_WIDTH/2, OFFSET, BUTTON_WIDTH, 30), string.Format("You drove {0} miles. Gas: {1}", _distance, _tank));
        if (_tank <= 0) {
            GUI.Box(new Rect(Screen.width/2-BUTTON_WIDTH/2, OFFSET*3, BUTTON_WIDTH, BUTTON_HEIGHT), "OUT OF GAS");
        } else {
            if (GUI.Button(new Rect(Screen.width/2-BUTTON_WIDTH/2, OFFSET*3, BUTTON_WIDTH, BUTTON_HEIGHT), "DRIVE")) {
                _distance += 10;
                --_tank;
                SaveData();
                Debug.Log("Vrooom. Tank is now " + _tank);
            }
        }
        if (_tank < TANK_MAX) {
            if (_processingPayment) {
                GUI.Box(new Rect(Screen.width/2-BUTTON_WIDTH/2, BUTTON_HEIGHT+OFFSET*4, BUTTON_WIDTH, BUTTON_HEIGHT), "BUYING GAS");
            } else {
                if (GUI.Button(new Rect(Screen.width/2-BUTTON_WIDTH/2, BUTTON_HEIGHT+OFFSET*4, BUTTON_WIDTH, BUTTON_HEIGHT), "BUY GAS")) {
                    _processingPayment = true;
                    OpenIAB.purchase(SKU_GAS);
                }
            }
        }
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
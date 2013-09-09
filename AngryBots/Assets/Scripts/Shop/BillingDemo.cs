using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using OpenIabPlugin;

public class BillingDemo : MonoBehaviour {

    private const int OFFSET = 5;
    private const int BUTTON_WIDTH = 200;
    private const int BUTTON_HEIGHT = 80;

    private const int SIDE_BUTTON_WIDTH = 120;
    private const int SIDE_BUTTON_HEIGHT = 80;

    private const int WINDOW_WIDTH = 400;
    private const int WINDOW_HEIGHT = 400;

    private const int FONT_SIZE = 24;

    private const string SKU_MEDKIT = "sku_medkit";

    private bool _processingPayment = false;
    private bool _showShopWindow = false;
    private string _popupText = "";

    private GameObject[] _joysticks = null;

    [SerializeField]
    private MedKitPack _playerMedKitPack = null;

    private string[] _sideButtons = new string[] {
        "MedKit"
    };

    #region Billing
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
        //LoadData();
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
    }

    private void OnQueryInventorySucceeded(Inventory inventory) {
        Debug.Log("Query inventory succeeded: " + inventory);

        // Check for delivery of expandable items. If we own some, we should consume everything immediately
        Purchase medKitPurchase = inventory.GetPurchase(SKU_MEDKIT);
        if (medKitPurchase  != null && VerifyDeveloperPayload(medKitPurchase.DeveloperPayload)) {
            Debug.Log("We have MedKit. Consuming it.");
            OpenIAB.consumeProduct(inventory.GetPurchase(SKU_MEDKIT));
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
            case SKU_MEDKIT:
                OpenIAB.consumeProduct(purchase);
                return;
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
        _playerMedKitPack.Supply(1);
        _processingPayment = false;
    }

    private void OnConsumePurchaseFailed(string error) {
        Debug.Log("Consume purchase failed: " + error);
        _processingPayment = false;
    }
    #endregion // Billing

    #region GUI
    void DrawPopup(int windowID) {
        // Close button
        if (GUI.Button(new Rect(WINDOW_WIDTH-35, 5, 30, 30), "X")) {
            _popupText = "";
            PauseGame(false);
            ShowJoysticks(true);
        }
        // Text
        GUI.Label(new Rect(10, WINDOW_HEIGHT*0.3f, WINDOW_WIDTH-20, WINDOW_HEIGHT), _popupText);
    }

    void DrawShopWindow(int windowID) {
        // Close button
        if (GUI.Button(new Rect(WINDOW_WIDTH-35, 5, 30, 30), "X")) {
            ShowShopWindow(false);
        }

        if (_processingPayment) return;

        GUI.skin.box.alignment = TextAnchor.MiddleCenter;

        // Buy MedKit
        if (_playerMedKitPack.IsFull) {
            GUI.Box(new Rect(10, 40, WINDOW_WIDTH-20, SIDE_BUTTON_HEIGHT), "MedKit pack is full");
        } else if (GUI.Button(new Rect(10, 40, WINDOW_WIDTH-20, SIDE_BUTTON_HEIGHT), "Buy MedKit")) {
            _processingPayment = true;
            OpenIAB.purchaseProduct(SKU_MEDKIT);
        }
    }

    void DrawSidePanel() {
        if (_sideButtons.Length <= 0 || _showShopWindow || !string.IsNullOrEmpty(_popupText)) return;

        _sideButtons[0] = string.Format("MedKit ({0})", _playerMedKitPack.Count);

        bool[] buttons = new bool[_sideButtons.Length];
        int startY = Screen.height/2-(SIDE_BUTTON_HEIGHT*_sideButtons.Length)/2;
        for (int i = 0; i < _sideButtons.Length; ++i) {
            buttons[i] = GUI.Button(new Rect(0, startY+SIDE_BUTTON_HEIGHT*i, SIDE_BUTTON_WIDTH, SIDE_BUTTON_HEIGHT), _sideButtons[i]);
        }

        // MedKit button
        if (buttons[0]) {
            if (!_playerMedKitPack.Use()) {
                ShowPopup("Sorry, no MedKit's left. You can buy supplies in the Shop.");
            }
        }
    }

    void ShowPopup(string text) {
        _popupText = text;
        PauseGame(true);
        ShowJoysticks(false);
    }

    void ShowJoysticks(bool show) {
        if (show) {
            foreach (var j in _joysticks) {
                j.SetActive(true);
            }
        } else {
            _joysticks = GameObject.FindGameObjectsWithTag("Joystick");
            foreach (var j in _joysticks) {
                j.SetActive(false);
            }
        }
    }

    void ShowShopWindow(bool show) {
        _showShopWindow = show;
        PauseGame(show);
        ShowJoysticks(!show);
    }

    void OnGUI() {
        GUI.skin.window.fontSize = GUI.skin.label.fontSize = GUI.skin.box.fontSize = GUI.skin.button.fontSize = FONT_SIZE;
        if (!_showShopWindow) {
            if (string.IsNullOrEmpty(_popupText) && GUI.Button(new Rect(Screen.width-BUTTON_WIDTH-OFFSET, OFFSET, BUTTON_WIDTH, BUTTON_HEIGHT), "Shop", GUI.skin.button)) {
                ShowShopWindow(true);
            }
        } else {
            GUI.Window(0, new Rect(Screen.width/2-WINDOW_WIDTH/2, Screen.height/2-WINDOW_HEIGHT/2, WINDOW_WIDTH, WINDOW_HEIGHT), DrawShopWindow, "Game Shop");
        }

        DrawSidePanel();

        if (!string.IsNullOrEmpty(_popupText)) {
            GUI.Window(0, new Rect(Screen.width/2-WINDOW_WIDTH/2, Screen.height/2-WINDOW_HEIGHT/2, WINDOW_WIDTH, WINDOW_HEIGHT), DrawPopup, "");
        }
    }
    #endregion // GUI

    void PauseGame(bool pause) {
        Time.timeScale = pause ? 0 : 1;
    }
}

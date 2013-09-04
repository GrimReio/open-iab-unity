using UnityEngine;
using System;
using System.Collections;

public class OpenIABEventManager : MonoBehaviour {
#if UNITY_ANDROID
    // Fired after init is called when billing is supported on the device
    public static event Action billingSupportedEvent;
    // Fired after init is called when billing is not supported on the device
    public static event Action<string> billingNotSupportedEvent;
    // Fired when a purchase completes allowing you to verify the signature on an external server if you would like
    public static event Action<string, string> purchaseCompleteAwaitingVerificationEvent;
    // Fired when a purchase succeeds
    public static event Action<string> purchaseSucceededEvent;
    // Fired when a purchase fails
    public static event Action<string> purchaseFailedEvent;

    private void Awake() {
        // Set the GameObject name to the class name for easy access from native plugin
        gameObject.name = GetType().ToString();
        DontDestroyOnLoad(this);
    }

    private void OnBillingSupported(string empty) {
        if (billingSupportedEvent != null)
            billingSupportedEvent();
    }
    private void OnBillingNotSupported(string error) {
        if (billingNotSupportedEvent != null)
            billingNotSupportedEvent(error);
    }
    private void OnPurchaseCompleteAwaitingVerification(string skuAndPayload) {
        string[] tokens = skuAndPayload.Split('|');
        if (tokens.Length < 2) {
            if (purchaseFailedEvent != null) {
                purchaseFailedEvent("Invalid developer payload");
            }
            return;
        }
        if (purchaseCompleteAwaitingVerificationEvent != null) {
            purchaseCompleteAwaitingVerificationEvent(tokens[0], tokens[1]);
        }
    }
    private void OnPurchaseSucceeded(string sku) {
        if (purchaseSucceededEvent != null)
            purchaseSucceededEvent(sku);
    }
    private void OnPurchaseFailed(string error) {
        if (purchaseFailedEvent != null)
            purchaseFailedEvent(error);
    }
#else
    private void Awake() {
        Destroy(this);
    }
#endif // UNITY_ANDROID
}

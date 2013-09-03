using UnityEngine;
using System;
using System.Collections;

public class OpenIABEventManager : MonoBehaviour {
#if UNITY_ANDROID
    // Fired after init is called when billing is supported on the device
    public static event Action billingSupportedEvent;
    // Fired after init is called when billing is not supported on the device
    public static event Action<string> billingNotSupportedEvent;
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
    private void OnPurchaseSucceeded(string productId) {
        if (purchaseSucceededEvent != null)
            purchaseSucceededEvent(productId);
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

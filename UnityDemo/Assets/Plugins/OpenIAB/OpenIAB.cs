using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using System;

#if UNITY_ANDROID
public class OpenIAB {
    private static AndroidJavaObject _plugin;

    public static readonly string STORE_GOOGLE;
    public static readonly string STORE_AMAZON;
    public static readonly string STORE_TSTORE;
    public static readonly string STORE_SAMSUNG;
    public static readonly string STORE_YANDEX;

    private static GameObject OpenIABEventManager { get { return GameObject.Find("OpenIABEventManager"); } }

    static OpenIAB() {
        if (Application.platform != RuntimePlatform.Android) {
            STORE_GOOGLE = "STORE_GOOGLE";
            STORE_AMAZON = "STORE_AMAZON";
            STORE_TSTORE = "STORE_TSTORE";
            STORE_SAMSUNG = "STORE_SAMSUNG";
            STORE_YANDEX = "STORE_YANDEX";
            return;
        }

        // Find the plugin instance
        using (var pluginClass = new AndroidJavaClass("com.openiab.OpenIAB")) {
            _plugin = pluginClass.CallStatic<AndroidJavaObject>("instance");
            STORE_GOOGLE = pluginClass.GetStatic<string>("STORE_GOOGLE");
            STORE_AMAZON = pluginClass.GetStatic<string>("STORE_AMAZON");
            STORE_TSTORE = pluginClass.GetStatic<string>("STORE_TSTORE");
            STORE_SAMSUNG = pluginClass.GetStatic<string>("STORE_SAMSUNG");
            STORE_YANDEX = pluginClass.GetStatic<string>("STORE_YANDEX");
            Debug.Log("********** OpenIAB plugin initialized **********");
        }
    }

    // Starts up the billing service. This will also check to see if in app billing is supported and fire the appropriate event
    public static void init(Dictionary<string, string> storeKeys) {
        if (Application.platform != RuntimePlatform.Android) {
            OpenIABEventManager.SendMessage("OnBillingSupported", "");
            return;
        }

        using (AndroidJavaObject obj_HashMap = new AndroidJavaObject("java.util.HashMap")) {
            IntPtr method_Put = AndroidJNIHelper.GetMethodID(obj_HashMap.GetRawClass(), "put",
                "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");

            object[] args = new object[2];
            foreach (KeyValuePair<string, string> kvp in storeKeys) {
                using (AndroidJavaObject k = new AndroidJavaObject("java.lang.String", kvp.Key)) {
                    using (AndroidJavaObject v = new AndroidJavaObject("java.lang.String", kvp.Value)) {
                        args[0] = k;
                        args[1] = v;
                        AndroidJNI.CallObjectMethod(obj_HashMap.GetRawObject(),
                            method_Put, AndroidJNIHelper.CreateJNIArgArray(args));
                    }
                }
            }
            _plugin.Call("init", obj_HashMap);
        }
    }

    // Purchases the product with the given productId
    public static void purchase(string productId) {
        if (Application.platform != RuntimePlatform.Android) {
            OpenIABEventManager.SendMessage("OnPurchaseSucceeded", productId);
            return;
        }
        _plugin.Call("purchase", productId);
    }
}
#endif // UNITY_ANDROID

using UnityEngine;
using System.Collections;

public class MedKitPack : MonoBehaviour {

    private const int MAX_KITS = 5;
    private int _nMedKits = 1;

    public int Count { get { return _nMedKits; } }

    public bool IsFull { get { return _nMedKits >= MAX_KITS; } }

    void Awake() {
        _nMedKits = PlayerPrefs.GetInt("nMedKits", 1);
    }

    void SaveData() {
        PlayerPrefs.SetInt("nMedKits", _nMedKits);
    }

    public void Supply(int nMedKits) {
        _nMedKits += nMedKits;
        if (_nMedKits > MAX_KITS) {
            _nMedKits = MAX_KITS;
        }
        SaveData();
    }

    public bool Use() {
        if (_nMedKits > 0) {
            --_nMedKits;
            SendMessage("Heal");
            SaveData();
            return true;
        } else {
            return false;
        }
    }
}

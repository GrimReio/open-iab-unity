using UnityEngine;
using System.Collections;

public class Car : MonoBehaviour {

    [SerializeField]
    float _rotationSpeed = 100.0f;

    [SerializeField]
    GameObject _simpleCar = null;
    [SerializeField]
    GameObject _premiumCar = null;

    void Awake() {
        _simpleCar.SetActive(false);
        _premiumCar.SetActive(false);
    }

    void Update() {
        transform.Rotate(0, _rotationSpeed * Time.deltaTime, 0);
    }

    public void SetPremium() {
        _simpleCar.SetActive(false);
        _premiumCar.SetActive(true);
    }

    public void SetRegular() {
        _simpleCar.SetActive(true);
        _premiumCar.SetActive(false);
    }
}

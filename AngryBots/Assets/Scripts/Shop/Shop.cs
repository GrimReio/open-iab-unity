using UnityEngine;
using System.Collections;

public class Shop : MonoBehaviour {

    private const int OFFSET = 5;
    private const int BUTTON_WIDTH = 200;
    private const int BUTTON_HEIGHT = 80;

    private const int WINDOW_WIDTH = 400;
    private const int WINDOW_HEIGHT = 600;

    private const int FONT_SIZE = 24;

    private bool _showShopWindow = false;

    [SerializeField]
    private GameObject _sidePanel = null;
    [SerializeField]
    private GameObject[] _gamePads = null;

    private GameObject[] _joysticks = null;

    void PauseGame(bool pause) {
        Time.timeScale = pause ? 0 : 1;
    }
    void DrawShopWindow(int windowID) {
        // Close button
        if (GUI.Button(new Rect(WINDOW_WIDTH-35, 5, 30, 30), "X")) {
            ShowShopWindow(false);
        }
    }
    void ShowShopWindow(bool show) {
        _sidePanel.SetActive(!show);
        _showShopWindow = show;
        PauseGame(show);
        if (show) {
            _joysticks = GameObject.FindGameObjectsWithTag("Joystick");
            foreach (var j in _joysticks) {
                j.SetActive(false);
            }
        } else {
            foreach (var j in _joysticks) {
                j.SetActive(true);
            }
        }
    }
    void OnGUI() {
        GUI.skin.window.fontSize = GUI.skin.label.fontSize = GUI.skin.box.fontSize = GUI.skin.button.fontSize = FONT_SIZE;

        if (!_showShopWindow) {
            if (GUI.Button(new Rect(Screen.width-BUTTON_WIDTH-OFFSET, OFFSET, BUTTON_WIDTH, BUTTON_HEIGHT), "Shop", GUI.skin.button)) {
                ShowShopWindow(true);
            }
        } else {
            GUI.Window(0, new Rect(Screen.width/2-WINDOW_WIDTH/2, Screen.height/2-WINDOW_HEIGHT/2, WINDOW_WIDTH, WINDOW_HEIGHT), DrawShopWindow, "Game Shop");
        }                    
    }
}

using UnityEngine;
using System.Collections;

public class SidePanel : MonoBehaviour {

    private const int BUTTON_WIDTH = 120;
    private const int BUTTON_HEIGHT = 80;
    private int _nButtons = 5;

    void OnGUI() {
        bool[] buttons = new bool[_nButtons];
        int startY = Screen.height/2-(BUTTON_HEIGHT*_nButtons)/2;
        for (int i = 0; i < _nButtons; ++i) {
            buttons[i] = GUI.Button(new Rect(0, startY+BUTTON_HEIGHT*i, BUTTON_WIDTH, BUTTON_HEIGHT), "Button_" + i);
        }
    }
}

package org.onepf.oms;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class PurchaseActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase);
    }

    public void onOkClick(View view) {
        Intent intent = getIntent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
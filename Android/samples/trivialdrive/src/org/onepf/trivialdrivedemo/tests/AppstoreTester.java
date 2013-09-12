package org.onepf.trivialdrivedemo.tests;

import android.content.Context;
import android.util.Log;
import org.onepf.oms.Appstore;

import java.util.Arrays;
import java.util.Iterator;

public class AppstoreTester implements AppstoreTest.OnTestFinishedListener {

    protected static final String TAG = "OpenIAB-TEST";

    AppstoreTest[] _tests;
    Iterator<AppstoreTest> _it;
    AppstoreTest _currentTest;
    Context _context;
    String _storeName;

    public void run(Context context, String storeName, AppstoreTest[] tests) {
        _context = context;
        _storeName = storeName;
        _tests = tests;
        if (tests == null || tests.length < 1) {
            Log.w(TAG, "Nothing to test");
            return;
        }
        Log.i(TAG, "--- " + _storeName + " TESTS ---");
        _it = Arrays.asList(_tests).iterator();
        runNextTest();
    }

    protected void runNextTest() {
        if (!_it.hasNext()) {
            Log.i(TAG, "Testing finished");
            return;
        }
        _currentTest = _it.next();
        _currentTest.run(_context, this);
    }

    @Override
    public void onTestFinished(AppstoreTest.Result result) {
        String message = _storeName + "." + _currentTest.getName() + " - ";
        if (result.passed) {
            Log.i(TAG, message + "PASSED. ");
        } else {
            Log.w(TAG, message + "FAILED. " + result.error);
        }
        runNextTest();
    }
}

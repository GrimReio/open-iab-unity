package org.onepf.trivialdrivedemo.tests;

import android.content.Context;

public abstract class AsyncAppstoreTest extends AppstoreTest {
    @Override
    public void run(Context context, OnTestFinishedListener testFinishedListener) {
        _testFinishedListener = testFinishedListener;
        runInternal(context);
    }
}

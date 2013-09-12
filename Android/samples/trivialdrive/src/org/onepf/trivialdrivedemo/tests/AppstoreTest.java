package org.onepf.trivialdrivedemo.tests;

import android.content.Context;

public abstract class AppstoreTest {

    protected OnTestFinishedListener _testFinishedListener;

    public class Result {
        public final boolean passed;
        public final String error;

        public Result(boolean success, String error) {
            this.passed = success;
            this.error = error;
        }

        public Result(boolean success) {
            this.passed = success;
            error = success ? "" : "Unknown Error";
        }
    }

    /**
     * Callback for test completion.
     */
    public interface OnTestFinishedListener {
        public void onTestFinished(Result result);
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public void run(Context context, OnTestFinishedListener testFinishedListener) {
        _testFinishedListener = testFinishedListener;
        runInternal(context);
        finish(true);
    }

    protected abstract void runInternal(Context context);

    protected void finish(boolean passed) {
        _testFinishedListener.onTestFinished(new Result(passed));
    }

    protected void assertTrue(boolean condition) {
        if (!condition)
            _testFinishedListener.onTestFinished(new Result(false));
    }

    protected void assertTrue(boolean condition, String error) {
        if (!condition)
            _testFinishedListener.onTestFinished(new Result(false, error));
    }

    protected void assertFalse(boolean condition) {
        assertTrue(!condition);
    }

    protected void assertFalse(boolean condition, String error) {
        assertTrue(!condition, error);
    }
}

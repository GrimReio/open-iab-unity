package org.onepf.oms;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import org.json.JSONException;
import org.onepf.oms.data.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BillingService extends Service {

    BillingBinder _binder;
    Database _database;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            _database = new Database(readJsonFromAssets("config.json"));
        } catch (JSONException e) {
            Log.e(AppstoreBinder.TAG, "Couldn't parse provided 'config.json'.", e);
            _database = new Database();
        }
        _binder = new BillingBinder(this, _database);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return _binder;
    }

    public String readJsonFromAssets(String fileName) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            String temp;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
        } catch (IOException e) {
            Log.e(AppstoreBinder.TAG, "Couldn't read 'config.json' from assets", e);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                Log.e(AppstoreBinder.TAG, "Couldn't close stream while reading 'config.json' from assets", e);
            }
        }
        return sb.toString();
    }
}

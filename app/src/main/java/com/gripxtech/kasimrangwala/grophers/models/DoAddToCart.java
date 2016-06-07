package com.gripxtech.kasimrangwala.grophers.models;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;

import com.gripxtech.kasimrangwala.grophers.MainActivity;
import com.gripxtech.kasimrangwala.grophers.R;
import com.gripxtech.kasimrangwala.grophers.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class DoAddToCart extends AsyncTask<String, Void, String> {

    public static final String TAG = DoAddToCart.class.getSimpleName();

    ProgressDialog dialog;
    MainActivity mActivity;
    View view;
    AppCompatTextView counter;
    Utils mUtils;

    public DoAddToCart(MainActivity activity, View view, AppCompatTextView counter) {
        this.mActivity = activity;
        this.view = view;
        this.counter = counter;
        mUtils = Utils.getInstance();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = mUtils.getProgressDialog(mActivity);
        dialog.setMessage("Adding Item to Cart...");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        List<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("uid", params[0]));
        pairs.add(new Pair<>("pid", params[1]));
        pairs.add(new Pair<>("qty", params[2]));
        try {
            return mUtils.getToServer(Utils.baseURL + "AddTocart.aspx", pairs);
        } catch (Exception e) {
            Log.e(TAG, "DoAddToCart::doInBackground(): " + e.getMessage());
            Snackbar.make(view,
                    mActivity.getString(R.string.cant_connect_to_server),
                    Snackbar.LENGTH_LONG)
                    .show();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // Log.e(TAG, "DoAddToCart::onPostExecute(): result is: " + result);
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (result != null && result.length() != 0) {
            if (result.equals("1")) {
                counter.post(new Runnable() {
                    @Override
                    public void run() {
                        counter.setText(mActivity.getString(R.string.one));
                    }
                });
                Snackbar.make(view, "Item Added Successfully.", Snackbar.LENGTH_SHORT)
                        .show();
            } else {
                Snackbar.make(view, "Something goes wrong while adding product to cart.",
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        }
    }
}

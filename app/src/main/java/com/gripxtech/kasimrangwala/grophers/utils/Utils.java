package com.gripxtech.kasimrangwala.grophers.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.gripxtech.kasimrangwala.grophers.R;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.List;

public class Utils {
    public static final String TAG = Utils.class.getSimpleName();
    public static final String baseURL = "http://apis.foooddies.com/api/";
    private static Utils ourInstance = new Utils();

    private Utils() {
    }

    public static Utils getInstance() {
        return ourInstance;
    }

    /**
     * @param context
     * @return boolean indicating whether network is connected or not <br /><br />
     * <p/>
     * <strong>Note:</strong><br/>
     * <p>This method requires android.permission.ACCESS_NETWORK_STATE permission </p>
     */
    public boolean isDeviceOnline(@NonNull Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public String getToServer(String url, List<Pair<String, String>> pairs) throws Exception {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();

        if (pairs != null) {
            int index = -1;
            for (Pair<String, String> pair : pairs) {
                index++;
                if (index == 0) {
                    url += "?";
                }
                url += pair.first + "=" + URLEncoder.encode(pair.second, "UTF-8");
                if (index != pairs.size() - 1) {
                    url += "&";
                }
            }
        }
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder().url(url);
        okhttp3.Request request = builder.build();
        okhttp3.Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException(response.message() + " " + response.toString());
        }
        return response.body().string();
    }

    /**
     * <strong>Uses:</strong><br/>
     * <p>
     * {@code
     * List<Pair<String, String>> pairs = new ArrayList<>();}
     * <br/>
     * {@code pairs.add(new Pair<>("key1", "value1"));}<br/>
     * {@code pairs.add(new Pair<>("key2", "value2"));}<br/>
     * {@code pairs.add(new Pair<>("key3", "value3"));}<br/>
     * <br/>
     * {@code Utils.postToServer("http://www.example.com/", pairs);}<br/>
     * </p>
     *
     * @param url
     * @param pairs List of support.V4 Pair
     * @return response from server in String format
     * @throws Exception
     */
    public String postToServer(String url, List<Pair<String, String>> pairs) throws Exception {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder().url(url);

        if (pairs != null) {
            okhttp3.FormBody.Builder postData = new okhttp3.FormBody.Builder();
            for (Pair<String, String> pair : pairs) {
                postData.add(pair.first, pair.second);
            }
            builder.post(postData.build());
        }
        okhttp3.Request request = builder.build();
        okhttp3.Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException(response.message() + " " + response.toString());
        }
        return response.body().string();
    }

    public void setWidgetTint(ViewGroup group, Context context) {
        for (int i = 0, count = group.getChildCount(); i < count; ++i) {
            View view = group.getChildAt(i);
            if (view instanceof EditText) {
                EditText editText = ((EditText) view);
                // Left drawable
                Drawable drawable = editText.getCompoundDrawables()[0];
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, ContextCompat.getColor(context, R.color.colorAccent));
                DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
                editText.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                // DrawableCompat.setTint(drawable, Color.parseColor("#FF000000"));
                // DrawableCompat.unwrap(drawable);
            }

            if (view instanceof ViewGroup && (((ViewGroup) view).getChildCount() > 0))
                setWidgetTint((ViewGroup) view, context);
        }
    }

    public String getCurrentDate() {
        Calendar now = Calendar.getInstance();
        int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
        int month = now.get(Calendar.MONTH) + 1;
        int year = now.get(Calendar.YEAR);
        return (dayOfMonth < 10 ? ("0" + dayOfMonth) : ("" + dayOfMonth)) + "/"
                + (month < 10 ? ("0" + month) : ("" + month)) + "/" + ("" + year);
    }

    public boolean isNetworkLocationEnabled(final Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isNetworkLocationEnabled(): " + e.getMessage());
        }
        return false;
    }

    public boolean isGPSLocationEnabled(final Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "isGPSLocationEnabled(): " + e.getMessage());
        }
        return false;
    }

    public ProgressDialog getProgressDialog(Context context) {
        ProgressDialog dialog;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog = new ProgressDialog(context, R.style.AppAlertDialogTheme);
        } else {
            dialog = new ProgressDialog(context);
        }
        return dialog;
    }

    public AlertDialog showMessage(Context context,
                                   String title,
                                   String message,
                                   String positiveButton,
                                   DialogInterface.OnClickListener positiveButtonListener) {
        return new AlertDialog.Builder(context, R.style.AppAlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, positiveButtonListener)
                .setCancelable(false)
                .show();
    }

    public AlertDialog showMessage(Context context,
                                   String title,
                                   String message,
                                   String positiveButton,
                                   DialogInterface.OnClickListener positiveButtonListener,
                                   String negativeButton,
                                   DialogInterface.OnClickListener negativeButtonListener) {
        return new AlertDialog.Builder(context, R.style.AppAlertDialogTheme)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton, positiveButtonListener)
                .setNegativeButton(negativeButton, negativeButtonListener)
                .setCancelable(false)
                .show();
    }

    public void rateApp(Activity activity) {
        try {
            activity.startActivity(
                    new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + activity.getPackageName())));
        } catch (ActivityNotFoundException e) {
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName())));
            } catch (ActivityNotFoundException e2) {
                e.printStackTrace();
                Log.e(TAG, "rateApp(): " + e.getMessage());
            }
        }
    }

    public void shareApp(Activity activity) {
        Intent share_que = new Intent(Intent.ACTION_SEND);
        share_que.setType("text/plain");
        share_que.putExtra(Intent.EXTRA_TEXT,
                "Hey friends,\nI just discover an amazing app called "
                        + activity.getResources().getString(R.string.app_name)
                        + ". Get it from https://play.google.com/store/apps/details?id=" + activity.getPackageName());
        activity.startActivityForResult(Intent.createChooser(share_que,
                "Share " + activity.getResources().getString(R.string.app_name) + " Using"), 123);
    }

    public void moreApps(Activity activity, String developerName) {
        activity.startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/developer?id=" + developerName)));
    }
}

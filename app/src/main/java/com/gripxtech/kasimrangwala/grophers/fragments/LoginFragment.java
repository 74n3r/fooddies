package com.gripxtech.kasimrangwala.grophers.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gripxtech.kasimrangwala.grophers.LoginActivity;
import com.gripxtech.kasimrangwala.grophers.MainActivity;
import com.gripxtech.kasimrangwala.grophers.R;
import com.gripxtech.kasimrangwala.grophers.utils.AppPrefs;
import com.gripxtech.kasimrangwala.grophers.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment implements TextWatcher {

    public static final String TAG = LoginFragment.class.getSimpleName();
    public static String TAG2;
    @BindView(R.id.clRootLogin)
    CoordinatorLayout mRootWidget;
    @BindView(R.id.etLoginMobileNo)
    AppCompatEditText mMobileNo;
    @BindView(R.id.etLoginPass)
    AppCompatEditText mPass;
    @BindView(R.id.bnLogin)
    AppCompatButton mLogin;
    LoginActivity mActivity;
    Handler mHandler;
    AppPrefs mPrefs;
    Utils mUtils;
    boolean isMobileNoOK = false;
    boolean isPassOK = false;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (LoginActivity) getActivity();
        mHandler = new Handler();
        mPrefs = new AppPrefs(mActivity);
        mUtils = Utils.getInstance();
        TAG2 = getTag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mUtils.setWidgetTint(mRootWidget, mActivity);
        validateWidgets();
    }

    public void validateWidgets() {
        mMobileNo.addTextChangedListener(this);
        mPass.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        View view = mActivity.getCurrentFocus();
        if (null != view) {
            switch (view.getId()) {
                case R.id.etLoginMobileNo:
                    validateMobileNo(s);
                    break;
                case R.id.etLoginPass:
                    validatePass(s);
                    break;
            }
        }
    }

    public void validateMobileNo(Editable s) {
        isMobileNoOK = s.length() >= 10;
        setBnLogin();
    }

    public void validatePass(Editable s) {
        isPassOK = !TextUtils.isEmpty(s);
        setBnLogin();
    }

    public void setBnLogin() {
        mLogin.setEnabled((isMobileNoOK && isPassOK));
    }

    @OnClick(R.id.bnLogin)
    public void onLogin() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mUtils.isDeviceOnline(mActivity)) {
                    new DoLogin().execute();
                } else {
                    Snackbar.make(mRootWidget, getString(R.string.device_offline), Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    class DoLogin extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        String mobileNo;
        String pass;

        public DoLogin() {
            super();
            dialog = mUtils.getProgressDialog(mActivity);
            mobileNo = mMobileNo.getText().toString();
            pass = mPass.getText().toString();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            List<Pair<String, String>> pairs = new ArrayList<>();
            pairs.add(new Pair<>(ServerData.Login.MobileNo, mobileNo));
            pairs.add(new Pair<>(ServerData.Login.Pass, pass));

            try {
                return mUtils.getToServer(ServerData.Login.URL, pairs);
            } catch (Exception e) {
                Log.e(TAG, "DoLogin::doInBackground(): " + e.getMessage());
                Snackbar.make(mRootWidget, getString(R.string.cant_connect_to_server), Snackbar.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (null != dialog && dialog.isShowing()) {
                dialog.dismiss();
            }

            if (result != null && result.length() != 0) {
                Log.e(TAG + " result", result);
                if (!result.equals("0")) {
                    mPrefs.setLoggedIn(true);
                    mPrefs.setMobileNo(mMobileNo.getText().toString());
                    mPrefs.setUserID(result);
                    startActivity(new Intent(mActivity, MainActivity.class));
                    mActivity.finish();
                } else {
                    mUtils.showMessage(
                            mActivity,
                            "Error",
                            "Login Fail.\nTry Again",
                            "OK", null
                    );
                }
            } else {
                Log.e(TAG, "DoLogin::onPostExecute(): unexpected server result");
            }
        }
    }

    class ServerData {
        public class Login {
            public static final String URL = Utils.baseURL + "login.aspx";
            public static final String MobileNo = "mo";
            public static final String Pass = "pass";
        }
    }
}

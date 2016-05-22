package com.gripxtech.kasimrangwala.grophers.fragments;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.gripxtech.kasimrangwala.grophers.LoginActivity;
import com.gripxtech.kasimrangwala.grophers.R;
import com.gripxtech.kasimrangwala.grophers.utils.AppPrefs;
import com.gripxtech.kasimrangwala.grophers.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistrationFragment extends Fragment
        implements TextWatcher {

    public static final String TAG = RegistrationFragment.class.getSimpleName();
    public static String TAG2;
    @BindView(R.id.clRootRegistration)
    CoordinatorLayout mRootWidget;
    @BindView(R.id.etRegisterFirstName)
    AppCompatEditText mFirstName;
    @BindView(R.id.etRegisterLastName)
    AppCompatEditText mLastName;
    @BindView(R.id.etRegisterEmail)
    AppCompatEditText mEmail;
    @BindView(R.id.etRegisterMobileNo)
    AppCompatEditText mMobileNo;
    @BindView(R.id.etRegisterPass)
    AppCompatEditText mPass;
    @BindView(R.id.etRegisterConfirmPass)
    AppCompatEditText mConfirmPass;
    @BindView(R.id.etRegisterCountry)
    AppCompatAutoCompleteTextView mCountry;
    @BindView(R.id.etRegisterState)
    AppCompatAutoCompleteTextView mState;
    @BindView(R.id.etRegisterCity)
    AppCompatAutoCompleteTextView mCity;
    @BindView(R.id.bnRegister)
    AppCompatButton mRegister;
    LoginActivity mActivity;
    Handler mHandler;
    AppPrefs mPrefs;
    Utils mUtils;
    List<String> mCountryIDs;
    List<String> mCountries;
    List<String> mStateIDs;
    List<String> mStates;
    List<String> mCityIDs;
    List<String> mCities;
    boolean isFirstNameOK = false;
    boolean isLastNameOK = false;
    boolean isEmailOK = false;
    boolean isMobileNoOK = false;
    boolean isPassOK = false;
    boolean isConfirmPassOK = false;

    public RegistrationFragment() {
        // Required empty public constructor
    }

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (LoginActivity) getActivity();
        mHandler = new Handler();
        mPrefs = new AppPrefs(mActivity);
        mUtils = Utils.getInstance();
        TAG2 = getTag();

        mCountryIDs = new ArrayList<>();
        mCountries = new ArrayList<>();
        mStateIDs = new ArrayList<>();
        mStates = new ArrayList<>();
        mCityIDs = new ArrayList<>();
        mCities = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mUtils.setWidgetTint(mRootWidget, mActivity);
        setupWidgets();
    }

    public void setupWidgets() {
        mCountry.setInputType(InputType.TYPE_NULL);
        mState.setInputType(InputType.TYPE_NULL);
        mCity.setInputType(InputType.TYPE_NULL);
        validateWidgets();

        if (mUtils.isDeviceOnline(mActivity)) {
            new GetCountry().execute();
        } else {
            Snackbar.make(mRootWidget,
                    getString(R.string.device_offline),
                    Snackbar.LENGTH_LONG)
                    .show();
        }

        mCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mState.getText().clear();
                mStateIDs.clear();
                mStates.clear();
                mCity.getText().clear();
                mCityIDs.clear();
                mCities.clear();
                setBnRegister();
                new GetState().execute();
            }
        });

        mState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCity.getText().clear();
                mCityIDs.clear();
                mCities.clear();
                setBnRegister();
                new GetCity().execute();
            }
        });

        mCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setBnRegister();
            }
        });
    }

    public void validateWidgets() {
        mFirstName.addTextChangedListener(this);
        mLastName.addTextChangedListener(this);
        mEmail.addTextChangedListener(this);
        mMobileNo.addTextChangedListener(this);
        mPass.addTextChangedListener(this);
        mConfirmPass.addTextChangedListener(this);
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
        if (view != null) {
            switch (view.getId()) {
                case R.id.etRegisterFirstName:
                    validateFirstName(s);
                    break;
                case R.id.etRegisterLastName:
                    validateLastName(s);
                    break;
                case R.id.etRegisterEmail:
                    validateEmail(s);
                    break;
                case R.id.etRegisterMobileNo:
                    validateMobileNo(s);
                    break;
                case R.id.etRegisterPass:
                    validatePass(s);
                    break;
                case R.id.etRegisterConfirmPass:
                    validateConfirmPass(s);
                    break;
            }
        }
    }

    public void validateFirstName(Editable s) {
        isFirstNameOK = !TextUtils.isEmpty(s);
        // setError(isFirstNameOK, mFirstName, "please, Enter First Name Properly.");
        setBnRegister();
    }

    public void validateLastName(Editable s) {
        isLastNameOK = !TextUtils.isEmpty(s);
        // setError(isLastNameOK, mLastName, "please, Enter Last Name Properly.");
        setBnRegister();
    }

    public void validateEmail(Editable s) {
        isEmailOK = !TextUtils.isEmpty(s);
        // setError(isEmailOK, mEmail, "please, Enter Email Address Properly.");
        setBnRegister();
    }

    public void validateMobileNo(Editable s) {
        isMobileNoOK = s.length() >= 10;
        // setError(isMobileNoOK, mMobileNo, "please, Enter Mobile No. Properly.");
        setBnRegister();
    }

    public void validatePass(Editable s) {
        isPassOK = !TextUtils.isEmpty(s);
        setError(isPassOK, mPass, "please, Enter Password Properly.");
        setBnRegister();
    }

    public void validateConfirmPass(Editable s) {
        isConfirmPassOK = mPass.getText().toString().equals(s.toString());
        setError(isConfirmPassOK, mConfirmPass, "Password & Confirm Password should be same.");
        setBnRegister();
    }

    public void setBnRegister() {
        boolean isCountryOK = !TextUtils.isEmpty(mCountry.getText());
        boolean isStateOK = !TextUtils.isEmpty(mState.getText());
        boolean isCityOK = !TextUtils.isEmpty(mCity.getText());

        mRegister.setEnabled((isFirstNameOK
                && isLastNameOK
                && isEmailOK
                && isMobileNoOK
                && isPassOK
                && isConfirmPassOK
                && isCountryOK
                && isStateOK
                && isCityOK
        ));

//        Log.e(TAG, "setBnRegister(): "
//                + isFirstNameOK + " "
//                + isLastNameOK + " "
//                + isEmailOK + " "
//                + isMobileNoOK + " "
//                + isPassOK + " "
//                + isConfirmPassOK + " "
//                + isCountryOK + " "
//                + isStateOK + " "
//                + isCityOK + " "
//        );
    }

    @OnClick(R.id.etRegisterCountry)
    public void onCountry() {
        mCountry.showDropDown();
    }

    @OnClick(R.id.etRegisterState)
    public void onState() {
        mState.showDropDown();
    }

    @OnClick(R.id.etRegisterCity)
    public void onCity() {
        mCity.showDropDown();
    }

    @OnClick(R.id.bnRegister)
    public void onRegister() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mUtils.isDeviceOnline(mActivity)) {
                    new DoRegister(
                            mFirstName.getText().toString(),
                            mLastName.getText().toString(),
                            mEmail.getText().toString(),
                            mMobileNo.getText().toString(),
                            mPass.getText().toString(),
                            mCountry.getText().toString(),
                            mState.getText().toString(),
                            mCity.getText().toString()
                    ).execute();
                } else {
                    Snackbar.make(mRootWidget,
                            getString(R.string.device_offline),
                            Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    public void setError(boolean isOK, final AppCompatEditText editText, final String message) {
        if (!isOK) {
            editText.post(new Runnable() {
                @Override
                public void run() {
                    editText.setError(message);
                }
            });
        } else {
            editText.post(new Runnable() {
                @Override
                public void run() {
                    editText.setError(null);
                }
            });
        }
    }

    public void switchToLogin() {
        mActivity.selectTab(0);
        Fragment fragment = mActivity.
                getSupportFragmentManager()
                .findFragmentByTag(LoginFragment.TAG2);
        if (fragment != null) {
            LoginFragment loginFragment = (LoginFragment) fragment;
            loginFragment.mMobileNo.setText(
                    mMobileNo.getText().toString()
            );
            loginFragment.mPass.setText(
                    mPass.getText().toString()
            );
            loginFragment.validateMobileNo(mMobileNo.getText());
            loginFragment.validatePass(mPass.getText());
        } else {
            Log.e(TAG, "switchToLogin(): fragment is " + fragment);
        }
    }

    class GetCity extends AsyncTask<Void, Void, String> {

        ProgressDialog dialog;
        String stateID;

        GetCity() {
            super();
            dialog = new ProgressDialog(mActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching Cities...");
            dialog.setCancelable(false);
            dialog.show();
            stateID = mStateIDs.get(mStates.indexOf(mState.getText().toString()));
        }

        @Override
        protected String doInBackground(Void... params) {
            List<Pair<String, String>> pairs = new ArrayList<>();
            pairs.add(new Pair<>(ServerData.City.StateID, stateID));
            try {
                return mUtils.getToServer(ServerData.City.URL, pairs);
            } catch (Exception e) {
                Log.e(TAG, "GetCity::doInBackground(): " + e.getMessage());
                Snackbar.make(mRootWidget,
                        getString(R.string.cant_connect_to_server),
                        Snackbar.LENGTH_LONG)
                        .show();
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
                Log.e(TAG, "GetCity::onPostExecute(): result is: " + result);
                try {
                    JSONArray cityList = new JSONObject(result)
                            .getJSONArray(ServerData.City.CityList);
                    if (null != cityList && cityList.length() > 0) {
                        for (int i = 0; i < cityList.length(); i++) {
                            JSONObject cityObject = cityList.getJSONObject(i);
                            if (cityObject != null) {
                                mCityIDs.add(cityObject.getString(ServerData.City.CityID));
                                mCities.add(cityObject.getString(ServerData.City.City));
                            } else {
                                Log.e(TAG, "GetCity::onPostExecute():" +
                                        " cityObject is null at index " + i);
                            }
                        }
                        mCity.setAdapter(new ArrayAdapter<>(
                                mActivity,
                                R.layout.support_simple_spinner_dropdown_item,
                                mCities
                        ));
                    } else {
                        Log.e(TAG, "GetCity::onPostExecute():" +
                                " cityList is null or empty");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "GetCity::onPostExecute(): " + e.getMessage());
                }
            }
        }
    }

    class GetState extends AsyncTask<Void, Void, String> {

        ProgressDialog dialog;
        String countryID;

        GetState() {
            super();
            dialog = new ProgressDialog(mActivity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Fetching States...");
            dialog.setCancelable(false);
            dialog.show();
            countryID = mCountryIDs.get(mCountries.indexOf(mCountry.getText().toString()));
        }

        @Override
        protected String doInBackground(Void... params) {
            List<Pair<String, String>> pairs = new ArrayList<>();
            pairs.add(new Pair<>(ServerData.State.CountryID, countryID));
            try {
                return mUtils.getToServer(ServerData.State.URL, pairs);
            } catch (Exception e) {
                Log.e(TAG, "GetState::doInBackground(): " + e.getMessage());
                Snackbar.make(mRootWidget,
                        getString(R.string.cant_connect_to_server),
                        Snackbar.LENGTH_LONG)
                        .show();
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
                Log.e(TAG, "GetState::onPostExecute(): result is: " + result);
                try {
                    JSONArray stateList = new JSONObject(result)
                            .getJSONArray(ServerData.State.StateList);
                    if (null != stateList && stateList.length() > 0) {
                        for (int i = 0; i < stateList.length(); i++) {
                            JSONObject stateObject = stateList.getJSONObject(i);
                            if (stateObject != null) {
                                mStateIDs.add(stateObject.getString(ServerData.State.StateID));
                                mStates.add(stateObject.getString(ServerData.State.State));
                            } else {
                                Log.e(TAG, "GetState::onPostExecute():" +
                                        " stateObject is null at index " + i);
                            }
                        }
                        mState.setAdapter(new ArrayAdapter<>(
                                mActivity,
                                R.layout.support_simple_spinner_dropdown_item,
                                mStates
                        ));
                    } else {
                        Log.e(TAG, "GetState::onPostExecute():" +
                                " stateList is null or empty");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "GetState::onPostExecute(): " + e.getMessage());
                }
            }
        }
    }

    class GetCountry extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                return mUtils.postToServer(ServerData.Country.URL, null);
            } catch (Exception e) {
                Log.e(TAG, "GetCountry::doInBackground(): " + e.getMessage());
                Snackbar.make(mRootWidget,
                        getString(R.string.cant_connect_to_server),
                        Snackbar.LENGTH_LONG)
                        .show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && result.length() != 0) {
                Log.e(TAG, "GetCountry::onPostExecute(): result is: " + result);
                try {
                    JSONArray countryList = new JSONObject(result)
                            .getJSONArray(ServerData.Country.CountryList);
                    if (null != countryList && countryList.length() > 0) {
                        for (int i = 0; i < countryList.length(); i++) {
                            JSONObject countryObject = countryList.getJSONObject(i);
                            if (countryObject != null) {
                                mCountryIDs.add(countryObject.getString(ServerData.Country.CountryID));
                                mCountries.add(countryObject.getString(ServerData.Country.Country));
                            } else {
                                Log.e(TAG, "GetCountry::onPostExecute():" +
                                        " countryObject is null at index " + i);
                            }
                        }
                        mCountry.setAdapter(new ArrayAdapter<>(
                                mActivity,
                                R.layout.support_simple_spinner_dropdown_item,
                                mCountries
                        ));
                    } else {
                        Log.e(TAG, "GetCountry::onPostExecute():" +
                                " countryList is null or empty");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "GetCountry::onPostExecute(): " + e.getMessage());
                }
            }
        }
    }

    class DoRegister extends AsyncTask<String, Void, String> {

        ProgressDialog dialog;
        String firstName;
        String lastName;
        String email;
        String mobileNo;
        String pass;
        String country;
        String state;
        String city;

        public DoRegister(String firstName, String lastName,
                          String email, String mobileNo, String pass,
                          String country, String state, String city) {
            super();
            dialog = new ProgressDialog(mActivity);
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.mobileNo = mobileNo;
            this.pass = pass;
            this.country = country;
            this.state = state;
            this.city = city;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Registering...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            List<Pair<String, String>> pairs = new ArrayList<>();
            pairs.add(new Pair<>(ServerData.Register.FirstName, firstName));
            pairs.add(new Pair<>(ServerData.Register.LastName, lastName));
            pairs.add(new Pair<>(ServerData.Register.Email, email));
            pairs.add(new Pair<>(ServerData.Register.MobileNo, mobileNo));
            pairs.add(new Pair<>(ServerData.Register.Pass, pass));
            pairs.add(new Pair<>(ServerData.Register.Country, country));
            pairs.add(new Pair<>(ServerData.Register.State, state));
            pairs.add(new Pair<>(ServerData.Register.City, city));

            try {
                return mUtils.postToServer(ServerData.Register.URL, pairs);
            } catch (Exception e) {
                Log.e(TAG, "DoRegister::doInBackground(): " + e.getMessage());
                Snackbar.make(mRootWidget,
                        getString(R.string.cant_connect_to_server),
                        Snackbar.LENGTH_LONG)
                        .show();
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
                Log.e(TAG, "DoRegister::onPostExecute(): result is: " + result);
                if (result.equals("1")) {
                    mUtils.showMessage(
                            mActivity,
                            "Success",
                            "Registration Successful.",
                            "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switchToLogin();
                                }
                            }
                    );
                } else {
                    mUtils.showMessage(
                            mActivity,
                            "Error",
                            "Registration Fail.\nTry Login",
                            "OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switchToLogin();
                                }
                            }
                    );
                }
            } else {
                Log.e(TAG, "DoRegister::onPostExecute(): unexpected server result");
            }
        }
    }

    class ServerData {
        public class City {
            public static final String URL = Utils.baseURL + "getcity.aspx";
            public static final String StateID = "cityid";
            public static final String CityList = "city";
            public static final String CityID = "CityID";
            public static final String City = "City";
        }

        public class State {
            public static final String URL = Utils.baseURL + "getstate.aspx";
            public static final String CountryID = "cid";
            public static final String StateList = "state";
            public static final String StateID = "StateID";
            public static final String State = "State";
        }

        public class Country {
            public static final String URL = Utils.baseURL + "getcountry.aspx";
            public static final String CountryList = "country";
            public static final String CountryID = "CountryID";
            public static final String Country = "Country";
        }

        public class Register {
            public static final String URL = Utils.baseURL + "registration.aspx";
            public static final String FirstName = "name";
            public static final String LastName = "lname";
            public static final String Email = "email";
            public static final String MobileNo = "mo";
            public static final String Pass = "password";
            public static final String Country = "countryid";
            public static final String State = "stateid";
            public static final String City = "cityid";
            public static final String ResultCode = "1";
        }
    }
}

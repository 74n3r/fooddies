package com.gripxtech.kasimrangwala.grophers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.gripxtech.kasimrangwala.grophers.fragments.CartFragment;
import com.gripxtech.kasimrangwala.grophers.fragments.HomeFragment;
import com.gripxtech.kasimrangwala.grophers.fragments.SearchFragment;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    private Handler mHandler;
    private Prefs mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mHandler = new Handler();
        mPrefs = new Prefs(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setUpNavDrawer();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContentMain, SearchFragment.newInstance(), SearchFragment.TAG)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        if (id == R.id.action_view_cart) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContentMain, CartFragment.newInstance(), CartFragment.TAG)
                    .addToBackStack(null)
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setUpNavDrawer() {
        Menu menu = mNavigationView.getMenu();
        menu.getItem(mPrefs.getSelectedNavDrawerPosition()).setChecked(true);
        loadFragment(mPrefs.getSelectedNavDrawerPosition());
        setUpNavView();
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

//    public NavigationView getNavigationView() {
//        return mNavigationView;
//    }

    public void setUpNavView() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                int selectedNavDrawerPosition = mPrefs.getSelectedNavDrawerPosition();
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        if (selectedNavDrawerPosition != 0) {
                            loadFragment(0);
                        }
                        break;
                    case R.id.action_edit_location:
                        break;
                    case R.id.action_addresses:
                        break;
                    case R.id.action_orders:
                        break;
                    case R.id.action_cart:
                        break;
                    case R.id.action_help_support:
                        break;
                    case R.id.action_rate:
                        break;
                    case R.id.action_share:
                        break;
                    case R.id.action_logout:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    public void loadFragment(int currentSelectedPosition) {
        mPrefs.setSelectedNavDrawerPosition(currentSelectedPosition);
        clearBackStack();
        if (mPrefs.getSelectedNavDrawerPosition() == 0) {
            setTitle(R.string.app_name);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContentMain, HomeFragment.newInstance(), HomeFragment.TAG)
                    .commit();
        }
    }

    public void clearBackStack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            fragmentManager.popBackStackImmediate();
        }
    }

    class Prefs {
        private static final String PrefsName = "navPrefs";
        private static final String SelectedNavDrawerPosition = "selectedNavDrawerPosition";

        private SharedPreferences mPrefs;

        public Prefs(Context context) {
            super();
            mPrefs = context.getSharedPreferences(PrefsName, Context.MODE_PRIVATE);
        }

        public int getSelectedNavDrawerPosition() {
            return mPrefs.getInt(SelectedNavDrawerPosition, 0);
        }

        public void setSelectedNavDrawerPosition(int selectedNavDrawerPosition) {
            mPrefs.edit().putInt(SelectedNavDrawerPosition, selectedNavDrawerPosition).apply();
        }

        @Override
        public String toString() {
            return Arrays.toString(
                    new String[]{String.valueOf(getSelectedNavDrawerPosition())});
        }
    }
}

package com.gripxtech.kasimrangwala.grophers.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gripxtech.kasimrangwala.grophers.MainActivity;
import com.gripxtech.kasimrangwala.grophers.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategoryFragment extends Fragment {

    public static final String TAG = CategoryFragment.class.getSimpleName();
    private static final String ARG_HEADER = "header";
    private static final String ARG_NAME = "name";

    @BindView(R.id.clRootCategory)
    CoordinatorLayout mRootWidget;

    @BindView(R.id.tbToolbar)
    Toolbar mToolbar;

    @BindView(R.id.ivCategoryHeader)
    AppCompatImageView mHeaderImg;

    @BindView(R.id.tlCategory)
    TabLayout mTabLayout;

    @BindView(R.id.vpCategoryTabs)
    ViewPager mViewPager;

    private ViewPagerAdapter mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private MainActivity mActivity;
    private Prefs mPrefs;

    private int mHeader;
    private String mName;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance(int header, String name) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_HEADER, header);
        args.putString(ARG_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHeader = getArguments().getInt(ARG_HEADER);
            mName = getArguments().getString(ARG_NAME);
        }
        mActivity = (MainActivity) getActivity();
        mPrefs = new Prefs(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setUpViews();
        setUpTabs();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void setUpViews() {
        mActivity.setSupportActionBar(mToolbar);
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(mActivity, mActivity.getDrawerLayout(), mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mActivity.getDrawerLayout().setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mActivity.setTitle(mName);
        mHeaderImg.setImageResource(mHeader);
    }

    public void setUpTabs() {
        List<ViewPagerItem> pagerItems = new ArrayList<>();
        pagerItems.add(new ViewPagerItem(new VegetableFragment(), "Mangoes"));
        pagerItems.add(new ViewPagerItem(new VegetableFragment(), "Fruits"));
        pagerItems.add(new ViewPagerItem(new VegetableFragment(), "Vegetables"));
        mAdapter = new ViewPagerAdapter(getChildFragmentManager(), pagerItems);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                mPrefs.setTabPosition(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        mTabLayout.getTabAt(mPrefs.getTabPosition()).select();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<ViewPagerItem> pagerItems;

        public ViewPagerAdapter(FragmentManager fm, List<ViewPagerItem> pagerItems) {
            super(fm);
            this.pagerItems = pagerItems;
        }

        @Override
        public Fragment getItem(int position) {
            return pagerItems.get(position).getFragment();
        }

        @Override
        public int getCount() {
            return pagerItems.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pagerItems.get(position).getPageTitle();
        }
    }

    class ViewPagerItem {
        private Fragment fragment;
        private String pageTitle;

        public ViewPagerItem(Fragment fragment, String pageTitle) {
            super();
            this.fragment = fragment;
            this.pageTitle = pageTitle;
        }

        public Fragment getFragment() {
            return fragment;
        }

        public String getPageTitle() {
            return pageTitle;
        }
    }

    class Prefs {
        private static final String PrefsName = "CategoryPrefs";
        private static final String TabPosition = "tabPosition";

        private SharedPreferences mPrefs;

        public Prefs(Context context) {
            super();
            mPrefs = context.getSharedPreferences(PrefsName, Context.MODE_PRIVATE);
        }

        public int getTabPosition() {
            return mPrefs.getInt(TabPosition, 0);
        }

        public void setTabPosition(int tabPosition) {
            mPrefs.edit().putInt(TabPosition, tabPosition).apply();
        }

        @Override
        public String toString() {
            return Arrays.toString(
                    new String[]{String.valueOf(getTabPosition())});
        }
    }
}

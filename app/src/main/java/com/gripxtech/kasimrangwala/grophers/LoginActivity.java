package com.gripxtech.kasimrangwala.grophers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.gripxtech.kasimrangwala.grophers.fragments.LoginFragment;
import com.gripxtech.kasimrangwala.grophers.fragments.RegistrationFragment;
import com.gripxtech.kasimrangwala.grophers.utils.AppPrefs;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import za.co.riggaroo.materialhelptutorial.TutorialItem;
import za.co.riggaroo.materialhelptutorial.tutorial.MaterialTutorialActivity;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE = 1234;
    @BindView(R.id.tbToolbar)
    Toolbar mToolbar;
    @BindView(R.id.tlLoginTabs)
    TabLayout mTabLayout;
    @BindView(R.id.vpLoginTabs)
    ViewPager mViewPager;
    ViewPagerAdapter mAdapter;
    Handler mHandler;
    AppPrefs mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mHandler = new Handler();
        mPrefs = new AppPrefs(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (!mPrefs.isLoggedIn()) {
            if (mPrefs.isFirstLaunch()) {
                loadIntro();
                mPrefs.setFirstLaunch(false);
            }
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        setUpViews();
        setUpTabs();
    }

    public void loadIntro() {
        Intent mainAct = new Intent(this, MaterialTutorialActivity.class);
        mainAct.putParcelableArrayListExtra(MaterialTutorialActivity.MATERIAL_TUTORIAL_ARG_TUTORIAL_ITEMS, getTutorialItems(this));
        startActivityForResult(mainAct, REQUEST_CODE);

    }

    private ArrayList<TutorialItem> getTutorialItems(Context context) {
        TutorialItem tutorialItem1 = new TutorialItem(R.string.slide_1_african_story_books, R.string.slide_1_african_story_books,
                R.color.slide_1, R.drawable.tut_page_1_front, R.drawable.tut_page_1_background);

        TutorialItem tutorialItem2 = new TutorialItem(R.string.slide_2_volunteer_professionals, R.string.slide_2_volunteer_professionals_subtitle,
                R.color.slide_2, R.drawable.tut_page_2_front, R.drawable.tut_page_2_background);

        TutorialItem tutorialItem3 = new TutorialItem(context.getString(R.string.slide_3_download_and_go), null,
                R.color.slide_3, R.drawable.tut_page_3_foreground);

        TutorialItem tutorialItem4 = new TutorialItem(R.string.slide_4_different_languages, R.string.slide_4_different_languages_subtitle,
                R.color.slide_4, R.drawable.tut_page_4_foreground, R.drawable.tut_page_4_background);

        ArrayList<TutorialItem> tutorialItems = new ArrayList<>();
        tutorialItems.add(tutorialItem1);
        tutorialItems.add(tutorialItem2);
        tutorialItems.add(tutorialItem3);
        tutorialItems.add(tutorialItem4);

        return tutorialItems;
    }

    public void setUpViews() {
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.title_login_activity));
    }

    public void selectTab(final int tabIndex) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mAdapter.getCount() > tabIndex) {
                    TabLayout.Tab tab = mTabLayout.getTabAt(tabIndex);
                    if (tab != null) {
                        tab.select();
                    } else {
                        Log.e(TAG, "selectTab(): tab is null at tabIndex " + tabIndex);
                    }
                } else {
                    Log.e(TAG, "selectTab(): tabIndex "
                            + tabIndex + " is greater than size "
                            + mAdapter.getCount()
                    );
                }
            }
        });
    }

    public void setUpTabs() {
        List<ViewPagerItem> pagerItems = new ArrayList<>();
        pagerItems.add(new ViewPagerItem(
                LoginFragment.newInstance(),
                getString(R.string.title_login_fragment)
        ));
        pagerItems.add(new ViewPagerItem(
                RegistrationFragment.newInstance(),
                getString(R.string.title_register_fragment)
        ));
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), pagerItems);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        List<ViewPagerItem> pagerItems;

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
        Fragment fragment;
        String pageTitle;

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
}

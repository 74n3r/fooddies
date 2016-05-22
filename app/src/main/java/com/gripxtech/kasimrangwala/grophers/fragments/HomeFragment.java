package com.gripxtech.kasimrangwala.grophers.fragments;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gripxtech.kasimrangwala.grophers.MainActivity;
import com.gripxtech.kasimrangwala.grophers.R;
import com.gripxtech.kasimrangwala.grophers.utils.Utils;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeFragment extends Fragment {

    public static final String TAG = HomeFragment.class.getSimpleName();

    @BindView(R.id.clRootHome)
    CoordinatorLayout mRootWidget;

    @BindView(R.id.tbToolbar)
    Toolbar mToolbar;

    @BindView(R.id.rvHome)
    SuperRecyclerView mCategoryList;
    List<String> mSliderImageRes;
    private List<Object> objects;
    // private OnMoreListener moreListener;
    private HomeAdapter mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private MainActivity mActivity;
    private Handler mHandler;
    private Utils mUtils;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mHandler = new Handler();
        mUtils = Utils.getInstance();

        mSliderImageRes = new ArrayList<>();

        objects = new ArrayList<>();
        mAdapter = new HomeAdapter(objects);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setUpViews();
        setupCategoryList();
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
    }

    public void setupCategoryList() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCategoryList.setLayoutManager(linearLayoutManager);
        new GetPickLocation().execute();
    }

    public void baseGetCategory(final boolean isMoreAsk) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                new GetCategoryList(isMoreAsk).execute();
            }
        });
    }

    class ItemViewTypes {
        public static final int PickLocation = 0;
        public static final int Category = 1;
        public static final int TopMenu = 2;
        public static final int Slider = 3;
    }

    class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Object> objects;

        public HomeAdapter(List<Object> objects) {
            this.objects = objects;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder = null;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            switch (viewType) {
                case ItemViewTypes.PickLocation:
                    viewHolder = new PickLocationViewHolder(
                            inflater.inflate(R.layout.item_view_pick_location, parent, false)
                    );
                    break;
                case ItemViewTypes.Category:
                    viewHolder = new CategoryViewHolder(
                            inflater.inflate(R.layout.item_view_home, parent, false)
                    );
                    break;
                case ItemViewTypes.TopMenu:
                    viewHolder = new TopMenuViewHolder(
                            inflater.inflate(R.layout.item_view_top_menu, parent, false)
                    );
                    break;
                case ItemViewTypes.Slider:
                    viewHolder = new SliderViewHolder(
                            inflater.inflate(R.layout.item_view_slider, parent, false)
                    );
                    break;
                default:
                    break;
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == ItemViewTypes.PickLocation) {
                PickLocationItem item = (PickLocationItem) objects.get(position);
                PickLocationViewHolder viewHolder = (PickLocationViewHolder) holder;
                viewHolder.getmAddress().setText(item.getLocation());
            } else if (holder.getItemViewType() == ItemViewTypes.TopMenu) {
                TopMenuItems item = (TopMenuItems) objects.get(position);
                TopMenuViewHolder viewHolder = (TopMenuViewHolder) holder;
                for (int i = 0; i < TopMenuItems.LIMIT; i++) {
                    viewHolder.getTopMenuImages().get(i).setImageResource(item.getResDrawable(i));
                    viewHolder.getTopMenuTexts().get(i).setText(item.getDescription(i));
                }
                // } else if (holder.getItemViewType() == ItemViewTypes.Slider) {
                // SliderItems items = (SliderItems) objects.get(position);
                // SliderViewHolder viewHolder = (SliderViewHolder) holder;
            } else if (holder.getItemViewType() == ItemViewTypes.Category) {
                CategoryItem item = (CategoryItem) objects.get(position);
                CategoryViewHolder viewHolder = (CategoryViewHolder) holder;
                viewHolder.getHeader().setImageResource(item.getHeader());
                viewHolder.getLogo().setImageResource(item.getLogo());
                viewHolder.getName().setText(item.getName());
                viewHolder.getDeliveryTime().setText(item.getDeliveryTime());
            }
        }

        @Override
        public int getItemCount() {
            return objects.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (objects.get(position) instanceof PickLocationItem) {
                return ItemViewTypes.PickLocation;
            } else if (objects.get(position) instanceof CategoryItem) {
                return ItemViewTypes.Category;
            } else if (objects.get(position) instanceof TopMenuItems) {
                return ItemViewTypes.TopMenu;
            } else if (objects.get(position) instanceof SliderItems) {
                return ItemViewTypes.Slider;
            }
            return -1;
        }

        public List<Object> getObjects() {
            return objects;
        }
    }

    class PickLocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvPickLocAddress)
        AppCompatTextView mAddress;

        public PickLocationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }

        public AppCompatTextView getmAddress() {
            return mAddress;
        }
    }

    class PickLocationItem {
        private String location;

        public PickLocationItem(String location) {
            this.location = location;
        }

        public String getLocation() {
            return location;
        }
    }

    class TopMenuViewHolder extends RecyclerView.ViewHolder {

        @BindViews({R.id.ivTopMenu1, R.id.ivTopMenu2, R.id.ivTopMenu3, R.id.ivTopMenu4, R.id.ivTopMenu5, R.id.ivTopMenu6})
        List<ImageView> mTopMenuImages;

        @BindViews({R.id.tvTopMenu1, R.id.tvTopMenu2, R.id.tvTopMenu3, R.id.tvTopMenu4, R.id.tvTopMenu5, R.id.tvTopMenu6})
        List<TextView> mTopMenuTexts;

        @BindViews({R.id.cvTopMenu4, R.id.cvTopMenu5, R.id.cvTopMenu6})
        List<CardView> mTopMenuItems;

        @BindView(R.id.glRootTopMenu)
        GridLayout mRoot;

        boolean isTopItemHidden = true;

        public TopMenuViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.ivTopMenuToggle)
        public void onToggle() {
            for (CardView view : mTopMenuItems) {
                view.setVisibility(isTopItemHidden ? View.VISIBLE : View.GONE);
            }
            isTopItemHidden = !isTopItemHidden;
        }

        @OnClick({R.id.cvTopMenu1, R.id.cvTopMenu2, R.id.cvTopMenu3, R.id.cvTopMenu4, R.id.cvTopMenu5, R.id.cvTopMenu6})
        public void onTopMenu(View view) {

        }

        public List<ImageView> getTopMenuImages() {
            return mTopMenuImages;
        }

        public List<TextView> getTopMenuTexts() {
            return mTopMenuTexts;
        }
    }

    class TopMenuItems {
        public static final int LIMIT = 6;
        private int[] resDrawable;
        private String[] description;

        public TopMenuItems(int[] resDrawable, String[] description) {
            this.resDrawable = resDrawable;
            this.description = description;
        }

        public int getResDrawable(int location) {
            return resDrawable[location];
        }

        public String getDescription(int location) {
            return description[location];
        }
    }

    class SliderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vpRootSlider1)
        ViewPager mPager;

        public SliderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mPager.setAdapter(new SliderAdapter());
        }
    }

    class SliderItems {

    }

    class SliderAdapter extends PagerAdapter {

        LayoutInflater inflater;

        @BindView(R.id.ivSlideImage)
        ImageView slider;

        public SliderAdapter() {
            inflater = mActivity.getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mSliderImageRes.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = inflater.inflate(
                    R.layout.item_view_item_view_slider,
                    container,
                    false
            );

            ButterKnife.bind(this, view);
            // slider.setImageResource(imageRes.get(position));
            Picasso.with(mActivity)
                    .load(mSliderImageRes.get(position))
                    .into(slider);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ivHomeHeader)
        AppCompatImageView mHeader;

        @BindView(R.id.ivHomeLogo)
        AppCompatImageView mLogo;

        @BindView(R.id.tvHomeName)
        AppCompatTextView mName;

        @BindView(R.id.tvHomeDeliveryTime)
        AppCompatTextView mDeliveryTime;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CategoryItem item = (CategoryItem) mAdapter.getObjects().get(getAdapterPosition());
            mActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContentMain, CategoryFragment.newInstance(
                            item.getHeader(),
                            item.getName()
                    ), CategoryFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        }

        public AppCompatImageView getHeader() {
            return mHeader;
        }

        public AppCompatImageView getLogo() {
            return mLogo;
        }

        public AppCompatTextView getName() {
            return mName;
        }

        public AppCompatTextView getDeliveryTime() {
            return mDeliveryTime;
        }
    }

    class CategoryItem {
        private int header;
        private int logo;
        private String name;
        private String deliveryTime;

        public CategoryItem(int header, int logo, String name, String deliveryTime) {
            this.header = header;
            this.logo = logo;
            this.name = name;
            this.deliveryTime = deliveryTime;
        }

        public int getHeader() {
            return header;
        }

        public int getLogo() {
            return logo;
        }

        public String getName() {
            return name;
        }

        public String getDeliveryTime() {
            return deliveryTime;
        }
    }

    class GetPickLocation extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            objects.add(new PickLocationItem(
                    "Hill Drive, Bhavnagar"
            ));

            mCategoryList.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            new GetTopMenu().execute();
        }
    }

    class GetTopMenu extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            objects.add(new TopMenuItems(
                    new int[]{
                            R.drawable.ic_grocery,
                            R.drawable.ic_mobiles,
                            R.drawable.ic_fruits_vegetables,
                            R.drawable.ic_grocery,
                            R.drawable.ic_mobiles,
                            R.drawable.ic_fruits_vegetables
                    },
                    new String[]{
                            "Household Needs",
                            "Fitness Devices",
                            "Vegetables",
                            "Household Needs",
                            "Fitness Devices",
                            "Vegetables"
                    }
            ));

            mAdapter.notifyDataSetChanged();
            new GetSlider().execute();
        }
    }

    class GetSlider extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                return mUtils.getToServer(ServerData.Slider.URL, null);
            } catch (Exception e) {
                Log.e(TAG, "GetSlider::doInBackground(): " + e.getMessage());
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
                Log.e(TAG, "GetSlider::onPostExecute(): result is: " + result);
                try {
                    JSONArray sliderList = new JSONObject(result)
                            .getJSONArray(ServerData.Slider.SliderList);
                    if (null != sliderList && sliderList.length() > 0) {
                        mSliderImageRes.clear();
                        for (int i = 0; i < sliderList.length(); i++) {
                            JSONObject sliderObject = sliderList.getJSONObject(i);
                            if (sliderObject != null) {
                                mSliderImageRes.add(
                                        ServerData.Slider.ImageURL +
                                                sliderObject.getString(ServerData.Slider.SliderImage));
                            } else {
                                Log.e(TAG, "GetSlider::onPostExecute():" +
                                        " sliderObject is null at index " + i);
                            }
                        }
                    } else {
                        Log.e(TAG, "GetSlider::onPostExecute():" +
                                " sliderList is null or empty");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "GetSlider::onPostExecute(): " + e.getMessage());
                }
            }
            objects.add(new SliderItems());

            mAdapter.notifyDataSetChanged();
            baseGetCategory(false);
            // mCategoryList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            //     @Override
            //     public void onRefresh() {
            //         baseGetCategory(false);
            //     }
            // });
            // moreListener = new OnMoreListener() {
            //     @Override
            //     public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
            //         baseGetCategory(true);
            //     }
            // };
        }
    }

    class GetCategoryList extends AsyncTask<Void, Void, String> {

        private boolean isMoreAsk;

        public GetCategoryList(boolean isMoreAsk) {
            super();
            this.isMoreAsk = isMoreAsk;
        }

        @Override
        protected String doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            setupCategoryList(s);
        }

        public void setupCategoryList(String s) {
            objects.add(new CategoryItem(
                    R.drawable.ic_header_1,
                    R.drawable.ic_grocery,
                    mActivity.getString(R.string.cat_1),
                    mActivity.getString(R.string.delivery_time)));
            objects.add(new CategoryItem(
                    R.drawable.ic_header_2,
                    R.drawable.ic_mobiles,
                    mActivity.getString(R.string.cat_2),
                    mActivity.getString(R.string.delivery_time)));
            objects.add(new CategoryItem(
                    R.drawable.ic_header_3,
                    R.drawable.ic_fruits_vegetables,
                    mActivity.getString(R.string.cat_3),
                    mActivity.getString(R.string.delivery_time)));
            objects.add(new CategoryItem(
                    R.drawable.ic_header_1,
                    R.drawable.ic_grocery,
                    mActivity.getString(R.string.cat_1),
                    mActivity.getString(R.string.delivery_time)));
            objects.add(new CategoryItem(
                    R.drawable.ic_header_2,
                    R.drawable.ic_mobiles,
                    mActivity.getString(R.string.cat_2),
                    mActivity.getString(R.string.delivery_time)));
            objects.add(new CategoryItem(
                    R.drawable.ic_header_3,
                    R.drawable.ic_fruits_vegetables,
                    mActivity.getString(R.string.cat_3),
                    mActivity.getString(R.string.delivery_time)));

            mAdapter.notifyDataSetChanged();
            // mCategoryList.setOnMoreListener(moreListener);
        }
    }

    class ServerData {
        public class Slider {
            public static final String URL = Utils.baseURL + "getslider.aspx";
            public static final String ImageURL = "http://foooddies.com/admin/";
            public static final String SliderList = "slider";
            public static final String SliderID = "sid";
            public static final String SliderImage = "image";
        }
    }
}

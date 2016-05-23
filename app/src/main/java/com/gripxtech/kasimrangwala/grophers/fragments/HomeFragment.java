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
import java.util.Timer;
import java.util.TimerTask;

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
        mActivity.setTitle(getString(R.string.app_name));

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
                    Picasso.with(mActivity)
                            .load(item.getResDrawable(i))
                            .into(viewHolder.getTopMenuImages().get(i));
                    viewHolder.getTopMenuTexts().get(i).setText(item.getDescription(i));
                }
                // } else if (holder.getItemViewType() == ItemViewTypes.Slider) {
                // SliderItems items = (SliderItems) objects.get(position);
                // SliderViewHolder viewHolder = (SliderViewHolder) holder;
            } else if (holder.getItemViewType() == ItemViewTypes.Category) {
                CategoryItem item = (CategoryItem) objects.get(position);
                CategoryViewHolder viewHolder = (CategoryViewHolder) holder;
                Picasso.with(mActivity)
                        .load(item.getHeader())
                        .into(viewHolder.getHeader());
                Picasso.with(mActivity)
                        .load(item.getLogo())
                        .into(viewHolder.getLogo());
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

        @BindViews({R.id.ivTopMenu1, R.id.ivTopMenu2, R.id.ivTopMenu3, R.id.ivTopMenu4, R.id.ivTopMenu5, R.id.ivTopMenu6, R.id.ivTopMenu7})
        List<ImageView> mTopMenuImages;

        @BindViews({R.id.tvTopMenu1, R.id.tvTopMenu2, R.id.tvTopMenu3, R.id.tvTopMenu4, R.id.tvTopMenu5, R.id.tvTopMenu6, R.id.tvTopMenu7})
        List<TextView> mTopMenuTexts;

        @BindViews({R.id.cvTopMenu4, R.id.cvTopMenu5, R.id.cvTopMenu6, R.id.cvTopMenu7})
        List<CardView> mTopMenuItems;

        @BindView(R.id.tvTopMenuToggle)
        TextView mToggleText;

        @BindView(R.id.glRootTopMenu)
        GridLayout mRoot;

        boolean isTopItemHidden = true;

        public TopMenuViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.ivTopMenuToggle)
        public void onToggle(ImageView imageView) {
            for (CardView view : mTopMenuItems) {
                view.setVisibility(isTopItemHidden ? View.VISIBLE : View.GONE);
            }
            isTopItemHidden = !isTopItemHidden;
            if (isTopItemHidden) {
                imageView.setImageResource(R.drawable.ic_add_circle_outline_black_48dp);
                mToggleText.setText(getString(R.string.action_expand));

            } else {
                imageView.setImageResource(R.drawable.ic_remove_circle_outline_black_48dp);
                mToggleText.setText(getString(R.string.action_collapse));
            }
        }

        @OnClick({R.id.ivTopMenu1, R.id.ivTopMenu2, R.id.ivTopMenu3, R.id.ivTopMenu4,
                R.id.ivTopMenu5, R.id.ivTopMenu6, R.id.ivTopMenu7})
        public void onItemClick(ImageView imageView) {
            int clickPosition = 0;
            switch (imageView.getId()) {
                case R.id.ivTopMenu1:
                    clickPosition = 0;
                    break;
                case R.id.ivTopMenu2:
                    clickPosition = 1;
                    break;
                case R.id.ivTopMenu3:
                    clickPosition = 2;
                    break;
                case R.id.ivTopMenu4:
                    clickPosition = 3;
                    break;
                case R.id.ivTopMenu5:
                    clickPosition = 4;
                    break;
                case R.id.ivTopMenu6:
                    clickPosition = 5;
                    break;
                case R.id.ivTopMenu7:
                    clickPosition = 6;
                    break;

            }
            TopMenuItems item = (TopMenuItems) mAdapter.getObjects().get(getAdapterPosition());
            mActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContentMain, CategoryFragment.newInstance(
                            item.getResDrawable(clickPosition),
                            item.getDescription(clickPosition),
                            item.getId(clickPosition)
                    ), CategoryFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        }

        public List<ImageView> getTopMenuImages() {
            return mTopMenuImages;
        }

        public List<TextView> getTopMenuTexts() {
            return mTopMenuTexts;
        }
    }

    class TopMenuItems {
        public static final int LIMIT = 7;
        private String[] id;
        private String[] resDrawable;
        private String[] description;

        public TopMenuItems(String[] id, String[] resDrawable, String[] description) {
            this.id = id;
            this.resDrawable = resDrawable;
            this.description = description;
        }

        public String getId(int location) {
            return id[location];
        }

        public String getResDrawable(int location) {
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
            final SliderAdapter sliderAdapter = new SliderAdapter();
            mPager.setAdapter(sliderAdapter);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mPager.getCurrentItem() == (sliderAdapter.getCount() - 1)) {
                                mPager.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPager.setCurrentItem(0, true);
                                    }
                                });
                            } else {
                                mPager.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
                                    }
                                });
                            }
                        }
                    });
                }
            }, 99, 1999);
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
                            item.getName(),
                            item.getId()
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
        private String id;
        private String header;
        private String logo;
        private String name;
        private String deliveryTime;

        public CategoryItem(String id, String header, String logo, String name, String deliveryTime) {
            this.id = id;
            this.header = header;
            this.logo = logo;
            this.name = name;
            this.deliveryTime = deliveryTime;
        }

        public String getId() {
            return id;
        }

        public String getHeader() {
            return header;
        }

        public String getLogo() {
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
            try {
                return mUtils.getToServer(ServerData.Category.URL, null);
            } catch (Exception e) {
                Log.e(TAG, "GetTopMenu::doInBackground(): " + e.getMessage());
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
                Log.e(TAG, "GetTopMenu::onPostExecute(): result is: " + result);
                String[] topMenuIds = new String[TopMenuItems.LIMIT];
                String[] topMenuImages = new String[TopMenuItems.LIMIT];
                String[] topMenuText = new String[TopMenuItems.LIMIT];
                try {
                    JSONArray topMenuList = new JSONObject(result)
                            .getJSONArray(ServerData.Category.CategoryList);
                    if (null != topMenuList && topMenuList.length() > 0) {
                        for (int i = 0; i < topMenuList.length(); i++) {
                            JSONObject topMenuObject = topMenuList.getJSONObject(i);
                            if (topMenuObject != null) {
                                topMenuIds[i] = topMenuObject.getString(ServerData.Category.CateGoryID);
                                topMenuImages[i] = ServerData.Category.ImageURL +
                                        topMenuObject.getString(ServerData.Category.CateGoryImage);
                                topMenuText[i] = topMenuObject.getString(ServerData.Category.Category);
                            } else {
                                Log.e(TAG, "GetTopMenu::onPostExecute():" +
                                        " topMenuObject is null at index " + i);
                            }
                            if (i == TopMenuItems.LIMIT - 1) {
                                break;
                            }
                        }

                    } else {
                        Log.e(TAG, "GetTopMenu::onPostExecute():" +
                                " topMenuList is null or empty");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "GetTopMenu::onPostExecute(): " + e.getMessage());
                }
                objects.add(new TopMenuItems(
                        topMenuIds,
                        topMenuImages,
                        topMenuText
                ));
            }
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
            try {
                return mUtils.getToServer(ServerData.Category.URL, null);
            } catch (Exception e) {
                Log.e(TAG, "GetCategoryList::doInBackground(): " + e.getMessage());
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
                Log.e(TAG, "GetCategoryList::onPostExecute(): result is: " + result);
                try {
                    JSONArray categoryList = new JSONObject(result)
                            .getJSONArray(ServerData.Category.CategoryList);
                    if (null != categoryList && categoryList.length() > 0) {
                        for (int i = 0; i < categoryList.length(); i++) {
                            JSONObject categoryObject = categoryList.getJSONObject(i);
                            if (categoryObject != null) {
                                objects.add(new CategoryItem(
                                        categoryObject.getString(ServerData.Category.CateGoryID),
                                        ServerData.Category.ImageURL +
                                                categoryObject.getString(ServerData.Category.CateGoryImage),
                                        ServerData.Category.ImageURL +
                                                categoryObject.getString(ServerData.Category.CateGoryImage),
                                        categoryObject.getString(ServerData.Category.Category),
                                        mActivity.getString(R.string.delivery_time)));
                            } else {
                                Log.e(TAG, "GetCategoryList::onPostExecute():" +
                                        " categoryObject is null at index " + i);
                            }
                        }
                    } else {
                        Log.e(TAG, "GetCategoryList::onPostExecute():" +
                                " categoryList is null or empty");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "GetCategoryList::onPostExecute(): " + e.getMessage());
                }
            }
            mAdapter.notifyDataSetChanged();
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

        public class Category {
            public static final String URL = Utils.baseURL + "getcategory.aspx";
            public static final String ImageURL = "http://foooddies.com/admin/";
            public static final String CategoryList = "category";
            public static final String CateGoryID = "Cid";
            public static final String Category = "Category";
            public static final String CateGoryImage = "image";
        }
    }
}

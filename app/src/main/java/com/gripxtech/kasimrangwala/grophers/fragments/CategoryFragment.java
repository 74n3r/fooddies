package com.gripxtech.kasimrangwala.grophers.fragments;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import butterknife.ButterKnife;

public class CategoryFragment extends Fragment {

    public static final String TAG = CategoryFragment.class.getSimpleName();
    private static final String ARG_HEADER = "header";
    private static final String ARG_NAME = "name";
    private static final String ARG_ID = "id";

    @BindView(R.id.clRootCategory)
    CoordinatorLayout mRootWidget;

    @BindView(R.id.ablCategory)
    AppBarLayout mAppBar;

    @BindView(R.id.ivCategoryHeader)
    AppCompatImageView mHeaderImg;

    @BindView(R.id.tbToolbar)
    Toolbar mToolbar;

    @BindView(R.id.rvCategory)
    SuperRecyclerView mCategoryList;

    private CategoryAdapter mAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    private MainActivity mActivity;
    private Handler mHandler;
    private Utils mUtils;

    private String mHeader;
    private String mName;
    private String mID;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment newInstance(String header, String name, String id) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HEADER, header);
        args.putString(ARG_NAME, name);
        args.putString(ARG_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mHeader = getArguments().getString(ARG_HEADER);
            mName = getArguments().getString(ARG_NAME);
            mID = getArguments().getString(ARG_ID);
        }
        mActivity = (MainActivity) getActivity();
        mHandler = new Handler();
        mUtils = Utils.getInstance();

        mAdapter = new CategoryAdapter(new ArrayList<CategoryItem>());
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
        mActivity.getDrawerLayout().addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mActivity.setTitle(mName);
        Picasso.with(mActivity)
                .load(mHeader)
                .into(mHeaderImg);

        mAppBar.post(new Runnable() {
            @Override
            public void run() {
                mAppBar.setExpanded(false, true);
            }
        });
    }

    public void setupCategoryList() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCategoryList.setLayoutManager(linearLayoutManager);
        mCategoryList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                baseGetCategory();
            }
        });
        baseGetCategory();
    }

    public void baseGetCategory() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                new GetCategoryList().execute();
            }
        });
    }

    class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

        private List<CategoryItem> categoryItems;

        public CategoryAdapter(List<CategoryItem> categoryItems) {
            super();
            this.categoryItems = categoryItems;
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CategoryViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_view_home, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(CategoryViewHolder holder, int position) {
            CategoryItem item = categoryItems.get(position);
            Picasso.with(mActivity)
                    .load(item.getLogo())
                    .into(holder.getLogo());
            holder.getName().setText(item.getName());
            holder.getDeliveryTime().setText(item.getDeliveryTime());
        }

        @Override
        public int getItemCount() {
            return categoryItems.size();
        }

        public List<CategoryItem> getCategoryItems() {
            return categoryItems;
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ivHomeHeader)
        AppCompatImageView mHeader;

        @BindView(R.id.ivHomeLogo)
        AppCompatImageView mLogo;

        @BindView(R.id.tvHomeName)
        AppCompatTextView mName;

        @BindView(R.id.tvDeliveryBy)
        AppCompatTextView mDeliveryBy;

        @BindView(R.id.tvHomeDeliveryTime)
        AppCompatTextView mDeliveryTime;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mHeader.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CategoryItem item = mAdapter.getCategoryItems().get(getAdapterPosition());
            mActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContentMain, ProductFragment.newInstance(
                            item.getLogo(),
                            item.getName(),
                            item.getId()
                    ), ProductFragment.TAG)
                    .addToBackStack(null)
                    .commit();
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
        private String logo;
        private String name;
        private String deliveryTime;

        public CategoryItem(String id, String logo, String name, String deliveryTime) {
            this.id = id;
            this.logo = logo;
            this.name = name;
            this.deliveryTime = deliveryTime;
        }

        public String getId() {
            return id;
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

    class GetCategoryList extends AsyncTask<Void, Void, String> {

        public GetCategoryList() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<Pair<String, String>> pairs = new ArrayList<>();
            pairs.add(new Pair<>("catid", mID));
            try {
                return mUtils.getToServer(ServerData.Category.URL, pairs);
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
                // Log.e(TAG, "GetCategoryList::onPostExecute(): result is: " + result);
                mAdapter.getCategoryItems().clear();
                try {
                    JSONArray categoryList = new JSONObject(result)
                            .optJSONArray("subcat");
                    if (null != categoryList && categoryList.length() > 0) {
                        for (int i = 0; i < categoryList.length(); i++) {
                            JSONObject category = categoryList.getJSONObject(i);
                            mAdapter.getCategoryItems().add(new CategoryItem(
                                    category.optString("Sid"),
                                    ServerData.Category.ImageURL +
                                            category.optString("image"),
                                    category.optString("SubCategory"),
                                    mActivity.getString(R.string.delivery_time)
                            ));
                        }
                    } else {
                        Log.e(TAG, "GetCategoryList::onPostExecute():" +
                                " categoryList is null or empty");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "GetCategoryList::onPostExecute(): " + e.getMessage());
                }
                mAdapter.notifyDataSetChanged();
            }
            mCategoryList.setAdapter(mAdapter);
        }
    }

    class ServerData {
        public class Category {
            public static final String URL = Utils.baseURL + "getsubcategory.aspx";
            public static final String ImageURL = "http://foooddies.com/admin/";
        }
    }
}

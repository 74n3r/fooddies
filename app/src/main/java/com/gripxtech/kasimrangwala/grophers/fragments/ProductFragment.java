package com.gripxtech.kasimrangwala.grophers.fragments;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import com.gripxtech.kasimrangwala.grophers.models.DoAddToCart;
import com.gripxtech.kasimrangwala.grophers.models.ProductItem;
import com.gripxtech.kasimrangwala.grophers.utils.AppPrefs;
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
import butterknife.OnClick;

public class ProductFragment extends Fragment {

    public static final String TAG = ProductFragment.class.getSimpleName();
    private static final String ARG_HEADER = "header";
    private static final String ARG_NAME = "name";
    private static final String ARG_ID = "id";

    @BindView(R.id.clRootProduct)
    CoordinatorLayout mRootWidget;

    @BindView(R.id.ablProduct)
    AppBarLayout mAppBar;

    @BindView(R.id.ivProductHeader)
    AppCompatImageView mHeaderImg;

    @BindView(R.id.tbToolbar)
    Toolbar mToolbar;

    @BindView(R.id.rvProduct)
    SuperRecyclerView mProductList;

    private ProductAdapter mAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    private MainActivity mActivity;
    private Handler mHandler;
    private AppPrefs mPrefs;
    private Utils mUtils;

    private String mHeader;
    private String mName;
    private String mID;


    public ProductFragment() {
        // Required empty public constructor
    }

    public static ProductFragment newInstance(String header, String name, String id) {
        ProductFragment fragment = new ProductFragment();
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
        mPrefs = new AppPrefs(mActivity);
        mUtils = Utils.getInstance();

        mAdapter = new ProductAdapter(new ArrayList<ProductItem>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setUpViews();
        setupProductList();
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

    public void setupProductList() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mProductList.setLayoutManager(linearLayoutManager);
        mProductList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                baseGetProduct();
            }
        });
        baseGetProduct();
    }

    public void baseGetProduct() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                new GetProductList().execute();
            }
        });
    }

    class ProductAdapter extends RecyclerView.Adapter<ProductViewHolder> {

        private List<ProductItem> productItems;

        public ProductAdapter(List<ProductItem> productItems) {
            super();
            this.productItems = productItems;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ProductViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_view_product, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(ProductViewHolder holder, int position) {
            ProductItem item = productItems.get(position);
            Picasso.with(mActivity)
                    .load(item.getLogo())
                    .into(holder.getHeader());
            holder.getName().setText(item.getName());
            holder.getDeliveryTime().setText(item.getDeliveryTime());
        }

        @Override
        public int getItemCount() {
            return productItems.size();
        }

        public List<ProductItem> getProductItems() {
            return productItems;
        }
    }

    class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ivProductHeader)
        AppCompatImageView mHeader;

        @BindView(R.id.ivProductLogo)
        AppCompatImageView mLogo;

        @BindView(R.id.tvProductName)
        AppCompatTextView mName;

        @BindView(R.id.tvProductDeliveryBy)
        AppCompatTextView mDeliveryBy;

        @BindView(R.id.tvProductDeliveryTime)
        AppCompatTextView mDeliveryTime;

        @BindView(R.id.tvProductCartCount)
        AppCompatTextView mCounter;


        public ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mLogo.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
        }

        @OnClick(R.id.ivProductAddToCart)
        public void onAddQty() {
            int counter = Integer.parseInt(mCounter.getText().toString()) + 1;
            mCounter.setText(
                    String.valueOf(counter)
            );
        }

        @OnClick(R.id.ivProductRemoveFromCart)
        public void onRemoveQty() {
            int counter = Integer.parseInt(mCounter.getText().toString());
            if (counter > 1) {
                counter -= 1;
                mCounter.setText(
                        String.valueOf(counter)
                );
            }
        }

        @OnClick(R.id.bnAddToCart)
        public void onAddToCart() {
            ProductItem productItem = mAdapter.getProductItems().get(getAdapterPosition());
            new DoAddToCart(mActivity, this.itemView, mCounter).execute(mPrefs.getUserID(),
                    productItem.getId(), mCounter.getText().toString());
        }

        @Override
        public void onClick(View v) {
            ProductItem item = mAdapter.getProductItems().get(getAdapterPosition());
            mActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContentMain, ProductDetailFragment.newInstance(
                            item.toString(), false
                    ), ProductDetailFragment.TAG)
                    .addToBackStack(null)
                    .commit();
        }

        public AppCompatImageView getHeader() {
            return mHeader;
        }

        public AppCompatTextView getName() {
            return mName;
        }

        public AppCompatTextView getDeliveryTime() {
            return mDeliveryTime;
        }
    }

    class GetProductList extends AsyncTask<Void, Void, String> {

        public GetProductList() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<Pair<String, String>> pairs = new ArrayList<>();
            pairs.add(new Pair<>("subcid", mID));
            try {
                return mUtils.getToServer(ServerData.Product.URL, pairs);
            } catch (Exception e) {
                Log.e(TAG, "GetProductList::doInBackground(): " + e.getMessage());
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
            Log.e(TAG, "GetProductList::onPostExecute(): result is: " + result);
            if (result != null && result.length() != 0) {
                mAdapter.getProductItems().clear();
                try {
                    JSONArray productList = new JSONObject(result)
                            .getJSONArray("Productdetails");
                    if (productList != null && productList.length() > 0) {
                        for (int i = 0; i < productList.length(); i++) {
                            JSONObject product = productList.getJSONObject(i);
                            String id = product.getString("pid");
                            String logo = ServerData.Product.ImageURL +
                                    product.getString("image");
                            String name = product.getString("pname");
                            String shortDesc = product.getString("shortdisc");
                            String desc = product.getString("p_desc");
                            String catID = product.getString("cat");
                            String subCatID = product.getString("Subcat");
                            String code = product.getString("pcode");
                            String sku = product.getString("sku");
                            String discount = product.getString("discount");
                            String size = product.getString("size");
                            String mrp = product.getString("mrp");
                            String rate = product.getString("rate");
                            String notification = product.getString("noti");
                            String weight = product.getString("pweight");
                            String vendorID = product.getString("vendor_id");
                            String todayOffer = product.getString("todayoffer");
                            String featureProductID = product.getString("feactureproduct");
                            String status = product.getString("status");
                            String hot = product.getString("hot");
                            String bestSeller = product.getString("bestseller");
                            String shippingCharge = product.getString("shipingcharge");
                            String pinCode = product.getString("pincode");
                            String deliveryTime = getString(R.string.delivery_time);

                            mAdapter.getProductItems().add(new ProductItem(
                                    id, logo, name, shortDesc,
                                    desc, catID, subCatID, code,
                                    sku, discount, size, mrp,
                                    rate, notification, weight, vendorID,
                                    todayOffer, featureProductID, status, hot,
                                    bestSeller, shippingCharge, pinCode, deliveryTime
                            ));
                        }
                    } else {
                        Log.e(TAG, "GetProductList::onPostExecute(): productList is null or empty.");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "GetProductList::onPostExecute(): " + e.getMessage());
                }
                mAdapter.notifyDataSetChanged();
            }
            mProductList.setAdapter(mAdapter);
        }
    }

    class ServerData {
        public class Product {
            public static final String URL = Utils.baseURL + "getproductdetails.aspx";
            public static final String ImageURL = "http://foooddies.com/admin/";
        }
    }
}

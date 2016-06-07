package com.gripxtech.kasimrangwala.grophers.fragments;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.gripxtech.kasimrangwala.grophers.MainActivity;
import com.gripxtech.kasimrangwala.grophers.R;
import com.gripxtech.kasimrangwala.grophers.models.CartItem;
import com.gripxtech.kasimrangwala.grophers.utils.AppPrefs;
import com.gripxtech.kasimrangwala.grophers.utils.Utils;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartFragment extends Fragment {

    public static final String TAG = CartFragment.class.getSimpleName();

    @BindView(R.id.clRootCart)
    CoordinatorLayout mRootWidget;

    @BindView(R.id.tbToolbar)
    Toolbar mToolbar;

    @BindView(R.id.rvCart)
    SuperRecyclerView mCartList;

    private CartAdapter mAdapter;

    private ActionBarDrawerToggle mDrawerToggle;
    private MainActivity mActivity;
    private Handler mHandler;
    private AppPrefs mPrefs;
    private Utils mUtils;

    public CartFragment() {
        // Required empty public constructor
    }

    public static CartFragment newInstance() {
        return new CartFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mHandler = new Handler();
        mUtils = Utils.getInstance();
        mPrefs = new AppPrefs(mActivity);
        mAdapter = new CartAdapter(new ArrayList<CartItem>());
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu != null) {
            menu.clear();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setUpViews();
        setupCartList();
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

        mActivity.setTitle(getString(R.string.action_cart));
    }

    public void setupCartList() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCartList.setLayoutManager(linearLayoutManager);
        mCartList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                baseGetCart();
            }
        });
        baseGetCart();
    }

    public void baseGetCart() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                new GetCartList().execute();
            }
        });
    }

    class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {

        private List<CartItem> cartItems;

        public CartAdapter(List<CartItem> cartItems) {
            super();
            this.cartItems = cartItems;
        }

        @Override
        public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CartViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_view_cart, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(CartViewHolder holder, int position) {
            CartItem item = cartItems.get(position);
            Picasso.with(mActivity)
                    .load(item.getLogo())
                    .into(holder.getLogo());
            holder.getName().setText(item.getName());
            holder.getDeliveryTime().setText(item.getDeliveryTime());
            holder.getQtyRate().setText(String.valueOf(
                    item.getQuantity() + " X " + item.getRate()
            ));
            holder.getTotal().setText(String.valueOf(
                    NumberUtils.toInt(item.getQuantity(), 0)
                            * NumberUtils.toInt(item.getRate(), 0)
            ));
        }

        @Override
        public int getItemCount() {
            return cartItems.size();
        }

        public List<CartItem> getCartItems() {
            return cartItems;
        }
    }

    class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ivCartHeader)
        AppCompatImageView mHeader;

        @BindView(R.id.ivCartLogo)
        AppCompatImageView mLogo;

        @BindView(R.id.tvCartName)
        AppCompatTextView mName;

        // @BindView(R.id.tvDeliveryBy)
        // AppCompatTextView mDeliveryBy;

        @BindView(R.id.tvCartDeliveryTime)
        AppCompatTextView mDeliveryTime;

        @BindView(R.id.tvCartQtyRate)
        AppCompatTextView mQtyRate;

        @BindView(R.id.tvCartTotal)
        AppCompatTextView mTotal;

        public CartViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mHeader.setVisibility(View.GONE);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            CartItem item = mAdapter.getCartItems().get(getAdapterPosition());
            mActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContentMain, ProductDetailFragment.newInstance(
                            item.toString(), true
                    ), ProductDetailFragment.TAG)
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

        public AppCompatTextView getQtyRate() {
            return mQtyRate;
        }

        public AppCompatTextView getTotal() {
            return mTotal;
        }
    }

    class GetCartList extends AsyncTask<Void, Void, String> {

        public GetCartList() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<Pair<String, String>> pairs = new ArrayList<>();
            pairs.add(new Pair<>("uid", mPrefs.getUserID()));
            try {
                return mUtils.getToServer(ServerData.Cart.URL, pairs);
            } catch (Exception e) {
                Log.e(TAG, "GetCartList::doInBackground(): " + e.getMessage());
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
                Log.e(TAG, "GetCartList::onPostExecute(): result is: " + result);
                mAdapter.getCartItems().clear();
                try {
                    JSONArray cartList = new JSONObject(result)
                            .optJSONArray("cart");
                    if (null != cartList && cartList.length() > 0) {
                        for (int i = 0; i < cartList.length(); i++) {
                            JSONObject cart = cartList.getJSONObject(i);
                            String id = cart.getString("pid");
                            String logo = ServerData.Cart.ImageURL +
                                    cart.getString("image");
                            String name = cart.getString("pname");
                            String shortDesc = cart.getString("shortdisc");
                            String desc = cart.getString("p_desc");
                            String catID = cart.getString("cat");
                            String subCatID = cart.getString("Subcat");
                            String code = cart.getString("pcode");
                            String sku = cart.getString("sku");
                            String discount = cart.getString("discount");
                            String size = cart.getString("size");
                            String mrp = cart.getString("mrp");
                            String rate = cart.getString("rate");
                            String notification = cart.getString("noti");
                            String weight = cart.getString("pweight");
                            String vendorID = cart.getString("vendor_id");
                            String todayOffer = cart.getString("todayoffer");
                            String featureProductID = cart.getString("feactureproduct");
                            String status = cart.getString("status");
                            String hot = cart.getString("hot");
                            String bestSeller = cart.getString("bestseller");
                            String shippingCharge = cart.getString("shipingcharge");
                            String pinCode = cart.getString("pincode");
                            String deliveryTime = getString(R.string.delivery_time);

                            String cartID = cart.getString("cartid");
                            String userID = cart.getString("uid");
                            String quantity = cart.getString("qty");
                            String delivery = cart.getString("delivery");
                            String sellerID = cart.getString("id");
                            String sellerShopName = cart.getString("shopname");
                            String sellerRating = cart.getString("Rating");
                            String sellerImage = ServerData.Cart.ImageURL +
                                    cart.getString("simage");
                            mAdapter.getCartItems().add(new CartItem(
                                    id, logo, name, shortDesc, desc, catID, subCatID, code,
                                    sku, discount, size, mrp, rate, notification, weight, vendorID,
                                    todayOffer, featureProductID, status, hot, bestSeller,
                                    shippingCharge, pinCode, deliveryTime, cartID, userID, quantity,
                                    delivery, sellerID, sellerShopName, sellerRating, sellerImage
                            ));
                        }
                    } else {
                        Log.e(TAG, "GetCartList::onPostExecute():" +
                                " cartList is null or empty");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "GetCartList::onPostExecute(): " + e.getMessage());
                }
                mAdapter.notifyDataSetChanged();
            }
            mCartList.setAdapter(mAdapter);
        }
    }

    class ServerData {
        public class Cart {
            public static final String URL = Utils.baseURL + "getcart.aspx";
            public static final String ImageURL = "http://foooddies.com/admin/";
        }
    }
}

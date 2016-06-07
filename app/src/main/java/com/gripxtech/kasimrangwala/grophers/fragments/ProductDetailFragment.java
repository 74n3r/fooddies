package com.gripxtech.kasimrangwala.grophers.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.gripxtech.kasimrangwala.grophers.MainActivity;
import com.gripxtech.kasimrangwala.grophers.R;
import com.gripxtech.kasimrangwala.grophers.models.DoAddToCart;
import com.gripxtech.kasimrangwala.grophers.models.ProductItem;
import com.gripxtech.kasimrangwala.grophers.utils.AppPrefs;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductDetailFragment extends Fragment {

    public static final String TAG = ProductDetailFragment.class.getSimpleName();
    private static final String ARG_ITEM = "item";
    private static final String ARG_CART = "cart";

    @BindView(R.id.clRootProductDetail)
    CoordinatorLayout mRootWidget;

    @BindView(R.id.tbToolbar)
    Toolbar mToolbar;

    @BindView(R.id.nsvProductDetail)
    NestedScrollView mScroll;

    @BindView(R.id.ivProductDetailHeader)
    AppCompatImageView mHeader;

    @BindView(R.id.tvProductDetailName)
    AppCompatTextView mName;

    // @BindView(R.id.tvProductDetailDeliveryBy)
    // AppCompatTextView mDeliveryBy;

    @BindView(R.id.tvProductDetailDeliveryTime)
    AppCompatTextView mDeliveryTime;

    @BindView(R.id.llProductCart)
    LinearLayoutCompat mCartLayout;

    @BindView(R.id.tvProductDetailCartCount)
    AppCompatTextView mCounter;

    @BindView(R.id.wvProductDetailDesc)
    WebView mDesc;

    private ActionBarDrawerToggle mDrawerToggle;
    private MainActivity mActivity;
    private AppPrefs mPrefs;

    private ProductItem mItem;
    private boolean mCart;

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    public static ProductDetailFragment newInstance(String item, boolean cart) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ITEM, item);
        args.putBoolean(ARG_CART, cart);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mItem = new ProductItem(getArguments().getString(ARG_ITEM));
            mCart = getArguments().getBoolean(ARG_CART);
        }
        mActivity = (MainActivity) getActivity();
        mPrefs = new AppPrefs(mActivity);
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
        return inflater.inflate(R.layout.fragment_product_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setUpViews();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @OnClick(R.id.ivProductDetailAddToCart)
    public void onAddQty() {
        int counter = Integer.parseInt(mCounter.getText().toString()) + 1;
        mCounter.setText(
                String.valueOf(counter)
        );
    }

    @OnClick(R.id.ivProductDetailRemoveFromCart)
    public void onRemoveQty() {
        int counter = Integer.parseInt(mCounter.getText().toString());
        if (counter > 1) {
            counter -= 1;
            mCounter.setText(
                    String.valueOf(counter)
            );
        }
    }

    @OnClick(R.id.bnProductDetailAddToCart)
    public void onAddToCart() {
        new DoAddToCart(mActivity, mRootWidget, mCounter).execute(mPrefs.getUserID(),
                mItem.getId(), mCounter.getText().toString());
    }

    public void setUpViews() {
        mActivity.setSupportActionBar(mToolbar);
        ActionBar actionBar = mActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mActivity.setTitle(mItem.getName());

        mDrawerToggle = new ActionBarDrawerToggle(mActivity, mActivity.getDrawerLayout(), mToolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mActivity.getDrawerLayout().addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        Picasso.with(mActivity)
                .load(mItem.getLogo())
                .into(mHeader);
        mName.setText(mItem.getName());
        mDeliveryTime.setText(mItem.getDeliveryTime());

        mDesc.loadData(mItem.getDesc(), "text/html", "UTF-8");

        if (mCart) {
            mCartLayout.setVisibility(View.GONE);
        }
    }
}

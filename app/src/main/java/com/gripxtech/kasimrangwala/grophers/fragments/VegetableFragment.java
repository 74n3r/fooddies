package com.gripxtech.kasimrangwala.grophers.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gripxtech.kasimrangwala.grophers.R;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VegetableFragment extends Fragment {

    public static final String TAG = VegetableFragment.class.getSimpleName();

    @BindView(R.id.rvVegetables)
    SuperRecyclerView mCategoryList;

    // private MainActivity mActivity;
    private Handler mHandler;

    public VegetableFragment() {
        // Required empty public constructor
    }

    public static VegetableFragment newInstance() {
        return new VegetableFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mActivity = (MainActivity) getActivity();
        mHandler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vegetable, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setupCategoryList();
    }

    public void setupCategoryList() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mCategoryList.setLayoutManager(linearLayoutManager);
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

    class CategoryListAdapter extends RecyclerView.Adapter<CategoryViewHolder> {

        private List<CategoryItem> categoryItems;

        public CategoryListAdapter(List<CategoryItem> categoryItems) {
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
            holder.getLogo().setImageResource(item.getLogo());
            holder.getName().setText(item.getName());
            holder.getDeliveryBy().setText(item.getDeliveryBy());
            holder.getDeliveryTime().setText(item.getDeliveryTime());
        }

        @Override
        public int getItemCount() {
            return categoryItems.size();
        }

//        public List<CategoryItem> getCategoryItems() {
//            return categoryItems;
//        }
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

        }

//        public AppCompatImageView getHeader() {
//            return mHeader;
//        }

        public AppCompatImageView getLogo() {
            return mLogo;
        }

        public AppCompatTextView getName() {
            return mName;
        }

        public AppCompatTextView getDeliveryBy() {
            return mDeliveryBy;
        }

        public AppCompatTextView getDeliveryTime() {
            return mDeliveryTime;
        }
    }

    class CategoryItem {

        private int logo;
        private String name;
        private String deliveryBy;
        private String deliveryTime;

        public CategoryItem(int logo, String name, String deliveryBy, String deliveryTime) {
            this.logo = logo;
            this.name = name;
            this.deliveryBy = deliveryBy;
            this.deliveryTime = deliveryTime;
        }

        public int getLogo() {
            return logo;
        }

        public String getName() {
            return name;
        }

        public String getDeliveryBy() {
            return deliveryBy;
        }

        public String getDeliveryTime() {
            return deliveryTime;
        }
    }

    class GetCategoryList extends AsyncTask<Void, Void, String> {

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
            List<CategoryItem> categoryItems = new ArrayList<>();
            categoryItems.add(new CategoryItem(
                    R.drawable.ic_logo_4,
                    "Bottle Gourd",
                    "1 Unit (400-700 gms)",
                    "RS 23/-"
            ));
            categoryItems.add(new CategoryItem(
                    R.drawable.ic_logo_5,
                    "Cabbage",
                    "1 Unit (500-700 gms)",
                    "RS 15/-"
            ));
            categoryItems.add(new CategoryItem(
                    R.drawable.ic_logo_6,
                    "Green Capsicum - Pack",
                    "1 Unit (200-300 gms)",
                    "RS 15/-"
            ));
            categoryItems.add(new CategoryItem(
                    R.drawable.ic_logo_7,
                    "Cauliflower",
                    "1 Unit (500-700 gms)",
                    "RS 30/-"
            ));
            categoryItems.add(new CategoryItem(
                    R.drawable.ic_logo_4,
                    "Bottle Gourd",
                    "1 Unit (400-700 gms)",
                    "RS 23/-"
            ));
            categoryItems.add(new CategoryItem(
                    R.drawable.ic_logo_5,
                    "Cabbage",
                    "1 Unit (500-700 gms)",
                    "RS 15/-"
            ));
            categoryItems.add(new CategoryItem(
                    R.drawable.ic_logo_6,
                    "Green Capsicum - Pack",
                    "1 Unit (200-300 gms)",
                    "RS 15/-"
            ));
            categoryItems.add(new CategoryItem(
                    R.drawable.ic_logo_7,
                    "Cauliflower",
                    "1 Unit (500-700 gms)",
                    "RS 30/-"
            ));

            CategoryListAdapter mAdapter = new CategoryListAdapter(categoryItems);
            mCategoryList.setAdapter(mAdapter);
        }
    }
}

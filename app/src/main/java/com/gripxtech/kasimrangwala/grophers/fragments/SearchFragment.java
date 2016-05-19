package com.gripxtech.kasimrangwala.grophers.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.gripxtech.kasimrangwala.grophers.MainActivity;
import com.gripxtech.kasimrangwala.grophers.R;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFragment extends Fragment {

    public static final String TAG = SearchFragment.class.getSimpleName();

    @BindView(R.id.tbSearch)
    Toolbar mToolbar;

    @BindView(R.id.etSearch)
    AppCompatEditText mSearch;

    @BindView(R.id.ivSearchClear)
    ImageView mClear;

    @BindView(R.id.rvSearchResult)
    SuperRecyclerView mSearchResults;

    SearchResultsAdapter mAdapter;
    OnMoreListener moreListener;

    MainActivity mActivity;
    Handler mHandler;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mHandler = new Handler();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setupViews();
    }

    public void setupViews() {
        mActivity.setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });

        mSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mAdapter.getFilter().filter(s.toString());
            }
        });

        mClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearch.setText("");
            }
        });

        // Setting up Search result
        setupSearchResults();
    }

    public void setupSearchResults() {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSearchResults.setLayoutManager(linearLayoutManager);
        baseGetSearchResults(false);
        // mCategoryList.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        //     @Override
        //     public void onRefresh() {
        //         baseGetCategory(false);
        //     }
        // });
        moreListener = new OnMoreListener() {
            @Override
            public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos) {
                baseGetSearchResults(true);
            }
        };
    }

    public void baseGetSearchResults(final boolean isMoreAsk) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                new GetSearchResults(isMoreAsk).execute();
            }
        });
    }

    class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultViewHolder> implements Filterable {

        private List<SearchResultItem> searchResultItems;
        private List<SearchResultItem> lastSearchResultItems;

        public SearchResultsAdapter(List<SearchResultItem> searchResultItems) {
            super();
            this.searchResultItems = searchResultItems;
        }

        @Override
        public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SearchResultViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_view_search_result, parent, false)
            );
        }

        @Override
        public void onBindViewHolder(SearchResultViewHolder holder, int position) {
            SearchResultItem item = searchResultItems.get(position);
            holder.getLogo().setImageResource(item.getLogo());
            holder.getName().setText(item.getName());
            holder.getDeliveryTime().setText(item.getDeliveryTime());
        }

        @Override
        public int getItemCount() {
            return searchResultItems.size();
        }


        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    final FilterResults oReturn = new FilterResults();
                    final List<SearchResultItem> results = new ArrayList<>();
                    if (lastSearchResultItems == null)
                        lastSearchResultItems = searchResultItems;
                    if (constraint != null) {
                        if (lastSearchResultItems != null && lastSearchResultItems.size() > 0) {
                            for (final SearchResultItem item : lastSearchResultItems) {
                                if (item.getName().toLowerCase(Locale.ENGLISH)
                                        .contains(constraint.toString().toLowerCase(Locale.ENGLISH)))
                                    results.add(item);
                            }
                        }
                        oReturn.values = results;
                    }
                    return oReturn;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    searchResultItems = (List<SearchResultItem>) results.values;
                    notifyDataSetChanged();
                }
            };
        }

        public List<SearchResultItem> getSearchResultItems() {
            return searchResultItems;
        }

    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.ivSearchLogo)
        AppCompatImageView mLogo;

        @BindView(R.id.tvSearchName)
        AppCompatTextView mName;

        @BindView(R.id.tvSearchDeliveryTime)
        AppCompatTextView mDeliveryTime;

        public SearchResultViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

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

    class SearchResultItem {
        private int logo;
        private String name;
        private String deliveryTime;

        public SearchResultItem(int logo, String name, String deliveryTime) {
            this.logo = logo;
            this.name = name;
            this.deliveryTime = deliveryTime;
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

    class GetSearchResults extends AsyncTask<Void, Void, String> {

        private boolean isMoreAsk;

        public GetSearchResults(boolean isMoreAsk) {
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
            setupSearchResults(s);
        }

        public void setupSearchResults(String s) {
            List<SearchResultItem> searchResultItems = new ArrayList<>();
            searchResultItems.add(new SearchResultItem(
                    R.drawable.ic_grocery,
                    mActivity.getString(R.string.cat_1),
                    mActivity.getString(R.string.delivery_time)));
            searchResultItems.add(new SearchResultItem(
                    R.drawable.ic_mobiles,
                    mActivity.getString(R.string.cat_2),
                    mActivity.getString(R.string.delivery_time)));
            searchResultItems.add(new SearchResultItem(
                    R.drawable.ic_fruits_vegetables,
                    mActivity.getString(R.string.cat_3),
                    mActivity.getString(R.string.delivery_time)));
            searchResultItems.add(new SearchResultItem(
                    R.drawable.ic_grocery,
                    mActivity.getString(R.string.cat_1),
                    mActivity.getString(R.string.delivery_time)));
            searchResultItems.add(new SearchResultItem(
                    R.drawable.ic_mobiles,
                    mActivity.getString(R.string.cat_2),
                    mActivity.getString(R.string.delivery_time)));
            searchResultItems.add(new SearchResultItem(
                    R.drawable.ic_fruits_vegetables,
                    mActivity.getString(R.string.cat_3),
                    mActivity.getString(R.string.delivery_time)));

            mAdapter = new SearchResultsAdapter(searchResultItems);
            mSearchResults.setAdapter(mAdapter);
        }
    }
}

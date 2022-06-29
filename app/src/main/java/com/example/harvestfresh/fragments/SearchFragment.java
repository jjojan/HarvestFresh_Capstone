package com.example.harvestfresh.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.harvestfresh.R;
import com.example.harvestfresh.StoreFront;
import com.example.harvestfresh.StoreFrontAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    public static final String SEARCH_REQUEST = "Searched: ";
    public static final String SEARCH_CHANGE = "Search changed to ";
    public static final String STORE_KEY = "StoreName";
    public static final String ERROR_MESSAGE = String.valueOf(R.string.store_error);

    private RecyclerView rvStores;
    private StoreFrontAdapter fragmentAdapter;
    private List<StoreFront> allStores;

    SearchView svSearch;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvStores = view.findViewById(R.id.rvSearch);
        rvStores.setHasFixedSize(true);

        final GridLayoutManager layout = new GridLayoutManager(getContext(), 1);
        rvStores.setLayoutManager(layout);

        allStores = new ArrayList<>();
        fragmentAdapter = new StoreFrontAdapter(getContext(), allStores);
        rvStores.setAdapter(fragmentAdapter);

        svSearch = view.findViewById(R.id.svSearch);

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                CharSequence searchValue = svSearch.getQuery().toString();
                Log.d(TAG, SEARCH_REQUEST + searchValue);
                queryFullSearch(searchValue);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CharSequence searchValue = svSearch.getQuery().toString();
                Log.d(TAG, SEARCH_CHANGE + searchValue);
                queryPartialSearch(searchValue);
                return false;
            }
        });
    }

    private void queryPartialSearch(CharSequence searchValue) {
        ParseQuery<StoreFront> query = ParseQuery.getQuery(StoreFront.class);
        query.include(StoreFront.KEY_NAME);
        query.setLimit(20);
        query.whereStartsWith(STORE_KEY, searchValue.toString());

        query.findInBackground(new FindCallback<StoreFront>() {
            @Override
            public void done(List<StoreFront> stores, ParseException e) {
                if (e != null) {
                    Log.e(TAG, ERROR_MESSAGE, e);
                    return;
                }
                for (StoreFront store : stores) {
                    Log.i(TAG, STORE_KEY + store.getName());
                }
                allStores.addAll(stores);
                fragmentAdapter.notifyDataSetChanged();
            }
        });
    }


    private void queryFullSearch(CharSequence searchValue) {
        ParseQuery<StoreFront> query = ParseQuery.getQuery(StoreFront.class);
        query.include(StoreFront.KEY_NAME);
        query.setLimit(20);
        query.whereFullText(STORE_KEY, searchValue.toString());

        query.findInBackground(new FindCallback<StoreFront>() {
            @Override
            public void done(List<StoreFront> stores, ParseException e) {
                if (e != null) {
                    Log.e(TAG, ERROR_MESSAGE, e);
                    return;
                }
                for (StoreFront store : stores) {
                    Log.i(TAG,STORE_KEY+ store.getName());
                }
                allStores.addAll(stores);
                fragmentAdapter.notifyDataSetChanged();
            }
        });
    }
    }

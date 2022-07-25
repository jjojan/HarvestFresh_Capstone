package com.example.harvestfresh.fragments;

import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.harvestfresh.HarvestFreshDatabase;
import com.example.harvestfresh.R;
import com.example.harvestfresh.SearchSuggestionProvider;
import com.example.harvestfresh.StoreFront;
import com.example.harvestfresh.StoreFrontAdapter;
import com.example.harvestfresh.StoreFrontDao;
import com.example.harvestfresh.StoreFrontRoom;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";
    public static final String STORE_KEY = "StoreName";
    public static final String DATABASE_NAME = "HarvestFreshDatabase";
    public static final String ERROR_MESSAGE = String.valueOf(R.string.store_error);
    public static final int SEARCH_LIMIT = 20;

    private RecyclerView rvStores;
    private StoreFrontAdapter fragmentAdapter;
    private List<StoreFront> allStores;
    private List<StoreFrontRoom> savedStores;
    private StoreFrontDao storeFrontDao;
    private static HomeFragment homeFragment;

    SearchView svSearch;

    public SearchFragment() {
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
        homeFragment = new HomeFragment();
        rvStores = view.findViewById(R.id.rvSearch);
        rvStores.setHasFixedSize(true);

        final GridLayoutManager layout = new GridLayoutManager(getContext(), 1);
        rvStores.setLayoutManager(layout);

        allStores = new ArrayList<>();
        fragmentAdapter = new StoreFrontAdapter(getContext(), allStores);
        rvStores.setAdapter(fragmentAdapter);


        HarvestFreshDatabase harvestFreshDatabase = Room.databaseBuilder(getContext().getApplicationContext(),
                HarvestFreshDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
        storeFrontDao = harvestFreshDatabase.storeFrontDao();
        savedStores = new ArrayList<>();

        svSearch = view.findViewById(R.id.svSearch);

        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                CharSequence searchValue = svSearch.getQuery().toString();
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getContext(),
                        SearchSuggestionProvider.AUTHORITY,
                        SearchSuggestionProvider.MODE);
                suggestions.saveRecentQuery(query, null);
                if (isNetworkConnected()) {
                    queryFullSearch(searchValue);
                } else {
                    roomSearch(searchValue);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                CharSequence searchValue = svSearch.getQuery().toString();
                if (isNetworkConnected()) {
                    queryPartialSearch(searchValue);
                } else {
                    roomSearch(searchValue);
                }
                return false;
            }
        });
    }

    private void roomSearch(CharSequence searchValue) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<StoreFrontRoom> savedStores = storeFrontDao.recentStores();
                List<StoreFrontRoom> savedStoreRooms = storeFrontDao.findStorewithName(searchValue.toString());
                List<StoreFront> stores;
                stores = new ArrayList<>();
                for (StoreFrontRoom storeFrontRoom : savedStoreRooms) {
                    stores.add(storeFrontRoom.toStoreFront());
                }
                allStores.clear();
                allStores.addAll(stores);
            }
        });
        fragmentAdapter.notifyDataSetChanged();
    }

    private void queryPartialSearch(CharSequence searchValue) {
        ParseQuery<StoreFront> query = ParseQuery.getQuery(StoreFront.class);
        query.include(StoreFront.KEY_NAME);
        query.setLimit(SEARCH_LIMIT);
        query.whereFullText(STORE_KEY, searchValue.toString());

        query.findInBackground(new FindCallback<StoreFront>() {
            @Override
            public void done(List<StoreFront> stores, ParseException e) {
                if (e != null) {
                    Log.e(TAG, ERROR_MESSAGE, e);
                    return;
                }
                allStores.clear();
                allStores.addAll(stores);
                fragmentAdapter.notifyDataSetChanged();
            }
        });
    }

    private void queryFullSearch(CharSequence searchValue) {
        ParseQuery<StoreFront> query = ParseQuery.getQuery(StoreFront.class);
        query.include(StoreFront.KEY_NAME);
        query.setLimit(SEARCH_LIMIT);
        query.whereFullText(STORE_KEY, searchValue.toString());

        query.findInBackground(new FindCallback<StoreFront>() {
            @Override
            public void done(List<StoreFront> stores, ParseException e) {
                if (e != null) {
                    Log.e(TAG, ERROR_MESSAGE, e);
                    return;
                }
                allStores.clear();
                allStores.addAll(stores);
                fragmentAdapter.notifyDataSetChanged();
            }
        });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(getContext().CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

}

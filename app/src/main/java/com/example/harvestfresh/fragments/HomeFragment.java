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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.harvestfresh.HarvestFreshDatabase;
import com.example.harvestfresh.ParseApplication;
import com.example.harvestfresh.R;
import com.example.harvestfresh.StoreFront;
import com.example.harvestfresh.StoreFrontAdapter;
import com.example.harvestfresh.StoreFrontDao;
import com.example.harvestfresh.StoreFrontRoom;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private static final String DATABASE_NAME = "HarvestFreshDatabase";
    private static final String OFFLINE_MESSAGE = "You are offline, and are viewing a stale page.";
    private static final String STORE_ERROR = String.valueOf(R.string.store_error);
    private static final int SPAN_COUNT = 2;
    private static final int STORE_LIMIT = 20;


    private RecyclerView rvStores;
    private StoreFrontAdapter fragmentAdapter;
    private List<StoreFront> allStores;
    private List<StoreFrontRoom> savedStores;
    private StoreFrontDao storeFrontDao;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvStores = view.findViewById(R.id.rvSearch);
        rvStores.setHasFixedSize(true);

        final GridLayoutManager layout = new GridLayoutManager(getContext(), SPAN_COUNT);
        rvStores.setLayoutManager(layout);

        allStores = new ArrayList<>();
        savedStores = new ArrayList<>();
        fragmentAdapter = new StoreFrontAdapter(getContext(), allStores);
        rvStores.setAdapter(fragmentAdapter);

        HarvestFreshDatabase harvestFreshDatabase = Room.databaseBuilder(getContext().getApplicationContext(),
                HarvestFreshDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
        storeFrontDao = harvestFreshDatabase.storeFrontDao();

        if(isNetworkConnected()) {
            queryStores();
        }
        else {
            Toasty.info(getContext(), OFFLINE_MESSAGE, Toast.LENGTH_LONG, true).show();
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    List<StoreFrontRoom> savedStoreFronts = storeFrontDao.recentStores();
                    List<StoreFront> storesFromRooms = StoreFrontRoom.getStoreFrontRooms(savedStoreFronts);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fragmentAdapter.addAll(storesFromRooms);
                        }
                    });
                }
            });
        }
    }

    private void queryStores() {
        ParseQuery<StoreFront> query = ParseQuery.getQuery(StoreFront.class);
        query.include(StoreFront.KEY_NAME);
        query.setLimit(STORE_LIMIT);

        query.findInBackground(new FindCallback<StoreFront>() {
            @Override
            public void done(List<StoreFront> stores, ParseException e) {
                if (e != null) {
                    Log.e(TAG, STORE_ERROR, e);
                    return;
                }
                for(StoreFront store: stores) {
                    savedStores.add(store.toStoreFrontRoom());
                }
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        for(StoreFront store: stores) {

                        }
                        storeFrontDao.insertStoreFront(savedStores.toArray(new StoreFrontRoom[0]));
                        List<StoreFrontRoom> savedStores = storeFrontDao.recentStores();
                    }
                });
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
package com.example.harvestfresh;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.harvestfresh.fragments.CartFragment;
import com.example.harvestfresh.fragments.EventsFragment;
import com.example.harvestfresh.fragments.HomeFragment;
import com.example.harvestfresh.fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    private final static String LOGOUT_MESSAGE = "You been logged out";

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FragmentManager fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.mHome:
                        fragment = new HomeFragment();
                        break;
                    case R.id.mSearch:
                        fragment = new SearchFragment();
                        break;
                    case R.id.mEvents:
                        fragment = new EventsFragment();
                        break;
                    case R.id.mCart:
                    default:
                        fragment = new CartFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.fLayoutContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.mHome);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mLogout) {
            onLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onLogout() {
        ParseUser.logOutInBackground();
        ParseUser currentUser = ParseUser.getCurrentUser();
        Toast.makeText(this, LOGOUT_MESSAGE, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
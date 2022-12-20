package com.example.compking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.compking.fragments.FavoriteFragment;
import com.example.compking.fragments.MusicFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_cointer, new MusicFragment()).commit();



        bottomNavigationView = findViewById(R.id.bottom_navigation);



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_music:
                        menuItem.setChecked(true);
                        selectorFragment = new MusicFragment();
                        break;

                    case R.id.nav_favorite:
                        selectorFragment = new FavoriteFragment();
                        break;



                }
               if (selectorFragment != null)  {
                   getSupportFragmentManager().beginTransaction().replace(R.id.fragment_cointer, selectorFragment).commit();

                }

                return true;
            }
        });

    // Bundle intent = getIntent().getExtras();
    // if (intent != null) {
    //     String songid = intent.getString("id");

    //     getSharedPreferences("song", MODE_PRIVATE).edit().putString("id", songid).apply();
    //     getSupportFragmentManager().beginTransaction().replace(R.id.fragment_cointer, new SongFragment()).commit();
    // }




    }
}
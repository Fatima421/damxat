package com.example.damxat.Views.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import com.example.damxat.R;
import com.example.damxat.Views.Fragments.GroupsFragment;
import com.example.damxat.Views.Fragments.MyXatsFragment;
import com.example.damxat.Views.Fragments.UserFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    FirebaseUser firebaseUser;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyXatsFragment()).commit();

        // Here we get the current user from firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.top_menu);


        BottomNavigationView bottomNav = findViewById(R.id.main_menu);

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;

            switch (item.getItemId()){
                case R.id.nav_xats:
                    selectedFragment = new MyXatsFragment();
                    break;

                case R.id.nav_group:
                    selectedFragment = new GroupsFragment();
                    break;

                case R.id.nav_users:
                    selectedFragment = new UserFragment();

                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        });
    }

    private void status(String status){
        // Here we get the reference to the Users collection from firebase and then set the status for a user
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        ref.updateChildren(hashMap);
    }

    // If the app is resumed we set the status as online
    @Override
    protected void onResume(){
        super.onResume();
        status("online");
    }

    // If the app is on pause we set the status as offline
    @Override
    protected void onPause(){
        super.onPause();
        status("offline");
    }

    // When the screen is created the inflate the menu view
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    // When the only option in the menu is clicked (the logout) it signs out from the firebase auth instance and sends the user to the StartActivity screen
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("TESTMENU", "hola" + item.getItemId());
        switch(item.getItemId()){
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                finish();
                return true;
        }
        return false;
    }
}
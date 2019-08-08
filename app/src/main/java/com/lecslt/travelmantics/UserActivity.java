package com.lecslt.travelmantics;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    /*ArrayList<TravelDeal> Deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private ChildEventListener myChildListener;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        MenuItem userMenu = menu.findItem(R.id.user_menu);
        if (FirebaseUtil.isAdmin == true){
            userMenu.setVisible(true);
        }
        else{
            userMenu.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.user_menu :
               Intent intent = new Intent(this, AdminActivity.class);
               startActivity(intent);
                return true;
            case R.id.logout_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("Logout", "User log out");// ...
                                FirebaseUtil.attachListener();
                            }
                        });
            default:
                return  super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detachListener();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseUtil.openFbReference("traveldeals");
        //setup our RecyclerView
        RecyclerView RvDeals = findViewById(R.id.Deals_Rv);
        final DealAdapter adapter = new DealAdapter();
        RvDeals.setAdapter(adapter);
        LinearLayoutManager DealsLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        RvDeals.setLayoutManager(DealsLayoutManager);
        FirebaseUtil.detachListener();
    }

    public void showMenu(){
        invalidateOptionsMenu(); //say to android that the content of the menu has changed.
    }
}

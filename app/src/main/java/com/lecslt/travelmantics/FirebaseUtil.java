package com.lecslt.travelmantics;

import android.app.Activity;
import android.app.ListActivity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {

    private static final int RC_SIGN_IN = 123 ;
    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference myRef;
    private static  FirebaseUtil firebaseUtil;
    private static FirebaseAuth mFirebaseAuth;
    private static FirebaseAuth.AuthStateListener myAuthListenner;
    public static FirebaseStorage mStorage;
    public static StorageReference mStorageRef;
    public static ArrayList<TravelDeal> mDeals;
    //private static ListActivity caller;
    public static boolean isAdmin;

    //default constructor
    private FirebaseUtil(){};

    public static void openFbReference(String ref){ //, final ListActivity callerActivity/ we can not an activity from outside an activity/ to initialise the class, let's create an activity called caller
        if (firebaseUtil == null ){
            firebaseUtil = new FirebaseUtil();
            mFirebaseDatabase = FirebaseDatabase.getInstance();
            mFirebaseAuth = FirebaseAuth.getInstance();
           // caller = callerActivity;

            // determine if user is log in or not
            myAuthListenner = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null){   // control if there is a user log in or not
                        FirebaseUtil.signIn();
                    }

                    //Toast.makeText(callerActivity.getBaseContext(), "Welcome back", Toast.LENGTH_LONG).show();
                }

            };
            connectStorage();

        }
        mDeals = new ArrayList<TravelDeal>();
        myRef = mFirebaseDatabase.getReference().child(ref);
    }

    private static  void signIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
            /*caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);*/
    }

    public static  void checkAdmin(String uid){
        FirebaseUtil.isAdmin = false;
        DatabaseReference ref = mFirebaseDatabase.getReference().child("administrators").child(uid);

        ChildEventListener listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.isAdmin = true;
                //Log.d("Admin", "you are an administrator");
                //caller.showMenu;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        ref.addChildEventListener(listener); //set the childeventlistener to the database reference.
    }

    // two method to manipulate the authListener
    public static void attachListener(){
        mFirebaseAuth.addAuthStateListener(myAuthListenner);
    }

    public static void detachListener(){
        mFirebaseAuth.removeAuthStateListener(myAuthListenner);
    }

    public static void connectStorage(){
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference().child("deals_pictures");
    }
}

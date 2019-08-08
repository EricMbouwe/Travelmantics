package com.lecslt.travelmantics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

import static com.lecslt.travelmantics.FirebaseUtil.openFbReference;

public class DealAdapter extends  RecyclerView.Adapter<DealAdapter.DealViewHolder>{

    ArrayList<TravelDeal> Deals;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private ChildEventListener myChildListener;
    ImageView imageDeal;

    public DealAdapter(){

        openFbReference("traveldeals");
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        myRef = FirebaseUtil.myRef;
        Deals = FirebaseUtil.mDeals;
        myChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                TravelDeal td = dataSnapshot.getValue(TravelDeal.class);
                Log.d("deal", td.getTitle());
                td.setId(dataSnapshot.getKey());
                Deals.add(td);
                notifyItemInserted(Deals.size()-1);
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
        myRef.addChildEventListener(myChildListener);
    }

    @NonNull
    @Override
    public DealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.rv_row, parent, false);
        return new DealViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DealViewHolder holder, int position) {
        TravelDeal deal = Deals.get(position);
        holder.bind(deal);
    }

    @Override
    public int getItemCount() {
        return Deals.size();
    }

    public class DealViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

        TextView tripTitle;
        TextView tripPrice;
        TextView tripDescription;

        //DealViewHolder constructor
        public DealViewHolder(@NonNull View itemView) {
            super(itemView);
            tripTitle = itemView.findViewById(R.id.tripTitle);
            tripDescription = itemView.findViewById(R.id.tripDescription);
            tripPrice = itemView.findViewById(R.id.tripPrice);
            imageDeal = itemView.findViewById(R.id.imageDeal);
            itemView.setOnClickListener(this);
        }

        public void bind (TravelDeal deal){
            tripTitle.setText(deal.getTitle());
            tripPrice.setText(deal.getPrice());
            tripDescription.setText(deal.getDescription());
            showImage(deal.getImageUrl());
        }


        // get the change when deal press in the RecyclerView
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Log.d("Click", String.valueOf(position));
            TravelDeal selectedDeal = Deals.get(position);
            Intent intent = new Intent(itemView.getContext(), UserActivity.class); //call the User activity class
            intent.putExtra("Deal", selectedDeal); // the Taveldeal class have to implements Serializable to use the putExtra method
            itemView.getContext().startActivity(intent); // start the UserActivity from the current context of the current view
        }

        private void showImage(String url){
            if ( url !=null && url.isEmpty() == false){
                int width = Resources.getSystem().getDisplayMetrics().widthPixels;
                Picasso.with(imageDeal.getContext())
                        .load(url)
                        .resize(80, 80)
                        .centerCrop()
                        .into(imageDeal);
            }
        }
    }

}

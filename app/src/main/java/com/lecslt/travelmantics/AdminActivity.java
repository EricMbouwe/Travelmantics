package com.lecslt.travelmantics;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class AdminActivity extends AppCompatActivity {

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private static final int PICTURE_RESULT = 42;

    EditText txtTitle;
    EditText txtPrice;
    EditText txtDescription;
    ImageView imageView;
    TravelDeal deal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //FirebaseUtil.openFbReference("traveldeals", this);
        mFirebaseDatabase = FirebaseUtil.mFirebaseDatabase;
        myRef = FirebaseUtil.myRef;

        txtTitle = findViewById(R.id.txtTitle);
        txtPrice = findViewById(R.id.txtPrice);
        txtDescription = findViewById(R.id.txtDesription);
        imageView = findViewById(R.id.image);

        // we should received the view of TravelDeal class and show it to the user

        Intent intent = getIntent();
        TravelDeal deal = (TravelDeal) intent.getSerializableExtra("Deal");

        if (deal == null) {
            deal = new TravelDeal();
        }

        this.deal = deal; // put the variable deal into the deal member;
        txtTitle.setText(deal.getTitle());
        txtDescription.setText(deal.getDescription());
        txtPrice.setText(deal.getPrice());
        showImage(deal.getImageUrl()); // show the image when the activity is created;

        // upload an image by a click on the button
        Button btnImage = findViewById(R.id.btnImage);
        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(intent.createChooser(intent, "insert picture"), 42);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_menu:
                saveDeals();
                Toast.makeText(this, "Deal saved", Toast.LENGTH_LONG).show();
                clean();
                backToList();
                return true;
            case R.id.delete_menu:
                deleteDeal();
                Toast.makeText(this, "Deal deleted", Toast.LENGTH_LONG).show();
                backToList();
                return true;
            default:
                return  super.onOptionsItemSelected(item);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        if (FirebaseUtil.isAdmin){                 // set authorization for the different users
            menu.findItem(R.id.delete_menu).setVisible(true);
            menu.findItem(R.id.save_menu).setVisible(true);
            enableTexts(true);
        }
        else {
            menu.findItem(R.id.delete_menu).setVisible(false);
            menu.findItem(R.id.save_menu).setVisible(false);
            enableTexts(false);
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT && resultCode == RESULT_OK){
            Uri imageUri = data.getData();
            StorageReference storef = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());

            //upload the image url to the Firebase database
            storef.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String url = taskSnapshot.getMetadata().toString(); // .getDownloadUrl() method depreciated
                    String pictureName = taskSnapshot.getStorage().getPath();
                    deal.setImageUrl(url);
                    deal.setImageName(pictureName);
                    Log.d("Name", url);
                    Log.d("Name", pictureName);
                    showImage(url); // show image add by the user to the travel deal;
                }
            });
        }
    }

    private void saveDeals(){
        /*String title = txtTitle.getText().toString();
        String price = txtPrice.getText().toString();
        String description = txtDescription.getText().toString();
        TravelDeal deal = new TravelDeal(title,description,price, "");
        */

        deal.setTitle(txtTitle.getText().toString());
        deal.setDescription(txtDescription.getText().toString());
        deal.setPrice(txtPrice.getText().toString());

        // test if deql exist or is a new one
        if (deal.getId() == null) {
            myRef.push().setValue(deal);
        }
        else {
            myRef.child(deal.getId()).setValue(deal);
        }
    }

    // delete deal method
    private void deleteDeal(){
        if (deal == null){
            Toast.makeText(this, "Please save the deal before deleting", Toast.LENGTH_LONG).show();
            return;
        }
        myRef.child(deal.getId()).removeValue();
        if ( deal.getImageName() != null && deal.getImageName().isEmpty() == false){
            StorageReference picRef = FirebaseUtil.mStorage.getReference().child(deal.getImageName());
            picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                   Log.d("Delete Image", "Image successfully deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                  Log.d("Delete Image", e.getMessage());
                }
            });
        }
    }

    // back to deals list method
    private  void backToList(){
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }

    private void clean(){
        txtTitle.setText("");
        txtDescription.setText("");
        txtPrice.setText("");
    }

    private void enableTexts(boolean isEnable){
        txtTitle.setEnabled(isEnable);
        txtDescription.setEnabled(isEnable);
        txtPrice.setEnabled(isEnable);
    }

    private void showImage(String url){
        if ( url !=null && url.isEmpty() == false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.with(this)
                    .load(url)
                    .resize(width, width*2/3)
                    .centerCrop()
                    .into(imageView);
        }
    }
}

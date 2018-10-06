package com.androidapp.scalar.hailee;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidapp.scalar.hailee.models.customer;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class CustomerDetailsEditActivity extends AppCompatActivity {

    private ImageView mUserProfilePic;
    private EditText mCustomerName,mCustomerEmail,mCustomerMobile;
    private CircularProgressButton mSaveBtn,mResetBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;

    private Uri resultUri;
    private String userID;
    private String mName = null,mEmail = null,mPhone = null,mProfileImageUrl =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details_edit);

        viewUserInformation();
        setUserProfilePic();
        saveCustomerInfo();
    }

    protected void setUserProfilePic(){
        mUserProfilePic = (ImageView) findViewById(R.id.userProfilePicEdit);

        mUserProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);

                intent.setType("image/*");

                startActivityForResult(intent, 1);
            }
        });
    }

    private void viewUserInformation(){

        mCustomerName = (EditText) findViewById(R.id.customer_name);
        mCustomerEmail = (EditText) findViewById(R.id.customer_email);
        mCustomerMobile = (EditText) findViewById(R.id.customer_mobileNum);
        mUserProfilePic = (ImageView) findViewById(R.id.userProfilePicEdit);

        mAuth = FirebaseAuth.getInstance();

        userID = mAuth.getCurrentUser().getUid();

        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);

        mCustomerDatabase.child("user details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>)dataSnapshot.getValue();

                    if(map.get("customerName") != null){
                        mName = map.get("customerName").toString();
                        mCustomerName.setText(mName);
                    }

                    if(map.get("customerEmail") != null){
                        mEmail = map.get("customerEmail").toString();
                        mCustomerEmail.setText(mEmail);
                    }

                    if(map.get("customerMobile") != null){
                        mPhone = map.get("customerMobile").toString();
                        mCustomerMobile.setText(mPhone);
                    }

                    if(map.get("customerProfileImageUrl") != null){
                        mProfileImageUrl = map.get("customerProfileImageUrl").toString();

                        Glide.with(getApplication()).load(mProfileImageUrl).into(mUserProfilePic);
                    }



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void saveCustomerInfo(){



        mSaveBtn = (CircularProgressButton) findViewById(R.id.saveUserData);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCustomerName = (EditText) findViewById(R.id.customer_name);
                mCustomerEmail = (EditText) findViewById(R.id.customer_email);
                mCustomerMobile = (EditText) findViewById(R.id.customer_mobileNum);
                mUserProfilePic = (ImageView) findViewById(R.id.userProfilePicEdit);

                mName = mCustomerName.getText().toString();
                mEmail = mCustomerEmail.getText().toString();
                mPhone = mCustomerMobile.getText().toString();
                mProfileImageUrl = mUserProfilePic.toString();

                if (mName == null || mEmail.isEmpty() || mPhone.isEmpty() || mProfileImageUrl.isEmpty()) {
                    Toast.makeText(CustomerDetailsEditActivity.this, "Please fill required fields", Toast.LENGTH_SHORT).show();
                } else if (!mEmail.contains("@")) {
                    Toast.makeText(CustomerDetailsEditActivity.this, "Please enter valid email address", Toast.LENGTH_SHORT).show();
                } else {

                    AlertDialog.Builder logout_builder = new AlertDialog.Builder(CustomerDetailsEditActivity.this);
                    logout_builder.setMessage("Do you want to save changes ?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    @SuppressLint("StaticFieldLeak") final AsyncTask<String, String, String> nextActivity = new AsyncTask<String, String, String>() {
                                        @Override
                                        protected String doInBackground(String... strings) {

                                            try {
                                                Thread.sleep(2000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                            mAuth = FirebaseAuth.getInstance();

                                            userID = mAuth.getCurrentUser().getUid();

                                            mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID).child("user details");



                                            Map userInfo = new HashMap();

                                            userInfo.put("customerName",mName);
                                            userInfo.put("customerEmail",mEmail);
                                            userInfo.put("customerMobile",mPhone);

                                            customer newCustomer = new customer(mName,mEmail,mPhone,mProfileImageUrl);

                                            mCustomerDatabase.updateChildren(userInfo);

                                            if(resultUri != null){
                                                StorageReference customerImageFilePath = FirebaseStorage.getInstance().getReference().child("customer_profile_images").child(userID);
                                                Bitmap bitmap = null;

                                                try{
                                                    bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 20 ,byteArrayInputStream);

                                                byte[] data = byteArrayInputStream.toByteArray();
                                                UploadTask uploadTask = customerImageFilePath.putBytes(data);

                                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        finish();
                                                    }
                                                });

                                                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        Uri downloadUrl = taskSnapshot.getDownloadUrl();

                                                        Map newProfileImage = new HashMap();

                                                        newProfileImage.put("customerProfileImageUrl", downloadUrl.toString());

                                                        mCustomerDatabase.updateChildren(newProfileImage);

                                                        finish();
                                                    }
                                                });


                                            }

                                            return "done";
                                        }

                                        @Override
                                        protected void onPostExecute(String s) {
                                            if(s.equals("done")) {
                                                Toast.makeText(CustomerDetailsEditActivity.this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(CustomerDetailsEditActivity.this, CustomerProfileActivity.class);
                                                startActivity(intent);
                                                mSaveBtn.revertAnimation();
                                            }
                                        }
                                    };
                                    mSaveBtn.startAnimation();
                                    nextActivity.execute();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    mSaveBtn.revertAnimation();
                                }
                            });

                    AlertDialog logout_alertDialog = logout_builder.create();
                    logout_alertDialog.setTitle("Saving Changes");
                    logout_alertDialog.show();






                }
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK){

            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mUserProfilePic.setImageURI(resultUri);

        }
    }
}

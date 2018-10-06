package com.androidapp.scalar.hailee.service_pack;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by pasin on 10/2/2018.
 */


public class AuthenticationController {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;

    private int status;

    public int checkUserRole(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {

            FirebaseDatabase.getInstance().getReference().child("roles").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    String role = dataSnapshot.getValue(String.class);

                    if (role.equals("customer")) {

                        status = 1;

                    } else if(role.equals("driver")){
                        status = 2;
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }


        return status;
    }


}

package com.androidapp.scalar.hailee;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Payment extends AppCompatActivity {

    RadioGroup radioPaymentMethodGroup;
    Button submitButton;
    RadioButton paymentMethodSelectedRadioButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerReference;
    private String userID;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        submitButton = (Button) findViewById(R.id.paymentMethodSubmitButton);
        radioPaymentMethodGroup = (RadioGroup) findViewById(R.id.paymentMethodRadioGroup);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        submitButton.setEnabled(false);
        submitButton.setText("Loading...");


        mCustomerReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);
        mCustomerReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                submitButton.setEnabled(true);
                submitButton.setText("Submit");

                if(dataSnapshot.child("name").exists()){
                    userName = dataSnapshot.child("name").getValue().toString();
                    if (dataSnapshot.child("paymentMethod").exists()) {
                        String paymentMethod  = dataSnapshot.child("paymentMethod").getValue().toString();
                        int selectedRadioId = R.id.radioButtonCash;
                        if (paymentMethod.equals("Cash")){
                            radioPaymentMethodGroup.check(R.id.radioButtonCash);
                        } else {
                            radioPaymentMethodGroup.check(R.id.radioButtonCredit);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioPaymentMethodGroup.getCheckedRadioButtonId();
                paymentMethodSelectedRadioButton = (RadioButton)findViewById(selectedId);
                String radioText = paymentMethodSelectedRadioButton.getText().toString();

                mCustomerReference.child("paymentMethod").setValue(radioText);
                if (radioText.equals("Cash")) {
                    finish();
                    //Toast.makeText(view.getContext(),"Payment Method Selected : Cash"  ,Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(view.getContext(), creditCardDetailsView.class);
                    startActivity(intent);
                    //Toast.makeText(view.getContext(),"Payment Method Selected : Credit card"  ,Toast.LENGTH_SHORT).show();



                }

                //Toast.makeText(view.getContext(),"fsfsfsf : " + radioText +" : " +userName ,Toast.LENGTH_LONG).show();
            }

        });


    }



    public void buttonOnClick(View view){

        //Intent intent = new Intent(this, creditCardDetailsView.class);
        //startActivity(intent);

    }

    public void buttonOnClick2(View view){
        Toast.makeText(this, "Credit Card entered Sucsessfully",
                Toast.LENGTH_LONG).show();
    }
}

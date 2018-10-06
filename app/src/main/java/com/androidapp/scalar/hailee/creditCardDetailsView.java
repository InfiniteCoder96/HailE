package com.androidapp.scalar.hailee;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidapp.scalar.hailee.models.CreditCard;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class creditCardDetailsView extends AppCompatActivity {

    private Button submitButton;
    private Button deleteButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerReference;
    private String userID;
    private CreditCard creditCard;

    private ConstraintLayout backgroundLayout;

    private EditText holderNameEditText, cardNumberEditText, yearEditText, monthEditText, ccvEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCustomerReference = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userID);

        Intent intent1 = getIntent();
        submitButton = (Button) findViewById(R.id.creditdetailssubmit);
        deleteButton = (Button) findViewById(R.id.creditDetailsDeleteButton);
        holderNameEditText = (EditText)findViewById(R.id.holderNameEditText);
        cardNumberEditText = (EditText)findViewById(R.id.cardNumberEditText);
        yearEditText = (EditText)findViewById(R.id.yearEditText);
        monthEditText = (EditText)findViewById(R.id.monthEditText);
        ccvEditText = (EditText)findViewById(R.id.ccvEditText);
        backgroundLayout = (ConstraintLayout)findViewById(R.id.backgroundLayout);

//
//        EditText cardHolderNameText = (EditText)findViewById(R.id.holdernametext);
//
//        String holderNameText = cardHolderNameText.getText().toString();
        //Toast.makeText(this,"Cash"+holderNameText  ,Toast.LENGTH_LONG).show();
        // mCustomerReference.child("card holder name").setValue(holdernNameText);


        mCustomerReference.child("creditCard").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    creditCard = new CreditCard(
                            dataSnapshot.child("holderName").getValue().toString(),
                            dataSnapshot.child("cardNumber").getValue().toString(),
                            Integer.parseInt(dataSnapshot.child("expiryMonth").getValue().toString()),
                            Integer.parseInt(dataSnapshot.child("expiryYear").getValue().toString()),
                            Integer.parseInt(dataSnapshot.child("ccv").getValue().toString())
                    );
                    holderNameEditText.setText(creditCard.getHolderName());
                    cardNumberEditText.setText(creditCard.getCardNumber());
                    monthEditText.setText(creditCard.getExpiryMonth() + "");
                    yearEditText.setText(creditCard.getExpiryYear() + "");
                    ccvEditText.setText(creditCard.getCcv() + "");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if (
                        (holderNameEditText.getText().toString().equals("")) ||
                        (cardNumberEditText.getText().toString().equals("")) ||
                        (monthEditText.getText().toString().equals("")) ||
                        (yearEditText.getText().toString().equals("")) ||
                        (ccvEditText.getText().toString().equals("") ) )  {
                    Toast.makeText(view.getContext(),"Please fill all the details" ,Toast.LENGTH_SHORT).show();
                } else {
                    creditCard = new CreditCard(
                            (holderNameEditText.getText().toString()),
                            (cardNumberEditText.getText().toString()),
                            (Integer.parseInt(monthEditText.getText().toString())),
                            (Integer.parseInt(yearEditText.getText().toString())),
                            (Integer.parseInt(ccvEditText.getText().toString())));

                    mCustomerReference.child("creditCard").setValue(creditCard);
                    Toast.makeText(view.getContext(),"Successfully Updated" ,Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(view.getContext(), CustomerMapActivity.class);
                    startActivity(intent);
                }


            }

        });

        backgroundLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view){

            }
        });




    }
}
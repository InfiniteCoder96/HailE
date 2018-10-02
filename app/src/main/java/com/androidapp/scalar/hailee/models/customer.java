package com.androidapp.scalar.hailee.models;

import java.util.List;

/**
 * Created by pasin on 9/28/2018.
 */

public class customer extends user {

    private List creditCardDetails;

    public customer(String email, String password, String name,List creditCardDetails) {
        super(email, password, name);
        this.setCreditCardDetails(creditCardDetails);
    }

    public List getCreditCardDetails() {
        return creditCardDetails;
    }

    public void setCreditCardDetails(List creditCardDetails) {
        this.creditCardDetails = creditCardDetails;
    }
}

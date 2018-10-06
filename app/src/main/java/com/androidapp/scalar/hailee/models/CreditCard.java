package com.androidapp.scalar.hailee.models;

/**
 * Created by Vidushan on 23/9/2018.
 */

public class CreditCard {
    private String holderName;
    private String cardNumber;
    private int expiryMonth;
    private int expiryYear;
    private int ccv;

    public CreditCard(String holderName, String cardNumber, int expiryMonth, int expiryYear, int ccv) {
        this.holderName = holderName;
        this.cardNumber = cardNumber;
        this.expiryMonth = expiryMonth;
        this.expiryYear = expiryYear;
        this.ccv = ccv;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public int getExpiryMonth() {
        return expiryMonth;
    }

    public int getExpiryYear() {
        return expiryYear;
    }

    public int getCcv() {
        return ccv;
    }

    //    public CreditCard(String holderName, String cardNumber, int expiryMonth, int expiryYear, int ccv){
//        this
//    }


}

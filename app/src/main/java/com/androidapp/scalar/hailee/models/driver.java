package com.androidapp.scalar.hailee.models;

/**
 * Created by pasin on 9/28/2018.
 */

public class driver extends user {

    private String vehicleRegNum;
    private String vehicleType;


    public driver(String email,String password,String name,String vehicleRegNum,String vehicleType){
        super(email,password,name);
        this.setVehicleRegNum(vehicleRegNum);
        this.setVehicleType(vehicleType);
    }

    public String getVehicleRegNum() {
        return vehicleRegNum;
    }

    public void setVehicleRegNum(String vehicleRegNum) {
        this.vehicleRegNum = vehicleRegNum;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}

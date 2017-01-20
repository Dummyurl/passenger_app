package com.passengerapp.main.network.model.response;

import java.io.Serializable;

/**
 * Created by adventis on 10/15/15.
 */
public class VerfiyCouponData implements Serializable{
    public boolean IsValid;
    public String DiscountType;
    public boolean IsSingle;
    public float Amount;
    public String CouponValue;
    public String MerchantId;

}

package com.gripxtech.kasimrangwala.grophers.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class CartItem extends ProductItem {

    protected String cartID;
    protected String userID;
    protected String quantity;
    protected String delivery;
    protected String sellerID;
    protected String sellerShopName;
    // protected String sellerInfo;
    // protected String sellerShopDesc;
    // protected String sellerCountry;
    // protected String sellerState;
    // protected String sellerCity;
    // protected String sellerStreet;
    protected String sellerRating;
    protected String sellerImage;
    // protected String sellerMobile;

    public CartItem(String jsonString) {
        super(jsonString);
        try {
            JSONObject object = new JSONObject(jsonString);
            cartID = object.optString("cartID");
            userID = object.optString("userID");
            quantity = object.optString("quantity");
            delivery = object.optString("delivery");
            sellerID = object.optString("sellerID");
            sellerShopName = object.optString("sellerShopName");
            sellerRating = object.optString("sellerRating");
            sellerImage = object.optString("sellerImage");
        } catch (JSONException e) {
            Log.e("CartItem", "CartItem(): JSONException: " + e.getMessage());
        }
    }

    public CartItem(String id, String logo, String name, String shortDesc, String desc,
                    String catID, String subCatID, String code, String sku, String discount,
                    String size, String mrp, String rate, String notification, String weight,
                    String vendorID, String todayOffer, String featureProductID, String status,
                    String hot, String bestSeller, String shippingCharge, String pinCode,
                    String deliveryTime,
                    String cartID, String userID, String quantity, String delivery,
                    String sellerID, String sellerShopName, String sellerRating,
                    String sellerImage) {
        super(id, logo, name, shortDesc, desc, catID, subCatID, code, sku, discount, size, mrp,
                rate, notification, weight, vendorID, todayOffer, featureProductID, status, hot,
                bestSeller, shippingCharge, pinCode, deliveryTime);
        this.cartID = cartID;
        this.userID = userID;
        this.quantity = quantity;
        this.delivery = delivery;
        this.sellerID = sellerID;
        this.sellerShopName = sellerShopName;
        this.sellerRating = sellerRating;
        this.sellerImage = sellerImage;
    }

    public String getCartID() {
        return cartID;
    }

    public String getUserID() {
        return userID;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getDelivery() {
        return delivery;
    }

    public String getSellerID() {
        return sellerID;
    }

    public String getSellerShopName() {
        return sellerShopName;
    }

    public String getSellerRating() {
        return sellerRating;
    }

    public String getSellerImage() {
        return sellerImage;
    }

    @Override
    public String toString() {
        String string = super.toString();
        try {
            JSONObject object = new JSONObject(string);
            object.put("cartID", getCartID());
            object.put("userID", getUserID());
            object.put("quantity", getQuantity());
            object.put("delivery", getDelivery());
            object.put("sellerID", getSellerID());
            object.put("sellerShopName", getSellerShopName());
            object.put("sellerRating", getSellerRating());
            object.put("sellerImage", getSellerImage());
            return object.toString();
        } catch (JSONException e) {
            Log.e("CartItem", "toString(): JSONException: " + e.getMessage());
        }
        return string;
    }
}

package com.gripxtech.kasimrangwala.grophers.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class ProductItem {

    protected String id;
    protected String logo;
    protected String name;
    protected String shortDesc;
    protected String desc;
    protected String catID;
    protected String subCatID;
    protected String code;
    protected String sku;
    protected String discount;
    protected String size;
    protected String mrp;
    protected String rate;
    protected String notification;
    protected String weight;
    protected String vendorID;
    protected String todayOffer;
    protected String featureProductID;
    protected String status;
    protected String hot;
    protected String bestSeller;
    protected String shippingCharge;
    protected String pinCode;
    protected String deliveryTime;

    public ProductItem(String jsonString) {
        try {
            JSONObject object = new JSONObject(jsonString);
            id = object.optString("id");
            logo = object.optString("logo");
            name = object.optString("name");
            shortDesc = object.optString("shortDesc");
            desc = object.optString("desc");
            catID = object.optString("catID");
            subCatID = object.optString("subCatID");
            code = object.optString("code");
            sku = object.optString("sku");
            discount = object.optString("discount");
            size = object.optString("size");
            mrp = object.optString("mrp");
            rate = object.optString("rate");
            notification = object.optString("notification");
            weight = object.optString("weight");
            vendorID = object.optString("vendorID");
            todayOffer = object.optString("todayOffer");
            featureProductID = object.optString("featureProductID");
            status = object.optString("status");
            hot = object.optString("hot");
            bestSeller = object.optString("bestSeller");
            shippingCharge = object.optString("shippingCharge");
            pinCode = object.optString("pinCode");
            deliveryTime = object.optString("deliveryTime");
        } catch (JSONException e) {
            Log.e("ProductItem", "ProductItem(): JSONException: " + e.getMessage());
        }
    }

    public ProductItem(String id, String logo, String name, String shortDesc, String desc,
                       String catID, String subCatID, String code, String sku, String discount,
                       String size, String mrp, String rate, String notification, String weight,
                       String vendorID, String todayOffer, String featureProductID, String status,
                       String hot, String bestSeller, String shippingCharge, String pinCode,
                       String deliveryTime) {
        this.id = id;
        this.logo = logo;
        this.name = name;
        this.shortDesc = shortDesc;
        this.desc = desc;
        this.catID = catID;
        this.subCatID = subCatID;
        this.code = code;
        this.sku = sku;
        this.discount = discount;
        this.size = size;
        this.mrp = mrp;
        this.rate = rate;
        this.notification = notification;
        this.weight = weight;
        this.vendorID = vendorID;
        this.todayOffer = todayOffer;
        this.featureProductID = featureProductID;
        this.status = status;
        this.hot = hot;
        this.bestSeller = bestSeller;
        this.shippingCharge = shippingCharge;
        this.pinCode = pinCode;
        this.deliveryTime = deliveryTime;

    }

    public String getId() {
        return id;
    }

    public String getLogo() {
        return logo;
    }

    public String getName() {
        return name;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public String getDesc() {
        return desc;
    }

    public String getCatID() {
        return catID;
    }

    public String getSubCatID() {
        return subCatID;
    }

    public String getCode() {
        return code;
    }

    public String getSku() {
        return sku;
    }

    public String getDiscount() {
        return discount;
    }

    public String getSize() {
        return size;
    }

    public String getMrp() {
        return mrp;
    }

    public String getRate() {
        return rate;
    }

    public String getNotification() {
        return notification;
    }

    public String getWeight() {
        return weight;
    }

    public String getVendorID() {
        return vendorID;
    }

    public String getTodayOffer() {
        return todayOffer;
    }

    public String getFeatureProductID() {
        return featureProductID;
    }

    public String getStatus() {
        return status;
    }

    public String getHot() {
        return hot;
    }

    public String getBestSeller() {
        return bestSeller;
    }

    public String getShippingCharge() {
        return shippingCharge;
    }

    public String getPinCode() {
        return pinCode;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    @Override
    public String toString() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", getId());
            object.put("logo", getLogo());
            object.put("name", getName());
            object.put("shortDesc", getShortDesc());
            object.put("desc", getDesc());
            object.put("catID", getCatID());
            object.put("subCatID", getSubCatID());
            object.put("code", getCode());
            object.put("sku", getSku());
            object.put("discount", getDiscount());
            object.put("size", getSize());
            object.put("mrp", getMrp());
            object.put("rate", getRate());
            object.put("notification", getNotification());
            object.put("weight", getWeight());
            object.put("vendorID", getVendorID());
            object.put("todayOffer", getTodayOffer());
            object.put("featureProductID", getFeatureProductID());
            object.put("status", getStatus());
            object.put("hot", getHot());
            object.put("bestSeller", getBestSeller());
            object.put("shippingCharge", getShippingCharge());
            object.put("pinCode", getPinCode());
            object.put("deliveryTime", getDeliveryTime());
        } catch (JSONException e) {
            Log.e("ProductItem", "toString(): JSONException: " + e.getMessage());
        }
        return object.toString();
    }
}

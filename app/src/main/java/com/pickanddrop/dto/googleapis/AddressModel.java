package com.pickanddrop.dto.googleapis;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by admin on 06-03-2017.
 */

public class AddressModel {
    String latitude, longitude, searchAddress, addressType, addressName, id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getSearchAddress() {
        return searchAddress;
    }

    public void setSearchAddress(String searchAddress) {
        this.searchAddress = searchAddress;
    }

    public static AddressModel getModelFromJsonString(Context context, String locationList) {
        AddressModel mModel = new AddressModel();
        try {
            JSONArray mArray = new JSONArray(locationList);
            if (mArray.length() > 0) {
                for (int i = 0; i < mArray.length(); i++) {
                    JSONObject addressObject = mArray.getJSONObject(i);
                    mModel.setId(addressObject.getString("id"));
                    mModel.setAddressType(addressObject.getString("Type"));
                    mModel.setAddressName(addressObject.getString("favourite_name"));
                    mModel.setSearchAddress(addressObject.getString("location_address"));
                    mModel.setLatitude(addressObject.getString("latitude"));
                    mModel.setLongitude(addressObject.getString("langitude"));
                }
            } else {
                Toast.makeText(context, "No saved favourite location found.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mModel;
    }
}

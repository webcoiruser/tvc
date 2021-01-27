package com.pickanddrop.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pickanddrop.dto.LoginDTO;

import java.lang.reflect.Type;

public class AppSession {

    //Declaration of variables
    private static final String SESSION_NAME = "com.tvc.utils";
    private static final String APP_DEFAULT_LANGUAGE = "en";
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor prefsEditor;

    //Constructor
    public AppSession(Context context) {
        mSharedPreferences = context.getSharedPreferences(SESSION_NAME,
                Context.MODE_PRIVATE);
        prefsEditor = mSharedPreferences.edit();
    }

    /*
    public UrlDto getUrls() {
        String urlsJSONString = mSharedPreferences.getString("urls", "");
        if (urlsJSONString == null)
            return null;
        Type type = new TypeToken<UrlDto>() {
        }.getType();
        UrlDto urlDTO = new Gson().fromJson(urlsJSONString, type);
        return urlDTO;
    }

    public void setUrls(UrlDto urlDTO) {
        prefsEditor = mSharedPreferences.edit();
        if (urlDTO == null)
            prefsEditor.putString("urls", null);
        else {
            String urlsJSONString = new Gson().toJson(urlDTO);
            prefsEditor.putString("urls", urlsJSONString);
        }
        prefsEditor.commit();
    }
*/


    public String getNotificationData(){
        return mSharedPreferences.getString("notifidata", "");

    }

    public void setNotificationData(String notidata) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("notifidata", notidata);
        prefsEditor.commit();
    }

    // nandha

    public String getDrop() {
        return mSharedPreferences.getString("pickup","");
    }

    public void setDrop(String pickup){
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("pickup",pickup);
        prefsEditor.commit();
    }

    public String getPick(){
        return mSharedPreferences.getString("drop","");
    }

    public void setPick(String drop){
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("drop",drop);
        prefsEditor.commit();
    }


    public String getNumber(){
        return mSharedPreferences.getString("number1","");
    }

    public void setNumber(String number){
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("number1",number);
        prefsEditor.commit();
    }

    public String getName(){
        return mSharedPreferences.getString("dropname","");
    }

    public void setName(String dropname){
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("dropname",dropname);
        prefsEditor.commit();
    }

    public String getMulPrice(){
        return mSharedPreferences.getString("mulprice","");
    }

    public void setMulPrice(String mulprice){
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("mulprice",mulprice);
        prefsEditor.commit();
    }

    public String getTotalcost(){
        return mSharedPreferences.getString("totalcost","");
    }

    public void setTotalcost(String totalcost){
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("totalcost",totalcost);
        prefsEditor.commit();
    }



    public String getJSonArrayString() {
        return mSharedPreferences.getString("jsonArray","");
    }

    public void setJSonArrayString(String jSonArrayString) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("jsonArray",jSonArrayString);
        prefsEditor.commit();
    }
    //END








    public String getLanguage() {
        return mSharedPreferences.getString("getLanguage", APP_DEFAULT_LANGUAGE);
    }

    public void setLanguage(String language) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getLanguage", language);
        prefsEditor.commit();
    }

    public String getResetId() {
        return mSharedPreferences.getString("getResetId", APP_DEFAULT_LANGUAGE);
    }

    public void setResetId(String resetid) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getResetId", resetid);
        prefsEditor.commit();
    }

    public String getLatitude() {
        return mSharedPreferences.getString("getLatitude", "");
    }


    public void setLatitude(String getLatitude) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getLatitude", getLatitude);
        prefsEditor.commit();
    }

    public String getLongitude() {
        return mSharedPreferences.getString("getLogitude", "");
    }


    public void setLongitude(String getLogitude) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getLogitude", getLogitude);
        prefsEditor.commit();
    }

    public LoginDTO getUser() {
        String userJSONString = mSharedPreferences.getString("getUser", "");
        if (userJSONString == null)
            return null;
        Type type = new TypeToken<LoginDTO>() {
        }.getType();
        LoginDTO loginDTO = new Gson().fromJson(userJSONString, type);
        return loginDTO;
    }

    public void setUser(LoginDTO loginDTO) {
        prefsEditor = mSharedPreferences.edit();
        if (loginDTO == null)
            prefsEditor.putString("getUser", null);
        else {
            String userJSONString = new Gson().toJson(loginDTO);
            prefsEditor.putString("getUser", userJSONString);
        }
        prefsEditor.commit();
    }


    public boolean isRememberMe() {
        return mSharedPreferences.getBoolean("isRememberMe", false);

    }

    public void setRememberMe(boolean isRememberMe) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putBoolean("isRememberMe", isRememberMe);
        prefsEditor.commit();
    }

    public String getPassword() {
        return mSharedPreferences.getString("getPassword", "");
    }

    public void setPassword(String password) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getPassword", password);
        prefsEditor.commit();
    }


    public String getLoginId() {
        return mSharedPreferences.getString("getLoginId", "");
    }

    public void setLoginId(String loginId) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getLoginId", loginId);
        prefsEditor.commit();
    }


    public boolean isLogin() {
        return mSharedPreferences.getBoolean("Login", false);
    }

    public void setLogin(boolean Login) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putBoolean("Login", Login);
        prefsEditor.commit();
    }

    public boolean isLoginUser() {
        return mSharedPreferences.getBoolean("Login_user", false);
    }

    public boolean isMultipleBack() {
        return mSharedPreferences.getBoolean("Multiple_back", false);
    }


    public void setLoginUser(boolean Login) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putBoolean("Login_user", Login);
        prefsEditor.commit();
    }

    public void setMultipleBack(boolean back) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putBoolean("Multiple_back", back);
        prefsEditor.commit();
    }

    public boolean isopen() {
        return mSharedPreferences.getBoolean("open", false);
    }

    public void setOpen(boolean Login) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putBoolean("open", Login);
        prefsEditor.commit();
    }


    //FCM
    public String getFCMToken() {
        return mSharedPreferences.getString("FCMToken", "");
    }

    public void setFCMToken(String FCMToken) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("FCMToken", FCMToken);
        prefsEditor.commit();
    }

    //For Merging
    public void setNotification(Boolean value) {
        prefsEditor.putBoolean("Notification", value);
        prefsEditor.commit();
    }

    public boolean isNotification() {
        return mSharedPreferences.getBoolean("Notification", true);
    }

    public void setSound(Boolean value) {
        prefsEditor.putBoolean("Sound", value);
        prefsEditor.commit();
    }

    public boolean isSound() {
        return mSharedPreferences.getBoolean("Sound", true);
    }


    public void setVibration(Boolean value) {
        prefsEditor.putBoolean("Vibration", value);
        prefsEditor.commit();
    }

    public boolean isVibration() {
        return mSharedPreferences.getBoolean("Vibration", true);
    }

    public Uri getImageUri() {
        String imageUri = mSharedPreferences.getString("getImageUri", "");
        if (imageUri == null || imageUri.equals(""))
            return null;
        return Uri.parse(imageUri);
    }


    public void setImageUri(Uri imageUri) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getImageUri", imageUri.toString());
        prefsEditor.commit();
    }


    public String getImage() {
        return mSharedPreferences.getString("image", "");
    }

    public void setImage(String image) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("image", image);
        prefsEditor.commit();
    }

    public String getCropImagePath() {
        return mSharedPreferences.getString("getCropImagePath", "");
    }

    public void setCropImagePath(String cropImagePath) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getCropImagePath", cropImagePath);
        prefsEditor.commit();
    }

    public String getCartCount() {
        return mSharedPreferences.getString("cartcount", "0");
    }

    public void setCartCount(String cropImagePath) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("cartcount", cropImagePath);
        prefsEditor.commit();
    }


    public String getImagePath() {
        return mSharedPreferences.getString("getImagePath", "");
    }

    public void setImagePath(String imagePath) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getImagePath", imagePath);
        prefsEditor.commit();
    }


    public String getSupportEmail() {
        return mSharedPreferences.getString("getSupportEmail", "");
    }

    public void setSupportEmail(String password) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getSupportEmail", password);
        prefsEditor.commit();
    }

    public String getSupportPhone() {
        return mSharedPreferences.getString("getSupportPhone", "");
    }

    public void setSupportPhone(String password) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getSupportPhone", password);
        prefsEditor.commit();
    }

    public boolean isInsuranceImage() {
        return mSharedPreferences.getBoolean("isInsuranceImage", false);

    }

    public void setInsuranceImage(boolean isInsuranceImage) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putBoolean("isInsuranceImage", isInsuranceImage);
        prefsEditor.commit();
    }

    public boolean isEditVehicle() {
        return mSharedPreferences.getBoolean("isEditVehicle", false);

    }

    public void setEditVehicle(boolean isEditVehicle) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putBoolean("isEditVehicle", isEditVehicle);
        prefsEditor.commit();
    }

    public boolean isRcBook() {
        return mSharedPreferences.getBoolean("isRcBook", false);

    }

    public void setRcBook(boolean isRcBook) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putBoolean("isRcBook", isRcBook);
        prefsEditor.commit();
    }

    public String getUserType() {
        return mSharedPreferences.getString("getUserType", "");
    }

    public void setUserType(String userType) {
        prefsEditor = mSharedPreferences.edit();
        prefsEditor.putString("getUserType", userType);
        prefsEditor.commit();
    }
}

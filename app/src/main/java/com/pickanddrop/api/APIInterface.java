package com.pickanddrop.api;

import com.pickanddrop.dto.BoxSubCatDTO;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.dto.DeliveryMultipleDTO;
import com.pickanddrop.dto.DeliverySendMultipleDataDTO;
import com.pickanddrop.dto.LocationDTO;
import com.pickanddrop.dto.LocationTrackDTO;
import com.pickanddrop.dto.LoginDTO;
import com.pickanddrop.dto.OtherAddCountDTO;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.dto.PriceDistanceDTO;
import com.pickanddrop.dto.PricesDTO;
import com.pickanddrop.dto.ProfileDTO;
import com.pickanddrop.dto.UserEditProfileDTO;
import com.pickanddrop.dto.VechicalCatagoryDTO;
import com.pickanddrop.model.ChangePasswordModel;
import com.pickanddrop.model.ForgotPasswordModel;
import com.pickanddrop.model.LoginModel;
import com.pickanddrop.model.ResetPasswordModel;
import com.pickanddrop.model.SignUpModel;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface APIInterface {

//    @FormUrlEncoded
//    @POST("login")
//    Call<LoginDTO> callLoginApi(@FieldMap Map<String, String> map);

    @POST("ApiController/login")
    Call<LoginDTO> callLoginApi(@Body LoginModel loginModel);

    @FormUrlEncoded
    @POST("ApiController/forgetPassword")
    Call<OtherDTO> callForgotApi(@FieldMap Map<String, String> map);

//    @POST("ApiController/forgetPassword")
//    Call<OtherDTO> callForgotApi(@Body ForgotPasswordModel forgotPasswordModel);

    @FormUrlEncoded
    @POST("ApiController/create_order")
    Call<OtherDTO> callCreateOrderApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/getBoxSubCatogry")
    Call<BoxSubCatDTO> getBoxSubCatogryApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/priceCalculation")
    Call<PricesDTO> priceCalculationApi(@FieldMap Map<String, String> map);

//    http://192.168.1.51/pickanddeliver/index.php/ApiController/createMultpileDrop
//
//    code:COURIERAPP-30062016
//    data:array()

    @POST("ApiController/createMultpileDrop")
    Call<OtherDTO> createMultpileDropAPI(@Body DeliverySendMultipleDataDTO deliverySendMultipleDataDTO);


    @POST("ApiController/priceCalculationForMultiple")
    Call<PricesDTO> priceCalculationForMultipleAPI(@Body DeliverySendMultipleDataDTO deliverySendMultipleDataDTO);

    @FormUrlEncoded
    @POST("Payment/purchase")
    Call<OtherDTO> callPurchasePayApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/rescheduleOrder")
    Call<OtherDTO> callRescheduleOrderApi(@FieldMap Map<String, String> map);

    @POST("ApiController/rescheduleOrderForMultiple")
    Call<OtherDTO> rescheduleMultipleOrderAPI(@Body DeliverySendMultipleDataDTO deliverySendMultipleDataDTO);

    @FormUrlEncoded
    @POST("ApiController/cancelDelivery")
    Call<OtherDTO> callCancelOrderApi(@FieldMap Map<String, String> map);

//    @FormUrlEncoded
//    @POST("changePassword")
//    Call<OtherDTO> callChangePassword(@FieldMap Map<String, String> map);

    @POST("ApiController/changePassword")
    Call<OtherDTO> callChangePassword(@Body ChangePasswordModel changePasswordModel);

    @FormUrlEncoded
    @POST("ApiController/getDriverLocation")
    Call<LocationTrackDTO> getDriverLocationkk(@FieldMap Map<String, String> map);

//    @Multipart
//    @POST("registration")
//    Call<OtherDTO> callSignUpApi(
//            @PartMap() Map<String, RequestBody> partMap,
//            @Part MultipartBody.Part profileImage);

//    @POST("ApiController/register")
//    Call<OtherDTO> callSignUpApi(
//            @Body SignUpModel signUpModel);

    @Multipart

    @POST("ApiController/register")
    Call<OtherDTO> callSignUpApi(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part profileImage);

    @POST("ApiController/resetPassword")
    Call<OtherDTO> callResetPassword(@Body ResetPasswordModel resetPasswordModel);

    @Multipart
    @POST("ApiController/editProfile")
    Call<LoginDTO> callEditProfileApi(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part profileImage);

    @FormUrlEncoded
    @POST("ApiController/deliveryDetail")
    Call<DeliveryDTO> callDeliveryDetailsApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/get_user_deliveries")
    Call<DeliveryDTO> callUserCurrentDeliveryApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/customerOrderHistory")
    Call<DeliveryDTO> callUserHistoryDeliveryApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/driverOrderHistory")
    Call<DeliveryDTO> callDriverHistoryDeliveryApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/get_driver_deliveries")
    Call<DeliveryDTO> callDriverCurrentDeliveryApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/notifications")
    Call<DeliveryDTO> callNotificationListApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/deleteNotification")
    Call<OtherDTO> deleteNotification(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/deleteOrders")
    Call<OtherDTO> deleteOrdersUser(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/getNearByDeliveryBoys")
    Call<LocationDTO> getNearDriver(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/getNearByDeliveries")
    Call<DeliveryDTO> callNearDeliveriesForDriverApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/acceptDeliveryRequest")
    Call<OtherDTO> callAcceptDeliveriesForDriverApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/pickupdelivery")
    Call<OtherDTO> callPickupDeliveriesForDriverApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/changeReportproblemstatus")
    Call<OtherDTO> callReOrderApi(@FieldMap Map<String, String> map);

    @Multipart
    @POST("ApiController/deliverOrder")
    Call<LoginDTO> callDeliverOrderApi(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part signatureImage,
            @Part MultipartBody.Part itemImage);

    @FormUrlEncoded
    @POST("ApiController/getSettings")
    Call<OtherDTO> getSettingForPrice1(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/getPriceDetails")
    Call<PriceDistanceDTO> getSettingForPrice(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/reportProblem")
    Call<OtherDTO> callReportProblemApi(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/review")
    Call<OtherDTO> callReviewApi(@FieldMap Map<String, String> map);



    @FormUrlEncoded
    @POST("ApiController/logout")
    Call<OtherDTO> callLogoutApi(@FieldMap Map<String, String> map);



    @FormUrlEncoded
    @POST("ApiController/userDeleteAccount")
    Call<OtherDTO> userDeleteAccounts(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/getNotifyCount")
    Call<OtherAddCountDTO> callGetNotifyCount(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/removeNotifyCount")
    Call<OtherDTO> callRemoveNotifyCount(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("ApiController/getVechileTypeCategory")
    Call<VechicalCatagoryDTO>callgetVechileTypeCategory(@Field("code") String code, @Field("type") String type);

    @FormUrlEncoded
    @POST("ApiController/profileinfo")
    Call<ProfileDTO>callsetProfileType(@FieldMap Map<String, String> map);

    @Multipart
    @POST("ApiController/userEditProfile")
    Call<UserEditProfileDTO> callgetusereditProfileType(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part profileImage);
}
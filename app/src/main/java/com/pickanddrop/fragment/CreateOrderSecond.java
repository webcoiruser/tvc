package com.pickanddrop.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.CreateOrderTwoBinding;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.CustomSpinnerForAll;
import com.pickanddrop.utils.OnDialogConfirmListener;
import com.pickanddrop.utils.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

public class CreateOrderSecond extends BaseFragment implements AppConstants, View.OnClickListener {

    private Context context;
    private AppSession appSession;
    private Utilities utilities;
    private CreateOrderTwoBinding createOrderTwoBinding;
    private String countryCode = "", dropOffLat = "", dropOffLong = "", companyName = "", firstName = "", lastName = "", mobile = "", dropOffAddress = "", dropOffSpecialInstruction = "", vehicleType = "", parcelHeight = "", parcelWidth = "", parcelWeight = "", parcelLenght = "";
    private DeliveryDTO.Data deliveryDTO;
    private PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
    private static final int REQUEST_PICK_PLACE = 1142;
    private CustomSpinnerForAll customSpinnerAdapter;
    private ArrayList<HashMap<String, String>> vehicleList;
    private boolean rescheduleStatus = false;
    private String TAG = CreateOrderSecond.class.getName();
    private OtherDTO otherDTO;
    private Double totalDeliveryCost = 0.0, driverDeliveryCost = 0.0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("deliveryDTO")) {
            deliveryDTO = getArguments().getParcelable("deliveryDTO");
            Log.d("dataorder", "onCreate: "+deliveryDTO);
            if (getArguments().containsKey("rescheduleStatus")) {
                rescheduleStatus = true;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        createOrderTwoBinding = DataBindingUtil.inflate(inflater, R.layout.create_order_two, container, false);
        return createOrderTwoBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        //Toast.makeText(context,"CreateOrderSecond",Toast.LENGTH_LONG).show();
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);

        initView();
        initToolBar();

        if (rescheduleStatus) {
            setValues();
        } else {
            getPrice();
        }
    }

    private void setValues() {
        createOrderTwoBinding.etCompany.setText(deliveryDTO.getDropoffComapnyName());
        createOrderTwoBinding.etFirstName.setText(deliveryDTO.getDropoffFirstName());
        createOrderTwoBinding.etLastName.setText(deliveryDTO.getDropoffLastName());
        createOrderTwoBinding.etMobile.setText(deliveryDTO.getDropoffMobNumber());
        createOrderTwoBinding.etDropoffAddress.setText(deliveryDTO.getDropoffaddress());
        createOrderTwoBinding.etParcelWeight.setText(deliveryDTO.getProductWeight());
        createOrderTwoBinding.etParcelLength.setText(deliveryDTO.getProductLength());
        createOrderTwoBinding.etParcelWidth.setText(deliveryDTO.getProductWidth());
        createOrderTwoBinding.etParcelHeight.setText(deliveryDTO.getProductHeight());
        createOrderTwoBinding.etDropSpecialInst.setText(deliveryDTO.getDropoffSpecialInst());

        createOrderTwoBinding.ccp.setCountryForPhoneCode(Integer.parseInt(deliveryDTO.getDropoffCountryCode()));

        dropOffLat = deliveryDTO.getDropoffLat();
        dropOffLong = deliveryDTO.getDropoffLong();

        for (int i = 0; i < vehicleList.size(); i++) {
            if (deliveryDTO.getVehicleType() != null && deliveryDTO.getVehicleType().equalsIgnoreCase(vehicleList.get(i).get(PN_VALUE))) {
                createOrderTwoBinding.spType.setSelection(i);
                break;
            }
        }

        createOrderTwoBinding.spType.setClickable(false);
        createOrderTwoBinding.spType.setEnabled(false);
        createOrderTwoBinding.llType.setEnabled(false);
        createOrderTwoBinding.etDropoffAddress.setEnabled(false);
    }

    public void getPrice() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);

            Map<String, String> map = new HashMap<>();
            map.put("code", APP_TOKEN);

            APIInterface apiInterface = APIClient.getClient();
            Call<OtherDTO> call = apiInterface.getSettingForPrice1(map);
            call.enqueue(new Callback<OtherDTO>() {
                @Override
                public void onResponse(Call<OtherDTO> call, Response<OtherDTO> response) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {
                            if (response.body().getResult().equalsIgnoreCase("success")) {
                                otherDTO = response.body();
                            } else {
                                utilities.dialogOK(context, "", response.body().getMessage(), getString(R.string.ok), false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<OtherDTO> call, Throwable t) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error)+t.toString(), context.getResources().getString(R.string.ok), false);
                    Log.e(TAG, t.toString());
                }
            });
        }
    }

    private void initToolBar() {

    }

    private void initView() {
//        createOrderTwoBinding.ccp.registerPhoneNumberTextView(createOrderTwoBinding.etMobile);

        otherDTO = new OtherDTO();
        vehicleList = new ArrayList<>();
        createOrderTwoBinding.btnSubmit.setOnClickListener(this);
        createOrderTwoBinding.ivBack.setOnClickListener(this);
        createOrderTwoBinding.etDropoffAddress.setOnClickListener(this);
        createOrderTwoBinding.llType.setOnClickListener(this);

        HashMap<String, String> hashMap1 = new HashMap<>();
        hashMap1.put(PN_NAME, "");
        hashMap1.put(PN_VALUE, getResources().getString(R.string.vehicle_type));
        vehicleList.add(hashMap1);
        hashMap1 = new HashMap<>();
        hashMap1.put(PN_NAME, "");
        hashMap1.put(PN_VALUE, getResources().getString(R.string.bike));
        vehicleList.add(hashMap1);
        hashMap1 = new HashMap<>();
        hashMap1.put(PN_NAME, "");
        hashMap1.put(PN_VALUE, getResources().getString(R.string.car));
        vehicleList.add(hashMap1);
        hashMap1 = new HashMap<>();
        hashMap1.put(PN_NAME, "");
        hashMap1.put(PN_VALUE, getResources().getString(R.string.van));
        vehicleList.add(hashMap1);
        hashMap1 = new HashMap<>();
        hashMap1.put(PN_NAME, "");
        hashMap1.put(PN_VALUE, getResources().getString(R.string.truck));
        vehicleList.add(hashMap1);

        customSpinnerAdapter = new CustomSpinnerForAll(context, R.layout.spinner_textview, vehicleList, getResources().getColor(R.color.black_color), getResources().getColor(R.color.light_black), getResources().getColor(R.color.transparent));
        createOrderTwoBinding.spType.setAdapter(customSpinnerAdapter);

        createOrderTwoBinding.spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    vehicleType = vehicleList.get(i).get(PN_VALUE);
                } else {
                    vehicleType = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                Utilities.hideKeyboard(createOrderTwoBinding.btnSubmit);
                countryCode = createOrderTwoBinding.ccp.getSelectedCountryCode();
                Log.e(TAG, ">>>>>>>>>>>>>>" + countryCode);

                companyName = createOrderTwoBinding.etCompany.getText().toString();
                firstName = createOrderTwoBinding.etFirstName.getText().toString();
                lastName = createOrderTwoBinding.etLastName.getText().toString();
                mobile = createOrderTwoBinding.etMobile.getText().toString();
                dropOffAddress = createOrderTwoBinding.etDropoffAddress.getText().toString();
                parcelWeight = createOrderTwoBinding.etParcelWeight.getText().toString();
                parcelLenght = createOrderTwoBinding.etParcelLength.getText().toString();
                parcelWidth = createOrderTwoBinding.etParcelWidth.getText().toString();
                parcelHeight = createOrderTwoBinding.etParcelHeight.getText().toString();
                dropOffSpecialInstruction = createOrderTwoBinding.etDropSpecialInst.getText().toString();

                if (isValid()) {
                    DeliveryCheckout deliveryCheckout = new DeliveryCheckout();
                    Bundle bundle = new Bundle();

                    deliveryDTO.setDropoffComapnyName(companyName);
                    deliveryDTO.setDropoffFirstName(firstName);
                    deliveryDTO.setDropoffLastName(lastName);
                    deliveryDTO.setDropoffMobNumber(mobile);
                    deliveryDTO.setDropoffaddress(dropOffAddress);
                    deliveryDTO.setProductHeight(parcelHeight);
                    deliveryDTO.setProductLength(parcelLenght);
                    deliveryDTO.setProductWeight(parcelWeight);
                    deliveryDTO.setProductWidth(parcelWidth);
                    deliveryDTO.setDropoffSpecialInst(dropOffSpecialInstruction);
                    deliveryDTO.setVehicleType(vehicleType);
                    deliveryDTO.setDropoffLat(dropOffLat);
                    deliveryDTO.setDropoffLong(dropOffLong);
                    deliveryDTO.setDropoffCountryCode(countryCode);

                    if (rescheduleStatus) {
                        callRescheduleOrderBookApi();
                    } else {

                        Location loc1 = new Location("");
                        loc1.setLatitude(Double.parseDouble(deliveryDTO.getPickupLat()));
                        loc1.setLongitude(Double.parseDouble(deliveryDTO.getPickupLong()));

                        Location loc2 = new Location("");
                        loc2.setLatitude(Double.parseDouble(dropOffLat));
                        loc2.setLongitude(Double.parseDouble(dropOffLong));

                        float distanceInmiles = (loc1.distanceTo(loc2)) / 1000;

                        if (vehicleType.equalsIgnoreCase(getString(R.string.bike))) {
                            totalDeliveryCost = distanceInmiles * Double.parseDouble(otherDTO.getVehicle().getMotorbike());
                        } else if (vehicleType.equalsIgnoreCase(getString(R.string.car))) {
                            totalDeliveryCost = distanceInmiles * Double.parseDouble(otherDTO.getVehicle().getCar());
                        } else if (vehicleType.equalsIgnoreCase(getString(R.string.van))) {
                            totalDeliveryCost = distanceInmiles * Double.parseDouble(otherDTO.getVehicle().getVan());
                        } else {
                            totalDeliveryCost = distanceInmiles * Double.parseDouble(otherDTO.getVehicle().getTruck());
                        }

                        driverDeliveryCost = totalDeliveryCost - ((totalDeliveryCost * Float.parseFloat(otherDTO.getVehicle().getDriverPercentage()) / 100));

                        try {
                            deliveryDTO.setDeliveryDistance(String.format("%.2f", Double.parseDouble(distanceInmiles + "")));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            String price = totalDeliveryCost + "";
                            if (price.contains(",")) {
                                price = price.replaceAll(",", "");
                            } else {
                                price = totalDeliveryCost+"";
                            }

                            deliveryDTO.setDeliveryCost(String.format("%2f", Double.parseDouble(price)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            String price = driverDeliveryCost + "";
                            if (price.contains(",")) {
                                price = price.replaceAll(",", "");
                            } else {
                                price = driverDeliveryCost+"";
                            }

                            deliveryDTO.setDriverDeliveryCost(String.format("%2f", Double.parseDouble(price)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        bundle.putParcelable("deliveryDTO", deliveryDTO);
                        deliveryCheckout.setArguments(bundle);
                        addFragmentWithoutRemove(R.id.container_main, deliveryCheckout, "DeliveryCheckout");
                    }
                }
                break;
            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).popFragment();
                break;
            case R.id.et_dropoff_address:
                try {
                    startActivityForResult(builder.build(getActivity()), REQUEST_PICK_PLACE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_type:
                createOrderTwoBinding.spType.performClick();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICK_PLACE && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(context, data);

            Log.i(getClass().getName(), "Class is >>>>>" + place.getName() + " " + place.getAddress() + "   " + place.getLatLng());
            dropOffLat = place.getLatLng().latitude + "";
            dropOffLong = place.getLatLng().longitude + "";
            createOrderTwoBinding.etDropoffAddress.setText(getAddressFromLatLong(place.getLatLng().latitude, place.getLatLng().longitude, false));
        }
    }

    public String getAddressFromLatLong(double latitude, double longitude, boolean status) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            return address + " " + city;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public boolean isValid() {
        if (firstName == null || firstName.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_first_name), getString(R.string.ok), false);
            createOrderTwoBinding.etFirstName.requestFocus();
            return false;
        } else if (lastName == null || lastName.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_last_name), getString(R.string.ok), false);
            createOrderTwoBinding.etLastName.requestFocus();
            return false;
        } else if (mobile.trim().length() == 0) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_mobile_number), getString(R.string.ok), false);
            createOrderTwoBinding.etMobile.requestFocus();
            return false;
        } else if (!utilities.checkMobile(mobile)) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_valid_mobile_number), getString(R.string.ok), false);
            createOrderTwoBinding.etMobile.requestFocus();
            return false;
        } else if (dropOffAddress == null || dropOffAddress.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_drop_address), getString(R.string.ok), false);
            createOrderTwoBinding.etDropoffAddress.requestFocus();
            return false;
        } else if (parcelHeight == null || parcelHeight.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_height), getString(R.string.ok), false);
            createOrderTwoBinding.etParcelHeight.requestFocus();
            return false;
        } else if (parcelWidth == null || parcelWidth.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_width), getString(R.string.ok), false);
            createOrderTwoBinding.etParcelWidth.requestFocus();
            return false;
        } else if (parcelLenght == null || parcelLenght.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_length), getString(R.string.ok), false);
            createOrderTwoBinding.etParcelLength.requestFocus();
            return false;
        } else if (parcelWeight == null || parcelWeight.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_parcel_weight), getString(R.string.ok), false);
            createOrderTwoBinding.etParcelWeight.requestFocus();
            return false;
        } else if (vehicleType == null || vehicleType.equals("")) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_select_vehicle), getString(R.string.ok), false);
            createOrderTwoBinding.spType.requestFocus();
            return false;
        }
        return true;
    }

    public void callRescheduleOrderBookApi() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);
            Map<String, String> map = new HashMap<>();
            map.put("user_id", appSession.getUser().getData().getUserId());
            map.put("order_id", deliveryDTO.getOrderId());
            map.put("pickup_comapny_name", deliveryDTO.getPickupComapnyName());
            map.put("pickup_first_name", deliveryDTO.getPickupFirstName());
            map.put("pickup_last_name", deliveryDTO.getPickupLastName());
            map.put("pickup_mob_number", deliveryDTO.getPickupMobNumber());
            map.put("pickupaddress", deliveryDTO.getPickupaddress());
            map.put("item_description", deliveryDTO.getItemDescription());
            map.put("item_quantity", deliveryDTO.getItemQuantity());
            map.put("delivery_date", deliveryDTO.getPickupDate());
            map.put("pickup_special_inst", deliveryDTO.getPickupSpecialInst());
            map.put("dropoff_first_name", deliveryDTO.getDropoffFirstName());
            map.put("dropoff_last_name", deliveryDTO.getDropoffLastName());
            map.put("dropoff_mob_number", deliveryDTO.getDropoffMobNumber());
            map.put("dropoff_special_inst", deliveryDTO.getDropoffSpecialInst());
            map.put("dropoffaddress", deliveryDTO.getDropoffaddress());
            map.put("parcel_height", deliveryDTO.getProductHeight());
            map.put("parcel_width", deliveryDTO.getProductWidth());
            map.put("parcel_lenght", deliveryDTO.getProductLength());
            map.put("parcel_weight", deliveryDTO.getProductWeight());
            map.put("delivery_type", deliveryDTO.getDeliveryType());
            map.put("driver_delivery_cost", deliveryDTO.getDriverDeliveryCost());
            map.put("delivery_distance", deliveryDTO.getDeliveryDistance());
            map.put("delivery_cost", deliveryDTO.getDeliveryCost());
            map.put("dropoff_comapny_name", deliveryDTO.getDropoffComapnyName());
            map.put("vehicle_type", deliveryDTO.getVehicleType());
            map.put("pickUpLat", deliveryDTO.getPickupLat());
            map.put("pickUpLong", deliveryDTO.getPickupLong());
            map.put("dropOffLong", deliveryDTO.getDropoffLong());
            map.put("dropOffLat", deliveryDTO.getDropoffLat());
            map.put("delivery_time", deliveryDTO.getDeliveryTime());
            map.put(PN_APP_TOKEN, APP_TOKEN);
            map.put("dropoff_country_code", "91");
            map.put("pickup_country_code", "91");


            APIInterface apiInterface = APIClient.getClient();
            Call<OtherDTO> call = apiInterface.callRescheduleOrderApi(map);
            call.enqueue(new Callback<OtherDTO>() {
                @Override
                public void onResponse(Call<OtherDTO> call, Response<OtherDTO> response) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {
                            if (response.body().getResult().equalsIgnoreCase("success")) {
                                utilities.dialogOKre(context, "", response.body().getMessage(), getString(R.string.ok), new OnDialogConfirmListener() {
                                    @Override
                                    public void onYes() {
                                        ((DrawerContentSlideActivity) context).popAllFragment();
                                    }

                                    @Override
                                    public void onNo() {
                                    }
                                });
                            } else {
                                utilities.dialogOK(context, "", response.body().getMessage(), context.getString(R.string.ok), false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<OtherDTO> call, Throwable t) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e(TAG, t.toString());
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);

                }
            });
        }
    }
}
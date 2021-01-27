package com.pickanddrop.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.DeliveryBookBinding;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.OnDialogConfirmListener;
import com.pickanddrop.utils.Utilities;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryCheckout extends BaseFragment implements AppConstants, View.OnClickListener {

    private Context context;
    private AppSession appSession;
    private Utilities utilities;
    private DeliveryBookBinding deliveryBookBinding;
    private String email = "";
    private DeliveryDTO.Data deliveryDTO;
    private String TAG = DeliveryCheckout.class.getName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("deliveryDTO")) {
            deliveryDTO = getArguments().getParcelable("deliveryDTO");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        deliveryBookBinding = DataBindingUtil.inflate(inflater, R.layout.delivery_book, container, false);
        return deliveryBookBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        //Toast.makeText(context,"DeliveryCheckout",Toast.LENGTH_LONG).show();
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);

        initView();
        initToolBar();
        setValues();
    }

    private void setValues() {
        deliveryBookBinding.etPickupAddress.setText(deliveryDTO.getPickupaddress());
        deliveryBookBinding.etDropoffAddress.setText(deliveryDTO.getDropoffaddress());
//        deliveryBookBinding.etPrice.setText(getString(R.string.us_dollar)+" "+deliveryDTO.getDeliveryCost());
//        deliveryBookBinding.etPrice.setText(getString(R.string.us_dollar)+" 20");
        try {
            deliveryBookBinding.etPrice.setText(getString(R.string.us_dollar)+" "+String.format("%.2f", Double.parseDouble(deliveryDTO.getDeliveryCost())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        deliveryBookBinding.etDeliveryDate.setText(deliveryDTO.getPickupDate());
        deliveryBookBinding.etDeliveryTime.setText(deliveryDTO.getDeliveryTime());
        deliveryBookBinding.etDistance.setText(deliveryDTO.getDeliveryDistance() +" "+ getString(R.string.mile));

        if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.bike))) {
            deliveryBookBinding.llCar.setAlpha(Float.parseFloat("0.1"));
            deliveryBookBinding.llVan.setAlpha(Float.parseFloat("0.1"));
            deliveryBookBinding.llTruck.setAlpha(Float.parseFloat("0.1"));
        } else if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.car))) {
            deliveryBookBinding.llBike.setAlpha(Float.parseFloat("0.1"));
            deliveryBookBinding.llVan.setAlpha(Float.parseFloat("0.1"));
            deliveryBookBinding.llTruck.setAlpha(Float.parseFloat("0.1"));
        } else if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.van))) {
            deliveryBookBinding.llBike.setAlpha(Float.parseFloat("0.1"));
            deliveryBookBinding.llCar.setAlpha(Float.parseFloat("0.1"));
            deliveryBookBinding.llTruck.setAlpha(Float.parseFloat("0.1"));
        } else if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.truck))) {
            deliveryBookBinding.llBike.setAlpha(Float.parseFloat("0.1"));
            deliveryBookBinding.llVan.setAlpha(Float.parseFloat("0.1"));
            deliveryBookBinding.llCar.setAlpha(Float.parseFloat("0.1"));
        }


        if (deliveryDTO.getDeliveryType().equalsIgnoreCase("single")) {
            deliveryBookBinding.btnFour.setAlpha(Float.parseFloat("0.1"));
            deliveryBookBinding.btnSame.setAlpha(Float.parseFloat("0.1"));
        } else if (deliveryDTO.getDeliveryType().equalsIgnoreCase("multiple")) {
            deliveryBookBinding.btnSame.setAlpha(Float.parseFloat("0.1"));
            deliveryBookBinding.btnTwo.setAlpha(Float.parseFloat("0.1"));
        } else if (deliveryDTO.getDeliveryType().equalsIgnoreCase("express")) {
            deliveryBookBinding.btnTwo.setAlpha(Float.parseFloat("0.1"));
            deliveryBookBinding.btnFour.setAlpha(Float.parseFloat("0.1"));
        }
    }

    private void initToolBar() {

    }

    private void initView() {
        deliveryBookBinding.btnSubmit.setOnClickListener(this);
        deliveryBookBinding.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_submit:
                Utilities.hideKeyboard(deliveryBookBinding.btnSubmit);
                callOrderBookApi();
                break;
            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).popFragment();
                break;
        }
    }

    public void callOrderBookApi() {
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
            map.put("nameOfGoods",deliveryDTO.getNameOfGoods());
            map.put("typeGoods",deliveryDTO.getTypeGoods());


            try {
                map.put("delivery_cost", String.format("%.2f", Double.parseDouble(deliveryDTO.getDeliveryCost())));
            } catch (Exception e) {
                map.put("delivery_cost", deliveryDTO.getDeliveryCost());
                e.printStackTrace();
            }

            map.put("dropoff_comapny_name", deliveryDTO.getDropoffComapnyName());
            map.put("vehicle_type", deliveryDTO.getVehicleType());
            map.put("pickUpLat", deliveryDTO.getPickupLat());
            map.put("pickUpLong", deliveryDTO.getPickupLong());
            map.put("dropOffLong", deliveryDTO.getDropoffLong());
            map.put("dropOffLat", deliveryDTO.getDropoffLat());
            map.put("delivery_time", deliveryDTO.getDeliveryTime());
            map.put("typeGoodsCategory", deliveryDTO.getTypeGoodsCategory());
            map.put("weight_unit", deliveryDTO.getWeight_unit());

            map.put(PN_APP_TOKEN, APP_TOKEN);

            map.put("dropoff_country_code", deliveryDTO.getDropoffCountryCode());
            map.put("pickup_country_code", deliveryDTO.getPickupCountryCode());

            APIInterface apiInterface = APIClient.getClient();
            Call<OtherDTO> call = apiInterface.callCreateOrderApi(map);
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
                            utilities.dialogOK(context, "", e.getMessage(), context.getString(R.string.ok), false);
                        }
                    }
                }

                @Override
                public void onFailure(Call<OtherDTO> call, Throwable t) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e(" Testing ", t.toString());
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);

                }
            });
        }
    }
}
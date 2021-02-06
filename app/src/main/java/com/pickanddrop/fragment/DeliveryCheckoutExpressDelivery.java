package com.pickanddrop.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.adapter.dataadapter;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.DeliveryBookExpressDeliveryBinding;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.dto.DeliveryMultipleDTO;
import com.pickanddrop.dto.DeliverySendMultipleDataDTO;
import com.pickanddrop.dto.MultipleDTO;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.DatabaseHandler;
import com.pickanddrop.utils.MyConstant;
import com.pickanddrop.utils.OnDialogConfirmListener;
import com.pickanddrop.utils.Utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryCheckoutExpressDelivery extends BaseFragment implements AppConstants, View.OnClickListener {

    private Context context;
    private AppSession appSession;
    private Utilities utilities;
    private dataadapter dataAdapter;
    private DeliveryBookExpressDeliveryBinding deliveryBookExpressDeliveryBinding;
    private String email = "";
    private DeliveryDTO.Data deliveryDTO;
    private MultipleDTO multipleDTO;
    private String  deliveryType = "";
    private LinearLayoutManager linearLayoutManager;
    DatabaseHandler db;
    private String TAG = DeliveryCheckoutExpressDelivery.class.getName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("deliveryDTO")) {
            deliveryDTO = getArguments().getParcelable("deliveryDTO");
            deliveryType = getArguments().getString("delivery_type");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        deliveryBookExpressDeliveryBinding = DataBindingUtil.inflate(inflater, R.layout.delivery_book_express_delivery, container, false);
        return deliveryBookExpressDeliveryBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();

        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);

        initView();
        initToolBar();
        setValues();
    }

    private void setValues() {

        if(deliveryType == "multiple") {
            deliveryDTO.setDeliveryType("multiple");
//            deliveryDTO.setDeliveryCost(String.valueOf(Double.parseDouble(deliveryDTO.getDeliveryCost())+(Double.parseDouble(deliveryDTO.getDeliveryCost())*0.1)));

            //Toast.makeText(context,deliveryDTO.getDeliveryCost(),Toast.LENGTH_LONG).show();
            //deliveryDTO.setDeliveryCost(String.valueOf(Double.parseDouble(deliveryDTO.getDeliveryCost())));

            deliveryBookExpressDeliveryBinding.etPickupAddress.setText(deliveryDTO.getPickupaddress());

          //  deliveryBookExpressDeliveryBinding.etDropoffAddress.setText(deliveryDTO.getDropoffaddress());

            deliveryBookExpressDeliveryBinding.etDropoffAddress.setVisibility(View.GONE);
            deliveryBookExpressDeliveryBinding.tvDv.setVisibility(View.GONE);


            deliveryBookExpressDeliveryBinding.llNumberOfData.setVisibility(View.VISIBLE);
            deliveryBookExpressDeliveryBinding.llDeliveryDistance.setVisibility(View.VISIBLE);


            if(deliveryType.equalsIgnoreCase("multiple")){

//                deliveryBookExpressDeliveryBinding.etDistance.setText(String.format("%.3f",Double.parseDouble(deliveryDTO.getDeliveryDistance())));
                deliveryBookExpressDeliveryBinding.etDistance.setText(""+deliveryDTO.getDeliveryDistance());
                //deliveryBookExpressDeliveryBinding.etDistance.setText(CreateOrderExpressDeliveryDrop.distance_value);

                Log.d(TAG, "setValues: "+deliveryDTO.getDeliveryDistance());

            }else {
                deliveryBookExpressDeliveryBinding.etDistance.setText(deliveryDTO.getDeliveryDistance());

            }

//        deliveryBookExpressDeliveryBinding.etPrice.setText(getString(R.string.us_dollar)+" "+deliveryDTO.getDeliveryCost());
//        deliveryBookExpressDeliveryBinding.etPrice.setText(getString(R.string.us_dollar)+" 20");
            try {
                if(deliveryType.equalsIgnoreCase("multiple")){
                    deliveryBookExpressDeliveryBinding.etPrice.setText(getString(R.string.us_dollar) + " " + String.format("%.2f", Double.parseDouble(deliveryDTO.getDeliveryCost())));
                    deliveryBookExpressDeliveryBinding.etDistance.setText(String.format("%.3f",Double.parseDouble(deliveryDTO.getTotal_distance()))+ " Mile");

                }else {

                    deliveryBookExpressDeliveryBinding.etPrice.setText(getString(R.string.us_dollar) + " " + String.format("%.2f", Double.parseDouble(deliveryDTO.getDeliveryCost())));
                    deliveryBookExpressDeliveryBinding.etDistance.setText(String.format("%.3f",Double.parseDouble(deliveryDTO.getTotal_distance()))+ " Mile");
                }
                System.out.println("deliverycost" + deliveryDTO.getDeliveryCost());
            } catch (Exception e) {
                e.printStackTrace();
            }

            deliveryBookExpressDeliveryBinding.etDeliveryDate.setText(deliveryDTO.getPickupDate());
            deliveryBookExpressDeliveryBinding.etDeliveryTime.setText(deliveryDTO.getDeliveryTime());
            deliveryBookExpressDeliveryBinding.etPickupPerson.setText(deliveryDTO.getPickupFirstName());
            deliveryBookExpressDeliveryBinding.etDropPerson.setVisibility(View.GONE);
            deliveryBookExpressDeliveryBinding.tvDropcontact.setVisibility(View.GONE);
           // deliveryBookExpressDeliveryBinding.etDropPerson.setText(deliveryDTO.getDropoffFirstName());

        //    deliveryBookExpressDeliveryBinding.etDistance.setText(deliveryDTO.getDeliveryDistance() + " " + getString(R.string.mile));
        //    deliveryBookExpressDeliveryBinding.etNoOfPallets.setText(deliveryDTO.getNoOfPallets());

            if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.bike))) {
                deliveryBookExpressDeliveryBinding.llCar.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llVan.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llTruck.setAlpha(Float.parseFloat("0.1"));
            } else if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.car))) {
                deliveryBookExpressDeliveryBinding.llBike.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llVan.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llTruck.setAlpha(Float.parseFloat("0.1"));
            } else if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.van))) {
                deliveryBookExpressDeliveryBinding.llBike.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llCar.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llTruck.setAlpha(Float.parseFloat("0.1"));
            } else if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.truck))) {
                deliveryBookExpressDeliveryBinding.llBike.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llVan.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llCar.setAlpha(Float.parseFloat("0.1"));
            }

            if (deliveryDTO.getDeliveryType().equalsIgnoreCase("single")) {
                if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Pallet")) {
                    deliveryBookExpressDeliveryBinding.tvNumberofdata.setText("Number of Pallet:");

                } else if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Box")) {
                    deliveryBookExpressDeliveryBinding.tvNumberofdata.setText("Number of Box:");
                } else if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Miscellaneous")) {
                    deliveryBookExpressDeliveryBinding.tvNumberofdata.setText("Weight of Miscellaneous:");
                }
                deliveryBookExpressDeliveryBinding.btnMis.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnFour.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnSame.setAlpha(Float.parseFloat("0.1"));
            } else if (deliveryDTO.getDeliveryType().equalsIgnoreCase("multiple")) {
                deliveryBookExpressDeliveryBinding.btnMis.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnSame.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnTwo.setAlpha(Float.parseFloat("0.1"));
            } else if (deliveryDTO.getDeliveryType().equalsIgnoreCase("express")) {

                if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Box")) {
                    deliveryBookExpressDeliveryBinding.tvNumberofdata.setText("Number of Box:");
                } else if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Envelop")) {
                    deliveryBookExpressDeliveryBinding.tvNumberofdata.setText("Number of Envelop:");
                }

                deliveryBookExpressDeliveryBinding.llTime.setVisibility(View.GONE);
                deliveryBookExpressDeliveryBinding.llTruck.setVisibility(View.GONE);
                deliveryBookExpressDeliveryBinding.btnMis.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnTwo.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnFour.setAlpha(Float.parseFloat("0.1"));
            } else if (deliveryDTO.getDeliveryType().equalsIgnoreCase("miscellaneous")) {
             if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Miscellaneous")) {
                    deliveryBookExpressDeliveryBinding.tvNumberofdata.setText("Weight of Miscellaneous:");
                }
                deliveryBookExpressDeliveryBinding.btnTwo.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnFour.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnSame.setAlpha(Float.parseFloat("0.1"));
            }
        }else {
            deliveryBookExpressDeliveryBinding.tvDv.setVisibility(View.VISIBLE);
            deliveryBookExpressDeliveryBinding.etDropPerson.setVisibility(View.VISIBLE);
            deliveryBookExpressDeliveryBinding.tvDropcontact.setVisibility(View.VISIBLE);
            deliveryBookExpressDeliveryBinding.lv.setVisibility(View.GONE);
            deliveryDTO.setDeliveryCost(String.valueOf((Double.parseDouble(deliveryDTO.getDeliveryCost()))));

            deliveryBookExpressDeliveryBinding.etPickupAddress.setText(deliveryDTO.getPickupaddress());
            deliveryBookExpressDeliveryBinding.etDropoffAddress.setText(deliveryDTO.getDropoffaddress());
//        deliveryBookExpressDeliveryBinding.etPrice.setText(getString(R.string.us_dollar)+" "+deliveryDTO.getDeliveryCost());
//        deliveryBookExpressDeliveryBinding.etPrice.setText(getString(R.string.us_dollar)+" 20");
            try {
                deliveryBookExpressDeliveryBinding.etPrice.setText(getString(R.string.us_dollar) + " " + String.format("%.2f", Double.parseDouble(deliveryDTO.getDeliveryCost())));
                deliveryBookExpressDeliveryBinding.etDistance.setText(String.format("%.3f",Double.parseDouble(deliveryDTO.getTotal_distance()))+ " Mile");
                System.out.println("deliverycost" + deliveryDTO.getDeliveryCost());
            } catch (Exception e) {
                e.printStackTrace();
            }

            deliveryBookExpressDeliveryBinding.etDeliveryDate.setText(deliveryDTO.getPickupDate());
            deliveryBookExpressDeliveryBinding.etPickupPerson.setText(deliveryDTO.getPickupFirstName());
            deliveryBookExpressDeliveryBinding.etDropPerson.setText(deliveryDTO.getDropoffFirstName());
            System.out.println("pickuppersonnnanem--->"+deliveryDTO.getPickupFirstName());
            deliveryBookExpressDeliveryBinding.etDeliveryTime.setText(deliveryDTO.getDeliveryTime());

            double disinmiles = Double.parseDouble(deliveryDTO.getDeliveryDistance());
            //disinmiles = disinmiles*0.62137;
            //deliveryBookExpressDeliveryBinding.etDistance.setText(disinmiles + " " + getString(R.string.mile));
            deliveryBookExpressDeliveryBinding.etDistance.setText(String.format("%.3f",Double.parseDouble(String.valueOf(disinmiles))) + " " + getString(R.string.mile));

            if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Miscellaneous")){
                deliveryBookExpressDeliveryBinding.etNoOfPallets.setText(deliveryDTO.getProductWeight()+ " " +deliveryDTO.getWeight_unit());
            }else {
                deliveryBookExpressDeliveryBinding.etNoOfPallets.setText(deliveryDTO.getNoOfPallets());
            }

            if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.bike))) {
                deliveryBookExpressDeliveryBinding.llCar.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llVan.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llTruck.setAlpha(Float.parseFloat("0.1"));
            } else if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.car))) {
                deliveryBookExpressDeliveryBinding.llBike.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llVan.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llTruck.setAlpha(Float.parseFloat("0.1"));
            } else if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.van))) {
                deliveryBookExpressDeliveryBinding.llBike.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llCar.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llTruck.setAlpha(Float.parseFloat("0.1"));
            } else if (deliveryDTO.getVehicleType().equalsIgnoreCase(getString(R.string.truck))) {
                deliveryBookExpressDeliveryBinding.llBike.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llVan.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.llCar.setAlpha(Float.parseFloat("0.1"));
            }

            if (deliveryDTO.getDeliveryType().equalsIgnoreCase("single")) {
                if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Pallet")) {
                    deliveryBookExpressDeliveryBinding.tvNumberofdata.setText("Number of Pallet:");

                } else if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Box")) {
                    deliveryBookExpressDeliveryBinding.tvNumberofdata.setText("Number of Box:");
                } else if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Miscellaneous")) {
                    deliveryBookExpressDeliveryBinding.tvNumberofdata.setText("Weight of Miscellaneous:");
                }
                deliveryBookExpressDeliveryBinding.btnMis.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnFour.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnSame.setAlpha(Float.parseFloat("0.1"));
            } else if (deliveryDTO.getDeliveryType().equalsIgnoreCase("multiple")) {
                deliveryBookExpressDeliveryBinding.btnMis.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnSame.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnTwo.setAlpha(Float.parseFloat("0.1"));
            } else if (deliveryDTO.getDeliveryType().equalsIgnoreCase("express")) {
                if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Box")) {
                    deliveryBookExpressDeliveryBinding.tvNumberofdata.setText("Number of Box:");
                } else if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Envelop")) {
                    deliveryBookExpressDeliveryBinding.tvNumberofdata.setText("Number of Envelop:");
                }
                deliveryBookExpressDeliveryBinding.llTime.setVisibility(View.GONE);
                deliveryBookExpressDeliveryBinding.llTruck.setVisibility(View.GONE);
                deliveryBookExpressDeliveryBinding.btnMis.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnTwo.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnFour.setAlpha(Float.parseFloat("0.1"));
            }else if (deliveryDTO.getDeliveryType().equalsIgnoreCase("miscellaneous")) {
                if (deliveryDTO.getTypeGoods().equalsIgnoreCase("Miscellaneous")) {
                    deliveryBookExpressDeliveryBinding.tvNumberofdata.setText("Weight of Miscellaneous:");
                }
                deliveryBookExpressDeliveryBinding.btnTwo.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnFour.setAlpha(Float.parseFloat("0.1"));
                deliveryBookExpressDeliveryBinding.btnSame.setAlpha(Float.parseFloat("0.1"));
            }
        }

    }

    private void initToolBar() {

    }

    private void initView() {

        if(deliveryType == "multiple") {

             db = new DatabaseHandler(getContext());
         //   List<String> labels = db.getAllLabels();
      //      System.out.println("labels"+labels);

            linearLayoutManager = new LinearLayoutManager(context);

            List<String> labels =  db.getAllLabels();
            List<String> labels1 =  db.getAllDropnames();
            int pallets = db.getpalletscount();
            int box = db.getboxcount();
            deliveryBookExpressDeliveryBinding.llNumberOfBox.setVisibility(View.VISIBLE);
            deliveryBookExpressDeliveryBinding.tvNumberofdata.setText("Total pallets");
            deliveryBookExpressDeliveryBinding.tvNumberofbox.setText("Total Boxes");
            deliveryBookExpressDeliveryBinding.etNoOfBox.setText(deliveryDTO.getNoOfPallets1());
            deliveryBookExpressDeliveryBinding.etNoOfPallets.setText(deliveryDTO.getNoOfPallets());
            Log.d(TAG, "initView: "+box);
//            deliveryBookExpressDeliveryBinding.etNoOfBox.setText(String.valueOf(box));
            linearLayoutManager = new LinearLayoutManager(context);
            dataAdapter = new dataadapter(context,labels,labels1);
            deliveryBookExpressDeliveryBinding.lv.setLayoutManager(linearLayoutManager);
            //linearLayoutManager.setReverseLayout(true);
            deliveryBookExpressDeliveryBinding.lv.setAdapter(dataAdapter);

        //       ArrayAdapter<String> dataAdapter = new ArrayAdapter(context, R.layout.drop_address, labels);
            //        deliveryBookExpressDeliveryBinding.lv.setLayoutManager(linearLayoutManager);
        //    linearLayoutManager.setReverseLayout(true);
            // Drop down layout style - list view with radio button
            // dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // attaching data adapter to spinner
        //    deliveryBookExpressDeliveryBinding.lv.setAdapter(dataAdapter);

            deliveryBookExpressDeliveryBinding.lv.setVisibility(View.VISIBLE);
        }

        deliveryBookExpressDeliveryBinding.btnSubmit.setOnClickListener(this);
        deliveryBookExpressDeliveryBinding.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_submit:

//                DeliveryMultipleDTO deliveryMultipleDTO =  new DeliveryMultipleDTO();
//                deliveryMultipleDTO.setDeliveryDTO(deliveryDTO);
//                deliveryMultipleDTO.setMultipleDTOArrayList(db.getAllContacts());
//                System.out.println(new Gson().toJson(deliveryMultipleDTO));

                Utilities.hideKeyboard(deliveryBookExpressDeliveryBinding.btnSubmit);

                if(deliveryDTO.getDeliveryType().equalsIgnoreCase("multiple")){
                    callOrderMultipleBookApi();
                }else {
                    callOrderBookApi();
                }

//                Utilities.hideKeyboard(deliveryBookExpressDeliveryBinding.btnSubmit);
//                CardStripePayment cardStripePayment = new CardStripePayment();
//                Bundle bundle = new Bundle();
//                bundle.putParcelable("deliveryDTO", deliveryDTO);
//                cardStripePayment.setArguments(bundle);
//                addFragmentWithoutRemove(R.id.container_main, cardStripePayment, "cardStripePayment");

//                callOrderBookApi();
                break;
            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).popFragment();
                break;
        }
    }

//    public void callOrderBookApi() {
//        if (!utilities.isNetworkAvailable())
//            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
//        else {
//            final ProgressDialog mProgressDialog;
//            mProgressDialog = ProgressDialog.show(context, null, null);
//            mProgressDialog.setContentView(R.layout.progress_loader);
//            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            mProgressDialog.setCancelable(false);
//
//            Map<String, String> map = new HashMap<>();
//            map.put("user_id", appSession.getUser().getData().getUserId());
//            map.put("pickup_first_name", deliveryDTO.getPickupFirstName());
//            map.put("pickup_last_name", deliveryDTO.getPickupLastName());
//            map.put("pickup_mob_number", deliveryDTO.getPickupMobNumber());
//            map.put("pickupaddress", deliveryDTO.getPickupaddress());
//            map.put("delivery_date", deliveryDTO.getPickupDate());
//            map.put("pickup_special_inst", deliveryDTO.getPickupSpecialInst());
//            map.put("dropoff_first_name", deliveryDTO.getDropoffFirstName());
//            map.put("dropoff_last_name", deliveryDTO.getDropoffLastName());
//            map.put("dropoff_mob_number", deliveryDTO.getDropoffMobNumber());
//            map.put("dropoffaddress", deliveryDTO.getDropoffaddress());
//            map.put("parcel_height", deliveryDTO.getProductHeight());
//            map.put("parcel_width", deliveryDTO.getProductWidth());
//            map.put("parcel_lenght", deliveryDTO.getProductLength());
//            map.put("parcel_weight", deliveryDTO.getProductWeight());
//            map.put("delivery_type", deliveryDTO.getDeliveryType());
//            map.put("driver_delivery_cost", deliveryDTO.getDriverDeliveryCost());
//            map.put("delivery_distance", deliveryDTO.getDeliveryDistance());
//            try {
//                map.put("delivery_cost", String.format("%.2f", Double.parseDouble(deliveryDTO.getDeliveryCost())));
//            } catch (Exception e) {
//                map.put("delivery_cost", deliveryDTO.getDeliveryCost());
//                e.printStackTrace();
//            }
//            map.put("vehicle_type", deliveryDTO.getVehicleType());
//            map.put("pickUpLat", deliveryDTO.getPickupLat());
//            map.put("pickUpLong", deliveryDTO.getPickupLong());
//            map.put("dropOffLong", deliveryDTO.getDropoffLong());
//            map.put("dropOffLat", deliveryDTO.getDropoffLat());
//            map.put("delivery_time", deliveryDTO.getDeliveryTime());
//            map.put(PN_APP_TOKEN, APP_TOKEN);
//            map.put("dropoff_country_code", deliveryDTO.getDropoffCountryCode());
//            map.put("pickup_country_code", deliveryDTO.getPickupCountryCode());
//            map.put("pickup_elevator", deliveryDTO.getPickupLiftGate());
//            map.put("classGoods", deliveryDTO.getClassGoods());
//            map.put("typeGoods", deliveryDTO.getTypeGoods());
//            map.put("noOfPallets", deliveryDTO.getNoOfPallets());
//            map.put("is_pallet", deliveryDTO.getIs_pallet());
//
//            APIInterface apiInterface = APIClient.getClient();
//            Call<OtherDTO> call = apiInterface.callCreateOrderApi(map);
//            call.enqueue(new Callback<OtherDTO>() {
//                @Override
//                public void onResponse(Call<OtherDTO> call, Response<OtherDTO> response) {
//                    if (mProgressDialog != null && mProgressDialog.isShowing())
//                        mProgressDialog.dismiss();
//                    if (response.isSuccessful()) {
//                        try {
//                            if (response.body().getResult().equalsIgnoreCase("success")) {
//                                utilities.dialogOKre(context, "", response.body().getMessage(), getString(R.string.ok), new OnDialogConfirmListener() {
//                                    @Override
//                                    public void onYes() {
//                                        ((DrawerContentSlideActivity) context).popAllFragment();
//                                    }
//                                    @Override
//                                    public void onNo() {
//
//                                    }
//                                });
//                            } else {
//                                utilities.dialogOK(context, "", response.body().getMessage(), context.getString(R.string.ok), false);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<OtherDTO> call, Throwable t) {
//                    if (mProgressDialog != null && mProgressDialog.isShowing())
//                        mProgressDialog.dismiss();
//                    Log.e(TAG, t.toString());
//                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);
//
//                }
//            });
//        }
//    }



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
            map.put("pickup_first_name", deliveryDTO.getPickupFirstName());
            map.put("pickup_last_name", deliveryDTO.getPickupLastName());
            map.put("pickup_mob_number", deliveryDTO.getPickupMobNumber());
            map.put("pickupaddress", deliveryDTO.getPickupaddress());
            map.put("delivery_date", deliveryDTO.getPickupDate());
            map.put("pickup_special_inst", deliveryDTO.getPickupSpecialInst());
            map.put("dropoff_special_inst", deliveryDTO.getDropoffSpecialInst());
            map.put("dropoff_first_name", deliveryDTO.getDropoffFirstName());
            map.put("dropoff_last_name", deliveryDTO.getDropoffLastName());
            map.put("dropoff_mob_number", deliveryDTO.getDropoffMobNumber());
            map.put("dropoffaddress", deliveryDTO.getDropoffaddress());
            map.put("parcel_height", deliveryDTO.getProductHeight());
            map.put("parcel_width", deliveryDTO.getProductWidth());
            map.put("parcel_lenght", deliveryDTO.getProductLength());
            map.put("parcel_weight", deliveryDTO.getProductWeight());
            map.put("delivery_type", deliveryDTO.getDeliveryType());
            map.put("no_tax_delivery_cost", deliveryDTO.getNo_tax_delivery_cost());
            map.put("delivery_distance", "12333");
            map.put("pickup_building_type",deliveryDTO.getPickupBuildingType());
            map.put("drop_building_type",deliveryDTO.getDropBuildingType());
            map.put("drop_elevator",deliveryDTO.getDropElevator());
            map.put("type_of_truck",deliveryDTO.getType_of_truck());
            try {
                map.put("delivery_cost",String.format("%.2f", Double.parseDouble(deliveryDTO.getDeliveryCost())) );
                //map.put("delivery_distance",String.format("%.2f", Double.parseDouble(deliveryDTO.getTotal_distance())) );
              } catch (Exception e) {
                map.put("delivery_cost", deliveryDTO.getDeliveryCost());
                //map.put("delivery_distance",String.format("%.2f", Double.parseDouble(deliveryDTO.getTotal_distance())) );
                e.printStackTrace();
            }
            map.put("vehicle_type", deliveryDTO.getVehicleType());
            map.put("pickUpLat", deliveryDTO.getPickupLat());
            map.put("pickUpLong", deliveryDTO.getPickupLong());
            map.put("dropOffLong", deliveryDTO.getDropoffLong());
            map.put("dropOffLat", deliveryDTO.getDropoffLat());
            map.put("delivery_time", deliveryDTO.getDeliveryTime());
            map.put(PN_APP_TOKEN, APP_TOKEN);
            map.put("dropoff_country_code", deliveryDTO.getDropoffCountryCode());
            map.put("pickup_country_code", deliveryDTO.getPickupCountryCode());
            map.put("pickup_elevator", deliveryDTO.getPickupLiftGate());
            map.put("classGoods", deliveryDTO.getClassGoods());
            map.put("typeGoods", deliveryDTO.getTypeGoods());
            map.put("noOfPallets", deliveryDTO.getNoOfPallets());
            map.put("is_pallet", deliveryDTO.getIs_pallet());
            map.put("weight_unit", deliveryDTO.getWeight_unit());
            map.put("nameOfGoods",deliveryDTO.getNameOfGoods());
            map.put("typeGoodsCategory", deliveryDTO.getTypeGoodsCategory());
            System.out.println("good name-----> "+new Gson().toJson(deliveryDTO));
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
                    Log.e(TAG, t.toString());
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);
                }
            });
        }
    }

    public void callOrderMultipleBookApi() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);

            deliveryDTO.setUserId(appSession.getUser().getData().getUserId());
            if(deliveryDTO.getNo_tax_delivery_cost() != null)
            deliveryDTO.setDriverDeliveryCost(deliveryDTO.getNo_tax_delivery_cost());
            else
                deliveryDTO.setDriverDeliveryCost("0");

            if(deliveryDTO.getDeliveryDistance() != null){
                deliveryDTO.setDeliveryDistance(deliveryDTO.getDeliveryDistance());
            }else {
                deliveryDTO.setDeliveryDistance("0");
            }



            DeliveryMultipleDTO deliveryMultipleDTO =  new DeliveryMultipleDTO();
            //deliveryDTO.setDeliveryCost(String.format("%.2f", Double.parseDouble(MultipleAdd.totalPrice)));
            deliveryDTO.setDriverDeliveryCost(deliveryBookExpressDeliveryBinding.etPrice.getText().toString().trim());
          //  deliveryDTO.setDriverDeliveryCost(deliveryBookExpressDeliveryBinding.etDistance.getText().toString().trim());
            deliveryDTO.setDeliveryDistance(deliveryBookExpressDeliveryBinding.etDistance.getText().toString().trim());
            Log.e("error","imran"+deliveryBookExpressDeliveryBinding.etDistance.getText().toString().trim());
            deliveryMultipleDTO.setDeliveryDTO(deliveryDTO);
            deliveryMultipleDTO.setMultipleDTOArrayList(db.getAllContacts());
            System.out.println("DDKJHDGKDFHGKDHGKJD"+new Gson().toJson(deliveryMultipleDTO));
            DeliverySendMultipleDataDTO deliverySendMultipleDataDTO = new DeliverySendMultipleDataDTO();
            deliverySendMultipleDataDTO.setData(APP_TOKEN);
            deliverySendMultipleDataDTO.setDeliveryMultipleDTO(deliveryMultipleDTO);

            Gson gson = new Gson();
            String dattamu = gson.toJson(deliverySendMultipleDataDTO);
            Log.d("dattamu send", dattamu);


            APIInterface apiInterface = APIClient.getClient();
            Call<OtherDTO> call = apiInterface.createMultpileDropAPI(deliverySendMultipleDataDTO);
            call.enqueue(new Callback<OtherDTO>() {
                @Override
                public void onResponse(Call<OtherDTO> call, Response<OtherDTO> response) {

                    Gson gson = new Gson();
                    String dattamu = gson.toJson(response.body());
                    Log.d("dattamu success", dattamu);
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
                    Log.e(TAG, t.toString());
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);

                }
            });
        }
    }


}
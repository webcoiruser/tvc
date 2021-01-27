package com.pickanddrop.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import androidx.databinding.DataBindingUtil;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;


import com.google.gson.Gson;
import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.DeliveryDetailsBinding;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.dto.DeliveryMultipleDTO;
import com.pickanddrop.dto.DeliverySendMultipleDataDTO;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.OnDialogConfirmListener;
import com.pickanddrop.utils.Utilities;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeliveryDetails extends BaseFragment implements AppConstants, View.OnClickListener {

    private Context context;
    private AppSession appSession;
    private Utilities utilities;
    private String deliveryId = "";
    private DeliveryDetailsBinding deliveryDetailsBinding;
    private String TAG = DeliveryDetails.class.getName();
    private DeliveryDTO.Data data;
    private boolean historyStatus = false, nearByStatus = false, multiple_type = false,payment_status = false;
    private String trackRoute = "trackallroute";

    private int mYear, mMonth, mDay;
    private Calendar c ;
    private DatePickerDialog datePickerDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null && getArguments().containsKey("delivery")) {
            deliveryId = getArguments().getString("delivery");
            Log.e(TAG, deliveryId);

            if (getArguments().containsKey("history")) {
                historyStatus = true;
            }

            if (getArguments().containsKey("payment")) {
                payment_status = true;
            }


            if (getArguments().containsKey("nearByStatus")) {
                nearByStatus = true;
            }


        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        deliveryDetailsBinding = DataBindingUtil.inflate(inflater, R.layout.delivery_details, container, false);
        return deliveryDetailsBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        //Toast.makeText(context,"DeliveryDetails",Toast.LENGTH_LONG).show();
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);

        if (getArguments() != null && getArguments().containsKey("from_payment_request")) {
            deliveryDetailsBinding.toolbar.setVisibility(View.GONE);

        }
         c = Calendar.getInstance();


        callDeliveryDetailsApi();
        initView();
        initToolBar();
    }

    private void initToolBar() {

    }

    @SuppressLint("SetTextI18n")
    private void initView() {


        if (appSession.getUserType().equals(CUSTOMER)) {

         /*   if(data.getDeliveryType() == "multiple") {
                deliveryDetailsBinding.btnDeliver.setVisibility(View.GONE);

            }else {*/



         if(!payment_status){
                deliveryDetailsBinding.btnDeliver.setText(getString(R.string.reschedule));
                deliveryDetailsBinding.btnRoute.setText(getString(R.string.cancel));
         }else {
             deliveryDetailsBinding.btnDeliver.setText("Payment");
             deliveryDetailsBinding.btnRoute.setText(getString(R.string.cancel));


         }

          //  }

        }


        deliveryDetailsBinding.btnDeliver.setOnClickListener(this);
        deliveryDetailsBinding.btnRoute.setOnClickListener(this);
        deliveryDetailsBinding.btnReport.setOnClickListener(this);
        deliveryDetailsBinding.ivBack.setOnClickListener(this);
        deliveryDetailsBinding.btnRutingsys.setOnClickListener(this);
        deliveryDetailsBinding.etPickupDate.setEnabled(false);
        deliveryDetailsBinding.etPickupDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = Calendar.getInstance();

                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                deliveryDetailsBinding.etPickupDate.setText(Utilities.formatDateShow(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth));
                            }
                        }, mYear, mMonth, mDay);
//                calendar.add(Calendar.YEAR, -18);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        deliveryDetailsBinding.etPickupDate.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white_color)));
        deliveryDetailsBinding.etPickupDate.setEnabled(false);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).popFragment();
                break;
            case R.id.btn_rutingsys:
                Bundle bundlse = new Bundle();
                Route routse = new Route();
                bundlse.putParcelable("deliveryDTO", data);
                bundlse.putString("trackRoute",trackRoute);
                bundlse.putString("ordeersid",deliveryId);
                routse.setArguments(bundlse);
                addFragmentWithoutRemove(R.id.container_main, routse, "Route");
                break;
            case R.id.btn_route:
                if (appSession.getUserType().equals(DRIVER)) {
                    Bundle bundle = new Bundle();
                    Route route = new Route();
                    bundle.putParcelable("deliveryDTO", data);
                    route.setArguments(bundle);
                    addFragmentWithoutRemove(R.id.container_main, route, "Route");
                } else {
                    new AlertDialog.Builder(context)
                            .setMessage(getString(R.string.are_you_cancel))
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    callCancelDeliveryApi(false);
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
                break;

            case R.id.btn_deliver:
                System.out.println("rescheduleStatuskjg----> "+new Gson().toJson(data));
                if (appSession.getUserType().equals(DRIVER)) {
                    if (nearByStatus) {
                        callAcceptDeliveryApi(deliveryId, false);
                    } else {
                        if (data.getDeliveryStatus().equals("6")) {
                            callCancelDeliveryApi(true);
                        } else {
                            Bundle bundle = new Bundle();
                            DeliveryStatus deliveryStatus = new DeliveryStatus();
                            bundle.putParcelable("deliveryDTO", data);
                            deliveryStatus.setArguments(bundle);
                            addFragmentWithoutRemove(R.id.container_main, deliveryStatus, "DeliveryStatus");
                        }
                    }
                } else {

                    if (data.getIs_paid().equals("no") && !data.getDeliveryStatus().equals("1") && payment_status) {
//                        Utilities.hideKeyboard(deliveryBookExpressDeliveryBinding.btnSubmit);
                        CardStripePayment cardStripePayment = new CardStripePayment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("deliveryDTO", data);
                        cardStripePayment.setArguments(bundle);
                        addFragmentWithoutRemove(R.id.container_main, cardStripePayment, "cardStripePayment");

                    }
//                    else {
//                        CreateOrderExpressDelivery createOrderExpressDelivery = new CreateOrderExpressDelivery();
//                        Bundle bundle = new Bundle();
//                        if (data != null) {
//                            System.out.println("rescheduleStatuskjg----> "+new Gson().toJson(data));
//
//                            bundle.putParcelable("deliveryDTO", data);
//                            createOrderExpressDelivery.setArguments(bundle);
//                            addFragmentWithoutRemove(R.id.container_main, createOrderExpressDelivery, "CreateOrderExpressDelivery");
//                        }
//                    }

                    else {

                        if (deliveryDetailsBinding.btnDeliver.getText().equals("Reschedule")){
                            Toast.makeText(context, "reschedule", Toast.LENGTH_SHORT).show();
                            deliveryDetailsBinding.btnDeliver.setText("SUBMIT");
                            deliveryDetailsBinding.etPickupDate.setEnabled(true);
                            deliveryDetailsBinding.etPickupDate.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black_color)));


                            CreateOrderExpressDelivery createOrderExpressDelivery = new CreateOrderExpressDelivery();
                            Bundle bundle  = new Bundle();
//                            if (data != null){
//                                deliveryDetailsBinding.etPickupDate.setEnabled(true);
//                                System.out.println(" rescheduledata"+ new Gson().toJson(data));
//                                //deliveryDetailsBinding.etPickupDate.setEnabled(true);
//                                bundle.putParcelable("deliveryDTO", data);
//                                callRescheduleOrderAPI();
//
//
//                            }
//
                        }else if (deliveryDetailsBinding.btnDeliver.getText().equals("SUBMIT")){

                            Bundle bundle = new Bundle();
                            bundle.putParcelable("deliveryDTO", data);

                            //callRescheduleOrderBookApi();
                            //callCreateOPrderApi();
                            //callRescheduleOrderAPI();

                            callRescheduleOrderBookApi();
                            //callOrderBookApireschedule();
                        }

                    }
                }
                break;

            case R.id.btn_report:
                if (appSession.getUserType().equals(CUSTOMER)) {
                    CardStripePayment cardStripePayment = new CardStripePayment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("deliveryDTO", data);
                    cardStripePayment.setArguments(bundle);
                    addFragmentWithoutRemove(R.id.container_main, cardStripePayment, "cardStripePayment");
//                    callAcceptDeliveryApi(deliveryId, true);
                } else {
                    Bundle bundle = new Bundle();
                    ReportProblem reportProblem = new ReportProblem();
                    bundle.putParcelable("deliveryDTO", data);
                    reportProblem.setArguments(bundle);
                    addFragmentWithoutRemove(R.id.container_main, reportProblem, "ReportProblem");
                }
                break;
        }
    }

    public void callDeliveryDetailsApi() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);

            Map<String, String> map = new HashMap<>();
            map.put("delivery_id", deliveryId);
            map.put(PN_APP_TOKEN, APP_TOKEN);

            APIInterface apiInterface = APIClient.getClient();
            Call<DeliveryDTO> call = apiInterface.callDeliveryDetailsApi(map);
            call.enqueue(new Callback<DeliveryDTO>() {
                @Override
                public void onResponse(Call<DeliveryDTO> call, Response<DeliveryDTO> response) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {
                            if (response.body().getResult().equalsIgnoreCase("success")){

                                System.out.println("rescheduleStatusssss---->  "+new Gson().toJson(response.body().getData()));
                                data = response.body().getData();

                                if(data.getIs_paid().equals("no") && !data.getDeliveryStatus().equals("1") && !payment_status){

                                    deliveryDetailsBinding.btnReport.setVisibility(View.VISIBLE);
                                    if(data.getDeliveryStatus().equalsIgnoreCase("4")){
                                        deliveryDetailsBinding.btnRoute.setVisibility(View.VISIBLE);
                                    }

                                }
                                deliveryDetailsBinding.etPickupDate.setText(""+data.getPickupDate());
                                deliveryDetailsBinding.tvPickInside.setText("Inside Pickup" + " - " + data.getPickupBuildingType());
                                deliveryDetailsBinding.tvDropInside.setText("Inside Drop" + " - " + data.getDropBuildingType());
                                deliveryDetailsBinding.tvPickLiftgate.setText("Pickup Lift Gate" + " - " + data.getPickupLiftGate());
                                deliveryDetailsBinding.tvDropLiftgate.setText("Drop Lift Gate" + " - " + data.getDropElevator());

                                if(data.getDeliveryType().equalsIgnoreCase("Express")) {

                                    deliveryDetailsBinding.tvPickLiftgate.setVisibility(View.GONE);
                                    deliveryDetailsBinding.tvDropLiftgate.setVisibility(View.GONE);

                                }else{
                                    deliveryDetailsBinding.tvPickLiftgate.setVisibility(View.VISIBLE);
                                    deliveryDetailsBinding.tvDropLiftgate.setVisibility(View.VISIBLE);
                                }

                                if (data.getDeliveryStatus().equals("6")) {
                                    deliveryDetailsBinding.btnRutingsys.setVisibility(View.VISIBLE);

                                    trackRoute = "trackdriver";
                                    if(deliveryDetailsBinding.btnDeliver.getText().equals("Reschedule")){
                                        deliveryDetailsBinding.btnDeliver.setVisibility(View.GONE);
                                        deliveryDetailsBinding.btnRoute.setVisibility(View.VISIBLE);
                                    }else{
                                        deliveryDetailsBinding.btnDeliver.setVisibility(View.GONE);
                                        deliveryDetailsBinding.btnRoute.setVisibility(View.VISIBLE);
                                        deliveryDetailsBinding.btnDeliver.setVisibility(View.VISIBLE);

                                    }
                                }else{
                                    deliveryDetailsBinding.btnRutingsys.setVisibility(View.GONE);

                                }






                                if (data.getDeliveryStatus().equals("7")) {
                                    deliveryDetailsBinding.btnRutingsys.setVisibility(View.VISIBLE);

                                }

                                deliveryDetailsBinding.typeOfdel.setText(data.getDeliveryType());
                                deliveryDetailsBinding.tvPickName.setText(getString(R.string.name_txt) + " - " + data.getPickupFirstName() + " " + data.getPickupLastName());
                                deliveryDetailsBinding.tvPickMobile.setText(getString(R.string.mob_no_txt) + " - " + data.getPickupMobNumber());
                                deliveryDetailsBinding.tvDropInsttru.setText("Instruction" + " -" + data.getDropoffSpecialInst());
                                deliveryDetailsBinding.tvPickInstru.setText("Instruction"+ " -" +data.getPickupSpecialInst());
                                deliveryDetailsBinding.tvPickAddress.setText(getString(R.string.pickup_txt) + " - " + data.getPickupaddress());
                                deliveryDetailsBinding.tvDropName.setText(getString(R.string.name_txt) + " - " + data.getDropoffFirstName() + " " + data.getDropoffLastName());
                                deliveryDetailsBinding.tvDropMobile.setText(getString(R.string.mob_no_txt) + " - " + data.getDropoffMobNumber());
                                deliveryDetailsBinding.tvDropAddress.setText(getString(R.string.drop_off_txt) + " - " + data.getDropoffaddress());
                                if(data.getTypeGoods().equals("box") || data.getTypeGoods().equals("Box")) {
                                    deliveryDetailsBinding.tvParcelPallets.setText("No of Boxes " + " - " + data.getNoOfPallets());
                                }else if(data.getTypeGoods().equals("pallet") || data.getTypeGoods().equals("Pallet")){
                                    deliveryDetailsBinding.tvParcelPallets.setText(getString(R.string.parcel_p_txt) + " - " + data.getNoOfPallets());

                                }else  if(data.getTypeGoods().equals("miscellaneous")  || data.getTypeGoods().equals("Miscellaneous")){
                                    deliveryDetailsBinding.tvParcelPallets.setVisibility(View.GONE);
                                    deliveryDetailsBinding.tvParcelPallets.setText("Name of Miscellaneous " + " - " + data.getNoOfPallets());

                                }else  if(data.getTypeGoods().equals("envelop")  || data.getTypeGoods().equals("Envelop")) {
                                    deliveryDetailsBinding.tvParcelPallets.setText("No of Envelop " + " - " + data.getNoOfPallets());
                                }

                                if(data.getTypeGoods().equals("box") || data.getTypeGoods().equals("Box")) {
                                    deliveryDetailsBinding.tvParcelHeight.setVisibility(View.GONE);
                                    deliveryDetailsBinding.tvParcelWidth.setVisibility(View.GONE);
                                    deliveryDetailsBinding.tvParcelLenght.setVisibility(View.GONE);
                                }else if(data.getTypeGoods().equals("pallet") || data.getTypeGoods().equals("Pallet")){
                                    deliveryDetailsBinding.tvParcelHeight.setVisibility(View.GONE);
                                    deliveryDetailsBinding.tvParcelWidth.setVisibility(View.GONE);
                                    deliveryDetailsBinding.tvParcelLenght.setVisibility(View.GONE);

                                }else  if(data.getTypeGoods().equals("miscellaneous")  || data.getTypeGoods().equals("Miscellaneous")){
                                    deliveryDetailsBinding.tvParcelHeight.setVisibility(View.VISIBLE);
                                    deliveryDetailsBinding.tvParcelWidth.setVisibility(View.VISIBLE);
                                    deliveryDetailsBinding.tvParcelLenght.setVisibility(View.VISIBLE);

                                }else if(data.getTypeGoods().equals("envelop") || data.getTypeGoods().equals("Envelop")) {
                                    deliveryDetailsBinding.tvParcelHeight.setVisibility(View.GONE);
                                    deliveryDetailsBinding.tvParcelWidth.setVisibility(View.GONE);
                                    deliveryDetailsBinding.tvParcelLenght.setVisibility(View.GONE);
                                }



                                    deliveryDetailsBinding.tvParcelHeight.setText(getString(R.string.parcel_h_txt) + " - " + data.getProductHeight());
                                deliveryDetailsBinding.tvParcelWidth.setText(getString(R.string.parcel_wid_txt) + " - " + data.getProductWidth());
                                deliveryDetailsBinding.tvParcelLenght.setText(getString(R.string.parcel_l_txt) + " - " + data.getProductLength());
                              //  deliveryDetailsBinding.tvParcelWeight.setText(getString(R.string.parcel_w_txt) + " ("+ data.getWeight_unit()+") - " + data.getProductWeight());
                                if(data.getTypeGoods().equals("envelop") || data.getTypeGoods().equals("Envelop")) {
                                    deliveryDetailsBinding.tvParcelWeight.setText(getString(R.string.parcel_envelope) + " ("+ data.getWeight_unit()+") - " +data.getProductWeight());

                                }else {
                                    deliveryDetailsBinding.tvParcelWeight.setText(getString(R.string.parcel_w_txt) + " ("+ data.getWeight_unit()+") - " + data.getProductWeight());
                                }
                                deliveryDetailsBinding.tvRemainingTime.setText(getString(R.string.due_in) + " - " + data.getDeliveryTimeDuration());
//                                deliveryDetailsBinding.tvItemDesc.setText(getString(R.string.item_des_txt) + " - " + data.getItemDescription());


                                if (appSession.getUserType().equals(DRIVER)) {

                                    try {
                                        deliveryDetailsBinding.tvDeliveryCharges.setText(getString(R.string.amonunt_txt) + " - " + getString(R.string.us_dollar) + " " + String.format("%.2f", Double.parseDouble(data.getDriverDeliveryCost())));
                                    } catch (Exception e) {
                                        deliveryDetailsBinding.tvDeliveryCharges.setText(context.getString(R.string.us_dollar));
                                        e.printStackTrace();
                                    }

                                    deliveryDetailsBinding.btnReport.setVisibility(View.VISIBLE);
                                    if (data.getDeliveryStatus().equals("6")) {
                                        deliveryDetailsBinding.btnDeliver.setText(getString(R.string.pickup));
                                    }

                                    if (historyStatus) {
                                        deliveryDetailsBinding.llButton.setVisibility(View.GONE);
//                                        deliveryDetailsBinding.btnReport.setVisibility(View.VISIBLE);
                                   }

                                    if (nearByStatus) {
                                        deliveryDetailsBinding.btnDeliver.setVisibility(View.VISIBLE);
                                        deliveryDetailsBinding.btnRoute.setVisibility(View.VISIBLE);
                                        deliveryDetailsBinding.btnReport.setVisibility(View.GONE);
                                        deliveryDetailsBinding.btnDeliver.setText(getString(R.string.accept_txt));
                                    }
                                } else {
                                    try {
                                        deliveryDetailsBinding.tvDeliveryCharges.setText(getString(R.string.amonunt_txt) + " - " + getString(R.string.us_dollar) + " " + String.format("%.2f", Double.parseDouble(data.getDeliveryCost())));
                                    } catch (Exception e) {
                                        deliveryDetailsBinding.tvDeliveryCharges.setText(context.getString(R.string.us_dollar));
                                        e.printStackTrace();
                                    }

                                    if (data.getDeliveryStatus().equals("7") && !payment_status) {
                                        deliveryDetailsBinding.btnRoute.setEnabled(false);
                                        deliveryDetailsBinding.btnDeliver.setEnabled(false);
                                        deliveryDetailsBinding.btnRoute.setAlpha(Float.parseFloat("0.1"));
                                        deliveryDetailsBinding.btnDeliver.setAlpha(Float.parseFloat("0.1"));
                                    }

                                    if (historyStatus) {
                                        deliveryDetailsBinding.llButton.setVisibility(View.GONE);
                                        deliveryDetailsBinding.btnRutingsys.setVisibility(View.GONE);
//                                        deliveryDetailsBinding.btnReport.setVisibility(View.GONE);

                                        if (data.getDeliveryStatus().equals("8")) {
                                            deliveryDetailsBinding.btnDeliver.setVisibility(View.GONE);
                                            deliveryDetailsBinding.btnRoute.setVisibility(View.GONE);
                                            deliveryDetailsBinding.btnReport.setVisibility(View.GONE);
                                            deliveryDetailsBinding.btnRutingsys.setVisibility(View.GONE);
                                            deliveryDetailsBinding.btnReport.setText(getString(R.string.reorder));
                                     }

                                        if (data.getDeliveryStatus().equals("3")) {
                                            deliveryDetailsBinding.btnRutingsys.setVisibility(View.GONE);
                                            deliveryDetailsBinding.btnDeliver.setVisibility(View.GONE);
                                            deliveryDetailsBinding.btnRoute.setVisibility(View.GONE);
                                            deliveryDetailsBinding.btnReport.setVisibility(View.GONE);
                                            deliveryDetailsBinding.btnReport.setText(getString(R.string.reorder));
                                        }

                                    }
                                }

                                if (data.getDeliveryStatus().equals("1")) {
                                    deliveryDetailsBinding.btnRoute.setVisibility(View.VISIBLE);
                                    deliveryDetailsBinding.btnDeliver.setVisibility(View.VISIBLE);
                                    deliveryDetailsBinding.btnRutingsys.setVisibility(View.GONE);
                                    deliveryDetailsBinding.btnReport.setVisibility(View.GONE);


                                }

//
//                                if (deliveryDetailsBinding.btnDeliver.getText().equals("Reschedule")){
//                                    deliveryDetailsBinding.btnDeliver.setText("SUBMIT");
////                                    multiListBinding.etPickupDate.setEnabled(true);
////                                    multiListBinding.etPickupTime.setEnabled(true);
////                                    multiListBinding.etPickupDate.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black_color)));
////                                    multiListBinding.etPickupTime.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black_color)));
//                                    deliveryDetailsBinding.etPickupDate.setEnabled(true);
//                                    deliveryDetailsBinding.etPickupDate.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.black_color)));
//
//
//                                }
                                else if (deliveryDetailsBinding.btnDeliver.getText().equals("SUBMIT")){

//                                    callRescheduleOrderAPI();

                                    deliveryDetailsBinding.etPickupDate.setEnabled(true);
                                }


                            } else {
                                utilities.dialogOK(context, "", response.body().getMessage(), context.getString(R.string.ok), false);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<DeliveryDTO> call, Throwable t) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);
                    Log.e(TAG, t.toString());
                }
            });
        }
    }


    public void callOrderBookApireschedule() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {

            Log.d(TAG, "callOrderBookApireschedule: "+new Gson().toJson(data));
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);
            Map<String, String> map = new HashMap<>();
            map.put("user_id", appSession.getUser().getData().getUserId());
            map.put("pickup_first_name", data.getPickupFirstName());
            map.put("pickup_last_name", data.getPickupLastName());
            map.put("pickup_mob_number", data.getPickupMobNumber());
            map.put("pickupaddress", data.getPickupaddress());
            map.put("delivery_date", data.getPickupDate());
            map.put("pickup_special_inst", data.getPickupSpecialInst());
            map.put("dropoff_special_inst",data.getDropoffSpecialInst());
            map.put("dropoff_first_name", data.getDropoffFirstName());
            map.put("dropoff_last_name", data.getDropoffLastName());
            map.put("dropoff_mob_number", data.getDropoffMobNumber());
            map.put("dropoffaddress", data.getDropoffaddress());
            map.put("parcel_height", data.getProductHeight());
            map.put("parcel_width", data.getProductWidth());
            map.put("parcel_lenght", data.getProductLength());
            map.put("parcel_weight", data.getProductWeight());
            map.put("delivery_type", data.getDeliveryType());
            map.put("no_tax_delivery_cost", data.getNo_tax_delivery_cost());
            map.put("delivery_distance", data.getDeliveryDistance());
            map.put("pickup_building_type",data.getPickupBuildingType());
            map.put("drop_building_type",data.getDropBuildingType());
            map.put("drop_elevator",data.getDropElevator());
            //map.put("type_of_truck",data.getType_of_truck());
            try {
                map.put("delivery_cost",String.format("%.2f", Double.parseDouble(data.getDeliveryCost())) );
            } catch (Exception e) {
                map.put("delivery_cost", data.getDeliveryCost());
                e.printStackTrace();
            }
            map.put("vehicle_type", data.getVehicleType());
            map.put("pickUpLat", data.getPickupLat());
            map.put("pickUpLong", data.getPickupLong());
            map.put("dropOffLong", data.getDropoffLong());
            map.put("dropOffLat", data.getDropoffLat());
            map.put("delivery_time", data.getDeliveryTime());
            map.put(PN_APP_TOKEN, APP_TOKEN);
            map.put("dropoff_country_code", data.getDropoffCountryCode());
            map.put("pickup_country_code", data.getPickupCountryCode());
            map.put("pickup_elevator", data.getPickupLiftGate());
            map.put("classGoods", data.getClassGoods());
            map.put("typeGoods", data.getTypeGoods());
            map.put("noOfPallets", data.getNoOfPallets());
            map.put("is_pallet", data.getIs_pallet());
            map.put("weight_unit", data.getWeight_unit());
            map.put("nameOfGoods",data.getNameOfGoods());
            map.put("typeGoodsCategory", data.getTypeGoodsCategory());
            System.out.println("good name-----> "+new Gson().toJson(data));
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





    public void callCreateOPrderApi() {
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
            map.put("pickup_first_name", data.getPickupFirstName());
            map.put("pickup_last_name", data.getPickupLastName());
            map.put("pickup_mob_number", data.getPickupMobNumber());
            map.put("pickupaddress", data.getPickupaddress());
            map.put("delivery_date", deliveryDetailsBinding.etPickupDate.getText().toString());
            map.put("pickup_special_inst", data.getPickupSpecialInst());
            map.put("dropoff_special_inst", data.getDropoffSpecialInst());
            map.put("dropoff_first_name", data.getDropoffFirstName());
            map.put("dropoff_last_name", data.getDropoffLastName());
            map.put("dropoff_mob_number", data.getDropoffMobNumber());
            map.put("dropoffaddress", data.getDropoffaddress());
            map.put("parcel_height", data.getProductHeight());
            map.put("parcel_width", data.getProductWidth());
            map.put("parcel_lenght", data.getProductLength());
            map.put("parcel_weight", data.getProductWeight());
            map.put("delivery_type", data.getDeliveryType());
            map.put("no_tax_delivery_cost", data.getNo_tax_delivery_cost());
            map.put("delivery_distance", data.getDeliveryDistance());
            map.put("pickup_building_type",data.getPickupBuildingType());
            map.put("drop_building_type",data.getDropBuildingType());
            map.put("drop_elevator",data.getDropElevator());
           // map.put("type_of_truck",data.getType_of_truck());
            try {
                map.put("delivery_cost",String.format("%.2f", Double.parseDouble(data.getDeliveryCost())) );
            } catch (Exception e) {
                map.put("delivery_cost", data.getDeliveryCost());
                e.printStackTrace();
            }
           // map.put("vehicle_type", data.getVehicleType());
            map.put("pickUpLat", data.getPickupLat());
            map.put("pickUpLong", data.getPickupLong());
            map.put("dropOffLong", data.getDropoffLong());
            map.put("dropOffLat", data.getDropoffLat());
            map.put("delivery_time", data.getDeliveryTime());
            map.put(PN_APP_TOKEN, APP_TOKEN);
            map.put("dropoff_country_code", data.getDropoffCountryCode());
            map.put("pickup_country_code", data.getPickupCountryCode());
            map.put("pickup_elevator", data.getPickupLiftGate());
            map.put("classGoods", data.getClassGoods());
            map.put("typeGoods", data.getTypeGoods());
            map.put("noOfPallets", data.getNoOfPallets());
            map.put("is_pallet", data.getIs_pallet());
            map.put("weight_unit", data.getWeight_unit());
            map.put("nameOfGoods",data.getNameOfGoods());
            map.put("typeGoodsCategory", data.getTypeGoodsCategory());
            System.out.println("forme-----> "+new Gson().toJson(data));
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
            map.put(PN_APP_TOKEN, APP_TOKEN);
            map.put("user_id", appSession.getUser().getData().getUserId());
            map.put("order_id", data.getOrderId());
            map.put("pickup_comapny_name", data.getPickupComapnyName());
            map.put("pickup_first_name", data.getPickupFirstName());
            map.put("pickup_last_name", data.getPickupLastName());
            map.put("pickup_mob_number", data.getPickupMobNumber());
            map.put("pickupaddress", data.getPickupaddress());
            map.put("item_description", data.getItemDescription());
            map.put("item_quantity", data.getItemQuantity());
            map.put("delivery_date", deliveryDetailsBinding.etPickupDate.getText().toString());
            map.put("pickup_special_inst", data.getPickupSpecialInst());
            map.put("dropoff_first_name", data.getDropoffFirstName());
            map.put("dropoff_last_name", data.getDropoffLastName());
            map.put("dropoff_mob_number", data.getDropoffMobNumber());
            map.put("dropoff_special_inst", data.getDropoffSpecialInst());
            map.put("dropoffaddress", data.getDropoffaddress());
            map.put("parcel_height", data.getProductHeight());
            map.put("parcel_width", data.getProductWidth());
            map.put("parcel_lenght", data.getProductLength());
            map.put("parcel_weight", data.getProductWeight());
            map.put("delivery_type", data.getDeliveryType());
            map.put("driver_delivery_cost", data.getDriverDeliveryCost());
            map.put("delivery_distance", data.getDeliveryDistance());
            map.put("delivery_cost", data.getDeliveryCost());
            map.put("dropoff_comapny_name", data.getDropoffComapnyName());
//            map.put("vehicle_type", data.getVehicleType());
            map.put("pickUpLat", data.getPickupLat());
            map.put("pickUpLong", data.getPickupLong());
            map.put("dropOffLong", data.getDropoffLong());
            map.put("dropOffLat", data.getDropoffLat());
            map.put("delivery_time", data.getDeliveryTime());
            map.put("is_pallet", data.getIs_pallet());
            map.put("dropoff_country_code", data.getDropoffCountryCode());
            map.put("pickup_country_code", data.getPickupCountryCode());

            Log.d(TAG, "callRescheduleOrderBookApi: "+new Gson().toJson(map));
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






    // here is im adding rescheduApi


    public void callMultipleRescheduleOrderAPI() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);
            data.setUserId(appSession.getUser().getData().getUserId());
            //System.out.println("etPickupDate---> "+ multiListBinding.etPickupDate.getText().toString());
            data.setPickupDate(deliveryDetailsBinding.etPickupDate.getText().toString());
            System.out.println("etPickupDate---> "+ data.getPickupDate());
            //deliveryDTO.setDeliveryTime(multiListBinding.etPickupTime.getText().toString());
            DeliveryMultipleDTO deliveryMultipleDTO =  new DeliveryMultipleDTO();
            deliveryMultipleDTO.setDeliveryDTO(data);
            //deliveryMultipleDTO.setMultipleDTOArrayList(contacts);
            System.out.println(new Gson().toJson(deliveryMultipleDTO));
            Log.d(TAG, "callRescheduleMultipleOrderAPI: "+ new Gson().toJson(deliveryMultipleDTO));
            DeliverySendMultipleDataDTO deliverySendMultipleDataDTO = new DeliverySendMultipleDataDTO();
            deliverySendMultipleDataDTO.setData(APP_TOKEN);
            deliverySendMultipleDataDTO.setDeliveryMultipleDTO(deliveryMultipleDTO);
            APIInterface apiInterface = APIClient.getClient();
            Call<OtherDTO> call = apiInterface.rescheduleMultipleOrderAPI(deliverySendMultipleDataDTO);
            call.enqueue(new Callback<OtherDTO>() {
                @Override
                public void onResponse(Call<OtherDTO> call, Response<OtherDTO> response) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {

                            if (response.body().getResult().equalsIgnoreCase("success")) {
                                //multiListBinding.btnSignup.setText("RESCHEDULE");
                                deliveryDetailsBinding.etPickupDate.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white_color)));
//                                multiListBinding.etPickupTime.setTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white_color)));
                                deliveryDetailsBinding.etPickupDate.setEnabled(false);
//                                multiListBinding.etPickupTime.setEnabled(false);
                                utilities.dialogOKre(context, "", response.body().getMessage(), getString(R.string.ok), new OnDialogConfirmListener() {
                                    @Override
                                    public void onYes() {
                                        ((DrawerContentSlideActivity) context).popAllFragment();
                                    }
                                    @Override
                                    public void onNo() {
                                    }
                                });
                            }else {
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






    public void callCancelDeliveryApi(final boolean pickUp) {
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
            map.put("order_id", deliveryId);
            map.put(PN_APP_TOKEN, APP_TOKEN);

            Call<OtherDTO> call;
            APIInterface apiInterface = APIClient.getClient();
            if (pickUp) {
                call = apiInterface.callPickupDeliveriesForDriverApi(map);
            } else {
                call = apiInterface.callCancelOrderApi(map);
            }
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
                                        ((DrawerContentSlideActivity) context).popFragment();
                                        if (!pickUp)
                                            replaceFragmentWithoutBack(R.id.container_main, new CurrentList(), "CurrentList");
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
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);
                    Log.e(TAG, t.toString());
                }
            });
        }
    }

    public void callAcceptDeliveryApi(String orderId, final boolean reorderStatus) {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);

            Map<String, String> map = new HashMap<>();
            map.put(PN_APP_TOKEN, APP_TOKEN);
            map.put("user_id", appSession.getUser().getData().getUserId());
            map.put("order_id", orderId);

            APIInterface apiInterface = APIClient.getClient();
            Call<OtherDTO> call;
            if (reorderStatus) {
                call = apiInterface.callReOrderApi(map);
            } else {
                call = apiInterface.callAcceptDeliveriesForDriverApi(map);
            }

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
                                        ((DrawerContentSlideActivity) context).popFragment();
//                                        ((DrawerContentSlideActivity) context).onBackPressed();
                                        if (!reorderStatus) {
                                            ((DrawerContentSlideActivity) context).popFragment();
                                        } else {
                                            Bundle bundle = new Bundle();
                                            CurrentList currentList = new CurrentList();
                                            bundle.putString(PN_VALUE, PN_VALUE);
                                            currentList.setArguments(bundle);
                                            replaceFragmentWithoutBack(R.id.container_main, currentList, "CurrentList");
                                        }
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
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);
                    Log.e(TAG, t.toString());
                }
            });
        }
    }
}
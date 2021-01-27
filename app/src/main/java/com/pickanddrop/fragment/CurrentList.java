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

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.gson.Gson;
import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.adapter.CurrentDeliveryAdapter;
import com.pickanddrop.adapter.DriverDeliveryAdapter;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.RvListBinding;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.OnItemClickListener;
import com.pickanddrop.utils.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CurrentList extends BaseFragment implements View.OnClickListener, AppConstants {

    private Context context;
    private AppSession appSession;
    private Utilities utilities;
    private LinearLayoutManager linearLayoutManager;
    private RvListBinding rvListBinding;
    private CurrentDeliveryAdapter currentDeliveryAdapter;
    private DriverDeliveryAdapter driverDeliveryAdapter;
    private String status = "";
    private ArrayList<DeliveryDTO.Data> deliveryDTOArrayList;
    private String TAG = CurrentList.class.getName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(PN_NAME)) {
                status = getString(R.string.notification);
            } else if (getArguments().containsKey(PN_VALUE)) {
                status = getString(R.string.delivery_history);
            } else {
                status = getString(R.string.cur_delivery);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rvListBinding = DataBindingUtil.inflate(inflater, R.layout.rv_list, container, false);
        return rvListBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);

        initView();
        initToolbar();
        callUserDeliveryApi();
        callRemoveNotifyCountApi();

    }

    private void initToolbar() {

        if (status.equalsIgnoreCase(getString(R.string.notification))) {
            rvListBinding.toolbarTitle.setText(getString(R.string.notification));
        } else if (status.equalsIgnoreCase(getString(R.string.delivery_history))) {
            rvListBinding.toolbarTitle.setText(getString(R.string.delivery_history));
        } else {
            rvListBinding.toolbarTitle.setText(getString(R.string.cur_delivery));
        }
    }

    private void initView() {
        deliveryDTOArrayList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(context);
        rvListBinding.rvDeliveries.setLayoutManager(linearLayoutManager);
        rvListBinding.ivBack.setOnClickListener(this);
    }

    OnItemClickListener.OnItemClickCallback onItemClickCallback = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {
            DeliveryDetails deliveryDetails = new DeliveryDetails();
            Bundle bundle = new Bundle();
            if (status.equalsIgnoreCase(getString(R.string.notification))) {
//                if(!deliveryDTOArrayList.get(position).getDeliveryType().equalsIgnoreCase("multiple")){
//                    bundle.putString("history", "history");
//                    bundle.putString("delivery", deliveryDTOArrayList.get(position).getOrderId());
//                    deliveryDetails.setArguments(bundle);
//                    addFragmentWithoutRemove(R.id.container_main, deliveryDetails, "DeliveryDetails");
//                }else {
//                    MultipleAdd multipleAdd = new MultipleAdd();
//                    bundle.putString("delivery", deliveryDTOArrayList.get(position).getOrderId());
//                    bundle.putString("multiple_data", "multiple_data");
//                    bundle.putString("history", "history");
//                    multipleAdd.setArguments(bundle);
//                    addFragmentWithoutRemove(R.id.container_main, multipleAdd, "MultipleAdd");
//                }
            } else if (status.equalsIgnoreCase(getString(R.string.delivery_history))) {
                if(!deliveryDTOArrayList.get(position).getDeliveryType().equalsIgnoreCase("multiple")){
                    bundle.putString("history", "history");
                    bundle.putString("delivery", deliveryDTOArrayList.get(position).getOrderId());
                    deliveryDetails.setArguments(bundle);
                    addFragmentWithoutRemove(R.id.container_main, deliveryDetails, "DeliveryDetails");
                }else {
                    MultipleAdd multipleAdd = new MultipleAdd();
                    bundle.putString("delivery", deliveryDTOArrayList.get(position).getOrderId());
                    bundle.putString("multiple_data", "multiple_data");
                    bundle.putString("history", "history");
                    multipleAdd.setArguments(bundle);
                    addFragmentWithoutRemove(R.id.container_main, multipleAdd, "MultipleAdd");
                }
            } else {

                if(!deliveryDTOArrayList.get(position).getDeliveryType().equalsIgnoreCase("multiple")){
                    bundle.putString("delivery", deliveryDTOArrayList.get(position).getOrderId());
                    deliveryDetails.setArguments(bundle);
                    addFragmentWithoutRemove(R.id.container_main, deliveryDetails, "DeliveryDetails");
                }else {
                    MultipleAdd multipleAdd = new MultipleAdd();
                    bundle.putString("delivery", deliveryDTOArrayList.get(position).getOrderId());
                    bundle.putString("multiple_data", "multiple_data");
                    multipleAdd.setArguments(bundle);
                    addFragmentWithoutRemove(R.id.container_main, multipleAdd, "MultipleAdd");
                }

            }
        }
    };
    OnItemClickListener.OnItemClickCallback onitemDeletelistner= new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {
            if (status.equalsIgnoreCase(getString(R.string.notification))) {
                if (!utilities.isNetworkAvailable()) {
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
                }  else {
                    final ProgressDialog mProgressDialog;
                    mProgressDialog = ProgressDialog.show(context, null, null);
                    mProgressDialog.setContentView(R.layout.progress_loader);
                    mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    mProgressDialog.setCancelable(false);

                    Map<String, String> map = new HashMap<>();
                    map.put("user_id", appSession.getUser().getData().getUserId());
                    map.put("mode","user");
                    map.put("order_id", deliveryDTOArrayList.get(position).getOrderId());
                    map.put(PN_APP_TOKEN, APP_TOKEN);
                    map.put("id", deliveryDTOArrayList.get(position).getNitifiId());
                    map.put("message", deliveryDTOArrayList.get(position).getMessage());
                    APIInterface apiInterface = APIClient.getClient();

                    Call<OtherDTO> call = apiInterface.deleteNotification(map);
                    call.enqueue(new Callback<OtherDTO>() {
                        @Override
                        public void onResponse(Call<OtherDTO> call, Response<OtherDTO> response) {
                            if (mProgressDialog != null && mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            if (response.isSuccessful()){
                                callUserDeliveryApi();
                                System.out.println(" NotificationDataaa--->----> " + new Gson().toJson(response.body()));
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

            } else if (status.equalsIgnoreCase(getString(R.string.delivery_history))) {
                if (!utilities.isNetworkAvailable()) {
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
                }  else {
                    final ProgressDialog mProgressDialog;
                    mProgressDialog = ProgressDialog.show(context, null, null);
                    mProgressDialog.setContentView(R.layout.progress_loader);
                    mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    mProgressDialog.setCancelable(false);

                    Map<String, String> map = new HashMap<>();
                    map.put("user_id", appSession.getUser().getData().getUserId());
                    map.put("mode","user");
                    map.put("order_id", deliveryDTOArrayList.get(position).getOrderId());
                    map.put(PN_APP_TOKEN, APP_TOKEN);
                    //map.put("id", deliveryDTOArrayList.get(position).);
                    APIInterface apiInterface = APIClient.getClient();

                    Call<OtherDTO> call = apiInterface.deleteOrdersUser(map);
                    call.enqueue(new Callback<OtherDTO>() {
                        @Override
                        public void onResponse(Call<OtherDTO> call, Response<OtherDTO> response) {
                            if (mProgressDialog != null && mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            if (response.isSuccessful()){
                                System.out.println(" Compleetedordersdeliver--->----> " + new Gson().toJson(response.body()));
                                callUserDeliveryApi();
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
    };


    OnItemClickListener.OnItemClickCallback onItemClickCallbackDriver = new OnItemClickListener.OnItemClickCallback() {
        @Override
        public void onItemClicked(View view, int position) {
            DeliveryDetails deliveryDetails = new DeliveryDetails();
            Bundle bundle = new Bundle();
            if (status.equalsIgnoreCase(getString(R.string.notification))) {
            } else if (status.equalsIgnoreCase(getString(R.string.delivery_history))) {
                bundle.putString("history", "history");

                bundle.putString("delivery", deliveryDTOArrayList.get(position).getOrderId());
                deliveryDetails.setArguments(bundle);
                addFragmentWithoutRemove(R.id.container_main, deliveryDetails, "DeliveryDetails");
            } else {
                bundle.putString("delivery", deliveryDTOArrayList.get(position).getOrderId());
                deliveryDetails.setArguments(bundle);
                addFragmentWithoutRemove(R.id.container_main, deliveryDetails, "DeliveryDetails");
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).openMenuDrawer();
                break;
        }
    }

    public void callUserDeliveryApi() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);

            APIInterface apiInterface = APIClient.getClient();
            Call<DeliveryDTO> call;
            Map<String, String> map = new HashMap<>();
            map.put(PN_APP_TOKEN, APP_TOKEN);
            System.out.println("appSessionssssssID--->" + appSession.getUser().getData().getUserId());
            if (status.equalsIgnoreCase(getString(R.string.notification))) {
                map.put("user_id", appSession.getUser().getData().getUserId());
                call = apiInterface.callNotificationListApi(map);
            } else if (status.equalsIgnoreCase(getString(R.string.delivery_history))) {
                map.put("userid", appSession.getUser().getData().getUserId());
                if (appSession.getUserType().equals(DRIVER)) {
                    call = apiInterface.callDriverHistoryDeliveryApi(map);
                } else {
                    call = apiInterface.callUserHistoryDeliveryApi(map);
                }
            } else {
                map.put("user_id", appSession.getUser().getData().getUserId());
                if (appSession.getUserType().equals(DRIVER)) {
                    call = apiInterface.callDriverCurrentDeliveryApi(map);
                } else {
                    call = apiInterface.callUserCurrentDeliveryApi(map);
                }
            }

            call.enqueue(new Callback<DeliveryDTO>() {
                @Override
                public void onResponse(Call<DeliveryDTO> call, Response<DeliveryDTO> response) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    if (response.isSuccessful()) {
                        try {

                            Handler refresh = new Handler(Looper.getMainLooper());
                            refresh.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (response.body().getResult().equalsIgnoreCase("success")) {


                                        System.out.println("responsepadddgess--->" + response.body().getData());
                                        rvListBinding.rvDeliveries.setVisibility(View.VISIBLE);
                                        rvListBinding.tvNoRecord.setVisibility(View.GONE);
                                        deliveryDTOArrayList = (ArrayList<DeliveryDTO.Data>) response.body().getDataList();

                                        Log.e(TAG, "Size is >>>>"+deliveryDTOArrayList.size());

                                        if (appSession.getUserType().equals(DRIVER)) {
                                            driverDeliveryAdapter = new DriverDeliveryAdapter(context, deliveryDTOArrayList, onItemClickCallbackDriver, status);
                                            rvListBinding.rvDeliveries.setAdapter(driverDeliveryAdapter);
                                        } else {
                                            System.out.println("deliveryDTOArrayListgggg--->" + new Gson().toJson(deliveryDTOArrayList));
                                            currentDeliveryAdapter = new CurrentDeliveryAdapter(context, deliveryDTOArrayList, onItemClickCallback,onitemDeletelistner, status);
                                            rvListBinding.rvDeliveries.setAdapter(currentDeliveryAdapter);
                                        }
                                    } else {
                                        rvListBinding.rvDeliveries.setVisibility(View.GONE);
                                        rvListBinding.tvNoRecord.setVisibility(View.VISIBLE);

                                        if(status.equals(getString(R.string.notification)))
                                            rvListBinding.tvNoRecord.setText("No notification !");
                                        else if(status.equals(getString(R.string.notification)))
                                            rvListBinding.tvNoRecord.setText("No delivery history !");
                                        else
                                            rvListBinding.tvNoRecord.setText("No orders !");

//                                utilities.dialogOK(context, "", response.body().getMessage(), context.getString(R.string.ok), false);
                                    }


                                }
                            });


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

    public void callRemoveNotifyCountApi() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            Map<String, String> map = new HashMap<>();
            map.put("user_id", appSession.getUser().getData().getUserId());
            map.put(PN_APP_TOKEN, APP_TOKEN);

            APIInterface apiInterface = APIClient.getClient();
            Call<OtherDTO> call = apiInterface.callRemoveNotifyCount(map);
            call.enqueue(new Callback<OtherDTO>() {
                @Override
                public void onResponse(Call<OtherDTO> call, Response<OtherDTO> response) {
                    if (response.isSuccessful()) {
                        try {
                            if (response.body().getResult().equalsIgnoreCase("success")) {
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
                    utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);
                    Log.e(TAG, t.toString());
                }
            });
        }
    }
}

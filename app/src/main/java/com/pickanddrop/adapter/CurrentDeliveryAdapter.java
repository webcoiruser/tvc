package com.pickanddrop.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.fragment.CurrentList;
import com.pickanddrop.fragment.DeliveryDetails;
import com.pickanddrop.fragment.MultipleAdd;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.ImageViewCircular;
import com.pickanddrop.utils.OnItemClickListener;
import com.pickanddrop.utils.Utilities;


import java.util.ArrayList;

public class CurrentDeliveryAdapter extends  RecyclerView.Adapter<CurrentDeliveryAdapter.ViewHolder> implements AppConstants {

    private Context context;
    private OnItemClickListener.OnItemClickCallback onItemdeleteItems;
    private OnItemClickListener.OnItemClickCallback onItemClickCallback;
    private AppSession appSession;
    private Utilities utilities;
    private RequestOptions requestOptions, requestOptions1;
    private String status = "";
    private ArrayList<DeliveryDTO.Data> deliveryDTOArrayList;
    CurrentList currentList = new CurrentList();
    Bundle bundle = new Bundle();

    public CurrentDeliveryAdapter(Context context, ArrayList<DeliveryDTO.Data> deliveryDTOArrayList, OnItemClickListener.OnItemClickCallback onItemClickCallback,OnItemClickListener.OnItemClickCallback onItemdeleteItems, String status) {
        this.context = context;
        this.onItemClickCallback = onItemClickCallback;
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);
        this.status = status;
        this.deliveryDTOArrayList = deliveryDTOArrayList;
        requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        this.onItemdeleteItems = onItemdeleteItems;
        requestOptions.override(150, 150);
        requestOptions.placeholder(R.drawable.user_praba);
        requestOptions.error(R.drawable.user_praba);
        requestOptions1 = new RequestOptions();
        requestOptions1.override(100, 100);
        requestOptions1.placeholder(R.drawable.truck_list);
        requestOptions1.error(R.drawable.truck_list);
    }

    @Override
    public CurrentDeliveryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
          View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_current_deliveries, parent, false);
             return new CurrentDeliveryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CurrentDeliveryAdapter.ViewHolder viewHolder, int position) {

        //        viewHolder.tvDeliveryId.setText();
//        viewHolder.tvDeliveryDate.setText();
//        viewHolder.tvPickLoc.setText();
//        viewHolder.tvDropLoc.setText();
//        viewHolder.tvDeliveryContactName.setText();
//        viewHolder.tvDeliveryContactNo.setText();
//        viewHolder.tvDriverName.setText();
//        viewHolder.tvDriverNo.setText();
//
//        Glide.with(context)
//                .setDefaultRequestOptions(requestOptions)
//                .load(imagePath)
//                .into(viewHolder.ivProfile);

        try {
            if (status.equalsIgnoreCase(context.getString(R.string.notification))) {

                //System.out.println("deliveryDTOArrayList--->" + new Gson().toJson(deliveryDTOArrayList));
                viewHolder.tvDelete.setVisibility(View.VISIBLE);
                viewHolder.tvDropLoc.setVisibility(View.GONE);

                viewHolder.tvDeliveryContactNo.setVisibility(View.GONE);
                viewHolder.tvDeliveryContactName.setVisibility(View.GONE);
                viewHolder.tvPrice.setVisibility(View.GONE);

                if(!deliveryDTOArrayList.get(position).getDeliveryStatus().equalsIgnoreCase("1")){
                    if(deliveryDTOArrayList.get(position).getIs_paid().equals("no")){
                        viewHolder.paidstatus.setTextColor(context.getResources().getColor(R.color.sighup_red));
                        viewHolder.paidstatus.setText("Not paid");
                    }else if(deliveryDTOArrayList.get(position).getIs_paid().equals("yes")){
                        viewHolder.paidstatus.setTextColor(context.getResources().getColor(R.color.togel));
                        viewHolder.paidstatus.setText("Paid");
                    }
                    viewHolder.paidstatus.setVisibility(View.GONE);
                }else {
                    viewHolder.paidstatus.setVisibility(View.GONE);
                }

                viewHolder.tvDeliveryId.setText(context.getString(R.string.delivery_id_txt) + " - " + deliveryDTOArrayList.get(position).getOrderId());
                viewHolder.tvDeliveryDate.setText(context.getString(R.string.pickup_contact_name) + " - " + deliveryDTOArrayList.get(position).getPickupFirstName() + " " + deliveryDTOArrayList.get(position).getPickupLastName());

                if(deliveryDTOArrayList.get(position).getPickupDate() != null && !deliveryDTOArrayList.get(position).getPickupDate().equalsIgnoreCase("") && !deliveryDTOArrayList.get(position).getPickupDate().equalsIgnoreCase("null")) {
                    viewHolder.tvPickLoc.setVisibility(View.VISIBLE);
                    viewHolder.tvPickLoc.setText(context.getString(R.string.delivery_datein_txt) + " - " + deliveryDTOArrayList.get(position).getPickupDate());
                }else {
                    viewHolder.tvPickLoc.setVisibility(View.GONE);
                    viewHolder.tvPickLoc.setText("");
                }

                if(deliveryDTOArrayList.get(position).getDriver_firstname() != null && !deliveryDTOArrayList.get(position).getDriver_firstname().equalsIgnoreCase("") && !deliveryDTOArrayList.get(position).getDriver_firstname().equalsIgnoreCase("null")) {
                    viewHolder.tvDriverName.setVisibility(View.VISIBLE);
                    viewHolder.tvDriverName.setText(context.getString(R.string.driver_name) +" - "+ deliveryDTOArrayList.get(position).getDriver_firstname()+" "+deliveryDTOArrayList.get(position).getDriver_lastname());
                    Glide.with(context)
                            .setDefaultRequestOptions(requestOptions)
                            .load(DrawerContentSlideActivity.profilepicture)
                            .into(viewHolder.ivProfile);

                }else {
                    viewHolder.tvDriverName.setText(context.getString(R.string.driver_name) +" - "+ deliveryDTOArrayList.get(position).getFirstname()+" "+deliveryDTOArrayList.get(position).getLastname());
                    viewHolder.tvDriverName.setText("");
                    Glide.with(context)
                            .setDefaultRequestOptions(requestOptions)
                            .load(DrawerContentSlideActivity.profilepicture)
                            .into(viewHolder.ivProfile);

                }
                viewHolder.tvDriverNo.setText(context.getString(R.string.delivery_contact_name_txt) + " - " + deliveryDTOArrayList.get(position).getDropoffFirstName() + " " + deliveryDTOArrayList.get(position).getDropoffLastName());

                if(deliveryDTOArrayList.get(position).getVehicle_reg_no() != null  && !deliveryDTOArrayList.get(position).getVehicle_reg_no().equals("")) {
                    viewHolder.tvVechileNumber.setText(deliveryDTOArrayList.get(position).getVehicle_reg_no());
                }/*else {
                    viewHolder.tvVechileNumber.setText("");
                }*/

                viewHolder.tvPriceText.setText(deliveryDTOArrayList.get(position).getMessage());

                viewHolder.fullClick.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (deliveryDTOArrayList.get(position).getDeliveryStatus()) {

                            case "6":
                                if(deliveryDTOArrayList.get(position).getIs_paid().equals("no")){
                                    DeliveryDetails deliveryDetails = new DeliveryDetails();
                                    Bundle bundle2 = new Bundle();
                                    if(!deliveryDTOArrayList.get(position).getDeliveryType().equalsIgnoreCase("multiple")){
                                        bundle2.putString("payment", "payment");
                                        bundle2.putString("delivery", deliveryDTOArrayList.get(position).getOrderId());
                                        deliveryDetails.setArguments(bundle2);
                                        addFragmentWithoutRemove(R.id.container_main, deliveryDetails, "DeliveryDetails");
                                    }else{

                                        MultipleAdd multipleAdd = new MultipleAdd();
                                        bundle2.putString("delivery", deliveryDTOArrayList.get(position).getOrderId());
                                        bundle2.putString("multiple_data", "multiple_data");
                                        bundle2.putString("payment", "payment");
                                        multipleAdd.setArguments(bundle2);
                                        addFragmentWithoutRemove(R.id.container_main, multipleAdd, "MultipleAdd");
                                    }
                                }else {
                                    bundle.putString(context.getString(R.string.cur_delivery), context.getString(R.string.cur_delivery));
                                    currentList.setArguments(bundle);
                                    addFragmentWithoutRemove(R.id.container_main, currentList, "CurrentList");
//                                    break;
                                }

//                                bundle.putString(context.getString(R.string.cur_delivery), context.getString(R.string.cur_delivery));
//                                currentList.setArguments(bundle);
//                                addFragmentWithoutRemove(R.id.container_main, currentList, "CurrentList");
                                break;
                            case "7":
                                if(deliveryDTOArrayList.get(position).getIs_paid().equals("no")){

                                    DeliveryDetails deliveryDetails = new DeliveryDetails();
                                    Bundle bundle2 = new Bundle();
                                    if(!deliveryDTOArrayList.get(position).getDeliveryType().equalsIgnoreCase("multiple")){
                                        bundle2.putString("payment", "payment");
                                        bundle2.putString("delivery", deliveryDTOArrayList.get(position).getOrderId());
                                        deliveryDetails.setArguments(bundle2);
                                        addFragmentWithoutRemove(R.id.container_main, deliveryDetails, "DeliveryDetails");
                                    }else{
                                        MultipleAdd multipleAdd = new MultipleAdd();
                                        bundle2.putString("delivery", deliveryDTOArrayList.get(position).getOrderId());
                                        bundle2.putString("multiple_data", "multiple_data");
                                        bundle2.putString("payment", "payment");
                                        multipleAdd.setArguments(bundle2);
                                        addFragmentWithoutRemove(R.id.container_main, multipleAdd, "MultipleAdd");
                                    }
                                }else {

                                    bundle.putString(context.getString(R.string.cur_delivery), context.getString(R.string.cur_delivery));
                                    currentList.setArguments(bundle);
                                    addFragmentWithoutRemove(R.id.container_main, currentList, "CurrentList");
                                }
                                break;

                            case "4":
                                bundle.putString(PN_VALUE, context.getString(R.string.delivery_history));
                                currentList.setArguments(bundle);
                                addFragmentWithoutRemove(R.id.container_main, currentList, "CurrentList");
                                break;
                        }
                    }
                });

            } else if (status.equalsIgnoreCase(context.getString(R.string.delivery_history))) {
                viewHolder.tvDelete.setVisibility(View.VISIBLE);
                //System.out.println("deliveryDTOArrayLifffst--->" + new Gson().toJson(deliveryDTOArrayList));
                if(deliveryDTOArrayList.get(position).getDeliveryStatus().equalsIgnoreCase("3")
                        || deliveryDTOArrayList.get(position).getDeliveryStatus().equalsIgnoreCase("4") ||deliveryDTOArrayList.get(position).getDeliveryStatus().equalsIgnoreCase("8")){
                    if(deliveryDTOArrayList.get(position).getIs_paid().equals("no")){
                        viewHolder.paidstatus.setTextColor(context.getResources().getColor(R.color.sighup_red));
                        viewHolder.paidstatus.setText("Not paid");
                    }else if(deliveryDTOArrayList.get(position).getIs_paid().equals("yes")){
                        viewHolder.paidstatus.setTextColor(context.getResources().getColor(R.color.togel));
                        viewHolder.paidstatus.setText("Paid");
                    }
                    viewHolder.paidstatus.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.paidstatus.setVisibility(View.GONE);
                }

                viewHolder.tvPickLoc.setVisibility(View.VISIBLE);
                viewHolder.tvDropLoc.setVisibility(View.VISIBLE);
                viewHolder.tvDeliveryContactNo.setVisibility(View.GONE);
                viewHolder.tvDeliveryContactName.setVisibility(View.GONE);
                viewHolder.tvDriverName.setVisibility(View.GONE);
                viewHolder.tvPrice.setVisibility(View.GONE);
                viewHolder.tvDeliveryId.setText(context.getString(R.string.delivery_id_txt) + " - " + deliveryDTOArrayList.get(position).getOrderId());
                viewHolder.tvDeliveryDate.setText(context.getString(R.string.delivery_datein_txt) + " - " + deliveryDTOArrayList.get(position).getPickupDate());
                viewHolder.tvPickLoc.setText(context.getString(R.string.pickup_loc_txt) + " - " + deliveryDTOArrayList.get(position).getPickupaddress());
                viewHolder.tvDropLoc.setText(context.getString(R.string.delivery_loc_txt) + " - " + deliveryDTOArrayList.get(position).getDropoffaddress());
                viewHolder.tvOrdertypes.setText(context.getString(R.string.ddelivery_type) + " - " + deliveryDTOArrayList.get(position).getDeliveryType().toUpperCase());

                //Completed orders

                if(deliveryDTOArrayList.get(position).getVehicle_reg_no() != null  && !deliveryDTOArrayList.get(position).getVehicle_reg_no().equals("")) {
                    viewHolder.tvVechileNumber.setText(deliveryDTOArrayList.get(position).getVehicle_reg_no());
                }/*else {
                    viewHolder.tvVechileNumber.setText("");
                }*/

                try {
                    viewHolder.tvDriverNo.setText(context.getString(R.string.delivery_price) + " - " + context.getString(R.string.us_dollar) + " " + String.format("%.2f", Double.parseDouble(deliveryDTOArrayList.get(position).getDeliveryCost())));
                } catch (Exception e) {
                    viewHolder.tvDriverNo.setText(context.getString(R.string.us_dollar));
                    e.printStackTrace();
                }

                if (deliveryDTOArrayList.get(position).getDeliveryStatus() != null && deliveryDTOArrayList.get(position).getDeliveryStatus().equals("3")) {
                    viewHolder.tvPriceText.setText(context.getString(R.string.cancelled));
                    viewHolder.paidstatus.setVisibility(View.GONE);
                } else if (deliveryDTOArrayList.get(position).getDeliveryStatus() != null && deliveryDTOArrayList.get(position).getDeliveryStatus().equals("4")) {
                    viewHolder.tvPriceText.setText(context.getString(R.string.delivered_txt));
                } else if (deliveryDTOArrayList.get(position).getDeliveryStatus() != null && deliveryDTOArrayList.get(position).getDeliveryStatus().equals("8")) {
                    viewHolder.tvPriceText.setText(context.getString(R.string.report_txt));
                }

                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(IMAGE_URL+deliveryDTOArrayList.get(position).getReceivedUserImage())
                        .into(viewHolder.ivProfile);

            } else {
                viewHolder.tvDelete.setVisibility(View.GONE);
                if(deliveryDTOArrayList.get(position).getDeliveryType().equalsIgnoreCase("multiple")){
                    viewHolder.tvDropLoc.setVisibility(View.GONE);
                }else {
                    viewHolder.tvDropLoc.setVisibility(View.VISIBLE);
                }

                viewHolder.tvPickLoc.setVisibility(View.VISIBLE);
                if(deliveryDTOArrayList.get(position).getDeliveryStatus().equalsIgnoreCase("6")
                || deliveryDTOArrayList.get(position).getDeliveryStatus().equalsIgnoreCase("5") ||deliveryDTOArrayList.get(position).getDeliveryStatus().equalsIgnoreCase("7")){
                if(deliveryDTOArrayList.get(position).getIs_paid().equals("no")){
                    viewHolder.paidstatus.setTextColor(context.getResources().getColor(R.color.sighup_red));
                    viewHolder.paidstatus.setText("Not paid");
                }else if(deliveryDTOArrayList.get(position).getIs_paid().equals("yes")){
                    viewHolder.paidstatus.setTextColor(context.getResources().getColor(R.color.togel));
                    viewHolder.paidstatus.setText("Paid");
                }
                    viewHolder.paidstatus.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.paidstatus.setVisibility(View.GONE);
                }


//           if(deliveryDTOArrayList.get(position).getIs_paid().equals("yes")){
//                    viewHolder.paidstatus.setTextColor(context.getResources().getColor(R.color.togel));
//                    viewHolder.paidstatus.setText("Paid");
//                }

//                if(!deliveryDTOArrayList.get(position).getFirstname().equals("") ){
//                    deliveryDTOArrayList.get(position).getLastname());
//                   deliveryDTOArrayList.get(position).getPhone(
//                }
                viewHolder.tvDeliveryContactNo.setVisibility(View.GONE);
                viewHolder.tvDeliveryContactName.setVisibility(View.GONE);
                viewHolder.tvPrice.setVisibility(View.VISIBLE);
                viewHolder.tvOrderStatus.setVisibility(View.VISIBLE);

                if(deliveryDTOArrayList.get(position).getDeliveryStatus().equals("4")){
                    viewHolder.tvOrderStatus.setText("Delivered");
                }else if(deliveryDTOArrayList.get(position).getDeliveryStatus().equals("6") && deliveryDTOArrayList.get(position).getIs_paid().equalsIgnoreCase("no")){
                    viewHolder.tvOrderStatus.setText("DRIVER HAS BEEN ASSIGNED,PLEASE DO THE PAYMENT");
                }else if(deliveryDTOArrayList.get(position).getDeliveryStatus().equals("6") && deliveryDTOArrayList.get(position).getIs_paid().equalsIgnoreCase("yes")){
                    viewHolder.tvOrderStatus.setText("DRIVER HAS BEEN ASSIGNED,PAYMENT SUCCESSFUL");
                }else if(deliveryDTOArrayList.get(position).getDeliveryStatus().equals("5") && deliveryDTOArrayList.get(position).getIs_paid().equalsIgnoreCase("yes")){
                    viewHolder.tvOrderStatus.setText("DRIVER HAS BEEN ASSIGNED,PAYMENT SUCCESSFUL");
                }else if(deliveryDTOArrayList.get(position).getDeliveryStatus().equals("7")){
                    viewHolder.tvOrderStatus.setText("DRIVER HAS BEEN PICKED YOUR ORDER");
                }else if(!deliveryDTOArrayList.get(position).getDeliveryStatus().equals("7") || !deliveryDTOArrayList.get(position).getDeliveryStatus().equals("6")){
                    viewHolder.tvOrderStatus.setVisibility(View.GONE);
                }

                viewHolder.tvVechileNumber.setText(deliveryDTOArrayList.get(position).getVehicle_reg_no());
                viewHolder.tvDeliveryId.setText(context.getString(R.string.delivery_id_txt) + " - " + deliveryDTOArrayList.get(position).getOrderId());
                viewHolder.tvDeliveryDate.setText(context.getString(R.string.pickup_date_qua_txt) + " - " + deliveryDTOArrayList.get(position).getPickupDate());
                viewHolder.tvPickLoc.setText(context.getString(R.string.pickup_loc_txt) + " - " + deliveryDTOArrayList.get(position).getPickupaddress());
                viewHolder.tvDropLoc.setText(context.getString(R.string.delivery_loc_txt) + " - " + deliveryDTOArrayList.get(position).getDropoffaddress());
                viewHolder.tvOrdertypes.setText(context.getString(R.string.ddelivery_type) + " - " + deliveryDTOArrayList.get(position).getDeliveryType().toUpperCase());

                System.out.println("deliveryDTOArrayList.get(position).getVehicle_reg_no()"+deliveryDTOArrayList.get(position).getVehicle_reg_no());
//current deliviries
                if(deliveryDTOArrayList.get(position).getVehicle_reg_no() != null  && !deliveryDTOArrayList.get(position).getVehicle_reg_no().equals("")) {
                    viewHolder.tvVechileNumber.setText(deliveryDTOArrayList.get(position).getVehicle_reg_no());
                }/*else {
                    viewHolder.tvVechileNumber.setText("");
                }*/

                try {
                    viewHolder.tvPrice.setText(context.getString(R.string.us_dollar) + " " + String.format("%.2f", Double.parseDouble(deliveryDTOArrayList.get(position).getDeliveryCost())));
                } catch (Exception e) {
                    viewHolder.tvPrice.setText(context.getString(R.string.us_dollar));
                    e.printStackTrace();
                }

                viewHolder.tvDeliveryContactNo.setText(context.getString(R.string.delivery_contact_no_txt) + " - " + deliveryDTOArrayList.get(position).getDropoffMobNumber());
                viewHolder.tvDeliveryContactName.setText(context.getString(R.string.delivery_contact_name_txt) + " - " + deliveryDTOArrayList.get(position).getDropoffFirstName() + " " + deliveryDTOArrayList.get(position).getDropoffLastName());

                if (deliveryDTOArrayList.get(position).getPhone() != null && !deliveryDTOArrayList.get(position).getPhone().equals("")) {
                    viewHolder.tvDriverName.setText(context.getString(R.string.driver_name) + " - " + deliveryDTOArrayList.get(position).getFirstname() + " " + deliveryDTOArrayList.get(position).getLastname());
                    viewHolder.tvDriverNo.setText(context.getString(R.string.driver_number) + " - " + deliveryDTOArrayList.get(position).getPhone());
                } else {
                    viewHolder.tvDriverName.setVisibility(View.GONE);
                    viewHolder.tvDriverNo.setVisibility(View.GONE);
                }

                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions)
                        .load(deliveryDTOArrayList.get(position).getProfileImage())
                        .into(viewHolder.ivProfile);

                if (deliveryDTOArrayList.get(position).getDriverId() != null && !deliveryDTOArrayList.get(position).getDriverId().equals("") && !deliveryDTOArrayList.get(position).getDriverId().equals("0")) {
                    viewHolder.ratingBar.setVisibility(View.GONE);

                    if (deliveryDTOArrayList.get(position).getAvgratingdriver() != null && !deliveryDTOArrayList.get(position).getAvgratingdriver().equals("")) {
                        viewHolder.ratingBar.setRating(Float.parseFloat(deliveryDTOArrayList.get(position).getAvgratingdriver()));
                    }

                } else {
                    viewHolder.ratingBar.setVisibility(View.GONE);
                }
            }

            if (deliveryDTOArrayList.get(position).getVehicleType() != null && deliveryDTOArrayList.get(position).getVehicleType().equalsIgnoreCase(context.getString(R.string.bike))) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions1)
                        .load(R.drawable.bike_list)
                        .into(viewHolder.ivVehicle);
            } else if (deliveryDTOArrayList.get(position).getVehicleType() != null && deliveryDTOArrayList.get(position).getVehicleType().equalsIgnoreCase(context.getString(R.string.car))) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions1)
                        .load(R.drawable.car_list)
                        .into(viewHolder.ivVehicle);
            } else if (deliveryDTOArrayList.get(position).getVehicleType() != null && deliveryDTOArrayList.get(position).getVehicleType().equalsIgnoreCase(context.getString(R.string.van))) {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions1)
                        .load(R.drawable.van_03)
                        .into(viewHolder.ivVehicle);
            } else {
                Glide.with(context)
                        .setDefaultRequestOptions(requestOptions1)
                        .load(R.drawable.truck_list)
                        .into(viewHolder.ivVehicle);
            }

            if (deliveryDTOArrayList.get(position).getDeliveryType() != null) {
                if (deliveryDTOArrayList.get(position).getDeliveryType().equalsIgnoreCase("single")) {
                    viewHolder.viewHours.setBackgroundColor(context.getResources().getColor(R.color.two_hours));
                } else if (deliveryDTOArrayList.get(position).getDeliveryType().equalsIgnoreCase("multiple")) {
                    viewHolder.viewHours.setBackgroundColor(context.getResources().getColor(R.color.four_hours));
                } else if(deliveryDTOArrayList.get(position).getDeliveryType().equalsIgnoreCase("miscellaneous")){
                    viewHolder.viewHours.setBackgroundColor(context.getResources().getColor(R.color.sighup_red));
                } else {
                    viewHolder.viewHours.setBackgroundColor(context.getResources().getColor(R.color.same_hours));
                }
            } else {
                viewHolder.viewHours.setVisibility(View.GONE);
            }

            viewHolder.itemView.setOnClickListener(new OnItemClickListener(position, onItemClickCallback));
            viewHolder.tvDelete.setOnClickListener(new OnItemClickListener(position, onItemdeleteItems));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return deliveryDTOArrayList.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPriceText, tvPrice, tvDeliveryId, tvDeliveryDate, tvPickLoc, tvDropLoc, tvDelete,tvDeliveryContactName,
                tvDeliveryContactNo, tvDriverName,tvOrdertypes, tvDriverNo,tvOrderStatus,tvVechileNumber,paidstatus;
        private ImageViewCircular ivProfile;
        private ImageView ivVehicle;
        private View viewHours;
        private RatingBar ratingBar;
        ConstraintLayout fullClick;


        public ViewHolder(View view) {
            super(view);
            tvDelete=(TextView)view.findViewById(R.id.tv_delete);
            tvOrderStatus = (TextView) view.findViewById(R.id.tv_order_status);
            paidstatus = (TextView) view.findViewById(R.id.paidstatus);
            tvVechileNumber = (TextView) view.findViewById(R.id.tv_vechicalnumber);
            fullClick = (ConstraintLayout) view.findViewById(R.id.full_click);
            tvDeliveryId = (TextView) view.findViewById(R.id.tv_delivery_id);
            tvDeliveryDate = (TextView) view.findViewById(R.id.tv_delivery_date);
            tvPickLoc = (TextView) view.findViewById(R.id.tv_pickup_location);
            tvDropLoc = (TextView) view.findViewById(R.id.tv_delivery_location);
            tvDeliveryContactName = (TextView) view.findViewById(R.id.tv_delivery_contact_name);
            tvDeliveryContactNo = (TextView) view.findViewById(R.id.tv_delivery_contact_no);
            tvDriverName = (TextView) view.findViewById(R.id.tv_driver_name);
            tvDriverNo = (TextView) view.findViewById(R.id.tv_driver_no);
            ivProfile = (ImageViewCircular) view.findViewById(R.id.iv_profile);
            ivVehicle = (ImageView) view.findViewById(R.id.iv_vehicle);
            tvPriceText = (TextView) view.findViewById(R.id.tv_price_text);
            tvPrice = (TextView) view.findViewById(R.id.tv_price);
            viewHours = (View) view.findViewById(R.id.view_hours);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            tvOrdertypes = (TextView) view.findViewById(R.id.tv_orders_type);
        }
    }

    public void addFragmentWithoutRemove(int containerViewId, Fragment fragment, String fragmentName) {
        String tag = (String) fragment.getTag();
        ((DrawerContentSlideActivity)context).getSupportFragmentManager().beginTransaction()
                // remove fragment from fragment manager
                //fragmentTransaction.remove(getActivity().getSupportFragmentManager().findFragmentByTag(tag));
                // add fragment in fragment manager
                .add(containerViewId, fragment, fragmentName)
                // add to back stack
                .addToBackStack(tag)
                .commit();
    }
}
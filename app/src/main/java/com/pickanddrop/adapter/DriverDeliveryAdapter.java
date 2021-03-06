package com.pickanddrop.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pickanddrop.R;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.ImageViewCircular;
import com.pickanddrop.utils.OnItemClickListener;
import com.pickanddrop.utils.Utilities;
import java.util.ArrayList;

public class DriverDeliveryAdapter extends RecyclerView.Adapter<DriverDeliveryAdapter.ViewHolder> implements AppConstants {
    private Context context;
    private OnItemClickListener.OnItemClickCallback onItemClickCallback;
    private AppSession appSession;
    private Utilities utilities;
    private RequestOptions requestOptions, requestOptions1;
    private String status = "";
    private ArrayList<DeliveryDTO.Data> deliveryDTOArrayList;

    public DriverDeliveryAdapter(Context context, ArrayList<DeliveryDTO.Data> deliveryDTOArrayList, OnItemClickListener.OnItemClickCallback onItemClickCallback, String status) {
        this.context = context;
        this.onItemClickCallback = onItemClickCallback;
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);
        this.status = status;
        this.deliveryDTOArrayList = deliveryDTOArrayList;

        requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        requestOptions.override(150, 150);
        requestOptions.placeholder(R.drawable.user_praba);
        requestOptions.error(R.drawable.user_praba);

        requestOptions1 = new RequestOptions();
        requestOptions1.override(100, 100);
        requestOptions1.placeholder(R.drawable.truck_list);
        requestOptions1.error(R.drawable.truck_list);
    }


    @Override
    public DriverDeliveryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_current_deliveries, parent, false);
        return new DriverDeliveryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DriverDeliveryAdapter.ViewHolder viewHolder, int position) {
        if (status.equalsIgnoreCase(context.getString(R.string.notification))) {
            viewHolder.tvDropLoc.setVisibility(View.GONE);
            viewHolder.tvDeliveryContactNo.setVisibility(View.GONE);
            viewHolder.tvDeliveryContactName.setVisibility(View.GONE);
            viewHolder.tvPrice.setVisibility(View.GONE);

            viewHolder.tvDeliveryId.setText(context.getString(R.string.delivery_id_txt) +" - "+ deliveryDTOArrayList.get(position).getOrderId());
            viewHolder.tvDeliveryDate.setText(context.getString(R.string.pickup_contact_name) +" - "+ deliveryDTOArrayList.get(position).getPickupFirstName() +" "+deliveryDTOArrayList.get(position).getPickupLastName());
          if(deliveryDTOArrayList.get(position).getPickupDate() != null && !deliveryDTOArrayList.get(position).getPickupDate().equalsIgnoreCase("") && !deliveryDTOArrayList.get(position).getPickupDate().equalsIgnoreCase("null")) {
             viewHolder.tvPickLoc.setVisibility(View.VISIBLE);
              viewHolder.tvPickLoc.setText(context.getString(R.string.delivery_datein_txt) + " - " + deliveryDTOArrayList.get(position).getPickupDate());
          }else {
              viewHolder.tvPickLoc.setVisibility(View.GONE);
              viewHolder.tvPickLoc.setText("");
          }

            if(deliveryDTOArrayList.get(position).getFirstname() != null && !deliveryDTOArrayList.get(position).getFirstname().equalsIgnoreCase("") && !deliveryDTOArrayList.get(position).getFirstname().equalsIgnoreCase("null")) {
                viewHolder.tvDriverName.setVisibility(View.VISIBLE);
                viewHolder.tvDriverName.setText(context.getString(R.string.driver_name) +" - "+ deliveryDTOArrayList.get(position).getFirstname()+" "+deliveryDTOArrayList.get(position).getLastname());
            }else {
                viewHolder.tvDriverName.setText(context.getString(R.string.driver_name) +" - "+ deliveryDTOArrayList.get(position).getFirstname()+" "+deliveryDTOArrayList.get(position).getLastname());
                viewHolder.tvDriverName.setText("");
            }

            viewHolder.tvDriverNo.setText(context.getString(R.string.delivery_contact_name_txt) +" - "+ deliveryDTOArrayList.get(position).getDropoffMobNumber());

            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(deliveryDTOArrayList.get(position).getProfileImage())
                    .into(viewHolder.ivProfile);
        } else if (status.equalsIgnoreCase(context.getString(R.string.delivery_history))) {
            viewHolder.tvPickLoc.setVisibility(View.VISIBLE);
            viewHolder.tvDropLoc.setVisibility(View.VISIBLE);
            viewHolder.tvDeliveryContactNo.setVisibility(View.GONE);
            viewHolder.tvDeliveryContactName.setVisibility(View.GONE);
            viewHolder.tvDriverName.setVisibility(View.GONE);
            viewHolder.tvPrice.setVisibility(View.GONE);


            viewHolder.tvDeliveryId.setText(context.getString(R.string.delivery_id_txt) +" - "+ deliveryDTOArrayList.get(position).getOrderId());
            viewHolder.tvDeliveryDate.setText(context.getString(R.string.delivery_datein_txt) +" - "+ deliveryDTOArrayList.get(position).getPickupDate());
            viewHolder.tvPickLoc.setText(context.getString(R.string.pickup_loc_txt) +" - "+ deliveryDTOArrayList.get(position).getPickupaddress());
            viewHolder.tvDropLoc.setText(context.getString(R.string.delivery_loc_txt) +" - "+ deliveryDTOArrayList.get(position).getDropoffaddress());


            try {
                viewHolder.tvDriverNo.setText(context.getString(R.string.delivery_price) +" - "+ context.getString(R.string.us_dollar) +" "+ String.format("%.2f", Double.parseDouble(deliveryDTOArrayList.get(position).getDriverDeliveryCost())));
            } catch (Exception e) {
                viewHolder.tvDriverNo.setText(context.getString(R.string.us_dollar));
                e.printStackTrace();
            }

            if (deliveryDTOArrayList.get(position).getDeliveryStatus() != null && deliveryDTOArrayList.get(position).getDeliveryStatus().equals("3")) {
                viewHolder.tvPriceText.setText(context.getString(R.string.cancelled));
            } else if (deliveryDTOArrayList.get(position).getDeliveryStatus() != null && deliveryDTOArrayList.get(position).getDeliveryStatus().equals("4")) {
                viewHolder.tvPriceText.setText(context.getString(R.string.delivered_txt));
            } else if (deliveryDTOArrayList.get(position).getDeliveryStatus() != null && deliveryDTOArrayList.get(position).getDeliveryStatus().equals("8")) {
                viewHolder.tvPriceText.setText(context.getString(R.string.report_txt));
            }


            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .load(IMAGE_URL + deliveryDTOArrayList.get(position).getReceivedUserImage())
                    .into(viewHolder.ivProfile);
        } else {
            viewHolder.tvPickLoc.setVisibility(View.VISIBLE);
            viewHolder.tvDropLoc.setVisibility(View.VISIBLE);
            viewHolder.tvDeliveryContactNo.setVisibility(View.GONE);
            viewHolder.tvDeliveryContactName.setVisibility(View.GONE);
            viewHolder.tvPrice.setVisibility(View.VISIBLE);
            viewHolder.ivProfile.setVisibility(View.GONE);
            viewHolder.tvDriverName.setVisibility(View.GONE);
            viewHolder.tvDriverNo.setVisibility(View.GONE);

            viewHolder.tvDeliveryId.setText(context.getString(R.string.delivery_id_txt) +" - "+ deliveryDTOArrayList.get(position).getOrderId());
            viewHolder.tvDeliveryDate.setText(context.getString(R.string.delivery_datein_txt) +" - "+ deliveryDTOArrayList.get(position).getPickupDate());
            viewHolder.tvPickLoc.setText(context.getString(R.string.pickup_loc_txt) +" - "+ deliveryDTOArrayList.get(position).getPickupaddress());

            try {
            viewHolder.tvDropLoc.setText(context.getString(R.string.delivery_loc_txt) +" - "+ deliveryDTOArrayList.get(position).getDropoffaddress());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                viewHolder.tvPrice.setText(context.getString(R.string.us_dollar) +" "+ String.format("%.2f", Double.parseDouble(deliveryDTOArrayList.get(position).getDriverDeliveryCost())));
            } catch (Exception e) {
                viewHolder.tvPrice.setText(context.getString(R.string.us_dollar));
                e.printStackTrace();
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
            } else {
                viewHolder.viewHours.setBackgroundColor(context.getResources().getColor(R.color.same_hours));
            }
        } else {
            viewHolder.viewHours.setVisibility(View.GONE);
        }


        viewHolder.itemView.setOnClickListener(new OnItemClickListener(position, onItemClickCallback));
    }

    @Override
    public int getItemCount() {
        return deliveryDTOArrayList.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPriceText, tvPrice, tvDeliveryId, tvDeliveryDate, tvPickLoc, tvDropLoc, tvDeliveryContactName, tvDeliveryContactNo, tvDriverName, tvDriverNo;
        private ImageViewCircular ivProfile;
        private ImageView ivVehicle;
        private View viewHours;

        public ViewHolder(View view) {
            super(view);
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
        }
    }

}







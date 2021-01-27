package com.pickanddrop.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.dto.MultipleDTO;
import com.pickanddrop.fragment.MultipleAdd;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.DatabaseHandler;
import com.pickanddrop.utils.OnItemClickListener;
import com.pickanddrop.utils.Utilities;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MultipleDropAdapter  extends  RecyclerView.Adapter<MultipleDropAdapter.ViewHolder> implements AppConstants {
    private Context context;
    private OnItemClickListener.OnItemClickCallback onItemClickCallback;
    private AppSession appSession;
    private Utilities utilities;
    private String status = "";
    private DeliveryDTO.Data deliveryDTO;
    private ArrayList<MultipleDTO> deliveryDTOArrayList;
    MultipleAdd multipleAdd = new MultipleAdd();
    Bundle bundle = new Bundle();
    DatabaseHandler db;
    int number = 0;
    private final int limit = 20;
    private Double totalDeliveryCost = 0.0;
    Boolean multiple_type = false, delverystatus = false;
    String totalPrice = "";

  //  Integer totalPrice = 0;

    

    public MultipleDropAdapter(Context context, ArrayList<MultipleDTO> deliveryDTOArrayList,Boolean multiple_type,Boolean delverystatus, OnItemClickListener.OnItemClickCallback onItemClickCallback) {
        this.context = context;
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);
        this.multiple_type = multiple_type;
        this.deliveryDTOArrayList = deliveryDTOArrayList;
        this.delverystatus = delverystatus;
        this.onItemClickCallback = onItemClickCallback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multipleadd, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {


    /*      System.out.println("tvPickLoc"+deliveryDTOArrayList.get(position).getPickupaddress());
            System.out.println("tvPickLoc"+deliveryDTOArrayList.get(position).getDropoffaddress());

*/
        System.out.println("position"+position);
        System.out.println("position"+deliveryDTOArrayList.get(position));

        MultipleDTO arrayPjo = deliveryDTOArrayList.get(position);

        System.out.println("position"+arrayPjo);
        viewHolder.tvDropInside.setText(arrayPjo.getDropBuildingType());
        viewHolder.tvDropLiftgate.setText(arrayPjo.getDropElevator());
        viewHolder.tvdropname.setText(arrayPjo.getDropoffFirstName());
        viewHolder.tvdropinst.setText(arrayPjo.getDropoff_special_inst());
        viewHolder.tvpickupinst.setText(arrayPjo.getPickupSpecialInst());
        viewHolder.tvdropnumber.setText(arrayPjo.getDropoffMobNumber());
        viewHolder.tvDropLoc.setText(arrayPjo.getDropoffaddress());
        viewHolder.tvposition.setText("Stop " + String.valueOf(position + 1));

        try {
            viewHolder.tvPrice.setText(String.format("%.2f", Double.parseDouble(arrayPjo.getDeliveryCost())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(delverystatus){
        if(arrayPjo.getDelivery_status().equals("4")){
        viewHolder.tv_position_status.setText("DELIVERED");
        }
        }
        if(!TextUtils.isEmpty(arrayPjo.getTypeGoods())){

            Log.d("adapter", "onBindViewHolder: ");
            //Toast.makeText(context, ""+arrayPjo.getClassGoods(), Toast.LENGTH_SHORT).show();

            if(arrayPjo.getTypeGoods().equalsIgnoreCase("pallet")){
                viewHolder.tv_drop_parcel_name.setText("No of pallets");

            }else if(arrayPjo.getTypeGoods().equalsIgnoreCase("box")){
                viewHolder.tv_drop_parcel_name.setText("No of Boxes");

            }
            else if(arrayPjo.getTypeGoods().equalsIgnoreCase("pallet and box"))
            {
                viewHolder.tv_drop_parcel_name.setText("No of pallets");

                viewHolder.tv_drop_parcel_name_box.setText("No of Boxes");

            }
        }


        viewHolder.tv_drop_parcel_count_box.setText(arrayPjo.getNoOfPallets1());
        viewHolder.tv_drop_parcel_count.setText(arrayPjo.getNoOfPallets());
        //viewHolder.tv_drop_parcel_count.setText(arrayPjo.getNoOfPallets1());
        System.out.println("position"+arrayPjo.getDeliveryCost());

   /*     for (int i = 0; i<deliveryDTOArrayList.size(); i++)
        {

           String a = (deliveryDTOArrayList.get(i).getMulprice());
           totalPrice +=a;
        }*/

  // Integer a = 0 +  Integer.parseInt(totalPrice);

   //     System.out.println("totalPrice"+a);

          //  viewHolder.tvPickLoc.setText(arrayPjo.getPickup());



if(!TextUtils.isEmpty(arrayPjo.getDeliveryCost())){

    totalDeliveryCost = totalDeliveryCost - (Float.parseFloat(arrayPjo.getDeliveryCost()));
}




        System.out.println("position"+totalDeliveryCost);


    }





    @Override
    public int getItemCount() {

        if (deliveryDTOArrayList.size() > limit) {
            return limit;
        } else{
            return deliveryDTOArrayList.size();
        }

    }



/*    @Override
    public int getItemCount() {
        return deliveryDTOArrayList.size();
    }*/

    public void delete(int position) {
        deliveryDTOArrayList.remove(position);
        notifyItemRemoved(position);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPriceText, tvPrice, tvorderid, tvPickLoc,tvdropnumber,tvdropinst, tvdropname, tvDropLoc,tvposition,
                tv_position_status,tv_drop_parcel_name,tv_drop_parcel_count,tvpickupinst,tvDropInside,tvDropLiftgate,tv_drop_parcel_name_box,tv_drop_parcel_count_box;
        ImageButton button;
        LinearLayout ll_postion_status;


        public ViewHolder(View view) {
            super(view);
         //   tvorderid = (TextView) view.findViewById(R.id.tv_Minfo_text);
          //  tvPickLoc = (TextView) view.findViewById(R.id.tv_Mpickup);
            tvDropInside=(TextView)view.findViewById(R.id.tv_drop_inside);
            tvDropLiftgate=(TextView)view.findViewById(R.id.tv_drop_liftgate);
            tvdropnumber = (TextView) view.findViewById(R.id.tv_drop_personmobile);
            tvdropname = (TextView) view.findViewById(R.id.tv_drop_personname);
            tvpickupinst = (TextView)view.findViewById(R.id.tv_pick_specialinstpick);
            tvdropinst = (TextView)view.findViewById(R.id.tv_instron);
            tvDropLoc = (TextView) view.findViewById(R.id.tv_Mdrop);
            tvPriceText = (TextView) view.findViewById(R.id.tv_price_text);
            tv_drop_parcel_name = (TextView) view.findViewById(R.id.tv_drop_parcel_name);
            tv_drop_parcel_count = (TextView) view.findViewById(R.id.tv_drop_parcel_count);
            tvPrice = (TextView) view.findViewById(R.id.tv_MPrice);
            button = (ImageButton) itemView.findViewById(R.id.delete);
            tvposition = (TextView) view.findViewById(R.id.tv_position);
            ll_postion_status = (LinearLayout) view.findViewById(R.id.ll_postion_status);
            tv_position_status = (TextView) view.findViewById(R.id.tv_position_status);

            tv_drop_parcel_name_box = view.findViewById(R.id.tv_drop_parcel_namebox);
            tv_drop_parcel_count_box = view.findViewById(R.id.tv_drop_parcel_countbox);

            if(multiple_type){
                  button.setVisibility(View.GONE);
            }

            if(delverystatus){
                ll_postion_status.setVisibility(View.VISIBLE);
            }

            button.setOnClickListener(new OnItemClickListener(getAdapterPosition(),onItemClickCallback));

//            button.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                     //   db.deletetitle(deliveryDTO.getDropoffMobNumber());
//                     //   delete(getAdapterPosition());
//                        deletePlace(getAdapterPosition());
////                        v.setOnClickListener(new OnItemClickListener(getAdapterPosition(),onItemClickCallback));
//
//                    }
//                }); //button onclick listener
        }

        private void deletePlace(int position){
            db  = new DatabaseHandler(context);
            db.deletetitle(deliveryDTOArrayList.get(position).getDropoffMobNumber());
            deliveryDTOArrayList.remove(position);
            notifyDataSetChanged();
        }

    }

    public void addFragmentWithoutRemove(int containerViewId, Fragment fragment, String fragmentName) {
        String tag = (String) fragment.getTag();
        ((DrawerContentSlideActivity)context).getSupportFragmentManager().beginTransaction()
                .add(containerViewId, fragment, fragmentName)
                .addToBackStack(tag)
                .commit();
    }

}

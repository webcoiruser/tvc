package com.pickanddrop.adapter;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.request.RequestOptions;
import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.fragment.MultipleAdd;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.DatabaseHandler;
import com.pickanddrop.utils.OnItemClickListener;
import com.pickanddrop.utils.Utilities;

import java.util.List;


public class dataadapter  extends  RecyclerView.Adapter<dataadapter.ViewHolder> implements AppConstants {

    private Context context;
    private OnItemClickListener.OnItemClickCallback onItemClickCallback;
    private AppSession appSession;
    private Utilities utilities;
    private RequestOptions requestOptions, requestOptions1;
    private String status = "";
    private DeliveryDTO.Data deliveryDTO;
    private List<String> deliveryDTOArrayList;
    private List<String> deliveryDTOArrayList1;
    MultipleAdd multipleAdd = new MultipleAdd();
    Bundle bundle = new Bundle();
    DatabaseHandler db;
    int number = 0;
    private final int limit = 20;
    private Double totalDeliveryCost = 0.0;
    String totalPrice = "";
    //  Integer totalPrice = 0;



    public dataadapter(Context context, List<String> deliveryDTOArrayList,List<String> deliveryDTOArrayList1) {
        this.context = context;
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);
        this.deliveryDTOArrayList = deliveryDTOArrayList;
        this.deliveryDTOArrayList1 = deliveryDTOArrayList1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drop_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

    /*    System.out.println("tvPickLoc"+deliveryDTOArrayList.get(position).getPickupaddress());
        System.out.println("tvPickLoc"+deliveryDTOArrayList.get(position).getDropoffaddress());
    */
        System.out.println("position"+position);
        System.out.println("position"+deliveryDTOArrayList.get(position));
        String arrayPjo = deliveryDTOArrayList.get(position);
        String arrayPjo1 = deliveryDTOArrayList1.get(position);
        System.out.println("position"+arrayPjo);
      //  viewHolder.tvdropname.setText(arrayPjo.getDropname());
     //   viewHolder.tvdropnumber.setText(arrayPjo.getNumber1());
        viewHolder.tvDropLoc.setText(arrayPjo);
        viewHolder.dropperson.setText(arrayPjo1);
        viewHolder.tvposition.setText(String.valueOf(position + 1));

      /*  try {
            viewHolder.tvPrice.setText(String.format("%.2f", Double.parseDouble(arrayPjo.getMulprice())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("position"+arrayPjo.getMulprice());*/

   /*     for (int i = 0; i<deliveryDTOArrayList.size(); i++)
        {

           String a = (deliveryDTOArrayList.get(i).getMulprice());
           totalPrice +=a;
        }*/
        // Integer a = 0 +  Integer.parseInt(totalPrice);
        //     System.out.println("totalPrice"+a);
        //  viewHolder.tvPickLoc.setText(arrayPjo.getPickup());
        //totalDeliveryCost = totalDeliveryCost - (Float.parseFloat(arrayPjo.getMulprice()));

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
        private TextView tvPriceText, tvPrice, dropperson, tvPickLoc,tvdropnumber, tvdropname, tvDropLoc,tvposition;
        ImageButton button;


        public ViewHolder(View view) {
            super(view);
            dropperson = (TextView) view.findViewById(R.id.tv_dropper);
            //  tvPickLoc = (TextView) view.findViewById(R.id.tv_Mpickup);
          //  tvdropnumber = (TextView) view.findViewById(R.id.tv_drop_personmobile);
          //  tvdropname = (TextView) view.findViewById(R.id.tv_drop_personname);
            tvDropLoc = (TextView) view.findViewById(R.id.tv_Mdrop);
         //   tvPriceText = (TextView) view.findViewById(R.id.tv_price_text);
        //    tvPrice = (TextView) view.findViewById(R.id.tv_MPrice);
        //    button = (ImageButton) itemView.findViewById(R.id.delete);
            tvposition = (TextView) view.findViewById(R.id.tv_position);


           /* button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //   db.deletetitle(deliveryDTO.getDropoffMobNumber());
                    //   delete(getAdapterPosition());
                    deletePlace(getAdapterPosition());

                }
            }); //button onclick listener*/
        }

     /*   private void deletePlace(int position){
            db  = new DatabaseHandler(context);
            db.deletetitle(deliveryDTOArrayList.get(position).getNumber1());
            deliveryDTOArrayList.remove(position);
            notifyDataSetChanged();

        }*/

    }

    public void addFragmentWithoutRemove(int containerViewId, Fragment fragment, String fragmentName) {
        String tag = (String) fragment.getTag();
        ((DrawerContentSlideActivity)context).getSupportFragmentManager().beginTransaction()
                .add(containerViewId, fragment, fragmentName)
                .addToBackStack(tag)
                .commit();
    }
}

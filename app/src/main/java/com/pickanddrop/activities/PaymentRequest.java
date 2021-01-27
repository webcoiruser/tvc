package com.pickanddrop.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;

import com.pickanddrop.R;
import com.pickanddrop.databinding.ActivityPaymentRequedtBinding;
import com.pickanddrop.fragment.DeliveryDetails;
import com.pickanddrop.fragment.MultipleAdd;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.Utilities;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.utils.OnDialogConfirmListener;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentRequest extends AppCompatActivity implements AppConstants , View.OnClickListener {

    private Context context;
    private Stripe stripe;
    private AppSession appSession;
    private Utilities utilities;
    private ActivityPaymentRequedtBinding activityPaymentRequedtBinding;
    private String TAG = PaymentRequest.class.getName();
    private CountDownTimer countDownTimer;
    private MediaPlayer mp;

    private String OrderId="";
    double pick_lat=0.0,pick_long=0.0;
    double drop_lat=0.0,drop_long=0.0;
    private String pick_address="",drop_address="";
    private String delivery_cost="";
    private String delivery_type="";
    private String driver_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPaymentRequedtBinding = DataBindingUtil. setContentView(this,R.layout.activity_payment_requedt);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = PaymentRequest.this;
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);

        activityPaymentRequedtBinding.myProgress.setMax(250);
        activityPaymentRequedtBinding.myProgress.setProgress(0);
        setLanguage(ENGLISH);

        activityPaymentRequedtBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finishSound();
                //go to home
                Intent intent = new Intent(PaymentRequest.this,DrawerContentSlideActivity.class);
                startActivity(intent);
                finishAffinity();

            }
        });
        initView();

        CountTimer();
        playsound();

    }


    private void playsound(){
        mp = MediaPlayer.create(this, R.raw.notification);
        mp.start();
        mp.setLooping(true);


    }


    private void finishSound(){

        if (countDownTimer!=null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        try {
            if (mp.isLooping()) {
                mp.setLooping(false);
            }
        }catch(Exception e){}
        try {
            if(mp.isPlaying()){
                mp.stop();
                mp.release();
            }
        }catch(Exception e){}

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();

    }


    @Override
    protected void onDestroy() {
        finishSound();
        super.onDestroy();
    }

    private void CountTimer(){
        countDownTimer = new CountDownTimer(250000, 1000) {

            public void onTick(long millisUntilFinished) {
                activityPaymentRequedtBinding.myProgress.setProgress(250-(int) millisUntilFinished/ 1000);


            }

            public void onFinish() {
                try {
                    if (mp.isLooping()) {
                        mp.setLooping(false);
                    }
                }catch(Exception e){}
                try {
                    if(mp.isPlaying()){
                        mp.stop();
                        mp.release();
                    }
                }catch(Exception e){}

                countDownTimer=null;
                //go to home
                Intent intent = new Intent(PaymentRequest.this,DrawerContentSlideActivity.class);
                startActivity(intent);
                finishAffinity();


            }
        }.start();

    }

    public void setLanguage(String language) {
        appSession.setLanguage(language);
        Locale locale1 = new Locale(language);
        Locale.setDefault(locale1);
        Configuration config1 = new Configuration();
        config1.locale = locale1;
        getResources().updateConfiguration(config1, getResources().getDisplayMetrics());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.iv_back:

                break;
        }
    }

    private void initView() {



        try {
            JSONObject jsonObject = new JSONObject(appSession.getNotificationData());

            OrderId = jsonObject.getString("order_id");
            pick_lat = Double.parseDouble(jsonObject.getString("pickup_lat"));
            pick_long = Double.parseDouble(jsonObject.getString("pickup_long"));
            drop_lat = Double.parseDouble(jsonObject.getString("dropoff_lat"));
            drop_long = Double.parseDouble(jsonObject.getString("dropoff_long"));
            pick_address = jsonObject.getString("pickupaddress");
            drop_address = jsonObject.getString("dropoffaddress");
            delivery_type = jsonObject.getString("delivery_type");
            driver_id = jsonObject.getString("driver_id");

            if(jsonObject.getString("delivery_type").trim().equalsIgnoreCase("express")){
                activityPaymentRequedtBinding.delType.setText("Express");

            }else if(jsonObject.getString("delivery_type").trim().equalsIgnoreCase("multiple")){
                activityPaymentRequedtBinding.delType.setText("Multiple");
                activityPaymentRequedtBinding.dropContainr.setVisibility(View.GONE);

            }else if(jsonObject.getString("delivery_type").trim().equalsIgnoreCase("single")){
                activityPaymentRequedtBinding.delType.setText("Single");

            }else if(jsonObject.getString("delivery_type").trim().equalsIgnoreCase("miscellaneous")){
                activityPaymentRequedtBinding.delType.setText("Miscellaneous");

            }


            delivery_cost = "";
            try {
                delivery_cost = String.format("%.2f", Double.parseDouble(jsonObject.getString("delivery_cost")));
            } catch (Exception e) {
                delivery_cost = jsonObject.getString("delivery_cost");
                e.printStackTrace();
            }

            //activityPaymentRequedtBinding.paymentCard.setText(getResources().getString(R.string.pay));
            /*cardPaymenrStripeBinding.paymentCard.setEnabled(false);
             cardPaymenrStripeBinding.paymentCard.setAlpha(Float.valueOf("0.1"));*/


            activityPaymentRequedtBinding.delDate.setText(jsonObject.getString("delivery_date"));
            activityPaymentRequedtBinding.delAmount.setText(jsonObject.getString("delivery_cost"));
            activityPaymentRequedtBinding.dropAddress.setText("Drop Location - "+jsonObject.getString("dropoffaddress"));
            activityPaymentRequedtBinding.pickupLocation.setText("Pickup Location - "+jsonObject.getString("pickupaddress"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

        activityPaymentRequedtBinding.closeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityPaymentRequedtBinding.moreInfoContainer.setVisibility(View.GONE);
            }
        });

        activityPaymentRequedtBinding.readMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(activityPaymentRequedtBinding.moreInfoContainer.getVisibility()!=View.VISIBLE) {
                    activityPaymentRequedtBinding.moreInfoContainer.setVisibility(View.VISIBLE);

                    Bundle bundle2 = new Bundle();
                    if (!delivery_type.trim().equalsIgnoreCase("multiple")) {
                        DeliveryDetails deliveryDetails = new DeliveryDetails();

                        bundle2.putString("payment", "payment");
                        bundle2.putString("delivery", OrderId);
                        bundle2.putString("from_payment_request", "payment_request");
                        deliveryDetails.setArguments(bundle2);

                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.add(R.id.fragment_container, deliveryDetails);
                        fragmentTransaction.commit();
                    } else {
                        MultipleAdd multipleAdd = new MultipleAdd();
                        bundle2.putString("delivery", OrderId);
                        bundle2.putString("multiple_data", "multiple_data");
                        bundle2.putString("payment", "payment");
                        bundle2.putString("from_payment_request", "payment_request");
                        multipleAdd.setArguments(bundle2);

                        FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fm.beginTransaction();
                        fragmentTransaction.add(R.id.fragment_container, multipleAdd);
                        fragmentTransaction.commit();
                    }

                }

            }
        });


        //PaymentConfiguration.init( "pk_live_uB9yAaATgvKiLxVrkLrMboDT00BoCv0j5h");
        PaymentConfiguration.init( "pk_test_D3mZ9NKT7es8AVtfzHVhSwi400afF9EdXS");
        activityPaymentRequedtBinding.paymentCard.setOnClickListener(this);


        activityPaymentRequedtBinding.paymentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activityPaymentRequedtBinding.paymentCard.getText().toString().trim().equalsIgnoreCase("Pay Now")){
                    activityPaymentRequedtBinding.paymentCard.setText("Pay");
                    activityPaymentRequedtBinding.cardDetailsContainer.setVisibility(View.VISIBLE);
                    activityPaymentRequedtBinding.contentContainer.setVisibility(View.GONE);
                    try {
                        if (mp.isLooping()) {
                            mp.setLooping(false);
                        }
                    }catch(Exception e){}
                    try {
                        if(mp.isPlaying()){
                            mp.stop();
                            mp.release();
                        }
                    }catch(Exception e){}

                }else if(activityPaymentRequedtBinding.paymentCard.getText().toString().trim().equalsIgnoreCase("Pay")){
                    activityPaymentRequedtBinding.paymentCard.setEnabled(false);
                    activityPaymentRequedtBinding.paymentCard.setAlpha(Float.valueOf("0.1"));
                    final ProgressDialog mProgressDialog;
                    mProgressDialog = ProgressDialog.show(context, null, null);
                    mProgressDialog.setContentView(R.layout.progress_loader);
                    mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    mProgressDialog.setCancelable(false);

                    if(activityPaymentRequedtBinding.cardMultilineWidget.getCard()!= null) {
                        Card card = activityPaymentRequedtBinding.cardMultilineWidget.getCard();
                        boolean validation = card.validateCard();
                        if (validation && validationname()) {
                            if (mProgressDialog != null && mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            //stripe =new Stripe(getContext(),"pk_test_LdDO8f12bgdmxn0YyvrDhh9y00RYrrksxL");
                            // stripe = new Stripe(getContext(), "pk_test_D3mZ9NKT7es8AVtfzHVhSwi400afF9EdXS");
                            stripe = new Stripe(context, "pk_test_D3mZ9NKT7es8AVtfzHVhSwi400afF9EdXS");
                            //stripe = new Stripe(context,"pk_live_uB9yAaATgvKiLxVrkLrMboDT00BoCv0j5h");
                            stripe.createToken(card, new ApiResultCallback<Token>() {
                                @Override
                                public void onSuccess(@androidx.annotation.NonNull Token result) {
                                    System.out.println("result --->" + result.getId());
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
                                        map.put("driver_id", driver_id);
                                        map.put("order_id", OrderId);
                                        map.put("email", appSession.getUser().getData().getEmail());
                                        map.put("stripeToken", result.getId());
                                        map.put("name", activityPaymentRequedtBinding.etCardName.getText().toString());
                                        map.put("card_number", card.getNumber());
                                        map.put("card_exp_month", String.valueOf(card.getExpMonth()));
                                        map.put("card_exp_year", String.valueOf(card.getExpYear()));
                                        map.put("card_cvc", card.getCVC());
                                        map.put("price", delivery_cost);

                                    /*try {
                                        map.put("price", String.format("%.2f", Double.parseDouble(deliveryDTO.getDeliveryCost())));
                                    } catch (Exception e) {
                                        map.put("price", deliveryDTO.getDeliveryCost());
                                        e.printStackTrace();
                                    }*/
                                        map.put("product_id", delivery_type);

                                        Log.d("paymentdata",map.toString());

                                        APIInterface apiInterface = APIClient.getClient();
                                        Call<OtherDTO> call = apiInterface.callPurchasePayApi(map);
                                        call.enqueue(new Callback<OtherDTO>() {
                                            @Override
                                            public void onResponse(Call<OtherDTO> call, Response<OtherDTO> response) {
                                                if (mProgressDialog != null && mProgressDialog.isShowing())
                                                    mProgressDialog.dismiss();
                                                System.out.println("payememnt--> "+new Gson().toJson(response.body()));

                                                if (response.isSuccessful()) {
                                                    try {
                                                        if (response.body().getResult().equalsIgnoreCase("success")) {
                                                            finishSound();

                                                            utilities.dialogOKre(context, "", response.body().getMessage(), getString(R.string.ok), new OnDialogConfirmListener() {
                                                                @Override
                                                                public void onYes() {

                                                                    //goto current list
                                                                    Intent intent = new Intent(PaymentRequest.this,DrawerContentSlideActivity.class);
                                                                    intent.putExtra("opentraker","yesopen");
                                                                    intent.putExtra("order_id",OrderId);
                                                                    intent.putExtra("delivery_type",delivery_type);
                                                                    startActivity(intent);
                                                                    finishAffinity();


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
                                                activityPaymentRequedtBinding.paymentCard.setEnabled(true);
                                                activityPaymentRequedtBinding.paymentCard.setAlpha(Float.valueOf("1"));
                                                utilities.dialogOK(context, "", context.getResources().getString(R.string.server_error), context.getResources().getString(R.string.ok), false);

                                            }
                                        });
                                    }

                                }

                                @Override
                                public void onError(@androidx.annotation.NonNull Exception e) {
                                    System.out.println("result --->" + e);
                                    utilities.dialogOK(context, "", context.getResources().getString(R.string.enter_valid_card_number), context.getResources().getString(R.string.ok), false);
                                    if (mProgressDialog != null && mProgressDialog.isShowing())
                                        mProgressDialog.dismiss();
                                    activityPaymentRequedtBinding.paymentCard.setEnabled(true);
                                    activityPaymentRequedtBinding.paymentCard.setAlpha(Float.valueOf("1"));
                                }
                            });

                        } else {
                            utilities.dialogOK(context, "", context.getResources().getString(R.string.enter_valid_card_number), context.getResources().getString(R.string.ok), false);
                      /*  cardPaymenrStripeBinding.paymentCard.setEnabled(true);
                        cardPaymenrStripeBinding.paymentCard.setAlpha(Float.valueOf("1"));*/
                            if (mProgressDialog != null && mProgressDialog.isShowing())
                                mProgressDialog.dismiss();
                            activityPaymentRequedtBinding.paymentCard.setEnabled(true);
                            activityPaymentRequedtBinding.paymentCard.setAlpha(Float.valueOf("1"));
                        }
                    }else{
                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        activityPaymentRequedtBinding.paymentCard.setEnabled(true);
                        activityPaymentRequedtBinding.paymentCard.setAlpha(Float.valueOf("1"));
                    }
              /*  cardPaymenrStripeBinding.paymentCard.setEnabled(true);
                cardPaymenrStripeBinding.paymentCard.setAlpha(Float.valueOf("1"));*/

                }


            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validationname() {
        if(activityPaymentRequedtBinding.etCardName.getText().toString().equalsIgnoreCase("")){

            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_vaild_cardholder_s_name), getString(R.string.ok), false);
            activityPaymentRequedtBinding.etCardName.requestFocus();
            return false;
        }
        return true;
    }

}


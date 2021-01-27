package com.pickanddrop.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import com.google.gson.Gson;
import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.CardPaymenrStripeBinding;
import com.pickanddrop.dto.DeliveryDTO;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.DatabaseHandler;
import com.pickanddrop.utils.OnDialogConfirmListener;
import com.pickanddrop.utils.Utilities;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;


import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CardStripePayment extends BaseFragment implements AppConstants, View.OnClickListener  {

    private Context context;
    private CardPaymenrStripeBinding cardPaymenrStripeBinding;
    private Stripe stripe;
    private AppSession appSession;
    private Utilities utilities;
    private DeliveryDTO.Data deliveryDTO;
    private String TAG = DeliveryCheckoutExpressDelivery.class.getName();
    DatabaseHandler db ;

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
        cardPaymenrStripeBinding = DataBindingUtil.inflate(inflater, R.layout.card_paymenr_stripe, container, false);
        return cardPaymenrStripeBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);
        db = new DatabaseHandler(getContext());
        initView();
    }

    private void initView() {
        //PaymentConfiguration.init( "pk_live_uB9yAaATgvKiLxVrkLrMboDT00BoCv0j5h");
        PaymentConfiguration.init( "pk_test_D3mZ9NKT7es8AVtfzHVhSwi400afF9EdXS");
        cardPaymenrStripeBinding.paymentCard.setOnClickListener(this);

        String delivery_cost = "";

//        try {

//            delivery_cost = String.format("%.2f", Double.parseDouble(deliveryDTO.getDeliveryCost()));
//        } catch (Exception e) {
//            delivery_cost = deliveryDTO.getDeliveryCost();
//            e.printStackTrace();
//        }

        try {
            delivery_cost = String.format("%.2f", Double.parseDouble(deliveryDTO.getDeliveryCost()));
        } catch (Exception e) {
            delivery_cost = deliveryDTO.getDeliveryCost();
            e.printStackTrace();
        }

        cardPaymenrStripeBinding.paymentCard.setText(getResources().getString(R.string.pay)+" $ "+delivery_cost);
        /*cardPaymenrStripeBinding.paymentCard.setEnabled(false);
        cardPaymenrStripeBinding.paymentCard.setAlpha(Float.valueOf("0.1"));
*/
        cardPaymenrStripeBinding.paymentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardPaymenrStripeBinding.paymentCard.setEnabled(false);
                cardPaymenrStripeBinding.paymentCard.setAlpha(Float.valueOf("0.1"));
                final ProgressDialog mProgressDialog;
                mProgressDialog = ProgressDialog.show(context, null, null);
                mProgressDialog.setContentView(R.layout.progress_loader);
                mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mProgressDialog.setCancelable(false);

                if(cardPaymenrStripeBinding.cardMultilineWidget.getCard()!= null) {
                    Card card = cardPaymenrStripeBinding.cardMultilineWidget.getCard();
                    boolean validation = card.validateCard();
                    if (validation && validationname()) {
                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                    //stripe =new Stripe(getContext(),"pk_test_LdDO8f12bgdmxn0YyvrDhh9y00RYrrksxL");
                        stripe = new Stripe(getContext(), "pk_test_D3mZ9NKT7es8AVtfzHVhSwi400afF9EdXS");
                       // stripe = new Stripe(getContext(), "pk_test_D3mZ9NKT7es8AVtfzHVhSwi400afF9EdXS");
                        //stripe = new Stripe(getContext(),"pk_live_uB9yAaATgvKiLxVrkLrMboDT00BoCv0j5h");
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
                                    map.put("driver_id", deliveryDTO.getDriverId());
                                    map.put("order_id", deliveryDTO.getOrderId());
                                    map.put("email", appSession.getUser().getData().getEmail());
                                    map.put("stripeToken", result.getId());
                                    map.put("name", cardPaymenrStripeBinding.etCardName.getText().toString());
                                    map.put("card_number", card.getNumber());
                                    map.put("card_exp_month", String.valueOf(card.getExpMonth()));
                                    map.put("card_exp_year", String.valueOf(card.getExpYear()));
                                    map.put("card_cvc", card.getCVC());
                                    map.put("price", deliveryDTO.getDeliveryCost());

                                    try {
                                        map.put("price", String.format("%.2f", Double.parseDouble(deliveryDTO.getDeliveryCost())));
                                    } catch (Exception e) {
                                        map.put("price", deliveryDTO.getDeliveryCost());
                                        e.printStackTrace();
                                    }
                                    map.put("product_id", deliveryDTO.getDeliveryType());

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

                                                        utilities.dialogOKre(context, "", response.body().getMessage(), getString(R.string.ok), new OnDialogConfirmListener() {
                                                            @Override
                                                            public void onYes() {
                                                               // ((DrawerContentSlideActivity) context).popAllFragment();
                                                                replaceFragmentWithoutBack(R.id.container_main, new CurrentList(), "CurrentList");
                                                                cardPaymenrStripeBinding.paymentCard.setEnabled(true);
                                                                cardPaymenrStripeBinding.paymentCard.setAlpha(Float.valueOf("1"));
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
                                            cardPaymenrStripeBinding.paymentCard.setEnabled(true);
                                            cardPaymenrStripeBinding.paymentCard.setAlpha(Float.valueOf("1"));
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
                                cardPaymenrStripeBinding.paymentCard.setEnabled(true);
                                cardPaymenrStripeBinding.paymentCard.setAlpha(Float.valueOf("1"));
                            }
                        });

                    } else {
                        utilities.dialogOK(context, "", context.getResources().getString(R.string.enter_valid_card_number), context.getResources().getString(R.string.ok), false);
                      /*  cardPaymenrStripeBinding.paymentCard.setEnabled(true);
                        cardPaymenrStripeBinding.paymentCard.setAlpha(Float.valueOf("1"));*/
                        if (mProgressDialog != null && mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        cardPaymenrStripeBinding.paymentCard.setEnabled(true);
                        cardPaymenrStripeBinding.paymentCard.setAlpha(Float.valueOf("1"));
                    }
                }else{
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    cardPaymenrStripeBinding.paymentCard.setEnabled(true);
                    cardPaymenrStripeBinding.paymentCard.setAlpha(Float.valueOf("1"));
                }
              /*  cardPaymenrStripeBinding.paymentCard.setEnabled(true);
                cardPaymenrStripeBinding.paymentCard.setAlpha(Float.valueOf("1"));*/

            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).popFragment();
                break;
        }
    }

    private boolean validationname() {
        if(cardPaymenrStripeBinding.etCardName.getText().toString().equalsIgnoreCase("")){

            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_vaild_cardholder_s_name), getString(R.string.ok), false);
            cardPaymenrStripeBinding.etCardName.requestFocus();
            return false;
        }
        return true;
    }




//    @Override
//    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        switch (checkedId){
//            case R.id.rb_inside_pickup:
//                dropoffLiftGate = "insidePickup";
//                break;
//            case R.id.rb_lift_gate:
//                dropoffLiftGate = "liftGate";
//                break;
//        }
//    }
}
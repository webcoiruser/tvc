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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.pickanddrop.R;
import com.pickanddrop.activities.NotificationDialog;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.ReviewBinding;
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

public class Review extends BaseFragment implements View.OnClickListener, AppConstants {

    private Context context;
    private AppSession appSession;
    private Utilities utilities;
    private ReviewBinding reviewBinding;
    private String order_id = "", profileImage = "", rateDriver = "", driverId = "", fullName = "", comments = "", customerRatingValue = "";
    private RequestOptions requestOptions;
    private String TAG = Review.class.getName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            rateDriver = getArguments().getString("rating");
            profileImage = getArguments().getString("profileImage");
            driverId = getArguments().getString("notification_id");
            fullName = getArguments().getString("fullName");
            order_id = getArguments().getString("order_id");

            Log.e(getClass().getName(), "Rate >>>>>>>>>" + rateDriver + "  Image >>>" + profileImage + "  Id >>>" + driverId + "  Name >>>>" + fullName);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        reviewBinding = DataBindingUtil.inflate(inflater, R.layout.review, container, false);
        return reviewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        appSession = new AppSession(context);
        utilities = Utilities.getInstance(context);
        initView();
        setValues();
    }

    private void setValues() {
        requestOptions = new RequestOptions();
        requestOptions.centerCrop();
        requestOptions.override(100, 100);
        requestOptions.placeholder(R.drawable.user_praba);
        requestOptions.error(R.drawable.user_praba);

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(profileImage)
                .into(reviewBinding.ivProfile);

        if (rateDriver != null && !rateDriver.equals("")) {
            reviewBinding.rbProfile.setRating(Float.parseFloat(rateDriver));
        }

        reviewBinding.tvName.setText(fullName);
        reviewBinding.tvText.setText("Rate "+fullName);
    }

    private void initView() {
        reviewBinding.btnSubmit.setOnClickListener(this);
        reviewBinding.ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                comments = reviewBinding.etComments.getText().toString().trim();
                customerRatingValue = reviewBinding.rbReview.getRating()+"";

                if (isValid()) {
                    callReviewApi();
                }
                break;
            case R.id.iv_back:
                ((NotificationDialog) context).finish();
//                ((DrawerContentSlideActivity) context).popFragment();
                break;
        }
    }

    private boolean isValid() {
        if (comments == null || comments.equals("")) {
            utilities.dialogOK(context, "", getString(R.string.please_give_comment), getString(R.string.ok), false);
            reviewBinding.etComments.requestFocus();
            return false;
        } else if (customerRatingValue == null || customerRatingValue.equals("0.0")) {
            utilities.dialogOK(context, "", getString(R.string.please_give_rate), getString(R.string.ok), false);
            return false;
        }
        return true;
    }

    public void callReviewApi() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);

            Map<String, String> map = new HashMap<>();
            map.put("customer_id", appSession.getUser().getData().getUserId());
            map.put("driver_id",  driverId);
            map.put("driverRatting",  customerRatingValue);
            map.put("feedback", comments);
            map.put("order_id", order_id);
            map.put("code", APP_TOKEN);

            APIInterface apiInterface = APIClient.getClient();
            Call<OtherDTO> call = apiInterface.callReviewApi(map);
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
//                                        ((DrawerContentSlideActivity) context).popFragment();
                                        ((NotificationDialog) context).finish();
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

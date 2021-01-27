package com.pickanddrop.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.pickanddrop.R;
import com.pickanddrop.activities.DrawerContentSlideActivity;
import com.pickanddrop.api.APIClient;
import com.pickanddrop.api.APIInterface;
import com.pickanddrop.databinding.ChangePasswordBinding;
import com.pickanddrop.dto.OtherDTO;
import com.pickanddrop.model.ChangePasswordModel;
import com.pickanddrop.utils.AppConstants;
import com.pickanddrop.utils.AppSession;
import com.pickanddrop.utils.DrawableClickListener;
import com.pickanddrop.utils.OnDialogConfirmListener;
import com.pickanddrop.utils.Utilities;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends BaseFragment implements AppConstants, View.OnClickListener {

    private Context context;
    private AppSession appSession;
    private Utilities utilities;
    String password_status = "show";
    private ChangePasswordBinding changePasswordBinding;
    private String oldPassword = "", newPassword = "", confirmPassword = "";
    private String TAG = ChangePassword.class.getName();
    ChangePasswordModel changePasswordModel = new ChangePasswordModel();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        changePasswordBinding = DataBindingUtil.inflate(inflater, R.layout.change_password, container, false);
        return changePasswordBinding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        initView();
//        initToolBar();
    }

//    private void initToolBar() {
//            android.support.v7.app.ActionBar actionBar = ((DrawerContentSlideActivity) context).getSupportActionBar();
//            actionBar.show();
//            ((DrawerContentSlideActivity) getActivity()).createBackButton(getResources().getString(R.string.change_password));
//            ColorDrawable colorDrawable = new ColorDrawable();
//            colorDrawable.setColor(getResources().getColor(R.color.background_color));
//            actionBar.setBackgroundDrawable(colorDrawable);
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setDisplayShowTitleEnabled(false);
//    }

    private void initView() {
        changePasswordBinding.btnSetPassword.setOnClickListener(this);
        changePasswordBinding.ivBack.setOnClickListener(this);

        changePasswordBinding.etCurrentPassword.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if (target == DrawablePosition.RIGHT) {//Do something here
                    if (password_status.equals("show")) {
                        password_status = "hide";

//                        logInBinding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        changePasswordBinding.etCurrentPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        changePasswordBinding.etCurrentPassword.setSelection(changePasswordBinding.etCurrentPassword.getText().length());
//                        logInBinding.etPassword.setTypeface(Typeface.create("titillium_regular", Typeface.NORMAL));
                        changePasswordBinding.etCurrentPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off_black_24dp, 0);
                    } else if (password_status.equals("hide")) {
                        password_status = "show";
//                        logInBinding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        changePasswordBinding.etCurrentPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        changePasswordBinding.etCurrentPassword.setSelection(changePasswordBinding.etCurrentPassword.getText().length());
//                        logInBinding.etPassword.setTypeface(Typeface.create("titillium_regular",Typeface.NORMAL));
                        changePasswordBinding.etCurrentPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic__eye_black_24dp, 0);
                    }
                }
            }
        });


        changePasswordBinding.etNewPassword.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if (target == DrawablePosition.RIGHT) {//Do something here
                    if (password_status.equals("show")) {
                        password_status = "hide";

//                        logInBinding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        changePasswordBinding.etNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        changePasswordBinding.etNewPassword.setSelection(changePasswordBinding.etNewPassword.getText().length());
//                        logInBinding.etPassword.setTypeface(Typeface.create("titillium_regular", Typeface.NORMAL));
                        changePasswordBinding.etNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off_black_24dp, 0);
                    } else if (password_status.equals("hide")) {
                        password_status = "show";
//                        logInBinding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        changePasswordBinding.etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        changePasswordBinding.etNewPassword.setSelection(changePasswordBinding.etNewPassword.getText().length());
//                        logInBinding.etPassword.setTypeface(Typeface.create("titillium_regular",Typeface.NORMAL));
                        changePasswordBinding.etNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic__eye_black_24dp, 0);
                    }
                }
            }
        });


        changePasswordBinding.etRepeatPassword.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                if (target == DrawablePosition.RIGHT) {//Do something here
                    if (password_status.equals("show")) {
                        password_status = "hide";

//                        logInBinding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        changePasswordBinding.etRepeatPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        changePasswordBinding.etRepeatPassword.setSelection(changePasswordBinding.etRepeatPassword.getText().length());
//                        logInBinding.etPassword.setTypeface(Typeface.create("titillium_regular", Typeface.NORMAL));
                        changePasswordBinding.etRepeatPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off_black_24dp, 0);
                    } else if (password_status.equals("hide")) {
                        password_status = "show";
//                        logInBinding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        changePasswordBinding.etRepeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        changePasswordBinding.etRepeatPassword.setSelection(changePasswordBinding.etRepeatPassword.getText().length());
//                        logInBinding.etPassword.setTypeface(Typeface.create("titillium_regular",Typeface.NORMAL));
                        changePasswordBinding.etRepeatPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic__eye_black_24dp, 0);
                    }
                }
            }
        });






    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_set_password:
                Utilities.hideKeyboard(changePasswordBinding.btnSetPassword);
                oldPassword = changePasswordBinding.etCurrentPassword.getText().toString();
                newPassword = changePasswordBinding.etNewPassword.getText().toString();
                confirmPassword = changePasswordBinding.etRepeatPassword.getText().toString();
                if (isValid()) {
                    callChangeApi();
                }
                break;
            case R.id.iv_back:
                ((DrawerContentSlideActivity) context).openMenuDrawer();
                break;
        }
    }

    public void callChangeApi() {
        if (!utilities.isNetworkAvailable())
            utilities.dialogOK(context, "", context.getResources().getString(R.string.network_error), context.getString(R.string.ok), false);
        else {
            final ProgressDialog mProgressDialog;
            mProgressDialog = ProgressDialog.show(context, null, null);
            mProgressDialog.setContentView(R.layout.progress_loader);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);

//            Map<String, String> map = new HashMap<>();
//            map.put("old_password", oldPassword);
//            map.put("new_password", newPassword);
//            map.put(PN_APP_TOKEN, APP_TOKEN);
//            map.put("userid", appSession.getUser().getData().getUserId());

            changePasswordModel.setOld_password(oldPassword);
            changePasswordModel.setNew_password(newPassword);
            changePasswordModel.setCode(APP_TOKEN);
            changePasswordModel.setUserid( appSession.getUser().getData().getUserId());


            APIInterface apiInterface = APIClient.getClient();
            Call<OtherDTO> call = apiInterface.callChangePassword(changePasswordModel);
            call.enqueue(new Callback<OtherDTO>() {
                @Override
                public void onResponse(Call<OtherDTO> call, Response<OtherDTO> response) {
                    if (mProgressDialog != null && mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    if (response.isSuccessful()) {
                        try {
                            if (response.body().getResult().equalsIgnoreCase("success")) {
                                String output = response.body().getMessage().substring(0, 1).toUpperCase() + response.body().getMessage().substring(1)+".";
                                utilities.dialogOKre(context, "", output, getString(R.string.ok), new OnDialogConfirmListener() {
                                    @Override
                                    public void onYes() {
                                        if (appSession.getUserType().equals(DRIVER))
                                            replaceFragmentWithoutBack(R.id.container_main, new DriverHome(), "DriverHome");
                                        else
//                                            replaceFragmentWithoutBack(R.id.container_main, new Home(), "Home");
                                            replaceFragmentWithoutBack(R.id.container_main, new UserHome(), "UserHome");
                                    }

                                    @Override
                                    public void onNo() {

                                    }
                                });
                            } else {
                                String output = response.body().getMessage().substring(0, 1).toUpperCase() + response.body().getMessage().substring(1);
                                utilities.dialogOK(context, "", output, context.getString(R.string.ok), false);
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

    private boolean isValid() {
        if (oldPassword.trim().length() == 0) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_current_password), getString(R.string.ok), false);
            changePasswordBinding.etCurrentPassword.requestFocus();
            return false;
        } else if (oldPassword.length() < 6 || oldPassword.length() > 20) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_valid_password), getString(R.string.ok), false);
            changePasswordBinding.etCurrentPassword.requestFocus();
            return false;
        } else if (newPassword.trim().length() == 0) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_password), getString(R.string.ok), false);
            changePasswordBinding.etNewPassword.requestFocus();
            return false;
        } else if (newPassword.length() < 6 || newPassword.length() > 20) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_valid_password), getString(R.string.ok), false);
            changePasswordBinding.etNewPassword.requestFocus();
            return false;
        } else if (confirmPassword.trim().length() == 0) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_confirm_password), getString(R.string.ok), false);
            changePasswordBinding.etRepeatPassword.requestFocus();
            return false;
        } else if (confirmPassword.length() < 6 || confirmPassword.length() > 20) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.please_enter_valid_password), getString(R.string.ok), false);
            changePasswordBinding.etRepeatPassword.requestFocus();
            return false;
        } else if (!newPassword.equals(confirmPassword)) {
            utilities.dialogOK(context, getString(R.string.validation_title), getString(R.string.your_confirm_pass_doesnot), getString(R.string.ok), false);
            changePasswordBinding.etRepeatPassword.requestFocus();
            return false;
        }

        return true;
    }
}


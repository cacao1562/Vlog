package kr.co.valuesys.vlog.mobile.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import kr.co.valuesys.vlog.mobile.Activity.LoginActivity;
import kr.co.valuesys.vlog.mobile.Application.MobileApplication;
import kr.co.valuesys.vlog.mobile.BuildConfig;
import kr.co.valuesys.vlog.mobile.Common.LogUtil;
import kr.co.valuesys.vlog.mobile.Common.SimpleAlert;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.FragmentAppInfoBinding;

import static kr.co.valuesys.vlog.mobile.Common.Constants.FaceBook;
import static kr.co.valuesys.vlog.mobile.Common.Constants.Kakao;

public class AppInfoFragment extends DialogFragment {

    private FragmentAppInfoBinding binding;

    public static AppInfoFragment newInstance() {
        AppInfoFragment fragment = new AppInfoFragment();

        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        return super.onCreateDialog(savedInstanceState);
        LogUtil.d("ddd", "onCreateDialog appinfo");
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                //do your stuff

                dismiss();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.d("ddd", "onStart appinfo");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.d("ddd", "onStop appinfo");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d("ddd", "onResume appinfo");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.d("ddd", "onDestroyView appinfo");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("ddd", "onDestroy appinfo");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.d("ddd", "onDetach appinfo");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_app_info, container, false);
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_app_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.versionTextview.setText(BuildConfig.VERSION_NAME);

//        String platform = MobileApplication.getContext().getmLoginPlatform();

        binding.loginPlatform.setText(" Login Platform : " + MobileApplication.getLoginSession());
        binding.loginName.setText(MobileApplication.getContext().getLoginkName());

        if (TextUtils.equals(MobileApplication.getLoginSession(), Kakao)) {

            binding.logoutButton.setBackgroundColor(getResources().getColor(R.color.kakao_color));
            binding.logoutButton.setTextColor(getResources().getColor(R.color.black));

        }else if (TextUtils.equals(MobileApplication.getLoginSession(), FaceBook)) {

            binding.logoutButton.setBackgroundColor(getResources().getColor(R.color.facebook_color));
            binding.logoutButton.setTextColor(getResources().getColor(R.color.white));

        }else {

            binding.logoutButton.setVisibility(View.GONE);
        }

        binding.backButton.setOnClickListener(v -> {

//            getActivity().finish();
            dismiss();

        });

        binding.logoutButton.setOnClickListener(v -> {

            if (TextUtils.equals(MobileApplication.getLoginSession(), Kakao)) {

                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {

                        LogUtil.d("ooo", "logout");

                        resetLogin();

                        if (getActivity() != null) {

                            getActivity().runOnUiThread(() -> {

                                Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                                AlertDialog alert = SimpleAlert.createAlert(getActivity(), "로그아웃 되었습니다.", false, dialog -> {

                                    dialog.dismiss();
                                    presentLogin();
//                                getActivity().finish();
                                });
                                alert.show();

                            });

                        }

                    }
                });

            } else if (TextUtils.equals(MobileApplication.getLoginSession(), FaceBook)) {

                LoginManager.getInstance().logOut();

                resetLogin();

                getActivity().runOnUiThread(() -> {

                    Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                    AlertDialog alert = SimpleAlert.createAlert(getActivity(), "로그아웃 되었습니다.", false, dialog -> {

                        dialog.dismiss();
                        presentLogin();
//                        getActivity().finish();
                    });
                    alert.show();

                });

            }

        });


    }

    private void resetLogin() {

        MobileApplication.getContext().setmLoginName("");
        MobileApplication.getContext().setmLoginPlatform("");
    }

    private void presentLogin() {

        dismiss();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


}

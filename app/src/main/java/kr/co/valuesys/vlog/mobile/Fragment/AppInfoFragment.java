package kr.co.valuesys.vlog.mobile.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

import kr.co.valuesys.vlog.mobile.BuildConfig;
import kr.co.valuesys.vlog.mobile.Common.LogUtil;
import kr.co.valuesys.vlog.mobile.Common.SimpleAlert;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.FragmentAppInfoBinding;


public class AppInfoFragment extends Fragment {

    private FragmentAppInfoBinding binding;

    public static AppInfoFragment newInstance() {
        AppInfoFragment fragment = new AppInfoFragment();

        return fragment;
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
        binding.backButton.setOnClickListener(v -> {
                getActivity().finish();

        });

        binding.kakaoLogout.setOnClickListener(v -> {

            UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {

                    LogUtil.d("ooo", "logout");

                    getActivity().runOnUiThread(() -> {

                        Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                        AlertDialog alert = new SimpleAlert().createAlert(getActivity(), "로그아웃 되었습니다.", false, dialog -> {

                            dialog.dismiss();
                            getActivity().finish();
                        });
                        alert.show();

                    });

                }
            });
        });

        binding.fbLogout.setOnClickListener(v -> {

            LoginManager.getInstance().logOut();
            getActivity().runOnUiThread(() -> {

                Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                AlertDialog alert = new SimpleAlert().createAlert(getActivity(), "로그아웃 되었습니다.", false, dialog -> {

                    dialog.dismiss();
                    getActivity().finish();
                });
                alert.show();

            });

        });

    }


}

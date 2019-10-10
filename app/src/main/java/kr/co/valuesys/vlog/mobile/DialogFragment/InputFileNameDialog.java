package kr.co.valuesys.vlog.mobile.DialogFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import kr.co.valuesys.vlog.mobile.Common.CommonInterface;
import kr.co.valuesys.vlog.mobile.Common.Constants;
import kr.co.valuesys.vlog.mobile.Common.FileManager;
import kr.co.valuesys.vlog.mobile.Common.SimpleAlert;
import kr.co.valuesys.vlog.mobile.R;
import kr.co.valuesys.vlog.mobile.databinding.DialogInputfilenameBinding;

public class InputFileNameDialog extends DialogFragment {


    public InputFileNameDialog() { }

    private String tempPath;

    public static InputFileNameDialog newInstance() {

        InputFileNameDialog dialog = new InputFileNameDialog();
        return dialog;
    }

    private DialogInputfilenameBinding binding;

    private CommonInterface.OnCallbackToMain mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof CommonInterface.OnCallbackToMain) {
            mListener = (CommonInterface.OnCallbackToMain) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        try {
//            mListener = (CommonInterface.OnInputDialogListener) getParentFragment();
//        } catch (ClassCastException e) {
//            throw new ClassCastException("Calling fragment must implement Callback interface");
//        }

        if (getArguments() != null) {
            tempPath = getArguments().getString(Constants.ARG_Key);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_inputfilename, container, false);
//        View view = inflater.inflate(R.layout.dialog_inputfilename, container);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        binding.backButton.setOnClickListener(v -> {
            dismiss();
        });

        binding.saveButton.setOnClickListener(v -> {

            if (TextUtils.isEmpty(binding.fileNameEdittext.getText().toString()) ) {

                AlertDialog alert = SimpleAlert.createAlert(getActivity(), getString(R.string.empty_name_alert_msg), false, dialog -> {

                    dialog.dismiss(); // AlertDialog dismiss

                });

                alert.show();

            }else {

                FileManager.saveFile(getActivity(), tempPath, binding.fileNameEdittext.getText().toString(), result -> {

                    if (result) {

                        AlertDialog alert = SimpleAlert.createAlert(getActivity(), getString(R.string.saved_alert_msg), false, dialog -> {

                            dialog.dismiss(); // AlertDialog dismiss

                            dismiss();       // DialogFragment dismiss
//                            getActivity().finish();
//                            getDialog().dismiss();
                            mListener.oncallbackMain(1);

                        });

                        alert.show();

                    }else {

                        Toast.makeText(getActivity(), "저장 실패", Toast.LENGTH_LONG).show();
                    }

                });


            }

        });
    }




}
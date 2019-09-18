package kr.co.valuesys.vlog.mobile;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.co.valuesys.vlog.mobile.databinding.DialogInputfilenameBinding;

public class InputFileNameDialog extends DialogFragment {

    public interface OnInputDialogListener {
        void onClickSave(String fileName);
    }

    private OnInputDialogListener mListener;

    public InputFileNameDialog() { }

    public static InputFileNameDialog newInstance() {
        return new InputFileNameDialog();
    }

    private DialogInputfilenameBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mListener = (OnInputDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
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

        binding.backButton.setOnClickListener(v -> {
            dismiss();
        });

        binding.saveButton.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onClickSave(binding.fileNameEdittext.getText().toString());
            }
            dismiss();
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mListener = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}

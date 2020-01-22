package kr.co.valuesys.vlog.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.co.valuesys.vlog.mobile.application.MobileApplication;
import kr.co.valuesys.vlog.mobile.common.CommonInterface;
import kr.co.valuesys.vlog.mobile.databinding.InfoItemBinding;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {

    private Context m_context;
    private CommonInterface.OnSelectedCallback m_callback;
    private String[] m_titleArray;

    public InfoAdapter(Context m_context, CommonInterface.OnSelectedCallback onSelectedCallback) {
        this.m_context = m_context;
        this.m_callback = onSelectedCallback;
    }

    @NonNull
    @Override
    public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        InfoItemBinding binding = InfoItemBinding.inflate(LayoutInflater.from(m_context), parent, false);
        return new InfoViewHolder(binding, m_callback);
    }

    @Override
    public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
        InfoItemBinding binding = holder.binding;
        binding.leftTextview.setText(m_titleArray[position]);
        switch (position) {
            case 0:
                binding.rightTextview.setText(MobileApplication.getContext().getLoginkName());
                break;
            case 1:
                binding.rightTextview.setText(MobileApplication.getContext().getmLoginPlatform());
                break;
        }

    }

    @Override
    public int getItemCount() {
        return m_titleArray.length;
    }

    public void setup(String[] data) {
        this.m_titleArray = data;
        notifyDataSetChanged();
    }

    public static class InfoViewHolder extends RecyclerView.ViewHolder {

        private InfoItemBinding binding;
        private CommonInterface.OnSelectedCallback callback;

        public InfoViewHolder(@NonNull InfoItemBinding binding, CommonInterface.OnSelectedCallback onSelectedCallback) {
            super(binding.getRoot());
            this.binding = binding;
            this.callback = onSelectedCallback;

            binding.itemView.setOnClickListener(v -> {
                if (callback != null) {
                    int pos = getAdapterPosition();
                    if (pos == 1) {
                        callback.onSelected(pos);
                    }
                }
            });
        }
    }
}

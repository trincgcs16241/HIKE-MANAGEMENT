package com.example.hike_manager_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hike_manager_app.R;
import com.example.hike_manager_app.model.HikeModel;

import java.util.ArrayList;

public class HikeAdapter extends RecyclerView.Adapter<HikeAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<HikeModel> arrayList;
    private final OnClickListener onClickListener;

    public HikeAdapter(Context context, ArrayList<HikeModel> arrayList, OnClickListener onClickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.hike_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HikeModel hikeModel = arrayList.get(position);
        holder.name.setText(hikeModel.getName());
        holder.date.setText(hikeModel.getDate());

        holder.itemView.setOnClickListener(v -> onClickListener.onItemClick(hikeModel.getUuid()));

        holder.btnEdit.setOnClickListener(v -> onClickListener.onEditClick(hikeModel.getUuid()));

        holder.btnDelete.setOnClickListener(v -> onClickListener.onDeleteClick(hikeModel.getUuid()));
    }

    public void onDataChange(ArrayList<HikeModel> arrayList) {
        this.arrayList.clear();
        this.arrayList = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView date;

        private Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public interface OnClickListener {
        void onItemClick(String id);

        void onEditClick(String id);

        void onDeleteClick(String id);
    }
}

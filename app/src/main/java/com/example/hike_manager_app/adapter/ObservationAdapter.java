package com.example.hike_manager_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hike_manager_app.R;
import com.example.hike_manager_app.model.ObservationItem;

import java.util.ArrayList;

public class ObservationAdapter extends RecyclerView.Adapter<ObservationAdapter.ExpenseItemViewHolder> {
    private Context context;
    private ArrayList<ObservationItem> arrayList;

    public ObservationAdapter(Context context, ArrayList<ObservationItem> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ExpenseItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View heroView = inflater.inflate(R.layout.observation_item_layout, parent, false);
        ExpenseItemViewHolder expenseItemViewHolder = new ExpenseItemViewHolder(heroView);
        return expenseItemViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseItemViewHolder holder, int position) {
        holder.observation.setText(arrayList.get(position).getObservation());
        holder.date.setText(arrayList.get(position).getDate());
        holder.time.setText(arrayList.get(position).getTime());
        holder.comment.setText(arrayList.get(position).getComment());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ExpenseItemViewHolder extends RecyclerView.ViewHolder {
        private TextView observation, date, time, comment;

        public ExpenseItemViewHolder(@NonNull View itemView) {
            super(itemView);
            observation = itemView.findViewById(R.id.observation);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}

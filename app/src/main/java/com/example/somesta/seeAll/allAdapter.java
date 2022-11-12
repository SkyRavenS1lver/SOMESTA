package com.example.somesta.seeAll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.example.somesta.Activity.MainActivity;
import com.example.somesta.R;
import java.util.HashSet;
import java.util.List;


public class allAdapter extends RecyclerView.Adapter<allAdapter.HolderData> {
    List<String> listData;
    LayoutInflater inflater;
    HashSet<String> grouped;


    public void setListData(List<String> listData) {
        this.listData = listData;
    }

    public allAdapter(@NonNull Context context, List<String> listData, HashSet<String> grouped) {
        this.listData = listData;
        this.inflater = LayoutInflater.from(context);
        this.grouped = grouped;

    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.see_all_item, parent, false);
        return new allAdapter.HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
//        holder.group.setText(listData.get(position));
        holder.group_text.setText(listData.get(position));
        String name = holder.group_text.getText().toString();
        holder.group.setChecked(grouped.contains(name));
        holder.group_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.group.isChecked()){
                    grouped.remove(name);
                    holder.group.setChecked(false);
                }else{
                    grouped.add(name);
                    holder.group.setChecked(true);
                }
            }
        });
        holder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.group.isChecked()){
                    grouped.add(name);
                }
                else {grouped.remove(name);}
            }
        });

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class HolderData extends RecyclerView.ViewHolder{
        public CheckBox group;
        public TextView group_text;
        public View group_layout;
        public HolderData(@NonNull View itemView) {
            super(itemView);
            group = itemView.findViewById(R.id.group);
            group_text = itemView.findViewById(R.id.group_text);
            group_layout = itemView.findViewById(R.id.group_layout);
        }
    }
}

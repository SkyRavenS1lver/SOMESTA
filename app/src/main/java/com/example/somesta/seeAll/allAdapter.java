package com.example.somesta.seeAll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        holder.group.setText(listData.get(position));
        String name = holder.group.getText().toString();
        holder.group.setChecked(grouped.contains(name));
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
        public HolderData(@NonNull View itemView) {
            super(itemView);
            group = itemView.findViewById(R.id.group);
        }
    }
}

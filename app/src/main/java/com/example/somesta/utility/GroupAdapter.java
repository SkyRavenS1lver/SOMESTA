package com.example.somesta.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.somesta.Activity.MainActivity;
import com.example.somesta.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.HolderData> {
    List<String> listData;
    LayoutInflater inflater;
    HashSet<String> grouped;



    public GroupAdapter(Context context, List<String> listData, HashSet<String> grouped) {
        this.grouped = grouped;
        this.listData = listData;
        this.inflater = LayoutInflater.from(context);
    }
    public void updateListData(List<String> listDataBaru) {
        final GroupDiffCallBack diffCallback = new GroupDiffCallBack(this.listData, listDataBaru);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.listData.clear();
        this.listData.addAll(listDataBaru);
        diffResult.dispatchUpdatesTo(this);
    }
    @NonNull
    @Override
    public GroupAdapter.HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.filter_data, parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        holder.group.setText(listData.get(position));
        holder.group.setTextOff(listData.get(position));
        holder.group.setTextOn(listData.get(position));
        String name = holder.group.getText().toString();
        if (grouped.contains(name)
        )
        {holder.group.setChecked(true);}
        else {holder.group.setChecked(false);}
//        if (this.tipe.contains(name)){holder.group.setChecked(true);}
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
    public void setListData(List<String> listData) {
        this.listData = listData;
    }
//    @Override
//    public void onBindViewHolder(@NonNull GroupAdapter.HolderData holder, int position, List<Object> payloads) {
//        holder.group.setText(listData.get(position));
//        holder.group.setTextOff(listData.get(position));
//        holder.group.setTextOn(listData.get(position));
//        String name = holder.group.getText().toString();
//        if (grouped.contains(name) || !payloads.isEmpty()
//        )
//        {holder.group.setChecked(true);}
//        else {holder.group.setChecked(false);}
////        if (this.tipe.contains(name)){holder.group.setChecked(true);}
//        holder.group.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b){
//                    grouped.add(name);
//                }
//                else {
//                    grouped.remove(name);
//                }
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class HolderData extends RecyclerView.ViewHolder{
        public ToggleButton group;
        public HolderData(@NonNull View itemView) {
            super(itemView);
            group = itemView.findViewById(R.id.group);
        }
    }
}

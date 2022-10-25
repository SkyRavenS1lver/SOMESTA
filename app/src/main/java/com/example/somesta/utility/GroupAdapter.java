package com.example.somesta.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.somesta.Activity.MainActivity;
import com.example.somesta.R;

import java.util.List;
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.HolderData> {
    List<String> listData;
    LayoutInflater inflater;

    public GroupAdapter(Context context, List<String> listData) {
        this.listData = listData;
        this.inflater = LayoutInflater.from(context);
    }
    @NonNull
    @Override
    public GroupAdapter.HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.filter_data, parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.HolderData holder, int position) {
        holder.group.setText(listData.get(position));
        holder.group.setTextOff(listData.get(position));
        holder.group.setTextOn(listData.get(position));
        String name = holder.group.getText().toString();
        if (MainActivity.clickedList.contains(name)){holder.group.setChecked(true);}
        holder.group.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){MainActivity.clickedList.add(name);}
                else {MainActivity.clickedList.remove(name);}
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class HolderData extends RecyclerView.ViewHolder{
        ToggleButton group;
        public HolderData(@NonNull View itemView) {
            super(itemView);
            group = itemView.findViewById(R.id.group);
        }
    }
}

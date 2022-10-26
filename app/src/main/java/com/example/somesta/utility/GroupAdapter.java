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

import java.util.ArrayList;
import java.util.List;
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.HolderData> {
    List<String> listData;
    LayoutInflater inflater;
    String tipe;


    public GroupAdapter(Context context, List<String> listData, String tipe) {
        this.tipe = tipe;
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
        if (
        MainActivity.groupClicked.contains(name) ||
        MainActivity.statusClicked.contains(name) ||
        MainActivity.lokasiClicked.contains(name) ||
        MainActivity.kebutuhanClicked.contains(name) ||
        MainActivity.jenisClicked.contains(name)
        )
        {holder.group.setChecked(true);}
//        if (this.tipe.contains(name)){holder.group.setChecked(true);}
        holder.group.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    if (tipe.equals("group")){MainActivity.groupClicked.add(name);}
                    else if (tipe.equals("status")){MainActivity.statusClicked.add(name);}
                    else if (tipe.equals("lokasi")){MainActivity.lokasiClicked.add(name);}
                    else if (tipe.equals("kebutuhan")){MainActivity.kebutuhanClicked.add(name);}
                    else if (tipe.equals("jenis")){MainActivity.jenisClicked.add(name);}
                }
                else {
                    if (tipe.equals("group")){MainActivity.groupClicked.remove(name);}
                    else if (tipe.equals("status")){MainActivity.statusClicked.remove(name);}
                    else if (tipe.equals("lokasi")){MainActivity.lokasiClicked.remove(name);}
                    else if (tipe.equals("kebutuhan")){MainActivity.kebutuhanClicked.remove(name);}
                    else if (tipe.equals("jenis")){MainActivity.jenisClicked.remove(name);}
                }
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

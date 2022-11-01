package com.example.somesta.seeAll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.somesta.R;
import com.example.somesta.utility.GroupAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class allAdapter extends RecyclerView.Adapter<allAdapter.HolderData> {
    List<String> listData;
    LayoutInflater inflater;
    ArrayList<String> grouped;
    String namaFilter;
    ArrayList<String> temporary;

    public allAdapter(@NonNull Context context, List<String> listData, ArrayList<String> grouped, String nama) {
        this.listData = listData;
        this.inflater = LayoutInflater.from(context);
        this.grouped = grouped;
        this.namaFilter = nama;
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
        if (grouped.contains(name))
        {holder.group.setChecked(true);}
        holder.group.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    grouped.add(name);
                }
                else {
                    grouped.remove(name);
                }
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

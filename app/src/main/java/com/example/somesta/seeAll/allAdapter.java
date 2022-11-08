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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.somesta.R;
import com.example.somesta.utility.GroupAdapter;
import com.example.somesta.utility.GroupDiffCallBack;

import org.w3c.dom.Text;

import java.util.ArrayList;
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
    public void updateListData(List<String> listDataBaru) {
        final GroupDiffCallBack diffCallback = new GroupDiffCallBack(this.listData, listDataBaru);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.listData.clear();
        this.listData.addAll(listDataBaru);
        diffResult.dispatchUpdatesTo(this);
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
        System.out.println(name);
        System.out.println(grouped.contains(name));
        holder.group.setChecked(grouped.contains(name));
//        holder.group.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (holder.group.isChecked()){
//                    grouped.add(name);
//                    System.out.println(grouped);
//                    System.out.println("+++");
//                }
//                else {grouped.remove(name);
//                    System.out.println(grouped);
//                    System.out.println("+++");
//                }
//            }
//        });
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

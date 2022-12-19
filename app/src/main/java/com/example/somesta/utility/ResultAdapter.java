package com.example.somesta.utility;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.somesta.Activity.MainActivity;
import com.example.somesta.Marker.Perusahaan;
import com.example.somesta.R;

import org.osmdroid.util.GeoPoint;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.HolderData> {
    ArrayList<Perusahaan> listData;
    LayoutInflater inflater;

    public ResultAdapter(Context ctx, ArrayList<Perusahaan> Perusahaan) {
        this.listData = Perusahaan;
        this.inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public HolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.result_data, parent, false);
        return new ResultAdapter.HolderData(view);
    }

    public void setListData(ArrayList<Perusahaan> listData) {
        this.listData = listData;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderData holder, int position) {
        Perusahaan perusahaan = listData.get(position);
        holder.nama.setText(perusahaan.getNama());
        holder.lokasi.setText(perusahaan.getTempat());
        holder.clickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.mapController.setZoom(17);
                MainActivity.mapController.setCenter(perusahaan.getLocation());
                MainActivity.viewListDialog.dismiss();
            }
        });
    }


    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class HolderData extends RecyclerView.ViewHolder {
        TextView nama;
        TextView lokasi;
        LinearLayout clickable;
        public HolderData(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.ResultNama);
            lokasi = itemView.findViewById(R.id.ResultLokasi);
            clickable = (LinearLayout) itemView.findViewById(R.id.BG);
        }
    }
}

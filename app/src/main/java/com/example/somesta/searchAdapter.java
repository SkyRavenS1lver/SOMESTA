package com.example.somesta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class searchAdapter extends RecyclerView.Adapter<searchAdapter.DataPerusahaanHolder> {
    //Remember Adapter class has 2 classes
    //DataPerusahaan Adapter
    private Context context;
    private ArrayList<searchData> dataPerusahaan;

    //constructors
    public searchAdapter(Context context, ArrayList<searchData> dataPerusahaan) {
        this.context = context;
        this.dataPerusahaan = dataPerusahaan;
    }

    @NonNull
    @Override
    public DataPerusahaanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //search_layout_items adalah layout xml untuk data-data nama perusahaan dimasukan
        View view = LayoutInflater.from(this.context).inflate(R.layout.search_layout_items,parent,false);
        return new DataPerusahaanHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull searchAdapter.DataPerusahaanHolder holder, int position) {
        searchData data = dataPerusahaan.get(position);

        holder.SetDetails(data);
    }

    @Override
    public int getItemCount() {
        return dataPerusahaan.size();
    }

    private onItemClickListenerCUSTOM mListener;

    public interface onItemClickListenerCUSTOM{
        void onItemClick(int position);
    }

    public void setOnItemClickListenerCUSTOM(onItemClickListenerCUSTOM listener){
        mListener = listener;
    }

    //DataPerusahaanHolder
    class DataPerusahaanHolder extends RecyclerView.ViewHolder{
        private TextView txtNamaPerusahaan, txtLokasiPerusahaan;

        public DataPerusahaanHolder(@NonNull View itemView, onItemClickListenerCUSTOM listener) {
            super(itemView);
            txtNamaPerusahaan = itemView.findViewById(R.id.textViewNamaPerusahaan);
            txtLokasiPerusahaan = itemView.findViewById(R.id.textViewLokasiPerusahaan);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        listener.onItemClick(position);
                    }
                }
            });
        }

        public void SetDetails(searchData dataDalamSearchData) {
            txtNamaPerusahaan.setText(dataDalamSearchData.getNamaPerusahaan());
            txtLokasiPerusahaan.setText(dataDalamSearchData.getLokasiPerusahaan());
        }

        public String getNamaPerusahaan(){
            return "null";
        }
    }
}

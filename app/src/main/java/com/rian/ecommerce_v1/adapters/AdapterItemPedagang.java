package com.rian.ecommerce_v1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rian.ecommerce_v1.R;
import com.rian.ecommerce_v1.models.ModelBarangs;
import com.rian.ecommerce_v1.server.BaseURL;
import com.rian.ecommerce_v1.utils.RupiahConvert;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterItemPedagang extends RecyclerView.Adapter<AdapterItemPedagang.ViewHolder> {

    private Context context;
    private List<ModelBarangs> barangList;

    public AdapterItemPedagang(Context context, List<ModelBarangs> barangList){
        this.context = context;
        this.barangList = barangList;
    }

    @NonNull
    @Override
    public AdapterItemPedagang.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_pedagang, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterItemPedagang.ViewHolder holder, int position) {
        ModelBarangs itemBarang = barangList.get(position);

        Picasso.get().load(BaseURL.baseUrl +  itemBarang.getFotobarang()).into(holder.photoBarang);
        holder.namaBarang.setText(itemBarang.getNamaBarang());
        holder.deskripsiBarang.setText(itemBarang.getDeksripsiBarang());
        holder.hargaBarang.setText(RupiahConvert.convertRupiah(itemBarang.getHargBarang()));
        holder.stokBarang.setText("Stok - " + itemBarang.getStokBarang());
    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView namaBarang, deskripsiBarang, hargaBarang, stokBarang;
        ImageView photoBarang;

        public ViewHolder(View itemView){
            super(itemView);

            namaBarang = itemView.findViewById(R.id.nama_barang);
            deskripsiBarang = itemView.findViewById(R.id.deskripsi_barang);
            hargaBarang = itemView.findViewById(R.id.harga_barang);
            stokBarang = itemView.findViewById(R.id.stok_barang);
            photoBarang = itemView.findViewById(R.id.photo_barang);
        }
    }
}

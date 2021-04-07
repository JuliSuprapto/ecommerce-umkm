package com.rian.ecommerce_v1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rian.ecommerce_v1.R;
import com.rian.ecommerce_v1.models.ModelKeranjang;
import com.rian.ecommerce_v1.server.BaseURL;
import com.rian.ecommerce_v1.utils.RupiahConvert;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterLog extends RecyclerView.Adapter<AdapterLog.ViewHolder> {

    private Context context;
    private List<ModelKeranjang> listPengiriminan;

    public AdapterLog(Context context, List<ModelKeranjang> listPengiriminan) {
        this.context = context;
        this.listPengiriminan = listPengiriminan;
    }

    @NonNull
    @Override
    public AdapterLog.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_log, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterLog.ViewHolder holder, int position) {
        final ModelKeranjang keranjang = listPengiriminan.get(position);

        holder.namaBarang.setText(keranjang.getNamaBarang());
        holder.namaToko.setText(keranjang.getNamaToko());
        Picasso.get().load(BaseURL.baseUrl + keranjang.getFotobarang()).resize(500, 400).centerCrop().into(holder.photoKeranjang);

        holder.hargaTotal.setText(RupiahConvert.convertRupiah(keranjang.getTotalHarga()));
    }

    @Override
    public int getItemCount() {
        return listPengiriminan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photoKeranjang;
        TextView namaBarang, namaToko, hargaTotal;
        LinearLayout btnSelesai;

        public ViewHolder(View itemView) {
            super(itemView);

            photoKeranjang = (ImageView) itemView.findViewById(R.id.photo_barang_keranjang);

            namaBarang = (TextView) itemView.findViewById(R.id.nama_barang_keranjang);
            namaToko = (TextView) itemView.findViewById(R.id.nama_toko_keranjang);

            hargaTotal = (TextView) itemView.findViewById(R.id.total_harga_keranjang);

            btnSelesai = (LinearLayout) itemView.findViewById(R.id.selesai_item);
        }
    }

}

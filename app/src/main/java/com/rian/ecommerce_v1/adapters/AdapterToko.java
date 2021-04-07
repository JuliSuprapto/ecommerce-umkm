package com.rian.ecommerce_v1.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rian.ecommerce_v1.pembeli.ListItemToko;
import com.rian.ecommerce_v1.R;
import com.rian.ecommerce_v1.models.ModelTokos;
import com.rian.ecommerce_v1.server.BaseURL;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterToko extends RecyclerView.Adapter<AdapterToko.ViewHolder> {

    private Context context;
    private List<ModelTokos> tokoList;

    public AdapterToko(Context context, List<ModelTokos> tokoList) {
        this.context = context;
        this.tokoList = tokoList;
    }

    @NonNull
    @Override
    public AdapterToko.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_toko, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ModelTokos modelTokos = tokoList.get(position);

        Picasso.get().load(BaseURL.baseUrl + modelTokos.getLogoToko()).resize(500, 400).centerCrop().into(holder.photoToko);
        holder.namaToko.setText(modelTokos.getNamaToko());
        holder.alamatToko.setText(modelTokos.getAlamat());
        holder.textDeskripsi = modelTokos.getDeskripsi();

        if (holder.textDeskripsi.length() > 30) {
            holder.textDeskripsi = holder.textDeskripsi.substring(0, 30) + "...";
            holder.deskripsiToko.setText(holder.textDeskripsi);
            System.out.println(holder.textDeskripsi);
        }

        holder.cardToko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ListItemToko.class).putExtra("idUser", modelTokos.getIdUser()).putExtra("namaToko", modelTokos.getNamaToko()).putExtra("alamatToko", modelTokos.getAlamat()).putExtra("deskripsiToko", modelTokos.getDeskripsi()).putExtra("photoToko", modelTokos.getLogoToko()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return tokoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView namaToko, deskripsiToko, alamatToko;
        ImageView photoToko;
        CardView cardToko;
        String textDeskripsi;

        public ViewHolder(View itemView) {
            super(itemView);

            cardToko = itemView.findViewById(R.id.card_toko);
            photoToko = itemView.findViewById(R.id.photo_toko);
            namaToko = itemView.findViewById(R.id.nama_toko);
            deskripsiToko = itemView.findViewById(R.id.deskripsi_toko);
            alamatToko = itemView.findViewById(R.id.alamat_toko);
        }
    }
}

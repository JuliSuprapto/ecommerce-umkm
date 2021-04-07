package com.rian.ecommerce_v1.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rian.ecommerce_v1.R;
import com.rian.ecommerce_v1.models.ModelKeranjang;
import com.rian.ecommerce_v1.pembeli.MainPembeli;
import com.rian.ecommerce_v1.server.BaseURL;
import com.rian.ecommerce_v1.utils.RupiahConvert;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdapterKeranjang extends RecyclerView.Adapter<AdapterKeranjang.ViewHolder> {

    private Context context;
    private List<ModelKeranjang> listKeranjang;
    private RequestQueue mRequestQueue;

    public AdapterKeranjang(Context context, List<ModelKeranjang> listKeranjang) {
        this.context = context;
        this.listKeranjang = listKeranjang;
    }

    @NonNull
    @Override
    public AdapterKeranjang.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_keranjang, null);
        mRequestQueue = Volley.newRequestQueue(context);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterKeranjang.ViewHolder holder, int position) {
        final ModelKeranjang keranjang = listKeranjang.get(position);

        holder.namaBarang.setText(keranjang.getNamaBarang());
        holder.namaToko.setText(keranjang.getNamaToko());
        holder.hargaBarang.setText(RupiahConvert.convertRupiah(keranjang.getHargaBarang()));
        holder.kuantitasBarang.setText(keranjang.getQty());
        Picasso.get().load(BaseURL.baseUrl + keranjang.getFotobarang()).resize(500, 400).centerCrop().into(holder.photoKeranjang);

        holder.hargaTotal.setText(RupiahConvert.convertRupiah(keranjang.getTotalHarga()));

        holder._idKeranjang = keranjang.get_id();

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("INFORMASI");
                builder.setMessage("Apakah anda yakin akan menghapus data ini ?");
                builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteDataKeranjang(holder._idKeranjang);
                    }
                });
                builder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listKeranjang.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photoKeranjang;
        TextView namaBarang, namaToko, hargaBarang, kuantitasBarang, hargaTotal;
        LinearLayout btnDelete;
        String _idKeranjang;

        public ViewHolder(View itemView) {
            super(itemView);

            photoKeranjang = (ImageView) itemView.findViewById(R.id.photo_barang_keranjang);

            namaBarang = (TextView) itemView.findViewById(R.id.nama_barang_keranjang);
            namaToko = (TextView) itemView.findViewById(R.id.nama_toko_keranjang);
            kuantitasBarang = (TextView) itemView.findViewById(R.id.kuantitas_pembeli_keranjang);
            hargaBarang = (TextView) itemView.findViewById(R.id.harga_barang_keranjang);

            hargaTotal = (TextView) itemView.findViewById(R.id.total_harga_keranjang);

            btnDelete = (LinearLayout) itemView.findViewById(R.id.delete_item);
        }
    }

    private void deleteDataKeranjang(String idKeranjang) {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.DELETE, BaseURL.deleteDataPesanan + idKeranjang, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        try {
                            boolean statusMsg = response.getBoolean("status");
                            if (statusMsg == true) {
                                StyleableToast.makeText(context, "Data berhasil dihapus", R.style.toastStyleSuccess).show();
                                context.startActivity(new Intent(context, MainPembeli.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        mRequestQueue.add(req);
    }
}

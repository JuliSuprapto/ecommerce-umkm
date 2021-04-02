package com.example.ecommerce_v1.adapters;

import android.content.Context;
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
import com.example.ecommerce_v1.R;
import com.example.ecommerce_v1.models.ModelKeranjang;
import com.example.ecommerce_v1.pembeli.MainPembeli;
import com.example.ecommerce_v1.server.BaseURL;
import com.example.ecommerce_v1.utils.RupiahConvert;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class AdapterPengirimanSampai extends RecyclerView.Adapter<AdapterPengirimanSampai.ViewHolder> {
    private Context context;
    private List<ModelKeranjang> listPengiriminan;
    private RequestQueue mRequestQueue;

    public AdapterPengirimanSampai(Context context, List<ModelKeranjang> listPengiriminan) {
        this.context = context;
        this.listPengiriminan = listPengiriminan;
    }

    @NonNull
    @Override
    public AdapterPengirimanSampai.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_sampai, null);
        mRequestQueue = Volley.newRequestQueue(context);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterPengirimanSampai.ViewHolder holder, int position) {
        final ModelKeranjang keranjang = listPengiriminan.get(position);

        holder.namaBarang.setText(keranjang.getNamaBarang());
        holder.namaToko.setText(keranjang.getNamaToko());
        holder.hargaBarang.setText(RupiahConvert.convertRupiah(keranjang.getHargaBarang()));
        holder.kuantitasBarang.setText(keranjang.getQty());
        Picasso.get().load(BaseURL.baseUrl + keranjang.getFotobarang()).resize(500, 400).centerCrop().into(holder.photoKeranjang);

        holder.hargaTotal.setText(RupiahConvert.convertRupiah(keranjang.getTotalHarga()));

        holder._idKeranjang = keranjang.get_id();
        holder._idPembeli = keranjang.getIdPembeli();

        holder.btnSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataPengirimanFunction(holder._idPembeli);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPengiriminan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photoKeranjang;
        TextView namaBarang, namaToko, hargaBarang, kuantitasBarang, hargaTotal;
        LinearLayout btnSelesai;
        String _idKeranjang, _idPembeli;

        public ViewHolder(View itemView) {
            super(itemView);

            photoKeranjang = (ImageView) itemView.findViewById(R.id.photo_barang_keranjang);

            namaBarang = (TextView) itemView.findViewById(R.id.nama_barang_keranjang);
            namaToko = (TextView) itemView.findViewById(R.id.nama_toko_keranjang);
            kuantitasBarang = (TextView) itemView.findViewById(R.id.kuantitas_pembeli_keranjang);
            hargaBarang = (TextView) itemView.findViewById(R.id.harga_barang_keranjang);

            hargaTotal = (TextView) itemView.findViewById(R.id.total_harga_keranjang);

            btnSelesai = (LinearLayout) itemView.findViewById(R.id.selesai_item);
        }
    }

    private void updateDataPengirimanFunction(String idPembeli) {
        System.out.println("ID KERANJANG = " + idPembeli);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", "5");

        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, BaseURL.getDataPesanan + idPembeli, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            System.out.println("res = " + jsonObject.toString());
                            String strMsg = jsonObject.getString("message");
                            boolean status = jsonObject.getBoolean("status");
                            if (status == true) {
                                StyleableToast.makeText(context, "Data berhasil dikonfirmasi", R.style.toastStyleSuccess).show();
                                context.startActivity(new Intent(context, MainPembeli.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                    }
                });
        mRequestQueue.add(req);
    }
}

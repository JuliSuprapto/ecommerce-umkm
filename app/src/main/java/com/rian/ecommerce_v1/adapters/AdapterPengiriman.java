package com.rian.ecommerce_v1.adapters;

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
import com.rian.ecommerce_v1.R;
import com.rian.ecommerce_v1.models.ModelKeranjang;
import com.rian.ecommerce_v1.models.ModelTokos;
import com.rian.ecommerce_v1.models.ModelUsers;
import com.rian.ecommerce_v1.pedagang.MainPedagang;
import com.rian.ecommerce_v1.server.BaseURL;
import com.rian.ecommerce_v1.utils.RupiahConvert;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdapterPengiriman extends RecyclerView.Adapter<AdapterPengiriman.ViewHolder> {
    private Context context;
    private List<ModelKeranjang> listPengiriminan;
    private RequestQueue mRequestQueue;
    TextView namaPembeli, phonePembeli, addressPembeli;

    public AdapterPengiriman(Context context, List<ModelKeranjang> listPengiriminan) {
        this.context = context;
        this.listPengiriminan = listPengiriminan;
    }

    @NonNull
    @Override
    public AdapterPengiriman.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_kirim, null);
        mRequestQueue = Volley.newRequestQueue(context);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterPengiriman.ViewHolder holder, int position) {
        final ModelKeranjang keranjang = listPengiriminan.get(position);

        holder.namaBarang.setText(keranjang.getNamaBarang());
        holder.namaToko.setText(keranjang.getNamaToko());
        holder.hargaBarang.setText(RupiahConvert.convertRupiah(keranjang.getHargaBarang()));
        holder.kuantitasBarang.setText(keranjang.getQty());
        Picasso.get().load(BaseURL.baseUrl + keranjang.getFotobarang()).resize(500, 400).centerCrop().into(holder.photoKeranjang);

        holder.hargaTotal.setText(RupiahConvert.convertRupiah(keranjang.getTotalHarga()));

        holder._idKeranjang = keranjang.get_id();
        holder._idToko = keranjang.getIdToko();
        holder._idPembeli = keranjang.getIdPembeli();

        holder.btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataPengirimanFunction(holder._idToko);
            }
        });

        getDataPembeli(holder._idPembeli);
    }

    @Override
    public int getItemCount() {
        return listPengiriminan.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photoKeranjang;
        TextView namaBarang, namaToko, hargaBarang, kuantitasBarang, hargaTotal;
        LinearLayout btnKirim;
        String _idKeranjang, _idToko, _idPembeli;

        public ViewHolder(View itemView) {
            super(itemView);

            photoKeranjang = (ImageView) itemView.findViewById(R.id.photo_barang_keranjang);

            namaBarang = (TextView) itemView.findViewById(R.id.nama_barang_keranjang);
            namaToko = (TextView) itemView.findViewById(R.id.nama_toko_keranjang);
            kuantitasBarang = (TextView) itemView.findViewById(R.id.kuantitas_pembeli_keranjang);
            hargaBarang = (TextView) itemView.findViewById(R.id.harga_barang_keranjang);
            namaPembeli = (TextView) itemView.findViewById(R.id.nama_pembeli_barang);
            phonePembeli = (TextView) itemView.findViewById(R.id.phone_pembeli_barang);
            addressPembeli = (TextView) itemView.findViewById(R.id.address_pembeli_barang);

            hargaTotal = (TextView) itemView.findViewById(R.id.total_harga_keranjang);

            btnKirim = (LinearLayout) itemView.findViewById(R.id.kirim_item);
        }
    }

    private void updateDataPengirimanFunction(String idToko) {
        System.out.println("ID KERANJANG = " + idToko);

        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, BaseURL.getDataPengiriman + idToko, null,
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
                                StyleableToast.makeText(context, "Data berhasil dikirim", R.style.toastStyleSuccess).show();
                                context.startActivity(new Intent(context, MainPedagang.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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

    private void getDataPembeli(String idPembeli) {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.getDataUser + idPembeli, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        try {
                            boolean statusMsg = response.getBoolean("status");
                            if (statusMsg == true) {
                                String toko = response.getString("result");
                                JSONArray jsonArray = new JSONArray(toko);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    final ModelUsers users = new ModelUsers();
                                    final String _id = jsonObject.getString("idUser");
                                    final String namaPembeliBarang = jsonObject.getString("nama");
                                    final String phonePembeliBarang = jsonObject.getString("phone");
                                    final String addressPembeliBarang = jsonObject.getString("alamat");
                                    namaPembeli.setText(namaPembeliBarang);
                                    phonePembeli.setText(phonePembeliBarang);
                                    addressPembeli.setText(addressPembeliBarang);
                                }
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

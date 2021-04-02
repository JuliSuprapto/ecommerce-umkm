package com.example.ecommerce_v1.adapters;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.ecommerce_v1.R;
import com.example.ecommerce_v1.models.ModelBarangs;
import com.example.ecommerce_v1.server.BaseURL;
import com.example.ecommerce_v1.utils.RupiahConvert;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class AdapterItemToko extends RecyclerView.Adapter<AdapterItemToko.ViewHolder> {

    private Context context;
    private List<ModelBarangs> barangList;

    private RequestQueue mRequestQueue;

    public String kuantitas, idBarang, idPedagang, idPembeli, dataTotalharga;
    int dataKuantitas, dataHarga;

    Dialog myDialog;
    ElegantNumberButton kuantitasBarang;

    public AdapterItemToko(Context context, List<ModelBarangs> barangList){
        this.context = context;
        this.barangList = barangList;
    }

    @NonNull
    @Override
    public AdapterItemToko.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_barang_toko, null);
        mRequestQueue = Volley.newRequestQueue(context);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterItemToko.ViewHolder holder, final int position) {
        final ModelBarangs itemBarang = barangList.get(position);

        Picasso.get().load(BaseURL.baseUrl +  itemBarang.getFotobarang()).into(holder.photoBarang);
        holder.namaBarang.setText(itemBarang.getNamaBarang());
        holder.deskripsiBarang.setText(itemBarang.getDeksripsiBarang());
        holder.hargaBarang.setText(RupiahConvert.convertRupiah(itemBarang.getHargBarang()));
        holder.stokBarang.setText(itemBarang.getStokBarang());

        holder._idBarang = itemBarang.get_id();
        holder._idPedagang = itemBarang.getIdUser();
        holder._idPembeli = itemBarang.getIdPembeli();

        myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.add_item_checkout);

        final Button btnPesanBarang = (Button) myDialog.findViewById(R.id.btn_pesan);
        kuantitasBarang = (ElegantNumberButton) myDialog.findViewById(R.id.btn_kuantitas);

        int stokForRange = Integer.parseInt(holder.stokBarangCheckout = (itemBarang.getStokBarang()));

        kuantitasBarang.setRange(0, stokForRange);
        kuantitasBarang.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                kuantitas = kuantitasBarang.getNumber();
                dataKuantitas = Integer.parseInt(kuantitas);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView photoBarangCheckout = (ImageView) myDialog.findViewById(R.id.photo_barang_checkout);

                TextView namaBarangCheckout = (TextView) myDialog.findViewById(R.id.nama_barang_checkout);
                TextView stokBarangCheckout = (TextView) myDialog.findViewById(R.id.stok_barang_checkout);
                TextView hargaBarangCheckout = (TextView) myDialog.findViewById(R.id.harga_barang_checkout);
                TextView deskripsiBarangCheckout = (TextView) myDialog.findViewById(R.id.deskripsi_barang_checkout);

                idBarang = barangList.get(holder.getAdapterPosition()).get_id();
                idPedagang = barangList.get(holder.getAdapterPosition()).getIdUser();
                idPembeli = barangList.get(holder.getAdapterPosition()).getIdPembeli();

                namaBarangCheckout.setText(barangList.get(holder.getAdapterPosition()).getNamaBarang());
                stokBarangCheckout.setText("STOK - " + barangList.get(holder.getAdapterPosition()).getStokBarang());
                hargaBarangCheckout.setText(RupiahConvert.convertRupiah(barangList.get(holder.getAdapterPosition()).getHargBarang()));
                deskripsiBarangCheckout.setText(barangList.get(holder.getAdapterPosition()).getDeksripsiBarang());
                Picasso.get().load(BaseURL.baseUrl +  barangList.get(holder.getAdapterPosition()).getFotobarang()).into(photoBarangCheckout);

                dataHarga = Integer.parseInt(barangList.get(holder.getAdapterPosition()).getHargBarang());

                myDialog.show();
                myDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        });

        btnPesanBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int totalharga = dataKuantitas * dataHarga;
                dataTotalharga = Integer.toString(totalharga);
                inputPesananFunction(idPedagang, idBarang, kuantitas, dataTotalharga, idPembeli);
            }
        });
    }

    @Override
    public int getItemCount() {
        return barangList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView namaBarang, deskripsiBarang, hargaBarang, stokBarang;
        ImageView photoBarang;
        String _idBarang, stokBarangCheckout, _idPedagang, _idPembeli;

        public ViewHolder(View itemView){
            super(itemView);

            namaBarang = itemView.findViewById(R.id.nama_barang);
            deskripsiBarang = itemView.findViewById(R.id.deskripsi_barang);
            hargaBarang = itemView.findViewById(R.id.harga_barang);
            stokBarang = itemView.findViewById(R.id.stok_barang);
            photoBarang = itemView.findViewById(R.id.photo_barang);
        }
    }

    private void inputPesananFunction(String idPedagang, String idBarang, final String kuantitas, String dataTotalharga, String idPembeli) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("idToko", idPedagang);
        params.put("idPembeli", idPembeli);
        params.put("idBarang", idBarang);
        params.put("total", dataTotalharga);
        params.put("qty", kuantitas);

        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, BaseURL.addPesanan, new JSONObject(params),
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
                                myDialog.dismiss();
                                kuantitasBarang.setNumber("0");
                                StyleableToast.makeText(context, kuantitas + " Barang di tambahkan ke dalam keranjang", R.style.toastStyleSuccess).show();
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

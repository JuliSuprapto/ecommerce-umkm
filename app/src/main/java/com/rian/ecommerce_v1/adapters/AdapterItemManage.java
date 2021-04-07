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
import com.rian.ecommerce_v1.pedagang.UpdateItemToko;
import com.rian.ecommerce_v1.models.ModelBarangs;
import com.rian.ecommerce_v1.pedagang.MainPedagang;
import com.rian.ecommerce_v1.server.BaseURL;
import com.rian.ecommerce_v1.utils.RupiahConvert;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdapterItemManage extends RecyclerView.Adapter<AdapterItemManage.ViewHolder> {

    private Context context;
    private List<ModelBarangs> barangList;
    private RequestQueue mRequestQueue;

    public AdapterItemManage(Context context, List<ModelBarangs> barangList){
        this.context = context;
        this.barangList = barangList;
    }

    @NonNull
    @Override
    public AdapterItemManage.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_manage, null);
        mRequestQueue = Volley.newRequestQueue(context);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterItemManage.ViewHolder holder, int position) {
        final ModelBarangs itemBarang = barangList.get(position);

        Picasso.get().load(BaseURL.baseUrl +  itemBarang.getFotobarang()).into(holder.photoBarang);
        holder.namaBarang.setText(itemBarang.getNamaBarang());
        holder.deskripsiBarang.setText(itemBarang.getDeksripsiBarang());
        holder.hargaBarang.setText(RupiahConvert.convertRupiah(itemBarang.getHargBarang()));
        holder.stokBarang.setText("Stok - " + itemBarang.getStokBarang());

        holder._idBarang = itemBarang.get_id();

        holder.linearUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UpdateItemToko.class).putExtra("_id", itemBarang.get_id()).putExtra("nama", itemBarang.getNamaBarang()).putExtra("deskripsi", itemBarang.getDeksripsiBarang()).putExtra("harga", itemBarang.getHargBarang()).putExtra("stok", itemBarang.getStokBarang()));
            }
        });

        holder.linearDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("INFORMASI");
                builder.setMessage("Apakah anda yakin akan menghapus data ini ?");
                System.out.println("ID " + holder._idBarang);
                builder.setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItemFunction(holder._idBarang);
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
        return barangList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView namaBarang, deskripsiBarang, hargaBarang, stokBarang;
        ImageView photoBarang;
        LinearLayout linearUpdate, linearDelete;
        String _idBarang;

        public ViewHolder(View itemView){
            super(itemView);

            namaBarang = itemView.findViewById(R.id.nama_barang);
            deskripsiBarang = itemView.findViewById(R.id.deskripsi_barang);
            hargaBarang = itemView.findViewById(R.id.harga_barang);
            stokBarang = itemView.findViewById(R.id.stok_barang);
            photoBarang = itemView.findViewById(R.id.photo_barang);

            linearUpdate = itemView.findViewById(R.id.update_item);
            linearDelete = itemView.findViewById(R.id.delete_item);
        }
    }

    private void deleteItemFunction(final String _idBarang) {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.DELETE, BaseURL.deleteBarang + _idBarang, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        try {
                            boolean statusMsg = response.getBoolean("status");
                            if (statusMsg == true) {
                                context.startActivity(new Intent(context, MainPedagang.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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

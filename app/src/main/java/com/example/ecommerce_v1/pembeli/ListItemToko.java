package com.example.ecommerce_v1.pembeli;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.ecommerce_v1.R;
import com.example.ecommerce_v1.adapters.AdapterItemToko;
import com.example.ecommerce_v1.models.ModelBarangs;
import com.example.ecommerce_v1.models.ModelUsers;
import com.example.ecommerce_v1.server.BaseURL;
import com.example.ecommerce_v1.utils.App;
import com.example.ecommerce_v1.utils.GsonHelper;
import com.example.ecommerce_v1.utils.Prefs;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListItemToko extends AppCompatActivity {

    RecyclerView recycleItemToko;
    RecyclerView.Adapter recycleViewAdapterItemToko;
    List<ModelBarangs> listItemToko;
    LinearLayout noItemData, availableItemData;
    ModelUsers modelUsers;
    ImageView photoToko;
    TextView namaTokoData, alamatTokoData, deskripsiTokoData;
    Toolbar toolbar;

    String _idPedagang, _idPembeli, namaTokoList, alamatTokoList, deskripsiTokoList, photoTokoList;

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item_toko);

        mRequestQueue = Volley.newRequestQueue(this);
        modelUsers = (ModelUsers) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUsers());

        namaTokoData = (TextView) findViewById(R.id.nama_toko);
        alamatTokoData = (TextView) findViewById(R.id.alamat_toko);
        deskripsiTokoData = (TextView) findViewById(R.id.deskripsi_toko);

        photoToko = (ImageView) findViewById(R.id.photo_toko);

        toolbar = (Toolbar) findViewById(R.id.toolbar_pembeli);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ListItemToko.this, MainPembeli.class));
                Animatoo.animateSlideUp(ListItemToko.this);
            }
        });

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            _idPedagang = extra.getString("idUser");
            namaTokoList = extra.getString("namaToko");
            alamatTokoList = extra.getString("alamatToko");
            deskripsiTokoList = extra.getString("deskripsiToko");
            photoTokoList = extra.getString("photoToko");

            namaTokoData.setText(namaTokoList);
            alamatTokoData.setText(alamatTokoList);
            deskripsiTokoData.setText(deskripsiTokoList);

            String photoTokoData = photoTokoList;

            if (photoTokoData == null) {
                photoToko.setImageResource(R.drawable.ic_online_shopping);
            } else {
                Picasso.get().load(BaseURL.baseUrl + photoTokoData).into(photoToko);
            }

            getSupportActionBar().setTitle(namaTokoList);

            checkDataBarang(_idPedagang);
        }

        _idPembeli = modelUsers.get_id();

        noItemData = (LinearLayout) findViewById(R.id.no_item);
        availableItemData = (LinearLayout) findViewById(R.id.available_item);
        recycleItemToko = (RecyclerView) findViewById(R.id.recycle_item_toko);
        recycleItemToko.setHasFixedSize(true);
        recycleItemToko.setLayoutManager(new GridLayoutManager(this, 2));
        listItemToko = new ArrayList<>();
        recycleViewAdapterItemToko = new AdapterItemToko(this, listItemToko);
    }

    private void checkDataBarang(final String _idPedagang) {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.getDataBarang + _idPedagang, null,
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
                                    final ModelBarangs barang = new ModelBarangs();
                                    final String _id = jsonObject.getString("idUser");
                                    final String _idbarang = jsonObject.getString("_id");
                                    final String namaBarang = jsonObject.getString("namaBarang");
                                    final String deskripsibarang = jsonObject.getString("deskripsiBarang");
                                    final String hargaBarang = jsonObject.getString("hargaBarang");
                                    final String stokBarang = jsonObject.getString("stokBarang");
                                    final String fotoBarang = jsonObject.getString("fotoBarang");
                                    barang.setIdPembeli(_idPembeli);
                                    barang.setNamaBarang(namaBarang);
                                    barang.setDeksripsiBarang(deskripsibarang);
                                    barang.setHargBarang(hargaBarang);
                                    barang.setStokBarang(stokBarang);
                                    barang.setIdUser(_id);
                                    barang.set_id(_idbarang);
                                    barang.setFotobarang(fotoBarang);
                                    listItemToko.add(barang);
                                    recycleItemToko.setAdapter(recycleViewAdapterItemToko);
                                }
                                noItemData.setVisibility(View.GONE);
                                availableItemData.setVisibility(View.VISIBLE);
                            } else {
                                noItemData.setVisibility(View.VISIBLE);
                                availableItemData.setVisibility(View.GONE);
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

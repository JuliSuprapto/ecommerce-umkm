package com.example.ecommerce_v1.pembeli;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecommerce_v1.R;
import com.example.ecommerce_v1.adapters.AdapterPengirimanSampai;
import com.example.ecommerce_v1.models.ModelKeranjang;
import com.example.ecommerce_v1.models.ModelUsers;
import com.example.ecommerce_v1.server.BaseURL;
import com.example.ecommerce_v1.utils.App;
import com.example.ecommerce_v1.utils.GsonHelper;
import com.example.ecommerce_v1.utils.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PengirimanSelesaiFragment extends Fragment {

    RecyclerView recyclerViewSampai;
    RecyclerView.Adapter recycleViewAdapterSampai;
    LinearLayout noItemData, availableData;

    ModelUsers modelUsers;
    List<ModelKeranjang> listSampai;

    String _idPembeli, idPesanan;
    private RequestQueue mRequestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pengiriman_selesai, container, false);

        mRequestQueue = Volley.newRequestQueue(getActivity());
        noItemData = (LinearLayout) v.findViewById(R.id.no_item_selesai);
        availableData = (LinearLayout) v.findViewById(R.id.available_item_selesai);

        recyclerViewSampai = (RecyclerView) v.findViewById(R.id.item_keranjang_selesai);
        recyclerViewSampai.setHasFixedSize(true);
        recyclerViewSampai.setLayoutManager(new LinearLayoutManager(getActivity()));

        listSampai = new ArrayList<>();
        recycleViewAdapterSampai = new AdapterPengirimanSampai(getActivity(), listSampai);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Konfirmasi Pengiriman");

        modelUsers = (ModelUsers) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelUsers()
        );

        _idPembeli = modelUsers.get_id();

        checkDataKeranjangKirimFunction(_idPembeli);

        return v;
    }

    private void checkDataKeranjangKirimFunction(String idPembeli) {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.getDataPesanan + idPembeli, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        try {
                            boolean statusMsg = response.getBoolean("status");
                            if (statusMsg == true) {
                                String pesanan = response.getString("result");
                                JSONArray jsonArray = new JSONArray(pesanan);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    final ModelKeranjang keranjangD = new ModelKeranjang();
                                    final String namaTokos = jsonObject.getJSONObject("toko").getString("namaToko");
                                    final String namaPesanan = jsonObject.getJSONObject("items").getString("namaBarang");
                                    final String fotoBarang = jsonObject.getJSONObject("items").getString("fotoBarang");
                                    final String hargaBarang = jsonObject.getJSONObject("items").getString("hargaBarang");
                                    final String deskripsiBarang = jsonObject.getJSONObject("items").getString("deskripsiBarang");
                                    final String totalHarga = jsonObject.getString("total");
                                    final String _idPesanan = jsonObject.getString("_id");
                                    final String qty = jsonObject.getString("qty");
                                    final String _idToko = jsonObject.getString("idToko");
                                    final String _idPembeli = jsonObject.getString("idPembeli");
                                    final String status = jsonObject.getString("status");
                                    if (status.equals("4")) {
                                        noItemData.setVisibility(View.GONE);
                                        availableData.setVisibility(View.VISIBLE);
                                        keranjangD.setNamaToko(namaTokos);
                                        keranjangD.setDeksripsiBarang(deskripsiBarang);
                                        keranjangD.setNamaBarang(namaPesanan);
                                        keranjangD.setFotobarang(fotoBarang);
                                        keranjangD.set_id(_idPesanan);
                                        keranjangD.setQty(qty);
                                        keranjangD.setIdPembeli(_idPembeli);
                                        keranjangD.setHargaBarang(hargaBarang);
                                        keranjangD.setTotalHarga(totalHarga);
                                        keranjangD.setIdToko(_idToko);

                                        idPesanan = _idPesanan;

                                        listSampai.add(keranjangD);
                                        recyclerViewSampai.setAdapter(recycleViewAdapterSampai);
                                    }else {
                                        noItemData.setVisibility(View.VISIBLE);
                                        availableData.setVisibility(View.GONE);
                                    }
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

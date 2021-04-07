package com.rian.ecommerce_v1.pedagang;

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
import com.rian.ecommerce_v1.R;
import com.rian.ecommerce_v1.adapters.AdapterPengiriman;
import com.rian.ecommerce_v1.models.ModelKeranjang;
import com.rian.ecommerce_v1.models.ModelUsers;
import com.rian.ecommerce_v1.server.BaseURL;
import com.rian.ecommerce_v1.utils.App;
import com.rian.ecommerce_v1.utils.GsonHelper;
import com.rian.ecommerce_v1.utils.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SendItemFragment extends Fragment {

    RecyclerView recyclerViewKirim;
    RecyclerView.Adapter recycleViewAdapterKirim;
    LinearLayout noItemData, availableData;

    ModelUsers modelUsers;
    List<ModelKeranjang> listKirim;

    String _idPedagang, idPesanan;
    private RequestQueue mRequestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_item, container, false);

        mRequestQueue = Volley.newRequestQueue(getActivity());
        noItemData = (LinearLayout) v.findViewById(R.id.no_item_kirim);
        availableData = (LinearLayout) v.findViewById(R.id.available_item_kirim);

        recyclerViewKirim = (RecyclerView) v.findViewById(R.id.item_keranjang_kirim);
        recyclerViewKirim.setHasFixedSize(true);
        recyclerViewKirim.setLayoutManager(new LinearLayoutManager(getActivity()));

        listKirim = new ArrayList<>();
        recycleViewAdapterKirim = new AdapterPengiriman(getActivity(), listKirim);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Pengiriman Barang");

        modelUsers = (ModelUsers) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelUsers()
        );

        _idPedagang = modelUsers.get_id();

        checkDataKeranjangKirimFunction(_idPedagang);

        return v;
    }

    private void checkDataKeranjangKirimFunction(String _idPedagang) {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.getDataPengiriman + _idPedagang, null,
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
                                    if (status.equals("3")) {
                                        noItemData.setVisibility(View.GONE);
                                        availableData.setVisibility(View.VISIBLE);
                                        keranjangD.setNamaToko(namaTokos);
                                        keranjangD.setDeksripsiBarang(deskripsiBarang);
                                        keranjangD.setNamaBarang(namaPesanan);
                                        keranjangD.setFotobarang(fotoBarang);
                                        keranjangD.set_id(_idPesanan);
                                        keranjangD.setQty(qty);
                                        keranjangD.setHargaBarang(hargaBarang);
                                        keranjangD.setTotalHarga(totalHarga);
                                        keranjangD.setIdToko(_idToko);
                                        keranjangD.setIdPembeli(_idPembeli);

                                        idPesanan = _idPesanan;

                                        listKirim.add(keranjangD);
                                        recyclerViewKirim.setAdapter(recycleViewAdapterKirim);
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

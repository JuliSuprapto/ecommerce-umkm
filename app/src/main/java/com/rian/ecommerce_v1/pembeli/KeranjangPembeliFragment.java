package com.rian.ecommerce_v1.pembeli;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.rian.ecommerce_v1.adapters.AdapterKeranjang;
import com.rian.ecommerce_v1.R;
import com.rian.ecommerce_v1.models.ModelKeranjang;
import com.rian.ecommerce_v1.models.ModelUsers;
import com.rian.ecommerce_v1.server.BaseURL;
import com.rian.ecommerce_v1.utils.App;
import com.rian.ecommerce_v1.utils.GsonHelper;
import com.rian.ecommerce_v1.utils.Prefs;
import com.rian.ecommerce_v1.utils.RupiahConvert;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class KeranjangPembeliFragment extends Fragment {

    RecyclerView recyclerViewKeranjang;
    RecyclerView.Adapter recycleViewAdapterKeranjang;
    TextView dates, grandTotal, totalHargaBarang, biayaPengiriman;
    LinearLayout noItemData, availableData;
    Button clickItem;

    ModelUsers modelUsers;
    List<ModelKeranjang> listKeranjang;

    String _idPembeli, idPesanan;
    int biaya = 20000;
    int sum;
    private RequestQueue mRequestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_keranjang_pembeli, container, false);

        mRequestQueue = Volley.newRequestQueue(getActivity());

        noItemData = (LinearLayout) v.findViewById(R.id.no_item_keranjang);
        availableData = (LinearLayout) v.findViewById(R.id.available_item_keranjang);
        clickItem = (Button) v.findViewById(R.id.btn_checkout);

        recyclerViewKeranjang = (RecyclerView) v.findViewById(R.id.item_keranjang_pembeli);
        recyclerViewKeranjang.setHasFixedSize(true);
        recyclerViewKeranjang.setLayoutManager(new LinearLayoutManager(getActivity()));
        listKeranjang = new ArrayList<>();
        recycleViewAdapterKeranjang = new AdapterKeranjang(getActivity(), listKeranjang);

        modelUsers = (ModelUsers) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelUsers()
        );

        _idPembeli = modelUsers.get_id();

        totalHargaBarang = (TextView) v.findViewById(R.id.total_harga);
        biayaPengiriman = (TextView) v.findViewById(R.id.biaya_pengiriman);

        biayaPengiriman.setText("Rp. 20.000,00");

        dates = (TextView) v.findViewById(R.id.date);
        grandTotal = (TextView) v.findViewById(R.id.grand_total);
        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMMM yyyy", Locale.getDefault());
        String formatDate = dateFormat.format(d);

        dates.setText(formatDate);

        clickItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkoutPesanan(_idPembeli);
            }
        });

        checkDataKeranjangPembeliFunction(_idPembeli);

        return v;
    }

    private void checkDataKeranjangPembeliFunction(String _idPembeli) {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.getDataPesanan + _idPembeli, null,
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
                                    final String status = jsonObject.getString("status");
                                    if (status.equals("0")) {
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

                                        idPesanan = _idPesanan;

                                        int dataharga = Integer.parseInt(totalHarga);

                                        sum += dataharga;

                                        totalHargaBarang.setText(RupiahConvert.convertRupiah(Integer.toString(sum)));

                                        int grandTotalHarga = sum + biaya;
                                        grandTotal.setText(RupiahConvert.convertRupiah(Integer.toString(grandTotalHarga)));

                                        listKeranjang.add(keranjangD);
                                        recyclerViewKeranjang.setAdapter(recycleViewAdapterKeranjang);
                                    } else {
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

    private void checkoutPesanan(String _idPembeli) {
        System.out.println("DATA ID PESANAN = " + _idPembeli);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("key", "1");

        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, BaseURL.checkoutDataPesanan + _idPembeli, new JSONObject(params),
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
                                StyleableToast.makeText(getActivity(), "Data berhasil checkout", R.style.toastStyleSuccess).show();
                                startActivity(new Intent(getActivity(), MainPembeli.class));
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

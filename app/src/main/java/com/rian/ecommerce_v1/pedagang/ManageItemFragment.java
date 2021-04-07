package com.rian.ecommerce_v1.pedagang;

import android.app.ProgressDialog;
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
import com.rian.ecommerce_v1.adapters.AdapterItemManage;
import com.rian.ecommerce_v1.models.ModelBarangs;
import com.rian.ecommerce_v1.models.ModelUsers;
import com.rian.ecommerce_v1.server.BaseURL;
import com.rian.ecommerce_v1.utils.App;
import com.rian.ecommerce_v1.utils.GsonHelper;
import com.rian.ecommerce_v1.utils.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManageItemFragment extends Fragment {

    LinearLayout noItemData;
    RecyclerView recycleItemManage;

    ModelUsers modelUsers;

    ProgressDialog progressDialog;
    RecyclerView.Adapter recycleViewAdapterItemManage;
    List<ModelBarangs> listItemPedagang;

    String _idPedagang;

    private RequestQueue mRequestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_manage_item, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        mRequestQueue = Volley.newRequestQueue(getActivity());

        noItemData = (LinearLayout) v.findViewById(R.id.no_item);

        recycleItemManage = (RecyclerView) v.findViewById(R.id.recycle_item_manage);
        recycleItemManage.setHasFixedSize(true);
        recycleItemManage.setLayoutManager(new LinearLayoutManager(getActivity()));
        listItemPedagang = new ArrayList<>();
        recycleViewAdapterItemManage = new AdapterItemManage(getActivity(), listItemPedagang);

        modelUsers = (ModelUsers) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUsers());

        _idPedagang = modelUsers.get_id();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Kelola Barang");

        checkDataBarangFunction();

        return v;
    }

    private void checkDataBarangFunction() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("_id", _idPedagang);
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.checkBarang + _idPedagang, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        hideDialog();
                        try {
                            boolean statusMsg = response.getBoolean("status");
                            if (statusMsg == true) {
                                String toko = response.getString("result");
                                JSONArray jsonArray = new JSONArray(toko);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    final ModelBarangs itemBarang = new ModelBarangs();
                                    final String _idBarang = jsonObject.getString("_id");
                                    final String _id = jsonObject.getString("idUser");
                                    final String namaBarang = jsonObject.getString("namaBarang");
                                    final String deskripsiBarang = jsonObject.getString("deskripsiBarang");
                                    final String hargaBarang = jsonObject.getString("hargaBarang");
                                    final String stokBarang = jsonObject.getString("stokBarang");
                                    final String fotoBarang = jsonObject.getString("fotoBarang");
                                    itemBarang.setNamaBarang(namaBarang);
                                    itemBarang.setDeksripsiBarang(deskripsiBarang);
                                    itemBarang.setHargBarang(hargaBarang);
                                    itemBarang.setStokBarang(stokBarang);
                                    itemBarang.setIdUser(_id);
                                    itemBarang.set_id(_idBarang);
                                    itemBarang.setFotobarang(fotoBarang);
                                    listItemPedagang.add(itemBarang);
                                    recycleItemManage.setAdapter(recycleViewAdapterItemManage);
                                }
                                noItemData.setVisibility(View.GONE);
                                recycleItemManage.setVisibility(View.VISIBLE);
                            } else {
                                noItemData.setVisibility(View.VISIBLE);
                                recycleItemManage.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                hideDialog();
            }
        });
        mRequestQueue.add(req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
            progressDialog.setContentView(R.layout.layout_loading);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void hideDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.setContentView(R.layout.layout_loading);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}

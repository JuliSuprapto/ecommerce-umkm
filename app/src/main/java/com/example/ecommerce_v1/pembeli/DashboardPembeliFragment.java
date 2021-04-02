package com.example.ecommerce_v1.pembeli;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.ecommerce_v1.adapters.AdapterToko;
import com.example.ecommerce_v1.R;
import com.example.ecommerce_v1.adapters.AdapterSliderBanner;
import com.example.ecommerce_v1.models.ModelBaners;
import com.example.ecommerce_v1.models.ModelTokos;
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
import java.util.Timer;
import java.util.TimerTask;

public class DashboardPembeliFragment extends Fragment {

    RecyclerView recyclerToko;
    RecyclerView.Adapter recycleViewAdapterToko;
    List<ModelTokos> listToko;
    List<ModelBaners> listBaner;
    ModelUsers modelUsers;
    ViewPager viewPager;
    LinearLayout noItemData, availbaleItemData,indicatorDot;
    ImageView banners;
    AdapterSliderBanner sliderBanerAdapter;
    String _idPedagang;

    private int dotsCount;
    private ImageView[] dots;
    private RequestQueue mRequestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dashboard_pembeli, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Ecommerce UMKM");

        banners = v.findViewById(R.id.banner);
        viewPager = v.findViewById(R.id.viewPager);
        indicatorDot = v.findViewById(R.id.bannerDot);

        listBaner = new ArrayList<>();
        listBaner.add(new ModelBaners(R.drawable.banner_1));
        listBaner.add(new ModelBaners(R.drawable.banner_2));
        listBaner.add(new ModelBaners(R.drawable.banner_3));

        sliderBanerAdapter = new AdapterSliderBanner(listBaner, getActivity());
        dotsCount = sliderBanerAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.nonactive_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            indicatorDot.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.active_dot));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.nonactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 4000);

        viewPager.setAdapter(sliderBanerAdapter);
        viewPager.setPadding(55, 0, 55, 0);

        mRequestQueue = Volley.newRequestQueue(getActivity());
        noItemData = (LinearLayout) v.findViewById(R.id.no_item);
        availbaleItemData = (LinearLayout) v.findViewById(R.id.available_item);
        recyclerToko = (RecyclerView) v.findViewById(R.id.recycle_toko);
        recyclerToko.setHasFixedSize(true);
        recyclerToko.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        listToko = new ArrayList<>();
        recycleViewAdapterToko = new AdapterToko(getActivity(), listToko);

        modelUsers = (ModelUsers) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelUsers()
        );

        _idPedagang = modelUsers.get_id();

        checkDataToko();

        return v;
    }

    private void checkDataToko() {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.getDataToko, null,
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
                                    final ModelTokos tokoD = new ModelTokos();
                                    final String _id = jsonObject.getString("idUser");
                                    final String _idToko = jsonObject.getString("_id");
                                    final String namaToko = jsonObject.getString("namaToko");
                                    final String deskripsi = jsonObject.getString("deskripsi");
                                    final String alamat = jsonObject.getString("alamat");
                                    final String logoToko = jsonObject.getString("logoToko");
                                    tokoD.setNamaToko(namaToko);
                                    tokoD.setAlamat(alamat);
                                    tokoD.setDeskripsi(deskripsi);
                                    tokoD.setLogoToko(logoToko);
                                    tokoD.setIdUser(_id);
                                    tokoD.setIdToko(_idToko);
                                    listToko.add(tokoD);
                                    recyclerToko.setAdapter(recycleViewAdapterToko);
                                }
                                noItemData.setVisibility(View.GONE);
                                availbaleItemData.setVisibility(View.VISIBLE);
                            } else {
                                noItemData.setVisibility(View.VISIBLE);
                                availbaleItemData.setVisibility(View.GONE);
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

    public class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() == 0) {
                        viewPager.setCurrentItem(1);
                    } else if (viewPager.getCurrentItem() == 1) {
                        viewPager.setCurrentItem(2);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }
}

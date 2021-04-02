package com.example.ecommerce_v1.pedagang;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.ecommerce_v1.R;
import com.example.ecommerce_v1.adapters.AdapterItemPedagang;
import com.example.ecommerce_v1.models.ModelBarangs;
import com.example.ecommerce_v1.models.ModelUsers;
import com.example.ecommerce_v1.server.BaseURL;
import com.example.ecommerce_v1.server.VolleyMultipart;
import com.example.ecommerce_v1.utils.App;
import com.example.ecommerce_v1.utils.GsonHelper;
import com.example.ecommerce_v1.utils.Prefs;
import com.example.ecommerce_v1.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokoFragment extends Fragment {

    TextView namaToko, deskripsiToko, alamatToko;
    LinearLayout panelRegistToko, takePhoto, photoResult, panelItemData, noItemData, itemData, addItem;
    TextInputEditText dataNamaToko, dataAlamatToko, dataDeksripsiToko;
    ImageView dataPhotoToko;
    Button btnRegistToko;
    RecyclerView recyclerItemPedagang;

    ModelUsers modelUsers;

    ProgressDialog progressDialog;
    RecyclerView.Adapter recycleViewAdapterItemPedagang;
    List<ModelBarangs> listItemPedagang;
    Bitmap bitmap;

    String mCurrentPhotoPath, _idPedagang;

    private final int CameraR_PP = 1;
    private RequestQueue mRequestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_toko, container, false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        mRequestQueue = Volley.newRequestQueue(getActivity());

        namaToko = (TextView) v.findViewById(R.id.nama_toko);
        deskripsiToko = (TextView) v.findViewById(R.id.deskripsi_toko);
        alamatToko = (TextView) v.findViewById(R.id.alamat_toko);

        dataPhotoToko = (ImageView) v.findViewById(R.id.photo_toko);
        btnRegistToko = (Button) v.findViewById(R.id.btn_regist_toko);

        dataNamaToko = (TextInputEditText) v.findViewById(R.id.namaToko);
        dataAlamatToko = (TextInputEditText) v.findViewById(R.id.alamatToko);
        dataDeksripsiToko = (TextInputEditText) v.findViewById(R.id.deksripsiTokoDaftar);

        dataNamaToko.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        dataAlamatToko.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        dataDeksripsiToko.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        panelItemData = (LinearLayout) v.findViewById(R.id.listItem);
        noItemData = (LinearLayout) v.findViewById(R.id.no_item);
        itemData = (LinearLayout) v.findViewById(R.id.item);
        panelRegistToko = (LinearLayout) v.findViewById(R.id.regist_toko);
        takePhoto = (LinearLayout) v.findViewById(R.id.take_photo);
        photoResult = (LinearLayout) v.findViewById(R.id.photo_result);
        addItem = (LinearLayout) v.findViewById(R.id.add_item);

        recyclerItemPedagang = (RecyclerView) v.findViewById(R.id.recycle_item_barang);
        recyclerItemPedagang.setHasFixedSize(true);
        recyclerItemPedagang.setLayoutManager(new LinearLayoutManager(getActivity()));
        listItemPedagang = new ArrayList<>();
        recycleViewAdapterItemPedagang = new AdapterItemPedagang(getActivity(), listItemPedagang);

        modelUsers = (ModelUsers) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUsers());

        _idPedagang = modelUsers.get_id();

        checkDataTokoFunction();

        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoFunction();
            }
        });

        btnRegistToko.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registTokoFunction(bitmap);
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddItemToko.class));
                Animatoo.animateSlideUp(getActivity());
            }
        });

        return v;
    }

    private void checkDataTokoFunction() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("idUser", _idPedagang);
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, BaseURL.checkToko + _idPedagang, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response.toString());
                            System.out.println("response = " + jObj);
                            String strMsg = jObj.getString("message");
                            boolean statusMsg = jObj.getBoolean("status");
                            if (statusMsg == true) {
                                JSONObject toko = jObj.getJSONObject("result");
                                String nama = toko.getString("namaToko");
                                String deskripsi = toko.getString("deskripsi");
                                String alamat = toko.getString("alamat");
                                _idPedagang = toko.getString("idUser");
                                checkDataBarang();
                                namaToko.setText(nama);
                                String tokoTitle = nama;
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tokoTitle);
                                deskripsiToko.setText(deskripsi);
                                alamatToko.setText(alamat);
                                panelRegistToko.setVisibility(View.GONE);
                                panelItemData.setVisibility(View.VISIBLE);
                            } else {
                                panelRegistToko.setVisibility(View.VISIBLE);
                                panelItemData.setVisibility(View.GONE);
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

    private void checkDataBarang() {
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
                                    itemBarang.setFotobarang(fotoBarang);
                                    listItemPedagang.add(itemBarang);
                                    recyclerItemPedagang.setAdapter(recycleViewAdapterItemPedagang);
                                }
                                noItemData.setVisibility(View.GONE);
                                itemData.setVisibility(View.VISIBLE);
                            } else {
                                noItemData.setVisibility(View.VISIBLE);
                                itemData.setVisibility(View.GONE);
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

    private void registTokoFunction(final Bitmap bitmap) {
        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();
        VolleyMultipart volleyMultipartRequest = new VolleyMultipart(Request.Method.POST, BaseURL.daftarToko,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        mRequestQueue.getCache().clear();
                        hideDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            System.out.println("res = " + jsonObject.toString());
                            String strMsg = jsonObject.getString("message");
                            boolean status = jsonObject.getBoolean("status");
                            if (status == true) {
                                JSONObject toko = jsonObject.getJSONObject("result");
                                Utils.storeProfileToko(toko.toString());
                                panelRegistToko.setVisibility(View.GONE);
                                panelItemData.setVisibility(View.VISIBLE);
                            } else {
                                panelRegistToko.setVisibility(View.VISIBLE);
                                panelItemData.setVisibility(View.GONE);
                                StyleableToast.makeText(getActivity().getApplicationContext(), strMsg, R.style.toastStyleWarning).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideDialog();
                        StyleableToast.makeText(getActivity(), error.getMessage(), R.style.toastStyleWarning).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idUser", _idPedagang);
                params.put("namaToko", dataNamaToko.getText().toString());
                params.put("alamat", dataAlamatToko.getText().toString());
                params.put("deskripsi", dataDeksripsiToko.getText().toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, VolleyMultipart.DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("logoToko", new VolleyMultipart.DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue = Volley.newRequestQueue(getActivity());
        mRequestQueue.add(volleyMultipartRequest);
    }

    private void takePhotoFunction() {
        addPermission();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.i("Tags", "IOException");
            }
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, CameraR_PP);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nameUser = modelUsers.getNama();
        String imageFileName = "JPEG_" + timeStamp + "_" + nameUser + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_CANCELED) {
            return;
        }

        if (requestCode == CameraR_PP) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.parse(mCurrentPhotoPath));
                dataPhotoToko.setImageBitmap(bitmap);
                if (dataPhotoToko.getDrawable() != null) {
                    dataPhotoToko.requestLayout();
                    dataPhotoToko.setScaleType(ImageView.ScaleType.FIT_XY);
                    ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) dataPhotoToko.getLayoutParams();
                    photoResult.setVisibility(View.VISIBLE);
                    takePhoto.setVisibility(View.GONE);
                }
            } catch (IOException e) {
                e.printStackTrace();
                StyleableToast.makeText(getActivity(), "Terjadi kesalahan...", R.style.toastStyleWarning).show();
            }
        }
    }

    public void addPermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getActivity(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
        }
        return byteArrayOutputStream.toByteArray();
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

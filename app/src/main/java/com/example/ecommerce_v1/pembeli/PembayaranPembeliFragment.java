package com.example.ecommerce_v1.pembeli;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.ecommerce_v1.models.ModelKeranjang;
import com.example.ecommerce_v1.models.ModelUsers;
import com.example.ecommerce_v1.pembeli.MainPembeli;
import com.example.ecommerce_v1.pembeli.UpdateProfilePembeli;
import com.example.ecommerce_v1.server.BaseURL;
import com.example.ecommerce_v1.server.VolleyMultipart;
import com.example.ecommerce_v1.utils.App;
import com.example.ecommerce_v1.utils.GsonHelper;
import com.example.ecommerce_v1.utils.Prefs;
import com.example.ecommerce_v1.utils.RupiahConvert;
import com.example.ecommerce_v1.utils.Utils;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PembayaranPembeliFragment extends Fragment {

    TextView dates, grandTotal;
    ModelUsers modelUsers;
    LinearLayout takePhoto, photoResult;
    ImageView dataPhotoPembeli;
    Button btnPembayaran;
    LinearLayout noItemData, availableItemData;

    ProgressDialog progressDialog;
    Bitmap bitmap;

    private final int CameraR_PP = 1;

    String _idPembeli, idPesanan, mCurrentPhotoPath;
    int sum;
    private RequestQueue mRequestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pembayaran_pembeli, container, false);

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        mRequestQueue = Volley.newRequestQueue(getActivity());

        dataPhotoPembeli = (ImageView) v.findViewById(R.id.photo_bukti);
        btnPembayaran = (Button) v.findViewById(R.id.btn_pembayaran);

        noItemData = (LinearLayout) v.findViewById(R.id.no_item_pembayaran);
        availableItemData = (LinearLayout) v.findViewById(R.id.available_item_pembayaran);

        takePhoto = (LinearLayout) v.findViewById(R.id.take_photo_bukti);
        photoResult = (LinearLayout) v.findViewById(R.id.photo_result_bukti);

        modelUsers = (ModelUsers) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelUsers()
        );

        _idPembeli = modelUsers.get_id();

        dates = (TextView) v.findViewById(R.id.date);
        grandTotal = (TextView) v.findViewById(R.id.grand_total);
        Date d = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMMM yyyy", Locale.getDefault());
        String formatDate = dateFormat.format(d);

        dates.setText(formatDate);

        checkDataKeranjangPembeliFunction(_idPembeli);

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

        btnPembayaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pembayaranFunction(_idPembeli);
            }
        });

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
                                    if (status.equals("1")) {
                                        noItemData.setVisibility(View.GONE);
                                        availableItemData.setVisibility(View.VISIBLE);
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
                                        grandTotal.setText(RupiahConvert.convertRupiah(Integer.toString(sum)));
                                    }else {
                                        noItemData.setVisibility(View.VISIBLE);
                                        availableItemData.setVisibility(View.GONE);
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

    private void pembayaranFunction(final String _idPembeli) {
        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();
        VolleyMultipart volleyMultipartRequest = new VolleyMultipart(Request.Method.PUT, BaseURL.pembayaranPesanan,
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
                                startActivity(new Intent(getActivity(), MainPembeli.class));
                                Animatoo.animateSlideDown(getActivity());
                            } else {
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
                params.put("idPembeli", _idPembeli);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("foto", new VolleyMultipart.DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
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
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
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
                dataPhotoPembeli.setImageBitmap(bitmap);
                if (dataPhotoPembeli.getDrawable() != null) {
                    dataPhotoPembeli.requestLayout();
                    dataPhotoPembeli.setScaleType(ImageView.ScaleType.FIT_XY);
                    ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) dataPhotoPembeli.getLayoutParams();
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

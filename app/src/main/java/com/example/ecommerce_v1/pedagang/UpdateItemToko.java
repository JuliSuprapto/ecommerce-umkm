package com.example.ecommerce_v1.pedagang;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
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
import com.example.ecommerce_v1.models.ModelUsers;
import com.example.ecommerce_v1.server.BaseURL;
import com.example.ecommerce_v1.server.VolleyMultipart;
import com.example.ecommerce_v1.utils.App;
import com.example.ecommerce_v1.utils.GsonHelper;
import com.example.ecommerce_v1.utils.Prefs;
import com.google.android.material.textfield.TextInputEditText;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateItemToko extends AppCompatActivity {

    TextView namaToko, deskripsiToko, alamatToko;
    LinearLayout takePhoto, photoResult;
    ImageView dataPhotoToko;
    Button btnUpdateItem;
    TextInputEditText namaBarang, deskripsiBarang, hargaBarang, stokBarang;

    Toolbar toolbar;
    ModelUsers modelUsers;

    ProgressDialog progressDialog;
    Bitmap bitmap;

    String mCurrentPhotoPath, _idPedagang, _idBarang;

    private final int CameraR_PP = 1;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item_toko);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        mRequestQueue = Volley.newRequestQueue(this);

        namaToko = (TextView) findViewById(R.id.nama_toko);
        deskripsiToko = (TextView) findViewById(R.id.deskripsi_toko);
        alamatToko = (TextView) findViewById(R.id.alamat_toko);

        dataPhotoToko = (ImageView) findViewById(R.id.photo_item);
        btnUpdateItem = (Button) findViewById(R.id.btn_update_item);

        takePhoto = (LinearLayout) findViewById(R.id.take_photo);
        photoResult = (LinearLayout) findViewById(R.id.photo_result);

        namaBarang = (TextInputEditText) findViewById(R.id.namaBarang);
        deskripsiBarang = (TextInputEditText) findViewById(R.id.deskripsiBarang);
        hargaBarang = (TextInputEditText) findViewById(R.id.hargaBarang);
        stokBarang = (TextInputEditText) findViewById(R.id.stokBarang);

        namaBarang.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        deskripsiBarang.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        toolbar = (Toolbar) findViewById(R.id.toolbar_pedagang);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Perbaharui Barang");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateItemToko.this, MainPedagang.class));
                Animatoo.animateSlideUp(UpdateItemToko.this);
            }
        });

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

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            String dataIdBarang = extra.getString("_id");
            String dataNamaBarang = extra.getString("nama");
            String dataDeskripsi = extra.getString("deskripsi");
            String dataHargaBarang = extra.getString("harga");
            String dataStokBarang = extra.getString("stok");

            _idBarang = dataIdBarang;
            namaBarang.setText(dataNamaBarang);
            deskripsiBarang.setText(dataDeskripsi);
            hargaBarang.setText(dataHargaBarang);
            stokBarang.setText(dataStokBarang);
            btnUpdateItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateItemTokoFunction(bitmap);
                }
            });
        }
    }

    private void updateItemTokoFunction(final Bitmap bitmap) {
        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();
        VolleyMultipart volleyMultipartRequest = new VolleyMultipart(Request.Method.PUT, BaseURL.updateBarang + _idBarang,
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
                                startActivity(new Intent(UpdateItemToko.this, MainPedagang.class));
                                Animatoo.animateSlideDown(UpdateItemToko.this);
                            } else {
                                StyleableToast.makeText(getApplicationContext(), strMsg, R.style.toastStyleWarning).show();
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
                        StyleableToast.makeText(UpdateItemToko.this, error.getMessage(), R.style.toastStyleWarning).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("namaBarang", namaBarang.getText().toString());
                params.put("deskripsiBarang", deskripsiBarang.getText().toString());
                params.put("stokBarang", stokBarang.getText().toString());
                params.put("hargaBarang", hargaBarang.getText().toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("fotoBarang", new VolleyMultipart.DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue = Volley.newRequestQueue(UpdateItemToko.this);
        mRequestQueue.add(volleyMultipartRequest);
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
                                namaToko.setText(nama);
                                deskripsiToko.setText(deskripsi);
                                alamatToko.setText(alamat);
                            } else {
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

    private void takePhotoFunction() {
        addPermission();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(this.getPackageManager()) != null) {
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

        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        if (requestCode == CameraR_PP) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
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
                StyleableToast.makeText(this, "Terjadi kesalahan...", R.style.toastStyleWarning).show();
            }
        }
    }

    public void addPermission() {
        Dexter.withActivity(this)
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
                        Toast.makeText(UpdateItemToko.this, "Some Error! ", Toast.LENGTH_SHORT).show();
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

package com.rian.ecommerce_v1.pedagang;

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
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.rian.ecommerce_v1.R;
import com.rian.ecommerce_v1.models.ModelUsers;
import com.rian.ecommerce_v1.server.BaseURL;
import com.rian.ecommerce_v1.server.VolleyMultipart;
import com.rian.ecommerce_v1.utils.App;
import com.rian.ecommerce_v1.utils.GsonHelper;
import com.rian.ecommerce_v1.utils.Prefs;
import com.rian.ecommerce_v1.utils.Utils;
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

public class UpdateProfile extends AppCompatActivity {

    LinearLayout takePhoto, photoResult;
    ImageView dataPhotoPedagang;
    TextInputEditText namaPedagang, emailPedagang, teleponPedagang, alamatPedagang;
    Button btnUpdateData;
    
    Toolbar toolbar;
    ModelUsers modelUsers;

    ProgressDialog progressDialog;
    Bitmap bitmap;

    String mCurrentPhotoPath, _idPedagang;

    private final int CameraR_PP = 1;
    private RequestQueue mRequestQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        mRequestQueue = Volley.newRequestQueue(this);

        dataPhotoPedagang = (ImageView) findViewById(R.id.photo_profile);
        btnUpdateData = (Button) findViewById(R.id.btn_update_profile); 
        
        takePhoto = (LinearLayout) findViewById(R.id.take_photo);
        photoResult = (LinearLayout) findViewById(R.id.photo_result);

        namaPedagang = (TextInputEditText) findViewById(R.id.namaLengkap);
        emailPedagang = (TextInputEditText) findViewById(R.id.email);
        teleponPedagang = (TextInputEditText) findViewById(R.id.nomorTelepon);
        alamatPedagang = (TextInputEditText) findViewById(R.id.alamatLengkap);

        namaPedagang.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        alamatPedagang.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        toolbar = (Toolbar) findViewById(R.id.toolbar_pedagang);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Perbaharui Profile");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UpdateProfile.this, MainPedagang.class));
                Animatoo.animateSlideUp(UpdateProfile.this);
            }
        });

        modelUsers = (ModelUsers) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUsers());

        _idPedagang = modelUsers.get_id();
        namaPedagang.setText(modelUsers.getNama());
        emailPedagang.setText(modelUsers.getEmail());
        teleponPedagang.setText(modelUsers.getPhone());
        alamatPedagang.setText(modelUsers.getAlamat());

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

        btnUpdateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataProfileFunction(bitmap);
            }
        });
    }

    private void updateDataProfileFunction(final Bitmap bitmap) {
        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();
        VolleyMultipart volleyMultipartRequest = new VolleyMultipart(Request.Method.PUT, BaseURL.updateProfile + _idPedagang,
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
                                JSONObject user = jsonObject.getJSONObject("result");
                                Utils.storeProfile(user.toString());
                                startActivity(new Intent(UpdateProfile.this, MainPedagang.class));
                                Animatoo.animateSlideDown(UpdateProfile.this);
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
                        StyleableToast.makeText(UpdateProfile.this, error.getMessage(), R.style.toastStyleWarning).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nama", namaPedagang.getText().toString());
                params.put("email", emailPedagang.getText().toString());
                params.put("phone", teleponPedagang.getText().toString());
                params.put("alamat", alamatPedagang.getText().toString());
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("fotoProfile", new VolleyMultipart.DataPart(imagename + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mRequestQueue = Volley.newRequestQueue(UpdateProfile.this);
        mRequestQueue.add(volleyMultipartRequest);
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
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
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
                dataPhotoPedagang.setImageBitmap(bitmap);
                if (dataPhotoPedagang.getDrawable() != null) {
                    dataPhotoPedagang.requestLayout();
                    dataPhotoPedagang.setScaleType(ImageView.ScaleType.FIT_XY);
                    ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) dataPhotoPedagang.getLayoutParams();
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
                        Toast.makeText(UpdateProfile.this, "Some Error! ", Toast.LENGTH_SHORT).show();
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

package com.rian.ecommerce_v1.access;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.rian.ecommerce_v1.R;
import com.rian.ecommerce_v1.server.BaseURL;
import com.rian.ecommerce_v1.utils.App;
import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegistSystem extends AppCompatActivity {

    private Button registPembeli, registPedagang;
    private TextInputEditText namaLengkap, username, password, nomorTelepon, emailAddress, alamatLengkap;
    private ProgressDialog progressDialog;

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_system);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mRequestQueue = Volley.newRequestQueue(this);

        namaLengkap = (TextInputEditText) findViewById(R.id.fullname);
        username = (TextInputEditText) findViewById(R.id.username);
        password = (TextInputEditText) findViewById(R.id.password);
        nomorTelepon = (TextInputEditText) findViewById(R.id.phone);
        emailAddress = (TextInputEditText) findViewById(R.id.mail);
        alamatLengkap = (TextInputEditText) findViewById(R.id.address);
        registPembeli = (Button) findViewById(R.id.regist_pembeli);
        registPedagang = (Button) findViewById(R.id.regist_pedagang);

        namaLengkap.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        alamatLengkap.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        registPembeli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getPref().clear();
                String dataNamaLengkap = namaLengkap.getText().toString();
                String dataUsername = username.getText().toString();
                String dataPassword = password.getText().toString();
                String dataNomorTelepon = nomorTelepon.getText().toString();
                String dataEmailAddress = emailAddress.getText().toString();
                String dataAlamatLengkap = alamatLengkap.getText().toString();

                if (dataNamaLengkap.isEmpty()){
                    StyleableToast.makeText(RegistSystem.this, "Data nama lengkap tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else if (dataUsername.isEmpty()){
                    StyleableToast.makeText(RegistSystem.this, "Data username tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else if (dataPassword.isEmpty()){
                    StyleableToast.makeText(RegistSystem.this, "Data password tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else if (dataNomorTelepon.isEmpty()){
                    StyleableToast.makeText(RegistSystem.this, "Data nomor telepon tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else if (dataEmailAddress.isEmpty()){
                    StyleableToast.makeText(RegistSystem.this, "Data alamat email tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else if (dataAlamatLengkap.isEmpty()){
                    StyleableToast.makeText(RegistSystem.this, "Data alamat lengkap tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else {
                    registPembeliFunction(dataNamaLengkap, dataUsername, dataPassword, dataNomorTelepon, dataEmailAddress, dataAlamatLengkap);
                }
            }
        });

        registPedagang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getPref().clear();
                String dataNamaLengkap = namaLengkap.getText().toString();
                String dataUsername = username.getText().toString();
                String dataPassword = password.getText().toString();
                String dataNomorTelepon = nomorTelepon.getText().toString();
                String dataEmailAddress = emailAddress.getText().toString();
                String dataAlamatLengkap = alamatLengkap.getText().toString();

                if (dataNamaLengkap.isEmpty()){
                    StyleableToast.makeText(RegistSystem.this, "Data tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else if (dataUsername.isEmpty()){
                    StyleableToast.makeText(RegistSystem.this, "Data tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else if (dataPassword.isEmpty()){
                    StyleableToast.makeText(RegistSystem.this, "Data tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else if (dataNomorTelepon.isEmpty()){
                    StyleableToast.makeText(RegistSystem.this, "Data tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else if (dataEmailAddress.isEmpty()){
                    StyleableToast.makeText(RegistSystem.this, "Data tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else if (dataAlamatLengkap.isEmpty()){
                    StyleableToast.makeText(RegistSystem.this, "Data tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else {
                    registPedagangFunction(dataNamaLengkap, dataUsername, dataPassword, dataNomorTelepon, dataEmailAddress, dataAlamatLengkap);
                }
            }
        });

    }

    private void registPembeliFunction(String dataNamaLengkap, String dataUsername, String dataPassword, String dataNomorTelepon, String dataEmailAddress, String dataAlamatLengkap) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("nama", dataNamaLengkap);
        params.put("username", dataUsername);
        params.put("password", dataPassword);
        params.put("phone", dataNomorTelepon);
        params.put("alamat", dataAlamatLengkap);
        params.put("email", dataEmailAddress);
        params.put("role", "1");

        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();

        final JsonObjectRequest req = new JsonObjectRequest(BaseURL.register, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            String strMsg = response.getString("message");
                            boolean statusMsg = response.getBoolean("status");

                            if (statusMsg == true) {
                                StyleableToast.makeText(RegistSystem.this, strMsg, R.style.toastStyleSuccess).show();
                                startActivity(new Intent(RegistSystem.this, LoginSystem.class));
                                Animatoo.animateSlideDown(RegistSystem.this);
                            } else {
                                StyleableToast.makeText(RegistSystem.this, strMsg, R.style.toastStyleWarning).show();
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

    private void registPedagangFunction(String dataNamaLengkap, String dataUsername, String dataPassword, String dataNomorTelepon, String dataEmailAddress, String dataAlamatLengkap) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("nama", dataNamaLengkap);
        params.put("username", dataUsername);
        params.put("password", dataPassword);
        params.put("phone", dataNomorTelepon);
        params.put("alamat", dataAlamatLengkap);
        params.put("email", dataEmailAddress);
        params.put("role", "2");

        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();

        final JsonObjectRequest req = new JsonObjectRequest(BaseURL.register, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            String strMsg = response.getString("message");
                            boolean statusMsg = response.getBoolean("status");

                            if (statusMsg == true) {
                                StyleableToast.makeText(RegistSystem.this, strMsg, R.style.toastStyleSuccess).show();
                                startActivity(new Intent(RegistSystem.this, LoginSystem.class));
                                Animatoo.animateSlideDown(RegistSystem.this);
                            } else {
                                StyleableToast.makeText(RegistSystem.this, strMsg, R.style.toastStyleWarning).show();
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegistSystem.this, LoginSystem.class));
        Animatoo.animateSlideDown(RegistSystem.this);
    }
}

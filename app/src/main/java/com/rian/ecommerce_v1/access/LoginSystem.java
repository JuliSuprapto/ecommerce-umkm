package com.rian.ecommerce_v1.access;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.rian.ecommerce_v1.pedagang.MainPedagang;
import com.rian.ecommerce_v1.pembeli.MainPembeli;
import com.rian.ecommerce_v1.R;
import com.rian.ecommerce_v1.models.ModelUsers;
import com.rian.ecommerce_v1.server.BaseURL;
import com.rian.ecommerce_v1.utils.App;
import com.rian.ecommerce_v1.utils.GsonHelper;
import com.rian.ecommerce_v1.utils.Prefs;
import com.rian.ecommerce_v1.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginSystem extends AppCompatActivity {

    private Button loginToSystem;
    private LinearLayout registToSystem;
    private TextInputEditText username, password;
    private ProgressDialog progressDialog;
    private RequestQueue mRequestQueue;

    ModelUsers modelUsers;

    boolean BackPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_system);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mRequestQueue = Volley.newRequestQueue(this);

        username = (TextInputEditText) findViewById(R.id.username);
        password = (TextInputEditText) findViewById(R.id.password);
        loginToSystem = (Button) findViewById(R.id.login_to_system);
        registToSystem = (LinearLayout) findViewById(R.id.regist);

        modelUsers = (ModelUsers) GsonHelper.parseGson(App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""), new ModelUsers());

        if(Utils.isLoggedIn()){
            int dataRole = Integer.parseInt(modelUsers.getRole());
            if (dataRole == 1){
                Intent i = new Intent(this , MainPembeli.class);
                startActivity(i);
                finish();
            }else if(dataRole == 2){
                Intent i = new Intent(this , MainPedagang.class);
                startActivity(i);
                finish();
            }
        }

        loginToSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dataUsername = username.getText().toString();
                String dataPassword = password.getText().toString();

                if (dataUsername.isEmpty()){
                    StyleableToast.makeText(LoginSystem.this, "Data username tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else if (dataPassword.isEmpty()){
                    StyleableToast.makeText(LoginSystem.this, "Data password tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else {
                    loginFunction(dataUsername, dataPassword);
                }
            }
        });

        registToSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginSystem.this, RegistSystem.class));
                Animatoo.animateSlideDown(LoginSystem.this);
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void loginFunction(String dataUsername, String dataPassword) {
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("username", dataUsername);
        params.put("password", dataPassword);

        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();

        final JsonObjectRequest req = new JsonObjectRequest(BaseURL.login, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            String strMsg = response.getString("message");
                            boolean statusMsg = response.getBoolean("status");

                            if (statusMsg == true) {
                                StyleableToast.makeText(LoginSystem.this, strMsg, R.style.toastStyleSuccess).show();

                                JSONObject user = response.getJSONObject("result");
                                String tRole = user.getString("role");
                                App.getPref().put(Prefs.PREF_IS_LOGEDIN, true);
                                Utils.storeProfile(user.toString());

                                if (tRole.equals("1")) {
                                    startActivity(new Intent(LoginSystem.this, MainPembeli.class));
                                    Animatoo.animateSlideDown(LoginSystem.this);
                                } else {
                                    startActivity(new Intent(LoginSystem.this, MainPedagang.class));
                                    Animatoo.animateSlideDown(LoginSystem.this);
                                }
                            } else {
                                StyleableToast.makeText(LoginSystem.this, strMsg, R.style.toastStyleWarning).show();
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
        if (BackPress) {
            super.onBackPressed();
            return;
        }
        this.BackPress = true;
        StyleableToast.makeText(this, "Tekan sekali lagi untuk keluar...", R.style.toastStyleDefault).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BackPress = false;
            }
        }, 2000);
    }
}

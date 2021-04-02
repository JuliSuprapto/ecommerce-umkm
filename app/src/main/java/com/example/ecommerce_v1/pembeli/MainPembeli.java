package com.example.ecommerce_v1.pembeli;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.ecommerce_v1.LogPembelianFragment;
import com.example.ecommerce_v1.R;
import com.example.ecommerce_v1.access.LoginSystem;
import com.example.ecommerce_v1.models.ModelUsers;
import com.example.ecommerce_v1.server.BaseURL;
import com.example.ecommerce_v1.utils.App;
import com.example.ecommerce_v1.utils.GsonHelper;
import com.example.ecommerce_v1.utils.Prefs;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainPembeli extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;
    CircleImageView photoProfile;
    TextView fullname, email;
    ModelUsers modelUsers;
    LottieAnimationView defaultPhoto;

    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pembeli);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Ecommerce UMKM");
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        loadFragment(new DashboardPembeliFragment());
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                id = item.getItemId();
                switch (id) {
                    case R.id.dashboard_pembeli:
                        loadFragment(new DashboardPembeliFragment());
                        break;
                    case R.id.keranjang_pembeli:
                        loadFragment(new KeranjangPembeliFragment());
                        break;
                    case R.id.pembayaran_pembeli:
                        loadFragment(new PembayaranPembeliFragment());
                        break;
                    case R.id.pengiriman_pembeli:
                        loadFragment(new PengirimanSelesaiFragment());
                        break;
                    case R.id.pembelian_pembeli:
                        loadFragment(new LogPembelianFragment());
                        break;
                    case R.id.me_pembeli:
                        loadFragment(new AccountPembeliFragment());
                        break;
                    case R.id.logout_pembeli:
                        App.getPref().clear();
                        startActivity(new Intent(MainPembeli.this, LoginSystem.class));
                        Animatoo.animateSlideUp(MainPembeli.this);
                        break;
                }
                return true;
            }
        });

        View headerItem = navigationView.getHeaderView(0);

        photoProfile = (CircleImageView) headerItem.findViewById(R.id.photo_profile);
        fullname = (TextView) headerItem.findViewById(R.id.fullname);
        email = (TextView) headerItem.findViewById(R.id.email_address);
        defaultPhoto = (LottieAnimationView) headerItem.findViewById(R.id.images);

        modelUsers = (ModelUsers) GsonHelper.parseGson(
                App.getPref().

                        getString(Prefs.PREF_STORE_PROFILE, ""),
                new

                        ModelUsers()
        );

        String dProfilePhoto = modelUsers.getFotoProfile();

        if (dProfilePhoto == null) {
            photoProfile.setVisibility(View.GONE);
            defaultPhoto.setVisibility(View.VISIBLE);
        } else {
            defaultPhoto.cancelAnimation();
            defaultPhoto.setVisibility(View.GONE);
            photoProfile.setVisibility(View.VISIBLE);
            Picasso.get().load(BaseURL.baseUrl + dProfilePhoto).into(photoProfile);
        }

        fullname.setText(modelUsers.getNama());
        email.setText(modelUsers.getEmail());
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager f = getSupportFragmentManager();
        FragmentTransaction t = f.beginTransaction();
        t.replace(R.id.frame, fragment).commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        t.addToBackStack(null);
    }
}

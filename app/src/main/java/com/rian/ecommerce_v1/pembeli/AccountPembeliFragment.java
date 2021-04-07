package com.rian.ecommerce_v1.pembeli;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.rian.ecommerce_v1.R;
import com.rian.ecommerce_v1.models.ModelUsers;
import com.rian.ecommerce_v1.server.BaseURL;
import com.rian.ecommerce_v1.utils.App;
import com.rian.ecommerce_v1.utils.GsonHelper;
import com.rian.ecommerce_v1.utils.Prefs;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountPembeliFragment extends Fragment {

    private TextView fullname, email, nomorTelepon, alamatLengkap, dEmail;
    private LottieAnimationView defaultPhoto;
    private CircleImageView profilePhoto;
    LinearLayout updateProfileData;

    ModelUsers modelUsers;
    String _id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account_pembeli, container, false);

        defaultPhoto = (LottieAnimationView)v.findViewById(R.id.images);

        profilePhoto = (CircleImageView)v.findViewById(R.id.photoprofileuser);
        fullname = (TextView)v.findViewById(R.id.fullname);
        nomorTelepon = (TextView)v.findViewById(R.id.phone);
        alamatLengkap = (TextView)v.findViewById(R.id.address);
        dEmail = (TextView)v.findViewById(R.id.email);
        email = (TextView)v.findViewById(R.id.demail);
        updateProfileData = (LinearLayout)v.findViewById(R.id.update_profile_pembeli);

        modelUsers = (ModelUsers) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelUsers()
        );

        fullname.setText(modelUsers.getNama());
        nomorTelepon.setText(modelUsers.getPhone());
        alamatLengkap.setText(modelUsers.getAlamat());
        dEmail.setText(modelUsers.getEmail());
        email.setText(modelUsers.getEmail());

        String dProfilePhoto = modelUsers.getFotoProfile();
        String namaPembeli = modelUsers.getNama();

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(namaPembeli);

        _id = modelUsers.get_id();

        if (dProfilePhoto == null){
            defaultPhoto.setVisibility(View.VISIBLE);
            profilePhoto.setVisibility(View.GONE);
        } else{
            defaultPhoto.cancelAnimation();
            defaultPhoto.setVisibility(View.GONE);
            profilePhoto.setVisibility(View.VISIBLE);
            Picasso.get().load(BaseURL.baseUrl + dProfilePhoto).into(profilePhoto);
        }

        updateProfileData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdateProfilePembeli.class));
                Animatoo.animateSlideUp(getActivity());
            }
        });

        return v;
    }
}

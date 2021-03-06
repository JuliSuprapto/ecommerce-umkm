package com.rian.ecommerce_v1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.rian.ecommerce_v1.R;
import com.rian.ecommerce_v1.models.ModelBaners;

import java.util.List;

public class AdapterSliderBanner extends PagerAdapter {

    private List<ModelBaners> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public AdapterSliderBanner(List<ModelBaners> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_banner, container, false);

        ImageView imageView;
        TextView title, desc;

        imageView = view.findViewById(R.id.banner);
        imageView.setImageResource(models.get(position).getImage());
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

}

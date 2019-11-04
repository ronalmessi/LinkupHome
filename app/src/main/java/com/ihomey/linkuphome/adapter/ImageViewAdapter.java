package com.ihomey.linkuphome.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.piasy.biv.view.BigImageView;
import com.ihomey.linkuphome.R;
import com.ihomey.linkuphome.widget.ProgressPieIndicator;

import java.util.List;

public class ImageViewAdapter extends PagerAdapter {

    private List<String> imageUrls;
    private Context context;

    public ImageViewAdapter(List<String> imageUrls, Context context) {
        this.imageUrls = imageUrls;
        this.context = context;
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        String url = imageUrls.get(position);
        View view = View.inflate(context, R.layout.item_instruction_image, null);
        BigImageView bigImageView = view.findViewById(R.id.mBigImage);
        bigImageView.setProgressIndicator(new ProgressPieIndicator());
        bigImageView.showImage(Uri.parse(url));
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}

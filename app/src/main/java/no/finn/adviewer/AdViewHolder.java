package no.finn.adviewer;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ivbaranov.mfb.MaterialFavoriteButton;

class AdViewHolder extends RecyclerView.ViewHolder {

    TextView txtTitle, txtLocation, txtPrice;
    MaterialFavoriteButton btnFavorite;
    ImageView ivAdImage;

    AdViewHolder(View view) {
        super(view);
        txtTitle = view.findViewById(R.id.title);
        txtLocation = view.findViewById(R.id.location);
        txtPrice = view.findViewById(R.id.price);
        ivAdImage = view.findViewById(R.id.ad_image);
        btnFavorite = view.findViewById(R.id.favorite);
    }

}
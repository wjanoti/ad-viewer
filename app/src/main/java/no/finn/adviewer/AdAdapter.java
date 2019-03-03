package no.finn.adviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class AdAdapter extends RecyclerView.Adapter<AdViewHolder> {

    private List<Ad> adsList;
    private Context context;

    AdAdapter(List<Ad> adsList, Context ctx) {
        this.adsList = adsList;
        this.context = ctx;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public AdViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.ad_view, viewGroup, false);

        return new AdViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdViewHolder viewHolder, int i) {

        final Ad ad = adsList.get(i);

        /*
          Save ad contents to shared preferences as json and its image to storage when the
          ad is favorited.
         */
        viewHolder.btnFavorite.setOnFavoriteChangeListener(
                (buttonView, favorite) -> {
                    String imagePath = context.getFilesDir().getPath() +
                            File.separator + Integer.toString(ad.getId()) + ".jpeg";
                    File imageFile = new File(imagePath);
                    SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    try {
                        if (favorite) {
                            ad.setImagePath(imagePath);
                            ad.setFavorite(true);
                            editor.putString(Integer.toString(ad.getId()), ad.toJSON());
                            if (!imageFile.exists()) {
                                FileOutputStream ostream = new FileOutputStream(imageFile);
                                Bitmap adImage = ((BitmapDrawable) viewHolder.ivAdImage.getDrawable()).getBitmap();
                                adImage.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                ostream.flush();
                                ostream.close();
                            }

                        } else {
                            ad.setFavorite(false);
                            ad.setImagePath("");
                            editor.remove(Integer.toString(ad.getId()));
                            imageFile.delete();
                        }
                    } catch (Exception e) {
                        Log.e("IOException", e.getLocalizedMessage());
                    } finally {
                        editor.apply();
                    }
                }
        );

        viewHolder.txtTitle.setText(ad.getTitle());
        viewHolder.txtLocation.setText(ad.getLocation());

        File adImageFile = new File(ad.getImagePath());
        if (adImageFile.exists()) {
            Picasso.get().load(adImageFile).transform(new RoundedCornersTransformation(10, 10)).
                    placeholder(R.drawable.no_image).into(viewHolder.ivAdImage);
        } else {
            Picasso.get().load(ad.getImageUrl()).transform(new RoundedCornersTransformation(10, 10)).
                    placeholder(R.drawable.no_image).into(viewHolder.ivAdImage);
        }

        if (ad.getPrice() == 0) {
            viewHolder.txtPrice.setText("");
        } else {
            String currency = "Kr";
            viewHolder.txtPrice.setText(String.format("%d", ad.getPrice()) + ' ' + currency);
        }

        viewHolder.btnFavorite.setFavorite(ad.isFavorite());
    }

    @Override
    public int getItemCount() {
        return adsList.size();
    }

    void setAdsList(List<Ad> adsList) {
        this.adsList = adsList;
    }
}

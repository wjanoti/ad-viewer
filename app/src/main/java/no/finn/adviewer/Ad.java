package no.finn.adviewer;

import android.graphics.Bitmap;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class to hold ad data.
 */
public class Ad {

    private int id;
    private String title;
    private int price;
    private String location;
    private String imageUrl;
    private String imagePath;
    private boolean isFavorite;

    Ad(int id, String title, int price, String location, String imageUrl) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.location = location;
        this.imageUrl = imageUrl;
        this.imagePath = "";
        this.isFavorite = false;
    }

    int getId() {
        return id;
    }

    String getTitle() {
        return title;
    }

    int getPrice() {
        return price;
    }

    String getLocation() {
        return location;
    }

    String getImageUrl() {
        return imageUrl;
    }

    String getImagePath() {
        return imagePath;
    }

    void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    boolean isFavorite() {
        return isFavorite;
    }

    void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", getId());
            jsonObject.put("title", getTitle());
            jsonObject.put("location", getLocation());
            jsonObject.put("price", getPrice());
            jsonObject.put("favorite", isFavorite());
            jsonObject.put("imagePath", getImagePath());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    /**
     * Create a new Ad object from JSON
     *
     * @param jsonString JSON representation of an ad
     * @return Ad
     */
    static Ad fromJson(String jsonString) throws JSONException {
        JSONObject json = new JSONObject(jsonString);

        Ad ad = new Ad(json.getInt("id"), json.getString("title"),
                json.getInt("price"), json.getString("location"), "");

        ad.setFavorite(json.getBoolean("favorite"));
        if (json.has("imagePath")) {
            ad.setImagePath(json.getString("imagePath"));
        }

        return ad;
    }

}

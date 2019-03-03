package no.finn.adviewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Class responsible for fetching ads either from the server or from storage.
 */
class AdDataFetcher {

    private static final String JSON_URL = "https://gist.githubusercontent.com/3lvis/\n" +
            "3799feea005ed49942dcb56386ecec2b/raw/\n" +
            "63249144485884d279d55f4f3907e37098f55c74/discover.json";

    private static final String IMAGE_BASE_URL = "https://images.finncdn.no/dynamic/480x360c/";

    private Context context;
    private SharedPreferences sharedPreferences;

    AdDataFetcher(Context context) {
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), MODE_PRIVATE);
    }

    void fetch() {
        ProgressBar progressBar = ((MainActivity) context).findViewById(R.id.progressBar);
        TextView errorText = ((MainActivity) context).findViewById(R.id.errorTextView);
        List<Ad> adList = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(context);

        // hide loading ivAdImage when request is done
        queue.addRequestFinishedListener(request -> progressBar.setVisibility(View.GONE));

        // prepare a request to fetch the JSON and updates the UI accordingly.
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                JSON_URL,
                null,
                jsonObjectResponse -> {
                    try {
                        JSONArray jsonResponseArray = jsonObjectResponse.getJSONArray("items");
                        for (int i = 0; i < jsonResponseArray.length(); i++) {
                            JSONObject adObject = jsonResponseArray.getJSONObject(i);
                            adList.add(extractAd(adObject));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ((MainActivity) context).refreshAdList(adList);
                }, error -> {
            adList.clear();
            progressBar.setVisibility(View.GONE);
            errorText.setText(context.getString(R.string.error_message));
            errorText.setVisibility(View.VISIBLE);
            ((MainActivity) context).refreshAdList(adList);
        });


        queue.add(request);
    }

    void fetchFavoritesFromPreferences(final Context context) {

        Map<String, ?> favorites = sharedPreferences.getAll();

        List<Ad> favoritedAds = new ArrayList<>();
        try {
            for (Map.Entry<String, ?> entry : favorites.entrySet()) {
                Ad ad = Ad.fromJson(entry.getValue().toString());
                favoritedAds.add(ad);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((MainActivity) context).refreshAdList(favoritedAds);
    }

    /**
     * Parse ad info from JSON and creates a new Ad object.
     *
     * @param adJSON Ad JSON object
     * @return Ad
     */
    private Ad extractAd(JSONObject adJSON) throws JSONException {

        String imageUrl = "";
        String location = "Unspecified";
        String title = "";
        int price = 0;
        int id = adJSON.getInt("id");
        Ad ad;

        String favoritedAdJson = sharedPreferences.getString(Integer.toString(id), "");
        if (!favoritedAdJson.equals("")) {
            ad = Ad.fromJson(favoritedAdJson);
        } else {
            if (adJSON.has("image")) {
                imageUrl = IMAGE_BASE_URL + adJSON.getJSONObject("image").getString("url");
            }

            if (adJSON.has("location")) {
                location = adJSON.getString("location");
            }

            if (adJSON.has("description")) {
                title = adJSON.getString("description");
            }

            if (adJSON.has("price")) {
                price = adJSON.getJSONObject("price").getInt("value");
            }

            ad = new Ad(id, title, price, location, imageUrl);
        }

        return ad;

    }

}

package no.finn.adviewer;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Ad> adList = new ArrayList<>();
    private AdAdapter mAdapter;
    private RecyclerView recyclerView;
    private TextView textViewError;
    private AdDataFetcher adDataFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // change app bar color
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(
                new ColorDrawable(
                        ContextCompat.getColor(this, R.color.colorFinnDarkBlue)
                )
        );

        textViewError = findViewById(R.id.errorTextView);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);

        mAdapter = new AdAdapter(adList, this);
        recyclerView.setAdapter(mAdapter);

        adDataFetcher = new AdDataFetcher(this);
        adDataFetcher.fetch();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the recycler view when with a list of Ads
     *
     * @param newAdList list of Ad objects
     */
    public void refreshAdList(List<Ad> newAdList) {
        adList = newAdList;
        mAdapter.setAdsList(adList);
        mAdapter.notifyDataSetChanged();
    }

    public void scrollToTop(View view) {
        recyclerView.scrollToPosition(0);
    }

    /**
     * Filter out favorites (if any) when the user clicks on the menu checkbox.
     *
     * @param item item clicked on the menu.
     */
    public void filterFavorites(MenuItem item) {

        if (textViewError.getVisibility() == View.VISIBLE) {
            textViewError.setVisibility(View.INVISIBLE);
        }

        if (item.isChecked()) {
            item.setChecked(false);
            adDataFetcher.fetch();
        } else {
            item.setChecked(true);
            adDataFetcher.fetchFavoritesFromPreferences(this);
        }
    }
}
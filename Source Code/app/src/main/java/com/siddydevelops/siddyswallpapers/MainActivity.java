package com.siddydevelops.siddyswallpapers;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.siddydevelops.siddyswallpapers.activities.LoginActivity;
import com.siddydevelops.siddyswallpapers.adapters.SuggestedAdapter;
import com.siddydevelops.siddyswallpapers.adapters.WallpaperAdapter;
import com.siddydevelops.siddyswallpapers.interfaces.RecyclerViewClickListener;
import com.siddydevelops.siddyswallpapers.models.SuggestedModel;
import com.siddydevelops.siddyswallpapers.models.WallpaperModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RecyclerViewClickListener {

    static final float END_SCALE = 0.7f;
    ImageView menuIcon;
    LinearLayout contentView;

    DrawerLayout drawerLayout;
    NavigationView navigationView;

    RecyclerView recyclerView, topMostRecyclerView;
    RecyclerView.Adapter adapter;
    WallpaperAdapter wallpaperAdapter;
    List<WallpaperModel> wallpaperModelList;

    ArrayList<SuggestedModel> suggestedModels = new ArrayList<>();

    Boolean isScrolling = false;
    int currentItems, totalItems, scrollOutItems;

    Dialog dialog;

    ProgressBar progressBar;

    TextView replaceTitle;

    EditText searchEt;
    ImageView searchIv;

    int pageNumber;

    String url = "https://api.pexels.com/v1/curated?page=" + pageNumber + "&per_page=80"; //We need multiple pages when scrolled

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content_view);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        navigationDrawer();

        //NavigationDrawerProfile
        View headerView = navigationView.getHeaderView(0);
        ImageView appLogo = headerView.findViewById(R.id.app_image);

        appLogo.setClipToOutline(true);


        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation_for_dia;
        Button closeButton = dialog.findViewById(R.id.dialogButton);
        ImageView mePhoto = dialog.findViewById(R.id.mePhoto);
        mePhoto.setClipToOutline(true);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Thank you for using my App.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        recyclerView = findViewById(R.id.recyclerView);
        topMostRecyclerView = findViewById(R.id.suggestedRecyclerView);

        wallpaperModelList = new ArrayList<>();
        wallpaperAdapter = new WallpaperAdapter(this, wallpaperModelList);

        recyclerView.setAdapter(wallpaperAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //Scrolling Behaviour
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                currentItems = gridLayoutManager.getChildCount();
                totalItems = gridLayoutManager.getItemCount();
                scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                if(isScrolling && (currentItems + scrollOutItems == totalItems))
                {
                    isScrolling = false;
                    fetchWallpaper();
                }

            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        replaceTitle = (TextView) findViewById(R.id.topMostTitle);

        fetchWallpaper();

        suggestedItems();

        //Search editText and imageView
        searchEt = findViewById(R.id.searchEv);
        searchIv = findViewById(R.id.search_image);
        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Set the functionality
                //Toast.makeText(MainActivity.this, "Search Button Clicked!", Toast.LENGTH_SHORT).show();
                String query = searchEt.getText().toString();
                //progressBar.setVisibility(View.VISIBLE);
                String searchUrl = "";

                replaceTitle.setText("Search results for " + "'" + query + "'" + " are:");
                url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=" + query;
                wallpaperModelList.clear();
                fetchWallpaper();
                //progressBar.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

    }

    private void navigationDrawer()
    {  //Navigation Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(drawerLayout.isDrawerVisible(GravityCompat.START))
                {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else
                {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        //Animation in the darawer
        animateNavigationDrawer();
    }

    private void animateNavigationDrawer()
    {
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //Scale the view based on current slide offset
                final float diffScaledOffSet = slideOffset * (1 - END_SCALE);
                final float offSetScale = 1 - diffScaledOffSet;
                contentView.setScaleX(offSetScale);
                contentView.setScaleY(offSetScale);

                //Translate the view accounting of the scale width
                final float xOffSet = drawerView.getWidth() * slideOffset;
                final float xOffsetdiff = contentView.getWidth() * diffScaledOffSet/2;
                final float xTranslation = xOffSet - xOffsetdiff;
                contentView.setTranslationX(xTranslation);


            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerVisible(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.nav_home:
                //Toast.makeText(this, "Home Clicked!", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);
                finish();
                break;

            case R.id.nav_trending:
                //Toast.makeText(this, "Trending Clicked!", Toast.LENGTH_SHORT).show();
                replaceTitle.setText("Trending");
                url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=trending";
                wallpaperModelList.clear();
                fetchWallpaper();
                //progressBar.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_mostViewed:
                //Toast.makeText(this, "MostViewed Clicked!", Toast.LENGTH_SHORT).show();
                replaceTitle.setText("Most Viewed");
                url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=most viewed";
                wallpaperModelList.clear();
                fetchWallpaper();
                //progressBar.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_logout:
                //Toast.makeText(this, "LogOut Clicked!", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
                break;

            case R.id.nav_about:
                //Toast.makeText(this, "About Clicked!", Toast.LENGTH_SHORT).show();
                dialog.show();
                break;
        }
        return true;
    }

    private void suggestedItems()
    {
        topMostRecyclerView.setHasFixedSize(true);
        topMostRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        suggestedModels.add(new SuggestedModel(R.drawable.trending_pic, "Trending"));
        suggestedModels.add(new SuggestedModel(R.drawable.film1, "Nature"));
        suggestedModels.add(new SuggestedModel(R.drawable.architecture_pic, "Architecture"));
        suggestedModels.add(new SuggestedModel(R.drawable.poeple_pic, "People"));
        suggestedModels.add(new SuggestedModel(R.drawable.buisness_pic, "Business"));
        suggestedModels.add(new SuggestedModel(R.drawable.health_pic, "Health"));
        suggestedModels.add(new SuggestedModel(R.drawable.fashion_pic, "Fashion"));
        suggestedModels.add(new SuggestedModel(R.drawable.cinema_pic, "Film"));
        suggestedModels.add(new SuggestedModel(R.drawable.travel_pic, "Travel"));

        adapter = new SuggestedAdapter(suggestedModels, MainActivity.this);
        topMostRecyclerView.setAdapter(adapter);
    }

    private void fetchWallpaper()
    {   //fetch image URL and name from the PEXELS API
        StringRequest request = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressBar.setVisibility(View.INVISIBLE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("photos");

                            int length = jsonArray.length();

                            for(int i=0;i<length;i++)
                            {
                                JSONObject object = jsonArray.getJSONObject(i);
                                int id = object.getInt("id");
                                String photographerName = object.getString("photographer");

                                JSONObject objectImage = object.getJSONObject("src");  //Nested Objects
                                String originaleUrl = objectImage.getString("original");
                                String mediumUrl = objectImage.getString("medium");

                                WallpaperModel wallpaperModel = new WallpaperModel(id, originaleUrl, mediumUrl, photographerName);
                                wallpaperModelList.add(wallpaperModel);
                            }

                            wallpaperAdapter.notifyDataSetChanged();
                            pageNumber++;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization","563492ad6f91700001000001f641cb977a684e7abaf284e355080979");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemClick(int position) {
        progressBar.setVisibility(View.VISIBLE);
        if(position == 0)
        {
            replaceTitle.setText("Trending");
            url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=trending";
            wallpaperModelList.clear();
            fetchWallpaper();
            //progressBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else if(position == 1)
        {
            replaceTitle.setText("Nature");
            url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=nature";
            wallpaperModelList.clear();
            fetchWallpaper();
            //progressBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else if(position == 2)
        {
            replaceTitle.setText("Architecture");
            url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=architecture";
            wallpaperModelList.clear();
            fetchWallpaper();
            //progressBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else if(position == 3)
        {
            replaceTitle.setText("People");
            url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=people";
            wallpaperModelList.clear();
            fetchWallpaper();
            //progressBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else if(position == 4)
        {
            replaceTitle.setText("Business");
            url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=business";
            wallpaperModelList.clear();
            fetchWallpaper();
            //progressBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else if(position == 5)
        {
            replaceTitle.setText("Health");
            url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=health";
            wallpaperModelList.clear();
            fetchWallpaper();
            //progressBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else if(position == 6)
        {
            replaceTitle.setText("Fashion");
            url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=fashion";
            wallpaperModelList.clear();
            fetchWallpaper();
            //progressBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else if(position == 7)
        {
            replaceTitle.setText("Film");
            url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=film";
            wallpaperModelList.clear();
            fetchWallpaper();
            //progressBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else if(position == 8)
        {
            replaceTitle.setText("Travel");
            url = "https://api.pexels.com/v1/search/?page=" + pageNumber + "&per_page=80&query=travel";
            wallpaperModelList.clear();
            fetchWallpaper();
            //progressBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }

    }
}
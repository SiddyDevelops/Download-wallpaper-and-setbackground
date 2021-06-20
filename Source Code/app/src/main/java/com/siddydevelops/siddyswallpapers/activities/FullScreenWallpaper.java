package com.siddydevelops.siddyswallpapers.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.artjimlop.altex.AltexImageDownloader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.siddydevelops.siddyswallpapers.MainActivity;
import com.siddydevelops.siddyswallpapers.R;

import java.io.IOError;
import java.io.IOException;

import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class FullScreenWallpaper extends AppCompatActivity {

    String originalUrl = "";
    PhotoView photoView;
    CircularProgressButton setWallpaperButton;
    CircularProgressButton downloadWallpaperButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_wallpaper);

        Intent intent = getIntent();
        originalUrl = intent.getStringExtra("originalUrl");

        final ProgressBar progressBar = findViewById(R.id.progress_bar);

        photoView = findViewById(R.id.photoView);
        Glide.with(this).load(originalUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(photoView);

        setWallpaperButton = (CircularProgressButton) findViewById(R.id.buttonSetWallpaper);
        setWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                AsyncTask<String, String, String> demoSetWallpaper = new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        try{
                            Thread.sleep(3000);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        return "done";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        if(s.equals("done"))
                        {
                            WallpaperManager wallpaperManager = WallpaperManager.getInstance(FullScreenWallpaper.this);
                            Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();

                            try {
                                wallpaperManager.setBitmap(bitmap);
                                Toast.makeText(FullScreenWallpaper.this, "Wallpaper changed Successfully!", Toast.LENGTH_SHORT).show();
                                //setWallpaperButton.doneLoadingAnimation(Color.parseColor("#333693"),
                                        //BitmapFactory.decodeResource(getResources(), R.drawable.done_icon));
                                setWallpaperButton.setText("Wallpaper Changed");
                                setWallpaperButton.setClickable(false);
                            }catch(IOException e)
                            {
                                e.printStackTrace();
                            }

                        }

                    }
                };
                //setWallpaperButton.startAnimation();
                demoSetWallpaper.execute();
            }
        });

        downloadWallpaperButton = (CircularProgressButton) findViewById(R.id.buttonDownloadWallpaper);
        downloadWallpaperButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            @NonNull
            public void onClick(View v) {
                AsyncTask<String, String, String> demoSetWallpaper = new AsyncTask<String, String, String>() {
                    @Override
                    protected String doInBackground(String... params) {
                        try{
                            Thread.sleep(3000);
                        }catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        return "done";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        if(s.equals("done"))
                        {
                            AltexImageDownloader.writeToDisk(FullScreenWallpaper.this, originalUrl, "pexelsWallpaper");
                            Toast.makeText(FullScreenWallpaper.this, "Downloading Started...", Toast.LENGTH_SHORT).show();
                            //downloadWallpaperButton.doneLoadingAnimation(Color.parseColor("#333693"),
                                    //BitmapFactory.decodeResource(getResources(), R.drawable.done_icon));
                            downloadWallpaperButton.setText("Image Downloaded");
                            downloadWallpaperButton.setClickable(false);
                        }
                    }
                };
                //downloadWallpaperButton.startAnimation();
                demoSetWallpaper.execute();
            }
        });

    }
}
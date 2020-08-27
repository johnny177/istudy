package com.nnoboa.istudy;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;

public class MainActivity extends AppCompatActivity {

    private static final String PREF = "pref" ;
    private AppBarConfiguration mAppBarConfiguration;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ChildEventListener childEventListener;
    DatabaseReference databaseReference;
    final int RC_SIGN_IN = 100;
    String USER_UID;
    String USER_EMAIL;
    String USER_PHOTO_URL;
    String USER_NAME;
    String USER_CHILD;
    ImageView userPhoto;
    TextView userName;
    TextView userEmail;
    View nav_parentView;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static int REQUEST_PERMISSIONS = 1;


    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user != null){
                try {
                    OnSignedInInitialize(user.getDisplayName(),user.getEmail(),user.getPhotoUrl().toString(),user.getUid());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }else{
                OnSignOutCleanUp();
                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build()
                );

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers)
                                .setTheme(R.style.LoginTheme)
                                .setLogo(R.drawable.ic_logo)

                                .build(), RC_SIGN_IN
                );
            }
        }
    };

    private void OnSignedInInitialize(String displayName, String email, String photoUrl, String uid) throws MalformedURLException {
        userEmail.setText(email);
        userName.setText(displayName);
        Picasso.get().load(photoUrl).transform(new CropCircleTransformation()).into(userPhoto);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        DownloadDisplayImage(USER_PHOTO_URL,USER_NAME,USER_UID);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        userName = headerView.findViewById(R.id.username);
        userEmail = headerView.findViewById(R.id.user_email);
        preferences = getSharedPreferences(PREF,Context.MODE_PRIVATE);
        userPhoto = headerView.findViewById(R.id.user_photo);
        nav_parentView = headerView.findViewById(R.id.nav_background);
        loadViews();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_alarm, R.id.nav_files, R.id.nav_flashcard, R.id.nav_blog,R.id.nav_study_chat)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    private void OnSignOutCleanUp(){
        editor = preferences.edit();
        editor.putString("dp",null);
        editor.commit();
        USER_EMAIL = null;
        USER_PHOTO_URL = null;
        USER_UID = null;
        USER_NAME = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if(requestCode == RC_SIGN_IN){
                if (resultCode == RESULT_OK){
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    assert user != null;
                    USER_NAME = user.getDisplayName();
                    USER_UID = user.getUid();
                    USER_PHOTO_URL = String.valueOf(user.getPhotoUrl());
                    USER_EMAIL = user.getEmail();
//                    SharedPreferences preferences = getSharedPreferences(PREF,Context.MODE_PRIVATE);
//                    String dpUri = preferences.getString("dp",null);
////                    DownloadDisplayImage(USER_PHOTO_URL,USER_NAME,USER_UID);
//                    if(dpUri != null){
//                        loadUserProfile();
//                    }else{
//                        Picasso.get().load(USER_PHOTO_URL).into(picassoImageTarget(getApplicationContext(), ".User_Profile_Photo", USER_NAME+"-"+USER_UID.substring(0,5)));
////                        DownloadDisplayImage(USER_PHOTO_URL,USER_NAME,USER_UID);
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserProfile(){
        SharedPreferences preferences = getSharedPreferences(PREF,Context.MODE_PRIVATE);
        String dpUri = preferences.getString("dp",null);
        Picasso.get()
                .load(dpUri)
                .transform(new CropCircleTransformation())
                .into(userPhoto);
        nav_parentView.setBackground(BitmapDrawable.createFromPath(String.valueOf(dpUri)));
    }

    private void loadViews(){

    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        }
    }


    private Target picassoImageTarget(Context context, final String imageDir, final String imageName) {
        Log.d("picassoImageTarget", " picassoImageTarget");
        ContextWrapper cw = new ContextWrapper(context);
        final File directory = cw.getDir(imageDir, Context.MODE_PRIVATE); // path to /data/data/yourapp/app_imageDir
        return new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final File myImageFile = new File(directory, imageName); // Create image file
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(myImageFile);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        editor = preferences.edit();
                        editor.putString("dp",String.valueOf(myImageFile));
                        editor.commit();
                        Log.i("image", "image saved to >>>" + myImageFile.getAbsolutePath());

                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                if (placeHolderDrawable != null) {}
            }
        };
    }

}
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
import android.view.MenuItem;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nnoboa.istudy.ui.chat.activities.GroupInfoActivity;
import com.nnoboa.istudy.ui.chat.activities.UserInfoActivity;
import com.nnoboa.istudy.ui.chat.models.User;
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
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import jp.wasabeef.picasso.transformations.BlurTransformation;
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
    String USER_CHILD = "users";
    String USERNAME;
    String PHONE;
    Long TIMESTAMP;
    ImageView userPhoto;
    TextView userName;
    TextView userEmail;
    View nav_parentView;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public static int REQUEST_PERMISSIONS = 1;
    private static int randomNumber;
    private String filePath = "ProfilePictures";
    File external_file;


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
        String uri = preferences.getString("dp",null);
        if(uri== null){
            Picasso.get()
                    .load(photoUrl)
                    .transform(new CropCircleTransformation())
                    .into(userPhoto);
            SaveProfilePic(photoUrl,USERNAME);
            loadUserProfile();
        }else {
//            Picasso.get().load(USER_PHOTO_URL).transform(new CropCircleTransformation()).into(userPhoto);
            loadUserProfile();

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Random random = new Random();
        randomNumber = random.nextInt(9999);
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
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(USER_CHILD);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_alarm, R.id.nav_files, R.id.nav_flashcard, R.id.nav_blog,R.id.nav_study_chat,R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        fn_permission();
        loadUserProfile();
        userEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GroupInfoActivity.class);
                startActivity(intent);
            }
        });
//        String uri = preferences.getString("dp",null);
//        if(uri== null){
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            USER_PHOTO_URL = String.valueOf(user.getPhotoUrl());
//            USERNAME = user.getDisplayName().split(" ")[0]+randomNumber;
//            Picasso.get()
//                    .load(USER_PHOTO_URL)
//                    .transform(new CropCircleTransformation())
//                    .transform(new BlurTransformation(getApplicationContext(),25,1))
//                    .into(userPhoto);
//            SaveProfilePic(USER_PHOTO_URL,USERNAME);
//            loadUserProfile();
//        }else {
////            Picasso.get().load(USER_PHOTO_URL).transform(new CropCircleTransformation()).into(userPhoto);
//            loadUserProfile();
//
//        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    private void OnSignOutCleanUp(){
        SharedPreferences preferences = getSharedPreferences(PREF,Context.MODE_PRIVATE);
        String dpUri = preferences.getString("dp",null);
        if(dpUri != null) {
            File file = new File(dpUri);
            file.delete();
        }

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
                    final FirebaseUser user = firebaseAuth.getCurrentUser();
                    assert user != null;
                    USER_NAME = user.getDisplayName();
                    USER_UID = user.getUid();
                    USER_PHOTO_URL = String.valueOf(user.getPhotoUrl());
                    USER_EMAIL = user.getEmail();
                    TIMESTAMP = Calendar.getInstance().getTimeInMillis();
                    final String[] name = USER_NAME.split(" ");
                    USERNAME = "@"+name[0]+randomNumber;
                    USERNAME = USERNAME.toLowerCase();
                    SaveProfilePic(USER_PHOTO_URL,USER_NAME);
                    final User newUser = new User(USER_NAME,USERNAME.toLowerCase(),USER_UID,USER_EMAIL,PHONE,USER_PHOTO_URL,TIMESTAMP,null,name[0]);
                    DatabaseReference root = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference oldUser = root.child(USER_CHILD);
                    SaveProfilePic(USER_PHOTO_URL,USERNAME);
                    oldUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child(user.getUid()).exists()){
                                Log.d("MainActivity", "user already exist");
                                Toast.makeText(MainActivity.this, "Welcome back "+name[0],Toast.LENGTH_LONG).show();
                            }else if (!dataSnapshot.child(USER_UID).exists()){
                                databaseReference.child(USER_UID).setValue(newUser);
                                Toast.makeText(MainActivity.this, "Welcome "+name[0]+" " +
                                        "Your username is "+USERNAME,Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
//                    SharedPreferences preferences = getSharedPreferences(PREF,Context.MODE_PRIVATE);
//                    String dpUri = preferences.getString("dp",null);
////                    DownloadDisplayImage(USER_PHOTO_URL,USER_NAME,USER_UID);
//                    if(dpUri != null){
//                        loadUserProfile();
//                    }else{
//                        Picasso.get().load(USER_PHOTO_URL).into(picassoImageTarget(getApplicationContext(), ".User_Profile_Photo", USER_NAME+"-"+USER_UID.substring(0,5)));
////                        DownloadDisplayImage(USER_PHOTO_URL,USER_NAME,USER_UID);
//                    }
                }else if(resultCode == RESULT_CANCELED){
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private void loadUserProfile(){
        SharedPreferences preferences = getSharedPreferences(PREF,Context.MODE_PRIVATE);
        String dpUri = preferences.getString("dp",null);

        nav_parentView.setBackgroundDrawable(BitmapDrawable.createFromPath(String.valueOf(dpUri)));
        File file = new File(dpUri);
        Picasso.get()
                .load(file)
                .fit()
                .transform(new CropCircleTransformation())
                .into(userPhoto);
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
        if((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)){
            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.INTERNET))) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET},
                        REQUEST_PERMISSIONS);

        }
        }

        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        }
    }


  private void SaveProfilePic(String url, String filename){
        Picasso.get()
                .load(url)
                .into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(external_file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.PNG, 100,fileOutputStream);
                try {
                    fileOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
      if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
      }
      else {
           external_file= new File(getExternalFilesDir(filePath), filename+".png");
           SharedPreferences.Editor editor = preferences.edit();
           editor.putString("dp",external_file.getAbsolutePath());
           editor.commit();

      }
  }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }
}
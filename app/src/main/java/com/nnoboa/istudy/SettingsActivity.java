package com.nnoboa.istudy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nnoboa.istudy.ui.chat.models.User;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;

public class SettingsActivity extends AppCompatActivity {
    private static final int RC_PHOTO_PICKER = 200;
    private static  String USER_PHOTO_URL;
    static int REQUEST_CODE_ALERT_RINGTONE = 100;
    String USER_UID;
    String USER_EMAIL;
    private static final String PREF = "pref" ;
    String USER_NAME;
    String USER_CHILD = "users";
    String USERNAME;
    String PHONE;
    SharedPreferences preferences;
    private static int randomNumber;
    Long TIMESTAMP;
    static Preference preference;
    static ListPreference listPreference;
    ImageView backgroundImage, avatar;
    TextView display_name, fullname,phoneNumber,userName,biography;
    CardView usernameCard,phoneCard,bioCard;
    ImageButton changePhoto;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ChildEventListener childEventListener;
    DatabaseReference databaseReference, reference;
    int RC_SIGN_IN = 1;
    File external_file;
    ValueEventListener valueEventListener;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    boolean isConnected;
    View parent;
    boolean isOpened = false;
    TextInputEditText phoneEdit,usernameEdit,bioEdit ,dnEdit;
    FrameLayout usernameEditView,phoneEditView,bioEditView ,dnEditView;
    LinearLayout editorsView;
    ExtendedFloatingActionButton updatePhone,updateBio,updateUsername ,updateDn;
    FloatingActionButton closeButton;
    ImageButton imageButton;
    StorageReference profilePhotoReference;
    FirebaseStorage firebaseStorage;


    FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user != null){
                OnSignedInInitialize(user.getDisplayName(),user.getEmail(),user.getPhotoUrl().toString(),user.getUid());
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

    private void OnSignOutCleanUp() {

    }

    private void OnSignedInInitialize(String displayName, String email, String toString, String uid) {
    }

    private void textWatcher(){
        phoneEdit.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        
    }

    private void editDisplayName(){
        final FirebaseUser user =  firebaseAuth.getCurrentUser();
        if(!isOpened){
            editorsView.bringToFront();
            editorsView.setVisibility(View.VISIBLE);
            dnEditView.setVisibility(View.VISIBLE);
            TranslateAnimation animation = new TranslateAnimation(0,0,parent.getHeight(),0);
            animation.setDuration(500);
            animation.setFillAfter(true);
            editorsView.startAnimation(animation);
            isOpened =true;
        }else{
            isOpened = false;
            editorsView.setVisibility(View.GONE);
            usernameEditView.setVisibility(View.GONE);
            phoneEditView.setVisibility(View.GONE);
            bioEditView.setVisibility(View.GONE);
            dnEditView.setVisibility(View.GONE);
            TranslateAnimation animation = new TranslateAnimation(0,0,0,parent.getHeight());
            animation.setDuration(500);
            animation.setFillAfter(true);
            editorsView.startAnimation(animation);
        }

        updateDn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpened = false;
                editorsView.setVisibility(View.GONE);
                usernameEditView.setVisibility(View.GONE);
                phoneEditView.setVisibility(View.GONE);
                bioEditView.setVisibility(View.GONE);
                dnEditView.setVisibility(View.GONE);
                DatabaseReference dbReference = databaseReference.child(user.getUid());
                String bio = dnEdit.getText().toString();

                dbReference.child("mDisplayName").setValue(bio);
                Toast.makeText(SettingsActivity.this,"DN Updated",Toast.LENGTH_SHORT).show();
                QueryDatabase();
                TranslateAnimation animation = new TranslateAnimation(0,0,0,parent.getHeight());
                animation.setDuration(500);
                animation.setFillAfter(true);
                editorsView.startAnimation(animation);
            }
        });
    }
    private void openEditor(){
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        usernameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpened){
                    editorsView.bringToFront();

                    editorsView.setVisibility(View.VISIBLE);
                    usernameEditView.setVisibility(View.VISIBLE);
                    TranslateAnimation animation = new TranslateAnimation(0,0,parent.getHeight(),0);
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    editorsView.startAnimation(animation);
                    isOpened =true;
                }else{
                    isOpened = false;
                    editorsView.setVisibility(View.GONE);
                    usernameEditView.setVisibility(View.GONE);
                    phoneEditView.setVisibility(View.GONE);
                    bioEditView.setVisibility(View.GONE);
                    dnEditView.setVisibility(View.GONE);
                    TranslateAnimation animation = new TranslateAnimation(0,0,0,parent.getHeight());
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    editorsView.startAnimation(animation);
                }
            }
        });

        updateUsername.extend();
        updateUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpened = false;
                editorsView.setVisibility(View.GONE);
                usernameEditView.setVisibility(View.GONE);
                phoneEditView.setVisibility(View.GONE);
                bioEditView.setVisibility(View.GONE);            dnEditView.setVisibility(View.GONE);
                DatabaseReference dbReference = databaseReference.child(user.getUid());
                String username = usernameEdit.getText().toString();
                if(!username.startsWith("@")){
                    username = "@"+username.toLowerCase();
                    dbReference.child("mUsername").setValue(username);
                    Toast.makeText(SettingsActivity.this,"Username changed to "+username,Toast.LENGTH_SHORT).show();

                }else if(username.contains(" ")){
                    username = username.replace(" ","_").toLowerCase();
                    dbReference.child("mUsername").setValue(username);
                    Toast.makeText(SettingsActivity.this,"Username changed to "+username,Toast.LENGTH_SHORT).show();

                }else if(username.isEmpty()|| username.equals("")){
                    Toast.makeText(SettingsActivity.this,"Username Unchanged",Toast.LENGTH_SHORT).show();
                }
                else if(!username.isEmpty()){
                    username = username.toLowerCase();
                    dbReference.child("mUsername").setValue(username);
                    Toast.makeText(SettingsActivity.this,"Username changed to "+username,Toast.LENGTH_SHORT).show();
                }
                QueryDatabase();
                TranslateAnimation animation = new TranslateAnimation(0,0,0,parent.getHeight());
                animation.setDuration(500);
                animation.setFillAfter(true);
                editorsView.startAnimation(animation);

            }
        });

        updateBio.extend();
        bioCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpened){
                    editorsView.bringToFront();

                    editorsView.setVisibility(View.VISIBLE);
                    bioEditView.setVisibility(View.VISIBLE);
                    TranslateAnimation animation = new TranslateAnimation(0,0,parent.getHeight(),0);
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    editorsView.startAnimation(animation);
                    isOpened =true;
                }else{
                    isOpened = false;

                    editorsView.setVisibility(View.GONE);
                    usernameEditView.setVisibility(View.GONE);
                    phoneEditView.setVisibility(View.GONE);
                    bioEditView.setVisibility(View.GONE);
                    dnEditView.setVisibility(View.GONE);
                    TranslateAnimation animation = new TranslateAnimation(0,0,0,parent.getHeight());
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    editorsView.startAnimation(animation);
                }
            }
        });

        updateBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpened = false;
                editorsView.setVisibility(View.GONE);
                usernameEditView.setVisibility(View.GONE);
                phoneEditView.setVisibility(View.GONE);
                bioEditView.setVisibility(View.GONE);
                dnEditView.setVisibility(View.GONE);

                DatabaseReference dbReference = databaseReference.child(user.getUid());
                String bio = bioEdit.getText().toString();

                    dbReference.child("mBiography").setValue(bio);
                    Toast.makeText(SettingsActivity.this,"Bio Updated",Toast.LENGTH_SHORT).show();
                QueryDatabase();
                TranslateAnimation animation = new TranslateAnimation(0,0,0,parent.getHeight());
                animation.setDuration(500);
                animation.setFillAfter(true);
                editorsView.startAnimation(animation);

            }
        });

        updatePhone.extend();
        phoneCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isOpened){
                    editorsView.bringToFront();

                    editorsView.setVisibility(View.VISIBLE);
                    phoneEditView.setVisibility(View.VISIBLE);
                    TranslateAnimation animation = new TranslateAnimation(0,0,parent.getHeight(),0);
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    editorsView.startAnimation(animation);
                    isOpened =true;
                }else{
                    isOpened = false;
                    editorsView.setVisibility(View.GONE);
                    usernameEditView.setVisibility(View.GONE);
                    phoneEditView.setVisibility(View.GONE);
                    bioEditView.setVisibility(View.GONE);
                    dnEditView.setVisibility(View.GONE);

                    TranslateAnimation animation = new TranslateAnimation(0,0,0,parent.getHeight());
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    editorsView.startAnimation(animation);
                }
            }
        });

        updatePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOpened = false;
                editorsView.setVisibility(View.GONE);
                usernameEditView.setVisibility(View.GONE);
                phoneEditView.setVisibility(View.GONE);
                bioEditView.setVisibility(View.GONE);
                dnEditView.setVisibility(View.GONE);

                DatabaseReference dbReference = databaseReference.child(user.getUid());
                String bio = phoneEdit.getText().toString();
                bio = PhoneNumberUtils.formatNumber(bio,"GH");
                dbReference.child("mPhone").setValue(bio);
                Toast.makeText(SettingsActivity.this,"Phone Updated",Toast.LENGTH_SHORT).show();
                QueryDatabase();
                TranslateAnimation animation = new TranslateAnimation(0,0,0,parent.getHeight());
                animation.setDuration(500);
                animation.setFillAfter(true);
                editorsView.startAnimation(animation);

            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOpened){
                    isOpened = false;
                    editorsView.setVisibility(View.GONE);
                    usernameEditView.setVisibility(View.GONE);
                    phoneEditView.setVisibility(View.GONE);
                    bioEditView.setVisibility(View.GONE);
                    dnEditView.setVisibility(View.GONE);
                    TranslateAnimation animation = new TranslateAnimation(0,0,0,parent.getHeight());
                    animation.setDuration(500);
                    animation.setFillAfter(true);
                    editorsView.startAnimation(animation);
                }
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(getDrawable(R.color.fui_transparent));
            actionBar.setTitle("");
        }



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("users");
        loadView();
        preferences = getSharedPreferences(PREF,Context.MODE_PRIVATE);
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        isConnected = networkInfo != null && networkInfo.isConnected();
        connectivityManager.addDefaultNetworkActiveListener(new ConnectivityManager.OnNetworkActiveListener() {
            @Override
            public void onNetworkActive() {
                QueryDatabase();
            }
        });

        FirebaseUser user = firebaseAuth.getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
        profilePhotoReference = firebaseStorage.getReference().child("user_profile_photos").child(user.getUid());
        openEditor();

        backgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, " Complete Action Using "), RC_PHOTO_PICKER);
            }
        });

        Random random = new Random();
        randomNumber = random.nextInt(9999);
        textWatcher();

    }

    private void loadView(){
        parent =findViewById(R.id.parent);
        backgroundImage = findViewById(R.id.background);
        avatar = findViewById(R.id.avatar);
        changePhoto = findViewById(R.id.upload);
        usernameCard= findViewById(R.id.usernameCard);
        display_name = findViewById(R.id.title);
        fullname = findViewById(R.id.subtitle);
        phoneCard = findViewById(R.id.phoneCard);
        bioCard = findViewById(R.id.bioCard);
        userName = findViewById(R.id.username);
        phoneNumber = findViewById(R.id.userphone);
        biography = findViewById(R.id.bio);
        editorsView = findViewById(R.id.editingViews);
        phoneEditView = findViewById(R.id.editPhoneView);
        usernameEditView = findViewById(R.id.editUsernameView);
        bioEditView = findViewById(R.id.editBioView);
        bioEdit = findViewById(R.id.bioEdit);
        usernameEdit = findViewById(R.id.usernameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        updateBio = findViewById(R.id.updateBioButton);
        updatePhone = findViewById(R.id.updatePhoneButton);
        updateUsername = findViewById(R.id.updateUsernameButton);
        closeButton = findViewById(R.id.close);
        dnEditView = findViewById(R.id.editDisplayNameView);
        dnEdit = findViewById(R.id.dnEdit);
        updateDn = findViewById(R.id.updateDnButton);
        imageButton = findViewById(R.id.upload);
    }

    private void QueryDatabase(){
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        final String uid = user.getUid();
        final User[] user1 = new User[1];
        reference = databaseReference.child(uid);
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
//                    user1[0] = dataSnapshot1.getValue(User.class);
//                }
                user1[0] = dataSnapshot.getValue(User.class);
                Log.d("MYSettings", String.valueOf(user1[0] ==null));
                savePref(user1[0].mDisplayName,user1[0].fullname,user1[0].mUsername,user1[0].mPhone,user1[0].mBiography,user1[0].mPhotoUrl);
                display_name.setText(user1[0].mDisplayName);
                fullname.setText(user1[0].fullname);
                userName.setText(user1[0].mUsername);
                phoneNumber.setText(user1[0].mPhone);
                biography.setText(user1[0].mBiography);
                Picasso.get().load(user1[0].mPhotoUrl)
                        .transform(new BlurTransformation(getApplicationContext(),25,1))
                        .into(backgroundImage);
                Picasso.get().load(user1[0].mPhotoUrl)
                        .transform(new CropSquareTransformation())
                        .into(avatar);
                if(phoneNumber.getText().toString() == null || phoneNumber.getText().toString().isEmpty()){
                    phoneNumber.setText("Add a Phone Number");
                }

                if(biography.getText().toString() == null || biography.getText().toString().isEmpty()){
                    biography.setText("Add a simple biography for others to know you more");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("Settings", "loadUserData:onCancelled",databaseError.toException());

            }
        };

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
//                    user1[0] = dataSnapshot1.getValue(User.class);
//                }
                user1[0] = dataSnapshot.getValue(User.class);
                savePref(user1[0].mDisplayName,user1[0].fullname,user1[0].mUsername,user1[0].mPhone,user1[0].mBiography,user1[0].mPhotoUrl);
                Log.d("MYSettings", String.valueOf(user1[0] ==null));
                try {
                    display_name.setText(user1[0].mDisplayName);
                    fullname.setText(user1[0].fullname);
                    userName.setText(user1[0].mUsername);
                    phoneNumber.setText(user1[0].mPhone);
                    biography.setText(user1[0].mBiography);
                    Picasso.get().load(user1[0].mPhotoUrl)
                            .fit().transform(new BlurTransformation(getApplicationContext(),25,1))
                            .into(backgroundImage);
                    Picasso.get().load(user1[0].mPhotoUrl)
                            .fit().into(avatar);
                    if(phoneNumber.getText().toString() == null || phoneNumber.getText().toString().isEmpty()){
                        phoneNumber.setText("Add a Phone Number");
                    }

                    if(biography.getText().toString() == null || biography.getText().toString().isEmpty()){
                        biography.setText("Add a simple biography for others to know you more");
                    }



                }catch (NullPointerException e){
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
                                Toast.makeText(SettingsActivity.this, "Welcome back "+name[0],Toast.LENGTH_LONG).show();
                            }else if (!dataSnapshot.child(USER_UID).exists()){
                                databaseReference.child(USER_UID).push().setValue(newUser);
                                Toast.makeText(SettingsActivity.this, "Welcome "+name[0]+" " +
                                        "Your username is "+USERNAME,Toast.LENGTH_LONG).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    reference.addValueEventListener(valueEventListener);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SaveProfilePic(String userPhotoUrl, String user_name) { Picasso.get()
            .load(userPhotoUrl)
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
            String filePath = "ProfilePictures";
            external_file= new File(getExternalFilesDir(filePath), user_name+".png");
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


    @Override
    protected void onStart() {
        super.onStart();
        readPref();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(valueEventListener != null){
            reference.removeEventListener(valueEventListener);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            preference =findPreference("ringtone");
            assert preference != null;
            preference.setSummary(RingtoneManager.getRingtone(getContext(),Uri.parse(getRingtonePreferenceValue())).getTitle(getContext()));
        }

        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            if (preference.getKey().equals("ringtone")) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                Uri
                        ringtone =
                        RingtoneManager.getActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtone);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_ALARM_ALERT_URI);
                String existingValue = getRingtonePreferenceValue();
                Log.d("RingtonePreference", getRingtonePreferenceValue());
                if (existingValue != null) {
                    if (existingValue.length() == 0) {
                        // Select "Silent"
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                    } else {
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(existingValue));
                    }
                } else {
                    // No ringtone has been selected, set to the default
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
                }

                startActivityForResult(intent, REQUEST_CODE_ALERT_RINGTONE);
                return true;
            } else {
                return super.onPreferenceTreeClick(preference);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                deleteCache(this);
                AuthUI.getInstance().signOut(this);
            case R.id.set_displayname:
                editDisplayName();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                FirebaseUser user = firebaseAuth.getCurrentUser();
                assert user != null;
            };
        } else if(requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK){
            Uri selectedImageUri = data.getData();
            StorageReference photoRef = profilePhotoReference.child(selectedImageUri.getLastPathSegment().trim());

            photoRef.putFile(selectedImageUri);

            profilePhotoReference.putFile(selectedImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return profilePhotoReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri  =task.getResult();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        DatabaseReference dbReference = databaseReference.child(user.getUid());
                        SharedPreferences pref = getSharedPreferences(PREF,Context.MODE_PRIVATE);
                        String un = pref.getString("username","Loading....");
                        Log.d("Activity Result",downloadUri+"");
                        dbReference.child("mPhotoUrl").setValue(downloadUri.toString());
                        SaveProfilePic(downloadUri.toString(),un);
                        QueryDatabase();
                    }
                }
            });
        } else if (resultCode==RESULT_OK && requestCode != RC_PHOTO_PICKER) {
                Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Log.d("ActivityResult", ringtone.toString());
            if (ringtone != null) {
                preference.setSummary(RingtoneManager.getRingtone(getApplicationContext(), ringtone).getTitle(getApplicationContext()));
                setRingtonePreferenceValue(ringtone.toString());
            } else {
                // "Silent" was selected
                setRingtonePreferenceValue("");
            }
        } else {
            Toast.makeText(getApplicationContext(), "Ringtone not set", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setRingtonePreferenceValue(String toString) {
        Toast.makeText(getApplicationContext(), "Ringtone set to " + RingtoneManager.getRingtone(getApplicationContext(), Uri.parse(toString)).getTitle(getApplicationContext()), Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor1;
        editor1 = preference.getSharedPreferences().edit();
        editor1.putString("general_ringtone", toString);
        editor1.commit();
    }

    private static String getRingtonePreferenceValue() {
        String
                preferredRingtone =
                preference.getSharedPreferences().getString("general_ringtone", "content://settings/system/alarm_sound");
        return preferredRingtone;

    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public void savePref(String displayname, String fullname, String username, String PhoneNumber, String Bio, String photoUrl){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("displayname",displayname);
        editor.putString("fullname",fullname);
        editor.putString("username",username);
        editor.putString("phonenumber",PhoneNumber);
        editor.putString("bio",Bio);
        editor.putString("photoUrl",photoUrl);
        editor.commit();

    }
    public void readPref(){
        SharedPreferences pref = getSharedPreferences(PREF,Context.MODE_PRIVATE);
        String dn = pref.getString("displayname","Loading....");
        String fn = pref.getString("fullname","Loading....");
        String un = pref.getString("username","Loading....");
        String pn = pref.getString("phonenumber","");
        String bio = pref.getString("bio","");
        String url = pref.getString("photoUrl","");
        String uri = pref.getString("dp","");
        Log.d("MYSettings",uri);

        display_name.setText(dn);
        fullname.setText(fn);
        userName.setText(un);
        phoneNumber.setText(pn);
        biography.setText(bio);
        if (!uri.isEmpty()||uri != null){
            File file = new File(uri);
        Picasso.get().load(file)
                .transform(new BlurTransformation(getApplicationContext(),25,1))
                .into(backgroundImage);
        Picasso.get().load(file)
                .into(avatar);
//        backgroundImage.setImageURI(null);
//        backgroundImage.setImageURI(Uri.parse(uri))
        ;
            try {
                backgroundImage.setImageDrawable(Drawable.createFromStream(getContentResolver().openInputStream(Uri.parse(uri)),null));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else{
            SaveProfilePic(url,un);
            Picasso.get().load(url)
                    .fit().transform(new BlurTransformation(getApplicationContext(),25,1))
                    .into(backgroundImage);
            Picasso.get().load(url)
                    .fit().into(avatar);
        }
        if(phoneNumber.getText().toString() == null || phoneNumber.getText().toString().isEmpty()){
            phoneNumber.setText("Add a Phone Number");
        }

        if(biography.getText().toString() == null || biography.getText().toString().isEmpty()){
            biography.setText("Add a simple biography for others to know you more");
        }


    }

}
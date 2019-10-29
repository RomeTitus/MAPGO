package com.example.mapgo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MainActivity extends AppCompatActivity {

    private ViewPager Pager;
    private LogInFragmentController myadapter;
    private LinearLayout status;
    private BottomNavigationView bottomNavigationView;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth auth;
    boolean register = false;
    private static final String TAG = "AddToDatabase";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.BottomNavigation);
        Pager = findViewById(R.id.ViewPager); //Creates Fragment


        Intent intent = new Intent(MainActivity.this, GoogleMaps.class);
        //intent.putExtra(EXTRA_MESSAGE, message);
        //startActivity(intent);
        //finish();



        auth = FirebaseAuth.getInstance();

        // Configure Google Sign In  https://firebase.google.com/docs/auth/android/google-signin
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);




        myadapter = new LogInFragmentController(this);
        Pager.setAdapter(myadapter);
        Pager.setCurrentItem(0);
        bottomNavigationView.setSelectedItemId(R.id.navigation_Login);

        Pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_Login);
                } else if (position == 1) {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_Create);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_Login:
                        Pager.setCurrentItem(0);
                        break;
                    case R.id.navigation_Create:
                        Pager.setCurrentItem(1);
                        break;
                }
                return true;
            }
        });

        myadapter.setCustomObjectListener(new LogInFragmentController.MyCustomObjectListener() {
            @Override
            public void onObjectReady(String title) {
                if(title.equals("GoogleSignInAuthenticator")){
                    setRegister(false);
                    signInGoogle();
                }else if(title.equals("GetIDGoogleSignInAuthenticator")){
                    setRegister(true);
                    signInGoogle();
                }else if(title.indexOf("SignIn#") !=-1){
                    String[] data = title.split("#");

                    auth.signInWithEmailAndPassword(data[1], data[2])
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information

                                        Toast.makeText(getApplicationContext(), "Welcome Back!", Toast.LENGTH_SHORT).show();
                                        //GoogleMaps map = new GoogleMaps(user);
                                        Intent intent = new Intent(MainActivity.this, GoogleMaps.class);
                                        //intent.putExtra(EXTRA_MESSAGE, message);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "The email or password is incorrect", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }else if(title.indexOf("SignUp#") !=-1){
                    String[] data = title.split("#");

                    auth.signInWithEmailAndPassword(data[1], data[2]);
                    auth.createUserWithEmailAndPassword(data[1], data[2])
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        //FirebaseUser user = mAuth.getCurrentUser();

                                        FirebaseDatabaseController databaseController = new FirebaseDatabaseController();

                                        databaseController.createUser(myadapter.createUserObject(), auth.getUid());
                                        Intent intent = new Intent(MainActivity.this, GoogleMaps.class);
                                        //intent.putExtra(EXTRA_MESSAGE, message);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());

                                        //updateUI(null);
                                    }

                                    // ...
                                }
                            });
                }
            }
        });



    }

    // Read from the database
    public void doesUserExist(final String userID){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        boolean userExist = false;

        myRef.child("Users").child(userID).orderByChild("userId").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<User> UserList = new ArrayList<User>();
                //deal with data object
                User user = dataSnapshot.getValue(User.class);
                if(user != null){
                    Toast.makeText(getApplicationContext(), "Welcome Back!", Toast.LENGTH_SHORT).show();
                    //GoogleMaps map = new GoogleMaps(user);
                    Intent intent = new Intent(MainActivity.this, GoogleMaps.class);
                    //intent.putExtra(EXTRA_MESSAGE, message);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Please Register :)", Toast.LENGTH_SHORT).show();
                    Pager.setCurrentItem(1);

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                //toastMsg(databaseError.getMessage());
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

    }



















    private void setRegister(boolean register){
        this.register = register;
    }

    //https://firebase.google.com/docs/auth/android/google-signin
    private void signInGoogle() {
        auth.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 637);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        final GoogleSignInAccount account1 = account;
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if(register == true){
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = auth.getCurrentUser();
                                User signUpUser = myadapter.createUserObject(user.getEmail());
                                Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
                                FirebaseDatabaseController firebaseDatabaseController = new FirebaseDatabaseController();
                                firebaseDatabaseController.createUser(signUpUser, user.getUid());
                                Intent intent = new Intent(MainActivity.this, GoogleMaps.class);
                                //intent.putExtra(EXTRA_MESSAGE, message);
                                startActivity(intent);
                                finish();

                                //updateUI(user);
                            }else{
                                // Sign in success, update UI with the signed-in user's information
                                //Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = auth.getCurrentUser();
                                //FirebaseDatabaseController FirebaseDatabase = new FirebaseDatabaseController();
                                myadapter.setHintName(account1.getDisplayName());
                                doesUserExist(user.getUid());
                                //Toast.makeText(getApplicationContext(), "Successfully Signed In", Toast.LENGTH_SHORT).show();
                                //

                                //updateUI(user);
                            }
                        } else {

                            Toast.makeText(getApplicationContext(), "Opps thats not supposed to happen, lets try again", Toast.LENGTH_SHORT).show();
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }


    //https://firebase.google.com/docs/auth/android/google-signin
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 637) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if(account!= null){
                    firebaseAuthWithGoogle(account);
                }

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }



}

package com.example.mapgo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.concurrent.Executor;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class LogInFragmentController extends PagerAdapter {
    private String prefferedMode = "CAR";
    private Context context;
    private LayoutInflater inflater;
    public View view, registerView;

    private FirebaseAuth mAuth;
    private MyCustomObjectListener GoogleSignIn;
    private SignInButton signInButton, signUpButton;
    private ImageView imageCar, imageCycle, imageWalk;
    private TextView TxtName, TextEmailLogin, TextPasswordLogin, TextEmailRegister, TextPasswordRegister;
    private Switch switchImperial;
    private Button ButtonSignIn;


    public LogInFragmentController (Context context){
        this.context = context;
    }

    public interface MyCustomObjectListener { //used to send the delay time
        // need to pass relevant arguments related to the event triggered
        void onObjectReady(String title);

    }

    public void setCustomObjectListener(MyCustomObjectListener listener) {
        this.GoogleSignIn = listener;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == object);
    }

    //variables to be used in the other User Interfaces
    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, int position) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        //different positions give different pages to show
        if (position == 0) {
            view = inflater.inflate(R.layout.fragment_login, container, false);
            TextEmailLogin = view.findViewById(R.id.textEmailLogin);
            TextPasswordLogin = view.findViewById(R.id.textPasswordLogin);
            signInButton = view.findViewById(R.id.GoogleSignIn);
            ButtonSignIn = view.findViewById(R.id.ButtonSignIn);

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoogleSignIn.onObjectReady("GoogleSignInAuthenticator");
                }
            });

            ButtonSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   if(!(TextEmailLogin.getText().toString()).equals("") && !(TextPasswordLogin.getText().toString()).equals("")){

                        GoogleSignIn.onObjectReady("SignIn#"+ TextEmailLogin.getText().toString() + "#" + TextPasswordLogin.getText().toString() );
                    }

                }
            });
        }

        else{
            view = inflater.inflate(R.layout.fragment_create_account, container, false);
            signUpButton = view.findViewById(R.id.GoogleCreateAccount);
            registerView = view;
            TxtName = view.findViewById(R.id.TxtName);
            switchImperial = view.findViewById(R.id.switchImperial);
            imageCar = view.findViewById(R.id.imageCarTransport);
            imageCycle = view.findViewById(R.id.imageCycleTransport);
            imageWalk = view.findViewById(R.id.imageWalkTransport);
            TextEmailRegister = view.findViewById(R.id.textEmailRegister);
            TextPasswordRegister = view.findViewById(R.id.textPasswordRegister);

           ButtonSignIn = view.findViewById(R.id.buttonSignInRegister);

            imageCar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefferedMode = "CAR";
                    imageCar.setBackgroundResource(R.drawable.circle);
                    imageCycle.setBackgroundResource(R.color.colorPrimary);
                    imageWalk.setBackgroundResource(R.color.colorPrimary);
                }
            });

            imageCycle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefferedMode = "CYCLE";
                    imageCar.setBackgroundResource(R.color.colorPrimary);
                    imageCycle.setBackgroundResource(R.drawable.circle);
                    imageWalk.setBackgroundResource(R.color.colorPrimary);
                }
            });

            imageWalk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefferedMode = "WALK";
                    imageCar.setBackgroundResource(R.color.colorPrimary);
                    imageCycle.setBackgroundResource(R.color.colorPrimary);
                    imageWalk.setBackgroundResource(R.drawable.circle);
                }
            });
            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GoogleSignIn.onObjectReady("GetIDGoogleSignInAuthenticator");
                }
            });


            ButtonSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(TextPasswordRegister.getText().toString().length()>= 0){
                        GoogleSignIn.onObjectReady("SignUp#"+ TextEmailRegister.getText().toString() + "#" + TextPasswordRegister.getText().toString() );
                    }else{
                        Toast.makeText(context, "The password needs to be greater than 5", Toast.LENGTH_SHORT).show();
                    }


                }
            });

        }


        container.addView(view);
        return view;
    }

    public void setHintName(String name){
        try {
            TxtName = registerView.findViewById(R.id.TxtName);
            TxtName.setHint(name);
        }catch (Exception e){

        }

    }

    public User createUserObject(String email){
        User user = new User();
        user.setUser_Email(email);
        user.setUser_isImperial(Boolean.toString(switchImperial.isChecked()));
        user.setUser_Name(TxtName.getText().toString());
        user.setUser_StartTransport(prefferedMode);
        return user;

    }
    public User createUserObject(){
        User user = new User();
        user.setUser_Email(TextEmailRegister.getText().toString());
        user.setUser_isImperial(Boolean.toString(switchImperial.isChecked()));
        user.setUser_Name(TxtName.getText().toString());
        user.setUser_StartTransport(prefferedMode);
        return user;

    }




}

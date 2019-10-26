package com.example.mapgo;

import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseController {
    // Write a message to the database
    private static final String TAG = "AddToDatabase";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();


    public void createUser(User user, String ID){
    myRef.child("Users").child(ID).setValue(user);
    }

    // Read from the database
    public boolean doesUserExist(final String userID){

        boolean userExist = false;

        myRef.child("Users").child(userID).orderByChild("userId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<User> UserList = new ArrayList<User>();
                //deal with data object
                UserList.add(dataSnapshot.getValue(User.class));

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                //toastMsg(databaseError.getMessage());
                Log.e(TAG, "onCancelled: " + databaseError.getMessage());
            }
        });

        return userExist;
    }



}

package com.graspery.www.spicemeup.Firebase;

import android.app.Activity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import androidx.annotation.NonNull;
import info.movito.themoviedbapi.model.MovieDb;

public class FirebaseDatabaseHelper {
    private Activity activity;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private boolean isRegisteredUser;

    public FirebaseDatabaseHelper(Activity activity) {
        reference = FirebaseDatabase.getInstance().getReference("movies");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        this.activity = activity;

        isRegisteredUser = false;
        isRegistered();
    }


    public FirebaseUser getCurrentUser() {
        return currentUser;
    }

    public boolean isRegisteredUser() {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            return true;
        }

        return false;
    }



    public void isRegistered() {
        currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(currentUser.getUid())) {
                        isRegisteredUser = true;
                    } else {
                        isRegisteredUser = false;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public boolean registerNewUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            currentUser = mAuth.getCurrentUser();
                            isRegisteredUser = true;
                            reference.child("users").child(currentUser.getUid());
                        } else {
                            isRegisteredUser = false;
                        }

                    }
                });

        return isRegisteredUser;
    }


    public String getUserId() {
        return currentUser.getUid();
    }

    public FirebaseUser getUser() {
        return currentUser;
    }

    public void addAlreadyWatchedListMovie(MovieDb movie) {
        if(currentUser != null) {
            HashMap<String, String> movieData = new HashMap<>();
            movieData.put(movie.getOriginalTitle(), movie.getPosterPath());
            reference.child("users").child(currentUser.getUid()).child("already_watched").child(movie.getId() + "").setValue(movieData);
        }
    }

    public void addWatchListMovie(MovieDb movie) {
        if(currentUser != null) {
            HashMap<String, String> movieData = new HashMap<>();
            movieData.put(movie.getOriginalTitle(), movie.getPosterPath());
            reference.child("users").child(currentUser.getUid()).child("watch_list").child(movie.getId() + "").setValue(movieData);
        }
    }

    public void removeAlreadyWatchedListMovie(MovieDb movie) {
        if(currentUser != null) {
            reference.child("users").child(currentUser.getUid()).child("already_watched").child(movie.getId() + "").removeValue();
        }
    }

    public void removeWatchListMovie(MovieDb movie) {
        if(currentUser != null) {
            reference.child("users").child(currentUser.getUid()).child("watch_list").child(movie.getId() + "").removeValue();
        }
    }

}

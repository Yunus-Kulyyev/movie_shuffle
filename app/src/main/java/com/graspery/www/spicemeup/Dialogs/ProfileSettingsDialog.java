package com.graspery.www.spicemeup.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graspery.www.spicemeup.CustomAdapters.ArchiveListAdapter;
import com.graspery.www.spicemeup.CustomAdapters.GenreListViewAdapter;
import com.graspery.www.spicemeup.Firebase.FirebaseDatabaseHelper;
import com.graspery.www.spicemeup.Models.ArchiveModelMovie;
import com.graspery.www.spicemeup.Platforms.NetflixActivity;
import com.graspery.www.spicemeup.R;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

public class ProfileSettingsDialog extends Dialog {

    private Activity c;
    private CardView signOutButton;
    private CardView deleteAccount;
    private FirebaseUser currentUser;
    private FirebaseDatabaseHelper mFirebaseDatabaseHelper;

    ImageView backBtn;
    private LinearLayout profileMenu;
    CardView watchListPage;
    CardView alreadyWatched;

    ListView wishOrWatchedList;
    DatabaseReference reference;
    ArrayList<ArchiveModelMovie> movieListModel;

    Animation left;
    Animation leftToRigh;
    Animation centerToRigh;
    Animation right;

    TextView navTitle;
    TextView watchText;
    TextView watchedText;

    LinearLayout listViewLayout;
    AutoCompleteTextView mAutoCompleteTextView;
    MovieInfoDialog movieInfoDialog;

    public ProfileSettingsDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.profile_layout);

        getWindow().getAttributes().windowAnimations = R.style.CoolDialogAnimation;
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        reference = FirebaseDatabase.getInstance().getReference("movies");

        profileMenu = findViewById(R.id.profile_main_menu);

        mFirebaseDatabaseHelper = new FirebaseDatabaseHelper(c);
        currentUser = mFirebaseDatabaseHelper.getCurrentUser();

        getStats();

        navTitle = findViewById(R.id.navigation_title);
        navTitle.setText("Back");

        listViewLayout = findViewById(R.id.listview_layout_inlayout);

        backBtn = findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(c, "Signed out successfully", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        deleteAccount = findViewById(R.id.delete_account);
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                builder.setTitle("Account");
                builder.setMessage("Are you sure you want to remove your account?");
                // Set the alert dialog yes button click listener
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        wishOrWatchedList = findViewById(R.id.wish_or_watched_list);

        leftToRigh = AnimationUtils.loadAnimation(c, R.anim.slide_left_to_right);
        centerToRigh = AnimationUtils.loadAnimation(c, R.anim.slide_center_to_right);
        centerToRigh.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
               // wishOrWatchedList.setVisibility(View.GONE);
                listViewLayout.setVisibility(View.GONE);
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        navTitle.setText("Back");
                        dismiss();
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        right = AnimationUtils.loadAnimation(c, R.anim.slide_far_left);

        left = AnimationUtils.loadAnimation(c, R.anim.slide_left);
        left.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                profileMenu.setVisibility(View.GONE);
                listViewLayout.setVisibility(View.VISIBLE);
                //wishOrWatchedList.setVisibility(View.VISIBLE);
                wishOrWatchedList.startAnimation(right);
                mAutoCompleteTextView.startAnimation(right);

                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        wishOrWatchedList.startAnimation(centerToRigh);
                        mAutoCompleteTextView.startAnimation(centerToRigh);
                        profileMenu.setVisibility(View.VISIBLE);
                        profileMenu.startAnimation(leftToRigh);

                        navTitle.setText("Back");
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        watchListPage = findViewById(R.id.watch_list_page);
        watchListPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navTitle.setText("Watch List");
                populateList("watch_list");
            }
        });

        alreadyWatched = findViewById(R.id.already_watched_list_page);
        alreadyWatched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navTitle.setText("Already Watched");
                populateList("already_watched");
            }
        });

        mAutoCompleteTextView = findViewById(R.id.autocomplete_search_archive);
    }

    public void deleteUser() {
        currentUser.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(c, "Deleted successfully", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(c, "Failed to delete. Please try again later.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //currentUser = mAuth.getCurrentUser();
    }

    private void getStats() {
        watchText = findViewById(R.id.watch_list_count);
        watchedText = findViewById(R.id.watched_list_count);

        String type[] = {"watch_list", "already_watched"};
        for(int i = 0; i < type.length; i++) {
            reference.child("users").child(currentUser.getUid()).child(type[i]).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getKey().equals("watch_list")) {
                        watchText.setText("Wish list: " + dataSnapshot.getChildrenCount() + "");
                    } else {
                        watchedText.setText("My collection: " + dataSnapshot.getChildrenCount() + "");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void populateList(String type) {
        movieListModel = new ArrayList<>();
        movieListModel.clear();

        reference.child("users").child(currentUser.getUid()).child(type).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> titles = new ArrayList<>();
                HashMap<String, HashMap<String, String>> tracker = new HashMap<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                   /*ArchiveModelMovie movieArchive = new ArchiveModelMovie(snapshot.getKey(), snapshot.getValue().toString(),
                            snapshot.child(snapshot.getValue().toString()).getValue().toString());
                   movieListModel.add(movieArchive);*/
                    HashMap<String, String> result = (HashMap) snapshot.getValue();
                    tracker.put(snapshot.getKey(), result);
                    for(String key : result.keySet()) {
                        ArchiveModelMovie movieArchive = new ArchiveModelMovie(snapshot.getKey(), key, result.get(key));
                        movieListModel.add(movieArchive);
                        titles.add(key);
                    }
                }

                //Toast.makeText(c, "SIZE: " + movieListModel.get(0), Toast.LENGTH_SHORT).show();
                ArchiveListAdapter customAdapter = new ArchiveListAdapter(c, R.layout.archive_row, movieListModel);
                wishOrWatchedList.setAdapter(customAdapter);


                String[] arr = titles.toArray(new String[titles.size()]);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(c,android.R.layout.simple_selectable_list_item, arr);

                mAutoCompleteTextView.setAdapter(adapter);
                mAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(movieInfoDialog != null && movieInfoDialog.isShowing()) {
                            movieInfoDialog.dismiss();
                        }

                        String searchedKey = "";
                        String movieName= "";
                        String moviePoster = "";
                        for(String key : tracker.keySet()) {
                            HashMap<String, String> inner = tracker.get(key);
                            for(String innerKey : inner.keySet()) {
                                if(innerKey.equals(adapter.getItem(position))) {
                                    searchedKey = key;
                                    movieName = innerKey;
                                    moviePoster = inner.get(innerKey);
                                }
                            }
                        }

                        Toast.makeText(c, "Pos:  " + position, Toast.LENGTH_SHORT).show();

                        ArchiveModelMovie movie = new ArchiveModelMovie(searchedKey, movieName, moviePoster);
                        movieInfoDialog = new MovieInfoDialog(c, movie);
                        movieInfoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        View view2 = View.inflate(c, R.layout.movie_info_dialog, null);
                        movieInfoDialog.setContentView(view2);
                        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(((View) view2.getParent()));
                        //bottomSheetBehavior.setPeekHeight((int)(height/(1.1)));
                        movieInfoDialog.show();
                    }
                });


                profileMenu.startAnimation(left);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void searchHelper() {

    }
}
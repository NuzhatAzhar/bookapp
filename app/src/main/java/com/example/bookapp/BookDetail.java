package com.example.bookapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookDetail extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference Bookreference;
    DatabaseReference Usersreference;
    DatabaseReference viewRef;
    FirebaseAuth auth;
    ImageView image;
    TextView name;
    Button rate_btn;
    float rating;
    RatingBar ratingBar;
    float average;
    int count = 0;
    float totalRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        image = findViewById(R.id.detail_image);
        name = findViewById(R.id.detail_name);
        rate_btn = findViewById(R.id.rate_btn);
        ratingBar = findViewById(R.id.rating);

        auth = FirebaseAuth.getInstance();


        Bundle bundle = getIntent().getExtras();
        final String bookId = bundle.getString("bookId");

        database = FirebaseDatabase.getInstance();
        Bookreference = database.getReference("books").child(bookId);
        viewRef = database.getReference("books").child(bookId).child("views");
        Usersreference = database.getReference("users").child(auth.getCurrentUser().getUid());


        rate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rating = ratingBar.getRating();

                Usersreference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        User user = dataSnapshot.getValue(User.class);
                        String uid = user.getUid();
                        String uname = user.getName();

                        BookRating bookRating = new BookRating(uid, uname, rating);
                        Bookreference.child("bookRating").child(uid).setValue(bookRating);


                        //average

                        Bookreference.child("bookRating").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                BookRating bookRating1 = dataSnapshot.getValue(BookRating.class);
                                totalRating += bookRating1.getRating();
                                count++;
                                average = totalRating / count;

                                Bookreference.child("avgRating").setValue(average);
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        Bookreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Book book = dataSnapshot.getValue(Book.class);
                name.setText(book.getName());
                Glide.with(BookDetail.this).load(book.getImage()).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        viewRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                long viewCount=dataSnapshot.getChildrenCount();

                for(DataSnapshot data : dataSnapshot.getChildren()){
                    if (data.getValue().equals(auth.getCurrentUser().getUid())){
                        Bookreference.child("viewCount").setValue(viewCount);
                        return;
                    }

                }

                viewRef.push().setValue(auth.getCurrentUser().getUid());
                Bookreference.child("viewCount").setValue(viewCount);
              //  viewRef.push().setValue(auth.getCurrentUser().getUid());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void logout(View view) {

        auth.signOut();
        startActivity(new Intent(this, MainActivity.class));
    }
}

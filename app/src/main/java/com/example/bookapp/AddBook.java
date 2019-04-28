package com.example.bookapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class AddBook extends AppCompatActivity {

    private static final int Pick_image = 1;
    EditText book_name;
    EditText book_price;
    EditText book_writer;
    Button add_book;
    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser user;
    float avarge_book;
    DatabaseReference bookRef;
    DatabaseReference bookRatingRef;
    private Uri imageUri;
    ImageView image;
    FirebaseStorage storage;
    StorageReference imageRef;
    String bookId;
    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        init();

        Bundle bundle = getIntent().getExtras();
        bookId = bundle.getString("bookId");


        bookRef.child(bookId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Book book = dataSnapshot.getValue(Book.class);
                avarge_book = book.getAvgRating();
                book_name.setText(book.getName());
                book_price.setText(book.getPrice());

                book_writer.setText(book.getWriter());
                Glide.with(AddBook.this).load(book.getImage()).into(image);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        add_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = book_name.getText().toString();
                String price = book_price.getText().toString();
                String writer = book_writer.getText().toString();

                key=bookId;
                if (bookId == null) {
                    key = bookRef.push().getKey();
                }
                imageRef = storage.getReference("book images/" + key);

                uploaIMage(name, price, writer, key);

            }
        });

    }

    private void uploaIMage(final String name, final String price, final String writer, final String bookId) {

        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();

        imageRef.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if ((task.isSuccessful())) {
                            String imageUrl = task.getResult().toString();

                            bookRatingRef=bookRef.child(bookId).child("bookRating");

                            if (bookRatingRef!=null) {
                                bookRatingRef.addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                        BookRating bookRating = dataSnapshot.getValue(BookRating.class);
                                        String ratingKey = dataSnapshot.getKey();
                                        bookRatingRef.child(ratingKey).setValue(bookRating);

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

                            Book book = new Book(bookId, name, price, writer, imageUrl);
                            bookRef.child(bookId).setValue(book);
                            bookRef.child(bookId).child("avgRating").setValue(avarge_book);
                            startActivity(new Intent(AddBook.this, BookList.class));
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void init() {

        book_name = findViewById(R.id.add_book_name);
        book_price = findViewById(R.id.add_book_price);
        book_writer = findViewById(R.id.add_book_writer);
        add_book = findViewById(R.id.add_book_btn);
        image = findViewById(R.id.addbook_image);
        storage = FirebaseStorage.getInstance();


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        bookRef = firebaseDatabase.getReference("books");

    }

    public void opengallery(View view) {

        Intent gallery = new Intent();
        gallery.setAction(Intent.ACTION_PICK);
        gallery.setType("image/*");
        startActivityForResult(Intent.createChooser(gallery, "Select"), Pick_image);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData();
                image.setImageURI(imageUri);
            }
        }
    }
}

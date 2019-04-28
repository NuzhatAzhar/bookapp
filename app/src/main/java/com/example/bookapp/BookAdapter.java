package com.example.bookapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookViewHolder> {

    ArrayList<Book> books;
    Context context;
    FirebaseDatabase database;
    DatabaseReference reference;
    DatabaseReference bookRef;

    public BookAdapter(ArrayList<Book> books, Context context) {
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.book_item, viewGroup, false);
        BookViewHolder bookViewHolder = new BookViewHolder(view);
        return bookViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final BookViewHolder bookViewHolder, final int i) {

        final Book book = books.get(i);
        database = FirebaseDatabase.getInstance();

        reference = database.getReference().child("books");

        bookRef = reference.child(book.getBookId());

        if (bookRef != null) {

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    DecimalFormat decimalFormat = new DecimalFormat("#.#");

                    if (dataSnapshot.hasChild("avgRating")) {
                        float avg = dataSnapshot.child("avgRating").getValue(Float.class);
                        bookViewHolder.price.setText(decimalFormat.format(avg));
                    }
                    bookViewHolder.view.setText(String.valueOf(dataSnapshot.child("viewCount").getValue(Long.class)));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        bookViewHolder.name.setText(book.getName());


        Glide.with(context).load(book.getImage()).into(bookViewHolder.image);
        bookViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("bookId", book.getBookId());
                Intent intent = new Intent(context, BookDetail.class);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
        bookViewHolder.img_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showpopupmenu(bookViewHolder.img_menu, book.getBookId(),i);
            }
        });
    }

    private void showpopupmenu(ImageView img_menu, final String bookId, final int i) {
        PopupMenu popupMenu = new PopupMenu(context, img_menu);
        MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.edit, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.edit_menu:
                        update(bookId);
                        break;

                    case R.id.delete_menun:
                        delete(i);
                }
                return false;
            }
        });

        popupMenu.show();

    }

    private void delete(int i) {

        bookRef.setValue(null);
        books.remove(i);
        notifyDataSetChanged();
    }

    private void update(String bookId) {


        Intent intent = new Intent(context, AddBook.class);
        Bundle bundle = new Bundle();
        bundle.putString("bookId", bookId);
        intent.putExtras(bundle);
        context.startActivity(intent);


    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}

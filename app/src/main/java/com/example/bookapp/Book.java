package com.example.bookapp;

public class Book {

    String bookId;
    String name;
    String price;
    String writer;
    String image;
    float avgRating;

    public Book(String bookId, String name, String price, String writer,String image) {
        this.bookId = bookId;
        this.name = name;
        this.price = price;
        this.writer = writer;
        this.image = image;


    }

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public Book() {
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }
}

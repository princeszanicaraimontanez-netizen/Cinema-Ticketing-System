/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.model;

/**
 * Entity model representing a film in the cinema catalogue.
 * 
 * Architectural Connection in System:
 * - Layer: Model / Domain Entity Layer (com.movie.model)
 * - Mapping: Maps 1:1 to the `movies` table in MySQL.
 * - Transport: Passed across DAO, Service, Controller, and View layers for catalogue rendering.
 */
public class Movie {
   private int movieId;
    private String title;
    private String genre;
    private int duration;       // minutes
    private String rating;      // PG, R, etc.
    private String synopsis;
    private double price;
    private String status;      // "NOW_SHOWING" or "COMING_SOON"

    public Movie() {}

    public Movie(int movieId, String title, String genre, int duration,
                 String rating, String synopsis, double price, String status) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.rating = rating;
        this.synopsis = synopsis;
        this.price = price;
        this.status = status;
    }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() { return title; }  
}

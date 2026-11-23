/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.model;

/**
 * Entity model representing a cinema auditorium/screening room.
 * 
 * Architectural Connection in System:
 * - Layer: Model / Domain Entity Layer (com.movie.model)
 * - Mapping: Maps 1:1 to the `halls` table in MySQL.
 * - Transport: Used by HallDAO, HallService, Showtime scheduling, and SeatMapFrame.
 */
public class Hall {
   private int hallId;
    private String hallName;
    private int totalSeats;

    public Hall() {}

    public Hall(int hallId, String hallName, int totalSeats) {
        this.hallId = hallId;
        this.hallName = hallName;
        this.totalSeats = totalSeats;
    }

    public int getHallId() { return hallId; }
    public void setHallId(int hallId) { this.hallId = hallId; }

    public String getHallName() { return hallName; }
    public void setHallName(String hallName) { this.hallName = hallName; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    @Override
    public String toString() {
        return hallName + " (" + totalSeats + " seats)";
    } 
}

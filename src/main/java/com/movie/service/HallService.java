/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.movie.service;

import com.movie.dao.HallDAO;
import com.movie.model.Hall;

import java.util.List;

/**
 * Service managing cinema halls and seating capacity.
 * 
 * Architectural Connection in System:
 * - Layer: Service / Business Logic Layer (com.movie.service)
 * - Upstream: Invoked by AdminDashboardController (hall setup) and ShowtimeService.
 * - Downstream: Delegates SQL operations to HallDAO.
 * - Validation: Validates hall names and physical seat capacity bounds (1 to 500).
 */
public class HallService {

    private final HallDAO hallDAO;

    public HallService() {
        this.hallDAO = new HallDAO();
    }

    public HallService(HallDAO hallDAO) {
        this.hallDAO = hallDAO;
    }

    public boolean addHall(String hallName, int totalSeats) {
        validateHall(hallName, totalSeats);
        Hall h = new Hall(0, hallName.trim(), totalSeats);
        return hallDAO.insert(h);
    }

    public boolean updateHall(int hallId, String hallName, int totalSeats) {
        if (hallId <= 0) {
            throw new IllegalArgumentException("Invalid hall ID.");
        }
        validateHall(hallName, totalSeats);
        Hall h = new Hall(hallId, hallName.trim(), totalSeats);
        return hallDAO.update(h);
    }

    public boolean deleteHall(int hallId) {
        if (hallId <= 0) {
            throw new IllegalArgumentException("Invalid hall ID.");
        }
        return hallDAO.delete(hallId);
    }

    public List<Hall> getAllHalls() {
        return hallDAO.getAll();
    }

    public Hall getHallById(int hallId) {
        return hallDAO.findById(hallId);
    }

    private void validateHall(String hallName, int totalSeats) {
        if (hallName == null || hallName.trim().isEmpty()) {
            throw new IllegalArgumentException("Cinema hall name is required.");
        }
        if (totalSeats <= 0) {
            throw new IllegalArgumentException("Total seats must be a positive number.");
        }
    }
}

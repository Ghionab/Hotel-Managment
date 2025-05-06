package com.hotel.dao;

import com.hotel.model.Room;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface RoomDAO {
    Optional<Room> findById(int roomNumber) throws SQLException;
    List<Room> findAll() throws SQLException;
    boolean addRoom(Room room) throws SQLException;
    boolean updateRoom(Room room) throws SQLException;
    boolean deleteRoom(int roomNumber) throws SQLException;
    List<Room> findAvailableRooms() throws SQLException; // Example specific query
    
    // Dashboard methods
    int getTotalRoomCount() throws SQLException;
    int getAvailableRoomCount() throws SQLException;
    int getOccupiedRoomCount() throws SQLException;
    int getCheckedInGuestsCount() throws SQLException;
    int getCheckOutsDueToday() throws SQLException;
    int getExpectedCheckInsToday() throws SQLException;
    int getReservationsToday() throws SQLException;
    int getNewBookingsToday() throws SQLException;
    
    // Room status update
    boolean updateRoomStatus(int roomNumber, String newStatus) throws SQLException;
} 
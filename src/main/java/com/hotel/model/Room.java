package com.hotel.model;

public class Room {
    private int roomNumber;
    private String type;
    private String status;
    private double price;
    private int floor;

    // Constructors
    public Room() {}

    public Room(int roomNumber, String type, String status, double price, int floor) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.status = status;
        this.price = price;
        this.floor = floor;
    }

    public Room(int roomNumber, String type, String status) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.status = status;
        this.price = 0.0; // Default price
        this.floor = roomNumber / 100; // Derive floor from room number
    }

    // Getters and Setters
    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " (" + type + ", Price: " + price + ", Floor: " + floor + ", Status: " + status + ")";
    }
}
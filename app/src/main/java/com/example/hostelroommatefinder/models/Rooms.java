package com.example.hostelroommatefinder.models;

public class Rooms {
    private String roomId;
    private String location;
    private int capacity;
    private int vacancies;
    private String description;
    private String phoneNumber;
    private double latitude;
    private double longitude;
    // Default constructor (required for Firebase)
    public Rooms() {}

    // Constructor with parameters
    public Rooms(String roomId, String location, int capacity, int vacancies, String description) {
        this.roomId = roomId;
        this.location = location;
        this.capacity = capacity;
        this.vacancies = vacancies;
        this.description = description;
    }
    public Rooms(String roomId, String location, int capacity, int vacancies) {
        this.roomId = roomId;
        this.location = location;
        this.capacity = capacity;
        this.vacancies = vacancies;

    }
    public Rooms(String roomId, String location, int capacity, int vacancies, String description,
                 String phoneNumber, double latitude, double longitude) {
        this.roomId = roomId;
        this.location = location;
        this.capacity = capacity;
        this.vacancies = vacancies;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public Rooms(String roomId, String location, int capacity, int vacancies, double latitude, double longitude) {
        this.roomId = roomId;
        this.location = location;
        this.capacity = capacity;
        this.vacancies = vacancies;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public Rooms(String roomId, String location, int capacity, int vacancies,String phoneNumber, double latitude, double longitude) {
        this.roomId = roomId;
        this.location = location;
        this.capacity = capacity;
        this.vacancies = vacancies;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phoneNumber = phoneNumber;
    }
    public String getPhoneNumber() { return phoneNumber; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    // Getters
    public String getRoomId() { return roomId; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public int getVacancies() { return vacancies; }
    public String getDescription() { return description; }

    // Setters (Optional, if needed)
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public void setLocation(String location) { this.location = location; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setVacancies(int vacancies) { this.vacancies = vacancies; }
    public void setDescription(String description) { this.description = description; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public String getContact() {
        return phoneNumber ;
    }

    public void setContact(String contact) {
        this.phoneNumber= contact;
    }

}

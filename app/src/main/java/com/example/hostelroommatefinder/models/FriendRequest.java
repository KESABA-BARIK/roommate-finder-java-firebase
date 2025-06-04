package com.example.hostelroommatefinder.models;

public class FriendRequest {

    private String fromUserId;
    private String toUserId;
    private String status; // pending, accepted, rejected
    private long timestamp;
    private String name;
    // Required empty constructor for Firestore deserialization
    public FriendRequest() {}

    public FriendRequest(String fromUserId, String toUserId, String status,String Name) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.status = status;
        this.timestamp = System.currentTimeMillis(); // Store request time
        this.name = Name;
    }

    public String getFromUserId() {
        return fromUserId;
    }
    public String getFromName() {
        return name;
    }


    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public void setName(String status) {
        this.name = status;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

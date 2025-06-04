package com.example.hostelroommatefinder.models;

public class Message {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String messageText;
    private long timestamp;

    // **Default Constructor (Required for Firebase)**
    public Message() {}

    // **Parameterized Constructor**
    public Message(String messageId, String senderId, String receiverId, String messageText, long timestamp) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.timestamp = timestamp;
    }

    // **Getters**
    public String getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getMessageText() { return messageText; }
    public long getTimestamp() { return timestamp; }

    // **Setters (Optional)**
    public void setMessageId(String messageId) { this.messageId = messageId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    public void setMessageText(String messageText) { this.messageText = messageText; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

package com.example.hostelroommatefinder.models;

public class User {
    private String userId;
    private String name;
    private String email;
    private int age;
    private String gender;
    private String interests;
    private String preferences;
    private String profileImage; // 🖼️ Added profile image

    // ✅ **Constructor with all fields including profile image**
    public User(String userId, String name, int age, String gender, String interests,
                String preferences, String email, String profileImage) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.interests = interests;
        this.preferences = preferences;
        this.email = email;
        this.profileImage = profileImage; // 🖼️ Assign profile image
    }

    // ✅ **Empty constructor required for Firebase**
    public User() {}

    // ✅ **Getters and Setters**
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getInterests() { return interests; }
    public void setInterests(String interests) { this.interests = interests; }

    public String getPreferences() { return preferences; }
    public void setPreferences(String preferences) { this.preferences = preferences; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
}

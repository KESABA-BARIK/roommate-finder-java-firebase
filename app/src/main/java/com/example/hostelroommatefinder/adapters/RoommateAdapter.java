package com.example.hostelroommatefinder.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hostelroommatefinder.R;
import com.example.hostelroommatefinder.models.Rooms;
import com.example.hostelroommatefinder.models.User;
import com.example.hostelroommatefinder.services.RoomNotAvailableService;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class RoommateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ROOM = 0;
    private static final int TYPE_USER = 1;
    static MaterialButton btnEnquire;

    private Context context;
    private List<Rooms> roomsList;
    private List<Rooms> fullRoomList;  // backup for search
    private List<User> userList;

    public interface OnRoommateClickListener {
        void onRoommateClick(User user);
    }

    private OnRoommateClickListener listener;

    // Constructor for Rooms
    public RoommateAdapter(Context context, List<Rooms> roomsList) {
        this.context = context;
        this.roomsList = new ArrayList<>(roomsList);
        this.fullRoomList = new ArrayList<>(roomsList);
    }

    // Constructor for Users
    public RoommateAdapter(Context context, List<User> roommateList, OnRoommateClickListener listener) {
        this.context = context;
        this.userList = roommateList;
        this.listener = listener;
    }

    public void setRooms(List<Rooms> rooms) {
        this.roomsList = new ArrayList<>(rooms);
        this.fullRoomList = new ArrayList<>(rooms);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        roomsList.clear();
        if (query == null || query.trim().isEmpty()) {
            roomsList.addAll(fullRoomList);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Rooms room : fullRoomList) {
                if (room.getLocation() != null && room.getLocation().toLowerCase().contains(lowerCaseQuery)) {
                    roomsList.add(room);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return userList != null ? TYPE_USER : TYPE_ROOM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_roommate, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_roommate, parent, false);
            return new RoomViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_USER) {
            User user = userList.get(position);
            UserViewHolder userHolder = (UserViewHolder) holder;

            userHolder.tvName.setText("Name: " + user.getName());
            userHolder.tvAge.setText("Age: " + user.getAge());
            userHolder.tvGender.setText("Gender: " + user.getGender());
            userHolder.tvInterests.setText("Interests: " + user.getInterests());
            userHolder.tvPreferences.setText("Preferences: " + user.getPreferences());

            userHolder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRoommateClick(user);
                }
            });

        } else {
            Rooms room = roomsList.get(position);
            RoomViewHolder roomHolder = (RoomViewHolder) holder;


            roomHolder.tvRoomNo.setText("Room No: " + room.getRoomId());
            roomHolder.tvCapacity.setText("Capacity: " + room.getCapacity());
            roomHolder.tvLocation.setText("Location: " + room.getLocation());
            roomHolder.tvVacancies.setText("Available: " + room.getVacancies());

            roomHolder.btnViewOnMap.setOnClickListener(v -> {
                String latitude = Double.toString(room.getLatitude());
                String longitude = Double.toString(room.getLongitude());

                String uri = "geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude;
                Uri gmmIntentUri = Uri.parse(uri);

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                } else {
                    Toast.makeText(context, "Google Maps not installed", Toast.LENGTH_SHORT).show();
                }
            });
            roomHolder.btnEnquire.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("How many seats?");

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

                builder.setPositiveButton("OK", (dialog, which) -> {
                    String enteredSeatsStr = input.getText().toString().trim();
                    if (!enteredSeatsStr.isEmpty()) {
                        int enteredSeats = Integer.parseInt(enteredSeatsStr);
                        int availableSeats = room.getVacancies();

                        if (enteredSeats > availableSeats) {
                            // Start the service to show unavailability
                            Intent intent = new Intent(context, RoomNotAvailableService.class);
                            context.startService(intent);
                        } else {
                            String phoneNumber = room.getPhoneNumber();

                            if (phoneNumber != null && !phoneNumber.trim().isEmpty()) {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                                context.startActivity(callIntent);
                            } else {
                                Toast.makeText(context, "Contact number not available", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please enter number of seats", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
            });

        }

    }

    @Override
    public int getItemCount() {
        if (userList != null) return userList.size();
        if (roomsList != null) return roomsList.size();
        return 0;
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomNo, tvCapacity, tvLocation, tvVacancies;
        Button btnViewOnMap,btnEnquire;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomNo = itemView.findViewById(R.id.tvRoomNo);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvVacancies = itemView.findViewById(R.id.tvVacancies);
            btnViewOnMap = itemView.findViewById(R.id.btnViewOnMap);
            btnEnquire = itemView.findViewById(R.id.btnEnquire);  // ✅ Fix here

        }
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAge, tvGender, tvInterests, tvPreferences;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvAge = itemView.findViewById(R.id.tvAge);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvInterests = itemView.findViewById(R.id.tvInterests);
            tvPreferences = itemView.findViewById(R.id.tvPreferences);
        }
    }
}

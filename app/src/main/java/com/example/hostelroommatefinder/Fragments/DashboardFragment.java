package com.example.hostelroommatefinder.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;

import com.example.hostelroommatefinder.LoginActivity;
import com.example.hostelroommatefinder.R;
import com.example.hostelroommatefinder.RoomAvailabilityActivity;
import com.example.hostelroommatefinder.RoommateMatchingActivity;
import com.example.hostelroommatefinder.addroom;
import com.google.firebase.auth.FirebaseAuth;

public class DashboardFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        Button btnFindRoommate = view.findViewById(R.id.btnFindRoommate);
        Button btnRoomAvailability = view.findViewById(R.id.btnRoomAvailability);
        Button btnChat = view.findViewById(R.id.btnChat);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        btnFindRoommate.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RoommateMatchingActivity.class)));

        btnRoomAvailability.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), RoomAvailabilityActivity.class)));

        btnChat.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), addroom.class)));

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}

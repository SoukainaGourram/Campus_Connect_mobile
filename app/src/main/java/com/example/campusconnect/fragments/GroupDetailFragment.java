package com.example.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.campusconnect.R;

public class GroupDetailFragment extends Fragment {

    private static final String ARG_GROUP_ID = "group_id";
    private static final String ARG_GROUP_NAME = "group_name";
    private static final String ARG_MY_UID = "my_uid";
    private static final String ARG_MY_NAME = "my_name";
    private static final String ARG_MY_ROLE = "my_role";

    private String groupId;
    private String groupName;

    public static GroupDetailFragment newInstance(String groupId, String groupName, String myUid, String myName, String myRole) {
        GroupDetailFragment fragment = new GroupDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUP_ID, groupId);
        args.putString(ARG_GROUP_NAME, groupName);
        args.putString(ARG_MY_UID, myUid);
        args.putString(ARG_MY_NAME, myName);
        args.putString(ARG_MY_ROLE, myRole);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString(ARG_GROUP_ID);
            groupName = getArguments().getString(ARG_GROUP_NAME);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // En attendant le layout spécifique de l'Étape 8, on utilise un placeholder
        View view = inflater.inflate(R.layout.fragment_global_chat, container, false);
        return view;
    }
}
package com.example.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.campusconnect.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class GroupDetailFragment extends Fragment {

    private static final String ARG_GROUP_ID = "group_id";
    private static final String ARG_GROUP_NAME = "group_name";
    private static final String ARG_MY_UID = "my_uid";
    private static final String ARG_MY_NAME = "my_name";
    private static final String ARG_MY_ROLE = "my_role";

    private String groupId;
    private String groupName;
    private String myUid;
    private String myName;
    private String myRole;

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
            myUid = getArguments().getString(ARG_MY_UID);
            myName = getArguments().getString(ARG_MY_NAME);
            myRole = getArguments().getString(ARG_MY_ROLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_detail, container, false);

        TextView tvTitle = view.findViewById(R.id.tvGroupDetailTitle);
        TabLayout tabLayout = view.findViewById(R.id.tabLayoutGroup);
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerGroup);

        tvTitle.setText(groupName);

        viewPager.setAdapter(new GroupPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Discussion");
            else tab.setText("Documents");
        }).attach();

        return view;
    }

    private class GroupPagerAdapter extends FragmentStateAdapter {
        public GroupPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return GroupChatFragment.newInstance(groupId, myUid, myName, myRole);
            } else {
                return GroupDocsFragment.newInstance(groupId, myRole);
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}
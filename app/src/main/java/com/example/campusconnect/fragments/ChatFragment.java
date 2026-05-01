package com.example.campusconnect.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.campusconnect.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ChatFragment extends Fragment {

    public ChatFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        TabLayout tabLayout   = view.findViewById(R.id.tabLayoutChat);
        ViewPager2 viewPager  = view.findViewById(R.id.viewPagerChat);

        viewPager.setAdapter(new ChatPagerAdapter(requireActivity()));

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("💬 Global");
            else               tab.setText("🔒 Privé");
        }).attach();

        return view;
    }

    // ── Adapter interne : fournit GlobalChatFragment ou PrivateChatFragment ──
    private static class ChatPagerAdapter extends FragmentStateAdapter {

        ChatPagerAdapter(FragmentActivity activity) { super(activity); }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Position 0 → Chat global, Position 1 → Chat privé
            return position == 0 ? new GlobalChatFragment() : new PrivateChatFragment();
        }

        @Override
        public int getItemCount() { return 2; }
    }
}
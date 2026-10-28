package com.example.madassignment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView tv = new TextView(getContext());
        tv.setText("Settings\n\nApp Version: 1.0\nDatabase: SQLite\n\nToggle Expiry Alerts (Coming Soon)");
        tv.setTextSize(18f);
        tv.setPadding(40, 40, 40, 40);
        return tv;
    }
}
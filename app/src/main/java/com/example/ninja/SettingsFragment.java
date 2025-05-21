package com.example.ninja;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class SettingsFragment extends Fragment {

    private Spinner languageSpinner;
    private TextView edit_profile;
    private SharedPreferences prefs;

    private boolean languageChanged = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);

        // Set app locale based on saved preference (if not already set)
        String savedLang = prefs.getString("lang", "hy"); // Armenian default
        setAppLocale(savedLang); // ensures UI is updated correctly

        languageSpinner = rootView.findViewById(R.id.languageSpinner);
        edit_profile = rootView.findViewById(R.id.edit_profile);
        TextView logOutTextView = rootView.findViewById(R.id.logOutTextView);

        // Setup language spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.language_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Set spinner to match saved language
        if (savedLang.equals("en")) {
            languageSpinner.setSelection(1);
        } else {
            languageSpinner.setSelection(0);
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean firstLoad = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstLoad) {
                    firstLoad = false;
                    return;
                }

                String selectedLang = (position == 0) ? "hy" : "en";

                if (!selectedLang.equals(savedLang)) {
                    prefs.edit()
                            .putString("lang", selectedLang)
                            .putBoolean("reload_settings", true)
                            .apply();
                    requireActivity().recreate(); // recreate activity to apply language
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        edit_profile.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), ProfileActivity.class));
        });

        logOutTextView.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            prefs.edit().clear().apply();
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        return rootView;
    }

    private void setAppLocale(String langCode) {
        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        requireActivity().getResources().updateConfiguration(
                config,
                requireActivity().getResources().getDisplayMetrics()
        );
    }
}

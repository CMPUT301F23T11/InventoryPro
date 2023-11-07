package com.example.inventorypro;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.time.LocalDate;
import java.util.ArrayList;

//TODO: some inheritance in settings and the fragments would be appropriate.

public class FilterFragment extends Fragment {

    private EditText from,to,keywords;

    private FilterSettings filterSettings;
    public FilterSettings getFilterSettings(){return filterSettings;}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        return layoutInflater.inflate(R.layout.filter_fragment, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        filterSettings = ItemList.getInstance().getFilterSettings();

        // Load ui from current settings.
        from = view.findViewById(R.id.date_from_input);
        to = view.findViewById(R.id.date_to_input);
        keywords = view.findViewById(R.id.keywords_input);

        // Load UI with current filter settings.
        LocalDate fd = filterSettings.getFrom();
        from.setText(fd==null ? "" : fd.toString());
        LocalDate td = filterSettings.getTo();
        to.setText(td==null ? "" : td.toString());

        ArrayList<String> words = (filterSettings.getKeywords() == null) ? new ArrayList<>() : filterSettings.getKeywords();
        String keyword = "";
        for (String k : words){
            keyword += k+", ";
        }
        if(words.size() > 0){
            keyword = keyword.substring(0,keyword.length()-2);
        }
        keywords.setText(keyword);

        // Add listeners to parse the UI when in changes.
        from.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterSettings.setFrom(tryParseDate(s.toString()));
            }
        });

        to.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterSettings.setTo(tryParseDate(s.toString()));
            }
        });

        keywords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                String[] st=text.split(",");
                ArrayList<String> t=new ArrayList<>();
                for (String i : st){
                    if(i.isEmpty()) continue;
                    t.add(i.toLowerCase());
                }
                filterSettings.setKeywords(t);
            }
        });
    }

    @Nullable
    private LocalDate tryParseDate(String s){
        return Helpers.parseDate(s.toString());
    }

    /**
     * Resets all the filter settings to default
     */
    public void clear() {
        // TODO: Reset the settings
        // This method is already called from the SortFilterDialogFragment when clear is clicked
        // This should reset all settings to default
        filterSettings = new FilterSettings();
    }
}

package com.example.inventorypro.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.inventorypro.FilterSettings;
import com.example.inventorypro.Helpers;
import com.example.inventorypro.R;
import com.example.inventorypro.User;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Fragment which updates the UserPreferences FilterSettings.
 */
public class FilterFragment extends Fragment {

    private EditText from,to,keywords;
    private ArrayList<String> selectedMakes = new ArrayList<>();
    private ArrayList<String> selectedTags = new ArrayList<>();
    private EditText makesEditText;
    private EditText tagsEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        return layoutInflater.inflate(R.layout.filter_fragment, viewGroup, false);
    }

    private FilterSettings filterSettings(){
        return User.getInstance().getFilterSettings();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        // Load ui from current settings.
        from = view.findViewById(R.id.date_from_input);
        to = view.findViewById(R.id.date_to_input);
        keywords = view.findViewById(R.id.keywords_input);

        // Load UI with current filter settings.
        LocalDate fd = filterSettings().getFrom();
        from.setText(fd==null ? "" : fd.toString());
        LocalDate td = filterSettings().getTo();
        to.setText(td==null ? "" : td.toString());

        ArrayList<String> words = (filterSettings().getKeywords() == null) ? new ArrayList<>() : filterSettings().getKeywords();
        String keyword = "";
        for (String k : words){
            keyword += k+", ";
        }
        if(words.size() > 0){
            keyword = keyword.substring(0,keyword.length()-2);
        }
        keywords.setText(keyword);

        makesEditText = view.findViewById(R.id.makes);
        ArrayList<String> makeWords = (filterSettings().getMakes() == null) ? new ArrayList<>() : filterSettings().getMakes();
        String makeText= "";
        for (String k : makeWords){
            makeText += k+", ";
        }
        if(makeWords.size() > 0){
            makeText = makeText.substring(0,makeText.length()-2);
        }
        makesEditText.setText(makeText);

        tagsEditText = view.findViewById(R.id.tags);
        ArrayList<String> tagWords = (filterSettings().getTags() == null) ? new ArrayList<>() : filterSettings().getTags();
        String tagText= "";
        for (String k : tagWords){
            tagText += k+", ";
        }
        if(tagWords.size() > 0){
            tagText = tagText.substring(0,tagText.length()-2);
        }
        tagsEditText.setText(tagText);


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
                filterSettings().setFrom(tryParseDate(s.toString()));
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
                filterSettings().setTo(tryParseDate(s.toString()));
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
                filterSettings().setKeywords(t);
            }
        });

        ((Button)view.findViewById(R.id.select_make_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMakeMultiSelectDialog();
            }
        });
        ((Button)view.findViewById(R.id.select_tags_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTagMultiSelectDialog();
            }
        });
    }
    private void showMakeMultiSelectDialog() {
        MakeMultiSelectFragment makeMultiSelectFragment = MakeMultiSelectFragment.newInstance(selectedMakes);
        makeMultiSelectFragment.setOnCompleteListener(new MakeMultiSelectFragment.OnCompleteListener() {
            @Override
            public void onComplete(ArrayList<String> makes) {
                selectedMakes = makes;
                filterSettings().setMakes(selectedMakes);

                // Update makesEditText with selected makes
                updateMakesEditText();
            }
        });
        makeMultiSelectFragment.show(getParentFragmentManager(), "MakeMultiSelectFragment");
    }
    private void showTagMultiSelectDialog() {
        TagMultiSelectFragment tagMultiSelectFragment = TagMultiSelectFragment.newInstance(selectedTags);
        tagMultiSelectFragment.setOnCompleteListener(new TagMultiSelectFragment.OnCompleteListener() {
            @Override
            public void onComplete(ArrayList<String> tags) {
                selectedTags = tags;
                filterSettings().setTags(selectedTags);
                updateTagsEditText();
            }
        });
        tagMultiSelectFragment.show(getParentFragmentManager(), "TagMultiSelectFragment");
    }
    private void updateMakesEditText() {
        StringBuilder makesText = new StringBuilder();
        for (String make : selectedMakes) {
            makesText.append(make).append(", ");
        }
        if (selectedMakes.size() > 0) {
            makesText.delete(makesText.length() - 2, makesText.length());
        }
        makesEditText.setText(makesText.toString());
    }
    private void updateTagsEditText() {
        StringBuilder tagsText = new StringBuilder();
        for (String tag : selectedTags) {
            tagsText.append(tag).append(", ");
        }
        if (selectedTags.size() > 0) {
            tagsText.delete(tagsText.length() - 2, tagsText.length());
        }
        tagsEditText.setText(tagsText.toString());
    }

    @Nullable
    private LocalDate tryParseDate(String s){
        return Helpers.parseDate(s.toString());
    }
}

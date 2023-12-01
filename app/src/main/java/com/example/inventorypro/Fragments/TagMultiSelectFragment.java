package com.example.inventorypro.Fragments;

import static android.app.PendingIntent.getActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;

import com.example.inventorypro.Item;
import com.example.inventorypro.ItemList;
import com.example.inventorypro.R;

import java.util.ArrayList;
import java.util.List;

public class TagMultiSelectFragment extends DialogFragment {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> allTags;
    private ArrayList<String> selectedTags;

    public interface OnCompleteListener {
        void onComplete(ArrayList<String> selectedTags);
    }
    private OnCompleteListener tListener;

    public TagMultiSelectFragment() {
    }

    public static TagMultiSelectFragment newInstance(ArrayList<String> selectedTags) {
        TagMultiSelectFragment fragment = new TagMultiSelectFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("selectedTags", selectedTags);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedTags = getArguments().getStringArrayList("selectedTags");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tag_multi_select_fragment, container, false);
        listView = view.findViewById(R.id.tagListView);

        allTags = getAllTagsFromItems();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_multiple_choice, allTags);

        listView.setAdapter(adapter);

        for (String tag : selectedTags) {
            int pos = allTags.indexOf(tag);
            if (pos != -1) {
                listView.setItemChecked(pos, true);
            }
        }
        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> dismiss());

        Button doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> {
            selectedTags.clear();
            for (int i = 0; i < listView.getCount(); i++) {
                if (listView.isItemChecked(i)) {
                    selectedTags.add(allTags.get(i));
                }
            }
            tListener.onComplete(selectedTags);
            dismiss();
        });
        return view;
    }
    public void setOnCompleteListener(OnCompleteListener listener) {
        tListener = listener;
    }

    private ArrayList<String> getAllTagsFromItems() {
        ArrayList<String> allTags = new ArrayList<>();

        ItemList itemList = ItemList.getInstance();
        if (itemList != null) {
            for (Item item : itemList.getOriginalItemList()) {
                List<String> itemTags = item.getTags();
                if (itemTags != null) {
                    for (String tag : itemTags) {
                        if (!allTags.contains(tag)) {
                            allTags.add(tag);
                        }
                    }
                }
            }
        }
        return allTags;
    }


}
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
import com.example.inventorypro.TagList;
import com.example.inventorypro.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used when the user wants to select multiple tags to filter by.
 */
public class TagMultiSelectFragment extends DialogFragment {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> allTags;
    private ArrayList<String> selectedTags;

    /**
     * Listener for when the user completes their selection of tags.
     */
    public interface OnCompleteListener {
        void onComplete(ArrayList<String> selectedTags);
    }
    private OnCompleteListener tListener;

    public TagMultiSelectFragment() {
    }

    /**
     * Create a new take multi-select fragment given the pre-selected tags.
     * @param selectedTags The selection to create this fragment with.
     * @return The fragment.
     */
    public static TagMultiSelectFragment newInstance(ArrayList<String> selectedTags) {
        TagMultiSelectFragment fragment = new TagMultiSelectFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedTags = User.getInstance().getFilterSettings().getTags();
        if(selectedTags==null){
            selectedTags = new ArrayList<>();
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
        return TagList.getInstance().getItemList();
    }


}

package com.example.inventorypro.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;

import com.example.inventorypro.FilterSettings;
import com.example.inventorypro.Item;
import com.example.inventorypro.ItemList;
import com.example.inventorypro.R;
import com.example.inventorypro.User;

import java.util.ArrayList;

/**
 * Fragment which is used when the user wants to select multiple makes (filtering).
 */
public class MakeMultiSelectFragment extends DialogFragment {
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> selectedMakes;

    /**
     * Listener which returns with the selected makes.
     */
    public interface OnCompleteListener {
        void onComplete(ArrayList<String> selectedMakes);
    }

    private OnCompleteListener mListener;

    public MakeMultiSelectFragment() {

    }

    /**
     * Constructs a new Make Multi-select Fragment using the pre-known make selection.
     * @param selectedMakes The makes to preselect.
     * @return The fragment.
     */
    public static MakeMultiSelectFragment newInstance(ArrayList<String> selectedMakes) {
        MakeMultiSelectFragment fragment = new MakeMultiSelectFragment();
        return fragment;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedMakes = User.getInstance().getFilterSettings().getMakes();
        if(selectedMakes==null){
            selectedMakes = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.make_multi_select_fragment, container, false);
        listView = view.findViewById(R.id.makeListView);


        ArrayList<String> allMakes = getAllMakesFromItems();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_multiple_choice, allMakes);

        listView.setAdapter(adapter);

        for (String make : selectedMakes) {
            int pos = allMakes.indexOf(make);
            if (pos != -1) {
                listView.setItemChecked(pos, true);
            }
        }

        Button doneButton = view.findViewById(R.id.doneButton);
        doneButton.setOnClickListener(v -> {
            selectedMakes.clear();
            for (int i = 0; i < listView.getCount(); i++) {
                if (listView.isItemChecked(i)) {
                    selectedMakes.add(allMakes.get(i));
                }
            }
            mListener.onComplete(selectedMakes);
            dismiss();
        });
        Button cancelButton = view.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(v -> dismiss());

        return view;
    }
    public void setOnCompleteListener(OnCompleteListener listener) {
        mListener = listener;
    }
    private ArrayList<String> getAllMakesFromItems() {
        ArrayList<String> allMakes = new ArrayList<>();

        ItemList itemList = ItemList.getInstance();
        if (itemList != null) {
            for (Item item : itemList.getOriginalItemList()) {
                String make = item.getMake();
                if (make != null && !allMakes.contains(make)) {
                    allMakes.add(make);
                }
            }
        }
        return allMakes;
    }
}

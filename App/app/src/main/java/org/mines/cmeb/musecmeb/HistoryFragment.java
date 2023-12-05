package org.mines.cmeb.musecmeb;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class HistoryFragment extends Fragment {

    private ListView listView;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        listView = view.findViewById(android.R.id.list);
        displayData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Create a new instance of PastSessionFragment
                PastSessionFragment pastSessionFragment = new PastSessionFragment();

                // Use the FragmentManager to start a FragmentTransaction
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Replace the current Fragment with the new PastSessionFragment instance
                transaction.replace(((ViewGroup)getView().getParent()).getId(), pastSessionFragment);

                // Add the transaction to the back stack so the user can navigate back
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        });

        return view;
    }

    private void displayData() {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<RelaxationSession> sessions = dbHelper.getAllRelaxationSessions();

        Log.d("HistoryFragment", "Number of sessions retrieved: " + sessions.size());

        if (sessions.isEmpty()) {
            // The list is empty, which means there is no data in the database
            System.out.println("No data in the database");
            return;
        }

        // Create a RelaxationSessionAdapter
        RelaxationSessionAdapter adapter = new RelaxationSessionAdapter(getContext(), sessions);

        // Log the count of items in the adapter
        Log.d("HistoryFragment", "Number of items in adapter: " + adapter.getCount());

        // Set the adapter on the ListView
        listView.setAdapter(adapter);
    }
}
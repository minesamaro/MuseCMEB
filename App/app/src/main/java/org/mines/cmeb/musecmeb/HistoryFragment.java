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

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryFragment extends Fragment {

    private ListView listView;
    private List<RelaxationSession> sessions;

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

                // Create a bundle and put the selected RelaxationSession into it
                Bundle bundle = new Bundle();
                bundle.putSerializable("session", sessions.get(position));

                // Set the bundle as arguments to the PastSessionFragment
                pastSessionFragment.setArguments(bundle);

                // Use the FragmentManager to start a FragmentTransaction
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Replace the current Fragment with the new PastSessionFragment instance
                transaction.replace(R.id.frame_layout, pastSessionFragment);

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
        sessions = dbHelper.getAllRelaxationSessions();

        // Sort the sessions in descending order of startDate
        Collections.sort(sessions, new Comparator<RelaxationSession>() {
            @Override
            public int compare(RelaxationSession s1, RelaxationSession s2) {
                return s2.getStartDate().compareTo(s1.getStartDate());
            }
        });

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
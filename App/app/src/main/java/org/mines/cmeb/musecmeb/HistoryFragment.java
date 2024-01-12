package org.mines.cmeb.musecmeb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HistoryFragment extends Fragment implements Session.OnSessionEndListener {

    // ListView to display the relaxation sessions
    private ListView listView;
    // List of relaxation sessions
    private List<RelaxationSession> sessions;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /*
        * This fragment displays the relaxation sessions in a ListView.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Initialize the ListView
        listView = view.findViewById(android.R.id.list);

        // Display the relaxation sessions in the ListView
        displayData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /*
                * This method is called when an item in the ListView is clicked.
                * It starts a new PastSessionFragment and passes the selected RelaxationSession to it.
             */
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

                // Replace the current Fragment in the FrameLayout with the new PastSessionFragment instance
                transaction.replace(R.id.frame_layout, pastSessionFragment);

                // Add the transaction to the back stack so the user can navigate back
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

                // Get the MainActivity
                MainActivity mainActivity = (MainActivity) getActivity();

                // Make the FrameLayout visible and the ViewPager invisible
                mainActivity.viewPager.setVisibility(View.GONE);
                mainActivity.frameLayout.setVisibility(View.VISIBLE);
            }
        });

        // Set the OnSessionEndListener in the Session activity
        Session.setOnSessionEndListener(this);

        return view;
    }

    /*
        * This method is called when a session ends o when the Fragment is Created.
        * It refreshes the session list.
     */
    private void displayData() {
        // Create a DatabaseHelper
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        // Get all relaxation sessions from the database
        sessions = dbHelper.getAllRelaxationSessions();

        // Sort the sessions in descending order of startDate
        Collections.sort(sessions, new Comparator<RelaxationSession>() {
            @Override
            public int compare(RelaxationSession s1, RelaxationSession s2) {
                return Integer.compare(s2.getId(), s1.getId());
            }
        });

        if (sessions.isEmpty()) {
            // The list is empty, which means there is no data in the database
            return;
        }

        // Create a RelaxationSessionAdapter
        RelaxationSessionAdapter adapter = new RelaxationSessionAdapter(getContext(), sessions);

        // Set the adapter on the ListView
        listView.setAdapter(adapter);
    }

    /*
        * This method is called when a session ends.
        * It refreshes the session list.
     */
    @Override
    public void onSessionEnd() {
        // Refresh the session list when a session ends
        displayData();
    }
}
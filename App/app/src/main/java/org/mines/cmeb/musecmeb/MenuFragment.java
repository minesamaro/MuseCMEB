package org.mines.cmeb.musecmeb;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.content.SharedPreferences;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Central button to start a session (for some reason, works with an ImageView as a button)
        ImageView startButton = view.findViewById(R.id.startSessionBt);

        // Starts the Session activity when the button is clicked
        startButton.setOnClickListener(view1 -> {
            String chosenMusic = getChosenMusicOption();
            Intent intent = new Intent(getActivity(), Session.class);
            intent.putExtra("chosenMusic", chosenMusic);
            startActivity(intent);
        });

        // Button + intent to start the SettingsMenu activity
        ImageButton connectBt = view.findViewById(R.id.imageButton2);
        connectBt.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), SettingsMenu.class); // changed
            startActivity(intent);
        });

        // Inflate the layout for this fragment
        return view;
    }

    private String getChosenMusicOption() {
        // Retrieve the chosen music option from shared preferences
        SharedPreferences preferences = requireContext().getSharedPreferences("MusicOptionsPrefs", requireContext().MODE_PRIVATE);

        // Get the stored value, or provide a default option if not available
        String chosenMusic = preferences.getString("chosenMusic", "");

        // Check if the chosenMusic is empty, it plays Music1 by default
        if (chosenMusic.isEmpty()) {
            chosenMusic = "Soothing Rain";
        }

        return chosenMusic;
    }
}
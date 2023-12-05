package org.mines.cmeb.musecmeb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PastSessionFragment extends Fragment {

    private ImageButton backButton;
    private TextView sessionTitle;
    private ImageView graphImage;
    private TextView relaxTimeText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past_session, container, false);

        backButton = view.findViewById(R.id.backButton);
        sessionTitle = view.findViewById(R.id.sessionTitle);
        graphImage = view.findViewById(R.id.graphImage);
        relaxTimeText = view.findViewById(R.id.relaxTimeText);

        // Set a click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the back button click event
            }
        });

        // Set the text for the TextViews and the image for the ImageView
        sessionTitle.setText("Session 2 - 30/10/2023");
        graphImage.setImageResource(R.drawable.graph_example);
        relaxTimeText.setText("Time of relaxation: 3 minutes and 23 seconds");

        return view;
    }
}

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
import java.text.SimpleDateFormat;

import java.util.Locale;

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

        // Retrieve the RelaxationSession from the arguments
        RelaxationSession session = (RelaxationSession) getArguments().getSerializable("session");

        // Format the date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(session.getStartDate());

        // Set the text for the TextViews
        sessionTitle.setText("Session - " + formattedDate);
        relaxTimeText.setText("Time of relaxation: " + session.getRelaxationTime() + " minutes");

        // TODO: Set the image for the ImageView based on the data in the RelaxationSession

        return view;
    }
}
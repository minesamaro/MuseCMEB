package org.mines.cmeb.musecmeb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RelaxationSessionAdapter extends ArrayAdapter<RelaxationSession> {
    //Used to display the past sessions in a list in the history fragment
    private final Context context;
    private final List<RelaxationSession> sessions;

    public RelaxationSessionAdapter(Context context, List<RelaxationSession> sessions) {
        super(context, R.layout.row, sessions);
        this.context = context;
        this.sessions = sessions;
    }

    /*
        * This class is used to display the relaxation sessions in a ListView.
        * It displays the session id and the date and time of the session.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Inflate the layout for each row of the ListView
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row, parent, false);

        // Get the TextViews for the session text and start date
        TextView sessionTextView = rowView.findViewById(R.id.session_text);
        TextView startDateView = rowView.findViewById(R.id.start_date);

        // Format the date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(sessions.get(position).getStartDate());

        // Append the session id to the "Session" string
        String sessionText = "Session " + sessions.get(position).getId();

        // Set the text of the TextViews
        sessionTextView.setText(sessionText);
        startDateView.setText(formattedDate);

        return rowView;
    }
}
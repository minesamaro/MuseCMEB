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
    private final Context context;
    private final List<RelaxationSession> sessions;

    public RelaxationSessionAdapter(Context context, List<RelaxationSession> sessions) {
        super(context, R.layout.row, sessions);
        this.context = context;
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row, parent, false);

        TextView sessionTextView = rowView.findViewById(R.id.session_text);
        TextView startDateView = rowView.findViewById(R.id.start_date);

        // Format the date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = sdf.format(sessions.get(position).getStartDate());

        sessionTextView.setText("Session");
        startDateView.setText(formattedDate);

        return rowView;
    }
}
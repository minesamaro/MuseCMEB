package org.mines.cmeb.musecmeb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

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

        TextView stressIndexesView = rowView.findViewById(R.id.stress_indexes);
        TextView relaxationTimeView = rowView.findViewById(R.id.relaxation_time);
        TextView startDateView = rowView.findViewById(R.id.start_date);

        RelaxationSession session = sessions.get(position);
        stressIndexesView.setText(Arrays.toString(session.getStressIndexes()));
        relaxationTimeView.setText(String.valueOf(session.getRelaxationTime()));
        startDateView.setText(session.getStartDate().toString());

        return rowView;
    }
}
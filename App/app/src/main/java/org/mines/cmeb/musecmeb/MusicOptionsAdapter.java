package org.mines.cmeb.musecmeb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MusicOptionsAdapter extends ArrayAdapter<String> {
    private String selectedMusic;

    public MusicOptionsAdapter(Context context, int resource, List<String> options, String selectedMusic) {
        super(context, resource, options);
        this.selectedMusic = selectedMusic;
    }

    // Update the selected music when a new option is selected
    public void setSelectedMusic(String selectedMusic) {
        this.selectedMusic = selectedMusic;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);

        // Check if the current item is the selected music
        if (getItem(position).equals(selectedMusic)) {
            // Set a different background color for the selected item
            view.setBackgroundResource(R.color.our_light_blue);
        } else {
            // Reset the background color for other items
            view.setBackgroundResource(android.R.color.transparent);
        }

        return view;
    }
}


package org.mines.cmeb.musecmeb;

import java.io.Serializable;
import java.util.Date;

public class RelaxationSession implements Serializable {

    private int id;
    private int[] stressIndexes;
    private float relaxationTime;
    private Date startDate;

    public RelaxationSession(int id, int[] stressIndexes, float relaxationTime, Date startDate) {
        this.id = id;
        this.stressIndexes = stressIndexes;
        this.relaxationTime = relaxationTime;
        this.startDate = startDate;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public int[] getStressIndexes() {

        // epochs of 3 seconds
        //read eeg 2 seconds and calculate the stress index
        // mean power_alpha from all samples of 2 seconds
        return stressIndexes;
    }


    public float getRelaxationTime() {
        return relaxationTime;
    }

    public Date getStartDate() {
        return startDate;
    }
}
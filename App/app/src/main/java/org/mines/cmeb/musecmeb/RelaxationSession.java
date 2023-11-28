package org.mines.cmeb.musecmeb;

import java.util.Date;

public class RelaxationSession {

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
        return stressIndexes;
    }

    public float getRelaxationTime() {
        return relaxationTime;
    }

    public Date getStartDate() {
        return startDate;
    }
}

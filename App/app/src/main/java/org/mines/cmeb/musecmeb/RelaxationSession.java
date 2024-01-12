package org.mines.cmeb.musecmeb;

import java.io.Serializable;
import java.util.Date;

public class RelaxationSession implements Serializable {

    private int id;
    private int[] stressIndexes;
    private float relaxationTime;
    private Date startDate;

    /*
        * This class is used to store the data from a relaxation session.
        * It stores the id of the session, the stress indexes calculated from the EEG data received
        * from the Muse, the duration of the session, and the date and time of the session.
     */
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
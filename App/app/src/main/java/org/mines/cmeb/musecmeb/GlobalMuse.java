package org.mines.cmeb.musecmeb;

import android.app.Application;

import com.choosemuse.libmuse.Muse;

public class GlobalMuse extends Application {
    private Muse connectedMuse;
    private double momentStressIndex;

    public Muse getConnectedMuse() {
        return connectedMuse;
    }

    public void setConnectedMuse(Muse muse) {
        connectedMuse = muse;
    }
    public void setMomentStressIndex(double stressIndex) {
        momentStressIndex = stressIndex;
    }
    public double getMomentStressIndex() {
        return momentStressIndex;
    }
}

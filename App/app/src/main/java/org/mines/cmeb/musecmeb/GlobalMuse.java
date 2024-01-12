package org.mines.cmeb.musecmeb;

import android.app.Application;

import com.choosemuse.libmuse.Muse;
/*
    * This class is used to store global variables that can be accessed from any activity or fragment
    * in the app. This is useful for storing the Muse object that is connected to the app, so that
    * it can be accessed from any activity or fragment.
    * This class is also used to store the momentStressIndex, which is the stress index calculated
    * from the most recent EEG data received from the Muse.
    * We chose to use this method of storing global variables because it is the simplest method to
    * get the data acquired in the background into the Session Activity.
 */
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
    public boolean MuseConnected() {
        return connectedMuse != null;
    }
}

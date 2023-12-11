package org.mines.cmeb.musecmeb;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import com.choosemuse.libmuse.Accelerometer;
import com.choosemuse.libmuse.AnnotationData;
import com.choosemuse.libmuse.ConnectionState;
import com.choosemuse.libmuse.Eeg;
import com.choosemuse.libmuse.LibmuseVersion;
import com.choosemuse.libmuse.MessageType;
import com.choosemuse.libmuse.Muse;
import com.choosemuse.libmuse.MuseArtifactPacket;
import com.choosemuse.libmuse.MuseConfiguration;
import com.choosemuse.libmuse.MuseConnectionListener;
import com.choosemuse.libmuse.MuseConnectionPacket;
import com.choosemuse.libmuse.MuseDataListener;
import com.choosemuse.libmuse.MuseDataPacket;
import com.choosemuse.libmuse.MuseDataPacketType;
import com.choosemuse.libmuse.MuseFileFactory;
import com.choosemuse.libmuse.MuseFileReader;
import com.choosemuse.libmuse.MuseFileWriter;
import com.choosemuse.libmuse.MuseListener;
import com.choosemuse.libmuse.MuseManagerAndroid;
import com.choosemuse.libmuse.MuseVersion;
import com.choosemuse.libmuse.Result;
import com.choosemuse.libmuse.ResultLevel;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



/**
 * This example will illustrate how to connect to a Muse headband,
 * register for and receive EEG data and disconnect from the headband.
 * Saving EEG data to a .muse file is also covered.
 *
 * Usage instructions:
 * 1. Pair your headband if necessary.
 * 2. Run this project.
 * 3. Turn on the Muse headband.
 * 4. Press "Refresh". It should display all paired Muses in the Spinner drop down at the
 *    top of the screen.  It may take a few seconds for the headband to be detected.
 * 5. Select the headband you want to connect to and press "Connect".
 * 6. You should see EEG and accelerometer data as well as connection status,
 *    version information and relative alpha values appear on the screen.
 * 7. You can pause/resume data transmission with the button at the bottom of the screen.
 * 8. To disconnect from the headband, press "Disconnect"
 */
public class DataBluetooth extends Activity {


    /**
     * Tag used for logging purposes.
     */
    private final String TAG = "TestLibMuseAndroid";

    /**
     * The MuseManager is how you detect Muse headbands and receive notifications
     * when the list of available headbands changes.
     */
    private MuseManagerAndroid manager;

    /**
     * A Muse refers to a Muse headband.  Use this to connect/disconnect from the
     * headband, register listeners to receive EEG data and get headband
     * configuration and version information.
     */
    private Muse muse;

    /**
     * The ConnectionListener will be notified whenever there is a change in
     * the connection state of a headband, for example when the headband connects
     * or disconnects.
     *
     * Note that ConnectionListener is an inner class at the bottom of this file
     * that extends MuseConnectionListener.
     */

    /**
     * The DataListener is how you will receive EEG (and other) data from the
     * headband.
     *
     * Note that DataListener is an inner class at the bottom of this file
     * that extends MuseDataListener.
     */
    private DataListener dataListener;

    /**
     * Data comes in from the headband at a very fast rate; 220Hz, 256Hz or 500Hz,
     * depending on the type of headband and the preset configuration.  We buffer the
     * data that is read until we can update the UI.
     *
     * The stale flags indicate whether or not new data has been received and the buffers
     * hold the values of the last data packet received.  We are displaying the EEG, ALPHA_RELATIVE
     * and ACCELEROMETER values in this example.
     *
     * Note: the array lengths of the buffers are taken from the comments in
     * MuseDataPacketType, which specify 3 values for accelerometer and 6
     * values for EEG and EEG-derived packets.
     */

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1; // I CHANGED THIS (Francisco)
    private final double[] eegBuffer = new double[6];
    private boolean eegStale;
    private final double[] alphaBuffer = new double[6];
    private boolean alphaStale;
    private final double[] alphaAbsBuffer = new double[6];
    private boolean alphaAbsStale;
    private double[][] alphaTimeBuffer = new double[100][2];
    private  double alphaMean;
    private  double[] betaAbsBuffer = new double[6];
    private boolean betaAbsStale;
    private double[][] betaTimeBuffer = new double[100][2];

    private  double betaMean;
    private double meanAlpha;
    private double meanBeta;
    private final double[] accelBuffer = new double[3];
    private boolean accelStale;
    private int samplesAlpha = 0;
    private int samplesBeta = 0;
    private boolean alphaReady = false;
    private boolean betaReady = false;
    private double stressIndex;

    /**
     * We will be updating the UI using a handler instead of in packet handlers because
     * packets come in at a very high frequency and it only makes sense to update the UI
     * at about 60fps. The update functions do some string allocation, so this reduces our memory
     * footprint and makes GC pauses less frequent/noticeable.
     */
    private Handler handler;

    /**
     * In the UI, the list of Muses you can connect to is displayed in a Spinner object for this example.
     * This spinner adapter contains the MAC addresses of all of the headbands we have discovered.
     */
    private ArrayAdapter<String> spinnerAdapter;

    /**
     * It is possible to pause the data transmission from the headband.  This boolean tracks whether
     * or not the data transmission is enabled as we allow the user to pause transmission in the UI.
     */
    private boolean dataTransmission = true;

    /**
     * To save data to a file, you should use a MuseFileWriter.  The MuseFileWriter knows how to
     * serialize the data packets received from the headband into a compact binary format.
     * To read the file back, you would use a MuseFileReader.
     */
    private final AtomicReference<MuseFileWriter> fileWriter = new AtomicReference<>();

    /**
     * We don't want file operations to slow down the UI, so we will defer those file operations
     * to a handler on a separate thread.
     */
    private final AtomicReference<Handler> fileHandler = new AtomicReference<>();


    //--------------------------------------
    // Lifecycle / Connection code


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        muse = ((GlobalMuse) getApplication()).getConnectedMuse();

        if (muse != null){
            setupListeners();
            muse.runAsynchronously();
        } else{
            // How to use app without Muse connected
            finish();
        }
    }

    private void setupListeners(){
        WeakReference<DataBluetooth> weakActivity = new WeakReference<>(this);
        // Register a listener to receive data from a Muse.
        dataListener = new DataListener(weakActivity);
        muse.registerDataListener(dataListener, MuseDataPacketType.EEG);
        muse.registerDataListener(dataListener, MuseDataPacketType.ALPHA_RELATIVE);
        muse.registerDataListener(dataListener, MuseDataPacketType.ACCELEROMETER);
        muse.registerDataListener(dataListener, MuseDataPacketType.BATTERY);
        muse.registerDataListener(dataListener, MuseDataPacketType.DRL_REF);
        muse.registerDataListener(dataListener, MuseDataPacketType.QUANTIZATION);
        muse.registerDataListener(dataListener, MuseDataPacketType.ALPHA_ABSOLUTE);
        muse.registerDataListener(dataListener, MuseDataPacketType.BETA_ABSOLUTE);
    }

    //--------------------------------------
    // Listeners

    /**
     * You will receive a callback to this method each time the headband sends a MuseDataPacket
     * that you have registered.  You can use different listeners for different packet types or
     * a single listener for all packet types as we have done here.
     * @param p     The data packet containing the data from the headband (eg. EEG data)
     * @param muse  The headband that sent the information.
     */
    @SuppressWarnings("unused")
    public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {

        // valuesSize returns the number of data values contained in the packet.
        @SuppressWarnings("unused") final long n = p.valuesSize();
        switch (p.packetType()) {

            case EEG:
                getEegChannelValues(eegBuffer,p);
                eegStale = true;
                Log.i("eegsample", "received one  sample");
                break;

            case ALPHA_RELATIVE:
                getEegChannelValues(alphaBuffer,p);
                alphaStale = true;
                break;

            case ALPHA_ABSOLUTE:
                getEegChannelValues(alphaAbsBuffer,p);
                alphaAbsStale = true;
                Log.i("alphaSample", "received one alpha sample");

                if (samplesAlpha < 100){
                    UpdateAlphaTimeBuffer(alphaTimeBuffer, alphaAbsBuffer,samplesAlpha);
                    samplesAlpha++;
                }

                else if (samplesAlpha == 100){
                    alphaReady = true;
                    Log.i("alphaready","im ready");
                }

                break;

            case BETA_ABSOLUTE:
                getEegChannelValues(betaAbsBuffer,p);
                betaAbsStale = true;
                Log.i("betaSample", "received one beta sample");

                if (samplesBeta < 100){
                    UpdateBetaTimeBuffer(betaTimeBuffer, betaAbsBuffer,samplesBeta);
                    samplesBeta++;
                }

                else if (samplesBeta == 100){
                    betaReady = true;
                    Log.i("betaready","im ready");
                }
                break;

            case BATTERY:
            case DRL_REF:
            case QUANTIZATION:
            default:
                break;
        }

        if (alphaReady && betaReady){
            // Calculate the stress index
            meanPower();  // displays classification in the logcat

            // Reset variables
            alphaReady = false;
            betaReady = false;
            samplesAlpha = 50;
            samplesBeta = 50;

            // Shift the time buffers (allocate space for new 50 samples, and keep the last 50)
            windowTimeBufferShift(alphaTimeBuffer);
            windowTimeBufferShift(betaTimeBuffer);
        }
    }
    private void getEegChannelValues(double[] buffer, MuseDataPacket p) {
        buffer[0] = p.getEegChannelValue(Eeg.EEG1);
        buffer[1] = p.getEegChannelValue(Eeg.EEG2);
        buffer[2] = p.getEegChannelValue(Eeg.EEG3);
        buffer[3] = p.getEegChannelValue(Eeg.EEG4);
        buffer[4] = p.getEegChannelValue(Eeg.AUX_LEFT);
        buffer[5] = p.getEegChannelValue(Eeg.AUX_RIGHT);
    }

    public void UpdateAlphaTimeBuffer(double[][] timeBuffer, double[] alphaBuffer, int sample){
        timeBuffer[sample][0]=alphaBuffer[0];
        timeBuffer[sample][1]=alphaBuffer[3];
    }
    public void UpdateBetaTimeBuffer(double[][] timeBuffer, double[] betaBuffer, int sample){
        timeBuffer[sample][0]=betaBuffer[1];
        timeBuffer[sample][1]=betaBuffer[2];
    }

    public void windowTimeBufferShift (double[][] timeBuffer){
        for (int i=0; i<50;i++){
            timeBuffer[i][0]=timeBuffer[i+50][0];
            timeBuffer[i][1]=timeBuffer[i+50][1];
        }
    }



    //--------------------------------------
    // UI Specific methods
    /**
     * The runnable that is used to update the UI at 60Hz.
     *
     * We update the UI from this Runnable instead of in packet handlers
     * because packets come in at high frequency -- 220Hz or more for raw EEG
     * -- and it only makes sense to update the UI at about 60fps. The update
     * functions do some string allocation, so this reduces our memory
     * footprint and makes GC pauses less frequent/noticeable.
     */
    private final Runnable tickUi = new Runnable() {
        @Override
        public void run() {
            // Change this to transmit data
            sendDataToOtherActivity();
            handler.postDelayed(tickUi, 1000 / 60);  // 60Hz, this is the refresh rate of the UI!!!
        }
    };

    // Method to send data to another activity
    private void sendDataToOtherActivity() {
        Intent intent = new Intent(this, Session.class);
        intent.putExtra("STRESS_INDEX", stressIndex);
        startActivity(intent);
    }


    //public static double averagePower(double[] MeanChannel) {
    //  return Arrays.stream(MeanChannel).average().orElse(Double.NaN);
    //}
    public static double averagePower(double[][] timeBuffer) {
        double sum = 0;
        for (double[] row : timeBuffer) {
            for (double elem : row) {
                sum += elem;
            }
        }
        return sum / (timeBuffer.length * timeBuffer[0].length); // average power of the signal (sum of all values divided by the number of values)
    }

    private void meanPower(){
        meanAlpha = averagePower(alphaTimeBuffer);
        meanBeta = averagePower(betaTimeBuffer);
        stressIndex = meanBeta / meanAlpha;
        Log.i("alpha", String.valueOf(meanAlpha));
        Log.i("beta", String.valueOf(meanBeta));
        Log.i("stressIndex", String.valueOf(stressIndex));
        if (stressIndex<0.9){

            Log.i("mental state", "relaxed");

        }
        else {
            Log.i("mental state", "stressed");
        }
    }


    //--------------------------------------
    // Listener translators
    //
    // Each of these classes extend from the appropriate listener and contain a weak reference
    // to the activity.  Each class simply forwards the messages it receives back to the Activity.


    static class DataListener extends MuseDataListener {
        final WeakReference<DataBluetooth> activityRef;

        DataListener(final WeakReference<DataBluetooth> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
            Log.i("musepacketsample", "received one sample");
            activityRef.get().receiveMuseDataPacket(p, muse);

        }

        @Override
        public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
            activityRef.get().receiveMuseArtifactPacket(p, muse);
        }
    }

    private void receiveMuseArtifactPacket(MuseArtifactPacket p, Muse muse) {
    }
}
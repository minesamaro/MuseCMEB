/*
 * Example of using libmuse library on android.
 * Interaxon, Inc. 2016
 */

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
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * This code was adapted from the Muse SDK example code.
 * It is used to connect to the Muse headband and receive EEG data from it.
 * It is also used to calculate the stress index from the EEG data.
 * Some of the comments in this code were written by the original authors of the Muse SDK example code.
 * Other comments and fucntions were written by the Euterpe team.
 */
public class LibTest extends Activity implements OnClickListener {


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
    private ConnectionListener connectionListener;

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

            // We need to set the context on MuseManagerAndroid before we can do anything.
            // This must come before other LibMuse API calls as it also loads the library.
            manager = MuseManagerAndroid.getInstance();
            manager.setContext(this);

            Log.i(TAG, "LibMuse version=" + LibmuseVersion.instance().getString());
            //idk what this is
            WeakReference<LibTest> weakActivity =
                    new WeakReference<>(this);
            // Register a listener to receive connection state changes.
            connectionListener = new ConnectionListener(weakActivity);
            // Register a listener to receive data from a Muse.
            dataListener = new DataListener(weakActivity);
            // Register a listener to receive notifications of what Muse headbands
            // we can connect to.
            manager.setMuseListener(new MuseL(weakActivity));

            // Muse 2016 (MU-02) headbands use Bluetooth Low Energy technology to
            // simplify the connection process.  This requires access to the COARSE_LOCATION
            // or FINE_LOCATION permissions.  Make sure we have these permissions before
            // proceeding.
            ensurePermissions();

            // Load and initialize our UI.
            initUI();

            // Start up a thread for asynchronous file operations.
            // This is only needed if you want to do File I/O.
            fileThread.start();

            // Start our asynchronous updates of the UI.
            handler = new Handler(getMainLooper());
            handler.post(tickUi);
    }


    protected void onPause() {
        super.onPause();
        // It is important to call stopListening when the Activity is paused
        // to avoid a resource leak from the LibMuse library.
        manager.stopListening();
    }

    /**
     * This is called when the user presses any button on the Connections Page.
     * @param v The view that triggered this callback.
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.refresh) {
            // The user has pressed the "Refresh" button.
            // Start listening for nearby or paired Muse headbands. We call stopListening
            // first to make sure startListening will clear the list of headbands and start fresh.
            manager.stopListening();
            manager.startListening();

        } else if (v.getId() == R.id.connect) {

            // The user has pressed the "Connect" button to connect to
            // the headband in the spinner.

            // Listening is an expensive operation, so now that we know
            // which headband the user wants to connect to we can stop
            // listening for other headbands.
            manager.stopListening();

            List<Muse> availableMuses = manager.getMuses();
            Spinner musesSpinner = findViewById(R.id.muses_spinner);

            // Check that we actually have something to connect to.
            if (availableMuses.size() < 1 || musesSpinner.getAdapter().getCount() < 1) {
                Log.w(TAG, "There is nothing to connect to");
            } else {

                // Cache the Muse that the user has selected.
                muse = availableMuses.get(musesSpinner.getSelectedItemPosition());
                // Unregister all prior listeners and register our data listener to
                // receive the MuseDataPacketTypes we are interested in.  If you do
                // not register a listener for a particular data type, you will not
                // receive data packets of that type.
                muse.unregisterAllListeners();
                muse.registerConnectionListener(connectionListener);
                muse.registerDataListener(dataListener, MuseDataPacketType.EEG);
                muse.registerDataListener(dataListener, MuseDataPacketType.ALPHA_RELATIVE);
                muse.registerDataListener(dataListener, MuseDataPacketType.ACCELEROMETER);
                muse.registerDataListener(dataListener, MuseDataPacketType.BATTERY);
                muse.registerDataListener(dataListener, MuseDataPacketType.DRL_REF);
                muse.registerDataListener(dataListener, MuseDataPacketType.QUANTIZATION);
                muse.registerDataListener(dataListener, MuseDataPacketType.ALPHA_ABSOLUTE);
                muse.registerDataListener(dataListener, MuseDataPacketType.BETA_ABSOLUTE);

                // Initiate a connection to the headband and stream the data asynchronously.
                muse.runAsynchronously();
                ((GlobalMuse) getApplication()).setConnectedMuse(muse);
                Log.i(TAG, muse.getConnectionState().toString());
            }

        } else if (v.getId() == R.id.disconnect) {

            // The user has pressed the "Disconnect" button.
            // Disconnect from the selected Muse.
            if (muse != null) {
                muse.disconnect();
                ((GlobalMuse) getApplication()).setConnectedMuse(null);
                setAllVerifiedDark();
            }

        } else if (v.getId() == R.id.pause) {

            // The user has pressed the "Pause/Resume" button to either pause or
            // resume data transmission.  Toggle the state and pause or resume the
            // transmission on the headband.
            if (muse != null) {
                dataTransmission = !dataTransmission;
                muse.enableDataTransmission(dataTransmission);
            }
        }
    }

    //--------------------------------------
    // Permissions

    /**
     * The ACCESS_FINE_LOCATION permission is required to use the
     * Bluetooth Low Energy library and must be requested at runtime for Android 6.0+
     * On an Android 6.0 device, the following code will display 2 dialogs,
     * one to provide context and the second to request the permission.
     * On an Android device running an earlier version, nothing is displayed
     * as the permission is granted from the manifest.
     *
     * If the permission is not granted, then Muse 2016 (MU-02) headbands will
     * not be discovered and a SecurityException will be thrown.
     */
    private void ensurePermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // We don't have the ACCESS_FINE_LOCATION permission so create the dialogs asking
            // the user to grant us the permission.

            DialogInterface.OnClickListener buttonListener =
                    (dialog, which) -> {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(LibTest.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                0);
                    };


            // This is the context dialog which explains to the user the reason we are requesting
            // this permission.  When the user presses the positive (I Understand) button, the
            // standard Android permission dialog will be displayed (as defined in the button
            // listener above).
            AlertDialog introDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.permission_dialog_title)
                    .setMessage(R.string.permission_dialog_description)
                    .setPositiveButton(R.string.permission_dialog_understand, buttonListener)
                    .create();
            introDialog.show();
        }
    }




    //--------------------------------------
    // Listeners

    /**
     * You will receive a callback to this method each time a headband is discovered.
     * In this example, we update the spinner with the MAC address of the headband.
     */
    public void museListChanged() {
        final List<Muse> list = manager.getMuses();
        spinnerAdapter.clear();
        for (Muse m : list) {
            spinnerAdapter.add(m.getName() + " - " + m.getMacAddress());
        }
    }

    /**
     * You will receive a callback to this method each time there is a change to the
     * connection state of one of the headbands.
     * @param p     A packet containing the current and prior connection states
     * @param muse  The headband whose state changed.
     */
    public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {

        final ConnectionState current = p.getCurrentConnectionState();

        // Format a message to show the change of connection state in the UI.
        final String status = p.getPreviousConnectionState() + " -> " + current;
        Log.i(TAG, status);

        // Update the UI with the change in connection state.
        handler.post(() -> {

            final TextView statusText = findViewById(R.id.con_status);
            statusText.setText(status);

            final MuseVersion museVersion = muse.getMuseVersion();
            final TextView museVersionText = findViewById(R.id.version);
            // If we haven't yet connected to the headband, the version information
            // will be null.  You have to connect to the headband before either the
            // MuseVersion or MuseConfiguration information is known.
            if (museVersion != null) {
                final String version = museVersion.getFirmwareType() + " - "
                        + museVersion.getFirmwareVersion() + " - "
                        + museVersion.getProtocolVersion();
                museVersionText.setText(version);

            } else {
                museVersionText.setText(R.string.undefined);
                ((GlobalMuse) getApplication()).setConnectedMuse(null);
                setAllVerifiedDark();
                museVersionText.setVisibility(View.INVISIBLE);
                TextView museVersionLabel = findViewById(R.id.version_label);
                museVersionLabel.setVisibility(View.INVISIBLE);
            }
        });

        if (current == ConnectionState.DISCONNECTED) {
            Log.i(TAG, "Muse disconnected:" + muse.getName());
            // Save the data file once streaming has stopped.
            saveFile();
            // We have disconnected from the headband, so set our cached copy to null.
            this.muse = null;
        }
    }

    /**
     * You will receive a callback to this method each time the headband sends a MuseDataPacket
     * that you have registered.  You can use different listeners for different packet types or
     * a single listener for all packet types as we have done here.
     * @param p     The data packet containing the data from the headband (eg. EEG data)
     * @param muse  The headband that sent the information.
     */
    @SuppressWarnings("unused")
    public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
        writeDataPacketToFile(p);

        // valuesSize returns the number of data values contained in the packet.
        @SuppressWarnings("unused") final long n = p.valuesSize();
        switch (p.packetType()) {

            case EEG:
                getEegChannelValues(eegBuffer,p);
                eegStale = true;
                break;
            case ACCELEROMETER:
                getAccelValues(p);
                accelStale = true;
                break;

            case ALPHA_RELATIVE:
                getEegChannelValues(alphaBuffer,p);
                alphaStale = true;
                break;

            case ALPHA_ABSOLUTE:
                getEegChannelValues(alphaAbsBuffer,p);
                alphaAbsStale = true;

                if (samplesAlpha < 100){
                    UpdateAlphaTimeBuffer(alphaTimeBuffer, alphaAbsBuffer,samplesAlpha);
                    samplesAlpha++;
                }

                else if (samplesAlpha == 100){
                    alphaReady = true;
                }

                break;

            case BETA_ABSOLUTE:
                getEegChannelValues(betaAbsBuffer,p);
                betaAbsStale = true;

                if (samplesBeta < 100){
                    UpdateBetaTimeBuffer(betaTimeBuffer, betaAbsBuffer,samplesBeta);
                    samplesBeta++;
                }

                else if (samplesBeta == 100){
                    betaReady = true;
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

    /**
     * You will receive a callback to this method each time an artifact packet is generated if you
     * have registered for the ARTIFACTS data type.  MuseArtifactPackets are generated when
     * eye blinks are detected, the jaw is clenched and when the headband is put on or removed.
     * @param p     The artifact packet with the data from the headband.
     * @param muse  The headband that sent the information.
     */
    @SuppressWarnings("unused")
    public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
    }

    /**
     * Helper methods to get different packet values.  These methods simply store the
     * data in the buffers for later display in the UI.
     *
     * getEegChannelValue can be used for any EEG or EEG derived data packet type
     * such as EEG, ALPHA_ABSOLUTE, ALPHA_RELATIVE or HSI_PRECISION.  See the documentation
     * of MuseDataPacketType for all of the available values.
     * Specific packet types like ACCELEROMETER, GYRO, BATTERY and DRL_REF have their own
     * getValue methods.
     */
    private void getEegChannelValues(double[] buffer, MuseDataPacket p) {
        buffer[0] = p.getEegChannelValue(Eeg.EEG1);
        buffer[1] = p.getEegChannelValue(Eeg.EEG2);
        buffer[2] = p.getEegChannelValue(Eeg.EEG3);
        buffer[3] = p.getEegChannelValue(Eeg.EEG4);
        buffer[4] = p.getEegChannelValue(Eeg.AUX_LEFT);
        buffer[5] = p.getEegChannelValue(Eeg.AUX_RIGHT);
    }

    private void getAccelValues(MuseDataPacket p) {
        accelBuffer[0] = p.getAccelerometerValue(Accelerometer.X);
        accelBuffer[1] = p.getAccelerometerValue(Accelerometer.Y);
        accelBuffer[2] = p.getAccelerometerValue(Accelerometer.Z);
    }


    //--------------------------------------
    // UI Specific methods

    /**
     * Initializes the UI of the example application.
     */
    private void initUI() {
        setContentView(R.layout.bluetooth2);
        Button refreshButton = findViewById(R.id.refresh);
        refreshButton.setOnClickListener(this);
        Button connectButton = findViewById(R.id.connect);
        connectButton.setOnClickListener(this);
        Button disconnectButton = findViewById(R.id.disconnect);
        disconnectButton.setOnClickListener(this);
        Button pauseButton = findViewById(R.id.pause);
        pauseButton.setOnClickListener(this);

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        Spinner musesSpinner = findViewById(R.id.muses_spinner);
        musesSpinner.setAdapter(spinnerAdapter);

        ImageButton backBt = findViewById(R.id.backButton);
        backBt.setOnClickListener(view -> onBackPressed());
    }

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
            if (betaAbsStale) {
                updateBeta();
                ((GlobalMuse) getApplication()).setConnectedMuse(muse);
            }
            if (alphaStale) {
                updateAlpha();
                ((GlobalMuse) getApplication()).setConnectedMuse(muse);
            }
            handler.postDelayed(tickUi, 1000 / 60);  // 60Hz, this is the refresh rate of the UI!!!
        }
    };

    /**
     * The following methods update the ImageViews in the UI if the Muse is well Positioned. or not.
     */
    private void updateBeta() {
        if (betaAbsBuffer[0] != 0){
            ImageView img = findViewById(R.id.ch1Status);
            img.setImageResource(R.drawable.verified);
        } else {
            ImageView img = findViewById(R.id.ch1Status);
            img.setImageResource(R.drawable.verified_dark);
        }
        if (betaAbsBuffer[1] != 0){
            ImageView img = findViewById(R.id.ch2Status);
            img.setImageResource(R.drawable.verified);
        } else {
            ImageView img = findViewById(R.id.ch2Status);
            img.setImageResource(R.drawable.verified_dark);
        }
        if (betaAbsBuffer[2] != 0){
            ImageView img = findViewById(R.id.ch3Status);
            img.setImageResource(R.drawable.verified);
        } else {
            ImageView img = findViewById(R.id.ch3Status);
            img.setImageResource(R.drawable.verified_dark);
        }
        if (alphaAbsBuffer[3] != 0){
            ImageView img = findViewById(R.id.ch4Status);
            img.setImageResource(R.drawable.verified);
        } else {
            ImageView img = findViewById(R.id.ch4Status);
            img.setImageResource(R.drawable.verified_dark);
        }
    }

    /**
     * Calculates the average power of the signal.
     * @param timeBuffer    The buffer containing the signal
     * @return              The average power of the signal
     */
    public static double averagePower(double[][] timeBuffer) {
        double sum = 0;
        for (double[] row : timeBuffer) {
            for (double elem : row) {
                sum += elem;
            }
        }
        return sum / (timeBuffer.length * timeBuffer[0].length); // average power of the signal (sum of all values divided by the number of values)
    }

    /**
     * The following methods update the ImageViews in the UI if the Muse is well positioned or not.
     */
    private void updateAlpha() {
        if (alphaAbsBuffer[0] != 0){
            ImageView img = findViewById(R.id.ch1Status);
            img.setImageResource(R.drawable.verified);
        } else {
            ImageView img = findViewById(R.id.ch1Status);
            img.setImageResource(R.drawable.verified_dark);
        }
        if (alphaAbsBuffer[1] != 0){
            ImageView img = findViewById(R.id.ch2Status);
            img.setImageResource(R.drawable.verified);
        } else {
            ImageView img = findViewById(R.id.ch2Status);
            img.setImageResource(R.drawable.verified_dark);
        }
        if (alphaAbsBuffer[2] != 0){
            ImageView img = findViewById(R.id.ch3Status);
            img.setImageResource(R.drawable.verified);
        } else {
            ImageView img = findViewById(R.id.ch3Status);
            img.setImageResource(R.drawable.verified_dark);
        }
        if (alphaAbsBuffer[3] != 0){
            ImageView img = findViewById(R.id.ch4Status);
            img.setImageResource(R.drawable.verified);
        } else {
            ImageView img = findViewById(R.id.ch4Status);
            img.setImageResource(R.drawable.verified_dark);
        }
    }

    /**
     * Calculates the mean power of the alpha and beta waves, and the stress index.
     */
    private void meanPower(){
        meanAlpha = averagePower(alphaTimeBuffer);
        meanBeta = averagePower(betaTimeBuffer);
        stressIndex = meanBeta / meanAlpha;

        Log.i("stressIndex", String.valueOf(stressIndex));

        ((GlobalMuse) getApplication()).setMomentStressIndex(stressIndex);
    }

    /**
     * Sets all the ImageViews to the dark verified image.
     */
    private void setAllVerifiedDark(){
        ImageView img = findViewById(R.id.ch1Status);
        img.setImageResource(R.drawable.verified_dark);
        ImageView img2 = findViewById(R.id.ch2Status);
        img2.setImageResource(R.drawable.verified_dark);
        ImageView img3 = findViewById(R.id.ch3Status);
        img3.setImageResource(R.drawable.verified_dark);
        ImageView img4 = findViewById(R.id.ch4Status);
        img4.setImageResource(R.drawable.verified_dark);
    }

    //--------------------------------------
    // File I/O

    /**
     * We don't want to block the UI thread while we write to a file, so the file
     * writing is moved to a separate thread.
     */
    private final Thread fileThread = new Thread() {
        @Override
        public void run() {
            Looper.prepare();
            fileHandler.set(new Handler(getMainLooper()));
            final File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            final File file = new File(dir, "new_muse_file.muse" );
            // MuseFileWriter will append to an existing file.
            // In this case, we want to start fresh so the file
            // if it exists.
            if (file.exists() && !file.delete()) {
                Log.e(TAG, "file not successfully deleted");
            }
            Log.i(TAG, "Writing data to: " + file.getAbsolutePath());
            fileWriter.set(MuseFileFactory.getMuseFileWriter(file));
            Looper.loop();
        }
    };

    /**
     * Writes the provided MuseDataPacket to the file.  MuseFileWriter knows
     * how to write all packet types generated from LibMuse.
     * @param p     The data packet to write.
     */
    private void writeDataPacketToFile(final MuseDataPacket p) {
        Handler h = fileHandler.get();
        if (h != null) {
            h.post(() -> fileWriter.get().addDataPacket(0, p));
        }
    }

    /**
     * Flushes all the data to the file and closes the file writer.
     */
    private void saveFile() {
        Handler h = fileHandler.get();
        if (h != null) {
            h.post(() -> {
                MuseFileWriter w = fileWriter.get();
                // Annotation strings can be added to the file to
                // give context as to what is happening at that point in
                // time.  An annotation can be an arbitrary string or
                // may include additional AnnotationData.
                w.addAnnotationString(0, "Disconnected");
                w.flush();
                w.close();
            });
        }
    }

    /**
     * Reads the provided .muse file and prints the data to the logcat.
     * @param name  The name of the file to read.  The file in this example
     *              is assumed to be in the Environment.DIRECTORY_DOWNLOADS
     *              directory.
     */
    @SuppressWarnings("unused")
    private void playMuseFile(String name) {

        File dir = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(dir, name);

        final String tag = "Muse File Reader";

        if (!file.exists()) {
            Log.w(tag, "file doesn't exist");
            return;
        }

        MuseFileReader fileReader = MuseFileFactory.getMuseFileReader(file);

        // Loop through each message in the file.  gotoNextMessage will read the next message
        // and return the result of the read operation as a Result.
        Result res = fileReader.gotoNextMessage();
        while (res.getLevel() == ResultLevel.R_INFO && !res.getInfo().contains("EOF")) {

            MessageType type = fileReader.getMessageType();
            int id = fileReader.getMessageId();
            long timestamp = fileReader.getMessageTimestamp();

            Log.i(tag, "type: " + type.toString() +
                    " id: " + id +
                    " timestamp: " + timestamp);

            switch(type) {
                // EEG messages contain raw EEG data or DRL/REF data.
                // EEG derived packets like ALPHA_RELATIVE and artifact packets
                // are stored as MUSE_ELEMENTS messages.
                case EEG:
                case BATTERY:
                case ACCELEROMETER:
                case QUANTIZATION:
                case GYRO:
                case MUSE_ELEMENTS:
                    MuseDataPacket packet = fileReader.getDataPacket();
                    Log.i(tag, "data packet: " + packet.packetType().toString());
                    break;
                case VERSION:
                    MuseVersion version = fileReader.getVersion();
                    Log.i(tag, "version" + version.getFirmwareType());
                    break;
                case CONFIGURATION:
                    MuseConfiguration config = fileReader.getConfiguration();
                    Log.i(tag, "config" + config.getBluetoothMac());
                    break;
                case ANNOTATION:
                    AnnotationData annotation = fileReader.getAnnotation();
                    Log.i(tag, "annotation" + annotation.getData());
                    break;
                default:
                    break;
            }

            // Read the next message.
            res = fileReader.gotoNextMessage();
        }
    }

    //--------------------------------------
    // Listener translators
    //
    // Each of these classes extend from the appropriate listener and contain a weak reference
    // to the activity.  Each class simply forwards the messages it receives back to the Activity.
    static class MuseL extends MuseListener {
        final WeakReference<LibTest> activityRef;

        MuseL(final WeakReference<LibTest> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void museListChanged() {
            activityRef.get().museListChanged();
        }
    }

    static class ConnectionListener extends MuseConnectionListener {
        final WeakReference<LibTest> activityRef;

        ConnectionListener(final WeakReference<LibTest> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseConnectionPacket(final MuseConnectionPacket p, final Muse muse) {
            activityRef.get().receiveMuseConnectionPacket(p, muse);
        }
    }

    static class DataListener extends MuseDataListener {
        final WeakReference<LibTest> activityRef;

        DataListener(final WeakReference<LibTest> activityRef) {
            this.activityRef = activityRef;
        }

        @Override
        public void receiveMuseDataPacket(final MuseDataPacket p, final Muse muse) {
            activityRef.get().receiveMuseDataPacket(p, muse);

        }

        @Override
        public void receiveMuseArtifactPacket(final MuseArtifactPacket p, final Muse muse) {
            activityRef.get().receiveMuseArtifactPacket(p, muse);
        }
    }
}
package com.danderflieger.angleofattackindicator;

//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
//import android.content.DialogInterface;
import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
import android.content.pm.PackageManager;
import android.graphics.drawable.RotateDrawable;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
//import android.media.PlaybackParams;
//import android.media.SoundPool;
//import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
//import android.view.DragEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
//import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

//import android.database.sqlite.SQLiteDatabase;


public class MainActivity extends AppCompatActivity {

    private final static int RECEIVE_INTERVAL = 180;
    private final double warningInterpolationMultiplier = 0.3;

    RelativeLayout disclaimerLayout;
    ScrollView disclaimerScrollView;
    TextView disclaimerTextView;
    Button disclaimerAgreementButton;

    // Button Layout
    Button sensorButton;
    Button aircraftButton;
    Button indicatorButton;

    // Sensor Layout
    RelativeLayout sensorLayout;
    Button startScanningButton;
    Button stopScanningButton;
    Button connectToDevice;
    Button disconnectDevice;
    TextView peripheralTextView;
    Spinner deviceSpinner;


    // Aircraft Layout
    RelativeLayout aircraftLayout;
    Button addNewAircraftButton;
    Button deleteAircraftButton;
    Button levelFlightUpdateButton;
    Button descentAngleUpdateButton;
    Button warningAngleUpdateButton;
    Button dangerAngleUpdateButton;
    Button dangerAngleFlapsUpdateButton;
    Button turnRateOffsetUpdateButton;
//    Button slipSkidOffsetUpdateButton;
    Switch editAircraftSwitch;
    Spinner selectAircraftSpinner;

    EditText currentAngle;
    EditText currentTurnRate;
    EditText currentSlipSkid;
    EditText levelFlight;
    EditText descentAngle;
    EditText warningAngle;
    EditText dangerAngle;
    EditText warningAngleFlaps;
    EditText dangerAngleFlaps;
    EditText turnRateOffset;
    EditText ballReadingMultiplier;
    EditText addNewAircraftId;


    // Indicator Layout
    RelativeLayout arrowIndicatorLayout;
    ImageView imageView;
    VectorDrawableCompat.VFullPath dangerAnglePath;
    VectorDrawableCompat.VFullPath warningAnglePath;
    VectorDrawableCompat.VFullPath dangerAngleFlapsPath;
    VectorDrawableCompat.VFullPath warningAngleFlapsPath;
    VectorDrawableCompat.VFullPath upperGlidePathAnglePath;
    VectorDrawableCompat.VFullPath lowerGlidePathAnglePath;
    VectorDrawableCompat.VFullPath levelCruiseAnglePath;
    VectorDrawableCompat.VFullPath negativeAnglePath;
    TextView arrowCalibratedAngleTextView;
    TextView arrowSensorAngleTextView;
    MediaPlayer player;
    Button airfoilIndicatorButton;
    ImageButton volumeOnButton;
    ImageButton volumeMuteButton;
    boolean volumeOn = false;


    // Airfoil Indicator Layout
    RelativeLayout airfoilIndicatorLayout;
    ImageView airfoilImageView;
    VectorDrawableCompat.VFullPath airfoilPath;
    VectorDrawableCompat.VFullPath ballChamberPath;

    ImageView airfoilLevelFlightImageView;
    ImageView airfoilGlidePathImageView;
    ImageView airfoilWarningImageView;
    ImageView airfoilDangerImageView;
    ImageView airfoilWarningFlapsImageView;
    ImageView airfoilDangerFlapsImageView;
    ImageView airfoilNegativeAngleImageView;
    ImageView airfoilTSNeedle;
    ImageView airfoilTSBall;

    TextView airfoilCalibratedAngleTextView;
    TextView airfoilSensorAngleTextView;
    TextView airfoilTurnRateTextView;
    TextView airfoilCalibratedTurnRateTextView;
    TextView airfoilCalibratedSlipSkidTextView;
    int ANGLE_MULTIPLIER = 2;
    Button arrowIndicatorButton;


    // Bluetooth Variables
    BluetoothManager btManager;
    BluetoothAdapter btAdapter;
    BluetoothLeScanner btScanner;
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    Boolean btScanning = false;
    Boolean btConnected = false;
    int deviceIndex = 0;
    ArrayList<BluetoothDevice> devicesDiscovered = new ArrayList<>();
    ArrayList<String> devicesDiscovered_Names = new ArrayList<>();
    BluetoothGatt bluetoothGatt;

    private GoogleApiClient client;

    final String ANGLE_SERVICE_UUID_STRING              = "00000001-627e-47e5-a3fc-ddabd97aa966";
    final String READING_CHARACTERISTIC_UUID_STRING     = "00000002-627E-47E5-A3FC-DDABD97AA966";
//    final String ANGLE_CHARACTERISTIC1_UUID_STRING      = "00000002-627e-47e5-a3fc-ddabd97aa966";
//    final String TURN_RATE_CHARACTERISTIC_UUID_STRING   = "00000003-627e-47e5-a3fc-ddabd97aa966";
//    final String SLIP_SKID_CHARACTERISTIC_UUID_STRING   = "00000004-627e-47e5-a3fc-ddabd97aa966";
    final String DESCRIPTOR_UUID_STRING                 = "00002902-0000-1000-8000-00805f9b34fb"; // This never changes!

    private UUID ANGLE_SERVICE_UUID;
    private UUID READING_CHARACTERISTIC_UUID;
//    private UUID ANGLE_CHARACTERISTIC1_UUID;
//    private UUID TURN_RATE_CHARACTERISTIC_UUID;
//    private UUID SLIP_SKID_CHARACTERISTIC_UUID;
    private UUID DESCRIPTOR_UUID;

    private double levelCruiseAngleValue;
    private double turnRateValue;
    private double slipSkidValue;
    private double glidePathAngleValue;
    private double warningAngleValue;
    private double dangerAngleValue;
    private double warningAngleFlapsValue;
    private double dangerAngleFlapsValue;


    long lastMillis = 0;
    float lastAngle;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Verify that the app has permission to location services on the device - Bluetooth
        //  requires it for some odd reason
        if (ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


        // Instantiate the ButtonsLayout and add ClickListeners

        //Disclaimer Stuff
        disclaimerLayout = findViewById(R.id.DisclaimerView);
        disclaimerScrollView = findViewById(R.id.disclaimerScrollView);
        disclaimerTextView = findViewById(R.id.disclaimerTextView);
        disclaimerAgreementButton = findViewById(R.id.disclaimerAgreeButton);

        // Set a requirement for the user to scroll down if the contents of the
        // disclaimer TextView is longer than its ScrollView object (requires
        // an OnGlobalLayoutListener to know when the app loads completely)
        ViewTreeObserver viewTreeObserver = disclaimerTextView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int disclaimerTextViewHeight = disclaimerTextView.getHeight();
                    int disclaimerScrollViewHeight = disclaimerScrollView.getHeight();
                    if (disclaimerScrollViewHeight < disclaimerTextViewHeight) {
                        disclaimerAgreementButton.setText("Scroll Down Disclaimer");
                        disclaimerAgreementButton.setEnabled(false);
                        disclaimerAgreementButton.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.light_gray));
                        if (disclaimerTextViewHeight != 0)
                            disclaimerTextView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    }
                }
            });
        }

        // Once the GlobalLayout is loaded, add an OnScrollListener to the ScrollView object to
        // determine if a user has scrolled all the way to the bottom of the disclaimer text
        disclaimerScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                // if the user has scrolled to the bottom of the disclaimer, enable the accept button
                // otherwise, disable the button and change the button text to "Scroll Down Disclaimer"
                if (!disclaimerScrollView.canScrollVertically(1)) {

                    disclaimerAgreementButton.setText("Agree and Continue");
                    disclaimerAgreementButton.setEnabled(true);
                    disclaimerAgreementButton.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.danger_red));

                } else {

                    disclaimerAgreementButton.setText("Scroll Down Disclaimer");
                    disclaimerAgreementButton.setEnabled(false);
                    disclaimerAgreementButton.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.light_gray));

                }
            }


        });

        // Create an onClick listener for the accept disclaimer button
        disclaimerAgreementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                acceptDisclaimer();
            }
        });

        // Instantiate the three buttons for the different sections of the app
        sensorButton = findViewById(R.id.sensorButton);
        aircraftButton = findViewById(R.id.aircraftButton);
        indicatorButton = findViewById(R.id.indicatorButton);

        // Create onClick listeners for each button
        sensorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSensorLayout();
            }
        });
        aircraftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAircraftLayout();
            }
        });
        indicatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showIndicatorLayout();
                showAirfoilIndicatorLayout();
            }
        });



        // Instantiate the SensorLayout
        sensorLayout = findViewById(R.id.sensorLayout);
        peripheralTextView = (TextView) findViewById(R.id.peripheralTextView);
        peripheralTextView.setMovementMethod(new ScrollingMovementMethod());
        deviceSpinner = (Spinner) findViewById(R.id.deviceSpinner);
        fillAircraftSpinner();

        // Instantiate the SensorLayout buttons and add onClick listeners to each
        connectToDevice = (Button) findViewById(R.id.ConnectButton);
        connectToDevice.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                connectToDeviceSelected();
            }
        });

        disconnectDevice = (Button) findViewById(R.id.DisconnectButton);
        disconnectDevice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                disconnectDeviceSelected();
            }
        });

        startScanningButton = (Button) findViewById(R.id.startScanningButton);
        startScanningButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startScanning();
            }
        });

        stopScanningButton = (Button) findViewById(R.id.stopScanningButton);
        stopScanningButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                stopScanning();
            }
        });

        // Instantiate the AircraftLayout
        aircraftLayout = findViewById(R.id.aircraftLayout);

        // Instantiate the buttons on the AircraftLayout section
        levelFlightUpdateButton = findViewById(R.id.levelFlightUpdateButton);
        descentAngleUpdateButton = findViewById(R.id.descentAngleUpdateButton);
//        warningAngleUpdateButton = findViewById(R.id.warningAngleUpdateButton);
        dangerAngleUpdateButton = findViewById(R.id.dangerAngleUpdateButton);
        dangerAngleFlapsUpdateButton = findViewById(R.id.dangerAngleFlapsUpdateButton);
        turnRateOffsetUpdateButton = findViewById(R.id.turnRateOffsetUpdateButton);
//        slipSkidOffsetUpdateButton = findViewById(R.id.slipSkidOffsetUpdateButton);

        // Instantiate the other controls on the AircraftLayout section
        selectAircraftSpinner = findViewById(R.id.aircraftSpinner);
        editAircraftSwitch = findViewById(R.id.editAircraftSwitch);
        currentAngle = findViewById(R.id.currentAngle);
        currentTurnRate = findViewById(R.id.currentTurnRate);
        currentSlipSkid = findViewById(R.id.currentSlipSkid);
        levelFlight = findViewById(R.id.levelFlight);
        descentAngle = findViewById(R.id.descentAngle);
//        warningAngle = findViewById(R.id.warningAngle);
        dangerAngle = findViewById(R.id.dangerAngle);

        dangerAngleFlaps = findViewById(R.id.dangerAngleFlaps);

        turnRateOffset = findViewById(R.id.turnRateOffset);
        ballReadingMultiplier = findViewById(R.id.ballReadingMultiplier);
        addNewAircraftId = findViewById(R.id.addNewAircraftId);
        addNewAircraftButton = findViewById(R.id.addAircraftButton);
        deleteAircraftButton = findViewById(R.id.deleteAircraftButton);

        // Create onClicked or onCheckedChanged listeners for each control on the AircraftLayout
        editAircraftSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (selectAircraftSpinner.getChildCount() > 0) {

                    boolean checked = editAircraftSwitch.isChecked();
                    levelFlight.setEnabled(checked);
                    descentAngle.setEnabled(checked);
                    //warningAngle.setEnabled(checked);
                    dangerAngle.setEnabled(checked);
                    dangerAngleFlaps.setEnabled(checked);
                    turnRateOffset.setEnabled(checked);
                    ballReadingMultiplier.setEnabled(checked);
                    levelFlightUpdateButton.setEnabled(checked);
                    descentAngleUpdateButton.setEnabled(checked);
//                    warningAngleUpdateButton.setEnabled(checked);
                    dangerAngleUpdateButton.setEnabled(checked);
                    dangerAngleFlapsUpdateButton.setEnabled(checked);
                    turnRateOffsetUpdateButton.setEnabled(checked);

//                    slipSkidOffsetUpdateButton.setEnabled(checked);
                    deleteAircraftButton.setEnabled(checked);

                    // save values when deselecting the switch
                    if (!checked) {
                        updateAircraftValues();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No aircraft have been added. Add one at the bottom of this page.", Toast.LENGTH_LONG).show();
                    editAircraftSwitch.setChecked(false);
                }
            }
        });

        addNewAircraftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AircraftModel aircraftModel;

                try {
                    if (addNewAircraftId.getText().toString().equals("")) {
                        Toast.makeText(MainActivity.this, "Please enter an Aircraft ID to add it.", Toast.LENGTH_LONG).show();
                    } else {
                        String newAircraftId = addNewAircraftId.getText().toString();
                        aircraftModel = new AircraftModel(newAircraftId, 0, 0, 0, 0, 0, 0);

                        SQLiteOpenHelper databaseHelper = new SQLiteOpenHelper(MainActivity.this);
                        if (!databaseHelper.doesAircraftExist(newAircraftId)) {
                            databaseHelper.addAirplane(aircraftModel);
                            Toast.makeText(MainActivity.this, aircraftModel.getAircraftId() + " added.", Toast.LENGTH_SHORT).show();
                            addNewAircraftId.setText("");
                        } else {
                            Toast.makeText(MainActivity.this, "Aircraft was already in database", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    Toast.makeText(MainActivity.this, "Error adding aircraft", Toast.LENGTH_SHORT).show();
                }
                updateAircraftValues();
                fillAircraftSpinner();

            }
        });

        deleteAircraftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String aircraftIdValue = addNewAircraftId.getText().toString();

                if (aircraftIdValue.equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter an Aircraft ID to delete it.", Toast.LENGTH_LONG).show();
                } else {

                    SQLiteOpenHelper databaseHelper = new SQLiteOpenHelper(MainActivity.this);
                    Boolean aircraftDeleted = databaseHelper.deleteAirplane(aircraftIdValue);
                    if (aircraftDeleted) {
                        Toast.makeText(MainActivity.this, aircraftIdValue + " deleted from database", Toast.LENGTH_LONG).show();
                        addNewAircraftId.setText("");

                    } else {
                        Toast.makeText(MainActivity.this, "Aircraft NOT deleted", Toast.LENGTH_LONG).show();
                    }

                    updateAircraftValues();
                    fillAircraftSpinner();
                }
            }
        });

        levelFlightUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                levelFlight.setText(currentAngle.getText());
            }
        });

        descentAngleUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currentAngleReading = new BigDecimal(currentAngle.getText().toString()).setScale(2, RoundingMode.HALF_UP).doubleValue(); // Double.parseDouble(currentAngle.getText().toString());
                double levelFlightReading = new BigDecimal(levelFlight.getText().toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();  //Double.parseDouble(levelFlight.getText().toString());
//                double currentAngleReading = roundValue(Double.parseDouble(currentAngle.getText().toString()), 2);
//                double levelFlightReading = roundValue(Double.parseDouble(levelFlight.getText().toString()), 2);
                descentAngle.setText( String.valueOf( roundValue( currentAngleReading - levelFlightReading, 2)));
            }
        });

//        warningAngleUpdateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                double currentAngleReading = Double.parseDouble(currentAngle.getText().toString());
//                double levelFlightReading = Double.parseDouble(levelFlight.getText().toString());
//                warningAngle.setText( String.valueOf(currentAngleReading - levelFlightReading) );
//            }
//        });

        dangerAngleUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currentAngleReading = Double.parseDouble(currentAngle.getText().toString());
                double levelFlightReading = Double.parseDouble(levelFlight.getText().toString());
                dangerAngle.setText(String.valueOf(roundValue( currentAngleReading - levelFlightReading, 2)));
            }
        });

        dangerAngleFlapsUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currentAngleReading = Double.parseDouble(currentAngle.getText().toString());
                double levelFlightReading = Double.parseDouble(levelFlight.getText().toString());
                dangerAngleFlaps.setText(String.valueOf(roundValue( currentAngleReading - levelFlightReading,2)));
            }
        });

        turnRateOffsetUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double currentTurnRateReading = Double.parseDouble(currentTurnRate.getText().toString());
                turnRateOffset.setText(String.valueOf(currentTurnRateReading));
            }
        });

//        slipSkidOffsetUpdateButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                double currentSlipSkidReading = Double.parseDouble(currentSlipSkid.getText().toString());
//                slipSkidOffset.setText(String.valueOf(currentSlipSkidReading));
//            }
//        });

        // Instantiate the UUIDs
        ANGLE_SERVICE_UUID              = UUID.fromString(ANGLE_SERVICE_UUID_STRING);
        READING_CHARACTERISTIC_UUID     = UUID.fromString(READING_CHARACTERISTIC_UUID_STRING);
        DESCRIPTOR_UUID                 = UUID.fromString(DESCRIPTOR_UUID_STRING);


        // Instantiate the arrowIndicatorLayout -
        // This is one of the two options for the AoA visual indicator
        arrowIndicatorLayout = findViewById(R.id.arrowIndicatorLayout);

        imageView = findViewById(R.id.indicator);
        VectorChildFinder vector        = new VectorChildFinder(this, R.drawable.ic_indicatorvector, imageView);
        dangerAnglePath                 = vector.findPathByName("DangerAngle");
        warningAnglePath                = vector.findPathByName("WarningAngle");
        dangerAngleFlapsPath            = vector.findPathByName("DangerAnglePath");
        warningAngleFlapsPath           = vector.findPathByName("WarningAnglePath");
        upperGlidePathAnglePath         = vector.findPathByName("UpperGlidePathAngle");
        lowerGlidePathAnglePath         = vector.findPathByName("LowerGlidePathAngle");
        levelCruiseAnglePath            = vector.findPathByName("LevelCruiseAngle");
        negativeAnglePath               = vector.findPathByName("NegativeAngle");
        arrowCalibratedAngleTextView    = findViewById(R.id.arrowCalibratedAngleTextView);
        arrowSensorAngleTextView        = findViewById(R.id.arrowSensorAngleTextView);

        airfoilIndicatorButton          = findViewById(R.id.airfoilIndicatorButton);

        volumeOnButton                  = findViewById(R.id.volumeOnButton);
        volumeMuteButton                = findViewById(R.id.volumeMuteButton);
        toggleVolume();

        airfoilIndicatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAirfoilIndicatorLayout();
            }
        });

        volumeOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVolume();
//                volumeMuteButton.setVisibility(View.VISIBLE);
//                volumeOnButton.setVisibility(View.INVISIBLE);
//                volumeOn = false;
            }
        });

        volumeMuteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVolume();
//                volumeOnButton.setVisibility(View.VISIBLE);
//                volumeMuteButton.setVisibility(View.INVISIBLE);

            }
        });


        // Instantiate the airfoilIndicatorLayout -
        // this is the other option for the AoA visual indicator
        airfoilIndicatorLayout = findViewById(R.id.airfoilIndicatorLayout);
        airfoilImageView = findViewById(R.id.airfoilImageView);

        // I found this really helpful library for working with vector objects in Android
        // https://github.com/devendroid/VectorChildFinder
        // It really simplifies changing colors, rotations, etc.
        VectorChildFinder airfoilVector = new VectorChildFinder(this, R.drawable.ic_airfoil, airfoilImageView);
        airfoilPath = airfoilVector.findPathByName("airfoil");

        airfoilLevelFlightImageView         = findViewById(R.id.airfoilLevelFlightImageView);
        airfoilWarningImageView             = findViewById(R.id.airfoilWarningImageView);
        airfoilDangerImageView              = findViewById(R.id.airfoilDangerImageView);

        airfoilWarningFlapsImageView        = findViewById(R.id.airfoilWarningFlapsImageView);
        airfoilDangerFlapsImageView         = findViewById(R.id.airfoilDangerFlapsImageView);

        airfoilGlidePathImageView           = findViewById(R.id.airfoilGlidePathImageView);
        airfoilNegativeAngleImageView       = findViewById(R.id.airfoilNegativeAngleImageView);
        airfoilTSNeedle                     = findViewById(R.id.TS_Needle);
        airfoilTSBall                       = findViewById(R.id.TS_Ball);

        airfoilCalibratedAngleTextView      = findViewById(R.id.airfoilCalibratedAngleTextView);
        airfoilSensorAngleTextView          = findViewById(R.id.airfoilSensorAngleTextView);
        airfoilTurnRateTextView             = findViewById(R.id.airfoilSensorTurnRateTextView);
        airfoilCalibratedTurnRateTextView   = findViewById(R.id.airfoilCalibratedTurnRateTextView);
        airfoilCalibratedSlipSkidTextView   = findViewById(R.id.airfoilCalibratedSlipSkidTextView);

        arrowIndicatorButton                = findViewById(R.id.arrowIndicatorButton);

        arrowIndicatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showArrowIndicatorLayout();
            }
        });

        // Instantiate Bluetooth Stuff
        btManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();
        btScanner = btAdapter.getBluetoothLeScanner();

        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        showDisclaimer();


    }

    private void updateAircraftValues() {

        if (selectAircraftSpinner.getChildCount() > 0) {
            SQLiteOpenHelper sqLiteOpenHelper = new SQLiteOpenHelper(MainActivity.this);
            String aircraftIdValue = selectAircraftSpinner.getSelectedItem().toString();
            double levelAngleValue = Double.parseDouble(levelFlight.getText().toString());
            double descentAngleValue = Double.parseDouble(descentAngle.getText().toString());
            double dangerAngleValue = Double.parseDouble(dangerAngle.getText().toString());
            double dangerAngleFlapsValue = Double.parseDouble(dangerAngleFlaps.getText().toString());
            double turnRate         = Double.parseDouble(turnRateOffset.getText().toString());
            double ballReading      = Double.parseDouble(ballReadingMultiplier.getText().toString());

//            AircraftModel aircraftModel = new AircraftModel(aircraftIdValue, levelAngleValue, descentAngleValue, warningAngleValue, dangerAngleValue, turnRate, ballReading);
            AircraftModel aircraftModel = new AircraftModel(aircraftIdValue, levelAngleValue, descentAngleValue, dangerAngleValue, dangerAngleFlapsValue, turnRate, ballReading);

            sqLiteOpenHelper.updateAirplane(aircraftModel);
        } else {
            Toast.makeText(getApplicationContext(), "No aircraft have been added. Add one at the bottom of this page.", Toast.LENGTH_LONG).show();
        }

    }

    private void fillAircraftSpinner() {
        SQLiteOpenHelper sqLiteOpenHelper = new SQLiteOpenHelper(MainActivity.this);
        selectAircraftSpinner = findViewById(R.id.aircraftSpinner);
        List<String> aircraftIds = new ArrayList<>();
        aircraftIds = sqLiteOpenHelper.getAirplanesIds();
        ArrayAdapter<String> aircraftSpinnerAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, aircraftIds);
        aircraftSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        selectAircraftSpinner.setAdapter(aircraftSpinnerAdapter);

        selectAircraftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                AircraftModel aircraftModel = sqLiteOpenHelper.getAirplane(selectAircraftSpinner.getSelectedItem().toString());
                setAircraftEditTexts(aircraftModel);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

    }

    private void setAircraftEditTexts(AircraftModel aircraftModel) {

        levelFlight.setText(String.valueOf(aircraftModel.getLevelCruiseAngle()));
        descentAngle.setText(String.valueOf(aircraftModel.getDescentAngle()));
//        warningAngle.setText(String.valueOf(aircraftModel.getWarningAngle()));
        dangerAngle.setText(String.valueOf(aircraftModel.getDangerAngle()));
        dangerAngleFlaps.setText(String.valueOf(aircraftModel.getDangerAngleFlaps()));
        turnRateOffset.setText(String.valueOf(aircraftModel.getTurnRate()));
        ballReadingMultiplier.setText(String.valueOf(aircraftModel.getBallReadingMultiplier()));

    }

    private void acceptDisclaimer() {
        showSensorLayout();
    }

    private void showDisclaimer() {
        disclaimerLayout.setVisibility(View.VISIBLE);
        sensorLayout.setVisibility(View.GONE);
        aircraftLayout.setVisibility(View.GONE);
        arrowIndicatorLayout.setVisibility(View.GONE);

        int disclaimerTextViewHeight = disclaimerTextView.getHeight();
        int disclaimerScrollViewHeight = disclaimerScrollView.getHeight();
        if (disclaimerScrollViewHeight < disclaimerTextViewHeight) {
            disclaimerAgreementButton.setText("Scroll Down Disclaimer");
            disclaimerAgreementButton.setEnabled(false);
            disclaimerAgreementButton.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.light_gray));
        }
    }

    private void showSensorLayout() {

        disclaimerLayout.setVisibility(View.GONE);
        sensorLayout.setVisibility(View.VISIBLE);
        aircraftLayout.setVisibility(View.GONE);
        arrowIndicatorLayout.setVisibility(View.GONE);
        airfoilIndicatorLayout.setVisibility(View.GONE);

    }

    private void showAircraftLayout() {

        disclaimerLayout.setVisibility(View.GONE);
        sensorLayout.setVisibility(View.GONE);
        aircraftLayout.setVisibility(View.VISIBLE);
        arrowIndicatorLayout.setVisibility(View.GONE);
        airfoilIndicatorLayout.setVisibility(View.GONE);

    }

    private void showArrowIndicatorLayout() {
        if (btConnected) {

            if (selectAircraftSpinner.getCount() > 0){

                disclaimerLayout.setVisibility(View.GONE);
                sensorLayout.setVisibility(View.GONE);
                aircraftLayout.setVisibility(View.GONE);
                arrowIndicatorLayout.setVisibility(View.VISIBLE);
                airfoilIndicatorLayout.setVisibility(View.GONE);

            } else {

                Toast.makeText(getApplicationContext(), "No aircraft selected", Toast.LENGTH_LONG).show();
                showAircraftLayout();

            }
        } else {

            Toast.makeText(getApplicationContext(), "No sensor connected.", Toast.LENGTH_LONG).show();
            showSensorLayout();

        }
    }

    private void showAirfoilIndicatorLayout() {
        if (btConnected) {
            if (selectAircraftSpinner.getCount() > 0){
                disclaimerLayout.setVisibility(View.GONE);
                sensorLayout.setVisibility(View.GONE);
                aircraftLayout.setVisibility(View.GONE);
                arrowIndicatorLayout.setVisibility(View.GONE);
                airfoilIndicatorLayout.setVisibility(View.VISIBLE);


                // grab all of the values set on the Aircraft tab
                glidePathAngleValue     = isNumeric(descentAngle.getText().toString())  ? Double.parseDouble(descentAngle.getText().toString()) : 0.0;

                //                warningAngleValue       = isNumeric(warningAngle.getText().toString())  ? Double.parseDouble(warningAngle.getText().toString()) : 0.0;

                dangerAngleValue        = isNumeric(dangerAngle.getText().toString())   ? Double.parseDouble(dangerAngle.getText().toString()) : 0.0;
                warningAngleValue       = dangerAngleValue < 0.0 ? dangerAngleValue - (dangerAngleValue * warningInterpolationMultiplier): 0.0;

                dangerAngleFlapsValue   = isNumeric(dangerAngleFlaps.getText().toString()) ? Double.parseDouble(dangerAngleFlaps.getText().toString()) : 0.0;
                warningAngleFlapsValue  = dangerAngleFlapsValue < 0.0 ? dangerAngleFlapsValue - (dangerAngleFlapsValue * warningInterpolationMultiplier) : 0.0;

                warningAngleFlapsValue = warningAngleFlapsValue + 0;

            } else {
                Toast.makeText(getApplicationContext(), "No aircraft selected", Toast.LENGTH_LONG).show();
                showAircraftLayout();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No sensor connected.", Toast.LENGTH_LONG).show();
            showSensorLayout();
        }
    }

    private void toggleVolume() {
        if (volumeOn) {
            volumeMuteButton.setVisibility(View.VISIBLE);
            volumeOnButton.setVisibility(View.INVISIBLE);
            volumeOn = false;
        } else {
            volumeMuteButton.setVisibility(View.INVISIBLE);
            volumeOnButton.setVisibility(View.VISIBLE);
            volumeOn = true;
        }
    }

    private boolean isNumeric(String d) {
        if (d == null || d.length() == 0) {
            return false;
        }

        try {
            Double.parseDouble(d);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public void connectToDeviceSelected() {

        if (deviceSpinner.getChildCount() > 0) {
            int deviceSelected = deviceSpinner.getSelectedItemPosition();
            bluetoothGatt = devicesDiscovered.get(deviceSelected).connectGatt(this, false, btleGattCallback);
        } else {
            Toast.makeText(getApplicationContext(), "No sensors found.", Toast.LENGTH_LONG).show();
            connectToDevice.setVisibility(View.INVISIBLE);
        }

    }

    public void disconnectDeviceSelected() {
        peripheralTextView.append("Disconnecting from device\n");

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);

        while (bluetoothManager.getConnectionState(bluetoothGatt.getDevice(), BluetoothProfile.GATT) != BluetoothGatt.STATE_DISCONNECTED) {
            bluetoothGatt.close();
            bluetoothGatt.disconnect();
        }
        btConnected = false;

        Toast.makeText(getApplicationContext(), "Sensor disconnected.", Toast.LENGTH_LONG).show();

        deviceSpinner.setAdapter(null);
        peripheralTextView.append("device disconnected\n");

        disconnectDevice.setVisibility(View.INVISIBLE);
//        connectToDevice.setVisibility(View.VISIBLE);
        connectToDevice.setVisibility(deviceSpinner.getChildCount() > 0 ? View.VISIBLE : View.INVISIBLE);

    }

    public void startScanning() {
        System.out.println("Start scanning for BT Devices ...");
        btScanning = true;
        deviceIndex = 0;
        devicesDiscovered.clear();
        peripheralTextView.setText("");
        peripheralTextView.append("Start scanning for BT Devices ...\n");
        devicesDiscovered_Names.clear();

        startScanningButton.setVisibility(View.INVISIBLE);
        stopScanningButton.setVisibility(View.VISIBLE);
        disconnectDevice.setVisibility(View.INVISIBLE);
        connectToDevice.setVisibility(View.INVISIBLE);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.startScan(leScanCallback);
            }
        });
    }

    public void stopScanning() {
        System.out.println("stopping scan");
        peripheralTextView.append("Stopping Scan\n");
        btScanning = false;
        startScanningButton.setVisibility(View.VISIBLE);
        stopScanningButton.setVisibility(View.INVISIBLE);

        if (devicesDiscovered.size() < 1) {
            disconnectDevice.setVisibility(View.INVISIBLE);
            connectToDevice.setVisibility(View.INVISIBLE);
        } else {
            disconnectDevice.setVisibility(View.INVISIBLE);
            connectToDevice.setVisibility(View.VISIBLE);
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                btScanner.stopScan(leScanCallback);
            }
        });
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {

        if (gattServices == null) return;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            if (gattService.getUuid().equals(ANGLE_SERVICE_UUID)) {

                final String uuid = gattService.getUuid().toString();
                System.out.println("Service discovered: " + uuid);
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        peripheralTextView.append("Service disovered: " + uuid + "\n");
                    }
                });



                new ArrayList<HashMap<String, String>>();
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();

                // Loops through available Characteristics.
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

                    final String charUuid = gattCharacteristic.getUuid().toString();
                    //String charValue = new String(gattCharacteristic.getValue());
                    System.out.println("Characteristic discovered for service: " + charUuid);
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            peripheralTextView.append(
                                    "Characteristic discovered for service: " + charUuid
                                            + "\n"
                            );
                        }
                    });

                }
            }
        }
    }

    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result.getDevice().getName() != null && result.getDevice().getName().equals("DanDerFlieger")) {

                if (!devicesDiscovered.contains(result.getDevice())) {

                    connectToDevice.setEnabled(true);

                    peripheralTextView.append(
                            "Index: " + deviceIndex +
                                    ", Device Name: " + result.getDevice().getName() +
                                    " rssi: " + result.getRssi() + "\n");

                    devicesDiscovered.add(result.getDevice());
                    deviceIndex++;
                    final int scrollAmount = peripheralTextView.getLayout().getLineTop(peripheralTextView.getLineCount()) - peripheralTextView.getHeight();
                    if (scrollAmount > 0) {
                        peripheralTextView.scrollTo(0, scrollAmount);
                    }

                    devicesDiscovered_Names.add(result.getDevice().getName() + " " + result.getDevice().getAddress());
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, devicesDiscovered_Names);
                    deviceSpinner.setAdapter(adapter);

                }
            }
        }
    };



    // Device connect call back
    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {



        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {

            BluetoothGattService service = gatt.getService(ANGLE_SERVICE_UUID);
            BluetoothGattCharacteristic readingCharacteristic = service.getCharacteristic(READING_CHARACTERISTIC_UUID);

            gatt.readCharacteristic(readingCharacteristic);

            DecimalFormat df = new DecimalFormat("#.#");
            DecimalFormat dfTS = new DecimalFormat("#.###");

            String reading = new String(characteristic.getValue(), StandardCharsets.UTF_8);

            System.out.println(String.format("reading: %s", reading));

            String[] readings = reading.split(",");

            if (readings.length == 3) {
                float angleReading = Float.parseFloat(readings[0]);
                if (Float.isNaN(angleReading)) {
                    angleReading = lastAngle;
                }

                float turnRateReading = Float.parseFloat(readings[1]);
                if (Float.isNaN(turnRateReading)) {
                    turnRateReading = 0.00f;
                }

                float slipSkidReading = Float.parseFloat(readings[2]);
                if (Float.isNaN(slipSkidReading)) {
                    slipSkidReading = 0.00f;
                }

                String strAngleReading = df.format(angleReading);
                float formattedAngleReading = Float.parseFloat(strAngleReading);
                levelCruiseAngleValue = isNumeric(levelFlight.getText().toString()) ? Double.parseDouble(levelFlight.getText().toString()) : 0.0;
                dangerAngleValue = Double.parseDouble(dangerAngle.getText().toString());


                String strTurnRateReading = df.format(turnRateReading);
                float formattedTurnRateReading = Float.parseFloat( isNumeric(strTurnRateReading)?strTurnRateReading:"0.0");
                turnRateValue = isNumeric(turnRateOffset.getText().toString()) ? Double.parseDouble(turnRateOffset.getText().toString()) : 0.0f;

                String strSlipSkidReading = dfTS.format(slipSkidReading);
                float formattedSlipSkidReading = Float.parseFloat( isNumeric(strSlipSkidReading)?strSlipSkidReading:"0.0");


                float finalAngleReading = angleReading;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {


//                        currentAngle.setText(String.format("%.2f", reading));
                        currentAngle.setText(strAngleReading);
                        currentTurnRate.setText(strTurnRateReading);
                        currentSlipSkid.setText(strSlipSkidReading);

                        // these two floats determine how opaque the backgrounds
                        // are on the arrow-type indicator - lightOn is fully opaque,
                        // lightOff is mostly transparent.
                        float lightOn = 255f;
                        float lightOff = 0.2f;

                        // Arrow Indicator Type
                        if (arrowIndicatorLayout.getVisibility() == View.VISIBLE) {

                            float calibratedReading = formattedAngleReading - (float) levelCruiseAngleValue;
                            arrowSensorAngleTextView.setText(strAngleReading);
                            arrowCalibratedAngleTextView.setText(String.format("%.1f", calibratedReading));

                            // Danger Angle
                            if (calibratedReading <= dangerAngleValue + 1.0) {
                                dangerAnglePath.setFillAlpha(lightOn);
                            } else {
                                dangerAnglePath.setFillAlpha(lightOff);
                            }

                            // Warning Angle
                            if (calibratedReading <= warningAngleValue + 1 && calibratedReading >= dangerAngleValue) { //warningAngle && reading2 >= dangerAngle) {
                                warningAnglePath.setFillAlpha(lightOn);
                            } else {
                                warningAnglePath.setFillAlpha(lightOff);
                            }

                            // Upper Glide Path Angle
                            if (calibratedReading <= glidePathAngleValue + 1 && calibratedReading >= warningAngleValue) {
                                upperGlidePathAnglePath.setFillAlpha(lightOn);
                            } else {
                                upperGlidePathAnglePath.setFillAlpha(lightOff);
                            }

                            // Lower Glide Path Angle
                            if (calibratedReading <= -1 && calibratedReading >= glidePathAngleValue - 1) {
                                lowerGlidePathAnglePath.setFillAlpha(lightOn);
                            } else {
                                lowerGlidePathAnglePath.setFillAlpha(lightOff);
                            }

                            // Level Cruise
                            if (calibratedReading <= 2 && calibratedReading >= -2) {
                                levelCruiseAnglePath.setFillAlpha(lightOn);
                            } else {
                                levelCruiseAnglePath.setFillAlpha(lightOff);
                            }

                            // Negative Angle
                            if (calibratedReading >= 1) {
                                negativeAnglePath.setFillAlpha(lightOn);
                            } else {
                                negativeAnglePath.setFillAlpha(lightOff);
                            }

                            imageView.invalidate();
                            controlAudibleWarning(calibratedReading);

                        }

                        // Airfoil Indicator Type
                        if (airfoilIndicatorLayout.getVisibility() == View.VISIBLE) {

                            float calibratedReading = formattedAngleReading - (float) levelCruiseAngleValue;
                            float calibratedTurnRateReading = formattedTurnRateReading - (float) turnRateValue;
                            float calibratedSlipSkidReading = formattedSlipSkidReading - (float) slipSkidValue;

                            if (calibratedReading < -60) {
                                calibratedReading = -60;
                            } else if (calibratedReading > 60) {
                                calibratedReading = 60;
                            }

                            airfoilSensorAngleTextView.setText(String.format("%.1f", finalAngleReading));
                            airfoilCalibratedAngleTextView.setText(String.format("%.1f", calibratedReading));
                            //airfoilTurnRateTextView.setText(String.format("%.1f", formattedTurnRateReading));
                            airfoilTurnRateTextView.setText(String.format("%.2f", calibratedTurnRateReading));
                            airfoilCalibratedTurnRateTextView.setText(String.format("%.2f", calibratedTurnRateReading));
                            airfoilCalibratedSlipSkidTextView.setText(String.format("%.2f", calibratedSlipSkidReading));

                            setAirfoilArcsPositions();

                            // Change the color of the airfoil, depending on its current value
                                if (calibratedReading - 1.0f <= dangerAngleValue) {
                                    // Turn it RED
                                    airfoilPath.setFillColor(0xFFFF5555);
                                    airfoilPath.setStrokeColor(0xFFCC2222);
//                                    } else if (calibratedReading - 1.0 <= warningAngleValue) {
//                                        // Turn it YELLOW
//                                        airfoilPath.setFillColor(0xFFFFFF55);
//                                        airfoilPath.setStrokeColor(0xFFCCAA33);
                                } else {
                                    //Turn it BLUE
                                    airfoilPath.setFillColor(0xFF9BBAF8);
                                    airfoilPath.setStrokeColor(0xFF728FC8);
                                }

                            //airfoilImageView.invalidate();

                            if (Float.isNaN(lastAngle)) {
                                lastAngle = 0.0f;
                            }

                            RotateAnimation rotateAirfoil = new RotateAnimation(
                                    -(lastAngle * ANGLE_MULTIPLIER), -(calibratedReading * ANGLE_MULTIPLIER), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                            );
                            rotateAirfoil.setDuration(RECEIVE_INTERVAL);
                            //rotateAirfoil.setRepeatCount(Animation.INFINITE);
                            rotateAirfoil.setInterpolator(new FastOutSlowInInterpolator());// AccelerateDecelerateInterpolator()); // LinearInterpolator()); // new AccelerateDecelerateInterpolator()); // LinearInterpolator());
                            rotateAirfoil.setFillAfter(true);
                            airfoilImageView.startAnimation(rotateAirfoil);

                            //System.out.println(String.format("reading: %s | levelCruiseAngleValue: %s | warningAngleValue: %s | dangerAngleValue: %s | calibratedReading: %s", reading, levelCruiseAngleValue, warningAngleValue, dangerAngleValue, calibratedReading));

                            controlAudibleWarning(calibratedReading);

                            lastAngle = (calibratedReading);
                            //lastMillis = currentMillis;

                            float turnRateNeedleDegrees;
                            if (calibratedTurnRateReading * 10 > 60f) {
                                turnRateNeedleDegrees = 60f;
                            } else if (calibratedTurnRateReading * 10 < -60f) {
                                turnRateNeedleDegrees = -60f;
                            } else {
                                turnRateNeedleDegrees = calibratedTurnRateReading * 10;
                            }

                            RotateAnimation rotateTurnAndSlipNeedle = new RotateAnimation(
                                turnRateNeedleDegrees,
                                turnRateNeedleDegrees,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f
                            );
                            rotateTurnAndSlipNeedle.setDuration(RECEIVE_INTERVAL);
                            rotateTurnAndSlipNeedle.setInterpolator(new FastOutSlowInInterpolator());
                            airfoilTSNeedle.startAnimation(rotateTurnAndSlipNeedle);

                            float ballSway;
                            ImageView gauge = findViewById(R.id.TS_GaugeFace);
                            int gaugeWidth = (gauge.getWidth()/3);
                            float swayMultiplier = -250.0f;

                            if (!Float.isNaN(Float.parseFloat(ballReadingMultiplier.getText().toString()))) {
                                swayMultiplier = Float.parseFloat(ballReadingMultiplier.getText().toString());
                            }

//                            if (calibratedSlipSkidReading * swayMultiplier > 90) {
//                                ballSway = 90;
//                            } else if (calibratedSlipSkidReading * swayMultiplier < -90) {
//                                ballSway = -90;
//                            } else {
//                                ballSway = calibratedSlipSkidReading * swayMultiplier;
//                            }

                            if ((calibratedSlipSkidReading * swayMultiplier) > gaugeWidth) {
                                ballSway = gaugeWidth/2;
                            } else if ((calibratedSlipSkidReading * swayMultiplier) < -gaugeWidth) {
                                ballSway = -(gaugeWidth/2);
                            } else {
                                ballSway = (calibratedSlipSkidReading * swayMultiplier)/2;
                            }

                            ObjectAnimator slideSlipSkidBall = ObjectAnimator.ofFloat(airfoilTSBall, "translationX", ballSway);
                            slideSlipSkidBall.setDuration(RECEIVE_INTERVAL);
                            slideSlipSkidBall.start();

//                            }
                        }
                    }
                });
            }
        }

        // This function will play or stop the "NOSE DOWN" audible warning
        private void controlAudibleWarning(float calibratedReading) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Audible Danger Warning
                    // check to see if the calibrated reading is within 1 degree of the
                    // configured danger angle. If so, play the audible warning. If not. Stop
                    // the audible warning
                    if (calibratedReading <= (dangerAngleValue) + 1.0 && volumeOn) {
//                        System.out.println(
//                                String.format(
//                                        "Sound playing. calibratedReading: %s | dangerAngleValue: %s",
//                                        calibratedReading,
//                                        dangerAngleValue
//                                )
//                        );
                        // check to see if a MediaPlayer object is instantiated. If not, instantiate one
                        if (player == null) {
                            player = MediaPlayer.create(MainActivity.this, R.raw.sound1);
                            player.setLooping(true);
                        }
                        // play the sound clip
                        player.start();
                    } else {
                        // stop the sound clip
                        stopPlayer();
                    }

                    // as a catch-all, if the bluetooth device disconnects, stop the audible warning
                    if (btConnected == false) {
                        stopPlayer();
                    }
                }
            });
        }

        // This function will gather the various angles you configured in the Aircraft layout and
        // rotate the arcs (all pre-created vector images) to the correct positions on the
        // airfoil indicator screen. E.g. if you change the "danger" angle and then go back to
        // the Indicator layout, the red arc will rotate so the bottom of the arc matches the
        // angle you set for it in the Aircraft layout.
        public void setAirfoilArcsPositions() {
            if (btConnected) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Level Cruise Arc (Green)
                        RotateAnimation rotateAirfoilLevelArc = new RotateAnimation(
                                -0.0f, -0.0f,  Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                        );
                        rotateAirfoilLevelArc.setDuration(1000);
                        rotateAirfoilLevelArc.setRepeatCount(Animation.INFINITE);
                        airfoilLevelFlightImageView.startAnimation(rotateAirfoilLevelArc);

                        // Glide Path Arc (Blue)
                        RotateAnimation rotateAirfoilGlidePathArc = new RotateAnimation(
                                -(float)((glidePathAngleValue) * ANGLE_MULTIPLIER), -(float)((glidePathAngleValue) * ANGLE_MULTIPLIER), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                        );
                        rotateAirfoilGlidePathArc.setDuration(1000);
                        rotateAirfoilGlidePathArc.setRepeatCount(Animation.INFINITE);
                        airfoilGlidePathImageView.startAnimation(rotateAirfoilGlidePathArc);

                        // Warning Angle Arc (Yellow)
                        RotateAnimation rotateAirfoilWarningArc = new RotateAnimation(
                                -(float)((warningAngleValue) * ANGLE_MULTIPLIER), -(float)((warningAngleValue) * ANGLE_MULTIPLIER), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                        );
                        rotateAirfoilWarningArc.setDuration(1000);
                        rotateAirfoilWarningArc.setRepeatCount(Animation.INFINITE);
                        airfoilWarningImageView.startAnimation(rotateAirfoilWarningArc);

                        // Danger Angle Arc (Red)
                        RotateAnimation rotateAirfoilDangerArc = new RotateAnimation(
                                -(float)((dangerAngleValue) * ANGLE_MULTIPLIER), -(float)((dangerAngleValue) * ANGLE_MULTIPLIER), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                        );
                        rotateAirfoilDangerArc.setDuration(1000);
                        rotateAirfoilDangerArc.setRepeatCount(Animation.INFINITE);
                        airfoilDangerImageView.startAnimation(rotateAirfoilDangerArc);

                        // *** FLAPS *** Warning Angle Arc (Yellow)
                        RotateAnimation rotateAirfoilWarningFlapsArc = new RotateAnimation(
                                -(float)((warningAngleFlapsValue) * ANGLE_MULTIPLIER), -(float)((warningAngleFlapsValue) * ANGLE_MULTIPLIER), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                        );
                        rotateAirfoilWarningFlapsArc.setDuration(1000);
                        rotateAirfoilWarningFlapsArc.setRepeatCount(Animation.INFINITE);
                        airfoilWarningFlapsImageView.startAnimation(rotateAirfoilWarningFlapsArc);

                        // *** FLAPS *** Danger Angle Arc (Red)
                        RotateAnimation rotateAirfoilDangerFlapsArc = new RotateAnimation(
                                -(float)((dangerAngleFlapsValue) * ANGLE_MULTIPLIER), -(float)((dangerAngleFlapsValue) * ANGLE_MULTIPLIER), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                        );
                        rotateAirfoilDangerFlapsArc.setDuration(1000);
                        rotateAirfoilDangerFlapsArc.setRepeatCount(Animation.INFINITE);
                        airfoilDangerFlapsImageView.startAnimation(rotateAirfoilDangerFlapsArc);

                    }
                });
            }
        }


        // function to stop the "Nose Down" audible warning
        private void stopPlayer() {
            if (player != null) {
                player.release();
                player = null;
            }
        }




        // this will get called when a bluetooth device connects or disconnects
        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            System.out.println(newState);
            switch (newState) {
                case 0:
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Sensor disconnected.", Toast.LENGTH_LONG).show();
                            deviceSpinner.setAdapter(null);
                            peripheralTextView.append("device disconnected\n");
                            connectToDevice.setEnabled(false);
                            connectToDevice.setVisibility(View.VISIBLE);
                            disconnectDevice.setVisibility(View.INVISIBLE);
                            showSensorLayout();
                            btConnected = false;
                            stopPlayer();
                        }
                    });
                    break;
                case 2:
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            peripheralTextView.append("device connected\n");
                            connectToDevice.setVisibility(View.INVISIBLE);
                            disconnectDevice.setVisibility(View.VISIBLE);
                            showAircraftLayout();
                            //setAirfoilArcsPositions();
                            btConnected = true;
                        }
                    });

                    // discover services and characteristics for this device
                    gatt.discoverServices();

                    break;
                default:
                    // This should never happen - but if bluetooth is ever in an unknown state
                    // (e.g. not connected or disconnected) throw an error about it
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            peripheralTextView.append("we encounterned an unknown state, uh oh\n");
                        }
                    });
                    break;
            }
        }

        // This runs when a Bluetooth service is discovered
        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a BluetoothGatt.discoverServices() call
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    peripheralTextView.append("device services have been discovered\n");

                    BluetoothGattService angleService = gatt.getService(ANGLE_SERVICE_UUID);
                    BluetoothGattCharacteristic readingCharacteristic = angleService.getCharacteristic(READING_CHARACTERISTIC_UUID);
                    BluetoothGattDescriptor readingDescriptor = readingCharacteristic.getDescriptor(DESCRIPTOR_UUID);
                    readingDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                    gatt.writeDescriptor(readingDescriptor);
                    gatt.setCharacteristicNotification(readingCharacteristic, true);
                    gatt.writeCharacteristic(readingCharacteristic);
                    gatt.readCharacteristic(readingCharacteristic);

                }
            });
            displayGattServices(bluetoothGatt.getServices());
        }

        @Override
        // Result of a characteristic read operation - a bluetooth "service" will have one or more
        // "characteristics" associated with it
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {

            //peripheralTextView.append(characteristic.getValue().toString());

        }

        @Override
        // Result of a descriptor read operation - a characteristic will have one or more
        // "descriptors" associated with it
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("onDescriptionRead successful!!");
            }
        }

    };

    public static double roundValue (double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }

}
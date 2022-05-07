package com.danderflieger.angleofattackindicator;

//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

//import android.database.sqlite.SQLiteDatabase;


public class MainActivity extends AppCompatActivity {

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
    Switch editAircraftSwitch;
    Spinner selectAircraftSpinner;

    EditText currentAngle;
    EditText levelFlight;
    EditText descentAngle;
    EditText warningAngle;
    EditText dangerAngle;
    EditText addNewAircraftId;


    // Indicator Layout
    RelativeLayout arrowIndicatorLayout;
    ImageView imageView;
    VectorDrawableCompat.VFullPath dangerAnglePath;
    VectorDrawableCompat.VFullPath warningAnglePath;
    VectorDrawableCompat.VFullPath upperGlidePathAnglePath;
    VectorDrawableCompat.VFullPath lowerGlidePathAnglePath;
    VectorDrawableCompat.VFullPath levelCruiseAnglePath;
    VectorDrawableCompat.VFullPath negativeAnglePath;
    TextView arrowCalibratedAngleTextView;
    TextView arrowSensorAngleTextView;
    MediaPlayer player;
    Button airfoilIndicatorButton;

    // Airfoil Indicator Layout
    RelativeLayout airfoilIndicatorLayout;
    ImageView airfoilImageView;
    VectorDrawableCompat.VFullPath airfoilPath;
    ImageView airfoilLevelFlightImageView;
    ImageView airfoilGlidePathImageView;
    ImageView airfoilWarningImageView;
    ImageView airfoilDangerImageView;
    ImageView airfoilNegativeAngleImageView;
    TextView airfoilCalibratedAngleTextView;
    TextView airfoilSensorAngleTextView;
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

    final String ANGLE_SERVICE_UUID_STRING          = "00000001-627e-47e5-a3fc-ddabd97aa966";
    final String ANGLE_CHARACTERISTIC1_UUID_STRING  = "00000002-627e-47e5-a3fc-ddabd97aa966";
    //final String MESSAGE_CHARACTERISTIC_UUID_STRING = "00000003-627e-47e5-a3fc-ddabd97aa966";
    final String DESCRIPTOR_UUID_STRING             = "00002902-0000-1000-8000-00805f9b34fb"; // This never changes!

    private UUID ANGLE_SERVICE_UUID;
    private UUID ANGLE_CHARACTERISTIC1_UUID;
    private UUID MESSAGE_CHARACTERISTIC_UUID;
    private UUID DESCRIPTOR_UUID;

    private double levelCruiseAngleValue;
    private double glidePathAngleValue;
    private double warningAngleValue;
    private double dangerAngleValue;

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
        warningAngleUpdateButton = findViewById(R.id.warningAngleUpdateButton);
        dangerAngleUpdateButton = findViewById(R.id.dangerAngleUpdateButton);

        // Instantiate the other controls on the AircraftLayout section
        selectAircraftSpinner = findViewById(R.id.aircraftSpinner);
        editAircraftSwitch = findViewById(R.id.editAircraftSwitch);
        currentAngle = findViewById(R.id.currentAngle);
        levelFlight = findViewById(R.id.levelFlight);
        descentAngle = findViewById(R.id.descentAngle);
        warningAngle = findViewById(R.id.warningAngle);
        dangerAngle = findViewById(R.id.dangerAngle);
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
                    warningAngle.setEnabled(checked);
                    dangerAngle.setEnabled(checked);
                    levelFlightUpdateButton.setEnabled(checked);
                    descentAngleUpdateButton.setEnabled(checked);
                    warningAngleUpdateButton.setEnabled(checked);
                    dangerAngleUpdateButton.setEnabled(checked);
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
                        aircraftModel = new AircraftModel(newAircraftId, 0, 0, 0, 0);

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
                double currentAngleReading = Double.parseDouble(currentAngle.getText().toString());
                double levelFlightReading = Double.parseDouble(levelFlight.getText().toString());
                descentAngle.setText( String.valueOf(currentAngleReading - levelFlightReading) );
            }
        });

        warningAngleUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currentAngleReading = Double.parseDouble(currentAngle.getText().toString());
                double levelFlightReading = Double.parseDouble(levelFlight.getText().toString());
                warningAngle.setText( String.valueOf(currentAngleReading - levelFlightReading) );
            }
        });

        dangerAngleUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double currentAngleReading = Double.parseDouble(currentAngle.getText().toString());
                double levelFlightReading = Double.parseDouble(levelFlight.getText().toString());
                dangerAngle.setText(String.valueOf(currentAngleReading - levelFlightReading));
            }
        });




        // Instantiate the arrowIndicatorLayout -
        // This is one of the two options for the AoA visual indicator
        arrowIndicatorLayout = findViewById(R.id.arrowIndicatorLayout);

        ANGLE_SERVICE_UUID          = UUID.fromString(ANGLE_SERVICE_UUID_STRING);
        ANGLE_CHARACTERISTIC1_UUID  = UUID.fromString(ANGLE_CHARACTERISTIC1_UUID_STRING);
        //MESSAGE_CHARACTERISTIC_UUID = UUID.fromString(MESSAGE_CHARACTERISTIC_UUID_STRING);
        DESCRIPTOR_UUID         = UUID.fromString(DESCRIPTOR_UUID_STRING);
        imageView = findViewById(R.id.indicator);
        VectorChildFinder vector        = new VectorChildFinder(this, R.drawable.ic_indicatorvector, imageView);
        dangerAnglePath                 = vector.findPathByName("DangerAngle");
        warningAnglePath                = vector.findPathByName("WarningAngle");
        upperGlidePathAnglePath         = vector.findPathByName("UpperGlidePathAngle");
        lowerGlidePathAnglePath         = vector.findPathByName("LowerGlidePathAngle");
        levelCruiseAnglePath            = vector.findPathByName("LevelCruiseAngle");
        negativeAnglePath               = vector.findPathByName("NegativeAngle");
        arrowCalibratedAngleTextView    = findViewById(R.id.arrowCalibratedAngleTextView);
        arrowSensorAngleTextView        = findViewById(R.id.arrowSensorAngleTextView);
        airfoilIndicatorButton          = findViewById(R.id.airfoilIndicatorButton);
        airfoilIndicatorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAirfoilIndicatorLayout();
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

        airfoilLevelFlightImageView     = findViewById(R.id.airfoilLevelFlightImageView);
        airfoilWarningImageView         = findViewById(R.id.airfoilWarningImageView);
        airfoilDangerImageView          = findViewById(R.id.airfoilDangerImageView);
        airfoilGlidePathImageView       = findViewById(R.id.airfoilGlidePathImageView);
        airfoilNegativeAngleImageView   = findViewById(R.id.airfoilNegativeAngleImageView);
        airfoilCalibratedAngleTextView  = findViewById(R.id.airfoilCalibratedAngleTextView);
        airfoilSensorAngleTextView      = findViewById(R.id.airfoilSensorAngleTextView);
        arrowIndicatorButton            = findViewById(R.id.arrowIndicatorButton);
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

//        AudioAttributes audioAttributes = new AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .build();

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
            double warningAngleValue = Double.parseDouble(warningAngle.getText().toString());
            double dangerAngleValue = Double.parseDouble(dangerAngle.getText().toString());

            AircraftModel aircraftModel = new AircraftModel(aircraftIdValue, levelAngleValue, descentAngleValue, warningAngleValue, dangerAngleValue);

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
        warningAngle.setText(String.valueOf(aircraftModel.getWarningAngle()));
        dangerAngle.setText(String.valueOf(aircraftModel.getDangerAngle()));

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
//                glidePathAngleValue     = isNumeric(descentAngle.getText().toString())  ? levelCruiseAngleValue + Double.parseDouble(descentAngle.getText().toString()) : 0.0;
//                warningAngleValue       = isNumeric(warningAngle.getText().toString())  ? levelCruiseAngleValue + Double.parseDouble(warningAngle.getText().toString()) : 0.0;
//                dangerAngleValue        = isNumeric(dangerAngle.getText().toString())   ? levelCruiseAngleValue + Double.parseDouble(dangerAngle.getText().toString()) : 0.0;
                glidePathAngleValue     = isNumeric(descentAngle.getText().toString())  ? Double.parseDouble(descentAngle.getText().toString()) : 0.0;
                warningAngleValue       = isNumeric(warningAngle.getText().toString())  ? Double.parseDouble(warningAngle.getText().toString()) : 0.0;
                dangerAngleValue        = isNumeric(dangerAngle.getText().toString())   ? Double.parseDouble(dangerAngle.getText().toString()) : 0.0;
                //setAirfoilArcsPositions();

            } else {
                Toast.makeText(getApplicationContext(), "No aircraft selected", Toast.LENGTH_LONG).show();
                showAircraftLayout();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No sensor connected.", Toast.LENGTH_LONG).show();
            showSensorLayout();
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

        int deviceSelected = deviceSpinner.getSelectedItemPosition();
        bluetoothGatt = devicesDiscovered.get(deviceSelected).connectGatt(this, false, btleGattCallback );

    }

    public void disconnectDeviceSelected() {
        peripheralTextView.append("Disconnecting from device\n");
        bluetoothGatt.disconnect();
        disconnectDevice.setVisibility(View.INVISIBLE);
        connectToDevice.setVisibility(View.VISIBLE);
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

            if (characteristic.getUuid().equals(ANGLE_CHARACTERISTIC1_UUID)) { // && reading.split(",").length == 2) {

                byte[] b = characteristic.getValue();
                DecimalFormat df = new DecimalFormat("#.#");
                float reading = ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getFloat();
                String strReading = df.format(reading);
                float formattedReading = Float.parseFloat(strReading);
                levelCruiseAngleValue   = isNumeric(levelFlight.getText().toString()) ? Double.parseDouble(levelFlight.getText().toString()) : 0.0;
                dangerAngleValue = Double.parseDouble(dangerAngle.getText().toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

//                        currentAngle.setText(String.format("%.2f", reading));
                        currentAngle.setText(strReading);

                        // these two floats determine how opaque the backgrounds
                        // are on the arrow-type indicator - lightOn is fully opaque,
                        // lightOff is mostly transparent.
                        float lightOn = 255f;
                        float lightOff = 0.2f;

                        // Arrow Indicator Type
                        if (arrowIndicatorLayout.getVisibility() == View.VISIBLE) {

//                            float calibratedReading = reading - (float)levelCruiseAngleValue;
                            float calibratedReading = formattedReading - (float)levelCruiseAngleValue;

//                            arrowSensorAngleTextView.setText(String.format("%.1f",reading));
                            arrowSensorAngleTextView.setText(strReading);
                            arrowCalibratedAngleTextView.setText(String.format("%.1f", calibratedReading));

                            // Danger Angle
//                            if (reading2 <= dangerAngleValue + 1.0) {
                            if (calibratedReading <= dangerAngleValue + 1.0) {
                                dangerAnglePath.setFillAlpha(lightOn);
                            } else {
                                dangerAnglePath.setFillAlpha(lightOff);
                            }

                            // Warning Angle
//                            if (reading2 <= warningAngleValue + 1 && reading2 >= dangerAngleValue ) { //warningAngle && reading2 >= dangerAngle) {
                            if (calibratedReading <= warningAngleValue + 1 && calibratedReading >= dangerAngleValue ) { //warningAngle && reading2 >= dangerAngle) {
                                warningAnglePath.setFillAlpha(lightOn);
                            } else {
                                warningAnglePath.setFillAlpha(lightOff);
                            }

                            // Upper Glide Path Angle
//                            if (reading2 <= glidePathAngleValue + 1  && reading2 >= warningAngleValue) {
                            if (calibratedReading <= glidePathAngleValue + 1  && calibratedReading >= warningAngleValue) {
                                upperGlidePathAnglePath.setFillAlpha(lightOn);
                            } else {
                                upperGlidePathAnglePath.setFillAlpha(lightOff);
                            }

                            // Lower Glide Path Angle
//                            if (reading2 <= levelCruiseAngleValue && reading2 >= glidePathAngleValue - 1) {
                            if (calibratedReading <= -1 && calibratedReading >= glidePathAngleValue - 1) {
                                lowerGlidePathAnglePath.setFillAlpha(lightOn);
                            } else {
                                lowerGlidePathAnglePath.setFillAlpha(lightOff);
                            }

                            // Level Cruise
//                            if (reading2 >= levelCruiseAngleValue - 1 && reading2 <= levelCruiseAngleValue + 1) {
                            if (calibratedReading <= 2 && calibratedReading >= -2) {
                                levelCruiseAnglePath.setFillAlpha(lightOn);
                            } else {
                                levelCruiseAnglePath.setFillAlpha(lightOff);
                            }

                            // Negative Angle
//                            if (reading2 > levelCruiseAngleValue + 1) {
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

                            //float calibratedReading = reading - (float)levelCruiseAngleValue;
                            float calibratedReading = formattedReading - (float)levelCruiseAngleValue;

                            if (calibratedReading < -60f) {
                                calibratedReading = -60f;
                            } else if (calibratedReading > 60f) {
                                calibratedReading = 60f;
                            }



                            airfoilSensorAngleTextView.setText(String.format("%.1f",reading));
                            airfoilCalibratedAngleTextView.setText(String.format("%.1f", calibratedReading));

                            setAirfoilArcsPositions();

                            // Change the color of the airfoil, depending on its current value
                            if (calibratedReading - 1.0 <= dangerAngleValue) {
                                // Turn it RED
                                airfoilPath.setFillColor(0xFFFF5555);
                                airfoilPath.setStrokeColor(0xFFCC2222);
                            } else if (calibratedReading - 1.0 <= warningAngleValue) {
                                // Turn it YELLOW
                                airfoilPath.setFillColor(0xFFFFFF55);
                                airfoilPath.setStrokeColor(0xFFCCAA33);
                            } else {
                                //Turn it BLUE
                                airfoilPath.setFillColor(0xFF9BBAF8);
                                airfoilPath.setStrokeColor(0xFF728FC8);
                            }

                            //airfoilImageView.invalidate();



                            RotateAnimation rotateAirfoil = new RotateAnimation(
                                -(calibratedReading * ANGLE_MULTIPLIER), -(calibratedReading * ANGLE_MULTIPLIER), Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
                            );
                            rotateAirfoil.setDuration(100);
                            rotateAirfoil.setRepeatCount(Animation.INFINITE);
                            airfoilImageView.startAnimation(rotateAirfoil);

                            //System.out.println(String.format("reading: %s | levelCruiseAngleValue: %s | warningAngleValue: %s | dangerAngleValue: %s | calibratedReading: %s", reading, levelCruiseAngleValue, warningAngleValue, dangerAngleValue, calibratedReading));

                            controlAudibleWarning(calibratedReading);

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
                    if (calibratedReading <= (dangerAngleValue) + 1.0) {
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
                    BluetoothGattCharacteristic angleCharacteristic = angleService.getCharacteristic(ANGLE_CHARACTERISTIC1_UUID);

                    BluetoothGattDescriptor angleDescriptor = angleCharacteristic.getDescriptor(DESCRIPTOR_UUID);
                    angleDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                    gatt.writeDescriptor(angleDescriptor);
                    //gatt.writeDescriptor(messageDescriptor);
                    gatt.setCharacteristicNotification(angleCharacteristic, true);
                    //gatt.setCharacteristicNotification(messageCharacteristic, true);

                    gatt.writeCharacteristic(angleCharacteristic);
                    //gatt.writeCharacteristic(messageCharacteristic);

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

            peripheralTextView.append(characteristic.getValue().toString());

        }

        @Override
        // Result of a descriptor read operation - a characteristic will have one or more
        // "descriptors" associated with it
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                System.out.println("onDescriptionRead successful!!");
            }
        }

//        @Override
//        // besides just reading a descriptor, you can actually send date back to the device as well
//        // I don't think this will ever actually fire ...
//        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
//
//            BluetoothGattCharacteristic angleCharacteristic =
//                    gatt.getService(ANGLE_SERVICE_UUID)
//                            .getCharacteristic(ANGLE_CHARACTERISTIC1_UUID);
//
//
//            angleCharacteristic.setValue(new byte[] {1,1});
//
//            gatt.writeCharacteristic(angleCharacteristic);
//
//        }
    };


}
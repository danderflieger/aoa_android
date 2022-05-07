package com.danderflieger.angleofattackindicator;

import android.widget.EditText;

public class AircraftModel {

    private String aircraftId;
    private double levelCruiseAngle;
    private double glidePathAngle;
    private double warningAngle;
    private double dangerAngle;

    public AircraftModel(String aircraftId, double levelCruiseAngle, double descentAngle, double warningAngle, double dangerAngle) {
        this.aircraftId         = aircraftId;
        this.levelCruiseAngle   = levelCruiseAngle;
        this.glidePathAngle     = descentAngle;
        this.warningAngle       = warningAngle;
        this.dangerAngle        = dangerAngle;
    }

    public String getAircraftId()       { return aircraftId; }
    public double getLevelCruiseAngle() { return levelCruiseAngle; }
    public double getDescentAngle()     { return glidePathAngle; }
    public double getWarningAngle()     { return warningAngle; }
    public double getDangerAngle()      { return dangerAngle; }


    public void setAircraftId       (String aircraftId )        { this.aircraftId = aircraftId; }
    public void setLevelCruiseAngle (double levelCruiseAngle)   { this.levelCruiseAngle = levelCruiseAngle; }
    public void setDescentAngle     (double descentAngle)       { this.glidePathAngle = descentAngle; }
    public void setWarningAngle     (double warningAngle)       { this.warningAngle = warningAngle; }
    public void setDangerAngle      (double dangerAngle)        { this.dangerAngle = dangerAngle; }


}

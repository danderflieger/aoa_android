package com.danderflieger.angleofattackindicator;

import android.widget.EditText;

public class AircraftModel {

    private String aircraftId;
    private double levelCruiseAngle;
    private double glidePathAngle;
    private double warningAngle;
    private double dangerAngle;
    private double turnRate;

    public AircraftModel(String aircraftId, double levelCruiseAngle, double descentAngle, double warningAngle, double dangerAngle, double turnRate) {
        this.aircraftId         = aircraftId;
        this.levelCruiseAngle   = levelCruiseAngle;
        this.glidePathAngle     = descentAngle;
        this.warningAngle       = warningAngle;
        this.dangerAngle        = dangerAngle;
        this.turnRate           = turnRate;
    }

    public String getAircraftId()       { return aircraftId; }
    public double getLevelCruiseAngle() { return levelCruiseAngle; }
    public double getDescentAngle()     { return glidePathAngle; }
    public double getWarningAngle()     { return warningAngle; }
    public double getDangerAngle()      { return dangerAngle; }
    public double getTurnRate()         { return turnRate; }



    public void setAircraftId       (String aircraftId )        { this.aircraftId = aircraftId; }
    public void setLevelCruiseAngle (double levelCruiseAngle)   { this.levelCruiseAngle = levelCruiseAngle; }
    public void setDescentAngle     (double descentAngle)       { this.glidePathAngle = descentAngle; }
    public void setWarningAngle     (double warningAngle)       { this.warningAngle = warningAngle; }
    public void setDangerAngle      (double dangerAngle)        { this.dangerAngle = dangerAngle; }
    public void setTurnRate         (double turnRate)           { this.turnRate = turnRate; }

}

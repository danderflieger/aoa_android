package com.danderflieger.angleofattackindicator;

import android.widget.EditText;

public class AircraftModel {

    private String aircraftId;
    private double levelCruiseAngle;
    private double glidePathAngle;
//    private double warningAngle;
    private double dangerAngle;
    private double dangerAngleFlaps;
    private double turnRate;
    private double ballReadingMultiplier;

//    public AircraftModel(String aircraftId, double levelCruiseAngle, double descentAngle, double warningAngle, double dangerAngle, double turnRate, double ballReadingMultiplier) {
    public AircraftModel(String aircraftId, double levelCruiseAngle, double descentAngle, double dangerAngle, double dangerAngleFlaps, double turnRate, double ballReadingMultiplier) {
        this.aircraftId         = aircraftId;
        this.levelCruiseAngle   = levelCruiseAngle;
        this.glidePathAngle     = descentAngle;
//        this.warningAngle       = warningAngle;

        this.dangerAngle        = dangerAngle;
        this.dangerAngleFlaps   = dangerAngleFlaps;

        this.turnRate           = turnRate;
        this.ballReadingMultiplier = ballReadingMultiplier;
    }

    public String getAircraftId()           { return aircraftId; }
    public double getLevelCruiseAngle()     { return levelCruiseAngle; }
    public double getDescentAngle()         { return glidePathAngle; }
    public double getDangerAngle()          { return dangerAngle; }
    public double getDangerAngleFlaps()         { return dangerAngleFlaps; }
    public double getTurnRate()             { return turnRate; }
    public double getBallReadingMultiplier(){ return ballReadingMultiplier; }



    public void setAircraftId       (String aircraftId )        { this.aircraftId = aircraftId; }
    public void setLevelCruiseAngle (double levelCruiseAngle)   { this.levelCruiseAngle = levelCruiseAngle; }
    public void setDescentAngle     (double descentAngle)       { this.glidePathAngle = descentAngle; }
//    public void setWarningAngle     (double warningAngle)       { this.dangerAngleFlaps = dangerAngleFlaps; }
    public void setDangerAngle      (double dangerAngle)        { this.dangerAngle = dangerAngle; }
    public void setDangerAngleFlaps (double dangerAngleFlaps)   { this.dangerAngleFlaps = dangerAngleFlaps; }
    public void setTurnRate         (double turnRate)           { this.turnRate = turnRate; }
    public void setBallReadingMultiplier (double ballReadingMultiplier) { this.ballReadingMultiplier = ballReadingMultiplier; }

}

package com.danderflieger.angleofattackindicator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SQLiteOpenHelper extends android.database.sqlite.SQLiteOpenHelper {

    public static final String tblAircraft = "tblAircraft";

    public SQLiteOpenHelper(@Nullable Context context) {
        super(context, "AoADatabase.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement =
            "CREATE TABLE " + tblAircraft + " ( " +
                " aircraftId TEXT PRIMARY KEY, " +
                " levelCruiseAngle REAL,   " +
                " descentAngle REAL, " +
                " warningAngle REAL," +
                " dangerAngle REAL, " +
                " turnRate REAL " +
            ") ";

        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addAirplane(AircraftModel aircraft) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("aircraftId", aircraft.getAircraftId());
        cv.put("levelCruiseAngle", aircraft.getLevelCruiseAngle());
        cv.put("descentAngle", aircraft.getDescentAngle());
        cv.put("warningAngle", aircraft.getWarningAngle());
        cv.put("dangerAngle", aircraft.getDangerAngle());
        cv.put("turnRate", aircraft.getTurnRate());

        long insert = db.insert(tblAircraft, null, cv);
        if (insert == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteAirplane(String aircraftId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String deleteQueryString = "DELETE FROM " + tblAircraft + " WHERE aircraftId = '" + aircraftId +"';";

        long response = db.delete(tblAircraft,"aircraftId=?", new String[]{ aircraftId });
        return response == -1 ? false: true;
    }

    public boolean updateAirplane(AircraftModel aircraft) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("aircraftId", aircraft.getAircraftId());
        cv.put("levelCruiseAngle", aircraft.getLevelCruiseAngle());
        cv.put("descentAngle", aircraft.getDescentAngle());
        cv.put("warningAngle", aircraft.getWarningAngle());
        cv.put("dangerAngle", aircraft.getDangerAngle());
        cv.put("turnRate", aircraft.getTurnRate());

        long response = db.update(tblAircraft, cv, "aircraftID=?", new String[]{aircraft.getAircraftId()});

        return response == -1 ? false : true;

    }

    public List<String> getAirplanesIds() {

        List<String> aircraftIds = new ArrayList<>();

        String queryString = "SELECT aircraftId FROM " + tblAircraft + ";";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null );

        if (cursor.moveToFirst()) {
            do {
                String aircraftId = cursor.getString(0);
                aircraftIds.add(aircraftId);

            }while(cursor.moveToNext());
        } else {

        }
        cursor.close();
        db.close();
        return aircraftIds;

    }

    public AircraftModel getAirplane(String aircraftId) {
        AircraftModel aircraftModel = null;

        String queryString = "SELECT * FROM tblAircraft WHERE aircraftId = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, new String[] {aircraftId});

        cursor.moveToFirst();
        aircraftModel = new AircraftModel(
                cursor.getString(0), // aircraftId
                cursor.getDouble(1), // levelCruiseAngle
                cursor.getDouble(2), // glidePathAngle
                cursor.getDouble(3), // warningAngle
                cursor.getDouble(4), // dangerAngle
                cursor.getDouble(5)  // turnRate
        );

        return aircraftModel;
    }

    Boolean doesAircraftExist(String aircraftId) {

        Boolean aircraftExists = false;

        String queryString = "SELECT * FROM " + tblAircraft + " WHERE aircraftId='" + aircraftId +"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        int count = cursor.getCount();

        return count > 0;
    }
}

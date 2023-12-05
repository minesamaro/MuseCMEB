package org.mines.cmeb.musecmeb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "euterpe.db";
	private static final int DATABASE_VERSION = 1;

	// Table name and column names
	private static final String TABLE_SESSIONS = "Sessions";
	private static final String COLUMN_ID = "id";
	private static final String COLUMN_STRESS_INDEXES = "stress_indexes";
	private static final String COLUMN_RELAXATION_TIME = "relaxation_time";
	private static final String COLUMN_START_DATE = "start_date";

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Create the meditation sessions table
		String createTableQuery = "CREATE TABLE " + TABLE_SESSIONS + " (" +
				COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				COLUMN_STRESS_INDEXES + " TEXT, " +
				COLUMN_RELAXATION_TIME + " REAL, " +
				COLUMN_START_DATE + " TEXT)";
		db.execSQL(createTableQuery);

		ContentValues values = new ContentValues();
		values.put(COLUMN_STRESS_INDEXES, "1,2,3,4,5");
		values.put(COLUMN_RELAXATION_TIME, 10.5f);
		values.put(COLUMN_START_DATE, "2022-01-01 00:00:00");
		long rowId1 = db.insert(TABLE_SESSIONS, null, values);

		ContentValues values2 = new ContentValues();
		values2.put(COLUMN_STRESS_INDEXES, "6,7,8,9,10");
		values2.put(COLUMN_RELAXATION_TIME, 20.5f);
		values2.put(COLUMN_START_DATE, "2022-02-01 00:00:00");
		long rowId2 = db.insert(TABLE_SESSIONS, null, values2);

		Log.d("DatabaseHelper", "Inserted row IDs: " + rowId1 + ", " + rowId2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Upgrade the database if needed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
		onCreate(db);
	}

	// Add a new meditation session to the database
	public long addSession(RelaxationSession session) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_STRESS_INDEXES, convertIntArrayToString(session.getStressIndexes()));
		values.put(COLUMN_RELAXATION_TIME, session.getRelaxationTime());
		values.put(COLUMN_START_DATE, formatDateToString(session.getStartDate()));

		long id = db.insert(TABLE_SESSIONS, null, values);
		db.close();
		return id;
	}

	// Get all meditation sessions from the database
	public List<RelaxationSession> getAllRelaxationSessions() {
		List<RelaxationSession> sessions = new ArrayList<RelaxationSession>();
		String selectQuery = "SELECT * FROM " + TABLE_SESSIONS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		Log.d("DatabaseHelper", "Cursor count: " + cursor.getCount());

		if (cursor.moveToFirst()) {
			do {
				int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
				int[] stressIndexes = convertStringToIntArray(cursor.getString(cursor.getColumnIndex(COLUMN_STRESS_INDEXES)));
				float relaxationTime = cursor.getFloat(cursor.getColumnIndex(COLUMN_RELAXATION_TIME));
				Date startDate = parseStringToDate(cursor.getString(cursor.getColumnIndex(COLUMN_START_DATE)));

				RelaxationSession session = new RelaxationSession(id, stressIndexes, relaxationTime, startDate);
				sessions.add(session);
				Log.d("DatabaseHelper", "Session ID: " + id + ", Stress Indexes: " + Arrays.toString(stressIndexes) + ", Relaxation Time: " + relaxationTime + ", Start Date: " + startDate.toString());
			} while (cursor.moveToNext());
		}

		cursor.close();
		db.close();
		Log.d("DatabaseHelper", "Number of sessions retrievedd: " + sessions.size());
		return sessions;
	}

	// Helper methods for data conversion
	private String convertIntArrayToString(int[] array) {
		StringBuilder builder = new StringBuilder();
		for (int value : array) {
			builder.append(value).append(",");
		}
		// Remove the trailing comma
		return builder.deleteCharAt(builder.length() - 1).toString();
	}

	private int[] convertStringToIntArray(String str) {
		String[] strArray = str.split(",");
		int[] intArray = new int[strArray.length];
		for (int i = 0; i < strArray.length; i++) {
			intArray[i] = Integer.parseInt(strArray[i]);
		}
		return intArray;
	}

	private String formatDateToString(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		return sdf.format(date);
	}

	private Date parseStringToDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}

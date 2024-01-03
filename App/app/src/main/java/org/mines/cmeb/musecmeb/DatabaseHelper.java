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
import java.util.Calendar;
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
				COLUMN_RELAXATION_TIME + " INTEGER, " +
				COLUMN_START_DATE + " TEXT)";
		db.execSQL(createTableQuery);

		ContentValues values = new ContentValues();
		values.put(COLUMN_STRESS_INDEXES, "100,62,62,62,62,62,53,53,53,53,53,57,57,57,57,57,43,43,43,43,43,36,36,36,36,36,44,44,44,44,44,44,53,53,53,53,53,54,54,54,54,54,54,54");
		values.put(COLUMN_RELAXATION_TIME, 5.5f);
		values.put(COLUMN_START_DATE, "2023-12-04");
		long rowId1 = db.insert(TABLE_SESSIONS, null, values);

		ContentValues values2 = new ContentValues();
		values2.put(COLUMN_STRESS_INDEXES, "100,62,62,62,62,62,53,53,53,53,53,57,57,57,57,57,43,43,43,43,43,36,36,36,36,36,44,44,44,44,44,44,53,53,53,53,53,54,54,54,54,54,54,54");
		values2.put(COLUMN_RELAXATION_TIME, 9.5f);
		values2.put(COLUMN_START_DATE, "2023-12-05");
		long rowId2 = db.insert(TABLE_SESSIONS, null, values2);

		ContentValues values3 = new ContentValues();
		values3.put(COLUMN_STRESS_INDEXES, "100,62,62,62,62,62,53,53,53,53,53,57,57,57,57,57,43,43,43,43,43,36,36,36,36,36,44,44,44,44,44,44,53,53,53,53,53,54,54,54,54,54,54,54");
		values3.put(COLUMN_RELAXATION_TIME, 7.2f);
		values3.put(COLUMN_START_DATE, "2023-12-06" );
		long rowId3 = db.insert(TABLE_SESSIONS, null, values3);

		ContentValues values4 = new ContentValues();
		values4.put(COLUMN_STRESS_INDEXES, "100,62,62,62,62,62,53,53,53,53,53,57,57,57,57,57,43,43,43,43,43,36,36,36,36,36,44,44,44,44,44,44,53,53,53,53,53,54,54,54,54,54,54,54");
		values4.put(COLUMN_RELAXATION_TIME, 2.23f);
		values4.put(COLUMN_START_DATE, "2023-12-07" );
		long rowId4 = db.insert(TABLE_SESSIONS, null, values4);

		ContentValues values5 = new ContentValues();
		values5.put(COLUMN_STRESS_INDEXES, "100,62,62,62,62,62,53,53,53,53,53,57,57,57,57,57,43,43,43,43,43,36,36,36,36,36,44,44,44,44,44,44,53,53,53,53,53,54,54,54,54,54,54,54");
		values5.put(COLUMN_RELAXATION_TIME, 0.74f);
		values5.put(COLUMN_START_DATE, "2023-12-10" );
		long rowId5 = db.insert(TABLE_SESSIONS, null, values5);

		ContentValues values6 = new ContentValues();
		values6.put(COLUMN_STRESS_INDEXES, "100,62,62,62,62,62,53,53,53,53,53,57,57,57,57,57,43,43,43,43,43,36,36,36,36,36,44,44,44,44,44,44,53,53,53,53,53,54,54,54,54,54,54,54");
		values6.put(COLUMN_RELAXATION_TIME, 2.74f);
		values6.put(COLUMN_START_DATE, "2023-12-11" );
		long rowId6 = db.insert(TABLE_SESSIONS, null, values6);

		ContentValues values7 = new ContentValues();
		values7.put(COLUMN_STRESS_INDEXES, "100,82,72,75,63,52,54,50,51,46,49,48,45,38,36,43,48,45,43,40,38,36,33,36,30,26,29,24,24,21,23,18,16,20,19,16,13,14,11,9,10,12,9,7");
		values7.put(COLUMN_RELAXATION_TIME, 3.74f);
		values7.put(COLUMN_START_DATE, "2023-12-11" );
		long rowId7 = db.insert(TABLE_SESSIONS, null, values7);

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
		// Check if the StringBuilder is not empty before removing the trailing comma
		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
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
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		return sdf.format(date);
	}

	private Date parseStringToDate(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		try {
			return sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public float[] getAllRelaxationTimes() {
		List<Float> relaxationTimesList = new ArrayList<>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_SESSIONS, new String[]{COLUMN_RELAXATION_TIME},
				null, null, null, null, COLUMN_ID + " ASC" );

		if (cursor != null && cursor.moveToFirst()) {
			do {
				float relaxationTime = cursor.getFloat(cursor.getColumnIndex(COLUMN_RELAXATION_TIME));
				relaxationTimesList.add(relaxationTime);
			} while (cursor.moveToNext());

			cursor.close();
		}

		db.close();

		// Convert List<Float> to float[]
		float[] relaxationTimesArray = new float[relaxationTimesList.size()];
		for (int i = 0; i < relaxationTimesList.size(); i++) {
			relaxationTimesArray[i] = relaxationTimesList.get(i);
		}

		return relaxationTimesArray;
	}

	// Method to get session counts for the last 7 days
	public int[] getSessionCountsLast7Days() {
		int[] sessionCounts = new int[7];
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

		SQLiteDatabase db = this.getReadableDatabase();

		for (int i = 0; i < 7; i++) {
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, -i);
			Date date = calendar.getTime();
			String formattedDate = dateFormat.format(date);

			String[] selectionArgs = {formattedDate + "%"};
			Cursor cursor = db.query(TABLE_SESSIONS,
					new String[]{"COUNT(*)"},
					COLUMN_START_DATE + " LIKE ?",
					selectionArgs,
					null,
					null,
					null);

			if (cursor != null && cursor.moveToFirst()) {
				sessionCounts[i] = cursor.getInt(0);
				cursor.close();
			} else {
				sessionCounts[i] = 0;
			}
		}

		db.close();

		return sessionCounts;
	}
}

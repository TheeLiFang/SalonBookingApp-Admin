package my.salonapp.salonbookingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LastSubmissionDbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "last_submission.db";

    // Table name
    private static final String TABLE_NAME = "last_submissions";

    // Column names
    private static final String COLUMN_ACTIVITY = "activity";
    private static final String COLUMN_LAST_SUBMISSION = "last_submission";

    // Create table SQL query
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ACTIVITY + " TEXT PRIMARY KEY,"
                    + COLUMN_LAST_SUBMISSION + " TEXT"
                    + ")";

    private Context context;

    public LastSubmissionDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create last submission table
        db.execSQL(CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    // Insert a new last submission time
    public long addSubmission(String activity, String lastSubmission) {
        // Get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ACTIVITY, activity);
        values.put(COLUMN_LAST_SUBMISSION, lastSubmission);

        // Insert row
        long id = db.insert(TABLE_NAME, null, values);

        // Close db connection
        db.close();

        // Return newly inserted row id
        return id;
    }

    // Get a last submission time by activity name
    public String getSubmissionByActivity(String activity) {
        // Get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();
        String lastSubmission = "";

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{
                        COLUMN_ACTIVITY, COLUMN_LAST_SUBMISSION
                },
                COLUMN_ACTIVITY + "=?",
                new String[]{activity}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        if (cursor.getCount() > 0) {
            lastSubmission = cursor.getString(cursor.getColumnIndex(COLUMN_LAST_SUBMISSION));
        }

        // Close db connection
        cursor.close();

        // Return last submission time
        return lastSubmission;
    }

    // Update last submission time by activity name
    public int updateSubmissionByActivity(String activity, String lastSubmission) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_LAST_SUBMISSION, lastSubmission);

        // Updating row
        return db.update(TABLE_NAME, values, COLUMN_ACTIVITY + "=?",
                new String[]{activity});
    }

    // Delete all last submission times
    public void deleteAllSubmissions() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Deleting row
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}

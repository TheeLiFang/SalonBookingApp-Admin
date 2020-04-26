package my.salonapp.salonbookingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class StaffDbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "staff.db";

    // Table name
    private static final String TABLE_NAME = "staffs";

    // Column names
    private static final String COLUMN_STAFF_ID = "staff_id";
    private static final String COLUMN_STAFF_NAME = "staff_name";
    private static final String COLUMN_STAFF_EMAIL = "staff_email";
    private static final String COLUMN_STAFF_PHONE = "staff_phone";

    // Create table SQL query
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_STAFF_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_STAFF_NAME + " TEXT,"
                    + COLUMN_STAFF_EMAIL + " TEXT,"
                    + COLUMN_STAFF_PHONE + " TEXT"
                    + ")";

    private Context context;

    public StaffDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create staff table
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

    // Insert a new staff
    public long addStaff(Staff staff) {
        // Get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STAFF_ID, staff.getStaffId());
        values.put(COLUMN_STAFF_NAME, staff.getStaffName());
        values.put(COLUMN_STAFF_EMAIL, staff.getStaffEmail());
        values.put(COLUMN_STAFF_PHONE, staff.getStaffPhone());

        // Insert row
        long id = db.insert(TABLE_NAME, null, values);

        // Close db connection
        db.close();

        // Return newly inserted row id
        return id;
    }

    // Insert a new list of staffs
    public void addAllStaffs(ArrayList<Staff> staffs) {
        SQLiteDatabase db = getWritableDatabase();

        for (Staff staff : staffs) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_STAFF_ID, staff.getStaffId());
            values.put(COLUMN_STAFF_NAME, staff.getStaffName());
            values.put(COLUMN_STAFF_EMAIL, staff.getStaffEmail());
            values.put(COLUMN_STAFF_PHONE, staff.getStaffPhone());

            db.insert(TABLE_NAME, null, values);
        }

        // Close db connection
        db.close();
    }

    // Get a staff by staff ID
    public Staff getStaffById(int id) {
        // Get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{
                        COLUMN_STAFF_ID, COLUMN_STAFF_NAME, COLUMN_STAFF_EMAIL,
                        COLUMN_STAFF_PHONE
                },
                COLUMN_STAFF_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        // Prepare staff object
        Staff staff = new Staff(
                cursor.getInt(cursor.getColumnIndex(COLUMN_STAFF_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_STAFF_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_STAFF_EMAIL)),
                cursor.getString(cursor.getColumnIndex(COLUMN_STAFF_PHONE)));

        // Close db connection
        cursor.close();

        // Return staff
        return staff;
    }

    // Get all list of staffs
    public ArrayList<Staff> getAllStaffs() {
        ArrayList<Staff> staffs = new ArrayList<>();

        // Select all query
        String selectQuery = String.format("SELECT * FROM %1$s ORDER BY %2$s ASC",
                TABLE_NAME, COLUMN_STAFF_ID);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Staff staff = new Staff();
                staff.setStaffId(cursor.getInt(cursor.getColumnIndex(COLUMN_STAFF_ID)));
                staff.setStaffName(cursor.getString(cursor.getColumnIndex(COLUMN_STAFF_NAME)));
                staff.setStaffEmail(cursor.getString(cursor.getColumnIndex(COLUMN_STAFF_EMAIL)));
                staff.setStaffPhone(cursor.getString(cursor.getColumnIndex(COLUMN_STAFF_PHONE)));

                staffs.add(staff);
            } while (cursor.moveToNext());
        }

        // Close db connection
        db.close();

        // Return list of staffs
        return staffs;
    }

    /*
    // Get total number of rows of staff
    public int getStaffsCount() {
        String countQuery = String.format("SELECT * FROM %1$s", TABLE_NAME);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // Return count
        return count;
    }
    */

    /*
    // Update staff by ID
    public int updateStaffById(Staff staff) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_STAFF_NAME, staff.getStaffName());
        values.put(COLUMN_STAFF_EMAIL, staff.getStaffEmail());
        values.put(COLUMN_STAFF_PHONE, staff.getStaffPhone());
        values.put(COLUMN_COMPANY_ID, staff.getCompanyId());
        values.put(COLUMN_COMPANY_NAME, staff.getCompanyName());

        // Updating row
        return db.update(TABLE_NAME, values, COLUMN_STAFF_ID + "=?",
                new String[]{String.valueOf(staff.getStaffId())});
    }
    */

    // Delete all staffs
    public void deleteAllStaffs() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Deleting row
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    /*
    // Delete staff by ID
    public void deleteStaffById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Deleting row
        db.delete(TABLE_NAME, COLUMN_STAFF_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
    }
    */
}

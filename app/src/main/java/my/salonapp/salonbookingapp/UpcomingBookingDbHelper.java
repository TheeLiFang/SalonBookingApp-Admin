package my.salonapp.salonbookingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class UpcomingBookingDbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "upcomingBookings.db";

    // Table name
    private static final String TABLE_NAME = "upcomingBookings";

    // Column names
    public static final String COLUMN_BOOKING_ID = "booking_id";
    public static final String COLUMN_BOOKING_TIME = "booking_name";
    public static final String COLUMN_STAFF_NAME = "staff_name";
    public static final String COLUMN_CLIENT_NAME = "client_name";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_BOOKING_ID + " INTEGER,"
                    + COLUMN_BOOKING_TIME + " TEXT,"
                    + COLUMN_STAFF_NAME + " TEXT,"
                    + COLUMN_CLIENT_NAME + " TEXT"
                    + ")";

    private Context context;

    public UpcomingBookingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create upcoming booking table
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

    // Insert a new list of upcoming bookings
    public void addAllUpcomingBookings(List<UpcomingBooking> bookings) {
        SQLiteDatabase db = getWritableDatabase();

        for (UpcomingBooking booking : bookings) {
            ContentValues values = new ContentValues();

            values.put(COLUMN_BOOKING_ID, booking.getBookingId());
            values.put(COLUMN_BOOKING_TIME, booking.getBookingTime());
            values.put(COLUMN_STAFF_NAME, booking.getStaffName());
            values.put(COLUMN_CLIENT_NAME, booking.getClientName());

            db.insert(TABLE_NAME, null, values);
        }

        // Close db connection
        db.close();
    }

    // Get a booking by booking ID
    public UpcomingBooking getUpcomingBookingById(int id) {
        // Get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{
                        COLUMN_BOOKING_ID, COLUMN_BOOKING_TIME, COLUMN_STAFF_NAME,
                        COLUMN_CLIENT_NAME
                },
                COLUMN_BOOKING_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        // Prepare booking object
        UpcomingBooking booking = new UpcomingBooking(
                cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_TIME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_STAFF_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME)));

        // Close the db connection
        cursor.close();

        // Return booking
        return booking;
    }

    // Get all list of upcoming bookings
    public ArrayList<UpcomingBooking> getAllUpcomingBookings() {
        ArrayList<UpcomingBooking> bookings = new ArrayList<>();

        // Select All Query
        String selectQuery = String.format("SELECT * FROM %1$s ORDER BY %2$s ASC",
                TABLE_NAME, COLUMN_BOOKING_ID);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                UpcomingBooking booking = new UpcomingBooking();
                booking.setBookingId(cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_ID)));
                booking.setBookingTime(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKING_TIME)));
                booking.setStaffName(cursor.getString(cursor.getColumnIndex(COLUMN_STAFF_NAME)));
                booking.setClientName(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME)));

                bookings.add(booking);
            } while (cursor.moveToNext());
        }

        // Close connection
        cursor.close();
        db.close();

        // Return upcoming bookings list
        return bookings;
    }

    // Delete all bookings
    public void deleteAllUpcomingBookings() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Deleting row
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}

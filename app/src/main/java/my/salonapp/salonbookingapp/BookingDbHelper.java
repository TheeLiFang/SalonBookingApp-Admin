package my.salonapp.salonbookingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class BookingDbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "booking.db";

    // Table name
    private static final String TABLE_NAME_BOOKING = "bookings";
    private static final String TABLE_NAME_BOOKING_SERVICE = "booking_services";

    // Column names
    private static final String COLUMN_BOOKING_ID = "booking_id";
    private static final String COLUMN_COMPANY_ID = "company_id";
    private static final String COLUMN_COMPANY_NAME = "company_name";
    private static final String COLUMN_CLIENT_ID = "client_id";
    private static final String COLUMN_CLIENT_NAME = "client_name";
    private static final String COLUMN_STAFF_ID = "staff_id";
    private static final String COLUMN_STAFF_NAME = "staff_name";
    private static final String COLUMN_BOOKING_DATE = "booking_date";
    private static final String COLUMN_BOOKING_DURATION = "booking_duration";
    // Column names
    private static final String COLUMN_BOOKING_SERVICE_ID = "booking_service_id";
    private static final String COLUMN_SERVICE_NAME = "service_name";
    private static final String COLUMN_SERVICE_PRICE = "service_price";
    private static final String COLUMN_SERVICE_DURATION = "service_duration";
    private static final String COLUMN_DISPLAY_ORDER = "display_order";

    // Create table SQL query
    private static final String CREATE_TABLE_BOOKING =
            "CREATE TABLE " + TABLE_NAME_BOOKING + "("
                    + COLUMN_BOOKING_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_COMPANY_ID + " INTEGER,"
                    + COLUMN_COMPANY_NAME + " TEXT,"
                    + COLUMN_CLIENT_ID + " INTEGER,"
                    + COLUMN_CLIENT_NAME + " TEXT,"
                    + COLUMN_STAFF_ID + " INTEGER,"
                    + COLUMN_STAFF_NAME + " TEXT,"
                    + COLUMN_BOOKING_DATE + " INTEGER,"
                    + COLUMN_BOOKING_DURATION + " INTEGER"
                    + ")";
    private static final String CREATE_TABLE_BOOKING_SERVICE =
            "CREATE TABLE " + TABLE_NAME_BOOKING_SERVICE + "("
                    + COLUMN_BOOKING_SERVICE_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_BOOKING_ID + " INTEGER,"
                    + COLUMN_SERVICE_NAME + " TEXT,"
                    + COLUMN_SERVICE_PRICE + " REAL,"
                    + COLUMN_SERVICE_DURATION + " INTEGER,"
                    + COLUMN_DISPLAY_ORDER + " INTEGER"
                    + ")";

    private Context context;

    public BookingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create booking table
        db.execSQL(CREATE_TABLE_BOOKING);
        db.execSQL(CREATE_TABLE_BOOKING_SERVICE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BOOKING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BOOKING_SERVICE);

        // Create tables again
        onCreate(db);
    }

    // Insert a new list of bookings
    public void addAllBookings(ArrayList<Booking> bookings) {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteDatabase db1 = getWritableDatabase();

        for (Booking booking : bookings) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_BOOKING_ID, booking.getBookingId());
            values.put(COLUMN_COMPANY_ID, booking.getCompanyId());
            values.put(COLUMN_COMPANY_NAME, booking.getCompanyName());
            values.put(COLUMN_CLIENT_ID, booking.getClientId());
            values.put(COLUMN_CLIENT_NAME, booking.getClientName());
            values.put(COLUMN_STAFF_ID, booking.getStaffId());
            values.put(COLUMN_STAFF_NAME, booking.getStaffName());
            values.put(COLUMN_BOOKING_DATE, booking.getBookingDate());
            values.put(COLUMN_BOOKING_DURATION, booking.getBookingDuration());

            db.insert(TABLE_NAME_BOOKING, null, values);

            for (BookingService bookingService : booking.getBookingServices()) {
                ContentValues values1 = new ContentValues();
                values1.put(COLUMN_BOOKING_ID, bookingService.getBookingId());
                values1.put(COLUMN_BOOKING_SERVICE_ID, bookingService.getBookingServiceId());
                values1.put(COLUMN_SERVICE_NAME, bookingService.getServiceName());
                values1.put(COLUMN_SERVICE_PRICE, bookingService.getServicePrice());
                values1.put(COLUMN_SERVICE_DURATION, bookingService.getServiceDuration());
                values1.put(COLUMN_DISPLAY_ORDER, bookingService.getDisplayOrder());

                db1.insert(TABLE_NAME_BOOKING_SERVICE, null, values1);
            }
        }

        // Close db connection
        db.close();
        db1.close();
    }

    // Get the list of bookings by date
    public ArrayList<Booking> getBookingsByDate(Date selectedDate) {
        ArrayList<Booking> bookings = new ArrayList<>();

        Date today = new Date();
        long dayDifference = TimeUnit.DAYS.convert(selectedDate.getTime() - today.getTime(), TimeUnit.MILLISECONDS);

        if (dayDifference >= 0) {
            if (!(DateUtils.isToday(selectedDate.getTime()))) {
                // Add 1 day if the selected date is later than today for sql query filtering
                dayDifference++;
            }
        }

        // Select all query
        String selectQuery = String.format(Locale.getDefault(),
                "SELECT * FROM %1$s WHERE date(datetime(%2$s / 1000, 'unixepoch', 'localtime')) = date('now', '%3$s day', 'localtime') ORDER BY %4$s ASC",
                TABLE_NAME_BOOKING, COLUMN_BOOKING_DATE, dayDifference, COLUMN_BOOKING_DATE);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Booking booking = new Booking();
                booking.setBookingId(cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_ID)));
                booking.setCompanyId(cursor.getInt(cursor.getColumnIndex(COLUMN_COMPANY_ID)));
                booking.setCompanyName(cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY_NAME)));
                booking.setClientId(cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID)));
                booking.setClientName(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME)));
                booking.setStaffId(cursor.getInt(cursor.getColumnIndex(COLUMN_STAFF_ID)));
                booking.setStaffName(cursor.getString(cursor.getColumnIndex(COLUMN_STAFF_NAME)));
                booking.setBookingDate(cursor.getLong(cursor.getColumnIndex(COLUMN_BOOKING_DATE)));
                booking.setBookingDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_DURATION)));

                bookings.add(booking);
            } while (cursor.moveToNext());
        }

        // Close cursor
        cursor.close();

        // Close db connection
        db.close();

        // Return list of bookings
        return bookings;
    }

    // Get a booking by booking ID
    public Booking getBookingByBookingId(int id) {
        // Get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME_BOOKING,
                new String[]{
                        COLUMN_BOOKING_ID, COLUMN_COMPANY_ID, COLUMN_COMPANY_NAME,
                        COLUMN_CLIENT_ID, COLUMN_CLIENT_NAME, COLUMN_STAFF_ID,
                        COLUMN_STAFF_NAME, COLUMN_BOOKING_DATE, COLUMN_BOOKING_DURATION
                },
                COLUMN_BOOKING_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        // Prepare booking object
        Booking booking = new Booking(
                cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_ID)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_COMPANY_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY_NAME)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_STAFF_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_STAFF_NAME)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_BOOKING_DATE)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_DURATION)),
                null);

        // Close cursor
        cursor.close();

        // Close db connection
        db.close();

        // Return booking
        return booking;
    }

    // Get the list of booking by client id
    public ArrayList<Booking> getBookingByClientId(int id) {
        ArrayList<Booking> bookings = new ArrayList<>();

        // Select all query
        String selectQuery = String.format("SELECT * FROM %1$s WHERE %2$s = %3$s ORDER BY %4$s ASC",
                TABLE_NAME_BOOKING, COLUMN_CLIENT_ID, id, COLUMN_BOOKING_DURATION);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Booking booking = new Booking();
                booking.setBookingId(cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_ID)));
                booking.setBookingDate(cursor.getLong(cursor.getColumnIndex(COLUMN_BOOKING_DATE)));

                bookings.add(booking);
            } while (cursor.moveToNext());
        }

        cursor.close();

        // Close db connection
        db.close();

        // Return list of bookings
        return bookings;
    }

    // Get the list of booking services by booking id
    public ArrayList<BookingService> getBookingServicesByBookingId(int id) {
        ArrayList<BookingService> bookingServices = new ArrayList<>();

        // Select all query
        String selectQuery = String.format("SELECT * FROM %1$s WHERE %2$s = %3$s ORDER BY %4$s ASC",
                TABLE_NAME_BOOKING_SERVICE, COLUMN_BOOKING_ID, id, COLUMN_DISPLAY_ORDER);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BookingService bookingService = new BookingService();
                bookingService.setBookingId(id);
                bookingService.setServiceName(cursor.getString(cursor.getColumnIndex(COLUMN_SERVICE_NAME)));
                bookingService.setServicePrice(cursor.getFloat(cursor.getColumnIndex(COLUMN_SERVICE_PRICE)));
                bookingService.setServiceDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_SERVICE_DURATION)));

                bookingServices.add(bookingService);
            } while (cursor.moveToNext());
        }

        // Close cursor
        cursor.close();

        // Close db connection
        db.close();

        // Return list of bookings
        return bookingServices;
    }

    // Get all list of bookings
    public ArrayList<Booking> getAllBookings() {
        ArrayList<Booking> bookings = new ArrayList<>();

        // Select all query
        String selectQuery = String.format("SELECT * FROM %1$s ORDER BY %2$s ASC",
                TABLE_NAME_BOOKING, COLUMN_BOOKING_DURATION);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Booking booking = new Booking();
                booking.setBookingId(cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_ID)));
                booking.setCompanyId(cursor.getInt(cursor.getColumnIndex(COLUMN_COMPANY_ID)));
                booking.setCompanyName(cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY_NAME)));
                booking.setClientId(cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID)));
                booking.setClientName(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME)));
                booking.setStaffId(cursor.getInt(cursor.getColumnIndex(COLUMN_STAFF_ID)));
                booking.setStaffName(cursor.getString(cursor.getColumnIndex(COLUMN_STAFF_NAME)));
                booking.setBookingDate(cursor.getLong(cursor.getColumnIndex(COLUMN_BOOKING_DATE)));
                booking.setBookingDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_BOOKING_DURATION)));

                bookings.add(booking);
            } while (cursor.moveToNext());
        }

        // Close cursor
        cursor.close();

        // Close db connection
        db.close();

        // Return list of bookings
        return bookings;
    }

    // Delete all bookings
    public void deleteAllBookings() {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db1 = this.getWritableDatabase();

        // Deleting row
        db.delete(TABLE_NAME_BOOKING, null, null);
        db1.delete(TABLE_NAME_BOOKING_SERVICE, null, null);

        // Close db connection
        db.close();
        db1.close();
    }
}

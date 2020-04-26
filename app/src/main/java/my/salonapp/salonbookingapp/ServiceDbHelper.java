package my.salonapp.salonbookingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ServiceDbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "salon_services.db";

    // Table name
    private static final String TABLE_NAME = "salon_services";

    // Column names
    public static final String COLUMN_SERVICE_ID = "service_id";
    public static final String COLUMN_SERVICE_NAME = "service_name";
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_CATEGORY_NAME = "category_name";
    public static final String COLUMN_SERVICE_PRICE = "service_price";
    public static final String COLUMN_SERVICE_DURATION = "service_duration";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_SERVICE_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_SERVICE_NAME + " TEXT,"
                    + COLUMN_CATEGORY_ID + " INTEGER,"
                    + COLUMN_CATEGORY_NAME + " TEXT,"
                    + COLUMN_SERVICE_PRICE + " REAL,"
                    + COLUMN_SERVICE_DURATION + " INTEGER"
                    + ")";

    private Context context;

    public ServiceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create service table
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

    // Insert a new service
    public long addService(Service service) {
        // Get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVICE_ID, service.getServiceId());
        values.put(COLUMN_SERVICE_NAME, service.getServiceName());
        values.put(COLUMN_CATEGORY_ID, service.getCategoryId());
        values.put(COLUMN_CATEGORY_NAME, service.getCategoryName());
        values.put(COLUMN_SERVICE_PRICE, service.getServicePrice());
        values.put(COLUMN_SERVICE_DURATION, service.getServiceDuration());

        // Insert row
        long id = db.insert(TABLE_NAME, null, values);

        // Close db connection
        db.close();

        // Return newly inserted row id
        return id;
    }

    // Insert a new list of services
    public void addAllServices(List<Service> services) {
        SQLiteDatabase db = getWritableDatabase();

        for (Service service : services) {
            ContentValues values = new ContentValues();

            values.put(COLUMN_SERVICE_ID, service.getServiceId());
            values.put(COLUMN_SERVICE_NAME, service.getServiceName());
            values.put(COLUMN_CATEGORY_ID, service.getCategoryId());
            values.put(COLUMN_CATEGORY_NAME, service.getCategoryName());
            values.put(COLUMN_SERVICE_PRICE, service.getServicePrice());
            values.put(COLUMN_SERVICE_DURATION, service.getServiceDuration());

            db.insert(TABLE_NAME, null, values);
        }

        // Close db connection
        db.close();
    }

    // Get a service by service ID
    public Service getServiceById(int id) {
        // Get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{
                        COLUMN_SERVICE_ID, COLUMN_SERVICE_NAME, COLUMN_CATEGORY_ID,
                        COLUMN_CATEGORY_NAME, COLUMN_SERVICE_PRICE, COLUMN_SERVICE_DURATION
                },
                COLUMN_SERVICE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        // Prepare service object
        Service service = new Service(
                cursor.getInt(cursor.getColumnIndex(COLUMN_SERVICE_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_SERVICE_NAME)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)),
                cursor.getFloat(cursor.getColumnIndex(COLUMN_SERVICE_PRICE)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_SERVICE_DURATION)));

        // Close the db connection
        cursor.close();

        // Return service
        return service;
    }

    // Get the list of services by category ID
    public ArrayList<Service> getServicesByCategoryId(int id) {
        ArrayList<Service> services = new ArrayList<>();

        String selectQuery = String.format("SELECT * FROM %1$s WHERE %2$s = %3$s ORDER BY %4$s ASC",
                TABLE_NAME, COLUMN_CATEGORY_ID, id, COLUMN_CATEGORY_NAME);

        // Get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Service service = new Service();
                service.setServiceId(cursor.getInt(cursor.getColumnIndex(COLUMN_SERVICE_ID)));
                service.setServiceName(cursor.getString(cursor.getColumnIndex(COLUMN_SERVICE_NAME)));
                service.setCategoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                service.setCategoryName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                service.setServicePrice(cursor.getFloat(cursor.getColumnIndex(COLUMN_SERVICE_PRICE)));
                service.setServiceDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_SERVICE_DURATION)));

                services.add(service);
            } while (cursor.moveToNext());
        }

        // Close the db connection
        cursor.close();

        // Return list of services
        return services;
    }

    // Get all list of services
    public ArrayList<Service> getAllServices() {
        ArrayList<Service> services = new ArrayList<>();

        // Select All Query
        String selectQuery = String.format("SELECT * FROM %1$s ORDER BY %2$s ASC",
                TABLE_NAME, COLUMN_SERVICE_ID);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Service service = new Service();
                service.setServiceId(cursor.getInt(cursor.getColumnIndex(COLUMN_SERVICE_ID)));
                service.setServiceName(cursor.getString(cursor.getColumnIndex(COLUMN_SERVICE_NAME)));
                service.setCategoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                service.setCategoryName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));
                service.setServicePrice(cursor.getFloat(cursor.getColumnIndex(COLUMN_SERVICE_PRICE)));
                service.setServiceDuration(cursor.getInt(cursor.getColumnIndex(COLUMN_SERVICE_DURATION)));

                services.add(service);
            } while (cursor.moveToNext());
        }

        // Close db connection
        db.close();

        // Return services list
        return services;
    }

    // Delete all services
    public void deleteAllServices() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Deleting row
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}

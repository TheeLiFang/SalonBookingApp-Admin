package my.salonapp.salonbookingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TransactionHistoryDbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "transaction_history.db";

    // Table name
    private static final String TABLE_NAME_TRANSACTION_HISTORY = "transaction_histories";
    private static final String TABLE_NAME_TRANSACTION_HISTORY_SERVICE = "transaction_history_services";

    // Column names
    private static final String COLUMN_TRANSACTION_ID = "transaction_id";
    private static final String COLUMN_REF_NO = "ref_no";
    private static final String COLUMN_STAFF_NAME = "staff_name";
    private static final String COLUMN_CLIENT_ID = "client_id";
    private static final String COLUMN_CLIENT_NAME = "client_name";
    private static final String COLUMN_BOOKING_DATE = "booking_date";
    private static final String COLUMN_SERVICES_DESC = "services_desc";
    private static final String COLUMN_SUB_TOTAL = "sub_total";
    // Column names
    private static final String COLUMN_BOOKING_SERVICE_ID = "booking_service_id";
    private static final String COLUMN_SERVICE_NAME = "service_name";
    private static final String COLUMN_SERVICE_PRICE = "service_price";
    private static final String COLUMN_DISPLAY_ORDER = "display_order";

    // Create table SQL query
    private static final String CREATE_TABLE_TRANSACTION_HISTORY =
            "CREATE TABLE " + TABLE_NAME_TRANSACTION_HISTORY + "("
                    + COLUMN_TRANSACTION_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_REF_NO + " TEXT,"
                    + COLUMN_STAFF_NAME + " TEXT,"
                    + COLUMN_CLIENT_ID + " INTEGER,"
                    + COLUMN_CLIENT_NAME + " TEXT,"
                    + COLUMN_BOOKING_DATE + " INTEGER,"
                    + COLUMN_SERVICES_DESC + " TEXT,"
                    + COLUMN_SUB_TOTAL + " INTEGER"
                    + ")";
    private static final String CREATE_TABLE_TRANSACTION_HISTORY_SERVICE =
            "CREATE TABLE " + TABLE_NAME_TRANSACTION_HISTORY_SERVICE + "("
                    + COLUMN_BOOKING_SERVICE_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_TRANSACTION_ID + " INTEGER,"
                    + COLUMN_SERVICE_NAME + " TEXT,"
                    + COLUMN_SERVICE_PRICE + " REAL,"
                    + COLUMN_DISPLAY_ORDER + " INTEGER"
                    + ")";

    private Context context;

    public TransactionHistoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create booking table
        db.execSQL(CREATE_TABLE_TRANSACTION_HISTORY);
        db.execSQL(CREATE_TABLE_TRANSACTION_HISTORY_SERVICE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TRANSACTION_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TRANSACTION_HISTORY_SERVICE);

        // Create tables again
        onCreate(db);
    }

    // Insert a new list of transaction histories
    public void addAllTransactionHistories(ArrayList<TransactionHistory> transactionHistories) {
        SQLiteDatabase db = getWritableDatabase();
        SQLiteDatabase db1 = getWritableDatabase();

        for (TransactionHistory transactionHistory : transactionHistories) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_TRANSACTION_ID, transactionHistory.getTransactionId());
            values.put(COLUMN_REF_NO, transactionHistory.getRefNo());
            values.put(COLUMN_STAFF_NAME, transactionHistory.getStaffName());
            values.put(COLUMN_CLIENT_ID, transactionHistory.getClientID());
            values.put(COLUMN_CLIENT_NAME, transactionHistory.getClientName());
            values.put(COLUMN_BOOKING_DATE, transactionHistory.getBookingDate());
            values.put(COLUMN_SERVICES_DESC, transactionHistory.getServicesDesc());
            values.put(COLUMN_SUB_TOTAL, transactionHistory.getSubTotal());

            db.insert(TABLE_NAME_TRANSACTION_HISTORY, null, values);

            for (TransactionHistoryService transactionHistoryService : transactionHistory.getTransactionHistoryServices()) {
                ContentValues values1 = new ContentValues();
                values1.put(COLUMN_TRANSACTION_ID, transactionHistoryService.getTransactionId());
                values1.put(COLUMN_BOOKING_SERVICE_ID, transactionHistoryService.getBookingServiceId());
                values1.put(COLUMN_SERVICE_NAME, transactionHistoryService.getServiceName());
                values1.put(COLUMN_SERVICE_PRICE, transactionHistoryService.getServicePrice());
                values1.put(COLUMN_DISPLAY_ORDER, transactionHistoryService.getDisplayOrder());

                db1.insert(TABLE_NAME_TRANSACTION_HISTORY_SERVICE, null, values1);
            }
        }

        // Close db connection
        db.close();
        db1.close();
    }

    // Get a transaction history by transaction id
    public TransactionHistory getTransactionHistoryById(int id) {
        // Get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME_TRANSACTION_HISTORY,
                new String[]{
                        COLUMN_TRANSACTION_ID, COLUMN_REF_NO, COLUMN_STAFF_NAME,
                        COLUMN_CLIENT_ID, COLUMN_CLIENT_NAME, COLUMN_BOOKING_DATE,
                        COLUMN_SERVICES_DESC, COLUMN_SUB_TOTAL
                },
                COLUMN_TRANSACTION_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        // Prepare transaction history object
        TransactionHistory transactionHistory = new TransactionHistory(
                cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_REF_NO)),
                cursor.getString(cursor.getColumnIndex(COLUMN_STAFF_NAME)),
                cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME)),
                cursor.getLong(cursor.getColumnIndex(COLUMN_BOOKING_DATE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_SERVICES_DESC)),
                cursor.getFloat(cursor.getColumnIndex(COLUMN_SUB_TOTAL)),
                null);

        // Close db connection
        cursor.close();

        // Return transaction history
        return transactionHistory;
    }

    // Get the list of transaction history services by transaction id
    public ArrayList<TransactionHistory> getTransactionHistoriesByClientId(int id) {
        ArrayList<TransactionHistory> transactionHistories = new ArrayList<>();

        // Select all query
        String selectQuery = String.format("SELECT * FROM %1$s WHERE %2$s = %3$s ORDER BY %4$s DESC",
                TABLE_NAME_TRANSACTION_HISTORY, COLUMN_CLIENT_ID, id, COLUMN_BOOKING_DATE);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TransactionHistory transactionHistory = new TransactionHistory();
                transactionHistory.setTransactionId(id);
                transactionHistory.setRefNo(cursor.getString(cursor.getColumnIndex(COLUMN_REF_NO)));
                transactionHistory.setStaffName(cursor.getString(cursor.getColumnIndex(COLUMN_STAFF_NAME)));
                transactionHistory.setClientID(cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID)));
                transactionHistory.setClientName(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME)));
                transactionHistory.setBookingDate(cursor.getLong(cursor.getColumnIndex(COLUMN_BOOKING_DATE)));
                transactionHistory.setServicesDesc(cursor.getString(cursor.getColumnIndex(COLUMN_SERVICES_DESC)));
                transactionHistory.setSubTotal(cursor.getFloat(cursor.getColumnIndex(COLUMN_SUB_TOTAL)));

                transactionHistories.add(transactionHistory);
            } while (cursor.moveToNext());
        }

        // Close cursor
        cursor.close();

        // Close db connection
        db.close();

        // Return list of transaction history services
        return transactionHistories;
    }

    // Get the list of transaction history services by transaction id
    public ArrayList<TransactionHistoryService> getTransactionHistoryServicesByTransactionId(int id) {
        ArrayList<TransactionHistoryService> transactionHistoryServices = new ArrayList<>();

        // Select all query
        String selectQuery = String.format("SELECT * FROM %1$s WHERE %2$s = %3$s ORDER BY %4$s ASC",
                TABLE_NAME_TRANSACTION_HISTORY_SERVICE, COLUMN_TRANSACTION_ID, id, COLUMN_DISPLAY_ORDER);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TransactionHistoryService transactionHistoryService = new TransactionHistoryService();
                transactionHistoryService.setTransactionId(id);
                transactionHistoryService.setServiceName(cursor.getString(cursor.getColumnIndex(COLUMN_SERVICE_NAME)));
                transactionHistoryService.setServicePrice(cursor.getFloat(cursor.getColumnIndex(COLUMN_SERVICE_PRICE)));

                transactionHistoryServices.add(transactionHistoryService);
            } while (cursor.moveToNext());
        }

        // Close cursor
        cursor.close();

        // Close db connection
        db.close();

        // Return list of transaction history services
        return transactionHistoryServices;
    }

    // Get all list of transaction histories
    public ArrayList<TransactionHistory> getAllTransactionHistories() {
        ArrayList<TransactionHistory> transactionHistories = new ArrayList<>();

        // Select all query
        String selectQuery = String.format("SELECT * FROM %1$s ORDER BY %2$s DESC",
                TABLE_NAME_TRANSACTION_HISTORY, COLUMN_BOOKING_DATE);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TransactionHistory transactionHistory = new TransactionHistory();
                transactionHistory.setTransactionId(cursor.getInt(cursor.getColumnIndex(COLUMN_TRANSACTION_ID)));
                transactionHistory.setRefNo(cursor.getString(cursor.getColumnIndex(COLUMN_REF_NO)));
                transactionHistory.setStaffName(cursor.getString(cursor.getColumnIndex(COLUMN_STAFF_NAME)));
                transactionHistory.setClientID(cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID)));
                transactionHistory.setClientName(cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME)));
                transactionHistory.setBookingDate(cursor.getLong(cursor.getColumnIndex(COLUMN_BOOKING_DATE)));
                transactionHistory.setServicesDesc(cursor.getString(cursor.getColumnIndex(COLUMN_SERVICES_DESC)));
                transactionHistory.setSubTotal(cursor.getFloat(cursor.getColumnIndex(COLUMN_SUB_TOTAL)));

                transactionHistories.add(transactionHistory);
            } while (cursor.moveToNext());
        }

        // Close cursor
        cursor.close();

        // Close db connection
        db.close();

        // Return list of transaction histories
        return transactionHistories;
    }

    // Delete all transaction histories
    public void deleteAllTransactionHistories() {
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db1 = this.getWritableDatabase();

        // Deleting row
        db.delete(TABLE_NAME_TRANSACTION_HISTORY, null, null);
        db1.delete(TABLE_NAME_TRANSACTION_HISTORY_SERVICE, null, null);

        // Close db connection
        db.close();
        db1.close();
    }
}

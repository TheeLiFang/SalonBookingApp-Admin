package my.salonapp.salonbookingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ClientDbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    public static final String DATABASE_NAME = "client.db";

    // Table name
    private static final String TABLE_NAME = "client";

    // Column names
    public static final String COLUMN_CLIENT_ID = "client_id";
    public static final String COLUMN_CLIENT_NAME = "client_name";
    public static final String COLUMN_CLIENT_EMAIL = "client_email";
    public static final String COLUMN_CLIENT_PHONE = "client_phone";
    public static final String COLUMN_CLIENT_ALLERGIC_REMARK = "client_allergic_remark";
    public static final String COLUMN_CLIENT_REMARK = "client_remark";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_CLIENT_ID + " INTEGER,"
                    + COLUMN_CLIENT_NAME + " TEXT,"
                    + COLUMN_CLIENT_EMAIL + " TEXT,"
                    + COLUMN_CLIENT_PHONE + " TEXT,"
                    + COLUMN_CLIENT_ALLERGIC_REMARK + " TEXT,"
                    + COLUMN_CLIENT_REMARK + " TEXT"
                    + ")";

    private Context context;

    public ClientDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create client table
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

    // Insert a new list of client
    public void addAllClients(List<Client> clients) {
        SQLiteDatabase db = getWritableDatabase();

        for (Client client : clients) {
            ContentValues values = new ContentValues();

            values.put(COLUMN_CLIENT_ID, client.getClientId());
            values.put(COLUMN_CLIENT_NAME, client.getClientName());
            values.put(COLUMN_CLIENT_EMAIL, client.getClientEmail());
            values.put(COLUMN_CLIENT_PHONE, client.getClientPhone());
            values.put(COLUMN_CLIENT_ALLERGIC_REMARK, client.getClientAllergicRemark());
            values.put(COLUMN_CLIENT_REMARK, client.getClientRemark());

            db.insert(TABLE_NAME, null, values);
        }

        // Close db connection
        db.close();
    }

    // Get a client by client ID
    public Client getClientById(int id) {
        // Get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                new String[]{
                        COLUMN_CLIENT_ID, COLUMN_CLIENT_NAME, COLUMN_CLIENT_EMAIL,
                        COLUMN_CLIENT_PHONE, COLUMN_CLIENT_ALLERGIC_REMARK, COLUMN_CLIENT_REMARK
                },
                COLUMN_CLIENT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        // Prepare client object
        Client client = new Client(
                cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_EMAIL)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_PHONE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_ALLERGIC_REMARK)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_REMARK)));

        // Close db connection
        cursor.close();

        // Return client
        return client;
    }

    // Get all list of clients
    public ArrayList<Client> getAllClients() {
        ArrayList<Client> clients = new ArrayList<>();

        // Select All Query
        String selectQuery = String.format("SELECT * FROM %1$s ORDER BY %2$s ASC",
                TABLE_NAME, COLUMN_CLIENT_ID);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                clients.add(new Client(cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_EMAIL)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_PHONE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_ALLERGIC_REMARK)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_REMARK))));
            } while (cursor.moveToNext());
        }

        // Close connection
        cursor.close();
        db.close();

        // Return clients list
        return clients;
    }

    // Get all list of clients
    public ArrayList<Client> getAllSpinnerClients() {
        ArrayList<Client> clients = new ArrayList<>();

        // Select All Query
        String selectQuery = String.format("SELECT DISTINCT %1$s, %2$s FROM %3$s ORDER BY %4$s ASC",
                COLUMN_CLIENT_ID, COLUMN_CLIENT_NAME, TABLE_NAME, COLUMN_CLIENT_NAME);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                clients.add(new Client(cursor.getInt(cursor.getColumnIndex(COLUMN_CLIENT_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_NAME))));
            } while (cursor.moveToNext());
        }

        // Close connection
        cursor.close();
        db.close();

        // Return clients list
        return clients;
    }

    // Delete all clients
    public void deleteAllClients() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Deleting row
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}

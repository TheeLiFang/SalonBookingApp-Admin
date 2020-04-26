package my.salonapp.salonbookingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CompanyDbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "company.db";

    // Table name
    private static final String TABLE_NAME = "companies";

    // Column names
    private static final String COLUMN_COMPANY_ID = "company_id";
    private static final String COLUMN_COMPANY_NAME = "company_name";
    private static final String COLUMN_COMPANY_EMAIL = "company_email";
    private static final String COLUMN_COMPANY_PASSWORD = "company_password";
    private static final String COLUMN_COMPANY_PHONE = "company_phone";
    private static final String COLUMN_COMPANY_ADDRESS = "company_address";
    private static final String COLUMN_MONDAY_YN = "monday_yn";
    private static final String COLUMN_MONDAY_START_TIME = "monday_start_time";
    private static final String COLUMN_MONDAY_END_TIME = "monday_end_time";
    private static final String COLUMN_TUESDAY_YN = "tuesday_yn";
    private static final String COLUMN_TUESDAY_START_TIME = "tuesday_start_time";
    private static final String COLUMN_TUESDAY_END_TIME = "tuesday_end_time";
    private static final String COLUMN_WEDNESDAY_YN = "wednesday_yn";
    private static final String COLUMN_WEDNESDAY_START_TIME = "wednesday_start_time";
    private static final String COLUMN_WEDNESDAY_END_TIME = "wednesday_end_time";
    private static final String COLUMN_THURSDAY_YN = "thursday_yn";
    private static final String COLUMN_THURSDAY_START_TIME = "thursday_start_time";
    private static final String COLUMN_THURSDAY_END_TIME = "thursday_end_time";
    private static final String COLUMN_FRIDAY_YN = "friday_yn";
    private static final String COLUMN_FRIDAY_START_TIME = "friday_start_time";
    private static final String COLUMN_FRIDAY_END_TIME = "friday_end_time";
    private static final String COLUMN_SATURDAY_YN = "saturday_yn";
    private static final String COLUMN_SATURDAY_START_TIME = "saturday_start_time";
    private static final String COLUMN_SATURDAY_END_TIME = "saturday_end_time";
    private static final String COLUMN_SUNDAY_YN = "sunday_yn";
    private static final String COLUMN_SUNDAY_START_TIME = "sunday_start_time";
    private static final String COLUMN_SUNDAY_END_TIME = "sunday_end_time";

    // Create table SQL query
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_COMPANY_ID + " INTEGER PRIMARY KEY,"
                    + COLUMN_COMPANY_NAME + " TEXT,"
                    + COLUMN_COMPANY_EMAIL + " TEXT,"
                    + COLUMN_COMPANY_PASSWORD+ " TEXT,"
                    + COLUMN_COMPANY_PHONE + " TEXT,"
                    + COLUMN_COMPANY_ADDRESS + " TEXT,"
                    + COLUMN_MONDAY_YN + " INTEGER,"
                    + COLUMN_MONDAY_START_TIME + " TEXT,"
                    + COLUMN_MONDAY_END_TIME + " TEXT,"
                    + COLUMN_TUESDAY_YN + " INTEGER,"
                    + COLUMN_TUESDAY_START_TIME + " TEXT,"
                    + COLUMN_TUESDAY_END_TIME + " TEXT,"
                    + COLUMN_WEDNESDAY_YN + " INTEGER,"
                    + COLUMN_WEDNESDAY_START_TIME + " TEXT,"
                    + COLUMN_WEDNESDAY_END_TIME + " TEXT,"
                    + COLUMN_THURSDAY_YN + " INTEGER,"
                    + COLUMN_THURSDAY_START_TIME + " TEXT,"
                    + COLUMN_THURSDAY_END_TIME + " TEXT,"
                    + COLUMN_FRIDAY_YN + " INTEGER,"
                    + COLUMN_FRIDAY_START_TIME + " TEXT,"
                    + COLUMN_FRIDAY_END_TIME + " TEXT,"
                    + COLUMN_SATURDAY_YN + " INTEGER,"
                    + COLUMN_SATURDAY_START_TIME + " TEXT,"
                    + COLUMN_SATURDAY_END_TIME + " TEXT,"
                    + COLUMN_SUNDAY_YN + " INTEGER,"
                    + COLUMN_SUNDAY_START_TIME + " TEXT,"
                    + COLUMN_SUNDAY_END_TIME + " TEXT"
                    + ")";

    private Context context;

    public CompanyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create company table
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

    // Insert a new company
    public long addCompany(Company company) {
        // Get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPANY_ID, company.getCompanyId());
        values.put(COLUMN_COMPANY_NAME, company.getCompanyName());
        values.put(COLUMN_COMPANY_EMAIL, company.getCompanyEmail());
        values.put(COLUMN_COMPANY_PASSWORD, company.getCompanyPassword());
        values.put(COLUMN_COMPANY_PHONE, company.getCompanyPhone());
        values.put(COLUMN_COMPANY_ADDRESS, company.getCompanyAddress());
        values.put(COLUMN_MONDAY_YN, company.getMondayYN());
        values.put(COLUMN_MONDAY_START_TIME, company.getMondayStartTime());
        values.put(COLUMN_MONDAY_END_TIME, company.getMondayEndTime());
        values.put(COLUMN_TUESDAY_YN, company.getTuesdayYN());
        values.put(COLUMN_TUESDAY_START_TIME, company.getTuesdayStartTime());
        values.put(COLUMN_TUESDAY_END_TIME, company.getTuesdayEndTime());
        values.put(COLUMN_WEDNESDAY_YN, company.getWednesdayYN());
        values.put(COLUMN_WEDNESDAY_START_TIME, company.getWednesdayStartTime());
        values.put(COLUMN_WEDNESDAY_END_TIME, company.getWednesdayEndTime());
        values.put(COLUMN_THURSDAY_YN, company.getThursdayYN());
        values.put(COLUMN_THURSDAY_START_TIME, company.getThursdayStartTime());
        values.put(COLUMN_THURSDAY_END_TIME, company.getThursdayEndTime());
        values.put(COLUMN_FRIDAY_YN, company.getFridayYN());
        values.put(COLUMN_FRIDAY_START_TIME, company.getFridayStartTime());
        values.put(COLUMN_FRIDAY_END_TIME, company.getFridayEndTime());
        values.put(COLUMN_SATURDAY_YN, company.getSaturdayYN());
        values.put(COLUMN_SATURDAY_START_TIME, company.getSaturdayStartTime());
        values.put(COLUMN_SATURDAY_END_TIME, company.getSaturdayEndTime());
        values.put(COLUMN_SUNDAY_YN, company.getSundayYN());
        values.put(COLUMN_SUNDAY_START_TIME, company.getSundayStartTime());
        values.put(COLUMN_SUNDAY_END_TIME, company.getSundayEndTime());

        // Insert row
        long id = db.insert(TABLE_NAME, null, values);

        // Close db connection
        db.close();

        // Return newly inserted row id
        return id;
    }

    // Get company user name
    public String getCompanyName() {
        String companyName = "";

        // Select all query
        String selectQuery = String.format("SELECT %1$s FROM %2$s LIMIT 1",
                COLUMN_COMPANY_NAME, TABLE_NAME);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            companyName = cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY_NAME));
        }

        // Close cursor
        cursor.close();

        // Close db connection
        db.close();

        // Return company name
        return companyName;
    }

    // Get company ID
    public int getCompanyId() {
        int id = 0;

        // Select all query
        String selectQuery = String.format("SELECT %1$s FROM %2$s LIMIT 1",
                COLUMN_COMPANY_ID, TABLE_NAME);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_COMPANY_ID));
        }

        // Close cursor
        cursor.close();

        // Close db connection
        db.close();

        // Return company ID
        return id;
    }

    // Get a company by company ID
    public Company getCompany() {
        // Select all query
        String selectQuery = String.format(
                "SELECT %1$s, %2$s, %3$s, %4$s, %5$s, %6$s,\n" +
                        "%7$s, %8$s, %9$s, %10$s, %11$s, %12$s,\n" +
                        "%13$s, %14$s, %15$s, %16$s, %17$s, %18$s,\n" +
                        "%19$s, %20$s, %21$s, %22$s, %23$s, %24$s,\n" +
                        "%25$s, %26$s, %27$s FROM %28$s LIMIT 1\n",
                COLUMN_COMPANY_ID, COLUMN_COMPANY_NAME, COLUMN_COMPANY_EMAIL,
                COLUMN_COMPANY_PASSWORD, COLUMN_COMPANY_PHONE, COLUMN_COMPANY_ADDRESS,
                COLUMN_MONDAY_YN, COLUMN_MONDAY_START_TIME, COLUMN_MONDAY_END_TIME,
                COLUMN_TUESDAY_YN, COLUMN_TUESDAY_START_TIME, COLUMN_TUESDAY_END_TIME,
                COLUMN_WEDNESDAY_YN, COLUMN_WEDNESDAY_START_TIME, COLUMN_WEDNESDAY_END_TIME,
                COLUMN_THURSDAY_YN, COLUMN_THURSDAY_START_TIME, COLUMN_THURSDAY_END_TIME,
                COLUMN_FRIDAY_YN, COLUMN_FRIDAY_START_TIME, COLUMN_FRIDAY_END_TIME,
                COLUMN_SATURDAY_YN, COLUMN_SATURDAY_START_TIME, COLUMN_SATURDAY_END_TIME,
                COLUMN_SUNDAY_YN, COLUMN_SUNDAY_START_TIME, COLUMN_SUNDAY_END_TIME, TABLE_NAME);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Company company = null;

        if (cursor.moveToFirst()) {
            // Prepare company object
            company = new Company(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_COMPANY_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY_PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY_PHONE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_COMPANY_ADDRESS)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_MONDAY_YN)) > 0,
                    cursor.getString(cursor.getColumnIndex(COLUMN_MONDAY_START_TIME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_MONDAY_END_TIME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_TUESDAY_YN)) > 0,
                    cursor.getString(cursor.getColumnIndex(COLUMN_TUESDAY_START_TIME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TUESDAY_END_TIME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_WEDNESDAY_YN)) > 0,
                    cursor.getString(cursor.getColumnIndex(COLUMN_WEDNESDAY_START_TIME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_WEDNESDAY_END_TIME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_THURSDAY_YN)) > 0,
                    cursor.getString(cursor.getColumnIndex(COLUMN_THURSDAY_START_TIME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_THURSDAY_END_TIME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_FRIDAY_YN)) > 0,
                    cursor.getString(cursor.getColumnIndex(COLUMN_FRIDAY_START_TIME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_FRIDAY_END_TIME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_SATURDAY_YN)) > 0,
                    cursor.getString(cursor.getColumnIndex(COLUMN_SATURDAY_START_TIME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SATURDAY_END_TIME)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_SUNDAY_YN)) > 0,
                    cursor.getString(cursor.getColumnIndex(COLUMN_SUNDAY_START_TIME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_SUNDAY_END_TIME)));
        }

        // Close cursor
        cursor.close();

        // Close db connection
        db.close();

        // Return company
        return company;
    }

    // Delete all companies
    public void deleteAllCompanies() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Deleting row
        db.delete(TABLE_NAME, null, null);

        // Close db connection
        db.close();
    }
}

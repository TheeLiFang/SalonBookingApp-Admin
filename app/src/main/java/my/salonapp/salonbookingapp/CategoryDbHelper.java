package my.salonapp.salonbookingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CategoryDbHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    public static final String DATABASE_NAME = "category.db";

    // Table name
    private static final String TABLE_NAME = "category";

    // Column names
    public static final String COLUMN_CATEGORY_ID = "category_id";
    public static final String COLUMN_CATEGORY_NAME = "category_name";

    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_CATEGORY_ID + " INTEGER,"
                    + COLUMN_CATEGORY_NAME + " TEXT"
                    + ")";

    private Context context;

    public CategoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create category table
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

    // Insert a new list of category
    public void addAllCategories(List<Category> categories) {
        SQLiteDatabase db = getWritableDatabase();

        for (Category category : categories) {
            ContentValues values = new ContentValues();

            values.put(COLUMN_CATEGORY_ID, category.getCategoryId());
            values.put(COLUMN_CATEGORY_NAME, category.getCategoryName());

            db.insert(TABLE_NAME, null, values);
        }

        // Close db connection
        db.close();
    }

    // Get all list of categories
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();

        // Select All Query
        String selectQuery = String.format("SELECT * FROM %1$s ORDER BY %2$s ASC",
                TABLE_NAME, COLUMN_CATEGORY_ID);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                /*Category category = new Category();
                category.setCategoryId(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)));
                category.setCategoryName(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME)));*/

                categories.add(new Category(cursor.getInt(cursor.getColumnIndex(COLUMN_CATEGORY_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY_NAME))));
            } while (cursor.moveToNext());
        }

        // Close connection
        cursor.close();
        db.close();

        // Return categories list
        return categories;
    }

    // Delete all categories
    public void deleteAllCategories() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Deleting row
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}

package com.example.coupondunia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

	private static DBHelper mInstance=null;
	
	// *******DataBase Version
	private static final int DATABASE_VERSION = 1;
	
	// *******DataBase Name
	private static final String DATABASE_NAME = "CouponDuniya";
	 
	// *******Table Names
	private static final String TABLE_COUPON_DETAILS = "T_COUPON_DETAILS";
	private static final String TABLE_COUPON_CATEGORY_DETAIL="T_CATEGORY_DETAILS"; 	

	// *******Column names for T_COUPON_DETAILS
	private static final String TCD_OUTLET_ID = "outlet_id";
	private static final String TCD_OUTLET_NAME = "outlet_Name";
	private static final String TCD_NUM_COUPONS = "num_coupons";
	private static final String TCD_LOGO_URL = "logo_url";
	private static final String TCD_LATITUDE = "latitude";
	private static final String TCD_LONGITUDE = "longitude";
	private static final String TCD_NEIGHBOURHOOD = "neighbourhood";
	
	// *******Column names for T_CATEGORY_DETAILS
	private static final String TCCD_CATEGORY_ID = "id";
	private static final String TCCD_CATEGORY = "category";
	private static final String TCCD_OUTLET_ID = "outlet_id";
		
	public static DBHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new DBHelper(context);
		}
		return mInstance;
	}

	public DBHelper(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {

		Log.d("creating tables..","1");

		// Create script for table_coupon_details
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_COUPON_DETAILS + "("
                + TCD_OUTLET_ID + " TEXT PRIMARY KEY,"
                + TCD_OUTLET_NAME + " TEXT,"
                + TCD_NUM_COUPONS + " TEXT,"
				+ TCD_LOGO_URL + " TEXT,"
                + TCD_NEIGHBOURHOOD + " TEXT,"
                + TCD_LATITUDE + " TEXT,"
                + TCD_LONGITUDE + " TEXT )"
                +";");
		Log.d("creating..","creating coupon table");
		
		// Create script for table_coupon_category_details
		db.execSQL("CREATE TABLE IF NOT EXISTS "
                + TABLE_COUPON_CATEGORY_DETAIL + "("
                + TCCD_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TCCD_OUTLET_ID + " TEXT,"
                + TCCD_CATEGORY + " TEXT,"
                + " FOREIGN KEY ("
                + TCCD_OUTLET_ID +
                ") REFERENCES "
                + TABLE_COUPON_DETAILS
                +"("+TCD_OUTLET_ID+"));");

		Log.d("creating...","creating category table");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUPON_DETAILS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUPON_CATEGORY_DETAIL);
		onCreate(db);
	}
	
	// ******************** Coupon Details Table Methods *********************
	
	// Inserting Coupon details in sqlite
	public Boolean insertCouponDetails(BeanCouponDetails beanCouponDetails) {
		try {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues requestDlsValues = new ContentValues();
			requestDlsValues.put(TCD_OUTLET_ID, beanCouponDetails.getOutletId());
			requestDlsValues.put(TCD_OUTLET_NAME, beanCouponDetails.getOutletName());
			requestDlsValues.put(TCD_NUM_COUPONS,beanCouponDetails.getNumCoupons());
			requestDlsValues.put(TCD_LOGO_URL, beanCouponDetails.getLogoUrl());
			requestDlsValues.put(TCD_LATITUDE, beanCouponDetails.getLatitude());
			requestDlsValues.put(TCD_LONGITUDE, beanCouponDetails.getLongitude());
			requestDlsValues.put(TCD_NEIGHBOURHOOD,beanCouponDetails.getNeighbourhood());
			
			db.insert(TABLE_COUPON_DETAILS, null, requestDlsValues);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	// Fetching coupon details for sqlite
	public ArrayList<BeanCouponDetails> GetCouponDetails() {
        try {
            ArrayList<BeanCouponDetails> beanCouponDetails = new ArrayList<BeanCouponDetails>();
            SQLiteDatabase db = this.getWritableDatabase();

            String selectQuery = "SELECT " + TCD_OUTLET_NAME + ","
                    + TCD_NUM_COUPONS + ","
                    + TCD_LOGO_URL + ","
                    + TCD_LATITUDE + ","
                    + TCD_LONGITUDE + ","
                    + TCD_NEIGHBOURHOOD + ","
                    + TCD_OUTLET_ID +
                    " FROM " + TABLE_COUPON_DETAILS;


            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    BeanCouponDetails newItem = new BeanCouponDetails();
                    newItem.setOutletName(cursor.getString(0));
                    newItem.setNumCoupons(cursor.getString(1));
                    newItem.setLogoUrl(cursor.getString(2));
                    newItem.setLatitude(cursor.getDouble(3));
                    newItem.setLongitude(cursor.getDouble(4));
                    newItem.setNeighbourhood(cursor.getString(5));
                    newItem.setOutletId(cursor.getString(6));

                    beanCouponDetails.add(newItem);
                    System.out.println("userDetails fetched");
                } while (cursor.moveToNext());
            }
            cursor.close();
            return beanCouponDetails;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public long checkRowExistOrNot() {
        long count;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String selectQuery = "SELECT COUNT(*) FROM " + TABLE_COUPON_DETAILS;
            SQLiteStatement statement = db.compileStatement(selectQuery);
            count = statement.simpleQueryForLong();

            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    // ******************** Coupon Category Details Table Methods *********************

	// Inserting Coupon Category details in sqlite

	// Inserting Coupon details in sqlite
	public Boolean insertCategoryCouponDetails(BeanCategoryDetails beanCategoryDetails) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues requestDlsValues = new ContentValues();
            requestDlsValues.put(TCCD_OUTLET_ID, beanCategoryDetails.getOutletId());
            requestDlsValues.put(TCCD_CATEGORY, beanCategoryDetails.getCategory());

            db.insert(TABLE_COUPON_CATEGORY_DETAIL, null, requestDlsValues);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
		
		// Fetching category details in sqlite
	public HashMap<String,List<BeanCategoryDetails>> GetCategoryDetails() {
        String outletID, category, selectQuery;
        try {
            HashMap<String, List<BeanCategoryDetails>> categoryMap =
                    new HashMap<String, List<BeanCategoryDetails>>();
            SQLiteDatabase db = this.getWritableDatabase();

            selectQuery = "SELECT "
                    + TCCD_OUTLET_ID + ","
                    + TCCD_CATEGORY
                    + " FROM "
                    + TABLE_COUPON_CATEGORY_DETAIL;

            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    outletID = cursor.getString(0);
                    category = cursor.getString(1);
                    List<BeanCategoryDetails> beanCategoryDetails = new ArrayList<BeanCategoryDetails>();
                    BeanCategoryDetails newItem = new BeanCategoryDetails();
                    newItem.setOutletId(outletID);
                    newItem.setCategory(category);

                    List<BeanCategoryDetails> listCategory = categoryMap.get(cursor.getString(0));
                    if (listCategory == null) {
                        beanCategoryDetails.add(newItem);
                        categoryMap.put(outletID, beanCategoryDetails);
                    } else {
                        listCategory.add(newItem);
                        categoryMap.put(outletID, listCategory);
                    }
                    System.out.println("Category fetched");
                } while (cursor.moveToNext());
            }
            cursor.close();
            return categoryMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

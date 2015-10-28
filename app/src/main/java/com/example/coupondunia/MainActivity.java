package com.example.coupondunia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener{

	// URL to get JSON
    private static String url = "http://staging.couponapitest.com/task_data.txt";

    // JSON Node names
    private static final String TAG_DATA = "data";
    private static final String TAG_OUTLETNAME = "OutletName";
    private static final String TAG_NUMCOUPONS = "NumCoupons";
    private static final String TAG_LOGOURL = "LogoURL";
    private static final String TAG_OUTLETID = "OutletID";
    private static final String TAG_LONGITUDE = "Longitude";
    private static final String TAG_LATITUDE = "Latitude";
    private static final String TAG_CATEGORIES = "Categories";
    private static final String TAG_CATEGORY_NAME = "Name";
    private static final String TAG_PARENT_CATEGORY_ID = "ParentCategoryID";
    private static final String TAG_NEIGHBOURHOOD_NAME = "NeighbourhoodName";

    // coupons JSONArray
    JSONObject coupons = null;
    
    // ArrayList for coupons
    ArrayList<BeanCouponDetails> couponList;
    //HashMap for CategoryList with key as OutletId and value as List of Categories
    HashMap<String,List<BeanCategoryDetails>> categoryList=new HashMap<String,List<BeanCategoryDetails>>();

    DBHelper db; //DbHelper object to perform SQlite db operations
    String provider;
    ListView listView;
    CustomListAdapter customListAdapter;
    protected LocationManager locationManager;
    private Double latitude;
    private Double longitude;
    private ProgressDialog pDialog;
    long checkRowExistOrNot;
    String outletName,numCoupons,logoURL,outletID,neighbourhood;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_main);

	couponList = new ArrayList<BeanCouponDetails>();
	listView = (ListView)findViewById(android.R.id.list);
		
	//finding current location by getting LocationManager object
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
        // Creating an empty criteria object
        Criteria criteria = new Criteria();
        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);
        if(provider!=null && !provider.equals("")){
            // Get the location from the given provider
            Location location = locationManager.getLastKnownLocation(provider);
            locationManager.requestLocationUpdates(provider, 20000, 1, this);
            if(location!=null) {
                onLocationChanged(location);
            } else {
                latitude = 0.0D;
                longitude = 0.0D;
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }

	db=new DBHelper(this);
	db.getWritableDatabase();
		
	checkRowExistOrNot = db.checkRowExistOrNot();
	Log.d("check DB Exist","Checking.."+checkRowExistOrNot);
	if( checkRowExistOrNot <= 0) {
		Log.d("syncing..","syncing from web service");
		// Calling async task to get json
		new getCoupons().execute();
	} else {
		showFromDB();
		Log.d("fetching..","fetch from db");
	}
    }

    /**
     * * Async task class to get json by making HTTP call
     * */
     private class getCoupons extends AsyncTask<Void, Void, Void> {
     	
     	@Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    coupons = jsonObj.getJSONObject(TAG_DATA);
                    Iterator<String> keys = coupons.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONObject obj = coupons.getJSONObject(key);

                        outletName = obj.getString(TAG_OUTLETNAME);
                        numCoupons = obj.getString(TAG_NUMCOUPONS);
                        logoURL = obj.getString(TAG_LOGOURL);
                        outletID = obj.getString(TAG_OUTLETID);
                        neighbourhood = obj.getString(TAG_NEIGHBOURHOOD_NAME);
                        Double longitude = obj.getDouble(TAG_LONGITUDE);
                        Double latitude = obj.getDouble(TAG_LATITUDE);

                        BeanCouponDetails beanCouponDetails = new BeanCouponDetails(outletID,
                                outletName, numCoupons, logoURL, neighbourhood, latitude, longitude);

                        db.insertCouponDetails(beanCouponDetails);

                        // adding beanCouponDetails to coupon list
                        couponList.add(beanCouponDetails);

                        JSONArray jsonCategory = obj.getJSONArray(TAG_CATEGORIES);

                        List<BeanCategoryDetails> categories = new ArrayList<BeanCategoryDetails>();

                        for (int i = 0; i < jsonCategory.length(); i++) {
                            JSONObject jsonCategoryType = jsonCategory.getJSONObject(i);

                            Log.d("Categories", jsonCategoryType.getString(TAG_CATEGORY_NAME));
                            String category = jsonCategoryType.getString(TAG_CATEGORY_NAME);
                            String parentCategoryID = jsonCategoryType.getString(TAG_PARENT_CATEGORY_ID);
                            if (!parentCategoryID.equalsIgnoreCase("null")) {
                                BeanCategoryDetails beanCategoryDetails = new BeanCategoryDetails();
                                beanCategoryDetails.setOutletId(outletID);
                                beanCategoryDetails.setCategory(category);
                                db.insertCategoryCouponDetails(beanCategoryDetails);
                                categories.add(beanCategoryDetails);
                            }
                        }
                        categoryList.put(outletID, categories);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            setLocationDistance(couponList);
            Collections.sort(couponList, new SortList());
            customListAdapter = new CustomListAdapter(MainActivity.this, couponList, categoryList);
            listView.setAdapter(customListAdapter);
        }
    }

    // fetching data from db
	public void showFromDB(){
	couponList = db.GetCouponDetails();
	categoryList = db.GetCategoryDetails();
        setLocationDistance(couponList);
        Collections.sort(couponList, new SortList());
        customListAdapter =new CustomListAdapter(MainActivity.this, couponList,categoryList);
		listView.setAdapter(customListAdapter);
	}

    private void setLocationDistance(ArrayList<BeanCouponDetails> couponList) {
        Double dist;
        for(BeanCouponDetails bean:couponList){
            dist = getGeoDistance(latitude, longitude, bean.getLatitude(), bean.getLongitude());
            bean.setDistance(dist);
        }
    }

    public static double getGeoDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        return dist;
    }

    @Override
    public void onLocationChanged(Location location) {
    	// TODO Auto-generated method stub
    	latitude=location.getLatitude();
    	longitude=location.getLongitude();
    	Log.d("Lat,Long","Latitude "+latitude+" longitude "+longitude);
    }

    @Override
    public void onProviderDisabled(String provider) {
    	// TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
    	// TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
	// TODO Auto-generated method stub
    }
}

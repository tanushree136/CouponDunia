package com.example.coupondunia;

import java.util.List;
import java.util.Map;

import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<BeanCouponDetails>{

	private final Activity context;
	private final List<BeanCouponDetails> beanCouponList;
	private final Map<String, List<BeanCategoryDetails>> category;

    TextView OutletName, NumCoupons, Distance, Neighbourhood;
    int numOfCoupons;
    String outletID;

	public CustomListAdapter(Activity context, List<BeanCouponDetails> beanCouponList,Map<String,List<BeanCategoryDetails>> category) {
		super(context, R.layout.list_item, beanCouponList);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.beanCouponList = beanCouponList;
		this.category = category;
	}

	public View getView(int position,View view,ViewGroup parent) {
		LayoutInflater inflater=context.getLayoutInflater();
		View vi=inflater.inflate(R.layout.list_item, null, true);

		BeanCouponDetails beanCoupon = beanCouponList.get(position);

        OutletName=(TextView)vi.findViewById(R.id.outlet_name);
        OutletName.setText(beanCoupon.getOutletName());
        Distance=(TextView)vi.findViewById(R.id.distance);
        Distance.setText(Integer.valueOf((int)Math.floor(beanCoupon.getDistance() + 0.5d)).toString() + " m");
        Neighbourhood=(TextView)vi.findViewById(R.id.neighbour);
        Neighbourhood.setText(beanCoupon.getNeighbourhood());

        NumCoupons=(TextView)vi.findViewById(R.id.numcoupons);
        numOfCoupons = Integer.parseInt(beanCoupon.getNumCoupons());
        if(numOfCoupons > 1) {
            NumCoupons.setText(numOfCoupons+" Offers");
        } else {
            NumCoupons.setText(numOfCoupons+" Offer");
        }

        Picasso.with(vi.getContext()).load(beanCoupon.getLogoUrl()).into((ImageView) vi.findViewById(R.id.outlet_icon));

        outletID=beanCoupon.getOutletId();
        List<BeanCategoryDetails> beanCategoryDetails=category.get(outletID);

        if (beanCategoryDetails != null) {
            final LinearLayoutManager layoutManager = new LinearLayoutManager(vi.getContext());
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            RecyclerView recyclerView=(RecyclerView)vi.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(layoutManager);
            MyAdapter mAdapter = new MyAdapter(beanCategoryDetails);
            recyclerView.setAdapter(mAdapter);
        }
        return vi;
	};
}

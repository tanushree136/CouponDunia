package com.example.coupondunia;

public class BeanCouponDetails {
	private String outlet_id;
	private String outlet_name;
	private String num_coupons;
	private String logo_url;
	private String neighbourhood;
	private Double latitude;
	private Double longitude;
	private Double distance;
	
	public BeanCouponDetails(){
	}
	 
	public BeanCouponDetails(String outlet_id,String outlet_name, String num_coupons, String logo_url,String neighbourhood, Double latitude, Double longitude){
	    this.outlet_id=outlet_id;
	    this.outlet_name = outlet_name;
	    this.num_coupons = num_coupons;
	    this.logo_url = logo_url;
	    this.neighbourhood = neighbourhood;
	    this.latitude=latitude;
	    this.longitude=longitude;
	}
	 
	public String getOutletName() {
	    return outlet_name;
	}
	
	public void setOutletName(String outlet_name) {
	    this.outlet_name = outlet_name;
	}
	
	public String getNumCoupons() {
	    return num_coupons;
	}
	
	public void setNumCoupons(String num_coupons) {
	    this.num_coupons = num_coupons;
	}
	
	public String getLogoUrl() {
	    return logo_url;
	}
	
	public void setLogoUrl(String logo_url) {
	    this.logo_url = logo_url;
	}

        public String getNeighbourhood() {
            return neighbourhood;
        }

        public void setNeighbourhood(String neighbourhood) {
            this.neighbourhood = neighbourhood;
        }
	
	public String getOutletId() {
	    return outlet_id;
	}
	
	public void setOutletId(String outlet_id) {
	    this.outlet_id = outlet_id;
	}
	
	public Double getLatitude() {
	    return latitude;
	}
	
	public void setLatitude(Double latitude) {
	    this.latitude = latitude;
	}

	public Double getDistance() {
	    return distance;
	}
	public void setDistance(Double distance) {
	    this.distance = distance;
	}

	public Double getLongitude() {
	    return longitude;
	}
	
	public void setLongitude(Double longitude) {
	    this.longitude = longitude;
	}
}

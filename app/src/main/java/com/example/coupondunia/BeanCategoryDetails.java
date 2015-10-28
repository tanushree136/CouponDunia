package com.example.coupondunia;

public class BeanCategoryDetails {
	
	private String category;
	private String outlet_id;
	
	public BeanCategoryDetails(){
	}
	
	public BeanCategoryDetails(String category){
	
		this.category=category;
	}
	
	public String getCategory() {
	    return category;
	}
	
	public void setCategory(String category) {
	    this.category = category;
	}
	
	public String getOutletId() {
	    return outlet_id;
	}
	
	public void setOutletId(String outlet_id) {
	    this.outlet_id = outlet_id;
	}
}

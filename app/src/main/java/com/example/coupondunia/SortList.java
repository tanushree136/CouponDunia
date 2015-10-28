package com.example.coupondunia;

public class SortList implements java.util.Comparator<BeanCouponDetails> {
    @Override
    public int compare(BeanCouponDetails r1, BeanCouponDetails r2) {
        if (r1.getDistance() != null && r2.getDistance() != null) {
            return (r1.getDistance() > r2.getDistance()) ? 1 : -1;
        }
        return 0;
    }
}

package com.airtel.agile.test;

import java.util.ArrayList;
import java.util.List;

import com.airtel.agile.model.LatLng;
import com.airtel.agile.utility.LatLonUtils;

public class Test {
	
	private static List<LatLng> makeList(double... coords) {
        int size = coords.length / 2;
        ArrayList<LatLng> list = new ArrayList<LatLng>(size);
        for (int i = 0; i < size; ++i) {
            list.add(new LatLng(coords[i + i], coords[i + i + 1]));
        }
        return list;
    }

	public static void main(String args[]) {
		final double small = 5e-3;  // About 5cm on equator, half the default tolerance.
		long time1 = System.currentTimeMillis();
		if(LatLonUtils.isLocationOnPath(new LatLng(0.024, 90.0007), makeList(0.34, 90-small, 0.34, 90, 0, 90+small), false, 10000)){
			System.out.println("within deviation");
		}else{
			System.out.println("outside deviation");
		}
		long time2 = System.currentTimeMillis();
		System.out.println("time taken to run the test : " + (time2 - time1));
		long time3 = System.currentTimeMillis();
		if(LatLonUtils.isLocationOnPath(new LatLng(0.024, 90.007), makeList(0.11, 90-2*small,0.12, 90-small, 0.15, 90, 0.19, 90+small, 0.11, 90+2*small), false, 1000)){
			System.out.println("within deviation");
		}else{
			System.out.println("outside deviation");
		}
		long time4 = System.currentTimeMillis();
		System.out.println("time taken to run the test : " + (time4 - time3));
		
		if(LatLonUtils.isLocationOnPath(new LatLng(28.479559, 77.086754), makeList(28.479600, 77.079877,28.477100, 77.082040,28.479564, 77.083506), false, 400)){
			System.out.println("within deviation");
		}else{
			System.out.println("outside deviation");
		}
		
		if(LatLonUtils.isLocationOnPath(new LatLng(28.481205, 77.085954), makeList(28.479600, 77.079877,28.477100, 77.082040,28.479564, 77.083506), true, 300)){
			System.out.println("within deviation");
		}else{
			System.out.println("outside deviation");
		}
		
		if(LatLonUtils.isLocationOnPath(new LatLng(28.490645, 77.08346), makeList(28.490995, 77.078436,28.490745, 77.079743,28.490079, 77.079091), true,300)){
			System.out.println("within deviation");
		}else{
			System.out.println("outside deviation");
		}
		
//		long time5 = System.currentTimeMillis();
//		if(LatLonUtils.isLocationOnPath(new LatLng(), makeList(), false, 1000)){
//			System.out.println("within deviation");
//		}else{
//			System.out.println("outside deviation");
//		}
//		long time6 = System.currentTimeMillis();
//		System.out.println("time taken to run the test : " + (time6 - time5));
	}
}

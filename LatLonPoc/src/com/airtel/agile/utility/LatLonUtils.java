package com.airtel.agile.utility;

import java.util.List;

import com.airtel.agile.model.LatLng;
import static java.lang.Math.*;

public class LatLonUtils {

	static final double EARTH_RADIUS = 6371009;

	public static boolean isLocationOnPath(LatLng point, List<LatLng> polyline, boolean geodesic, double tolerance) {
		return isLocationOnEdgeOrPath(point, polyline, false, geodesic, tolerance);
	}

	private static boolean isLocationOnEdgeOrPath(LatLng point, List<LatLng> poly, boolean closed, boolean geodesic,
			double toleranceEarth) {
		int idx = locationIndexOnEdgeOrPath(point, poly, closed, geodesic, toleranceEarth);

		return (idx >= 0);
	}

	static double clamp(double x, double low, double high) {
		return x < low ? low : (x > high ? high : x);
	}

	private static int locationIndexOnEdgeOrPath(LatLng point, List<LatLng> poly, boolean closed, boolean geodesic,
			double toleranceEarth) {
		int size = poly.size();
		if (size == 0) {
			return -1;
		}
		double tolerance = toleranceEarth / EARTH_RADIUS;
		double havTolerance = hav(tolerance);
		double lat3 = toRadians(point.getLatitude());
		double lng3 = toRadians(point.getLongitude());
		LatLng prev = poly.get(closed ? size - 1 : 0);
		double lat1 = toRadians(prev.getLatitude());
		double lng1 = toRadians(prev.getLongitude());
		int idx = 0;
		// We project the points to mercator space, where the Rhumb segment is a
		// straight line,
		// and compute the geodesic distance between point3 and the closest
		// point on the
		// segment. This method is an approximation, because it uses "closest"
		// in mercator
		// space which is not "closest" on the sphere -- but the error is small
		// because
		// "tolerance" is small.
		double minAcceptable = lat3 - tolerance;
		double maxAcceptable = lat3 + tolerance;
		double y1 = mercator(lat1);
		double y3 = mercator(lat3);
		double[] xTry = new double[3];
		for (LatLng point2 : poly) {
			double lat2 = toRadians(point2.getLatitude());
			double y2 = mercator(lat2);
			double lng2 = toRadians(point2.getLongitude());
			if (max(lat1, lat2) >= minAcceptable && min(lat1, lat2) <= maxAcceptable) {
				// We offset longitudes by -lng1; the implicit x1 is 0.
				double x2 = wrap(lng2 - lng1, -PI, PI);
				double x3Base = wrap(lng3 - lng1, -PI, PI);
				xTry[0] = x3Base;
				// Also explore wrapping of x3Base around the world in both
				// directions.
				xTry[1] = x3Base + 2 * PI;
				xTry[2] = x3Base - 2 * PI;
				for (double x3 : xTry) {
					double dy = y2 - y1;
					double len2 = x2 * x2 + dy * dy;
					double t = len2 <= 0 ? 0 : clamp((x3 * x2 + (y3 - y1) * dy) / len2, 0, 1);
					double xClosest = t * x2;
					double yClosest = y1 + t * dy;
					double latClosest = inverseMercator(yClosest);
					double havDist = havDistance(lat3, latClosest, x3 - xClosest);
					if (havDist < havTolerance) {
						return Math.max(0, idx - 1);
					}
				}
			}
			lat1 = lat2;
			lng1 = lng2;
			y1 = y2;
			idx++;
		}
		return -1;
	}
	
	 /**
     * Returns haversine(angle-in-radians).
     * hav(x) == (1 - cos(x)) / 2 == sin(x / 2)^2.
     */

	static double hav(double x) {
		double sinHalf = sin(x * 0.5);
		return sinHalf * sinHalf;
	}

	/**
	 * Returns latitude from mercator Y.
	 */
	static double inverseMercator(double y) {
		return 2 * atan(exp(y)) - PI / 2;
	}

	/**
	 * Returns mercator Y corresponding to latitude. See
	 * http://en.wikipedia.org/wiki/Mercator_projection .
	 */
	static double mercator(double lat) {
		return log(tan(lat * 0.5 + PI / 4));
	}

	static double mod(double x, double m) {
		return ((x % m) + m) % m;
	}

	/**
	 * Wraps the given value into the inclusive-exclusive interval between min
	 * and max.
	 * 
	 * @param n
	 *            The value to wrap.
	 * @param min
	 *            The minimum.
	 * @param max
	 *            The maximum.
	 */
	static double wrap(double n, double min, double max) {
		return (n >= min && n < max) ? n : (mod(n - min, max - min) + min);
	}

	/**
	 * Returns hav() of distance from (lat1, lng1) to (lat2, lng2) on the unit
	 * sphere.
	 */
	static double havDistance(double lat1, double lat2, double dLng) {
		return hav(lat1 - lat2) + hav(dLng) * cos(lat1) * cos(lat2);
	}
}

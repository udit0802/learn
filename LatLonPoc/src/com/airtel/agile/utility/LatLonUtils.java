package com.airtel.agile.utility;

import java.util.List;

import com.airtel.agile.model.LatLng;
import static java.lang.Math.*;

public class LatLonUtils {

	static final double EARTH_RADIUS = 6371009;
	
	private static double sinDeltaBearing(double lat1, double lng1, double lat2, double lng2, double lat3,
			double lng3) {
		double sinLat1 = sin(lat1);
		double cosLat2 = cos(lat2);
		double cosLat3 = cos(lat3);
		double lat31 = lat3 - lat1;
		double lng31 = lng3 - lng1;
		double lat21 = lat2 - lat1;
		double lng21 = lng2 - lng1;
		double a = sin(lng31) * cosLat3;
		double c = sin(lng21) * cosLat2;
		double b = sin(lat31) + 2 * sinLat1 * cosLat3 * hav(lng31);
		double d = sin(lat21) + 2 * sinLat1 * cosLat2 * hav(lng21);
		double denom = (a * a + b * b) * (c * c + d * d);
		return denom <= 0 ? 1 : (a * d - b * c) / sqrt(denom);
	}
	
	// Given h==hav(x), returns sin(abs(x)).
    static double sinFromHav(double h) {
        return 2 * sqrt(h * (1 - h));
    }
    
 // Returns hav(asin(x)).
    static double havFromSin(double x) {
        double x2 = x * x;
        return x2 / (1 + sqrt(1 - x2)) * .5;
    }
    
 // Returns sin(arcHav(x) + arcHav(y)).
    static double sinSumFromHav(double x, double y) {
        double a = sqrt(x * (1 - x));
        double b = sqrt(y * (1 - y));
        return 2 * (a + b - 2 * (a * y + b * x));
    }

	private static boolean isOnSegmentGC(double lat1, double lng1, double lat2, double lng2, double lat3, double lng3,
			double havTolerance) {
		double havDist13 = havDistance(lat1, lat3, lng1 - lng3);
		if (havDist13 <= havTolerance) {
			return true;
		}
		double havDist23 = havDistance(lat2, lat3, lng2 - lng3);
		if (havDist23 <= havTolerance) {
			return true;
		}
		double sinBearing = sinDeltaBearing(lat1, lng1, lat2, lng2, lat3, lng3);
		double sinDist13 = sinFromHav(havDist13);
		double havCrossTrack = havFromSin(sinDist13 * sinBearing);
		if (havCrossTrack > havTolerance) {
			return false;
		}
		double havDist12 = havDistance(lat1, lat2, lng1 - lng2);
		double term = havDist12 + havCrossTrack * (1 - 2 * havDist12);
		if (havDist13 > term || havDist23 > term) {
			return false;
		}
		if (havDist12 < 0.74) {
			return true;
		}
		double cosCrossTrack = 1 - 2 * havCrossTrack;
		double havAlongTrack13 = (havDist13 - havCrossTrack) / cosCrossTrack;
		double havAlongTrack23 = (havDist23 - havCrossTrack) / cosCrossTrack;
		double sinSumAlongTrack = sinSumFromHav(havAlongTrack13, havAlongTrack23);
		return sinSumAlongTrack > 0; // Compare with half-circle == PI using
										// sign of sin().
	}

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
		if (geodesic) {
			for (LatLng point2 : poly) {
				double lat2 = toRadians(point2.getLatitude());
				double lng2 = toRadians(point2.getLongitude());
				if (isOnSegmentGC(lat1, lng1, lat2, lng2, lat3, lng3, havTolerance)) {
					return Math.max(0, idx - 1);
				}
				lat1 = lat2;
				lng1 = lng2;
				idx++;
			}
		} else {
			// We project the points to mercator space, where the Rhumb segment
			// is a
			// straight line,
			// and compute the geodesic distance between point3 and the closest
			// point on the
			// segment. This method is an approximation, because it uses
			// "closest"
			// in mercator
			// space which is not "closest" on the sphere -- but the error is
			// small
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
		}
		return -1;
	}

	/**
	 * Returns haversine(angle-in-radians). hav(x) == (1 - cos(x)) / 2 == sin(x
	 * / 2)^2.
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

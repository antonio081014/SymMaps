package com.antonio081014.symantecmap;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

public class MainMap extends MapActivity implements Comparable<GeoPoint> {

	private MapController mMapController;
	private MapView mMapView;
	private LocationManager mLocationManager;
	private MapOverlays itemizedoverlay;
	private MyLocationOverlay mLocationOverlay;

	private Button mButton1;
	private Button mButton2;

	private ArrayList<GeoPoint> interestPlaceList;
	private ArrayList<GeoPoint> mLocationHistory;
	private GeoPoint currentLocation;

	private long minUpdateTime;
	private float minUpdateDist;

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_main);

		this.interestPlaceList = new ArrayList<GeoPoint>();
		this.mLocationHistory = new ArrayList<GeoPoint>();

		this.minUpdateDist = 0;
		this.minUpdateTime = 1000;

		mMapView = (MapView) findViewById(R.id.mapview);
		mMapView.setBuiltInZoomControls(true);
		// mMapView.setSatellite(true);
		mMapView.setTraffic(true);
		mMapController = mMapView.getController();
		mMapController.setZoom(14);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				this.minUpdateTime, this.minUpdateDist, new GeoUpdateHandler());

		mLocationOverlay = new MyLocationOverlay(this, mMapView);
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(mLocationOverlay);

		mLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mMapView.getController().animateTo(
						mLocationOverlay.getMyLocation());
			}
		});

		Drawable drawable = this.getResources().getDrawable(
				android.R.drawable.star_on);
		itemizedoverlay = new MapOverlays(this, drawable);

		mButton1 = (Button) findViewById(R.id.button1);
		mButton2 = (Button) findViewById(R.id.button2);

		mButton1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addPlace(mLocationOverlay.getMyLocation());
				Toast.makeText(getApplicationContext(),
						"Interest Place Added.", Toast.LENGTH_LONG).show();
			}
		});
		mButton2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				generateAllMarker();
			}
		});
	}

	private void addPlace(GeoPoint p) {
		this.currentLocation = p;
		if (!this.interestPlaceList.contains(p))
			this.interestPlaceList.add(p);
	}

	private void addHistory(GeoPoint p) {
		if (!this.mLocationHistory.contains(p))
			this.mLocationHistory.add(p);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public class GeoUpdateHandler implements LocationListener {
		@Override
		public void onLocationChanged(Location location) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			mMapController.animateTo(point);
			addHistory(point);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}

	private void generateAllMarker() {
		mMapView.getOverlays().remove(itemizedoverlay);
		itemizedoverlay.deleteOverlayItem();
		for (GeoPoint p : this.mLocationHistory) {
			OverlayItem overlayitem = new OverlayItem(p, "Spot", String.format(
					"Lat:%.6f\nLog:%.6f", (double) p.getLatitudeE6() / 1000000,
					(double) p.getLongitudeE6() / 1000000));
			itemizedoverlay.addOverlayItem(overlayitem);
		}
		if (itemizedoverlay.size() > 0) {
			mMapView.getOverlays().add(itemizedoverlay);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationOverlay.enableMyLocation();
		mLocationOverlay.enableCompass();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mLocationOverlay.disableMyLocation();
		mLocationOverlay.disableCompass();
	}

	@Override
	public int compareTo(GeoPoint another) {
		return this.currentLocation.getLatitudeE6() - another.getLatitudeE6()
				+ this.currentLocation.getLongitudeE6()
				- another.getLongitudeE6();
	}
}
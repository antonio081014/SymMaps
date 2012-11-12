/**
 * 
 */
package com.antonio081014.symantecmap;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * @author antonio081014
 * 
 */
public class MapOverlays extends ItemizedOverlay<OverlayItem> {

	private Context mContext;
	private ArrayList<OverlayItem> mOverlayItemList;

	public MapOverlays(Context context, Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		this.mContext = context;
		this.mOverlayItemList = fetchList();
	}

	@Override
	protected OverlayItem createItem(int index) {
		return this.mOverlayItemList.get(index);
	}

	@Override
	public int size() {
		return this.mOverlayItemList.size();
	}

	private ArrayList<OverlayItem> fetchList() {
		return new ArrayList<OverlayItem>();
	}

	protected void addOverlayItem(OverlayItem overlayItem) {
		this.mOverlayItemList.add(overlayItem);
		populate();
	}

	protected void deleteOverlayItem() {
		this.mOverlayItemList.clear();
	}

	protected boolean onTap(int index) {
		OverlayItem overlayItem = this.mOverlayItemList.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle(overlayItem.getTitle());
		dialog.setMessage(overlayItem.getSnippet());
		dialog.show();
		return true;
	};
}

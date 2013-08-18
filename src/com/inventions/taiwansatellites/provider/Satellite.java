package com.inventions.taiwansatellites.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class Satellite implements BaseColumns {
	public static final String SATELLITE_URL = "url";
	public static final String SATELLITE_LOCAL = "local";
	public static final String TYPE = "type";
	public static final String TIME = "time";

	public static final String DEFAULT_SORT_ORDER = TIME + " desc";
	public static final String TABLE = "satellite";
	public static final Uri CONTENT_URI = Uri.parse("content://" + StelliteProvider.AUTHORITY
			+ "/" + TABLE);
}

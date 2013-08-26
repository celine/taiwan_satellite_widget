package com.inventions.taiwansatellites;

import android.net.Uri;

public interface Constants {

	String LOAD_SATELLITE = "com.inventions.taiwansatellites.load_satellite";
	String SATELLITE_HQ_FORMAT = "http://www.cwb.gov.tw/V7/observe/satellite/Data/HS1P/HS1P-%s-%s-%s-%s-%s.jpg";
	String SATELLITE_BW_FORMAT = "http://www.cwb.gov.tw/V7/observe/satellite/Data/HS1O/HS1O-%s-%s-%s-%s-%s.jpg";
	String SATELLITE_VIS_FORMAT = "";
	String SATELLITE_ENHANVE = "http://www.cwb.gov.tw/V7/observe/satellite/Data/HS1Q/HS1Q-%s-%s-%s-%s-%s.jpg";
	String EXTRA_SORTED_ARRAY = "sorted_array";
	String EXTRA_PATH_MAP = "path_map";
	String WIDGET_ID = "widget_id";
	String LOAD_FINISH = "com.inventions.taiwansatellites.LOAD_FINISH";
	String UPDATE_WIDGET = "com.inventions.taiwansatellites.UPDATE_WIDGET";
	int MAX_STORE_SIZE = 15;
	int IMAG_SIZE_TO_PLAY = 3;
	Uri notifyUri = Uri.parse("com.inventions.taiwansatellitewidget.UPDATE");
}

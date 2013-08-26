package com.inventions.taiwansatellites.remote;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.inventions.taiwansatellites.Constants;
import com.inventions.taiwansatellites.LoadSatellite;
import com.inventions.taiwansatellites.SatelliteData;
import com.inventions.taiwansatellites.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

public class SatelliteService extends IntentService implements Constants {
	public SatelliteService() {
		super("SatelliteService");
		// TODO Auto-generated constructor stub
	}

	File mParent;
	Map<Long, String> pathMap;
	List<Long> sortTimeList;
	long latestTime = -1;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		SatelliteData data = new SatelliteData(getBaseContext());
		if (pathMap == null || pathMap.isEmpty()) {
			data.readData();
			pathMap = data.getPathMap();
			sortTimeList = data.getSortTimeList();
			if (latestTime < 0) {
				latestTime = data.getLatestTime();
			}
		}

		Utils.loadDataAndStore(getApplicationContext(), data);

	}
}

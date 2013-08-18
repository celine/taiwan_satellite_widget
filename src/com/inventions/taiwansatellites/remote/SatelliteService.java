package com.inventions.taiwansatellites.remote;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.inventions.taiwansatellites.Constants;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SatelliteService extends IntentService implements Constants {

	static final String LOG_TAG = "SatelliteService";

	public SatelliteService() {
		super(LOG_TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (LOAD_SATELLITE.equals(intent.getAction())) {
			try {
				Document doc = Jsoup
						.connect(
								"http://www.cwb.gov.tw/V7/observe/satellite/Sat_EA.htm")
						.get();
				Elements img = doc.select("#im");
				String src = img.attr("src");
				Log.d(LOG_TAG,"img " + img);
				Log.d(LOG_TAG, "src " + src);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}

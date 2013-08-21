package com.inventions.taiwansatellites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class SatelliteWidget extends AppWidgetProvider {
	private static final String LOG_TAG = "SatelliteWidget";
	ComponentName thisWidget;
	Map<Long, String> pathMap;
	List<Long> sortTimeList;
	private static final int IMAG_SIZE_TO_PLAY = 2;
	private static final long TIME_PERIOD = 10000;
	AppWidgetManager mManager;
	int mIndex;
	RemoteViews mViews;
	String mCurrentUrl;

	@Override
	public void onUpdate(final Context context,
			final AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		mViews = new RemoteViews(context.getPackageName(),
				R.layout.satellite_img);
		thisWidget = new ComponentName(context, SatelliteWidget.class);
		mManager = appWidgetManager;
	}

	public void reloadTask(final Context context) {
		setInProgress(true);
		mManager.updateAppWidget(thisWidget, mViews);
		new Thread() {
			public void run() {
				LoadSatellite loader = new LoadSatellite(context);
				pathMap = loader.getAllSatellites();
				if (pathMap != null) {
					Set<Long> timeSet = pathMap.keySet();
					sortTimeList = new ArrayList<Long>(timeSet.size());
					Collections.sort(sortTimeList);
					if (sortTimeList != null) {
						mIndex = 0;
						Timer timer = new Timer();
						timer.schedule(new UpdateTask(context), 0, TIME_PERIOD);

					}
				}
				// List<String> files = loader.loadSatellitesPath();
				// for (String file : files) {
				// Log.d(LOG_TAG, "file " + file);
				// }
				// Timer timer = new Timer();
				// timer.scheduleAtFixedRate(new UpdateTask(context,
				// appWidgetManager, files), 1, 1000);
			}
		}.start();
	}

	public static void resetToNow() {

	}

	public class UpdateTask extends TimerTask {
		long mInitialTime;
		Context mContext;

		public UpdateTask(Context context) {

			thisWidget = new ComponentName(context, SatelliteWidget.class);
			mInitialTime = System.currentTimeMillis();
			mContext = context;
		}

		@Override
		public void run() {
			if (mIndex > pathMap.size()) {
				mIndex = 0;
			}
			mCurrentUrl = pathMap.get(sortTimeList.get(mIndex));
			setImage(mContext, mCurrentUrl);
			mIndex++;
		}
	}

	public void setInProgress(boolean progress) {
		mViews.setViewVisibility(R.id.img, progress ? View.GONE : View.VISIBLE);
		mViews.setViewVisibility(R.id.progress, progress ? View.VISIBLE
				: View.GONE);
	}

	public void setImage(final Context context, final String url) {
		Target target = new Target() {

			@Override
			public void onSuccess(Bitmap bitmap) {
				if (url.equals(mCurrentUrl)) {
					mViews.setImageViewBitmap(R.id.img, bitmap);
					setInProgress(false);
					mManager.updateAppWidget(thisWidget, mViews);
				}
			}

			@Override
			public void onError() {
				// TODO Auto-generated method stub

			}

		};
		Picasso.with(context).load(url).into(target);
	}
}

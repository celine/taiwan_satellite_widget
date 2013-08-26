package com.inventions.taiwansatellites;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.inventions.taiwansatellites.remote.SatelliteService;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class SatelliteWidget extends AppWidgetProvider implements Constants {
	private static final String LOG_TAG = "SatelliteWidget";
	ComponentName thisWidget;
	SatelliteData mData;
	private static final long TIME_PERIOD = 5000;
	private static final long FIRST_DELAY_TIME = 5000;
	AppWidgetManager mManager;
	LruCache mCache;
	static File mParent;
	static final String VIEW_NOW = "com.inventions.taiwansatellites.VIEW_NOW";
	static Map<Integer, Timer> timerMap = new HashMap<Integer, Timer>();
	Handler worker;

	@Override
	public void onDisabled(Context context) {
		Log.d(LOG_TAG, "onDisabled");
		super.onDisabled(context);
		if (worker != null) {
			worker.getLooper().quit();
			worker = null;
		}
		for (int id : timerMap.keySet()) {
			Timer timer = timerMap.get(id);
			timer.cancel();
		}
	}

	@Override
	public void onEnabled(Context context) {
		Log.d(LOG_TAG, "onEnabled");
		super.onEnabled(context);
	}

	public RemoteViews bindRemoteViews(Context context, int widgetId) {
		RemoteViews views = new RemoteViews(context.getPackageName(),
				R.layout.satellite_img);

		return views;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG,
				"onReceive " + intent.getAction() + " " + this.toString());

		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(final Context context,
			final AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		super.onUpdate(context, appWidgetManager, appWidgetIds);
		if (mCache == null) {
			mCache = new LruCache(8000000);
		}
		if (mParent == null) {
			mParent = context.getCacheDir();
		}
		if (thisWidget == null) {
			thisWidget = new ComponentName(context, SatelliteWidget.class);
		}
		if (worker == null) {
			HandlerThread thread = new HandlerThread("update");
			thread.start();
			worker = new Handler(thread.getLooper());
		}

		mManager = appWidgetManager;
		Log.d(LOG_TAG, "onUpdate " + appWidgetIds.length);
		mData = new SatelliteData(context);
		for (int i = 0; i < appWidgetIds.length; i++) {
			int widgetId = appWidgetIds[i];
			RemoteViews views = bindRemoteViews(context, widgetId);
			appWidgetManager.updateAppWidget(widgetId, views);
			setInProgress(views, context, true);

			resetToNowAndPlay(context, widgetId, views, mData);
		}

	}

	public void resetToNowAndPlay(Context context, int widgetId,
			RemoteViews views, SatelliteData data) {
		data.readData();

		Timer timer = timerMap.get(widgetId);
		if (timer == null) {
			timer = new Timer();
			timerMap.put(widgetId, timer);
		}

		long latestTime = data.getLatestTime();
		Map<Long, String> pathMap = data.getPathMap();
		if (pathMap != null && !pathMap.isEmpty()) {
			String url = pathMap.get(latestTime);
			setImage(context, views, mCache, mManager, url, widgetId);
			timer.schedule(new UpdateTask(context, widgetId, views, mData,
					mCache), FIRST_DELAY_TIME, TIME_PERIOD);

		}
	}

	public class UpdateTask extends TimerTask {
		Context mContext;
		int mIndex = 0;
		int mWidgetId;
		RemoteViews mViews;
		List<String> urlToDiaplay;
		SatelliteData mData;
		int diaplaySize = 0;
		LruCache mCache;

		public UpdateTask(Context context, int widgetId, RemoteViews views,
				SatelliteData data, LruCache cache) {
			mIndex = 0;
			mContext = context;
			mWidgetId = widgetId;
			mViews = views;
			mData = data;
			updateData();
			mCache = cache;
		}

		public void updateData() {
			diaplaySize = mData.getDisplaySize();
			urlToDiaplay = new ArrayList<String>(diaplaySize);
			Map<Long, String> pathMap = mData.getPathMap();
			List<Long> sortTimeList = mData.getSortTimeList();
			Log.d(LOG_TAG,"sortTimeList " + sortTimeList);
			for (int i = MAX_STORE_SIZE - diaplaySize; i < MAX_STORE_SIZE; i++) {
				long key = sortTimeList.get(i);
				Log.d(LOG_TAG,"key " + key);
				urlToDiaplay.add(pathMap.get(key));
			}
		}

		@Override
		public void run() {

			if (mIndex >= diaplaySize) {
				mIndex = 0;
				mData.readData();
				updateData();
			}
			Log.d(LOG_TAG,"index " + mIndex);
			String url = urlToDiaplay.get(mIndex);
			setImage(mContext, mViews, mCache, mManager, url, mWidgetId);
			mIndex++;
		}

	}

	public static void setImage(final Context context, final RemoteViews views,
			final LruCache cache, final AppWidgetManager manager,
			final String url, final int widgetId) {
		Log.d(LOG_TAG, "url " + url);
		final File file = new File(mParent, "img_" + url.hashCode());
		Target target = new Target() {

			@Override
			public void onSuccess(Bitmap bitmap) {
				cache.set(url, bitmap);
				Log.d(LOG_TAG, "url " + url + " size " + bitmap.getRowBytes());

				try {
					FileOutputStream out = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.PNG, 40, out);
					Log.d(LOG_TAG, "onSuccess " + bitmap.getRowBytes());
					views.setImageViewBitmap(R.id.img, bitmap);
					setInProgress(views, context, false);
					manager.updateAppWidget(widgetId, views);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					Log.e(LOG_TAG, "error", e);
				}

			}

			@Override
			public void onError() {
				Log.d(LOG_TAG, "onError");
			}

		};
		Bitmap bitmap = cache.get(url);
		if (bitmap == null && file.exists()) {
			bitmap = BitmapFactory.decodeFile(file.getPath());
		}
		if (bitmap != null) {
			target.onSuccess(bitmap);
		} else {
			Picasso.with(context).load(url).into(target);
		}
	}

	public static void setInProgress(RemoteViews views, Context context,
			boolean progress) {
		views.setViewVisibility(R.id.progress, progress ? View.VISIBLE
				: View.GONE);
	}

}

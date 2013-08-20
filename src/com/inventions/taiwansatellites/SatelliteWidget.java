package com.inventions.taiwansatellites;

import java.util.List;
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
import android.widget.RemoteViews;

public class SatelliteWidget extends AppWidgetProvider {
	private static final String LOG_TAG = "SatelliteWidget";

	@Override
	public void onUpdate(final Context context, final AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		new Thread() {
			public void run() {
				LoadSatellite loader = new LoadSatellite(context);
				List<String> files = loader.loadSatellitesPath();
				for(String file:files){
					Log.d(LOG_TAG,"file " + file);
				}
				Timer timer = new Timer();
				timer.scheduleAtFixedRate(new UpdateTask(context,
						appWidgetManager, files), 1, 1000);
			}
		}.start();

	}

	public static class UpdateTask extends TimerTask {
		RemoteViews mView;
		AppWidgetManager mManager;
		Context mContext;
		int mIndex = 0;
		List<String> filePath;
		ComponentName thisWidget;

		public UpdateTask(Context context, AppWidgetManager manager,
				List<String> files) {
			mManager = manager;
			mContext = context;
			mView = new RemoteViews(context.getPackageName(),
					R.layout.satellite_img);
			filePath = files;
			mIndex = 0;
			thisWidget = new ComponentName(context, SatelliteWidget.class);
		}

		@Override
		public void run() {
			Log.d(LOG_TAG, "run " + mIndex);
			Target target = new Target() {

				@Override
				public void onSuccess(Bitmap bitmap) {
					mView.setImageViewBitmap(R.id.img, bitmap);
					mManager.updateAppWidget(thisWidget, mView);
					mIndex++;
				}

				@Override
				public void onError() {
					// TODO Auto-generated method stub

				}

			};
			Picasso.with(mContext).load(filePath.get(mIndex)).into(target);

		}
	}
}

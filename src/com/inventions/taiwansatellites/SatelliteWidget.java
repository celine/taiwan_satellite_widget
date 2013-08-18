package com.inventions.taiwansatellites;

import java.util.Timer;
import java.util.TimerTask;

import com.squareup.picasso.Picasso;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class SatelliteWidget extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		LoadSatellite loader = new LoadSatellite(context);
		loader.loadSatellites();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new UpdateTask(context, appWidgetManager), 1,
				1000);
	}

	public static class UpdateTask extends TimerTask {
		RemoteViews mView;
		AppWidgetManager mManager;
		Context mContext;

		public UpdateTask(Context context, AppWidgetManager manager) {
			mManager = manager;
			mContext = context;
			mView = new RemoteViews(context.getPackageName(),
					R.layout.satellite_img);
		}

		@Override
		public void run() {
			File file = new File();
			Picasso.with(mContext).load(file).into();
			mView.setImageViewBitmap(R.id.img, bitmap);

		}
	}
}

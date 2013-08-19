package com.inventions.taiwansatellites;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

public class SatelliteWidget extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		LoadSatellite loader = new LoadSatellite(context);
		List<String> files = loader.loadSatellitesPath();
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new UpdateTask(context, appWidgetManager,
				files), 1, 1000);
	}

	public static class UpdateTask extends TimerTask {
		RemoteViews mView;
		AppWidgetManager mManager;
		Context mContext;
		int mIndex = 0;
		List<String> filePath;

		public UpdateTask(Context context, AppWidgetManager manager,
				List<String> files) {
			mManager = manager;
			mContext = context;
			mView = new RemoteViews(context.getPackageName(),
					R.layout.satellite_img);
			filePath = files;
			mIndex = 0;
		}

		@Override
		public void run() {
			Target target = new Target() {

				@Override
				public void onSuccess(Bitmap bitmap) {
					mView.setImageViewBitmap(R.id.img, bitmap);

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

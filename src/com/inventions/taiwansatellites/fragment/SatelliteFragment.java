package com.inventions.taiwansatellites.fragment;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.Map;

import com.inventions.taiwansatellites.Constants;
import com.inventions.taiwansatellites.LoadSatellite;
import com.inventions.taiwansatellites.R;
import com.inventions.taiwansatellites.SatelliteData;
import com.inventions.taiwansatellites.Utils;
import com.squareup.picasso.Loader.Response;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestBuilder;
import com.squareup.picasso.UrlConnectionLoader;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.ViewFlipper;

public class SatelliteFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<SatelliteData>, Constants {
	static final String PREFIX = "http://www.cwb.gov.tw";
	ImageView mImgView;
	private static final String LOG_TAG = "SatelliteFragment";
	Button mCreate;
	int mAppWidgetId;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(LOG_TAG, "onActivityCreated");
		getLoaderManager().initLoader(0, null, this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.satellite_fragment, container,
				false);
		Activity main = getActivity();
		if (main != null) {
			Intent intent = main.getIntent();
			Bundle extras = intent.getExtras();
			if (extras != null) {
				mAppWidgetId = extras.getInt(
						AppWidgetManager.EXTRA_APPWIDGET_ID,
						AppWidgetManager.INVALID_APPWIDGET_ID);
			}
			AppWidgetManager appWidgetManager = AppWidgetManager
					.getInstance(main.getApplicationContext());
			RemoteViews views = new RemoteViews(main.getPackageName(),
					R.layout.satellite_img);
			appWidgetManager.updateAppWidget(mAppWidgetId, views);
		}
		mCreate = (Button) view.findViewById(R.id.start);
		mCreate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						mAppWidgetId);
				Activity activity = getActivity();
				if (activity != null) {
					activity.setResult(Activity.RESULT_OK, resultValue);
					activity.finish();
				}

			}
		});
		mCreate.setEnabled(false);
		mImgView = (ImageView) view.findViewById(R.id.img);
		return view;
	}

	public static class SatelliteLoader extends AsyncTaskLoader<SatelliteData> {
		Context mContext;
		SatelliteData mResults = null;

		public SatelliteLoader(Context context) {
			super(context);
			mContext = context;
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onStartLoading() {
			if (takeContentChanged() || mResults == null) {
				forceLoad();
			} else {
				deliverResult(mResults);
			}
		}

		@Override
		public SatelliteData loadInBackground() {
			SatelliteData data = new SatelliteData(mContext);
			data.readData();
			Utils.loadDataAndStore(mContext, data);
			return data;
		}

	}

	static int BUFFSIZE = 125;

	private static String readStream(InputStream in) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		byte data[] = new byte[BUFFSIZE];
		int byteRead = 0;
		try {
			while ((byteRead = in.read(data)) > 0) {
				out.write(data);
			}
			byte result[] = out.toByteArray();

			return new String(result);
		} catch (IOException e) {
			Log.e(LOG_TAG, "error", e);
		}
		return null;
	}

	@Override
	public Loader<SatelliteData> onCreateLoader(int arg0, Bundle arg1) {
		return new SatelliteLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<SatelliteData> arg0, SatelliteData data) {
		long latest = data.getLatestTime();
		Map<Long, String>pathMap = data.getPathMap();
		if(pathMap != null){
			String url = pathMap.get(latest);
			Picasso.with(getActivity()).load(url).into(mImgView);
		}
		mCreate.setEnabled(true);
	}

	@Override
	public void onLoaderReset(Loader<SatelliteData> arg0) {
		// mResults = null;

	}

}

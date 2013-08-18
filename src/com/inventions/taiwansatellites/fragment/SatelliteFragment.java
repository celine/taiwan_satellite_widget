package com.inventions.taiwansatellites.fragment;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

import com.inventions.taiwansatellites.R;
import com.squareup.picasso.Loader.Response;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestBuilder;
import com.squareup.picasso.UrlConnectionLoader;

import android.content.Context;
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
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class SatelliteFragment extends Fragment implements
		LoaderManager.LoaderCallbacks<String[]> {
	static final String satellites = "http://www.cwb.gov.tw/V7/js/HS1P.js";
	static final String PREFIX = "http://www.cwb.gov.tw";
	ImageView mImgView;
	private static final String LOG_TAG = "SatelliteFragment";

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
		mImgView = (ImageView) view.findViewById(R.id.img);
		return view;
	}

	public static class SatelliteLoader extends AsyncTaskLoader<String[]> {
		Context mContext;
		String mResults[] = null;

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
		public String[] loadInBackground() {
			try {
				URLConnection conn = new URI(satellites).toURL()
						.openConnection();
				conn.connect();
				InputStream in = new BufferedInputStream(conn.getInputStream());
				try {
					String result = readStream(in);
					Log.d(LOG_TAG, "result " + result);
					result = result.substring(result.indexOf('{') + 1,
							result.indexOf('}') - 1);
					String output[] = result.split(",");
					return output;
				} finally {
					in.close();
				}

			} catch (IOException e) {
				Log.e(LOG_TAG, "error", e);
			} catch (URISyntaxException e) {
				Log.e(LOG_TAG, "error", e);
			}
			return null;
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
	public Loader<String[]> onCreateLoader(int arg0, Bundle arg1) {
		return new SatelliteLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<String[]> arg0, String[] pics) {
		Uri uris[] = new Uri[pics.length];
		int i = 0;
		for (String pic : pics) {
			Log.d(LOG_TAG, "pic " + pic);
			String uri = PREFIX
					+ pic.substring(pic.indexOf('"') + 1, pic.indexOf('"', 2));
			Log.d(LOG_TAG, "uri " + uri);
			uris[i++] = Uri.parse(uri);
		}

		RequestBuilder builder = Picasso.with(getActivity()).load(uris[0]);
		Log.d(LOG_TAG, "uris " + uris[0]);
		builder.into(mImgView);

	}

	@Override
	public void onLoaderReset(Loader<String[]> arg0) {
		// TODO Auto-generated method stub

	}

}

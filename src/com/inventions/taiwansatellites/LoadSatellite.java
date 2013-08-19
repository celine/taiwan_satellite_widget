package com.inventions.taiwansatellites;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.squareup.picasso.Cache;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.Builder;
import com.squareup.picasso.Picasso.Listener;
import com.squareup.picasso.RequestBuilder;
import com.squareup.picasso.Target;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

public class LoadSatellite {
	static final String satellites = "http://www.cwb.gov.tw/V7/js/HS1P.js";
	static final String PREFIX = "http://www.cwb.gov.tw";
	private static final String LOG_TAG = "LoadSatellite";
	Context mContext;
	private final File PARENT_DIRECTORY;

	public LoadSatellite(Context context) {
		mContext = context;
		PARENT_DIRECTORY = new File(context.getCacheDir(), "satellites");
	}

	public List<String> loadSatellitesPath() {
		String pics[] = getAllSatellites();
		List<String> filePath = new ArrayList<String>();
		Uri uris[] = new Uri[pics.length];
		int i = 0;
		try {
			for (String pic : pics) {
				Log.d(LOG_TAG, "pic " + pic);
				String urltime[] = pic.split(":");
				String uri = PREFIX
						+ urltime[0].substring(1, urltime[0].length() - 1);
				Log.d(LOG_TAG, "uri " + uri);
				String time = urltime[1].substring(1, urltime[1].length() - 1);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");
				Date date;
				try {
					date = sdf.parse(time);
					URLConnection conn = new URI(uri).toURL().openConnection();
					conn.connect();
					filePath.add(writeToFile(conn.getInputStream(),
							String.valueOf(date.getTime())));

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				i++;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filePath;
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

	private String writeToFile(InputStream in, String filename)
			throws FileNotFoundException {
		File file = new File(PARENT_DIRECTORY, filename);
		FileOutputStream fos = new FileOutputStream(file);
		byte data[] = new byte[BUFFSIZE];
		int byteRead = 0;
		try {
			while ((byteRead = in.read(data)) > 0) {
				fos.write(data, 0, byteRead);
			}
			fos.flush();

		} catch (IOException e) {
			Log.e(LOG_TAG, "error", e);
		}
		return file.getPath();
	}

	public String[] getAllSatellites() {
		try {
			URLConnection conn = new URI(satellites).toURL().openConnection();
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

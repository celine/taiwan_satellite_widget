/*
 * Copyright (C) 2008 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.inventions.taiwansatellites.provider;

import android.content.ContentProvider;
import android.content.UriMatcher;
import android.content.Context;
import android.content.ContentValues;
import android.content.ContentUris;
import android.content.res.Resources;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.net.Uri;
import android.text.TextUtils;
import android.app.SearchManager;

import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class StelliteProvider extends ContentProvider {
	private static final String LOG_TAG = "StelliteProvider";

	private static final String DATABASE_NAME = "satellites.db";
	private static final int DATABASE_VERSION = 1;

	private static final int SATELLITE_ID = 1;
	private static final int SATELLITE = 2;

	public static final String AUTHORITY = "taiwansatellites";
	private static final UriMatcher URI_MATCHER;
	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(AUTHORITY, Satellite.TABLE, SATELLITE);
		URI_MATCHER.addURI(AUTHORITY, Satellite.TABLE + "/#", SATELLITE_ID);
	}

	private SQLiteOpenHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		switch (URI_MATCHER.match(uri)) {
		case SATELLITE:
			qb.setTables(Satellite.TABLE);
			break;
		case SATELLITE_ID:
			qb.setTables(Satellite.TABLE);
			qb.appendWhere("_id=" + uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = Satellite.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), uri);

		return c;
	}

	public String getType(Uri uri) {
		switch (URI_MATCHER.match(uri)) {
		case SATELLITE:
			return "vnd.android.cursor.dir/vnd.org.curiouscreature.provider.satellite";
		case SATELLITE_ID:
			return "vnd.android.cursor.item/vnd.org.curiouscreature.provider.satellite";
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	public Uri insert(Uri uri, ContentValues initialValues) {
		ContentValues values;

		if (initialValues != null) {
			values = initialValues;
		} else {
			values = new ContentValues();
		}

		if (URI_MATCHER.match(uri) != SATELLITE) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		final long rowId = db.insert(Satellite.TABLE, Satellite.SATELLITE_URL, values);
		if (rowId > 0) {
			Uri insertUri = ContentUris.withAppendedId(Satellite.CONTENT_URI,
					rowId);
			getContext().getContentResolver().notifyChange(uri, null);
			return insertUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();

		int count;
		switch (URI_MATCHER.match(uri)) {
		case SATELLITE:
			count = db.delete("books", selection, selectionArgs);
			break;
		case SATELLITE_ID:
			String segment = uri.getPathSegments().get(1);
			count = db.delete("books", Satellite._ID
					+ "="
					+ segment
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ')' : ""), selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);

		return count;
	}

	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE books (" + Satellite._ID
					+ " INTEGER PRIMARY KEY, " + Satellite.SATELLITE_URL + " TEXT, "
					+ Satellite.SATELLITE_LOCAL + " TEXT, " + Satellite.TIME
					+ " TEXT, " + Satellite.TYPE + " SHORT);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(LOG_TAG, "Upgrading database from version " + oldVersion
					+ " to " + newVersion + ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS books");
			onCreate(db);
		}
	}
}

package com.inventions.taiwansatellites.fragment;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

public class URLLoader {
	public void load(String url) throws MalformedURLException, IOException,
			URISyntaxException {
		URLConnection conn = new URI(url).toURL().openConnection();
	}
}

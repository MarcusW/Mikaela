package test;

import java.net.HttpURLConnection;
import java.net.URL;

import photonCollector.TransformationHelper;

public class TransformationHelperForTesting extends TransformationHelper
{
	public static void deleteAllPhotos()
	{
		for (int i = 1; i <= 48; i++)
		{
			transmitBytes(i);
		}
	}
	
	protected static HttpURLConnection transmitBytes(int id)
	{
		HttpURLConnection httpCon = null;
		try
		{
			URL url = new URL(getWebserviceUrl() + "?id=" + id);
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestProperty(
				    "Content-Type", "application/x-www-form-urlencoded" );
			httpCon.setRequestMethod("DELETE");
			int code = httpCon.getResponseCode();
			if(code != 200)
				System.err.println("Konnte Bild mit ID" + id + " nicht entfernen!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return httpCon;
	}
}

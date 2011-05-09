package photonCollector;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.drew.metadata.Metadata;
import com.drew.metadata.exif.*;

public class TransformationHelper
{
	public static String tmpFilePath;
	public static String webServiceUrl;

	public static long dateToUnixTimestamp(String time)
	{
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");
			Date date = dateFormat.parse(time);
			return date.getTime();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return 0;
	}

	public static int uploadImage(String url)
	{
		try
		{
			URL imageUrl = new URL(url);
			BufferedInputStream bufIn = new BufferedInputStream(imageUrl.openStream());

			int dotPos = imageUrl.getPath().lastIndexOf(".");
			if (dotPos < 0)
				return -1; // TODO: Fehler fuer Log

			String filepath = tmpFilePath + "/tmp" + imageUrl.getPath().substring(dotPos);

			java.io.FileOutputStream fos = new java.io.FileOutputStream(filepath, false);
			java.io.BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
			byte data[] = new byte[1024];
			int count;
			while ((count = bufIn.read(data, 0, 1024)) != -1)
			{
				bout.write(data, 0, count);
			}
			bout.close();
			fos.close();
			bufIn.close();

			return put(filepath);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 0;
		}
	}

	private static int put(String localImagePath)
	{
		File f = new File(localImagePath);
		try
		{
			return put(new BufferedInputStream(new FileInputStream(f)), f.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	private static int put(BufferedInputStream picInput, String picName)
	{
		if (picInput == null || picName == null)
			return -1;

		try
		{
			// Uebertrage Daten
			HttpURLConnection httpCon = transmitBytes("PUT", "name", picName, picInput);

			// Frage zugewiesene ID ab
			BufferedReader br = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
			String str;
			StringBuffer sb = new StringBuffer();
			while ((str = br.readLine()) != null)
			{
				sb.append(str);
			}
			br.close();
			return Integer.parseInt(sb.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return 0;
	}

	private static HttpURLConnection transmitBytes(String httpType, String addressAttribute, String attributeValue, InputStream content)
	{
		HttpURLConnection httpCon = null;
		try
		{
			// Url des Webservice erstellen
			URL url = new URL(webServiceUrl + "?" + addressAttribute + "=" + attributeValue);

			// Verbindung aufbauen und den Typ setzen
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod(httpType);

			// Stream fuer den Output definieren
			BufferedOutputStream out = new BufferedOutputStream(httpCon.getOutputStream(), 1024);

			// Lade immer 1MB grosse Bloecke hoch
			byte[] data = new byte[1048576];

			int dataSize = 0;
			do
			{
				dataSize = content.read(data);
				out.write(data, 0, dataSize);
			}
			while (dataSize == 1048576);

			out.close();
			content.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return httpCon;
	}

	public static String getMetaInformation(String name, String url)
	{
		BufferedInputStream picInput = null;
		System.out.println("name:" + name + "; url:" + url);
		try
		{
			int dotPos = url.lastIndexOf(".");

			File jpgFile = new File(tmpFilePath + "/tmp" + url.substring(dotPos));
			if (!jpgFile.exists()) // Datei existiert nicht
				return "";
			picInput = new BufferedInputStream(new FileInputStream(jpgFile));
			Metadata metadata = com.drew.imaging.ImageMetadataReader.readMetadata(picInput);
			com.drew.metadata.exif.ExifDirectory exifDirectory = (ExifDirectory) metadata.getDirectory(com.drew.metadata.exif.ExifDirectory.class);

			if (name.equals("geo_lat"))
			{
				GpsDescriptor gps = (GpsDescriptor)exifDirectory.getObject(com.drew.metadata.exif.ExifDirectory.TAG_GPS_INFO);
				return gps == null ? "" : gps.getGpsLatitudeDescription();
			}
			if (name.equals("geo_long"))
			{
				GpsDescriptor gps = (GpsDescriptor)exifDirectory.getObject(com.drew.metadata.exif.ExifDirectory.TAG_GPS_INFO);
				return gps == null ? "" : gps.getGpsLongitudeDescription();
			}
			picInput.close();
		}
		catch (Exception ex)
		{

		}
		finally
		{
			try
			{
				picInput.close();
			}
			catch (Exception e)
			{
			}
		}
		
		return "";
	}
}

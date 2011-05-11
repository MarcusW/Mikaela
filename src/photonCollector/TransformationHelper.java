package photonCollector;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.GpsDirectory;

public class TransformationHelper
{
	public static String tmpFilePath;
	public static String webServiceUrl;

	/**
	 * Wandelt einen Zeitstring in Unixzeit um.
	 * @param time Der Zeitstring der Form dd.MM.yy 
	 * @return Die berechnete Unixzeit.</br>
	 * Der Wert ist <code>-1</code> falls ein Fehler aufgetreten ist.
	 */
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

		return -1;
	}

	/**
	 * Laedt das Bild mit der angegebenen ID zum Webservice hoch. 
	 * @param url Die url unter der das Bild erreichbar ist.
	 * @return Die ID welche vom Webservice vergeben wurde. </br>
	 * Der Wert betraegt <code>-1</code> falls ein Fehler aufgetreten ist.
	 */
	public static int uploadImage(String url)
	{
		try
		{
			URL imageUrl = new URL(url);
			File urlFile = new File(url);
			BufferedInputStream bufIn = new BufferedInputStream(imageUrl.openStream());

			int dotPos = imageUrl.getPath().lastIndexOf(".");
			if (dotPos < 0)
				return -1;

			String filepath = tmpFilePath + "/tmp" + imageUrl.getPath().substring(dotPos);
			File tmpFile = new File(filepath);
			if(tmpFile.exists())
				tmpFile.delete();
			
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

			return put(filepath, urlFile.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	private static int put(String localImagePath, String fileName)
	{
		File f = new File(localImagePath);
		try
		{
			return put(new BufferedInputStream(new FileInputStream(f)), fileName);
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

	public static boolean push(int id, NodeList nodes)
	{
		try
		{
			Document newXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		    
	        for (int i = 0; i < nodes.getLength(); i++) 
	        {
	            Node node = nodes.item(i);
	            Node copyNode = newXmlDocument.importNode(node, true);
	            newXmlDocument.appendChild(copyNode);
	        }

	        StringWriter output = new StringWriter();

	        Transformer transformer = TransformerFactory.newInstance().newTransformer();
	        transformer.transform(new DOMSource(nodes.item(0)), new StreamResult(output));

	        String xmlString = output.toString();

		    String xmltmp = "<photo created=\"1302011614\" title=\"Catedral del buen pastor\" geo_lat=\"43.31721809\" geo_long=\"-1.98207736000229\" aperture=\"F/8\" exposuretime=\"1/250s\" focallength=\"24mm\"";
		    xmltmp += " user_name=\"MaNi\" author=\"1\" upload_complete=\"1\">";
		    xmltmp += "<tags>";
		    xmltmp += "<tag>Architektur</tag>";
		    xmltmp += "<tag>Donostia</tag>";
		    xmltmp += "</tags>";
		    xmltmp += "<description>Kathedrale in Donostia, Spanien.</description>";
		    xmltmp += "</photo>";
		    InputStream inputStream = new ByteArrayInputStream(xmltmp.getBytes());
		    
		    //return transmitBytes("POST", "id", String.valueOf(id), inputStream) != null;
            
			// Frage ReturnCode ab
			BufferedReader br = new BufferedReader(new InputStreamReader(transmitBytes("POST", "id", String.valueOf(id), inputStream).getInputStream()));
			String str;
			StringBuffer sb = new StringBuffer();
			while ((str = br.readLine()) != null)
			{
				sb.append(str);
			}
			br.close();
		    return true;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
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
			out.flush();
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
		try
		{
			int dotPos = url.lastIndexOf(".");

			File jpgFile = new File(tmpFilePath + "/tmp" + url.substring(dotPos));
			if (!jpgFile.exists()) // Datei existiert nicht
				return "";
			picInput = new BufferedInputStream(new FileInputStream(jpgFile));
			Metadata metadata = com.drew.imaging.ImageMetadataReader.readMetadata(picInput);

			Directory gpsDir = metadata.getDirectory(GpsDirectory.class);
			if (name.equals("geo_lat")&&gpsDir != null)
			{	
				String latitude = gpsDir.getDescription(GpsDirectory.TAG_GPS_LATITUDE);
				String latitudeRef = gpsDir.getDescription(GpsDirectory.TAG_GPS_LATITUDE_REF);
				if(latitude == null)
					return "";
				double res = convertHourToDecimal(latitude);
				return latitudeRef.equalsIgnoreCase("S") ? String.valueOf(-res) : String.valueOf(res);
			}
			if (name.equals("geo_long")&&gpsDir != null)
			{
				String longitude = gpsDir.getDescription(GpsDirectory.TAG_GPS_LONGITUDE);
				String longitudeRef = gpsDir.getDescription(GpsDirectory.TAG_GPS_LONGITUDE_REF);
				if(longitude == null)
					return "";
				double res = convertHourToDecimal(longitude);
				return longitudeRef.equalsIgnoreCase("W") ? String.valueOf(-res) : String.valueOf(res);
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
	
	private static double convertHourToDecimal(String degree) {
	    if(!degree.matches("(-)?([0-6])?[0-9]\"[0-6][0-9]\'([0-6])?[0-9](.[0-9]{1,9})?"))
	        return 0;
	    String[] strArray=degree.split("[\"']");
	    return Double.parseDouble(strArray[0])+Double.parseDouble(strArray[1])/60+Double.parseDouble(strArray[2])/3600;
	}
}

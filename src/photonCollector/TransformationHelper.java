package photonCollector;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

/**
 * Diese Klasse bietet statische Funktionen an um alle Uploadvorgaenge
 * ermoeglichen zu koennen.
 * 
 * @author marcus
 * 
 */
public class TransformationHelper
{
	public static String currentPath;

	/**
	 * Ueberprueft ob die Datei config.prop korrekt strukturiert ist.
	 * 
	 * @return Gibt <code>true</code> zurueck falls alle benoetigten
	 *         Eigenschaften enthalten und korrekt sind.
	 */
	public static boolean checkPropertyFile()
	{
		if (getWebservicePhotoUrl().equals(""))
		{
			System.err.println("Keine photos.xml angegeben.");
			return false;
		}

		if (getWebserviceUserUrl().equals(""))
		{
			System.err.println("Keine users.xml angegeben.");
			return false;
		}
		
		File firstXsl = new File(getFirstXslPath());
		File secondXsl = new File(getSecondXslPath());
		File xhtmlFile = new File(getXhtmlPath());
		File tmpDir = new File(getTmpFolderPath());

		if (!firstXsl.exists())
		{
			System.err.println("xsl_xhtmlToXml existiert nicht.");
			return false;
		}
		if (!secondXsl.exists())
		{
			System.err.println("xsl_xmlToLog existiert nicht.");
			return false;
		}
		if (!xhtmlFile.exists())
		{
			System.err.println("xhtml_file existiert nicht.");
			return false;
		}
		if (!tmpDir.exists())
		{
			System.err.println("tmp_folder existiert nicht.");
			return false;
		}

		return true;
	}

	/**
	 * Gibt die Url des Webservice zurueck auf den die Photos hochgeladen werden
	 * sollen.
	 * 
	 * @return Die Url des Webservice.
	 */
	public static String getWebservicePhotoUrl()
	{
		return getPropertyInformation("web_photo_url");
	}
	
	/**
	 * Gibt die Url des Webservers zurueck unter der die users.xml verfuegbar ist.
	 * @return Die Url zur users.xml
	 */
	public static String getWebserviceUserUrl()
	{
		return getPropertyInformation("web_user_url");
	}

	/**
	 * Gibt den absoluten Pfad zur Transformationsdatei Xhtml -> Xml zurueck.
	 * 
	 * @return Der absolute Dateipfad.
	 */
	public static String getFirstXslPath()
	{
		return currentPath + "/" + getPropertyInformation("xsl_xhtmlToXml");
	}

	/**
	 * Gibt den absoluten Pfad zur Transformationsdatei Xml -> XmlLog zurueck.
	 * 
	 * @return Der absolute Dateipfad.
	 */
	public static String getSecondXslPath()
	{
		return currentPath + "/" + getPropertyInformation("xsl_xmlToLog");
	}

	/**
	 * Gibt den absoluten Pfad zur Xhtml-Datei zurueck die transformiert werden
	 * soll.
	 * 
	 * @return Der absolute Dateipfad.
	 */
	public static String getXhtmlPath()
	{
		return currentPath + "/" + getPropertyInformation("xhtml_file");
	}

	/**
	 * Gibt den absoluten Pfad zum Ordner zurueck indem einzelne Bilder abgelegt
	 * werden koennen.
	 * 
	 * @return Der absolute Dateipfad.
	 */
	protected static String getTmpFolderPath()
	{
		return currentPath + "/" + getPropertyInformation("tmp_folder");
	}

	/**
	 * Liest einen Eintrag aus der Konfigurationsdatei aus.
	 * 
	 * @param name
	 *            Der Name der gesuchten Eigenschaft.
	 * @return Der Wert welcher Der Eigenschaft zugewiesen wurde. Der
	 *         <code>String</code> ist leer falls die Eigenschaft nicht
	 *         ausgelesen werden konnte.
	 */
	private static String getPropertyInformation(String name)
	{
		Properties properties = new Properties();
		try
		{
			properties.load(new FileInputStream(currentPath + "/WEB-INF/config.prop"));
			return properties.get(name).toString();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return "";
	}

	/**
	 * Wandelt einen Zeitstring in Unixzeit um.
	 * 
	 * @param time
	 *            Der Zeitstring der Form dd.MM.yy
	 * @return Die berechnete Unixzeit.</br> Der Wert ist <code>-1</code> falls
	 *         ein Fehler aufgetreten ist.
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
	 * Laedt das Bild mit der angegebenen Url zum Webservice hoch.
	 * 
	 * @param url
	 *            Die url unter der das Bild erreichbar ist.
	 * @return Die ID welche vom Webservice vergeben wurde. </br> Der Wert
	 *         betraegt <code>-1</code> falls ein Fehler aufgetreten ist.
	 */
	public static int uploadImage(String url)
	{
		System.out.println("Lade Bild " + url + " hoch");
		try
		{
			URL imageUrl = new URL(url);
			File urlFile = new File(url);
			BufferedInputStream bufIn = new BufferedInputStream(imageUrl.openStream());

			int dotPos = imageUrl.getPath().lastIndexOf(".");
			if (dotPos < 0)
				return -1;

			String filepath = getTmpFolderPath() + "/tmp" + imageUrl.getPath().substring(dotPos);
			File tmpFile = new File(filepath);
			if (tmpFile.exists())
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

	/**
	 * Fuehrt einen Http-Putbefehl auf dem Webservice aus.
	 * 
	 * @param localImagePath
	 *            Der Pfad des lokal gespeicherten Bildes.
	 * @param picName
	 *            Der Name des Bildes.
	 * @return Die vom Webservice zurueckgelieferte ID. Gibt <code>-1</code>
	 *         falls ein Fehler aufgetreten ist.
	 */
	private static int put(String localImagePath, String picName)
	{
		File f = new File(localImagePath);
		try
		{
			return put(new BufferedInputStream(new FileInputStream(f)), picName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Fuehrt einen Http-Putbefehl auf dem Webservice aus.
	 * 
	 * @param picInput
	 *            Der zu uebertragene Stream.
	 * @param picName
	 *            Der Name des Bildes.
	 * @return Die vom Webservice zurueckgelieferte ID. Gibt <code>-1</code>
	 *         falls ein Fehler aufgetreten ist.
	 */
	private static int put(BufferedInputStream picInput, String picName)
	{
		if (picInput == null || picName == null)
			return -1;

		try
		{
			// Uebertrage Daten
			HttpURLConnection httpCon = transmitBytes("PUT", "name", picName, picInput, null);

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

	/**
	 * Pueft ob die angegebene Datei existiert und einen beliebigen Inhalt
	 * besitzt.
	 * 
	 * @param path
	 *            Der URL zur Datei.
	 * @return Gibt <code>false</code> zurueck falls auf die Datei nicht
	 *         zugegriffen werden kann oder diese leer ist.
	 */
	public static boolean isFileNullOrEmpty(String url_string)
	{
		InputStream in = null;
		try
		{
			URL url = new URL(url_string);
			in = url.openStream();
			BufferedReader dis = new BufferedReader(new InputStreamReader(in));
			return dis.read() == -1;
		}
		catch (Exception ex)
		{
		}
		finally
		{
			try
			{
				in.close();
			}
			catch (Exception ex)
			{
			}
		}
		return true;
	}

	/**
	 * Laedt die Metadaten eines Bildes zum Webservice hoch.
	 * 
	 * @param id
	 *            Die ID des Bildes.
	 * @param nodes
	 *            Die xml-knoten welche die Metadaten representieren.
	 * @return Gibt <code>true</code> zurueck falls der Server mit Http 200
	 *         geantwortet hat.
	 */
	public static boolean uploadMetadata(int id, NodeList nodes)
	{
		try
		{
			System.out.println("Lade Metadaten fuer Bild mit ID: " + id + " hoch");
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

			InputStream inputStream = new ByteArrayInputStream(xmlString.getBytes());

			HashMap<String, String> requestProperties = new HashMap<String, String>();
			requestProperties.put("Content-Type", "text/xml");

			// Frage ReturnCode ab
			HttpURLConnection httpCon = transmitBytes("POST", "id", String.valueOf(id), inputStream, requestProperties);

			return httpCon.getResponseCode() == 200;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return false;
	}

	/**
	 * Uebertraegt Daten via Http zum Webserver.
	 * 
	 * @param httpType
	 *            Der Http-Type wie zum Beispiel <code>PUT</code>.
	 * @param addressAttribute
	 *            Ein zusaetzliches Attribut welches an die Url angehangen
	 *            werden kann. Beispiel: <code>id</code>.
	 * @param attributeValue
	 *            Der Wert den das <code>addressAttribute</code> haben soll.
	 * @param content
	 *            Die zu uebertragenen Daten.
	 * @param requestProperties
	 *            Zusaetzliche Properties die fuer die Uebertragung gesetzt
	 *            werden koennen. Geben sie <code>null</code> an falls keine
	 *            Werte benoetigt werden.
	 * @return Das UrlConnection-Objekt aus welchem die Antwort bzw. der
	 *         Statuscode ausgelesen werden kann.
	 */
	private static HttpURLConnection transmitBytes(String httpType, String addressAttribute, String attributeValue, InputStream content, Map<String, String> requestProperties)
	{
		HttpURLConnection httpCon = null;
		try
		{
			// Url des Webservice erstellen
			URL url = new URL(getWebservicePhotoUrl() + "?" + addressAttribute + "=" + attributeValue);

			// Verbindung aufbauen und den Typ setzen
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod(httpType);

			// Alle Uebertragungseigenschaften festlegen
			if (requestProperties != null)
				for (String value : requestProperties.keySet())
					httpCon.setRequestProperty(value, requestProperties.get(value));

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
	
	/**
	 * Extrahiert den Dateinamen inklusive Endung aus dem angegebenen Pfad.
	 * @param path Der Pfad bzw. die Url der Datei
	 * @return Der Dateiname mit Endung. Falls der <code>path</code> keinen Slash enthielt, so wird <code>path</code> zurueckgegeben.
	 */
	public static String extractNameFromFilePath(String path)
	{
		String[] parts = path.split("/");
		if(parts.length == 0)
			return path;
		
		return parts[parts.length - 1];
	}

	/**
	 * Liest Exif-Informationen aus dem zuletzt hochgeladenem Bild aus.
	 * 
	 * @param name
	 *            Der Name der Exif-Information die ausgelesen werden soll.
	 *            Derzeit wird nur <code>geo_lat</code> und
	 *            <code>geo_long</code> unterstuetzt.
	 * @param url
	 *            Die Url des Bildes.
	 * @return Der ausgelesene Wert. Der <code>String</code> ist leer falls die
	 *         Information nicht ausgelesen werden konnte.
	 */
	public static String getMetaInformation(String name, String url)
	{
		BufferedInputStream picInput = null;
		try
		{
			int dotPos = url.lastIndexOf(".");

			File jpgFile = new File(getTmpFolderPath() + "/tmp" + url.substring(dotPos));
			if (!jpgFile.exists()) // Datei existiert nicht
				return "";
			picInput = new BufferedInputStream(new FileInputStream(jpgFile));
			Metadata metadata = com.drew.imaging.ImageMetadataReader.readMetadata(picInput);

			Directory gpsDir = metadata.getDirectory(GpsDirectory.class);
			if (name.equals("geo_lat") && gpsDir != null)
			{
				String latitude = gpsDir.getDescription(GpsDirectory.TAG_GPS_LATITUDE);
				String latitudeRef = gpsDir.getDescription(GpsDirectory.TAG_GPS_LATITUDE_REF);
				if (latitude == null)
					return "";
				double res = convertHourToDecimal(latitude);
				return latitudeRef.equalsIgnoreCase("S") ? String.valueOf(-res) : String.valueOf(res);
			}
			if (name.equals("geo_long") && gpsDir != null)
			{
				String longitude = gpsDir.getDescription(GpsDirectory.TAG_GPS_LONGITUDE);
				String longitudeRef = gpsDir.getDescription(GpsDirectory.TAG_GPS_LONGITUDE_REF);
				if (longitude == null)
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

	/**
	 * Konvertiert eine GPS-Minutenangabe in Grad.
	 * 
	 * @param degree
	 *            Die GPS-Minutenangabe.
	 * @return Der ermittelte Gradwert.
	 */
	private static double convertHourToDecimal(String degree)
	{
		if (!degree.matches("(-)?([0-6])?[0-9]\"[0-6][0-9]\'([0-6])?[0-9](.[0-9]{1,9})?"))
			return 0;
		String[] strArray = degree.split("[\"']");
		return Double.parseDouble(strArray[0]) + Double.parseDouble(strArray[1]) / 60 + Double.parseDouble(strArray[2]) / 3600;
	}
}

package photonCollector;

import java.io.*;
import java.net.*;
import org.xml.sax.InputSource;


public class WebserviceConnection
{
	// Arguments
	private String webServiceUrl;
	private String tmpFilePath;

	// Constructor
	public WebserviceConnection(String webServiceUrl, String tmpFilePath)
	{
		this.webServiceUrl = webServiceUrl;
		this.tmpFilePath = tmpFilePath;
	}

	// Methods
	public int put(String localImagePath)
	{
		File f = new File(localImagePath);
		try
		{	
			return put(new BufferedInputStream(new FileInputStream(f)),f.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	public synchronized int put(URL imageUrl)
	{
		try
		{
			BufferedInputStream bufIn = new BufferedInputStream(imageUrl.openStream());	
			
			int dotPos = imageUrl.getPath().lastIndexOf(".");
			if(dotPos < 0)
				return -1; //TODO: Fehler fuer Log
			
			String filepath = tmpFilePath + "/tmp." + imageUrl.getPath().substring(dotPos);
			
			java.io.FileOutputStream fos = new java.io.FileOutputStream(filepath, false);
			java.io.BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
			byte data[] = new byte[1024];
			int count;
			while((count = bufIn.read(data,0,1024)) != -1)
			{
				bout.write(data,0,count);
			}
			bout.close();
			fos.close();
			bufIn.close();
			
			return put(filepath);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	private int put(BufferedInputStream picInput, String picName)
	{
		if(picInput == null || picName == null)
			return -1;

		try
		{
			//TODO: Informationen eintragen in XML
//			Metadata metadata = com.drew.imaging.ImageMetadataReader.readMetadata(picInput);
//            com.drew.metadata.exif.ExifDirectory exifDirectory = (ExifDirectory) metadata.getDirectory(com.drew.metadata.exif.ExifDirectory.class);
//            java.util.Date date = exifDirectory.getDate(com.drew.metadata.exif.ExifDirectory.TAG_DATETIME);
//			
//            System.out.println(date);
			
			//Uebertrage Daten
			HttpURLConnection httpCon = transmitBytes("PUT", "name", picName, picInput);
			
			//Frage zugewiesene ID ab
			BufferedReader br = new BufferedReader(new InputStreamReader(
					httpCon.getInputStream()));
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
		
		return -1;
	}

	public boolean post(String xmlContent, int photoId)
	{
		return transmitBytes("POST", "id", String.valueOf(photoId), (new InputSource(new StringReader(xmlContent))).getByteStream()) != null;
	}
	
	private HttpURLConnection transmitBytes(String httpType, String addressAttribute, String attributeValue, InputStream content)
	{
		HttpURLConnection httpCon = null;
		try
		{
			// Url des Webservice erstellen
			URL url = new URL(webServiceUrl + "?" + addressAttribute + "=" + attributeValue);
			
			//Verbindung aufbauen und den Typ setzen
			httpCon = (HttpURLConnection) url.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod(httpType);

			//Stream fuer den Output definieren
			BufferedOutputStream out = new BufferedOutputStream(httpCon.getOutputStream(), 1024);

			//Lade immer 1MB grosse Bloecke hoch
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
	
	// Getter and Setter
	public String getWebServiceUrl()
	{
		return webServiceUrl;
	}

	public void setWebServiceUrl(String webServiceUrl)
	{
		this.webServiceUrl = webServiceUrl;
	}
}

package photonCollector;

import java.io.*;
import java.net.*;

import org.xml.sax.InputSource;

public class WebserviceConnection
{
	// Arguments
	private String webServiceUrl;

	// Constructor
	public WebserviceConnection(String webServiceUrl)
	{
		this.webServiceUrl = webServiceUrl;
	}

	// Methods
	public int put(String localImagePath)
	{
		File f = new File(localImagePath);
		try
		{
			return put(new FileInputStream(f),f.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	public int put(URL imageUrl)
	{
		File f = new File(imageUrl.toString());
		try
		{
			DataInputStream dis = new DataInputStream(new BufferedInputStream(imageUrl.openStream()));
			return put(dis, f.getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return -1;
		}
	}

	private int put(InputStream picInput, String picName)
	{
		if(picInput == null || picName == null)
			return -1;

		try
		{
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
			OutputStream out = httpCon.getOutputStream();

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

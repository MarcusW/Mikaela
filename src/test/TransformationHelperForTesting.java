package test;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import photonCollector.TransformationHelper;

/**
 * Erweiterter TransformationHelper, vorrangig fuer Testzwecke.
 * @author marcus
 *
 */
public class TransformationHelperForTesting extends TransformationHelper
{
	/**
	 * Sendet fuer jedes Photo eine Delete-Anfrage.
	 */
	public static void deleteAllPhotos()
	{
		try
		{
			if(isFileNullOrEmpty(getWebservicePhotoUrl()))
				return;
	        Document xmlDocument = DocumentBuilderFactory.
			newInstance().newDocumentBuilder().
			parse(getWebservicePhotoUrl());
			
			XPathFactory xPathFactory = XPathFactory.newInstance();

			XPath xPath = xPathFactory.newXPath();

			String expression = "/*/*/@id";
			XPathExpression xPathExpression = xPath.compile(expression);

			NodeList nl = (NodeList)xPathExpression.evaluate(xmlDocument, XPathConstants.NODESET);
			for (int i = 0; i < nl.getLength(); i++)
			{
				transmitBytes(Integer.parseInt(nl.item(i).getNodeValue()));
				System.out.println("Loesche Bild mit ID:" + nl.item(i).getNodeValue());
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	protected static HttpURLConnection transmitBytes(int id)
	{
		HttpURLConnection httpCon = null;
		try
		{
			URL url = new URL(getWebservicePhotoUrl() + "?id=" + id);
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
	
	public static void cleanTmpFolder()
	{
		try
		{
			File tmpFolder = new File(getTmpFolderPath());
			for (File file : tmpFolder.listFiles())
			{
				if(file.getName().startsWith("tmp."))
				{
					System.out.println("Loesche temporaere Datei: " + file.getName());
					file.delete();
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}

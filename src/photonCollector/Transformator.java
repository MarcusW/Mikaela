package photonCollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Diese Klasse transformiert Xml mittels Xsl.
 * @author marcus
 *
 */
public class Transformator
{
	//Der Pfad zur Xsl-Datei welche fuer die Transformation genutzt werden soll.
	private String xslFilePath;
	
	/**
	 * Gibt den Pfad zur XSL-Datei zurueck, die fuer die Transformation genutzt wird.
	 * @return Der abgelegte Pfad.
	 */
	public String getXslFilePath()
	{
		return xslFilePath;
	}
	
	/**
	 * Setzt den Pfad fuer die XSL-Datei welche fuer die Transformation genutzt werden soll.
	 * @param xslFilePath Der neue Pfad.
	 */
	public void setXslFilePath(String xslFilePath)
	{
		this.xslFilePath = xslFilePath;
	}
	
	/**
	 * Erstellt eine neue Instanz einer Transformatorklasse, welche Xml mittels XSL transformieren kann.
	 * @param xslFilePath Der Pfad zur Xsl-Datei die verwendet werden soll.
	 */
	public Transformator(String xslFilePath)
	{
		this.xslFilePath = xslFilePath;
	}
	
	/**
	 * Fuehrt die eigentliche Xsl-Transformation aus.
	 * @param resStream Der Stream in den die Ausgabe geschrieben werden soll.
	 * @param xmlFilePath Der Pfad zur Xml-Datei welche transformiert werden soll.
	 * @return Gibt <code>true</code> zurueck falls keine Fehler aufgetreten sind.
	 */
	public boolean transform(StreamResult resStream, String xmlFilePath)
	{
		// Verweise auf die Ausgangs- und Transformationsdatei
		File xhtmlFile = new File(xmlFilePath);
		try
		{
			InputStream inStream = new FileInputStream(xhtmlFile);
			return transform(resStream, inStream);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Fuehrt die eigentliche Xsl-Transformation aus.
	 * @param resStream Der Stream in den die Ausgabe geschrieben werden soll.
	 * @param inStream Der <code>InputStream</code> der den Xml-Inhalt bereitstellt.
	 * @return Gibt <code>true</code> zurueck falls keine Fehler aufgetreten sind.
	 */
	public boolean transform(StreamResult resStream, InputStream inStream)
	{
		File xsltFile = new File(xslFilePath);
		// JAXP liest Daten über die Source-Schnittstelle
		Source xsltSource = new StreamSource(xsltFile);
		
		try
		{
			// Reader um Doctype-Problem zu umgehen
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setEntityResolver(new EntityResolver()
			{
				public InputSource resolveEntity(String publicId,
						String systemId) throws SAXException, IOException
				{
					if (systemId.endsWith(".dtd"))
					{
						StringReader stringInput = new StringReader("");
						return new InputSource(stringInput);
					}
					else
					{
						return null;
					}
				}
			});
			
			SAXSource xmlSource = new SAXSource(reader, new InputSource(inStream));

			// Führe Transformation aus
			SAXTransformerFactory transFact = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			Transformer trans = transFact.newTransformer(xsltSource);
			trans.transform(xmlSource, resStream);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
}

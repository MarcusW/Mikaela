package photonCollector;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class Transformator
{
	private String xmlFilePath;
	private String xslFilePath;
	
	public Transformator(String xmlFilePath, String xslFilePath)
	{
		this.xmlFilePath = xmlFilePath;
		this.xslFilePath = xslFilePath;
	}
	
	public String getXmlFilePath()
	{
		return xmlFilePath;
	}
	
	public void setXmlFilePath(String xmlFilePath)
	{
		this.xmlFilePath = xmlFilePath;
	}
	
	public String getXslFilePath()
	{
		return xslFilePath;
	}
	
	public void setXslFilePath(String xslFilePath)
	{
		this.xslFilePath = xslFilePath;
	}
	
	public void transform(StreamResult resStream)
	{
		// Verweise auf die Ausgangs- und Transformationsdatei
		File xhtmlFile = new File(xmlFilePath);
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

			SAXSource xmlSource = new SAXSource(reader, new InputSource(
					xhtmlFile.getAbsolutePath()));

			// Führe Transformation aus
			SAXTransformerFactory transFact = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
			Transformer trans = transFact.newTransformer(xsltSource);
			trans.transform(xmlSource, resStream);
		}
		catch (Exception e)
		{
		}
	}
}

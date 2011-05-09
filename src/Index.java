import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import photonCollector.Transformator;
import photonCollector.WebserviceConnection;

import java.io.*;
import java.net.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.*;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;

/**
 * Servlet implementation class Index
 */
@WebServlet("/Index")
public class Index extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Index()
	{
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter writer = response.getWriter();

		// Versuche Pfad zu xhtml-datei auszulesen - Root ist dabei WebContent
		String path = request.getParameter("path");

		// Falls der Pfad nich spezifiziert wurde, so breche ab
		if (path == null)
		{
			writer.write("Pfad nicht angegeben.");
			return;
		}

		photonCollector.TransformationHelper.tmpFilePath = getServletContext().getRealPath("");
		photonCollector.TransformationHelper.webServiceUrl = "http://141.76.61.48:8103/photos";
		
		// Generiere ein TransformatorObjekt um die XML-Umwandlung
		// durchzufuehren
		Transformator transformator = new Transformator(
				getServletContext().getRealPath("/WEB-INF/" + path),
				getServletContext().getRealPath("/WEB-INF/transformation.xsl"));

		// Die transformierte XML-Datei
		ByteArrayOutputStream xml = new ByteArrayOutputStream();

		// fuehre Transformation aus
		transformator.transform(new StreamResult(System.out));

		WebserviceConnection con = new WebserviceConnection("http://141.76.61.48:8103/photos", getServletContext().getRealPath(""));
		
		//TODO: Bug mit URL beheben und Informationen hochladen
		//System.out.println(con.put("/home/marcus/amsterdam.jpg"));
		//System.out.println(con.put(new URL("http://www.mmt.inf.tu-dresden.de/Lehre/Sommersemester_11/AWE/Uebung/material/bilder/amsterdam.jpg")));
//		try
//		{
//			// DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance()
//			// .newDocumentBuilder();
//			// org.w3c.dom.Document doc = docBuilder
//			// .parse(new ByteArrayInputStream(xml.toByteArray()));
//			javax.xml.xpath.XPath x = javax.xml.xpath.XPathFactory
//					.newInstance().newXPath();
//			NodeList selectedNodes = (NodeList) x
//					.evaluate("//pp", new InputSource(new ByteArrayInputStream(
//							xml.toByteArray())), XPathConstants.NODESET);
//
//			StringWriter sw = new StringWriter();
//			Transformer serializer = TransformerFactory.newInstance().newTransformer();
//			serializer.transform(new DOMSource(selectedNodes.item(0)), new StreamResult(sw));
//			System.out.println(sw.toString()); 
//		}
//		catch (Exception ex)
//		{
//			ex.printStackTrace();
//		}
	}

	/**
	 * Laedt die Binaerdatei des Bildes hoch.
	 * 
	 * @param picName
	 *            Der Name des Bildes mit Dateiendung.
	 * @return Die vom Webservice zurueckgegebene ID.
	 */
	private int putImage(String picName)
	{
		picName = "Firefox_wallpaper.png"; // TODO: Entfernen und File anpassen
		try
		{
			// Bild laden
			File pic = new File("/home/marcus/Firefox_wallpaper.png");

			// Url des Webservice fuer Putbefehl erstellen
			URL url = new URL("http://141.76.61.48:8103/photos?name=" + picName);
			HttpURLConnection httpCon = (HttpURLConnection) url
					.openConnection();
			httpCon.setDoOutput(true);
			httpCon.setRequestMethod("PUT");

			InputStream in = new FileInputStream(pic);
			OutputStream out = httpCon.getOutputStream();

			byte[] data = new byte[1048576];

			int dataSize = 0;
			do
			{
				dataSize = in.read(data);
				out.write(data, 0, dataSize);
			}
			while (dataSize == 1048576);

			out.close();
			in.close();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					httpCon.getInputStream()));
			String str;
			StringBuffer sb = new StringBuffer();
			while ((str = br.readLine()) != null)
			{
				sb.append(str);
				sb.append("\n");
			}
			br.close();
			System.out.println("ID:" + str);
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		return 0;
	}

	private void executeTransformation(String xmlPath, StreamResult resStream)
	{
		// Verweise auf die Ausgangs- und Transformationsdatei
		File xhtmlFile = new File(getServletContext().getRealPath(
				"/WEB-INF/" + xmlPath));
		File xsltFile = new File(getServletContext().getRealPath(
				"/WEB-INF/transformation.xsl"));

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
			TransformerFactory transFact = TransformerFactory.newInstance();
			Transformer trans = transFact.newTransformer(xsltSource);
			trans.transform(xmlSource, resStream);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

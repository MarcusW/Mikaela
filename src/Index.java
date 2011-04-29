import org.xml.sax.*;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter writer = response.getWriter();
			
		//Versuche Pfad zu xhtml-datei auszulesen - Root ist dabei WebContent
		String path = request.getParameter("path");
		
		//Falls der Pfad nich spezifiziert wurde, so breche ab
		if(path == null)
		{
			writer.write("Pfad nicht angegeben.");
			return;
		}

		//Verweise auf die Ausgangs- und Transformationsdatei
		File xhtmlFile = new File(getServletContext().getRealPath("/WEB-INF/" + path));
		File xsltFile = new File(getServletContext().getRealPath("/WEB-INF/transformation.xsl"));
		
        // JAXP liest Daten über die Source-Schnittstelle
        Source xsltSource = new StreamSource(xsltFile);

		try
		{
			//Reader um Doctype-Problem zu umgehen
			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setEntityResolver(new EntityResolver() 
			{
				public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException 
				{
					if (systemId.endsWith(".dtd")) 
					{
						StringReader stringInput = new StringReader("");
						return new InputSource(stringInput);
					}
					else 
					{
						return null; // use default behavior
					}
				}
			});
					
			SAXSource xmlSource = new SAXSource(reader, new InputSource(xhtmlFile.getAbsolutePath()));
			
			//Führe Transformation aus
			TransformerFactory transFact = TransformerFactory.newInstance();
	        Transformer trans = transFact.newTransformer(xsltSource);
			trans.transform(xmlSource, new StreamResult(System.out));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		writer.write("Done");
	}
}

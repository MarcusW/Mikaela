import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;

import photonCollector.Transformator;

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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter writer = response.getWriter();

		// Versuche Pfad zu xhtml-datei auszulesen - Root ist dabei WebContent
		String path = request.getParameter("path");

		// Falls der Pfad nich spezifiziert wurde, so breche ab
		if (path == null)
		{
			writer.write("<log><error>Pfad nicht angegeben.</error></log>");
			return;
		}

		photonCollector.TransformationHelper.tmpFilePath = getServletContext().getRealPath("");
		photonCollector.TransformationHelper.webServiceUrl = "http://141.76.61.48:8103/photos";
		
		//Transformationsschritt 1 laedt das Bild hoch und gibt eine Xml-Datei zurueck welche alle zu aktualisierenden Photos enthaelt.
		Transformator transformatorToUploadDesc = new Transformator(getServletContext().getRealPath("/WEB-INF/transformation.xsl"));

		//Transformationsschritt 2 laedt alle Metainformationen zum Server hoch und erzeugt eine Log-Xml.
		Transformator transformatorToLog = new Transformator(getServletContext().getRealPath("/WEB-INF/transformation2.xsl"));
		
		//Die XML-Datei welche alle zu aktualisierenden Photos enthaelt.
		ByteArrayOutputStream uploadXml = new ByteArrayOutputStream();

		//Die XML-Datei welche alle Logausgaben der Transformationen enthaelt.
		ByteArrayOutputStream logXml = new ByteArrayOutputStream();
		
		//Fuehre die beiden Transformationen aus
		transformatorToUploadDesc.transform(new StreamResult(uploadXml), getServletContext().getRealPath("/WEB-INF/" + path));

		transformatorToLog.transform(new StreamResult(logXml), new ByteArrayInputStream(uploadXml.toByteArray()));
		
		//Ausgeben der Logdatei
		writer.write(logXml.toString());
	}
}

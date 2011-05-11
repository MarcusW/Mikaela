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

import photonCollector.TransformationHelper;
import photonCollector.Transformator;
import test.TransformationHelperForTesting;

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
		
		//Das Arbeitsverzeichnis ablegen
		TransformationHelper.currentPath = getServletContext().getRealPath("");
		
		//Ueberpruefe Property-Datei
		if(!TransformationHelper.checkPropertyFile())
		{
			writer.write("<log><error>Die Konfigurationsdatei ist fehlerhaft.</error></log>");
			return;
		}
		
		String path = request.getParameter("delete");
		
		//Falls delete=1 angehangen wird, werden die default-photos entfernt vom Webserver
		if(path != null && path.equals("1"))
			TransformationHelperForTesting.deleteAllPhotos();
		
		// Transformationsschritt 1 laedt das Bild hoch und gibt eine Xml-Datei
		// zurueck welche alle zu aktualisierenden Photos enthaelt.
		Transformator transformatorToUploadDesc = new Transformator(TransformationHelper.getFirstXslPath());

		// Transformationsschritt 2 laedt alle Metainformationen zum Server hoch
		// und erzeugt eine Log-Xml.
		Transformator transformatorToLog = new Transformator(TransformationHelper.getSecondXslPath());

		// Die XML-Datei welche alle zu aktualisierenden Photos enthaelt.
		ByteArrayOutputStream uploadXml = new ByteArrayOutputStream();

		// Die XML-Datei welche alle Logausgaben der Transformationen enthaelt.
		ByteArrayOutputStream logXml = new ByteArrayOutputStream();

		// Fuehre die beiden Transformationen aus
		if (!transformatorToUploadDesc.transform(new StreamResult(uploadXml), TransformationHelper.getXhtmlPath()))
		{
			writer.write("<log><error>Die Photos konnten nicht aus der Xhtml-Datei extrahiert werden.</error></log>");
			return;
		}

		if (!transformatorToLog.transform(new StreamResult(logXml), new ByteArrayInputStream(uploadXml.toByteArray())))
		{
			writer.write("<log><error>Die Loginformationen konnten nicht aus der Photos-Xml extrahiert werden.</error></log>");
			return;
		}
		
		// Ausgeben der Logdatei
		writer.write(logXml.toString());
	}
}

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


public class PictureContentHandler implements ContentHandler
{

	public void characters(char[] ch, int start, int length) throws SAXException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endDocument() throws SAXException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException
	{
		// TODO Auto-generated method stub
		
	}

	public void setDocumentLocator(Locator locator)
	{

	}

	public void skippedEntity(String name) throws SAXException
	{

	}

	public void startDocument() throws SAXException
	{

	}

	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		int i = 5;
	}

	public void startPrefixMapping(String prefix, String uri) throws SAXException
	{
	
	}

}

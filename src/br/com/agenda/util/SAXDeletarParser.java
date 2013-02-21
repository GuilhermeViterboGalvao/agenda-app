package br.com.agenda.util;

import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Xml;
import android.util.Xml.Encoding;

public class SAXDeletarParser extends DefaultHandler {

	private String mensagem;
	private StringBuilder sb;
	
	public String getMensagem(){
		return mensagem;
	}
	
	public static String parseDelete(InputStream xml) throws IOException, SAXException{
		SAXDeletarParser parser = new SAXDeletarParser();
		Xml.parse(xml, Encoding.UTF_8, parser);
		return parser.getMensagem();
	}
	
	@Override
	public void startDocument() throws SAXException {
		sb = new StringBuilder();
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		sb.append(ch,start,length);
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("mensagem".equals(localName)) {
			mensagem = sb.toString().trim();
			sb.setLength(0);
		}
	}
}
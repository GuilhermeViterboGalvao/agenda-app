package br.com.agenda.util;

import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;
import android.util.Xml;
import android.util.Xml.Encoding;
import br.com.agenda.bean.Contato;

public class SAXContatoParser extends DefaultHandler {

	private Contato contato;
	private StringBuilder sb;
	
	public Contato getContato(){
		return contato;
	}
	
	public static Contato parseContato(InputStream xml) throws IOException, SAXException{
		SAXContatoParser parser = new SAXContatoParser();
		Xml.parse(xml, Encoding.UTF_8, parser);
		return parser.getContato();
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
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if ("contato".equals(localName)) {
			contato = new Contato();
			
			for (int i = 0; i < attributes.getLength(); i++) {
				if ("id".equals(attributes.getLocalName(i))) {
					try {
						contato.setCodigo(Integer.parseInt(attributes.getValue(i)));
					} catch (Exception e) {
						Log.e("SAXContatoParser.startElement()", "Erro ao inserir código do contato.", e);
					}
				} else if ("caminhoFoto".equals(attributes.getLocalName(i))) {
					contato.setCaminhoFoto(attributes.getValue(i));					
				}
			}
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("contato".equals(localName)) {
			contato.setNome(sb.toString().trim());
			sb.setLength(0);
		} else if ("mensagem".equals(localName)) {
			contato.setMensagem(sb.toString().trim());
			sb.setLength(0);
		}
	}
}
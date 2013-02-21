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
import br.com.agenda.bean.Email;

public class SAXEmailParser extends DefaultHandler {
	
	private Email email;
	private Contato contato;
	private StringBuilder sb;
	
	public Email getEmail(){
		return email;
	}
	
	public static Email parseEmail(InputStream xml) throws IOException, SAXException {
		SAXEmailParser parser = new SAXEmailParser();
		Xml.parse(xml, Encoding.UTF_8, parser);
		return parser.getEmail();
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
		if ("email".equals(localName)) {
			email = new Email();
			
			for (int i = 0; i < attributes.getLength(); i++) {
				if ("email".equals(attributes.getLocalName(i))) {
					email.setEmail(attributes.getValue(i));
				}
			}			
		} else if ("contato".equals(localName)) {
			contato = new Contato();
			
			for (int i = 0; i < attributes.getLength(); i++) {
				if ("id".equals(attributes.getLocalName(i))) {
					try {
						contato.setCodigo(Integer.parseInt(attributes.getValue(i)));
					} catch (Exception e) {
						Log.e("SAXEmailParser.startElement()", "Erro ao inserir código do contato.", e);
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
		} else if ("email".equals(localName)) {
			email.setContato(contato);
			sb.setLength(0);
		} else if ("mensagem".equals(localName)) {
			email.setMensagem(sb.toString().trim());
			sb.setLength(0);
		}
	}
}
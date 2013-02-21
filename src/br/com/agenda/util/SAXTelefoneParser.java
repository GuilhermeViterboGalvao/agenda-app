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
import br.com.agenda.bean.Telefone;

public class SAXTelefoneParser extends DefaultHandler {
	
	private Telefone telefone;
	private Contato contato;
	private StringBuilder sb;
	
	public Telefone getTelefone(){
		return telefone;
	}
	
	public static Telefone parseTelefone(InputStream xml) throws IOException, SAXException{
		SAXTelefoneParser parser = new SAXTelefoneParser();
		Xml.parse(xml, Encoding.UTF_8, parser);
		return parser.getTelefone();
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
		if ("telefone".equals(localName)) {
			telefone = new Telefone();
			
			for (int i = 0; i < attributes.getLength(); i++) {
				if ("idTipoTelefone".equals(attributes.getLocalName(i))) {
					try {
						telefone.setTipo(Integer.parseInt(attributes.getValue(i)));
					} catch (Exception e) {
						Log.e("SAXTelefoneParser.startElement()", "Erro ao inserir tipo do telefone.", e);
					}
				} else if ("numero".equals(attributes.getLocalName(i))) {
					telefone.setNumero(attributes.getValue(i));
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
		} else if ("telefone".equals(localName)) {
			telefone.setContato(contato);
			sb.setLength(0);
		} else if ("mensagem".equals(localName)) {
			telefone.setMensagem(sb.toString().trim());
			sb.setLength(0);
		}
	}
}
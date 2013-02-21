package br.com.agenda.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import br.com.agenda.bean.Contato;
import br.com.agenda.bean.Email;
import br.com.agenda.bean.ListaDeContatos;
import br.com.agenda.bean.Telefone;

public class JSONListaDeContatosParser {

	public static ListaDeContatos parserListaDeContatos(InputStream in) throws JSONException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String json = null;
		String line = "";
		
		try {
			while (line != null) {
				sb.append(line.replaceAll("\t", ""));
				line = reader.readLine();
			}
			json = sb.substring(sb.indexOf("{"), sb.length());
			Log.i("JSON", json);
		} catch (Exception e) {
			Log.e("JSONListaDeContatosParser.parserListaDeContatos()", "Erro ao ler InputSream", e);
		} finally {
			try {
				reader.close();	
			} catch (Exception e) {
				Log.e("JSONListaDeContatosParser.parserListaDeContatos()", "Erro ao fechar BufferedReader", e);
			}			
		}
		
		ListaDeContatos listaDeContatos =  new ListaDeContatos();
		
		JSONObject jsonListaDeContatos = new JSONObject(json);
		
		if (jsonListaDeContatos.has("mensagem")) {
			listaDeContatos.setMensagem(jsonListaDeContatos.getString("mensagem"));
		} else {
			listaDeContatos.setQtContatosPagina(jsonListaDeContatos.getInt("qtContatosPagina"));
			listaDeContatos.setQtPaginas(jsonListaDeContatos.getInt("paginas"));
			listaDeContatos.setPaginaAtual(jsonListaDeContatos.getInt("paginaAtual"));
			
			JSONArray arrayContatos = jsonListaDeContatos.getJSONArray("contatos");
			JSONArray arrayTelefones = null;
			JSONArray arrayEmails = null;
			JSONObject objContato = null;
			JSONObject objTelefone = null;
			JSONObject objEmail = null;	
			
			List<Contato> contatos = new ArrayList<Contato>();
			List<Telefone> telefones = null;		
			List<Email> emails = null;
			Contato contato = null;
			Telefone telefone = null;
			Email email = null;
			
			for (int i = 0; i < arrayContatos.length(); i++) {
				objContato = arrayContatos.getJSONObject(i).getJSONObject("contato");
				
				contato = new Contato();
				contato.setCodigo(objContato.getInt("cdContato"));
				contato.setNome(objContato.getString("nmContato"));
				contato.setCaminhoFoto(objContato.getString("nmCaminhoFoto"));
				
				arrayTelefones = objContato.getJSONArray("telefones");
				telefones = new ArrayList<Telefone>();
				for (int j = 0; j < arrayTelefones.length(); j++) {
					objTelefone = arrayTelefones.getJSONObject(j);
					telefone = new Telefone();
					telefone.setNumero(objTelefone.getString("nmTelefone"));
					telefone.setTipo(objTelefone.getInt("cdTipoTelefone"));
					telefones.add(telefone);
				}
				contato.setTelefones(telefones);
				
				arrayEmails = objContato.getJSONArray("emails");
				emails = new ArrayList<Email>();
				for (int j = 0; j < arrayEmails.length(); j++) {
					objEmail = arrayEmails.getJSONObject(j);
					email = new Email();
					email.setEmail(objEmail.getString("nmEmail"));
					emails.add(email);
				}
				contato.setEmails(emails);
				
				contatos.add(contato);
			}
			
			listaDeContatos.setContatos(contatos);		
		}
		
		return listaDeContatos;
	}
}
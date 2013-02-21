package br.com.agenda.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONLoginParser {

	public static String parseLogin(InputStream in) throws JSONException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();
		String json = null;
		String line = "";
		
		try {
			while (line != null) {
				sb.append(line);
				line = reader.readLine();
			}
			json = sb.substring(sb.indexOf("{"), sb.length());
		} catch (Exception e) {
			Log.e("JSONBuscarContatosParser.parserListaDeContatos()", "Erro ao ler InputSream", e);
		} finally {
			try {
				reader.close();	
			} catch (Exception e) {
				Log.e("JSONBuscarContatosParser.parserListaDeContatos()", "Erro ao fechar BufferedReader", e);
			}			
		}
		
		JSONObject jsonListaDeContatos = new JSONObject(json);
		
		String mensagem = jsonListaDeContatos.getString("mensagem");
		
		return mensagem;
	}
}

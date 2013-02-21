package br.com.agenda.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.json.JSONException;
import org.xml.sax.SAXException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import br.com.agenda.bean.Contato;
import br.com.agenda.bean.Email;
import br.com.agenda.bean.ListaDeContatos;
import br.com.agenda.bean.Telefone;

public class GerenciadorDeDados {
	
	private HttpClient httpClient;
	private HttpResponse response;
	private HttpPost requestPost;
	private HttpGet requestGet;
	public static String urlLogin             = "http://192.168.0.124:8080/Agenda/login.do";
	public static String urlLogout            = "http://192.168.0.124:8080/Agenda/sistema/logout.do";
	public static String urlUploadContatoFoto = "http://192.168.0.124:8080/Agenda/sistema/uploadcontatofoto.do";
	public static String urlContatoFoto       = "http://192.168.0.124:8080/Agenda/sistema/contatofoto.do";
	public static String urlListarContatos    = "http://192.168.0.124:8080/Agenda/sistema/listarcontatos.do";
	public static String urlBuscarContatos    = "http://192.168.0.124:8080/Agenda/sistema/buscarcontatos.do";
	public static String urlCadastrarContato  = "http://192.168.0.124:8080/Agenda/sistema/cadastrarcontato.do";	
	public static String urlAlterarContato    = "http://192.168.0.124:8080/Agenda/sistema/alterarcontato.do";
	public static String urlDeletarContato    = "http://192.168.0.124:8080/Agenda/sistema/deletarcontato.do";	
	public static String urlCadastrarEmail    = "http://192.168.0.124:8080/Agenda/sistema/cadastraremail.do";	
	public static String urlAlterarEmail      = "http://192.168.0.124:8080/Agenda/sistema/alteraremail.do";
	public static String urlDeletarEmail      = "http://192.168.0.124:8080/Agenda/sistema/deletaremail.do";	
	public static String urlCadastrarTelefone = "http://192.168.0.124:8080/Agenda/sistema/cadastrartelefone.do";
	public static String urlAlterarTelefone   = "http://192.168.0.124:8080/Agenda/sistema/alterartelefone.do";
	public static String urlDeletarTelefone   = "http://192.168.0.124:8080/Agenda/sistema/deletartelefone.do";	
	
	public GerenciadorDeDados(HttpClient httpClient){
		this.httpClient = httpClient;
	}
	
	/**
	 * Caso logado com sucesso retorna uma mensagem de "Logado com sucesso",
	 * caso contrário retorna uma mensagem informando o problema na autenticação.
	 * @param login
	 * @param senha
	 * @return Mensagem contendo o status de autenticação.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws SAXException 
	 */
	public String login(String login, String senha) throws ClientProtocolException, IOException, SAXException{
		Log.i("GerenciadorDeDados", "getLoginMensagem()");
		String mensagem = null;
		
		if (login != null && senha != null) {
			InputStream in = getInputStream(urlLogin + "?login=" + login + "&senha=" + senha);
			mensagem = SAXLoginParser.parseLogin(in);
			if (in != null) {
				in.close();
			}
		}
		
		return mensagem;
	}
	
	/**
	 * Remove a sessão do usuário no servidor.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public void logout() throws ClientProtocolException, IOException{
		Log.i("GerenciadorDeDados", "getLoginMensagem()");		
		InputStream in = getInputStream(urlLogout);
		if (in != null) {
			in.close();
		}
	}
	
	/**
	 * Cadastra uma foto a um determinado contato.
	 * @param codigo
	 * @param foto
	 * @return Mensagem conténdo o status de envio da foto.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws SAXException 
	 */
	public String uploadContatoFoto(int idContato, Bitmap foto) throws ClientProtocolException, IOException, SAXException{
		Log.i("GerenciadorDeDados", "uploadContatoFoto()");
		String mensagem = null;
		
		if (idContato > 0 && foto != null) {						
			ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
			foto.compress(CompressFormat.JPEG, 100, byteOutput);
			
			byte[] dados = byteOutput.toByteArray();
			ByteArrayBody byteArray = new ByteArrayBody(dados, "imagem.jpg");
			
			MultipartEntity multipart = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			multipart.addPart("foto", byteArray);
			
			InputStream in = getInputStream(urlUploadContatoFoto + "?idContato=" + idContato, multipart);
			mensagem = SAXUploadContatoFotoParser.parseUpload(in);
			if (in != null) {
				in.close();
			}
		}
		
		return mensagem;
	}
	
	/**
	 * Retorna uma lista de contatos de acordo com o usuário logado.
	 * @return ListaDeContatos
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public ListaDeContatos listarContatos() throws ClientProtocolException, IOException, JSONException{
		Log.i("GerenciadorDeDados", "getListaDeContatos()");
		InputStream in = getInputStream(urlListarContatos);
		ListaDeContatos listaDeContatos = JSONListaDeContatosParser.parserListaDeContatos(in);
		if (in != null) {
			in.close();
		}
		return listaDeContatos;
	}
	
	/**
	 * Retorna uma lista de contatos de acordo com o usuário logado.
 	 * @param pagina
	 * @return ListaDeContatos
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public ListaDeContatos listarContatos(int pagina) throws ClientProtocolException, IOException, JSONException{
		Log.i("GerenciadorDeDados", "getListaDeContatos()");
		InputStream in = getInputStream(urlListarContatos + "?pagina=" + pagina);
		ListaDeContatos listaDeContatos = JSONListaDeContatosParser.parserListaDeContatos(in);
		if (in != null) {
			in.close();
		}
		return listaDeContatos;
	}
	
	/**
	 * Retorna uma lista de contatos de acordo com o usuário logado.
 	 * @param pagina
 	 * @param qtContatosPagina
	 * @return ListaDeContatos
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public ListaDeContatos listarContatos(int pagina, int qtContatosPagina) throws ClientProtocolException, IOException, JSONException{
		Log.i("GerenciadorDeDados", "getListaDeContatos()");
		InputStream in = getInputStream(urlListarContatos + "?pagina=" + pagina + "&qtContatosPagina=" + qtContatosPagina);
		ListaDeContatos listaDeContatos = JSONListaDeContatosParser.parserListaDeContatos(in);
		if (in != null) {
			in.close();
		}
		return listaDeContatos;
	}	
	
	/**
	 * Retorna uma lista de contatos de acordo com o nome enviado como parâmetro.
	 * Exemplo:
	 * 	nome = "An";
	 *  Irá retornar todos os nomes que começam com "An".
	 * @param nome
	 * @return ListaDeContatos
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws ClientProtocolException 
	 * @throws JSONException 
	 */
	public ListaDeContatos buscarContatos(String nome) throws ClientProtocolException, UnsupportedEncodingException, IOException, JSONException{
		Log.i("GerenciadorDeDados", "buscarContatos()");
		ListaDeContatos listaDeContatos = null;
		
		if (nome != null) {
			InputStream in = getInputStream(urlBuscarContatos + "?nome=" + URLEncoder.encode(nome, "UTF-8"));	
			listaDeContatos = JSONBuscarContatosParser.parserListaDeContatos(in);
			if (in != null) {
				in.close();
			}
		}
		
		return listaDeContatos;
	}
	
	/**
	 * Cadastra um novo contato.
	 * @param nome
	 * @return Novo Contato cadastrado
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws SAXException 
	 */
	public Contato cadastrarContato(String nome) throws ClientProtocolException, IOException, SAXException{
		Log.i("GerenciadorDeDados", "cadastrarContato()");
		Contato contato = null;
		
		if (nome != null) {
			InputStream in = getInputStream(urlCadastrarContato + "?nome=" + nome);
			contato = SAXContatoParser.parseContato(in);
			if (in != null) {
				in.close();
			}
		}
		
		return contato;		
	}
	
	/**
	 * Altera o nome do contato de acordo com o codigo informado.
	 * @param codigo
	 * @param nome
	 * @return Contato alterado
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws SAXException 
	 */
	public Contato alterarContato(int codigo, String nome) throws ClientProtocolException, IOException, SAXException{
		Log.i("GerenciadorDeDados", "alterarContato()");
		Contato contato = null;
		
		if (nome != null && codigo > 0) {
			InputStream in = getInputStream(urlAlterarContato + "?idContato=" + codigo + "&nome=" + nome);
			contato = SAXContatoParser.parseContato(in);
			if (in != null) {
				in.close();
			}
		}
		
		return contato;		
	}
	
	/**
	 * Remove um contato de acordo com o código informado.
	 * @param codigo
	 * @return Mensagem contendo status de remoção do contato.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws SAXException 
	 */
	public String deletarContato(int codigo) throws ClientProtocolException, IOException, SAXException{
		Log.i("GerenciadorDeDados", "deletarContato()");
		String mensagem = null;
		
		if (codigo > 0) {
			InputStream in = null;
			in = getInputStream(urlDeletarContato + "?idContato=" + codigo);
			mensagem = SAXDeletarParser.parseDelete(in);
			if (in != null) {
				in.close();
			}
		}
		
		return mensagem;
	}
	
	/**
	 * Cadastra um novo e-mail a um determinado contato.
	 * @param idContato
	 * @param nmEmail
	 * @return Novo Email cadastrado
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws SAXException 
	 */
	public Email cadastrarEmail(int idContato, String nmEmail) throws ClientProtocolException, IOException, SAXException{
		Log.i("GerenciadorDeDados", "cadastrarEmail()");
		Email email = null;
		
		if (nmEmail != null && idContato > 0) {
			InputStream in = getInputStream(urlCadastrarEmail + "?idContato=" + idContato + "&email=" + nmEmail);
			email = SAXEmailParser.parseEmail(in);
			if (in != null) {
				in.close();
			}
		}
		
		return email;		
	}
	
	/**
	 * Altera o e-mail de um determinado contato.
	 * @param idContato
	 * @param emailAntigo
	 * @param emailNovo
	 * @return Email alterado.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws SAXException 
	 */
	public Email alterarEmail(int idContato, String emailAntigo, String emailNovo) throws ClientProtocolException, IOException, SAXException{
		Log.i("GerenciadorDeDados", "alterarEmail()");
		Email email = null;
		
		if (idContato > 0 && emailAntigo != null && emailNovo != null) {
			InputStream in = getInputStream(urlAlterarEmail + "?idContato=" + idContato + "&emailAntigo=" + emailAntigo + "&emailNovo=" + emailNovo);
			email = SAXEmailParser.parseEmail(in);
			if (in != null) {
				in.close();
			}
		}
		
		return email;		
	}
	
	/**
	 * Deleta um e-mail de um determinado contato.
	 * @param idContato
	 * @param nmEmail
	 * @return Mensagem contendo status de remoção do e-mail.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws SAXException 
	 */
	public String deletarEmail(int idContato, String nmEmail) throws ClientProtocolException, IOException, SAXException{
		Log.i("GerenciadorDeDados", "deletarContato()");
		String mensagem = null;
		
		if (idContato > 0 && nmEmail != null) {
			InputStream in = getInputStream(urlDeletarEmail + "?idContato=" + idContato + "&email=" + nmEmail);
			mensagem = SAXDeletarParser.parseDelete(in);
			if (in != null) {
				in.close();
			}
		}
		
		return mensagem;
	}
	
	/**
	 * Cadastra um novo telefone a um determinado contato.
	 * @param idContato
	 * @param idTopoTelefone
	 * @param numero
	 * @return Novo Telefone cadastrado.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws SAXException 
	 */
	public Telefone cadastrarTelefone(int idContato, int idTopoTelefone, String numero) throws ClientProtocolException, IOException, SAXException{
		Log.i("GerenciadorDeDados", "cadastrarTelefone()");
		Telefone telefone = null;
		
		if (idContato > 0 && idTopoTelefone > 0 && numero != null) {
			InputStream in = getInputStream(urlCadastrarTelefone + "?idContato=" + idContato + "&idTipoTelefone=" + idTopoTelefone + "&numero=" + numero);
			telefone = SAXTelefoneParser.parseTelefone(in);
			if (in != null) {
				in.close();
			}
		}
		
		return telefone;		
	}
	
	/**
	 * Altera um telefone de um determinado contato.
	 * @param idContato
	 * @param idTopoTelefone
	 * @param numeroAntigo
	 * @param numeroNovo
	 * @return Telefone alterado.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws SAXException 
	 */
	public Telefone alterarTelefone(int idContato, int idTopoTelefone, String numeroAntigo, String numeroNovo) throws ClientProtocolException, IOException, SAXException{
		Log.i("GerenciadorDeDados", "alterarTelefone()");
		Telefone telefone = null;
		
		if (idContato > 0 && idTopoTelefone > 0 && numeroAntigo != null && numeroNovo != null) {
			String url = urlAlterarTelefone + "?idContato=" + idContato + "&idTipoTelefone=" + idTopoTelefone + "&numeroAntigo=" + numeroAntigo + "&numeroNovo=" + numeroNovo;
			InputStream in = getInputStream(url);
			telefone = SAXTelefoneParser.parseTelefone(in);
			if (in != null) {
				in.close();
			}
		}
		
		return telefone;		
	}
	
	/**
	 * Deleta o telefone de um determinado contato.
	 * @param idContato
	 * @param numero
	 * @return Mensagem contendo status de remoção do telefone.
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 * @throws SAXException 
	 */
	public String deletarTelefone(int idContato, String numero) throws ClientProtocolException, IOException, SAXException{
		Log.i("GerenciadorDeDados", "deletarTelefone()");
		String mensagem = null;
		
		if (idContato > 0 && numero != null) {
			InputStream in = getInputStream(urlDeletarTelefone + "?idContato=" + idContato + "&numero=" + numero);
			mensagem = SAXDeletarParser.parseDelete(in);
			if (in != null) {
				in.close();
			}
		}
		
		return mensagem;
	}
	
	/**
	 * É executada um requisição Http Get ao servidor.
	 * @param url
	 * @return InputStream
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public synchronized InputStream getInputStream(String url) throws ClientProtocolException, IOException {
		Log.i("GerenciadorDeDados.getInputStream()", "url: " + url);
		
		requestGet = new HttpGet(url);				
		requestGet.setHeader("Accept", "text/plain");
		response = httpClient.execute(requestGet);
		
		int statusCode = response.getStatusLine().getStatusCode();
		InputStream in = null;
		
		if (statusCode == HttpStatus.SC_OK) {
			in = response.getEntity().getContent();
		} else {
			response.getEntity().getContent().close();
		}				
		
		return in;
	}
	
	/**
	 * É executada um requisição Http Post ao servidor.
	 * @param url
	 * @param multipart
	 * @return InputStream
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public synchronized InputStream getInputStream(String url, MultipartEntity multipart) throws ClientProtocolException, IOException {
		Log.i("GerenciadorDeDados.getInputStream()", "url: " + url);
		
		requestPost = new HttpPost(url);
		requestPost.setHeader("Accept", "text/plain");
		requestPost.setEntity(multipart);
		response = httpClient.execute(requestPost);
		
		int statusCode = response.getStatusLine().getStatusCode();
		InputStream in = null;
		
		if (statusCode == HttpStatus.SC_OK) {
			in = response.getEntity().getContent();	
		} else {
			response.getEntity().getContent().close();
		}				
		
		return in;
	}
}
package br.com.agenda;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import br.com.agenda.util.GerenciadorDeDados;
import br.com.agenda.util.MyCookieStore;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class AgendaApp extends Application {

	private static final HttpRequestRetryHandler retryHandler;
	
	private static final AbstractHttpClient httpClient;	
	
	static {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));		
		HttpParams clientParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(clientParams, 10 * 1000);
		HttpConnectionParams.setSoTimeout(clientParams, 10 * 1000);
		ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(clientParams, schemeRegistry);	
		httpClient = new DefaultHttpClient(cm, null);
		httpClient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,"Agenda App 1.0");
		retryHandler = new DefaultHttpRequestRetryHandler(5, false) {
			@Override
			public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
				Log.i("http-exception", exception.getClass().getSimpleName() + ":" + exception.getMessage());
				boolean result = super.retryRequest(exception, executionCount, context);
				if (result == false) {
					return false;
				}		
				if (exception instanceof NoHttpResponseException) {
					return true;
				}
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					
				}
				return true;
			}
		};
		httpClient.setHttpRequestRetryHandler(retryHandler);
	}
	
	private ConnectivityManager connectivityManager;
	
	private GerenciadorDeDados gerenciador;
	
	public GerenciadorDeDados getGerenciador(){
		return gerenciador;
	}
	
	private SharedPreferences preferences;
	
	public SharedPreferences getPreferences(){
		return preferences;
	}
	
	private HashMap<String, Bitmap> cache;
	
	public void setImagem(String url, Bitmap imagem){
		cache.put(url, imagem);
	}
	
	public Bitmap getImagem(String url){
		return cache.get(url);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		preferences = getSharedPreferences("AgendaApp", Context.MODE_PRIVATE);
		httpClient.setCookieStore(new MyCookieStore(preferences));
		gerenciador = new GerenciadorDeDados(httpClient);
		cache = new HashMap<String, Bitmap>();
	}
	
	@Override
	public void onLowMemory() {
		cache.clear();
		super.onLowMemory();
	}
	
	public synchronized boolean conectado(){
		NetworkInfo infoWIFI = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (infoWIFI.isConnected()) {
			return true;
		}
		
		NetworkInfo infoMOBILE = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (infoMOBILE.isConnected()) {
			return true;
		}
		
		return false;
	}
	
	public boolean logado(){
		List<Cookie> cookies = httpClient.getCookieStore().getCookies();
		
		boolean login = false;
		boolean senha = false;
		boolean jsessionid = false;
		
		for (int i = 0; i < cookies.size(); i++) {
			if (cookies.get(i).getName().equals("login")) {
				login = true;
			} else if (cookies.get(i).getName().equals("senha")) {
				senha = true;
			} else if (cookies.get(i).getName().equals("JSESSIONID") || cookies.get(i).getName().equals("jsessionid")) {
				jsessionid = false;
			}
			
			if ((login && senha) || jsessionid) {
				return true;
			}
		}
		
		return false;
	}
	
	public void sair(){
		SharedPreferences.Editor edit = preferences.edit();
		edit.remove("login");
		edit.remove("senha");
		edit.commit();
		
		httpClient.getCookieStore().clear();
	}
}
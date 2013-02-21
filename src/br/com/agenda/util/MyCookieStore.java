package br.com.agenda.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieIdentityComparator;
import org.apache.http.impl.cookie.BasicClientCookie;

import android.content.SharedPreferences;

public class MyCookieStore implements CookieStore {
	
	private final ArrayList<Cookie> cookies;
	private final Comparator<Cookie> cookieComparator;
	
	public MyCookieStore(SharedPreferences preferences){
		this.cookies = new ArrayList<Cookie>();
		this.cookieComparator = new CookieIdentityComparator();
		
		if (preferences != null) {
			BasicClientCookie basicCookie;
			
			String login = preferences.getString("login", null);
			if (login != null){ 
				basicCookie = new BasicClientCookie("login", login);
				basicCookie.setDomain("192.168.0.124");
				basicCookie.setPath("/Agenda");
				basicCookie.setExpiryDate(new Date(System.currentTimeMillis()+365*24*3600000));
				basicCookie.setVersion(1);
				addCookie(basicCookie);
			}
			
			String senha = preferences.getString("senha", null);
			if (senha != null) {
				basicCookie = new BasicClientCookie("senha", senha);
				basicCookie.setDomain("192.168.0.124");
				basicCookie.setPath("/Agenda");
				basicCookie.setExpiryDate(new Date(System.currentTimeMillis()+365*24*3600000));
				basicCookie.setVersion(1);
				addCookie(basicCookie);
			}			
		}
	}

	public synchronized void addCookie(Cookie cookie) {
		if (cookie != null) {
			/* Caso haja um cookie com o mesmo nome, é necessário removê - lo
			   para poder substituir pelo novo. */
			for (Iterator<Cookie> it = cookies.iterator(); it.hasNext();) {
				if (cookieComparator.compare(cookie, it.next()) == 0) {
					it.remove();
					break;
				}
			}

			if (!cookie.isExpired(new Date())) {
				cookies.add(cookie);
			}
		}
	}
	
	public synchronized void addCookies(Cookie[] cookies) {
		if (cookies != null) {
			for (Cookie cooky : cookies) {
				this.addCookie(cooky);
			}
		}
	}

	public synchronized void clear() {
		cookies.clear();
	}

	public synchronized boolean clearExpired(final Date date) {
		if (date == null) {
			return false;
		}
		boolean removed = false;
		for (Iterator<Cookie> it = cookies.iterator(); it.hasNext();) {
			if (it.next().isExpired(date)) {
				it.remove();
				removed = true;
			}
		}
		return removed;
	}

	public synchronized List<Cookie> getCookies() {
		return Collections.unmodifiableList(this.cookies);
	}
}
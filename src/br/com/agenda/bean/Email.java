package br.com.agenda.bean;

import java.io.Serializable;

public class Email implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5352941899445580791L;
	private String email;
	private Contato contato;
	private String mensagem;
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public Contato getContato() {
		return contato;
	}
	public void setContato(Contato contato) {
		this.contato = contato;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}

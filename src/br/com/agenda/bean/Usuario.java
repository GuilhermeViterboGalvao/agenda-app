package br.com.agenda.bean;

import java.io.Serializable;

public class Usuario implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8546714693170360245L;
	private String login;
	private String senha;
	private ListaDeContatos contatos;
	private String mensagem;
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public ListaDeContatos getContatos() {
		return contatos;
	}
	public void setContatos(ListaDeContatos contatos) {
		this.contatos = contatos;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
}

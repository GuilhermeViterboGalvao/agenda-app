package br.com.agenda.bean;

import java.io.Serializable;

public class Telefone implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2310933251992074875L;
	private String numero;
	private int tipo;
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
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
}

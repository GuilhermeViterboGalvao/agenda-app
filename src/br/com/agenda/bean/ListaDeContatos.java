package br.com.agenda.bean;

import java.io.Serializable;
import java.util.List;

public class ListaDeContatos implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3959786217965732225L;
	private int qtContatosPagina;
	private int paginaAtual;
	private int qtPaginas;
	private List<Contato> contatos;
	private String mensagem;
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public int getQtContatosPagina() {
		return qtContatosPagina;
	}
	public void setQtContatosPagina(int qtContatosPagina) {
		this.qtContatosPagina = qtContatosPagina;
	}
	public int getPaginaAtual() {
		return paginaAtual;
	}
	public void setPaginaAtual(int paginaAtual) {
		this.paginaAtual = paginaAtual;
	}
	public int getQtPaginas() {
		return qtPaginas;
	}
	public void setQtPaginas(int qtPaginas) {
		this.qtPaginas = qtPaginas;
	}
	public List<Contato> getContatos() {
		return contatos;
	}
	public void setContatos(List<Contato> contatos) {
		this.contatos = contatos;
	}
}
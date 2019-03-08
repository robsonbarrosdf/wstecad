package br.leg.camara.wstecad.model;

import java.util.List;

public class Termo {

	private String termo;
	
	private List<Categoria> categorias;
	private List<Relacionamento> relacionamentos;
	
	private String notaAplicativa;
	private String notaExplicativa;
	private String notaHistorica;
	private String fonte;
	
	public String getTermo() {
		return termo;
	}
	public void setTermo(String termo) {
		this.termo = termo;
	}
	public List<Relacionamento> getRelacionamentos() {
		return relacionamentos;
	}
	public void setRelacionamentos(List<Relacionamento> relacionamentos) {
		this.relacionamentos = relacionamentos;
	}
	public List<Categoria> getCategorias() {
		return categorias;
	}
	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}
	public String getNotaAplicativa() {
		return notaAplicativa;
	}
	public void setNotaAplicativa(String notaAplicativa) {
		this.notaAplicativa = notaAplicativa;
	}
	public String getNotaExplicativa() {
		return notaExplicativa;
	}
	public void setNotaExplicativa(String notaExplicativa) {
		this.notaExplicativa = notaExplicativa;
	}
	public String getNotaHistorica() {
		return notaHistorica;
	}
	public void setNotaHistorica(String notaHistorica) {
		this.notaHistorica = notaHistorica;
	}
	public String getFonte() {
		return fonte;
	}
	public void setFonte(String fonte) {
		this.fonte = fonte;
	}
}

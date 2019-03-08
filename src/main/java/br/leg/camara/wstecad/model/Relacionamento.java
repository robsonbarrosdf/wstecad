package br.leg.camara.wstecad.model;

public class Relacionamento {

	//private Termo termoPrincipal;
	private TipoRelacionamento tipoRelacionamento;
	private Termo termoRelacionado;
	
	public TipoRelacionamento getTipoRelacionamento() {
		return tipoRelacionamento;
	}
	public void setTipoRelacionamento(TipoRelacionamento tipoRelacionamento) {
		this.tipoRelacionamento = tipoRelacionamento;
	}
	public Termo getTermoRelacionado() {
		return termoRelacionado;
	}
	public void setTermoRelacionado(Termo termoRelacionado) {
		this.termoRelacionado = termoRelacionado;
	}
	
}

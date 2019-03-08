package br.leg.camara.wstecad.model;

public class TipoRelacionamento {
	
	private String codigoRelacionamento;
	private String descricao;
	private String codigoRelacionamentoPai;
	//private String classificacao;
	
	public String getCodigoRelacionamento() {
		return codigoRelacionamento;
	}
	public void setCodigoRelacionamento(String codigoRelacionamento) {
		this.codigoRelacionamento = codigoRelacionamento;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getCodigoRelacionamentoPai() {
		return codigoRelacionamentoPai;
	}
	public void setCodigoRelacionamentoPai(String codigoRelacionamentoPai) {
		this.codigoRelacionamentoPai = codigoRelacionamentoPai;
	}
//	public String getClassificacao() {
//		return classificacao;
//	}
//	public void setClassificacao(String classificacao) {
//		this.classificacao = classificacao;
//	}
	
}

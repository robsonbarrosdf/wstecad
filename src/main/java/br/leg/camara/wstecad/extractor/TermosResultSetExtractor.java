package br.leg.camara.wstecad.extractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import br.leg.camara.wstecad.model.Categoria;
import br.leg.camara.wstecad.model.TipoRelacionamento;
import br.leg.camara.wstecad.model.Relacionamento;
import br.leg.camara.wstecad.model.Termo;

public class TermosResultSetExtractor implements ResultSetExtractor<List<Termo>> {
	
	private final int colTermo = 1;
	private final int colCodigoRelacionamento = 2;
	private final int colTermoRelacionado = 3;
	
	private final int colDescricaoRelacionamento = 4;
	private final int colCodigoRelacionamentoPai = 5;
	private final int colDescricaoRelacionamentoPai = 6;
	
	//private final int colClassificacaoRelacionamento = 7;
	
	private Map<String, TipoRelacionamento> mapTipoRelacionamentos = new HashMap<String, TipoRelacionamento>();
	private Map<String, Categoria> mapCategorias = new HashMap<String, Categoria>();

	@Override
	public List<Termo> extractData(ResultSet rs) throws SQLException, DataAccessException {
		
		Termo termo = null;
		List<Termo> termos = new ArrayList<Termo>();
		//Map<String, Categoria> mapCategoriasTermo = null;
		String valorTermoAnterior = "";
		String valorTermoCorrente = "";
		//String textoRelacionamentoAnterior = "";
		//String textoRelacionamentoCorrente = "";
		while(rs.next()) {
			
			valorTermoCorrente = rs.getString(colTermo);
			
			if (termo==null || !valorTermoCorrente.isEmpty() && !valorTermoCorrente.equals(valorTermoAnterior)) {

				if (termo!=null) termos.add(termo);
				
				termo = new Termo();
				termo.setTermo(valorTermoCorrente);
				termo.setRelacionamentos(new ArrayList<Relacionamento>());
				termo.setCategorias(new ArrayList<Categoria>());
				
			} 

			String codigoRelacionamento = rs.getString(colCodigoRelacionamento);
			switch (codigoRelacionamento) {
			
			case "NA":
				termo.setNotaAplicativa(rs.getString(colTermoRelacionado));
				break;
			case "NE":		
				termo.setNotaExplicativa(rs.getString(colTermoRelacionado));
				break;
			case "NH":					
				termo.setNotaHistorica(rs.getString(colTermoRelacionado));
				break;
			case "FT":					
				termo.setFonte(rs.getString(colTermoRelacionado));
				break;
				
			case "USE":
			case "UP":
			case "TG":
			case "TE":
			case "TR":
				
				TipoRelacionamento tipoRelacionamento = getTipoRelacionamento(rs);
				
				Termo termoRelacionado = new Termo();
				termoRelacionado.setTermo(rs.getString(colTermoRelacionado));
				
				Relacionamento relacionamento = new Relacionamento();
				relacionamento.setTipoRelacionamento(tipoRelacionamento);
				relacionamento.setTermoRelacionado(termoRelacionado);
				
				termo.getRelacionamentos().add(relacionamento);
				
				break;
				
			default: // Categorias / Modificadores / Identificadores
				
				Categoria categoria = getCategoria(rs);
				termo.getCategorias().add(categoria);
					
				break;
			}

			valorTermoAnterior = valorTermoCorrente;
		}
		if (termo!=null) termos.add(termo);
		return termos.size()==0 ? null : termos;
	}
	
	private Categoria getCategoria(ResultSet rs) throws SQLException {
		String codigoCategoria = rs.getString(colCodigoRelacionamento); // a categoria é um tipo de relacionamento;
		String codigoCategoriaPai = rs.getString(colCodigoRelacionamentoPai);
		
		String chaveCategoria = codigoCategoria + (codigoCategoriaPai.equals("N/A") ? "" : "." + codigoCategoriaPai);
		Categoria categoria = mapCategorias.get(chaveCategoria);
		
		if(categoria==null) {
			categoria = new Categoria();
			categoria.setCodigoCategoria(codigoCategoria);
			categoria.setDescricao(rs.getString(colDescricaoRelacionamento));
			
			if (!codigoCategoriaPai.equals("N/A")) {
				Categoria categoriaPai = mapCategorias.get(codigoCategoriaPai);
				if (categoriaPai==null) {
					categoriaPai = new Categoria();
					categoriaPai.setCodigoCategoria(codigoCategoriaPai);
					categoriaPai.setDescricao(rs.getString(colDescricaoRelacionamentoPai));
					mapCategorias.put(codigoCategoriaPai, categoriaPai);
				}
				categoria.setCategoriaPai(categoriaPai);
			}
			
			mapCategorias.put(chaveCategoria, categoria);
		}
		return categoria;
	}
	
	private TipoRelacionamento getTipoRelacionamento(ResultSet rs) throws SQLException {
		String codigoRelacionamento = rs.getString(colCodigoRelacionamento);
		TipoRelacionamento tipoRelacionamento = mapTipoRelacionamentos.get(codigoRelacionamento);
		if (tipoRelacionamento==null) {
			tipoRelacionamento = new TipoRelacionamento();
			tipoRelacionamento.setCodigoRelacionamento(codigoRelacionamento);
			//tipoRelacionamento.setClassificacao(rs.getString(colClassificacaoRelacionamento));
			tipoRelacionamento.setDescricao(rs.getString(colDescricaoRelacionamento));
			String codigoRelacionamentoPai = rs.getString(colCodigoRelacionamentoPai);
			if (codigoRelacionamentoPai!=null && codigoRelacionamentoPai.equals("N/A")) codigoRelacionamentoPai = null;
			tipoRelacionamento.setCodigoRelacionamentoPai(codigoRelacionamentoPai);			
			mapTipoRelacionamentos.put(codigoRelacionamento, tipoRelacionamento);
		}
		return tipoRelacionamento;
	}
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}	

}

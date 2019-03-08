package br.leg.camara.wstecad.dao;

import java.util.Map;
import java.util.List;

public interface TermoDao {

	Object getTermos();
	
	Object getTermosERelacionamentos(boolean isResultadoEstruturado);

	Object getCategorias();

	Object getSubCategorias(String pCatCode);
	

	Object getStartWith(String pSearchString, Long pOffset, int termsPerPage, boolean pProcurarEmNotas, String pAreaTematica, String pIdentificador, String pModificador);

	Object getTermContains(String pSearchString, Long pOffset, int termsPerPage, boolean pProcurarEmNotas, String pAreaTematica, String pIdentificador, String pModificador);

	Object getTermContainsWord(String pSearchString, Long pOffset, int termsPerPage, boolean pProcurarEmNotas, String pAreaTematica, String pIdentificador, String pModificador);

	
	Object getHierarquia(String pTerm, String pRelType);

	Object getTermo(String pTerm, boolean isResultadoEstruturado);

	Object getCodes();

	/*
	Object getTopTermList(String pSQL, String relsBroader, String relsNarrower);
	
	Object getOrphanList(String pSQL, Long pOffset, String relsTerms,int termsPerPage);
	
	Object getTopTerms(String pSearchString, String relsBroader, String relsNarrower);
	
	Object getOrphans(String pSearchString, Long pOffset, String relsTerms, int termsPerPage);
	
	Object getRelContains(String pSearchString, Long pOffset, int termsPerPage);
	
	Object getRelStartsWith(String pSearchString, Long pOffset, int termsPerPage);
	
	Object getRelContainsWord(String pSearchString, Long pOffset, int termsPerPage);
	*/

}

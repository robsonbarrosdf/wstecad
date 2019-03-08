package br.leg.camara.wstecad.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.leg.camara.wstecad.dao.TermoDao;
import br.leg.camara.wstecad.model.Termo;

@RestController
@RequestMapping("wstecad")
public class TecadController {

	@Autowired
	private TermoDao termoDao;
	
	//private final String pLang = "por";

	@GetMapping("/pesquisarTermos")
	public @ResponseBody Object  pesquisarTermos(@RequestParam("tipoPesquisa") String pTipoPesquisa, @RequestParam("textoPesquisado") String pTextoPesquisado, @RequestParam("procurarEmNotas") String pProcurarEmNotas, @RequestParam("offset") Long pOffset, @RequestParam("termosPorPagina") int pTermosPorPagina, @RequestParam("tipoCategoria") String pTipoCategoria, @RequestParam("valorCategoria") String pValorCategoria) {
		//http://localhost:8080/ws-tecad/pesquisarTermos?tipoPesquisa=xxx&textoPesquisado=parlamentar ...
		//tipoPesquisa => startwith | contains | containsword

//		if (pLang == null || pOffset == null) {
//			return null;
//		}
				
		pTipoPesquisa = Objects.toString(pTipoPesquisa, "contains");
		pTextoPesquisado = Objects.toString(pTextoPesquisado, "").replace("'", "''");
		pProcurarEmNotas = Objects.toString(pProcurarEmNotas, "");
		pOffset = pOffset == null ? 1L : pOffset;
		pTermosPorPagina = pTermosPorPagina == 0 ? 25 : pTermosPorPagina;
		pTipoCategoria = Objects.toString(pTipoCategoria, "");
		pValorCategoria = Objects.toString(pValorCategoria, "*");
		
		boolean bProcurarEmNotas = pProcurarEmNotas.toLowerCase().startsWith("s");
		
		Object  result =  null;
		
		String pAreaTematica = "";
		String pIdentificador = "";
		String pModificador = "";
		
		switch (pTipoCategoria.toLowerCase()) {
		case "areatematica":			
			pAreaTematica = pValorCategoria;
			break;
		case "modificador":
			pModificador = "mod";
			break;
		case "identificador":
			pIdentificador = pValorCategoria;
			break;
		}
		
		//Executa a rotina de pesquisa de acordo com os parâmetros recebidos.
		switch (pTipoPesquisa.toLowerCase()) {
		case "startwith":
			
			result = termoDao.getStartWith(pTextoPesquisado, pOffset, pTermosPorPagina, bProcurarEmNotas, pAreaTematica, pIdentificador, pModificador);
			break;
			
		case "contains":
			
			result = termoDao.getTermContains(pTextoPesquisado, pOffset, pTermosPorPagina, bProcurarEmNotas, pAreaTematica, pIdentificador, pModificador);
			break;
			
		case "containsword":
			
			result = termoDao.getTermContainsWord(pTextoPesquisado, pOffset, pTermosPorPagina, bProcurarEmNotas, pAreaTematica, pIdentificador, pModificador);
			break;
			
		default:
			break;
		}
		


		
		return result;
	}	
	
	@GetMapping("/teste")
	public @ResponseBody Object  getTeste() {
		//http://localhost:8080/ws-tecad/categorias
		return "Teste";
	}

	@GetMapping("/categorias")
	public @ResponseBody Object  getCategories() {
		//http://localhost:8080/ws-tecad/categorias
		return termoDao.getCategorias();
	}
	
	@GetMapping("/subcategorias")
	public @ResponseBody Object  getSubCategorias(@RequestParam("codigoCategoria") String pCatCode) {
		//http://localhost:8080/ws-tecad/subCategorias?codigoCategoria=050
		return termoDao.getSubCategorias(pCatCode);
	}

	
	@GetMapping("/termo")
	public @ResponseBody Object  getTermRecord(@RequestParam("termo") String pTerm, @RequestParam("resultadoEstruturado") String pResultadoEstruturado) {
		//http://localhost:8080/ws-tecad/termo?termo=voto
		pResultadoEstruturado = Objects.toString(pResultadoEstruturado, "");
		return termoDao.getTermo(pTerm, pResultadoEstruturado.trim().length()>0);
	}

	@GetMapping("/termos")	
	public @ResponseBody Object  getTermos() {
		//http://localhost:8080/ws-tecad/termos
		//Object  result = termoDao.getAllTermRecords();
		return termoDao.getTermos();
	}
	
	@GetMapping("/termosERelacionamentos")
	public @ResponseBody Object  getTermosERelacionamentos(@RequestParam("resultadoEstruturado") String pResultadoEstruturado) {
		//http://localhost:8080/ws-tecad/termosERelacionamentos
		pResultadoEstruturado = Objects.toString(pResultadoEstruturado, "");
		return termoDao.getTermosERelacionamentos(pResultadoEstruturado.trim().length()>0);
	}	
	
	@GetMapping("/hierarquia")
	public @ResponseBody Object  getHierarchy(@RequestParam("termo") String pTerm, @RequestParam("codigoRelacionamento")  String pRelType) {
		//http://localhost:8080/ws-tecad/hierarquia?termo=voto?codigoRelacionamento=050
		return termoDao.getHierarquia(pTerm,pRelType);
	}
	
	
	@GetMapping("/codes")
	public @ResponseBody Object  getCodes() {
		//http://localhost:8080/ws-tecad/codes
		return termoDao.getCodes();
	}
	
	
	
	/* 
	 * ******************************************************************************************
	 * Métodos allTermRecords e allTermRecordsComplete foram deixados apenas para compatibilidade
	 * ******************************************************************************************
	 */
	
	@GetMapping("/allTermRecords")	
	public @ResponseBody Object  getAllTermRecords() {
		//http://localhost:8080/ws-tecad/allTermRecords
		//Object  result = termoDao.getAllTermRecords();
		return termoDao.getTermos();
	}

	
	@GetMapping("/allTermRecordsComplete")
	public @ResponseBody Object  getAllTermRecordsComplete(@RequestParam("resultadoEstruturado") String pResultadoEstruturado) {
		//http://localhost:8080/ws-tecad/termosERelacionamentos
		pResultadoEstruturado = Objects.toString(pResultadoEstruturado, "");
		return termoDao.getTermosERelacionamentos(pResultadoEstruturado.trim().length()>0);
	}
	
/*	
	@GetMapping("/topTerms")
	public @ResponseBody Object  getTopTerms(@RequestParam("pSearchString") String pSearchString, @RequestParam("relsBroader") String relsBroader, @RequestParam("relsNarrower") String relsNarrower) {
		//http://localhost:8080/ws-tecad/topTerms/por/voto/('070')/('050')
		return termoDao.getTopTerms(pLang,pSearchString,relsBroader,relsNarrower);
	}

	
	@GetMapping("/orphans")
	public @ResponseBody Object  getOrphans(@RequestParam("pSearchString") String pSearchString, @RequestParam("pOffset") Long pOffset, @RequestParam("relsTerms") String relsTerms, @RequestParam("termsPerPage") int termsPerPage) {
		//http://localhost:8080/ws-tecad/orphans/por/voto/1/('050')/1
		return termoDao.getOrphans(pLang,pSearchString,pOffset,relsTerms,termsPerPage);
	}

	
	@GetMapping("/relContains")
	public @ResponseBody Object  getRelContains(@RequestParam("pSearchString") String pSearchString, @RequestParam("pOffset") Long pOffset, @RequestParam("termsPerPage") int termsPerPage) {
		//http://localhost:8080/ws-tecad/termContains/por/voto/1/4
		return termoDao.getRelContains(pLang,pSearchString,pOffset, termsPerPage);
	}
	
	@GetMapping("/relStartsWith")
	public @ResponseBody Object  getRelStartsWith(@RequestParam("pSearchString") String pSearchString, @RequestParam("pOffset") Long pOffset, @RequestParam("termsPerPage")  int termsPerPage) {
		return termoDao.getRelStartsWith(pLang,pSearchString,pOffset,termsPerPage);
	}
	
	@GetMapping("/relContainsWord")
	public @ResponseBody Object  getRelContainsWord(@RequestParam("pSearchString") String pSearchString, @RequestParam("pOffset") Long pOffset, @RequestParam("termsPerPage") int termsPerPage) {
		return termoDao.getRelContainsWord(pLang,pSearchString,pOffset,termsPerPage);
	}
*/

}

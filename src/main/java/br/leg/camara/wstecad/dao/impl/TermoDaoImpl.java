package br.leg.camara.wstecad.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.leg.camara.wstecad.dao.DaoOperations;
import br.leg.camara.wstecad.dao.TermoDao;
import br.leg.camara.wstecad.extractor.TermosResultSetExtractor;
import br.leg.camara.wstecad.model.Termo;

@Service
public class TermoDaoImpl implements TermoDao {
	
	@Autowired
	DaoOperations daoOperations;

	@Override
	public Object  getCategorias() {

		String queryString = 
				"SELECT c.relation_code,c.description "
				+ " FROM mtCodes c "
				+ " WHERE c.type = 'Category'"
				+ " ORDER BY c.relation_code,c.description";

		return daoOperations.executeQuery(queryString);
	}

	@Override
	public Object  getSubCategorias(String pCatCode) {

		String queryString = "SELECT c.relation_code,c.description as description,count(*) as termCount " 
				+ "  FROM mtCodes c LEFT OUTER JOIN mtTerms_attr a "
				+ " ON (a.language_code = c.language_code" 
				+ " AND a.relation_code = c.parent_code" 
				+ " AND a.subcategory_code = c.relation_code)" 
				+ " WHERE c.parent_code   ='" + pCatCode + "'" 
				+ " GROUP BY c.language_code,c.relation_code,c.description" 
				+ " ORDER BY c.relation_code,c.language_code,c.description";

		return daoOperations.executeQuery(queryString);
		
	}

	@Override
	public Object  getHierarquia(String pTerm, String pRelType) {

		String queryString = "SELECT t.term,a.relation_code,a.value,a.node_label " 
							+ "  FROM mtterms t, mtterms_attr a" 
							+ " WHERE t.term          ='" + pTerm.replace("'","''") + "'" 
							+ "   AND a.term_id       = t.term_id" 
							+ "   AND a.relation_code IN ('" + pRelType + "')"  
							+ " ORDER BY a.relation_code,a.node_label,CAST(a.value as varchar)";

		return daoOperations.executeQuery(queryString);
		
	}

	@Override
	public Object  getTermo(String pTerm, boolean isResultadoEstruturado) {
		String queryString = getSQLTerm(pTerm);
		
		if (isResultadoEstruturado) {
			Termo termo = null;
			List<Termo> result = (List<Termo>) daoOperations.executeQuery(queryString, new TermosResultSetExtractor());
			if (result!=null && result.size()>0) termo = result.get(0); 
			return termo;
		} else 
			return daoOperations.executeQuery(queryString);
		
	}

	@Override
	public Object  getTermos() {
	
		String queryString = "select t.term, ta.relation_code, ta.value "
				+ " from mtTerms t "
				+ " left join mtTerms_attr ta on ta.language_code = t.language_code AND ta.term_id = t.term_id AND ta.relation_code = 'USE'"
				+ " order by t.term";
		
		return daoOperations.executeQuery(queryString);
		
	}

	@Override
	public Object  getTermosERelacionamentos(boolean isResultadoEstruturado) {

		String queryString = getSQLTerm(null);

		if (isResultadoEstruturado) 
			return daoOperations.executeQuery(queryString, new TermosResultSetExtractor());
		 else 
			return daoOperations.executeQuery(queryString);
		
	}

	@Override
	public Object getStartWith(String pSearchString, Long pOffset, int termsPerPage, boolean pProcurarEmNotas, String pAreaTematica, String pIdentificador, String pModificador) {
		
		String sCriterioTermo = "";
		String sCriterioNotas = "";
		
		if (pSearchString.isEmpty()) {
			sCriterioTermo = "1 = 1";
		} else {
			
	        sCriterioTermo = " term >= '" + pSearchString + "' AND term <= '" + pSearchString  + "' + CHAR(255) + CHAR(255)";
	        if (pProcurarEmNotas) sCriterioNotas = " substring(value,1,8000) >= '" + pSearchString + "' AND substring(value,1,8000) <= '" + pSearchString + "' + CHAR(255) + CHAR(255)";
			
		}

		return getTermList(sCriterioTermo, sCriterioNotas, pAreaTematica, pIdentificador, pModificador, pOffset, termsPerPage);
	}

	@Override
	public Object  getTermContains(String pSearchString, Long pOffset, int termsPerPage, boolean pProcurarEmNotas, String pAreaTematica, String pIdentificador, String pModificador) {
		String sCriterioTermo = "";
		String sCriterioNotas = "";
		
		if (pSearchString.isEmpty()) {
			sCriterioTermo = "1 = 1";
		} else {
			
			sCriterioTermo = "term like '%" + pSearchString + "%'";
			if (pProcurarEmNotas) sCriterioNotas = "value like '%" + pSearchString + "%'"; 
			
		}

		return getTermList(sCriterioTermo, sCriterioNotas, pAreaTematica, pIdentificador, pModificador, pOffset, termsPerPage);
	}

	@Override
	public Object  getTermContainsWord(String pSearchString, Long pOffset, int termsPerPage, boolean pProcurarEmNotas, String pAreaTematica, String pIdentificador, String pModificador) {
		String sCriterioTermo = "";
		String sCriterioNotas = "";
		
		if (pSearchString.isEmpty()) {
			sCriterioTermo = "1 = 1";
		} else {
			
			sCriterioTermo = " FREETEXT (term, '" + pSearchString + "') AND term like '%" + pSearchString + "%' ";
			if (pProcurarEmNotas) sCriterioNotas = " FREETEXT (value, '" + pSearchString + "') AND value like '%" + pSearchString + "%'"; 
			
		}

		return getTermList(sCriterioTermo, sCriterioNotas, pAreaTematica, pIdentificador, pModificador, pOffset, termsPerPage);
	}	


	@Override
	public Object  getCodes() {
	
		String queryString = "SELECT * from mtCodes WHERE TYPE <>'SCAT' ORDER BY relation_code,language_code,description";

		return daoOperations.executeQuery(queryString);
	}

	private String sqlJoinCategoria(String pAreaTematica, String pIdentificador, String pModificador) {
		String sql = "";
		if(!"".equals(pAreaTematica + pIdentificador + pModificador)) {
	        sql = sql + " \n " + "LEFT JOIN mtTerms_attr categ ON categ.language_code = t.language_code AND categ.term_id = t.term_id";
	        sql = sql + " \n " + "LEFT JOIN mtCodes codeCateg  ON codeCateg.language_code = categ.language_code AND codeCateg.relation_code = categ.relation_code AND codeCateg.relation_code <> 'MOD'";
		}
		return sql;
	}
	
	private String sqlCriterioCategoria(String pAreaTematica, String pIdentificador, String pModificador) {
		String sql = "";

	    if (!pAreaTematica.isEmpty()) {
        	if (pAreaTematica.equals("*")) {
	            //Procura em todas as áreas temáticas
	            sql = sql + " \n " + "AND ISNUMERIC(categ.relation_code) = 1 AND codeCateg.type = 'Category'";
        	} else {
            	if(pAreaTematica.indexOf(",") == -1) {
	                //Procura em UMA área temática
	                sql = sql + " \n " + "AND categ.relation_code = '" + pAreaTematica + "'";
            	} else {
	                //Procura em DUAS OU MAIS áreas temáticas
	                sql = sql + " \n " + "AND categ.relation_code IN ('" + pAreaTematica.replace(",", "','") + "')";
            	}
        	}
	    }
    
	    if (!pIdentificador.isEmpty()) {
	        if (pIdentificador.equals("*")) {
	            //Procura em todos os identificadores
				sql = sql + " \n " + "AND codeCateg.relation_code = 'IDE'";
	        } else {
	            if (pIdentificador.indexOf(",") == -1) {
	                //Procura em UM identificador
	                sql = sql + " \n " + "AND categ.subcategory_code = '" + pIdentificador + "'";
	            } else {
	                //Procura em DOIS OU MAIS identificadores
	                sql = sql + " \n " + "AND categ.subcategory_code IN '" + pIdentificador.replace(",", "','") + "')";
	            }
	        }
	    }
	    
	    if (!pModificador.isEmpty()) {
	        //Se o parâmetro pModificador for diferente de vazio, deve ser obrigatoriamente MOD
	        sql = sql + " \n " + "AND categ.subcategory_code = '" + pModificador + "'";
	    }
    
		return sql;
	}

	private Object  getTermList(String pCriterioSQLTermo, String pCriterioSQLNotas, String pAreaTematica, String pIdentificador, String pModificador, Long pOffset, int termsPerPage) {

		String cteSQL;

	    //Procura texto nos termos
	    cteSQL = " SELECT ";
	    cteSQL = cteSQL + " \n " + "tt.term";
	    cteSQL = cteSQL + " \n " + ",tt.term_id";
	    cteSQL = cteSQL + " \n " + ",tt.language_code";
	    cteSQL = cteSQL + " \n " + ",row_number() over (order by tt.language_code, tt.term) as rownum";
	    cteSQL = cteSQL + " \n " + "FROM (";
	    cteSQL = cteSQL + " \n " + "SELECT DISTINCT ";
	    cteSQL = cteSQL + " \n " + "t.term";
	    cteSQL = cteSQL + " \n " + ",t.term_id";
	    cteSQL = cteSQL + " \n " + ",t.language_code";
	    cteSQL = cteSQL + " \n " + "FROM mtterms t";
	    cteSQL = cteSQL + " \n " + sqlJoinCategoria(pAreaTematica, pIdentificador, pModificador);
	    cteSQL = cteSQL + " \n " + "WHERE t.language_code='POR'";
	    cteSQL = cteSQL + " \n " + "AND " + pCriterioSQLTermo;
	    cteSQL = cteSQL + " \n " + sqlCriterioCategoria(pAreaTematica, pIdentificador, pModificador);
	    
	    //Se for para procurar texto nas notas (NE, NA, NH)
	    if(!pCriterioSQLNotas.isEmpty()) {
	        cteSQL = cteSQL + " \n " + "union";
	        
	        cteSQL = cteSQL + " \n " + "SELECT";
	        cteSQL = cteSQL + " \n " + "t.term";
	        cteSQL = cteSQL + " \n " + ",t.term_id";
	        cteSQL = cteSQL + " \n " + ",t.language_code";
	        cteSQL = cteSQL + " \n " + "FROM (";
	        
	        //Subquery para recuperar termos que possuam NOTAS que atendam ao critério de pesquisa, ou seja, a pesquisa é feita nas NOTAS.
	        cteSQL = cteSQL + " \n " + "    SELECT";
	        cteSQL = cteSQL + " \n " + "    t.term";
	        cteSQL = cteSQL + " \n " + "    ,t.term_id";
	        cteSQL = cteSQL + " \n " + "    ,t.language_code";
	        cteSQL = cteSQL + " \n " + "    FROM mtTerms t";
	        cteSQL = cteSQL + " \n " + "    LEFT JOIN mtTerms_attr ta ON ta.language_code = t.language_code AND ta.term_id = t.term_id";
	        cteSQL = cteSQL + " \n " + "    WHERE t.language_code = 'POR'";
	        cteSQL = cteSQL + " \n " + "    AND ta.relation_code IN ('NE', 'NA', 'NH')";
	        cteSQL = cteSQL + " \n " + "    AND " + pCriterioSQLNotas;
	        
	        cteSQL = cteSQL + " \n " + ") t";
	        cteSQL = cteSQL + " \n " + sqlJoinCategoria(pAreaTematica, pIdentificador, pModificador);
	        cteSQL = cteSQL + " \n " + "    WHERE 1=1";
	        cteSQL = cteSQL + " \n " + sqlCriterioCategoria(pAreaTematica, pIdentificador, pModificador);
	    }
	    
	    cteSQL = cteSQL + " \n " + ") tt";

	    String sqlFinal;
	    
	    sqlFinal = ";WITH cteTERM AS (";
	    sqlFinal = sqlFinal + " \n " + cteSQL;
	    sqlFinal = sqlFinal + " \n " + ")";
	    sqlFinal = sqlFinal + " \n " + "SELECT";
	    sqlFinal = sqlFinal + " \n " + "term";
	    sqlFinal = sqlFinal + " \n " + ",relation_code";
	    sqlFinal = sqlFinal + " \n " + ",value";
	    //sqlFinal = sqlFinal + " \n " + ",ct.language_code";
	    //sqlFinal = sqlFinal + " \n " + ",rownum";
	    //sqlFinal = sqlFinal + " \n " + ",(SELECT MAX(rownum) FROM cteTERM ) as nRows ";
	    sqlFinal = sqlFinal + " \n " + "FROM cteTERM ct";
	    sqlFinal = sqlFinal + " \n " + "LEFT JOIN mtTerms_attr ta2 ON ta2.language_code = ct.language_code AND ta2.term_id = ct.term_id AND ta2.relation_code = 'USE'";
	    sqlFinal = sqlFinal + " \n " + "WHERE 1 = 1";
	    sqlFinal = sqlFinal + " \n " + "AND rownum BETWEEN " + pOffset + " AND " + pOffset + termsPerPage; //+ 1;
	    sqlFinal = sqlFinal + " \n " + "ORDER BY ct.language_code,term;";

		return daoOperations.executeQuery(sqlFinal);
	}
	
	private String getSQLTerm(String termo) {

		String queryString = "with termoCTE as " 
				+ "("
				+ " SELECT "
				+ "		t.term"
				+ "		, relation_code = isnull(cSub.relation_code, cRelation.relation_code)"
				+ "		, value =	case	"
				+ "						when isnull(cSub.relation_code, cRelation.relation_code) in ('NA', 'NE', 'NH', 'FT', 'TG', 'TE', 'TR', 'USE', 'UP') then a.value"
				+ "						else cSub.description "
				+ "					end"
				+ "		, description        =	isnull(cSub.description, cRelation.description)"
				+ "		, parent_category    =	cSuper.relation_code"
				+ "		, description_parent =	cSuper.description "
				+ "		, num_ordem          =	case"
				+ "									when a.relation_code = 'USE' then 20 "
				+ "									when a.relation_code = 'UP'  then 30"

				+ "									when a.relation_code = 'NE'  then 40"
				+ "									when a.relation_code = 'NH'  then 50"
				+ "									when a.relation_code = 'NA'  then 60"
				+ "									when a.relation_code = 'FT'  then 70"

				+ "									when a.relation_code = 'TG'  then 80"
				+ "									when a.relation_code = 'TE'  then 90"
				+ "									when a.relation_code = 'TR'  then 100"

				+ "									else 110"
				+ "								end"
				+ "	FROM mtTerms t "
				+ "	LEFT JOIN mtterms_attr a ON (a.term_id=t.term_id AND a.language_code=t.language_code) "
				+ "	LEFT JOIN mtCodes cSub on cSub.language_code = a.language_code and cSub.relation_code = a.subcategory_code and cSub.parent_code is not null and cSub.parent_code <> 'N/A'"
				+ "	LEFT JOIN mtCodes cSuper on cSuper.language_code = cSub.language_code and cSuper.relation_code = cSub.parent_code and cSuper.parent_code = 'N/A'"
				+ "	LEFT JOIN mtCodes cRelation on cRelation.language_code = a.language_code and cRelation.relation_code = a.relation_code and cRelation.parent_code = 'N/A'"
				+ "	WHERE coalesce(cast(a.value as varchar),'')<>''"
				+ (termo==null || termo.isEmpty() ? "" : "   AND t.term   COLLATE Latin1_general_CI_AS   ='" + termo.replace("'","''")  + "'")
				+ "		) "
				+ "			select" 
				+ "				t.term"
				+ "				, t.relation_code"
				+ "				, t.value"
				+ "				, t.description" 
				+ "				, t.parent_category"
				+ "				, t.description_parent"
				+ "			from termoCTE t"
				+ "			ORDER BY t.term, t.num_ordem, CAST(t.value as varchar(4000))";
		
		return queryString;
		
	}

	/*

	@Override
	public Object  getRelContains(String pSearchString, Long pOffset, int termsPerPage) {
		if (pLang == null || pSearchString == null) {
			return null;
		}

		String queryString = "";

		String theCode, queryAux;
		int posColon;
		posColon = pSearchString.indexOf(":", 0) + 1;

		if (posColon > 1) {

			theCode = pSearchString.substring(0, Math.min(posColon - 1, pSearchString.length()));
			queryAux = pSearchString.substring(posColon + 1).trim();
		} else {
			theCode = "";
			queryAux = pSearchString;
		}

		queryString = ",mtTerms_attr a " + " WHERE t.language_code='" + pLang + "' " + " AND a.term_id= t.term_id"
				+ " AND a.language_code= t.language_code";

		if (theCode != "") {
			queryString = queryString + " AND a.relation_code='" + theCode + "' ";
		}
		if (queryAux != "") {
			queryString = queryString + " AND a.value LIKE '%" + queryAux + "%'";
		}

		return null;
		//return getTermList(pCriterioSQLTermo, "", "", "", "", pOffset, termsPerPage);

	}

	@Override
	public Object  getRelStartsWith(String pSearchString, Long pOffset, int termsPerPage) {
		if (pLang == null || pSearchString == null) {
			return null;
		}

		String queryString = "";
		
		String theCode,queryAux;
		int posColon;
		queryString =  ",mtTermsattr a WHERE a.languagecode='" + pLang + "'";
		
		posColon =  pSearchString.indexOf(":", 0) + 1;
		
		if ( posColon < 2) { 
			posColon = pSearchString.indexOf(" ", 0) + 1;
		}

		if (posColon>1) {
			
			theCode = pSearchString.substring(0, Math.min(posColon - 1, pSearchString.length()));
			queryAux	= pSearchString.substring(posColon + 1).trim();

			queryString = queryString + " AND a.relation_code='" + theCode + "' " ;
			if (queryAux != "") {
				queryString = queryString + " AND rtrim(cast(a.value as varchar(80))) >= '" + queryAux + "' AND rtrim(cast(a.value as varchar(80))) <='" + queryAux + "ZZZZZZ'";
			}
			}
		else {
			queryString = queryString + " AND rtrim(cast(a.value as varchar(80))) >= '" + pSearchString + "' AND rtrim(cast(a.value as varchar(80))) <='" + pSearchString + "ZZZZZZ'";
		}

		queryString =  queryString + " AND t.language_code='" + pLang + "' " 
				+ " AND t.term_id= a.term_id" 
				+ " AND t.language_code= a.language_code"; 
		
		return null;
		//return getTermList(queryString,pOffset,termsPerPage);

		
	}

	@Override
	public Object  getRelContainsWord(String pSearchString, Long pOffset, int termsPerPage) {
		if (pLang == null || pSearchString == null) {
			return null;
		}

		String queryString = "";
		
		String quaryAux,theCode;
		int posColon;
		
		
		posColon = pSearchString.indexOf(":", 0) + 1;

		if (posColon>1) {
			theCode = pSearchString.substring(0, Math.min(posColon - 1, pSearchString.length()));
			quaryAux	= pSearchString.substring(posColon + 1).trim();
			}
		else{
			theCode = "";
			quaryAux	= pSearchString;
		}

		queryString = " , mtTerms_attr a " 
			+ " WHERE t.language_code = '" + pLang + "'" 
			+ "   AND a.language_code = t.language_code" 
			+ "   AND a.term_id       = t.term_id " ;

		if (theCode !="") {
			queryString = queryString + " AND a.relation_code='" + theCode + "'";
		 }

		if (quaryAux != "") {
			queryString = queryString + " AND FREETEXT(a.value,'" + quaryAux + "')" ;
		 }

		return null;
		//return getTermList(queryString,pOffset,termsPerPage);

	}
	
	@Override
	public Object  getTopTermList(String pSQL, String relsBroader,
			String relsNarrower) {

		String queryString = "";

		queryString = "SELECT t.term,'' as relation_code,'' as value " + ", coalesce(sum(case when hier.relation_code IN "
				+ relsBroader + " then 1 else 0 end),0) as btCount" + ", coalesce(sum(case when hier.relation_code IN "
				+ relsNarrower + " then 1 else 0 end),0) as ntCount"
				+ " FROM mtterms t LEFT OUTER JOIN mtTerms_attr hier ON (hier.term_id = t.term_id) " + pSQL
				+ " GROUP BY t.term " 
				+ " HAVING coalesce(sum(case when hier.relation_code IN " + relsNarrower + "then 1 else 0 end),0)>0"
				+ " AND coalesce(sum(case when hier.relation_code IN " + relsBroader + " then 1 else 0 end),0)=0 "
				+ " ORDER BY t.term";

		return daoOperations.executeQuery(queryString);
	}

	@Override
	public Object  getOrphanList(String pSQL, Long pOffset, String relsTerms, int termsPerPage) {

		String queryString = "";

		queryString = ";WITH cteList AS" + "(" + "SELECT t.term, '' as relation_code, '' as value "
				+ ", coalesce(sum(case when rels.relation_code IN " + relsTerms + " then 1 else 0 end),0) as relCount  "
				+ ", row_number() over (order by t.term) as rownum " + "FROM mtterms t  "
				+ "LEFT OUTER JOIN mtTerms_attr rels ON (rels.term_id = t.term_id)  " + "WHERE t.language_code='POR' "
				+ "GROUP BY t.term " + "HAVING coalesce(sum(case when rels.relation_code IN " + relsTerms
				+ " then 1 else 0 end),0)=0  " + ") " + "SELECT TOP " + termsPerPage + 1
				+ " term, relation_code, value, relCount, rownum, (SELECT MAX(rownum) FROM cteLIST ) as nRows  "
				+ "FROM cteList " + "WHERE rownum >= " + pOffset + "ORDER BY term";

		return daoOperations.executeQuery(queryString);
	}

	@Override
	public Object  getTopTerms(String pSearchString, String relsBroader, String relsNarrower) {
		if (pLang == null) {
			return null;
		}

		String queryString = "";

		queryString = " WHERE t.language_code='" + pLang + "' ";

		if (pSearchString != "") {
			queryString = queryString + " AND t.term >='" + pSearchString + "'" + " AND t.term <='" + pSearchString + "' + char(255) + char(255)";
		}

		return getTopTermList(queryString, relsBroader, relsNarrower);
	}

	@Override
	public Object  getOrphans(String pSearchString, Long pOffset, String relsTerms, int termsPerPage) {
		if (pLang == null || pOffset == null) {
			return null;
		}

		String queryString = " WHERE t.language_code='" + pLang + "' ";

		if (pSearchString != "") {
			queryString = queryString + " AND t.term >='" + pSearchString + "' AND t.term <='" + pSearchString + "' + char(255) + char(255)";
		}

		return getOrphanList(queryString, pOffset, relsTerms, termsPerPage);
	}
			 
	 */
}

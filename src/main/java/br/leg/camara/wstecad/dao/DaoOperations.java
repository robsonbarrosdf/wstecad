package br.leg.camara.wstecad.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.ResultSetExtractor;

public interface DaoOperations<T> {

	public List<Map<String, Object>> executeQuery(String sql);
	
	public List<T> executeQuery(String sql, ResultSetExtractor<T> rse);
	
}

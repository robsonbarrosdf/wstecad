package br.leg.camara.wstecad.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Service;

import br.leg.camara.wstecad.dao.DaoOperations;

@Service
public class DaoOperationsImpl<T> implements DaoOperations<T> {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public List<Map<String, Object>> executeQuery(String sql) {
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }


	@Override
	public List<T> executeQuery(String sql, ResultSetExtractor<T> rse) {
        //List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, rse);
        List<T> objectList = (List<T>) jdbcTemplate.query(sql, rse);
        return objectList;
	}    

    
}

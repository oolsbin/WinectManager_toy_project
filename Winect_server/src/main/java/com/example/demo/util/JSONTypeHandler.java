package com.example.demo.util;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

@MappedTypes(Object.class)
public class JSONTypeHandler extends BaseTypeHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(JSONTypeHandler.class);
    
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
            throws SQLException {
        // JDBC type is required
        String p = new Gson().toJson(parameter);
        ps.setObject(i, p);
    }

    @Override
    public JsonObject getNullableResult(ResultSet rs, String columnName) throws SQLException { 
        Object d = rs.getObject(columnName); 

        if(d == null) 
            return new JsonObject(); 
        else {
            PGobject pGobject = (PGobject) d;
            return new Gson().fromJson(pGobject.getValue(), JsonObject.class);
        }
        // return rs.getString(columnName);
    } 
    
    @Override 
    public JsonObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException { 
        Object d = rs.getObject(columnIndex); 

        if(d == null) 
            return new JsonObject(); 
        else {
            PGobject pGobject = (PGobject) d;
            return new Gson().fromJson(pGobject.getValue(), JsonObject.class);
        }
        // return rs.getString(columnIndex);
    } 
    
    @Override 
    public JsonObject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException { 
        Object d = cs.getObject(columnIndex); 
        
        if(d == null) 
            return new JsonObject(); 
        else {
            PGobject pGobject = (PGobject) d;
            return new Gson().fromJson(pGobject.getValue(), JsonObject.class);
        }
        // return cs.getString(columnIndex);
    }
}
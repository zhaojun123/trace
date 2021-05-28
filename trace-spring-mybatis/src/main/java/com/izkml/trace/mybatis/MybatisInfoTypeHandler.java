package com.izkml.trace.mybatis;

import com.izkml.trace.core.InfoTypeHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

public class MybatisInfoTypeHandler implements InfoTypeHandler<MybatisTags> {

    private MappedStatement mappedStatement;
    private Object params;
    private MybatisTags mybatisTags;

    public MybatisInfoTypeHandler(MappedStatement mappedStatement,Object params){
        this.mappedStatement = mappedStatement;
        this.params = params;
        this.mybatisTags = new MybatisTags();
    }

    @Override
    public MybatisTags simple() {
        mybatisTags.setSqlId(mappedStatement.getId());
        if(mappedStatement.getSqlCommandType()!=null){
            mybatisTags.setSqlType(mappedStatement.getSqlCommandType().name());
        }
        return mybatisTags;
    }

    @Override
    public MybatisTags normal() {
        return simple();
    }

    @Override
    public MybatisTags full() {
        normal();
        mybatisTags.setParams(params);
        SqlSource sqlSource = mappedStatement.getSqlSource();
        BoundSql boundSql = sqlSource.getBoundSql(params);
        mybatisTags.setSql(boundSql.getSql());
        return mybatisTags;
    }
}

package com.pht.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.util.UUID;

@MappedJdbcTypes(JdbcType.OTHER)
@MappedTypes(Long.class)
public class LongTypeHandler extends BaseTypeHandler<Long> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Long aLong, JdbcType jdbcType) throws SQLException {
        if (aLong == null) {
            preparedStatement.setNull(i, Types.VARCHAR);
        } else {
            preparedStatement.setString(i, aLong.toString());
        }
    }

    @Override
    public Long getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value != null ? Long.valueOf(value) : null;
    }

    @Override
    public Long getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value != null ? Long.valueOf(value) : null;
    }

    @Override
    public Long getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value != null ? Long.valueOf(value) : null;
    }
}

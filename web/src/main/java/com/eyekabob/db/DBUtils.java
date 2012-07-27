package com.eyekabob.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * © Copyright 2012 Brien Coffield
 * All rights reserved
 * <p/>
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
public class DBUtils {
    private static final String USER = "eyekabob";
    private static final String PASS = "privateeye";
    private static final String DB_NAME = "eyekabob";
    private static final String DB_HOST = "localhost";
    private static final String JDBC = "jdbc";
    private static final String MYSQL = "mysql";
    private static final String CONNECTION = JDBC + ":" + MYSQL + "://" + DB_HOST + "/" + DB_NAME;

    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(CONNECTION, USER, PASS);
    }

    public static List<Map<String, Object>> query(String preparedStatementStr, Object... preparedArgs) throws SQLException {
        Connection conn = getConn();
        PreparedStatement preparedStatement = conn.prepareStatement(preparedStatementStr);
        // Prepared statement args are 1-based, so start at 1.
        int argCount = 1;
        for (Object arg : preparedArgs) {
            if (arg instanceof Integer) {
                preparedStatement.setInt(argCount, (Integer)arg);
            }
            argCount++;
        }

        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<String, Object>();
            for (int colIndex = 0; colIndex < columnCount; colIndex++) {
                String columnName = metaData.getColumnName(colIndex);
                row.put(columnName, resultSet.getObject(colIndex));
            }
            result.add(row);
        }

        return result;
    }
}

package com.eyekabob.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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

    /**
     * Query for a prepared statement given an array of arguments to the statement.
     * The preparedStatementStr should look something like:
     * "SELECT * FROM MY_TABLE WHERE ID=?"
     *
     * Given the above example and assuming ID is an int, the preparedArgs should be an
     * array containing one int:
     * [12345]
     *
     * @param preparedStatementStr
     * @param preparedArgs
     * @return A list of rows, represented as maps. Each row has a column name key and an Object value.
     * @throws SQLException
     */
    public static List<Map<String, Object>> query(String preparedStatementStr, Object... preparedArgs) throws SQLException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        Connection conn = getConn();
        PreparedStatement preparedStatement = conn.prepareStatement(preparedStatementStr);
        int argCount = 1;
        for (Object arg : preparedArgs) {
            if (arg instanceof Integer) {
                preparedStatement.setInt(argCount, (Integer)arg);
            }
            else if (arg instanceof String) {
                preparedStatement.setString(argCount, (String)arg);
            }
            argCount++;
        }

        ResultSet resultSet = preparedStatement.executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        while (resultSet.next()) {
            Map<String, Object> row = new HashMap<String, Object>();
            for (int colIndex = 1; colIndex < columnCount + 1; colIndex++) {
                String columnName = metaData.getColumnName(colIndex);
                row.put(columnName, resultSet.getObject(colIndex));
            }
            result.add(row);
        }

        return result;
    }
}

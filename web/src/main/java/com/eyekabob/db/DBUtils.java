package com.eyekabob.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

    public static final Connection getConn() throws SQLException {
        return DriverManager.getConnection(CONNECTION, USER, PASS);
    }
}

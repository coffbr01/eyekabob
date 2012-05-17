package com.eyekabob;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class Main {
    private static final String API = "api";
    private static final String ARTIST = "artist";
    private static final String METHOD = "method";
    private static final String QUERY = "query";
    private static final String GET = "GET";
    private static final String POST = "POST";

    public static JSONObject processRequest(HttpServletRequest request) {
        JSONObject result = null;

        try {
            result = new JSONObject();
            String method = request.getMethod(); // GET, POST, etc.
            String apiParam = request.getParameter(API);

            Connection conn = null;
            try {
                conn = getConnection();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }

            if (ARTIST.equals(apiParam)) {
                if (POST.equals(method)) {
                    String genre = request.getParameter("genre");
                    String name = request.getParameter("name");
                    String url = request.getParameter("url");
                    String bio = request.getParameter("bio");
                    String query = "INSERT INTO artist (genre, name, url, bio) VALUES ('" + genre + "', '" + name + "', '" + url + "', '" + bio + "')";
                    Statement stmt = null;
                    try {
                        stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                    }
                    catch (SQLException e ) {
                        e.printStackTrace();
                    }
                    finally {
                        if (stmt != null) {
                            try {
                                stmt.close();
                            }
                            catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                else {
                    // Assume GET.
                    String queryParam = request.getParameter(QUERY);
                    Statement stmt = null;
                    String query = "SELECT * FROM artist WHERE name LIKE '" + queryParam + "'";
                    try {
                        stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query);
                        JSONArray artists = new JSONArray();
                        result.put("artists", artists);
                        while (rs.next()) {
                            String name = rs.getString("name");
                            JSONObject artist = new JSONObject();
                            artist.put("name", name);
                            artists.add(artist);
                        }
                    }
                    catch (SQLException e ) {
                        e.printStackTrace();
                    }
                    finally {
                        if (stmt != null) {
                            try {
                                stmt.close();
                            }
                            catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            result.put(API, apiParam);
            result.put(METHOD, method);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static Connection getConnection() throws SQLException {
        Properties connectionProps = new Properties();
        connectionProps.put("user", "eyekabob");
        connectionProps.put("password", "privateeye");

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/eyekabob", connectionProps);
    }
}

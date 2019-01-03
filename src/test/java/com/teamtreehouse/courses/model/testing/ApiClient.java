package com.teamtreehouse.courses.model.testing;

import sun.misc.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ApiClient {
    private String server;

    public ApiClient(String server) {
        this.server = server;
    }
//GET Request method
    public ApiResponse request(String method, String uri){
        return request(method, uri, null);
    }
//POST Request method
    public ApiResponse request(String method, String uri, /*course to be posted*/String requetBody) {
        try{
            URL url = new URL(server + uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("Content-Type", "application/json");
            if (requetBody != null){
                connection.setDoInput(true);
                try (OutputStream outputStream = connection.getOutputStream()){
                    outputStream.write(requetBody.getBytes("UTF-8"));
                }
            }
            connection.connect();
            InputStream inputStream = connection.getResponseCode() < 400 /*if responseCode < 400*/?
                    /*do this*/connection.getInputStream() : /*else this*/connection.getErrorStream();
                    String body = spark.utils.IOUtils.toString(inputStream);
                    return new ApiResponse(connection.getResponseCode(), body);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Whoops! Connection error");
        }
    }
}

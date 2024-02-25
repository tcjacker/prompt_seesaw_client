package com.neure.agent.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HttpRequestClient
 *
 * @author tc
 * @date 2024-02-25 15:28
 */
public class HttpRequestClient {

    public static String sendRequest(String urlString, String method, String payload) {
        BufferedReader in = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

            // For methods that send data, configure the connection to output mode and write the payload
            if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) {
                connection.setDoOutput(true); // For sending request body
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(payload.getBytes(), 0, payload.length());
                }
            }

            // For all methods, read the response
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            return response.toString();
        } catch (Exception ex) {
            return "Error: " + ex.getMessage();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }
}

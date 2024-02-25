package com.neure.agent.client;

import com.neure.agent.model.Response;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * HttpRequestClient
 *
 * @author tc
 * @date 2024-02-25 15:28
 */
@Slf4j
public class HttpRequestClient {

    // 发送GET请求
    public static String sendGet(String url)  {
        URL obj = null;
        try {
            obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode != 200){
                return Response.buildError();
            }

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                return response.toString();
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return Response.buildError();

    }

    // 发送POST请求
    public static String sendPost(String url, String urlParameters) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
            wr.writeBytes(urlParameters);
            wr.flush();
        }

        int responseCode = connection.getResponseCode();
        System.out.println("POST Response Code :: " + responseCode);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            return response.toString();
        }
    }
}

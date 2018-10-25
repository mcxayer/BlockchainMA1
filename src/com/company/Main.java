package com.company;

import java.util.Base64;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.*;
import java.io.*;
/*
Created 25-10-2018
Created by Niels Bonde Nielsen
 */


public class Main {

    public static String address;

    public static void main(String[] args) throws IOException, ParseException {

        URL url = new URL("http://127.0.0.1:18443");

        System.out.println("Commands:" + "\n" + "get balance" + "\n" +
                "get new address" + "\n" + "send bitcoins" + "\n" + "unspent transactions");

        Scanner scanner = new Scanner(System.in);

        String command;
        while ((command = scanner.nextLine()) != null) {
            String json;
            switch (command) {
                case "get balance":
                    json = "{\"method\": \"getbalance\"}";
                    System.out.println(createConnection(url, json));
                    break;
                case "get new address":
                    json = "{\"method\": \"getnewaddress\"}";
                    address = createConnection(url, json);
                    System.out.println(address);
                case "send bitcoins":
                    System.out.println("Amount to send");
                    int amount = scanner.nextInt();
                    json = "{\"method\": \"sendtoaddress\"," + "\"params\": [\"" + address + "\"," + amount + "] }";
                    System.out.println(createConnection(url, json));
                case "unspent transactions":
                    json = "{ \"method\": \"listunspent\", \"params\": [0]}";
                    System.out.println(createConnection(url, json));
            }
        }

        scanner.close();
    }


    public static String createConnection(URL url, String json) throws IOException, ParseException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/plain");
        connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString(("user:pass").getBytes()));
        connection.setDoOutput(true);

        DataOutputStream outStream = new DataOutputStream(
                connection.getOutputStream());
        outStream.writeBytes(json);
        outStream.close();

        InputStream inStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        StringBuilder result = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            result.append(line);
        }

        reader.close();

        JSONParser parser = new JSONParser();
        JSONObject finalResult = (JSONObject) parser.parse(result.toString());

        return finalResult.get("result").toString();
    }
}

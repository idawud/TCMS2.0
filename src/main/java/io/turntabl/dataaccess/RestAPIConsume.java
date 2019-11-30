package io.turntabl.dataaccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RestAPIConsume {
    public static Optional<List<Client>> getClients(String url){
        String location = url;
        var client = HttpClient.newBuilder().build();
        URI uri = null;
        try {
            uri = new URI(location);
        } catch (
                URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
        }
        var req = HttpRequest.newBuilder(uri).build();

        try {
            var response = client.send(req, HttpResponse.BodyHandlers.ofString(Charset.defaultCharset()));
            var jsonData = jsonStringToClientObject(response.body());
            return Optional.of(jsonData);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private static List<Client> jsonStringToClientObject(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            CollectionType collectionType = typeFactory.constructCollectionType(
                    List.class, Client.class);
            return objectMapper.readValue(json, collectionType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Client parseJsonToMap(String json) {
        var mapper = new ObjectMapper();
        var typeRef = new TypeReference<HashMap<String,Object>>() {};

        try {
            Map<String, Object> mJson = mapper.readValue(json, typeRef);
            Client client = new Client();
            client.setName((String) mJson.get("name"));
            client.setAddress((String) mJson.get("address"));
            client.setTelephoneNumber((String) mJson.get("telephoneNumber"));
            client.setEmail((String) mJson.get("email"));
            return client;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static boolean post(String uri, String data) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(data))
                .build();

        var response = client.send(request, HttpResponse.BodyHandlers.ofString(Charset.defaultCharset()));
        return (response.statusCode() == 200);
    }

    public static Optional<Client> delete(String uri) throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                //.header("Content-Type", "application/json")
                .DELETE()
                .build();
        try {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString(Charset.defaultCharset()));
            var jsonData = parseJsonToMap(response.body());
            return Optional.of(jsonData);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static String clientObjectToJsonString(Client client) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(client);
    }
}

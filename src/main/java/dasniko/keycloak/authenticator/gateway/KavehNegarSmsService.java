package dasniko.keycloak.authenticator.gateway;

import okhttp3.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KavehNegarSmsService {

    private static final String API_URL = "https://api.kavehnegar.com/v1/{API_KEY}/verify/lookup.json";
    private final String apiKey;

    private final OkHttpClient httpClient = new OkHttpClient();

    public KavehNegarSmsService() {
        Properties prop = new Properties();
        try (InputStream input = KavehNegarSmsService.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Unable to find config.properties");
            }
            //load a properties file from class path
            prop.load(input);

            //get the property value
            apiKey = prop.getProperty("api_key");
        } catch (IOException ex) {
            throw new RuntimeException("Error reading from config.properties", ex);
        }
    }

    public void send(String phoneNumber, String token) throws IOException {
        send(phoneNumber, token, "password-change-3");
    }

    public void send(String phoneNumber, String token, String template) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("receptor", phoneNumber)
                .add("token", token)
                .add("template", template)
                .build();

        Request request = new Request.Builder()
                .url(API_URL.replace("{API_KEY}", apiKey))
                .post(formBody)
                .build();

        Response response = httpClient.newCall(request).execute();

        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
    }
}

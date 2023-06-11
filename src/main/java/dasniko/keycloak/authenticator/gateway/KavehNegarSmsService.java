package dasniko.keycloak.authenticator.gateway;

import okhttp3.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import java.util.Map;

public class KavehNegarSmsService implements SmsService {

	// private static final String API_URL = "https://api.kavehnegar.com/v1/{API_KEY}/verify/lookup.json";
	private static final String API_URL = "https://api.kavenegar.com/v1/{API-KEY}/sms/send.json";
	private final String apiKey;

	private final OkHttpClient httpClient = new OkHttpClient();

	public KavehNegarSmsService(Map<String, String> config) {
		apiKey = config.get("API_KEY");
		if (apiKey == null) {
			throw new RuntimeException("API_KEY environment variable is not set");
		}
	}

	public void send(String phoneNumber, String message) {
		try {
			send(phoneNumber, message, "password-change-3");
		} catch (IOException e) {
			throw new RuntimeException("Error sending SMS", e);
		}
	}

	public void send(String phoneNumber, String token, String template) throws IOException {
		RequestBody formBody = new FormBody.Builder()
				.add("receptor", phoneNumber)
				.add("message", token)
				// .add("template", template)
				.build();

		Request request = new Request.Builder()
				.url(API_URL.replace("{API_KEY}", apiKey))
				.post(formBody)
				.build();

		Response response = httpClient.newCall(request).execute();

		if (!response.isSuccessful())
			throw new IOException("Unexpected code " + response);
	}
}

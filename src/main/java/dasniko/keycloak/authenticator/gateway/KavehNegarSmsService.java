package dasniko.keycloak.authenticator.gateway;

import okhttp3.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import java.util.Map;
import org.jboss.logging.Logger;

public class KavehNegarSmsService implements SmsService {

	private static final Logger LOG = Logger.getLogger(SmsServiceFactory.class);
	private static final String API_URL = "https://api.kavehnegar.com/v1/{API_KEY}/verify/lookup.json";
	private final String apiKey;
	private final String template;

	private final OkHttpClient httpClient = new OkHttpClient();

	public KavehNegarSmsService(Map<String, String> config) {
		apiKey = config.get("API_KEY");
		template = config.get("sms_template");
		if (apiKey == null) {
			throw new RuntimeException("API_KEY environment variable is not set");
		}
	}

	public void send(String phoneNumber, String message) {
		try {
			LOG.warn(String.format("***** KavehNeger Debug ***** url: %s, phone: %s, token: %s, template: %s", API_URL.replace("{API_KEY}", apiKey),  phoneNumber, message, template));
			send(phoneNumber, message, template);
		} catch (IOException e) {
			LOG.warn(String.format("***** KavehNeger Debug ***** sending sms error: %s", e.getMessage()));
			throw new RuntimeException("Error sending SMS", e);
		}
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

		if (!response.isSuccessful())
			throw new IOException("Unexpected code " + response);
	}
}

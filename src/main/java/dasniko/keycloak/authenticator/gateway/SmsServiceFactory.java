package dasniko.keycloak.authenticator.gateway;

import org.jboss.logging.Logger;

import java.util.Map;

public class SmsServiceFactory {

    private static final Logger LOG = Logger.getLogger(SmsServiceFactory.class);

    public static SmsService get(Map<String, String> config) {
        if (Boolean.parseBoolean(config.getOrDefault("simulation", "false"))) {
            return (phoneNumber, message) ->
                LOG.warn(String.format("***** SIMULATION MODE ***** Would send SMS to %s with text: %s", phoneNumber, message));
        } else {
			String serviceType = Optional.ofNullable(System.getenv("SMS_SERVICE")).orElse("aws");

			if ("kavehnegar".equals(serviceType)) {
				return new KavehNegarSmsService();
			} else if ("aws".equals(serviceType)) {
				return new AwsSmsService();
			} else {
				throw new IllegalArgumentException("Unsupported sms_service: " + serviceType);
			}
        }
    }
}

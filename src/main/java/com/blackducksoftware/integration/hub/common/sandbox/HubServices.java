package com.blackducksoftware.integration.hub.common.sandbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.synopsys.integration.blackduck.configuration.HubServerConfig;
import com.synopsys.integration.blackduck.configuration.HubServerConfigBuilder;
import com.synopsys.integration.blackduck.rest.BlackduckRestConnection;
import com.synopsys.integration.blackduck.service.HubServicesFactory;
import com.synopsys.integration.exception.EncryptionException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class HubServices {
    private final Logger logger = LoggerFactory.getLogger(HubServices.class);

    public HubServicesFactory createHubServicesFactory() throws EncryptionException {
        final IntLogger intLogger = createIntLogger();
        final HubServerConfig hubServerConfig = createHubServerConfig(intLogger);
        return createHubServicesFactory(intLogger, hubServerConfig);
    }

    public IntLogger createIntLogger() {
        final IntLogger intLogger = new Slf4jIntLogger(logger);
        return intLogger;
    }

    public HubServerConfig createHubServerConfig(final IntLogger intLogger) {
        final HubServerConfigBuilder hubServerConfigBuilder = new HubServerConfigBuilder();
        //        hubServerConfigBuilder.setUrl(System.getenv().get("HUB_QA_URL"));
        //        hubServerConfigBuilder.setUrl("https://bizdevhub.blackducksoftware.com");
        //        hubServerConfigBuilder.setApiToken("MTYyNzQxY2ItODJjZS00Mzc2LTk4ZmItYmNhYjgzZWJkNWU0OmVjZDhjOGQ1LThmMGMtNDBjYS05NDFjLThkOWJlYjE1NDQ5ZA==");
        //        hubServerConfigBuilder.setUrl(System.getenv().get("INT-HUB02_URL"));
        hubServerConfigBuilder.setUrl(System.getenv().get("INT-HUB04_URL"));
        //        hubServerConfigBuilder.setUrl(System.getenv().get("BLACKDUCK_HUB_URL"));
        //        hubServerConfigBuilder.setApiToken(System.getenv().get("BLACKDUCK_HUB_API_TOKEN"));
        hubServerConfigBuilder.setUsername(System.getenv().get("BLACKDUCK_HUB_USERNAME"));
        hubServerConfigBuilder.setPassword(System.getenv().get("BLACKDUCK_HUB_PASSWORD"));
        hubServerConfigBuilder.setTimeout(120);
        hubServerConfigBuilder.setTrustCert(true);
        hubServerConfigBuilder.setLogger(intLogger);

        final HubServerConfig hubServerConfig = hubServerConfigBuilder.build();
        return hubServerConfig;
    }

    public HubServicesFactory createHubServicesFactory(final IntLogger intLogger, final HubServerConfig hubServerConfig) throws EncryptionException {
        final BlackduckRestConnection blackduckRestConnection = hubServerConfig.createRestConnection(intLogger);

        final Gson gson = HubServicesFactory.createDefaultGson();
        final JsonParser jsonParser = HubServicesFactory.createDefaultJsonParser();
        final HubServicesFactory hubServicesFactory = new HubServicesFactory(gson, jsonParser, blackduckRestConnection, intLogger);

        return hubServicesFactory;
    }

}

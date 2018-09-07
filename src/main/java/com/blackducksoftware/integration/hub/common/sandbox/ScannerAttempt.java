package com.blackducksoftware.integration.hub.common.sandbox;

import java.io.File;

import com.synopsys.integration.blackduck.configuration.HubServerConfig;
import com.synopsys.integration.blackduck.signaturescanner.ScanJob;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobBuilder;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobManager;
import com.synopsys.integration.blackduck.signaturescanner.command.ScanTarget;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.PrintStreamIntLogger;

public class ScannerAttempt {
    private static final HubServices hubServices = new HubServices();

    public static void main(final String[] args) throws Exception {
        final File installPath = new File("/Users/ekerwin/working/scan_install");
        final File outputPath = new File("/Users/ekerwin/working/scan_output");
        final String targetPath = "/Users/ekerwin/Documents/source/integration/libraries/hub-common";
        final String projectName = "ek-hub-common";
        final String projectVersionName = "scanner-attempt-1";

        final IntLogger logger = new PrintStreamIntLogger(System.out, LogLevel.INFO);
        final HubServerConfig hubServerConfig = hubServices.createHubServerConfig(logger);
        final ScanJobManager scanManager = ScanJobManager.createDefaultScanManager(logger, hubServerConfig);

        // todo consider turning file to string
        final ScanTarget scanTarget = ScanTarget.createBasicTarget(targetPath);

        final ScanJobBuilder scanJobBuilder = new ScanJobBuilder();
        scanJobBuilder.projectAndVersionNames(projectName, projectVersionName).fromHubServerConfig(hubServerConfig).installDirectory(installPath).outputDirectory(outputPath).addTarget(scanTarget);
        final ScanJob scanJob = scanJobBuilder.build();

        scanManager.executeScans(scanJob);
    }

}

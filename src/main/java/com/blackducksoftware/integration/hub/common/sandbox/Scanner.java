package com.blackducksoftware.integration.hub.common.sandbox;

import java.io.File;
import java.util.List;

import com.synopsys.integration.blackduck.configuration.HubServerConfig;
import com.synopsys.integration.blackduck.rest.BlackduckRestConnection;
import com.synopsys.integration.blackduck.signaturescanner.ScanJob;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobBuilder;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobManager;
import com.synopsys.integration.blackduck.signaturescanner.ScanJobOutput;
import com.synopsys.integration.blackduck.signaturescanner.command.ScanCommandOutput;
import com.synopsys.integration.blackduck.signaturescanner.command.ScanCommandRunner;
import com.synopsys.integration.blackduck.signaturescanner.command.ScanPathsUtility;
import com.synopsys.integration.blackduck.signaturescanner.command.ScanTarget;
import com.synopsys.integration.blackduck.signaturescanner.command.ScannerZipInstaller;
import com.synopsys.integration.blackduck.summary.Result;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.util.CleanupZipExpander;
import com.synopsys.integration.util.IntEnvironmentVariables;
import com.synopsys.integration.util.OperatingSystemType;

public class Scanner {
    private static final HubServices hubServices = new HubServices();

    public static void main(final String[] args) throws Exception {
        final String installPath = "/Users/ekerwin/working/scan_install";
        //final String installPath = "/Users/ekerwin/working/scan_install/Black_Duck_Scan_Installation/scan.cli-4.8.0";
        final String outputPath = "/Users/ekerwin/working/scan_output";
        final String targetPath = "/Users/ekerwin/Documents/source/integration/libraries/hub-common";
        final String projectName = "ek-hub-common";

        final Scanner scanner = new Scanner();
        scanner.doFullScanJob(installPath, outputPath, targetPath, projectName, "revised-scan-04");
    }

    public void doFullScanJob(final String installPath, final String outputPath, final String targetPath, final String projectName, final String versionName) throws Exception {
        final IntLogger logger = hubServices.createIntLogger();
        final HubServerConfig hubServerConfig = hubServices.createHubServerConfig(logger);
        final String blackDuckServerUrl = hubServerConfig.getHubUrl().toString();
        final BlackduckRestConnection blackduckRestConnection = hubServerConfig.createRestConnection(logger);
        final OperatingSystemType operatingSystemType = OperatingSystemType.determineFromSystem();
        final IntEnvironmentVariables intEnvironmentVariables = new IntEnvironmentVariables();
        final CleanupZipExpander cleanupZipExpander = new CleanupZipExpander(logger);
        final ScanPathsUtility scanPathsUtility = new ScanPathsUtility(logger, operatingSystemType);
        final ScanCommandRunner scanJobRunner = new ScanCommandRunner(logger, intEnvironmentVariables, scanPathsUtility);

        final ScannerZipInstaller scannerZipInstaller = new ScannerZipInstaller(logger, blackduckRestConnection, cleanupZipExpander, scanPathsUtility, blackDuckServerUrl, operatingSystemType);

        final ScanJobBuilder scanJobBuilder = new ScanJobBuilder();
        scanJobBuilder.installDirectory(new File(installPath));
        scanJobBuilder.outputDirectory(new File(outputPath));
        scanJobBuilder.fromHubServerConfig(hubServerConfig);
        scanJobBuilder.addTarget(ScanTarget.createBasicTarget(targetPath));
        scanJobBuilder.projectAndVersionNames(projectName, versionName);
        scanJobBuilder.dryRun(true);

        final ScanJob scanJob = scanJobBuilder.build();

        final ScanJobManager scanManager = new ScanJobManager(logger, intEnvironmentVariables, scannerZipInstaller, scanPathsUtility, scanJobRunner);

        System.out.println("\n\n\n\n");
        final ScanJobOutput scanJobOutput = scanManager.executeScans(scanJob);
        printOutput(scanJobOutput.getScanCommandOutputs());
    }

    private void printOutput(final List<ScanCommandOutput> scanCommandOutputs) {
        for (final ScanCommandOutput scanCommandOutput : scanCommandOutputs) {
            System.out.println(scanCommandOutput.getResult());
            if (Result.SUCCESS == scanCommandOutput.getResult()) {
                if (scanCommandOutput.wasDryRun()) {
                    System.out.println(scanCommandOutput.getDryRunFile().get().getAbsolutePath());
                } else {
                    System.out.println(scanCommandOutput.getScanSummaryFile().get().getAbsolutePath());
                }
            } else {
                System.out.println(scanCommandOutput.getErrorMessage());
                System.out.println(scanCommandOutput.getException().getMessage());
            }
        }
    }

}

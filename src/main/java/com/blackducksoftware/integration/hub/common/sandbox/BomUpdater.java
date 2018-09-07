package com.blackducksoftware.integration.hub.common.sandbox;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.enumeration.ScanSummaryStatusType;
import com.synopsys.integration.blackduck.api.generated.view.CodeLocationView;
import com.synopsys.integration.blackduck.api.view.ScanSummaryView;
import com.synopsys.integration.blackduck.exception.DoesNotExistException;
import com.synopsys.integration.blackduck.service.CodeLocationService;
import com.synopsys.integration.blackduck.service.HubService;
import com.synopsys.integration.exception.IntegrationException;

public class BomUpdater {
    private final Logger logger = LoggerFactory.getLogger(BomUpdater.class);

    private final CodeLocationService codeLocationService;
    private final HubService hubService;

    public BomUpdater(final CodeLocationService codeLocationService, final HubService hubService) {
        this.codeLocationService = codeLocationService;
        this.hubService = hubService;
    }

    // we will only attempt to add the code location if it doesn't exist
    public void addCommonsFileUpload() throws IntegrationException, InterruptedException {
        logger.info("starting to add commons-fileupload");
        removeCommonsFileUpload();
        try {
            codeLocationService.getCodeLocationByName("apache commons-fileupload");
        } catch (final DoesNotExistException e) {
            final String filePath = getClass().getClassLoader().getResource("add-commons-fileupload.jsonld").getFile();
            final File jsonldFile = new File(filePath);
            codeLocationService.importBomFile(jsonldFile);
        }

        logger.info("added commons-fileupload, now waiting for bom update");
        boolean codeLocationComplete = false;
        while (!codeLocationComplete) {
            try {
                final CodeLocationView codeLocationView = codeLocationService.getCodeLocationByName("apache commons-fileupload");
                final String scansLink = hubService.getFirstLinkSafely(codeLocationView, CodeLocationView.SCANS_LINK);
                final List<ScanSummaryView> codeLocationScanSummaryViews = hubService.getAllResponses(scansLink, ScanSummaryView.class);
                for (final ScanSummaryView scanSummaryView : codeLocationScanSummaryViews) {
                    if (ScanSummaryStatusType.COMPLETE != scanSummaryView.status) {
                        break;
                    }
                }
                codeLocationComplete = true;
            } catch (final Exception e) {
                logger.warn("error getting code location status: " + e.getMessage());
            }
            logger.warn("The Code Location has not yet completed.");
            Thread.sleep(Application.FIVE_SECONDS);
        }

        logger.info("Code Location completed!");
    }

    public void removeCommonsFileUpload() throws IntegrationException {
        try {
            final CodeLocationView codeLocationView = codeLocationService.getCodeLocationByName("apache commons-fileupload");
            codeLocationService.deleteCodeLocation(codeLocationView);
        } catch (final DoesNotExistException e) {
            logger.warn("The Code Location can\"t be removed - it doesn't exist!");
        }
    }

}

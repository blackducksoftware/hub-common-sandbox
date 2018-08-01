package com.blackducksoftware.integration.hub.common.sandbox;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.enumeration.PolicyRuleConditionOperatorType;
import com.blackducksoftware.integration.hub.api.generated.component.PolicyRuleExpressionSetView;
import com.blackducksoftware.integration.hub.api.generated.view.ComponentVersionView;
import com.blackducksoftware.integration.hub.api.generated.view.PolicyRuleViewV2;
import com.blackducksoftware.integration.hub.api.view.MetaHandler;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.exception.DoesNotExistException;
import com.blackducksoftware.integration.hub.service.ComponentService;
import com.blackducksoftware.integration.hub.service.PolicyRuleService;
import com.blackducksoftware.integration.hub.service.model.PolicyRuleExpressionSetBuilder;

public class PolicyRuleCreator {
    public static final String NO_FILEUPLOAD_1_2_1 = "No Commons FileUpload 1.2.1";

    private final PolicyRuleService policyRuleService;
    private final ComponentService componentService;
    private final MetaHandler metaHandler;
    private final ExternalIdFactory externalIdFactory;

    public PolicyRuleCreator(final PolicyRuleService policyRuleService, final ComponentService componentService, final MetaHandler metaHandler, final ExternalIdFactory externalIdFactory) {
        this.policyRuleService = policyRuleService;
        this.componentService = componentService;
        this.metaHandler = metaHandler;
        this.externalIdFactory = externalIdFactory;
    }

    public PolicyRuleViewV2 createNoCommonsFileUpload() throws IntegrationException {
        PolicyRuleViewV2 noCommonsFileUploadPolicyView = null;
        try {
            noCommonsFileUploadPolicyView = policyRuleService.getPolicyRuleViewByName(NO_FILEUPLOAD_1_2_1);
        } catch (final DoesNotExistException e) {
            final ExternalId commonsFileUploadExternalId = externalIdFactory.createMavenExternalId("commons-fileupload", "commons-fileupload", "1.2.1");
            final ComponentVersionView componentVersionView = componentService.getComponentVersion(commonsFileUploadExternalId);

            final PolicyRuleExpressionSetBuilder builder = new PolicyRuleExpressionSetBuilder(metaHandler);
            builder.addComponentVersionCondition(PolicyRuleConditionOperatorType.EQ, componentVersionView);
            final PolicyRuleExpressionSetView expressionSet = builder.createPolicyRuleExpressionSetView();

            noCommonsFileUploadPolicyView = new PolicyRuleViewV2();
            noCommonsFileUploadPolicyView.name = "No Commons FileUpload 1.2.1";
            noCommonsFileUploadPolicyView.enabled = true;
            noCommonsFileUploadPolicyView.overridable = true;
            noCommonsFileUploadPolicyView.expression = expressionSet;

            policyRuleService.createPolicyRule(noCommonsFileUploadPolicyView);
        }
        return noCommonsFileUploadPolicyView;
    }

}

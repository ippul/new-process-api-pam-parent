package com.redhat.example;

import io.cucumber.datatable.DataTable;
import io.cucumber.java8.En;
import io.cucumber.java8.Scenario;
import org.junit.Assert;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.KieContainerResource;
import org.kie.server.api.model.ReleaseId;
import org.kie.server.api.model.ServiceResponse;
import org.kie.server.api.model.definition.SearchQueryFilterSpec;
import org.kie.server.api.model.instance.ProcessInstance;
import org.kie.server.api.model.instance.ProcessInstanceUserTaskWithVariables;
import org.kie.server.api.util.QueryParamFactory;
import org.kie.server.client.*;

import java.util.Arrays;
import java.util.List;

public class NewAPISteps implements En {

    private static final String KIE_SERVER_URL = "http://localhost:8080/kie-server/services/rest/server";
    private static final String PAM_ADMIN_USER = "rhpamAdmin";
    private static final String PAM_ADMIN_PASSWORD = "Pa$$w0rd";
    private static final String CONTAINER_ID = "test-new-api-kjar_1.0";
    private static final ReleaseId RELEASE_ID = new ReleaseId("com.redhat.example", "test-new-api-kjar" , "1.0");

    private QueryServicesClient queryServicesClient = null;
    private List<ProcessInstanceUserTaskWithVariables> queryUserTaskByVariables = null;

    public NewAPISteps() {

        Before((Scenario scenario) -> {
            final KieServicesClient kieServicesClient = getKieServicesClient(PAM_ADMIN_USER);
            final ServiceResponse<ReleaseId> serviceResponse = kieServicesClient.getReleaseId(CONTAINER_ID);
            if(serviceResponse.getResult() == null) {
                kieServicesClient.createContainer(CONTAINER_ID, new KieContainerResource(CONTAINER_ID, RELEASE_ID));
            }
        });

        After((Scenario scenario) -> {
            final KieServicesClient kieServicesClient = getKieServicesClient(PAM_ADMIN_USER);
            final ProcessServicesClient processServicesClient = kieServicesClient.getServicesClient(ProcessServicesClient.class);
            final List<ProcessInstance> processInstances = processServicesClient.findProcessInstances(CONTAINER_ID, 0, 1000);
            for(ProcessInstance processInstance : processInstances){
                processServicesClient.abortProcessInstance(CONTAINER_ID, processInstance.getId());
            }
            final ServiceResponse<ReleaseId> serviceResponse = kieServicesClient.getReleaseId(CONTAINER_ID);
            if(serviceResponse.getResult() != null) {
                kieServicesClient.disposeContainer(CONTAINER_ID);
            }
        });

        Given("an instance of the process definition {string} is created", (String processDefinitionId) -> {
            final KieServicesClient kieServicesClient = getKieServicesClient(PAM_ADMIN_USER);
            final ProcessServicesClient processServicesClient = kieServicesClient.getServicesClient(ProcessServicesClient.class);
            processServicesClient.startProcess(CONTAINER_ID, processDefinitionId);
        });

        When("a query service client is created by user {string}", (String userId) -> {
            final KieServicesClient kieServicesClient = getKieServicesClient(userId);
            queryServicesClient = kieServicesClient.getServicesClient(QueryServicesClient.class);
        });

        And("the new api is invoked to get all available tasks with status", (DataTable dataTable) -> {
            final SearchQueryFilterSpec filter = new SearchQueryFilterSpec();
            filter.setAttributesQueryParams(Arrays.asList(QueryParamFactory.in("TASK_STATUS", dataTable.asList(String.class).toArray(new String[3]))));
            queryUserTaskByVariables = queryServicesClient.queryUserTaskByVariables(filter,0,1000);
        });

        Then("the returned task list contains {int} elements", (Integer expectedTask) -> {
            Assert.assertEquals(expectedTask, Integer.valueOf(queryUserTaskByVariables.size()));
        });
    }

    public KieServicesClient getKieServicesClient(String username) {
        final KieServicesConfiguration config = KieServicesFactory.newRestConfiguration(KIE_SERVER_URL, username, PAM_ADMIN_PASSWORD);
        config.setMarshallingFormat(MarshallingFormat.JSON);
        config.setTimeout(100000l);
        return KieServicesFactory.newKieServicesClient(config);
    }

}

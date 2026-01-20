package com.example.workflow.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("perfTestRestServiceDelegate")
public class PerfTestRestServiceDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(PerfTestRestServiceDelegate.class);
    private final RestTemplate restTemplate;

    // OpenResty URL (Configured via properties or environment variable)
    private static final String MOCK_URL = "http://nginx-openresty/simulate-delay";

    public PerfTestRestServiceDelegate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String businessKey = execution.getProcessBusinessKey();
        LOG.info("Start REST call for BusinessKey: {}", businessKey);

        try {
            // The OpenResty server will hold this connection open for 2 seconds
            String response = restTemplate.getForObject(MOCK_URL, String.class);
            
            execution.setVariable("restResponse", response);
            execution.setVariable("isSuccess", true);
            
            LOG.info("REST call completed for BusinessKey: {}", businessKey);
        } catch (Exception e) {
            LOG.error("REST call failed for BusinessKey: {}", businessKey, e);
            execution.setVariable("isSuccess", false);
            // Throwing an exception here allows Camunda to handle retries if Async is configured
            throw e; 
        }
    }
}

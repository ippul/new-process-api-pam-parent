Feature: New Process query API query potential owner test
  Scenario Outline: As developer I want to test that the new Process query API return only the tasks owned or potentially owned by the logged user
    Given an instance of the process definition '<process-definition-id>' is created
    When a query service client is created by user '<query-client-userid>'
    And the new api is invoked to get all available tasks with status
      |Created  |
      |Ready    |
      |Reserved |
    Then the returned task list contains <expected-returned-task> elements
    Examples:
      | process-definition-id             | query-client-userid | expected-returned-task |
      | test-new-api.process-test-case-1  |  rhpamdeveloper     |0                       |
      | test-new-api.process-test-case-1  |  application-user   |1                       |
      | test-new-api.process-test-case-2  |  rhpamdeveloper     |0                       |
      | test-new-api.process-test-case-2  |  application-user   |1                       |
      | test-new-api.process-test-case-3  |  rhpamdeveloper     |0                       |
      | test-new-api.process-test-case-3  |  application-user   |1                       |
      | test-new-api.process-test-case-4  |  rhpamdeveloper     |1                       |
      | test-new-api.process-test-case-4  |  application-user   |1                       |
      | test-new-api.process-test-case-5  |  rhpamdeveloper     |1                       |
      | test-new-api.process-test-case-5  |  application-user   |1                       |
Feature: Read UTN and DCN

  @Test
  Scenario Outline: no patient record found
    Given User login with Valid 'username' and 'password'
    When User Triggers the ReadUTNandDCN Functionalblock with the given '<input>'
    Then User checks the status of the job with the status code '<code>'

    Examples: 
      | input                                                               | code                                                            |
      | DCNandUTN, noDL, startDateBetweenRange, oldPeriodBegin, wrongAgency | Success, E5.Palmetto.E0122, E5.Palmetto.E0225, Success, Success |

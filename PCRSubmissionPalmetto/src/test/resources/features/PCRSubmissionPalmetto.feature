Feature: PCR Submission Palmetto

  @Test
  Scenario Outline: no patient record found
    Given User login with Valid 'username' and 'password'
    When User Triggers the PCRSubmission Functionalblock with the given '<input>'
    Then User checks the status of the job with the status code '<code>'

    Examples: 
      | input                                                      | code                                                                                    |
      | Q1yes, Q1No, BP2SameAsBP1, InvalidFile, BP2TaskFileInvalid | E5.Core.E0076, E5.FB.PALMETTO.E0016, E5.FB.PALMETTO.E0016, E5.Core.E0077, E5.Core.E0077 |

  @Test02
  Scenario Outline: no patient record found
    Given User login with Valid 'username' and 'password'
    When User Triggers the PCRSubmission Functionalblock with the given '<input>'
    Then User checks the status of the job with the status code '<code>'

    Examples: 
      | input                                            | code                                                                                                  |
      | ICDCodeBlank, Q2yes, Q2YesT1Blank, BP2Yes, BP2No | E5.FB.PALMETTO.E0016, E5.FB.PALMETTO.E0016, E5.Core.E0079, E5.FB.PALMETTO.E0016, E5.FB.PALMETTO.E0016 |

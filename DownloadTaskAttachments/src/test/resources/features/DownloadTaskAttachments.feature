Feature: Download Task Attachments

 
  Scenario Outline: no patient record found
    Given User login with Valid 'username' and 'password'
    When User Triggers the DownloadtaskAttachments Functionalblock with the given '<input>'
    Then User checks the status of the job with the status code '<code>'

    Examples: 
      | input             | code    |
      | noCurrentCoverage | Success |

  Scenario Outline: no patient record found
    Given User login with Valid 'username' and 'password'
    When User Triggers the DownloadtaskAttachments Functionalblock with the given '<input>'
    Then User checks the status of the job with the status code '<code>'

    Examples: 
      | input       | code    |
      | validInput2 | Success |

  Scenario Outline: no patient record found
    Given User login with Valid 'username' and 'password'
    When User Triggers the DownloadtaskAttachments Functionalblock with the given '<input>'
    Then User checks the status of the job with the status code '<code>'

    Examples: 
      | input      | code    |
      | validInput | Success |

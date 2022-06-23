Feature: Update PCR Status

@NoPatient
Scenario Outline: no patient record found
Given User login with Valid 'username' and 'password'
When User Triggers the UpdatePCRStatus Functionalblock with the given '<input>'
Then User checks the status of the job with the status code '<code>'

Examples:
    |input        | code |
    |noPatient| E5.FB.HCHB.E0005 |   

@BeforeDecisionReceiptDate
Scenario Outline: Decision receipt date given as before submission date
Given User login with Valid 'username' and 'password'
When User Triggers the UpdatePCRStatus Functionalblock with the given '<input>'
Then User checks the status of the job with the status code '<code>'

Examples:
    |input | code |
    |pastDateInDecisionReciptDate | E5.HCHB.E0334 |
    
@NoTrackingNumber
Scenario Outline: change status to Affirmative with no UTN
Given User login with Valid 'username' and 'password'
When User Triggers the UpdatePCRStatus Functionalblock with the given '<input>'
Then User checks the status of the job with the status code '<code>'

Examples:
    |input | code |
    |noTrackingNumber | E5.HCHB.E0334 |
    
@DecisionPendingSuccess
Scenario Outline: providing valid input for Decision Pending success PCR update
Given User login with Valid 'username' and 'password'
When User Triggers the UpdatePCRStatus Functionalblock with the given '<input>'
Then User checks the status of the job with the status code '<code>'

Examples:
    |input | code |
    |DecisionPendingSuccess | Success |
 
@AffirmedSuccess
Scenario Outline: providing valid input for Affirmed Success PCR update
Given User login with Valid 'username' and 'password'
When User Triggers the UpdatePCRStatus Functionalblock with the given '<input>'
Then User checks the status of the job with the status code '<code>'

Examples:
    |input | code |
    |AffirmedSuccess | Success |
    
@sumbmissionDateBeforePeroidBegin
Scenario Outline: providing valid input for success PCR update
Given User login with Valid 'username' and 'password'
When User Triggers the UpdatePCRStatus Functionalblock with the given '<input>'
Then User checks the status of the job with the status code '<code>'

Examples:
    |input | code |
    |sumbmissionDateBeforePeroidBegin | E5.HCHB.E0333 |
    
    
@test
Scenario Outline: providing valid input for success PCR update
Given User login with Valid 'username' and 'password'
When User Triggers the UpdatePCRStatus Functionalblock with the given '<input>'
Then User checks the status of the job with the status code '<code>'

Examples:
    |input | code |
    |check | E5.FB.HCHB.E0005, E5.FB.HCHB.E0005 |
Feature: Bank account history service

  Scenario: Successful bank account history record creation
    Given New bank account history record for bank account with id 1
    When Method createBankAccountHistoryRecord for bank account with id 1 called
    Then The response should contain the details of the created bank account history record

  Scenario: Successful retrieval of last withdrawal date of bank account when withdrawal exists
    Given There are withdrawals in the system for bank account with id 1
    When Method getLastWithdrawalDate for bank account with id 1 called
    Then The response should contain the details of last withdrawal date

  Scenario: Successful retrieval bank account history with pagination and sorting
    Given There is bank account with id 1 history in the system in page 0 with size 2 and sort by "id"
    When Method getBankAccountHistory for bank account with id 1 called with page 0, size 2, and sort by "id"
    Then The response should contain a page of bank account history number 0 with size 2

Feature: Bank account service

  Scenario: Successful bank account creation
    Given New bank account has unique number "123jh278fha234"
    When Method createBankAccount called
    Then The response should contain the details of the created bank account

  Scenario: Error when creating bank account with an existing phone number
    Given New bank account has existing number "123jh278fha234"
    When Method createBankAccount called
    Then The AccountNumberUniqueException should be thrown with message "Bank account with number '123jh278fha234' already exist"

  Scenario:  Error when creating bank account when driver with id 1 already has account
    Given New bank account created for driver with id 1 who already has account
    When Method createBankAccount called
    Then The DriverBankAccountException should be thrown with message "Driver with id '1' already has bank account"

  Scenario: Successful retrieval of bank account by id
    Given There is a bank account with id 1
    When Method getBankAccountById called with id 1
    Then The response should contain the details of bank account with id 1

  Scenario: Error when attempting to get non-existing bank account by id
    Given There is no bank account with id 1
    When Method getBankAccountById called with id 1
    Then The BankAccountNotFoundException should be thrown with message "Bank account with id '1' not found"

  Scenario: Successful deleting bank account by driver id
    Given Deleting bank account with driver id 1 exists
    When Method deleteBankAccount called with driver id 1

  Scenario: Error when deleting non-existing bank account
    Given There is no bank account with driver id 1
    When Method deleteBankAccount called with driver id 1
    Then The BankAccountNotFoundException should be thrown with message "Driver with id '1' bank account not found"

  Scenario: Successful retrieval of bank account balance by id
    Given There is a bank account with id 1 to retrieval balance
    When Method getBankAccountBalance called with id 1
    Then The response should contain the details of bank account balance

  Scenario: Error when attempting to get non-existing bank account by id
    Given There is no bank account with id 1
    When Method getBankAccountBalance called with id 1
    Then The BankAccountNotFoundException should be thrown with message "Bank account with id '1' not found"

  Scenario: Successful withdrawal money from bank account by id
    Given There is bank account with id 1 and conditions are met
    When Method withdrawalPaymentFromBankAccount for account with id 1 called
    Then The response should contain the details of bank account after withdrawal money

  Scenario: Error when attempting to withdrawal money from non-existing bank account
    Given There is no bank account with id 1
    When Method withdrawalPaymentFromBankAccount for account with id 1 called
    Then The BankAccountNotFoundException should be thrown with message "Bank account with id '1' not found"

  Scenario: Successful refill bank account
    Given There is bank account of driver with id 1 to refill
    When Method refillBankAccount called
    Then The response should contain the details of bank account of driver with id 1 after refilling

  Scenario: Error when attempting to refill non-existing bank account
    Given There is no bank account of driver with id 1
    When Method refillBankAccount called
    Then The BankAccountNotFoundException should be thrown with message "Driver with id '1' bank account not found"

  Scenario: Successful retrieval active bank accounts with pagination and sorting
    Given There are active accounts in the system in page 0 with size 2 and sort by "id"
    When Method getAllActiveBankAccounts called with page 0, size 2, and sort by "id"
    Then The response should contain a page of active bank accounts number 0 with size 2

  Scenario: Successful retrieval bank accounts with pagination and sorting
    Given There are accounts in the system in page 0 with size 2 and sort by "id"
    When Method getAllBankAccounts called with page 0, size 2, and sort by "id"
    Then The response should contain a page of bank accounts number 0 with size 2

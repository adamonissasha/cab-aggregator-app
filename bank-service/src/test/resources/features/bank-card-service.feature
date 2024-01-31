Feature: Bank card service

  Scenario: Successful bank card creation
    Given New bank card has unique number "1234 1234 1234 1234"
    When Method createBankCard called
    Then The response should contain the details of the created bank card

  Scenario: Error when creating bank card with an existing phone number
    Given New bank card has existing number "1234 1234 1234 1234"
    When Method createBankCard called
    Then The CardNumberUniqueException should be thrown with message "Card with number '1234 1234 1234 1234' already exist"

  Scenario: Successful bank card editing
    Given Editing bank card with id 1 exists and number "1234 1234 1234 1234" unique
    When Method editBankCard called with id 1
    Then The response should contain the details of the edited bank card

  Scenario: Error when editing bank card with an existing number
    Given Editing bank card with id 1 has existing number "1234 1234 1234 1234"
    When Method editBankCard called with id 1
    Then The CardNumberUniqueException should be thrown with message "Card with number '1234 1234 1234 1234' already exist"

  Scenario: Error when editing non-existing bank card
    Given There is no bank card with id 1
    When Method editBankCard called with id 1
    Then The BankCardNotFoundException should be thrown with message "Card with id '1' not found"

  Scenario: Successful retrieval of bank card by id
    Given There is a bank card with id 1
    When Method getBankCardById called with id 1
    Then The response should contain the details of bank card with id 1

  Scenario: Error when attempting to get non-existing bank card by id
    Given There is no bank card with id 1
    When Method getBankCardById called with id 1
    Then The BankCardNotFoundException should be thrown with message "Card with id '1' not found"

  Scenario: Successful deleting bank card by id
    Given Deleting bank card with id 1 exists
    When Method deleteBankCard called with id 1

  Scenario: Error when deleting non-existing bank card
    Given There is no bank card with id 1
    When Method deleteBankCard called with id 1
    Then The BankCardNotFoundException should be thrown with message "Card with id '1' not found"

  Scenario: Successful retrieval user bank cards with pagination and sorting
    Given There are bank cards of user "PASSENGER" with id 1 in the system in page 0 with size 2 and sort by "id"
    When Method getBankCardsByBankUser for user "PASSENGER" with id 1 called with page 0, size 2, and sort by "id"
    Then The response should contain a page of user bank cards number 0 with size 2

  Scenario: Successful retrieval of default user bank card
    Given There is a default user "PASSENGER" with id 1 bank card
    When Method getDefaultBankCard of user "PASSENGER" with id 1 called
    Then The response should contain the details of default user bank card

  Scenario: Error when attempting to get non-existing default user bank card
    Given There is no default user "PASSENGER" with id 1 bank card
    When Method getDefaultBankCard of user "PASSENGER" with id 1 called
    Then The BankCardNotFoundException should be thrown with message "PASSENGER's with id 1 default card not found"

  Scenario: Successful retrieval of bank card balance by id
    Given There is a bank card with id 1 to retrieval balance
    When Method getBankCardBalance called with id 1
    Then The response should contain the details of bank card balance

  Scenario: Error when attempting to get non-existing bank card by id
    Given There is no bank card with id 1
    When Method getBankCardBalance called with id 1
    Then The BankCardNotFoundException should be thrown with message "Card with id '1' not found"

  Scenario: Successful withdrawal payment from bank card by id
    Given There is bank card with id 1 with sufficient balance
    When Method withdrawalPaymentFromBankCard for card with id 1 called
    Then The response should contain the details of bank card with id 1 after withdrawal payment

  Scenario: Error when attempting to withdrawal payment from bank card with id with insufficient balance
  Given There is bank card with id 1 with insufficient balance to pay 50 BYN
    When Method withdrawalPaymentFromBankCard for card with id 1 called
    Then The BankCardBalanceException should be thrown with message "There is not enough balance money to pay 50 BYN for the ride. Refill card or change payment method";

  Scenario: Error when attempting to withdrawal payment from non-existing bank card
    Given There is no bank card with id 1
    When Method withdrawalPaymentFromBankCard for card with id 1 called
    Then The BankCardNotFoundException should be thrown with message "Card with id '1' not found"

  Scenario: Successful refill bank card when id provided
    Given There is bank card with id 1 to refill
    When Method refillBankCard for card with id 1 called
    Then The response should contain the details of bank card with id 1 after refilling

  Scenario: Successful refill bank card when id not provided
    Given Bank card id to refill not provided
    When Method refillBankCard for card without called
    Then The response should contain the details of bank card with id 1 after refilling

  Scenario: Error when attempting to refill non-existing bank card
    Given There is no bank card with id 1
    When Method refillBankCard for card with id 1 called
    Then The BankCardNotFoundException should be thrown with message "Card with id '1' not found"
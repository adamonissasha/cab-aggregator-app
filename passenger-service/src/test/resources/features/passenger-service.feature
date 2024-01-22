Feature: Passenger service

  Scenario: Successful passenger creation
    Given New passenger has unique phone number "+375291234567"
    When Method createPassenger called
    Then The response should contain the details of the created passenger

  Scenario: Error when creating passenger with an existing phone number
    Given New passenger has existing phone number "+375291234567"
    When Method createPassenger called
    Then The PhoneNumberUniqueException should be thrown with message "Passenger with phone number '+375291234567' already exist"

  Scenario: Successful passenger editing
    Given Editing passenger with id 1 exists and phone number "+375291234567" unique
    When Method editPassenger called with id 1
    Then The response should contain the details of the edited passenger

  Scenario: Error when editing passenger with an existing phone number
    Given Editing passenger with id 1 has existing phone number "+375291234567"
    When Method editPassenger called with id 1
    Then The PhoneNumberUniqueException should be thrown with message "Passenger with phone number '+375291234567' already exist"

  Scenario: Error when editing non-existing passenger
    Given There is no passenger with id 1
    When Method editPassenger called with id 1
    Then The PassengerNotFoundException should be thrown with message "Passenger with id '1' not found"

  Scenario: Successful retrieval of passenger by id
    Given There is a passenger with id 1
    When Method getPassengerById called with id 1
    Then The response should contain the details of passenger with id 1

  Scenario: Error when attempting to get non-existing passenger by id
    Given There is no passenger with id 1
    When Method getPassengerById called with id 1
    Then The PassengerNotFoundException should be thrown with message "Passenger with id '1' not found"

  Scenario: Successful deleting passenger by id
    Given Deleting passenger with id 1 exists
    When Method deletePassengerById called with id 1

  Scenario: Error when deleting non-existing passenger
    Given There is no passenger with id 1
    When Method deletePassengerById called with id 1
    Then The PassengerNotFoundException should be thrown with message "Passenger with id '1' not found"

  Scenario: Successful retrieval passengers with pagination and sorting
    Given There are passengers in the system in page 0 with size 2 and sort by "id"
    When Method getAllPassengers called with page 0, size 2, and sort by "id"
    Then The response should contain a page of passengers number 0 with size 2

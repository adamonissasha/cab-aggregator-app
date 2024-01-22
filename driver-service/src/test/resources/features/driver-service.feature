Feature: Driver service

  Scenario: Successful driver creation
    Given New driver has unique phone number "+375291234567"
    When Method createDriver called
    Then The response should contain the details of the created driver

  Scenario: Error when creating driver with an existing phone number
    Given New driver has existing phone number "+375291234567"
    When Method createDriver called
    Then The PhoneNumberUniqueException should be thrown with message "Driver with phone number '+375291234567' already exist"

  Scenario: Error when creating driver with non-existing car
    Given New driver has non-existing car with id 1
    When Method createDriver called
    Then The CarNotFoundException should be thrown in method createDriver with message "Car with id '1' not found"

  Scenario: Successful driver editing
    Given Editing driver with id 1 exists and phone number "+375291234567" unique
    When Method editDriver called with id 1
    Then The response should contain the details of the edited driver

  Scenario: Error when editing driver with an existing phone number
    Given Editing driver with id 1 has existing phone number "+375291234567"
    When Method editDriver called with id 1
    Then The PhoneNumberUniqueException should be thrown with message "Driver with phone number '+375291234567' already exist"

  Scenario: Error when editing non-existing driver
    Given There is no driver with id 1
    When Method editDriver called with id 1
    Then The DriverNotFoundException should be thrown with message "Driver with id '1' not found"

  Scenario: Successful retrieval of driver by id
    Given There is a driver with id 1
    When Method getDriverById called with id 1
    Then The response should contain the details of driver with id 1

  Scenario: Error when attempting to get non-existing driver by id
    Given There is no driver with id 1
    When Method getDriverById called with id 1
    Then The DriverNotFoundException should be thrown with message "Driver with id '1' not found"

  Scenario: Successful retrieval drivers with pagination and sorting
    Given There are drivers in the system in page 0 with size 2 and sort by "id"
    When Method getAllDrivers called with page 0, size 2, and sort by "id"
    Then The response should contain a page of drivers number 0 with size 2

  Scenario: Successful changing drivers status to free
    Given There is driver with id 1 and status BUSY
    When Method changeDriverStatusToFree called with id 1
    Then The response should contain the details of the changing driver with id 1

  Scenario: Error when changing status of non-existing driver
    Given There is no driver with id 1
    When Method changeDriverStatusToFree called with id 1
    Then The DriverNotFoundException should be thrown with message "Driver with id '1' not found"

  Scenario: Error when changing status of already free driver
    Given Status of driver with id 1 already free
    When Method changeDriverStatusToFree called with id 1
    Then The DriverStatusException should be thrown with message "Driver with id '1' is already free";

Feature: Car service

  Scenario: Successful car creation
    Given New car has unique car number "1234-BH-5"
    When Method createCar called
    Then The response should contain the details of the created car

  Scenario: Error when creating car with an existing car number
    Given New car has existing car number "1234-BH-5"
    When Method createCar called
    Then The CarNumberUniqueException should be thrown with message "Car with number '1234-BH-5' already exist"

  Scenario: Successful car editing
    Given Editing car with id 1 exists and car number "1234-BH-5" unique
    When Method editCar called with id 1
    Then The response should contain the details of the edited car

  Scenario: Error when editing car with an existing car number
    Given Editing car with id 1 has existing car number "1234-BH-5"
    When Method editCar called with id 1
    Then The CarNumberUniqueException should be thrown with message "Car with number '1234-BH-5' already exist"

  Scenario: Error when editing non-existing car
    Given There is no car with id 1
    When Method editCar called with id 1
    Then The CarNotFoundException should be thrown with message "Car with id '1' not found"

  Scenario: Successful retrieval of car by id
    Given There is a car with id 1
    When Method getCarById called with id 1
    Then The response should contain the details of car with id 1

  Scenario: Error when attempting to get non-existing car by id
    Given There is no car with id 1
    When Method getCarById called with id 1
    Then The CarNotFoundException should be thrown with message "Car with id '1' not found"

  Scenario: Successful retrieval cars with pagination and sorting
    Given There are cars in the system in page 0 with size 2 and sort by "id"
    When Method getAllCars called with page 0, size 2, and sort by "id"
    Then The response should contain a page of cars number 0 with size 2

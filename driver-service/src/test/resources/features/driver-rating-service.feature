Feature: Driver rating service

  Scenario: Successful rating driver
    Given Rating driver with id 1 exists
    When Method rateDriver called with id 1

  Scenario: Error when rating non-existing driver
    Given Rating driver with id 1 not exists
    When Method rateDriver called with id 1
    Then The DriverNotFoundException should be thrown with the following message "Driver with id '1' not found"

  Scenario: Successful retrieval of driver ratings
    Given Driver with id 1 to retrieval all ratings exists
    When Method getRatingsByDriverId called with id 1
    Then The response should contains the list of driver ratings

  Scenario: Error when retrieval of ratings non-existing driver
    Given Rating driver with id 1 not exists
    When Method getRatingsByDriverId called with id 1
    Then The DriverNotFoundException should be thrown with the following message "Driver with id '1' not found"

  Scenario: Successful retrieval of driver average rating
    Given Driver with id 1 to retrieval average rating exists
    When Method getAverageDriverRating called with id 1
    Then The response should contains the average driver with id 1 rating

  Scenario: Error when retrieval of non-existing driver average rating
    Given Rating driver with id 1 not exists
    When Method getAverageDriverRating called with id 1
    Then The DriverNotFoundException should be thrown with the following message "Driver with id '1' not found"

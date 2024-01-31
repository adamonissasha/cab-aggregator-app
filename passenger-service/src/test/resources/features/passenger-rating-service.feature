Feature: Passenger rating service

  Scenario: Successful rating passenger
    Given Rating passenger with id 1 exists
    When Method ratePassenger called with id 1

  Scenario: Error when rating non-existing passenger
    Given Rating passenger with id 1 not exists
    When Method ratePassenger called with id 1
    Then The PassengerNotFoundException should be thrown with the following message "Passenger with id '1' not found"

  Scenario: Successful retrieval of passenger ratings
    Given Passenger with id 1 to retrieval all ratings exists
    When Method getRatingsByPassengerId called with id 1
    Then The response should contains the list of passenger ratings

  Scenario: Error when retrieval of ratings non-existing passenger
    Given Rating passenger with id 1 not exists
    When Method getRatingsByPassengerId called with id 1
    Then The PassengerNotFoundException should be thrown with the following message "Passenger with id '1' not found"

  Scenario: Successful retrieval of passenger average rating
    Given Passenger with id 1 to retrieval average rating exists
    When Method getAveragePassengerRating called with id 1
    Then The response should contains the average passenger with id 1 rating

  Scenario: Error when retrieval of non-existing passenger average rating
    Given Rating passenger with id 1 not exists
    When Method getAveragePassengerRating called with id 1
    Then The PassengerNotFoundException should be thrown with the following message "Passenger with id '1' not found"

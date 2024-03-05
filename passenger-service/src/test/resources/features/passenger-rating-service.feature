Feature: Passenger rating service

  Scenario: Successful rating passenger
    Given Rating passenger with id "65cbbb08e399fa178470785d" exists
    When Method ratePassenger called with id "65cbbb08e399fa178470785d"

  Scenario: Error when rating non-existing passenger
    Given Rating passenger with id "65cbbb08e399fa178470785d" not exists
    When Method ratePassenger called with id "65cbbb08e399fa178470785d"
    Then The PassengerNotFoundException should be thrown with the following message "Passenger with id '65cbbb08e399fa178470785d' not found"

  Scenario: Successful retrieval of passenger ratings
    Given Passenger with id "65cbbb08e399fa178470785d" to retrieval all ratings exists
    When Method getRatingsByPassengerId called with id "65cbbb08e399fa178470785d"
    Then The response should contains the list of passenger ratings

  Scenario: Error when retrieval of ratings non-existing passenger
    Given Rating passenger with id "65cbbb08e399fa178470785d" not exists
    When Method getRatingsByPassengerId called with id "65cbbb08e399fa178470785d"
    Then The PassengerNotFoundException should be thrown in the getRatingsByPassengerId method with the following message "Passenger with id '65cbbb08e399fa178470785d' not found"

  Scenario: Successful retrieval of passenger average rating
    Given Passenger with id "65cbbb08e399fa178470785d" to retrieval average rating exists
    When Method getAveragePassengerRating called with id "65cbbb08e399fa178470785d"
    Then The response should contains the average passenger with id "65cbbb08e399fa178470785d" rating

  Scenario: Error when retrieval of non-existing passenger average rating
    Given Rating passenger with id "65cbbb08e399fa178470785d" not exists
    When Method getAveragePassengerRating called with id "65cbbb08e399fa178470785d"
    Then The PassengerNotFoundException should be thrown in the getAveragePassengerRating method with the following message "Passenger with id '65cbbb08e399fa178470785d' not found"

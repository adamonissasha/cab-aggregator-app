Feature: Stop service

  Scenario: Successful ride stops creation
    Given Provided valid ride requests and ride
    When Method createStops called
    Then The response should contain the details of ride stops

  Scenario: Ride stops creation when empty stops request
    Given Provided empty stop requests
    When Method createStops called
    Then The response should contain empty list of stops

  Scenario: Successful retrieval of ride stops by ride
    Given Provided valid ride and ride has stops
    When Method getRideStops called
    Then The response should contain the details of ride stops

  Scenario: Retrieval stops of ride that hasn't stops
    Given Provided valid ride and ride hasn't stops
    When Method getRideStops called
    Then The response should contain empty list of stops

  Scenario: Successful ride stops editing when stops exists
    Given Provided valid ride that has stops
    When Method editStops called
    Then The response should contain the details of ride stops

  Scenario: Successful ride stops editing when stops not exists
    Given Provided valid ride that hasn't stops
    When Method editStops called
    Then The response should contain the details of ride stops

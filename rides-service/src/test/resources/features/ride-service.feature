Feature: Ride service

  Scenario: Successful ride creation
    Given New ride with valid payment data
    When Method createRide called
    Then The response should contain the details of the created ride

  Scenario: Error when creating promo code with incorrect dates
    Given New ride with payment method "CARD" and not selected card
    When Method createRide called
    Then The PaymentMethodException should be thrown with message "If you have chosen CARD payment method, select the card for payment"

  Scenario: Successful retrieval of ride by id
    Given There is a ride with id 1
    When Method getRideById called with id 1
    Then The response should contain the details of ride with id 1

  Scenario: Error when attempting to get non-existing ride by id
    Given There is no ride with id 1
    When Method getRideById called with id 1
    Then The RideNotFoundException should be thrown with message "Ride with id '1' not found"

  Scenario: Successful ride editing
    Given Editing ride with id 1 exists and status correct
    When Method editRide called with id 1
    Then The response should contain the details of the edited ride

  Scenario: Error when editing ride with incorrect status
    Given Editing ride with id 1 has invalid status "CANCELED"
    When Method editRide called with id 1
    Then The RideStatusException should be thrown with message "The ride with id '1' has already been canceled"

  Scenario: Error when editing non-existing ride
    Given There is no ride with id 1
    When Method editRide called with id 1
    Then The RideNotFoundException should be thrown with message "Ride with id '1' not found"

  Scenario: Successful ride cancellation
    Given Canceled ride with id 1 has correct status
    When Method cancelRide called with id 1
    Then The response should contain the details of the canceled ride

  Scenario: Error when cancel ride that has already completed
    Given Canceled ride with id 1 has incorrect status "COMPLETED"
    When Method cancelRide called with id 1
    Then The RideStatusException should be thrown with message "The ride with id '1' has already been completed"

  Scenario: Error when non-existing ride cancellation
    Given There is no ride with id 1
    When Method cancelRide called with id 1
    Then The RideNotFoundException should be thrown with message "Ride with id '1' not found"

  Scenario: Successful start of the ride
    Given Started ride with id 1 has correct status "CREATED"
    When Method startRide called with id 1
    Then The response should contain the details of the started ride

  Scenario: Error when start ride that has already completed
    Given Started ride with id 1 has incorrect status "COMPLETED"
    When Method startRide called with id 1
    Then The RideStatusException should be thrown with message "The ride with id '1' has already been completed"

  Scenario: Error when start of the non-existing ride
    Given There is no ride with id 1
    When Method startRide called with id 1
    Then The RideNotFoundException should be thrown with message "Ride with id '1' not found"

  Scenario: Successful complete of the ride
    Given Completed ride with id 1 has correct status "STARTED"
    When Method completeRide called with id 1
    Then The response should contain the details of the completed ride

  Scenario: Error when complete ride that has already canceled
    Given Completed ride with id 1 has incorrect status "CANCELED"
    When Method completeRide called with id 1
    Then The RideStatusException should be thrown with message "The ride with id '1' has already been canceled"

  Scenario: Error when complete ride that not started
    Given Completed ride with id 1 not started
    When Method completeRide called with id 1
    Then The RideStatusException should be thrown with message "The ride with id '1' hasn't started"

  Scenario: Error when complete of the non-existing ride
    Given There is no ride with id 1
    When Method completeRide called with id 1
    Then The RideNotFoundException should be thrown with message "Ride with id '1' not found"

  Scenario: Successful passenger rating when ride completed
    Given Ride with id 1 is completed
    When Method ratePassenger called with id 1

  Scenario: Error when rate passenger before complete ride
    Given Ride with id 1 isn't completed
    When Method ratePassenger called with id 1
    Then The RideStatusException should be thrown with message "The ride with id '1' hasn't completed"

  Scenario: Error when rate passenger of the non-existing ride
    Given There is no ride with id 1
    When Method ratePassenger called with id 1
    Then The RideNotFoundException should be thrown with message "Ride with id '1' not found"

  Scenario: Successful driver rating when ride completed
    Given Ride with id 1 is completed
    When Method rateDriver called with id 1

  Scenario: Error when rate driver before complete ride
    Given Ride with id 1 isn't completed
    When Method rateDriver called with id 1
    Then The RideStatusException should be thrown with message "The ride with id '1' hasn't completed"

  Scenario: Error when rate driver of the non-existing ride
    Given There is no ride with id 1
    When Method rateDriver called with id 1
    Then The RideNotFoundException should be thrown with message "Ride with id '1' not found"
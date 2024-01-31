Feature: Promo code service

  Scenario: Successful promo code creation
    Given New promo code "SUMMER" unique and dates valid
    When Method createPromoCode called
    Then The response should contain the details of the created promo code

  Scenario: Error when creating promo code with incorrect dates
    Given New promo code has invalid dates: start date "2024-01-31", end date "2024-01-13"
    When Method createPromoCode called
    Then The IncorrectDateException should be thrown with message "Start date '2024-01-31' must not be after end date '2024-01-13'"

  Scenario: Error when creating promo code with an existing code
    Given New promo code "SUMMER" already exists with later end date
    When Method createPromoCode called
    Then The PromoCodeAlreadyExistsException should be thrown with message "Promo code 'SUMMER' already exists in this period of time"

  Scenario: Successful promo code editing
    Given Editing promo code with id 1 exists and code "SUMMER" unique
    When Method editPromoCode called with id 1
    Then The response should contain the details of the edited promo code

  Scenario: Error when editing promo code with incorrect dates
    Given Editing promo code with id 1 has invalid dates: start date "2024-01-31", end date "2024-01-13"
    When Method editPromoCode called with id 1
    Then The IncorrectDateException should be thrown with message "Start date '2024-01-31' must not be after end date '2024-01-13'"

  Scenario: Error when editing promo code with an existing code
    Given Editing promo code with id 1 and code "SUMMER" already exists with later end date
    When Method editPromoCode called with id 1
    Then The PromoCodeAlreadyExistsException should be thrown with message "Promo code 'SUMMER' already exists in this period of time"

  Scenario: Error when editing non-existing promo code
    Given There is no promo code with id 1
    When Method editPromoCode called with id 1
    Then The PromoCodeNotFoundException should be thrown with message "Promo code with id '1' not found"

  Scenario: Successful retrieval of promo code by id
    Given There is a promo code with id 1
    When Method getPromoCodeById called with id 1
    Then The response should contain the details of promo code with id 1

  Scenario: Error when attempting to get non-existing promo code by id
    Given There is no promo code with id 1
    When Method getPromoCodeById called with id 1
    Then The PromoCodeNotFoundException should be thrown with message "Promo code with id '1' not found"

  Scenario: Successful retrieval of promo code by name
    Given There is a promo code with code "SUMMER"
    When Method getPromoCodeByName called with code "SUMMER"
    Then The response should contain the details of promo code with code "SUMMER"

  Scenario: Error when attempting to get non-existing promo code by code
    Given There is no promo code with code "SUMMER"
    When Method getPromoCodeByName called with code "SUMMER"
    Then The PromoCodeNotFoundException should be thrown with message "Promo code 'SUMMER' not found"

  Scenario: Successful retrieval all promo codes
    Given There are promo codes in the system
    When Method getAllPromoCodes called
    Then The response should contain a list of promo codes

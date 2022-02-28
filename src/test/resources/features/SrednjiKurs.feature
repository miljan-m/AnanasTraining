Feature: NBS Testing

Scenario: Getting average exchange rate for USD, EUR, RUB
  Given I am on the RSD exchange rate page
  When I take the average exchange rate for USD, EUR, RUB
  Then I should save it to my spreadsheet
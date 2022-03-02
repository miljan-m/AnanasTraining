Feature: NBS Testing
  Get average exchange rate between RSD and currencies supplied in a template excel workbook

@AverageRate
Scenario: Getting average exchange rate
  Given I am on page "https://www.nbs.rs/sr_RS/finansijsko_trziste/medjubankarsko-devizno-trziste/kursna-lista/zvanicni-srednji-kurs-dinara/index.html"
  When I take the exchange rate for all currencies from table "srednjiKursLista"
  Then I save the results based on template "CurrencyTemplate.xlsx"

@RatesByDay
Scenario: Getting exchange rate for the previous work day
  Given I am on page "https://nbs.rs/sr_RS/indeks/"
  When I navigate to the exchange rates by day filter page
  And I input the previous work day and show the list
  And I take the exchange rate for all currencies from table "spisakDeviza"
  Then I save the results based on template "CurrencyTemplate.xlsx"

Feature: Search by keyword

#  @demo
#  Scenario: Searching for a term
#    Given Sergey is on the DuckDuckGo home page
#    When he searches for "Cucumber"
#    Then all the result titles should contain the word "Cucumber"
#
#  @demo
#  Scenario: Refining a search using two terms
#    Given Sergey is on the DuckDuckGo home page
#    And he has searched for "Cucumber"
#    When he searches again for "zucchini"
#    Then all the result titles should contain the word "zucchini"

  @demo
  Scenario Outline: Search for all red images of a term
    Given Sergey is on the DuckDuckGo home page
    And he has searched for "<City>"
    Then he clicks "Images" tab to select "<Colour>" colour images

    Examples:
      | City   | Colour |
      | Paris  | Red    |
      | London | Red    |



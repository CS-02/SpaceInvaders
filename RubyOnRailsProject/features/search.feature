Feature: Search
  In order to find pages on the web
  As an information seeker
  I want to be able to search using keywords

  Scenario: Search for youtube
    Given I am on the home page
    When I search for "cucumber bdd"
    Then I should see "An introductio to Behavior-Driven Developement (BDD) with Cucumber for Java"
	
  Scenario: Click on a video
    Given I am on the search page of "cucumber bdd"
    When I click on the first video
    Then I should be taken to that video

  Scenario: Subscribe to a channel
    Given I am on a video page
    When I click on subscribe button
    Then the log in window appears
  
  Scenario: Share a video
    Given I am on a video page
    When I click on "Compartir" button
    Then the share window appears
  
  Scenario: Report a video
    Given I am on a video page
    When I click on "Más acciones" button
    And I click on "Denunciar" button
    Then the log in window appears
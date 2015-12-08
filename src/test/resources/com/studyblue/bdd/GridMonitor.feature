Feature: Monitor the grid


@smoke @gridmonitor
Scenario: Test open google page on grid
	Given I'm on the google page
	Then the page title contains "Google"
	
	

	
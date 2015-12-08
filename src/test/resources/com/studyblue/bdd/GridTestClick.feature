Feature: Generate open and close browser activities

@smoke
Scenario Outline: Test open google page on grid <Loop>
	Given I'm on the google page
	Then the page title contains "Google"
	Then I take a snapshot
	And I close the browser
		Examples:
	            | Loop | 
	            | 1    |
	            | 2    |
	            | 3    |
	            | 4    |
	            | 5    |
	            | 6    |
	            | 7    |
	            | 8    |
	            | 9    |
	            | 10   |
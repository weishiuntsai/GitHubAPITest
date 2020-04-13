# GitHub API Test
This set of tests is designed to test some GitHub APIs from https://api.github.com.  Tests were implemented using REST Assured, with Maven and JUnit.

## Test Scenarios
```
test_request():
  Test HTTP REQUEST (with GET) on https://api.github.com.
```
```
Scenario: Heading
  Given the webpage is visited
  Then the heading must be 'The greatest factorial calculator!'
```
```
Scenario: Calculator with supported integer values
  Given the webpage is visited
  When an integer between 0 (-0) to 170 is entered into the field
  And the submit button is clicked
  Then correct factorial result is returned
```
```
Scenario: Calculator with integer values outside of supported range
  Given the webpage is visited
  When an integer outside of the 0 (-0) to 170 range is entered into the field
  And the submit button is clicked
  Then an error is returned
```
```
Scenario: Calculator with non-integer values
  Given the webpage is visited
  When a non-integer value is entered into the field
  And the submit button is clicked
  Then the error 'Please enter an integer' is returned
```
```
Scenario: Terms and Conditions
  Given the webpage is visited
  When the 'Terms and Conditions' link is clicked
  Then the user is redirected to the 'Terms and Conditions' page
```
```
Scenario: Privacy
  Given the webpage is visited
  When the 'Privacy' link is clicked
  Then the user is redirected to the 'Privacy' page
```
```
Scenario: Copyright
  Given the webpage is visited
  When the 'Copyright' link is clicked
  Then the user is redirected to the 'Qxf2 Services' page
```
## Setup
The tests were implemented using JavaScript with the Cypress testing framework.  Follow the following steps to set up the environment on a Linux machine.
1. Download the GitHub repository https://github.com/weishiuntsai/dotData-SDET-Factorial-Task/ as a zip file.
2. `unzip dotData-SDET-Factorial-Task-master.zip`
3. `cd dotData-SDET-Factorial-Task-master/factorial`
4. `npm install cypress --save-dev`

## How to run the tests
To run the tests, start Cypress Test Runner:
1. `node_modules/.bin/cypress open`
2. Click on **factorial_spec.js**
3. A browser should show up and the tests will be run automatically.

## Test layout
This test contains a test file **factorial/cypress/integration/factorial_spec.js** and a library **factorial/cypress/dotdata_lib/test_site.js**.  The test file implements the Cypress tests.  The library implements the utilities and helper methods.  The library directory **factorial/cypress/dotdata_lib** is designed to be the central location for private testing libraries, so that all private Cypress testing libraries can be collected in one place in the future.

## Test results
|Test|Result|Bugs|Notes|
|---|---|---|---|
|Page Title|Failed|[BUG-0001]||
|Heading|Passed|||
|Calculator with supported integer values|Failed|[BUG-0004]|This test is designed to be adjustable by changing const scale to a smaller value and roundUp to false to test with less precision in the significant of a scientific notation for the factorial value if needed.  To accommodate the CI/CD model, it is also designed to be adjustable for testing all 171 supported integer values as a development unit test if needed.|
|Calculator with integer values outside of supported range|Passed|[BUG-0005]|The spec doesn't specify the expected behavior. Test is implemented according to the current behavior.  It needs to be updated once the behavior (what error would it return?) is clarified.|
|Calculator with non-integer values|Passed||| 
|Terms and Conditions|Failed|[BUG-0002]||
|Privacy|Failed|[BUG-0003]||
|Copyright|Passed|||

## Bug reports
---
**[BUG-0001] Incorrect page title**

**Description:**

The page title is incorrect.  The spec specifies it as 'Factorial', but it is 'Factoriall' instead.

**How to Reproduce:**
1. Visit http://qainterview.pythonanywhere.com/
2. Verify the tab name.
---
**[BUG-0002] Incorrect link for 'Terms and Conditions’**

**Description:**

The 'Terms and Conditions' link has an incorrect link.  It now redirects the user to the /privacy page.

**How to Reproduce:**
1. Visit http://qainterview.pythonanywhere.com/
2. Click on the 'Terms and Conditions' link.
---
**[BUG-0003] Incorrect link for 'Privacy’**

**Description:**

The 'Privacy' link has an incorrect link.  It now redirects the user to the /terms page.

**How to Reproduce:**
1. Visit http://qainterview.pythonanywhere.com/
2. Click on the 'Privacy' link.
---
**[BUG-0004] Inconsistent and sometimes incorrect significant in a result with scientific notation**

**Description:**

When a factorial result is longer than 20 digits (the result of n! where n>=22), the calculator returns the result in the scientific notation <significant>e+<order of magnitude>. The calculator displays a maximum of 16-digit scale (the part after ‘.’) in the significant for integer values from 22-170.  However, it is not consistent.  The following examples show such inconsistency.  In the case of 22!, 25!, and 26!, they have 16-digit scales.  In the case of 23! and 24!, they have 15-digit scales.  42! displays a 14-digit scale.  Other than the inconsistency, the significant also appears to be wrong in some cases.  For example, in the case of 26!, the result is 403291461126605635584000000, but the calculator returns it as 4.0329146112660565e+26, while it should be 4.0329146112660564e+26 (with rounding-up) instead.
  
22! (1124000727777607680000) => 1.1240007277776077e+21 (16-digit scale, rounding-up) 

23! (25852016738884976640000) => 2.585201673888498e+22  (15-digit scale, rounding-up)

24! (620448401733239439360000)  => 6.204484017332394e+23  (15-digit scale, rounding-up) 

25! (15511210043330985984000000) => 1.5511210043330986e+25 (16-digit scale, rounding-up)

26! (403291461126605635584000000) => 4.0329146112660565e+26 (16-digit scale, neither rounding-up nor -down with the ending digit 5)

42! (1405006117752879898543142606244511569936384000000000) => 1.40500611775288e+51 (14-digit scale, rounding-up)

**How to Reproduce:**
1. Visit http://qainterview.pythonanywhere.com/
2. Enter the values 22, 23, 24, 25, 26, 42 (and others if needed)
3. Click on the 'Calculate!' button for each value and verify the result.
---
**[BUG-0005] Unsupported integer values should return an error**

**Description:**

The spec specifies that only integer values in the range of 0-170 are supported.  However, the behavior of entering an unsupported integer values raises usability issues.  Currently, entering an integer between 170-989 returns an error 'The factorial of <num> is: Infinity', while entering an integer with a value >= 990 or < 0 does not return anything.  The problem of not returning anything is that the result displayed remains the one from the previous input.  For example, if a user does the following sequentially:

**Enter 1 and click the ‘Calculate!’ button.  The result is ‘The factorial of 1 is: 1’.**

**Enter -1 and click the ‘Calculate!’ button.  The result is not cleared.  It remains as ‘The factorial of 1 is: 1’.**

Or if a user does the following sequentially:

**Enter ABCD and click the ‘Calculate!’ button.  The result is ‘Please enter an integer’.**

**Enter -1 and click the ‘Calculate!’ button.  The result is not cleared.  It remains as ‘Please enter an integer’.**

This is confusing to the user.  Entering all these unsupported integers should raise a proper error to make the application more user friendly. 

**How to Reproduce:**
1. Visit http://qainterview.pythonanywhere.com/
2. Enter integer values from the range 170-989, greater than 989, and negative integer values. 
3. Click on the 'Calculate!' button for each value and verify the result.
---

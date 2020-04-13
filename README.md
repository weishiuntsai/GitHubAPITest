# GitHub API Test
This set of tests is designed to test some GitHub APIs from https://api.github.com.  Tests were implemented using REST Assured, with Maven and JUnit.

## Test Scenarios
```
test_request():
  Test HTTP REQUEST (with GET) on https://api.github.com to verify the list of APIs and endpoints.
```
```
test_get():
  Test HTTP GET on https://api.github.com/users/{user} by accessing the repo weishiuntsai and verifying 
  the information.
```
```
test_head():
  Test HTTP HEAD on https://api.github.com/feeds (API exists) and 
  https://api.github.com/doesntexist (API does not exist).  Verify the status code.
```
```
test_put():
  Test HTTP PUT on https://api.github.com/repos/:owner/:repo/contents/:path by updating a file
  /repos/weishiuntsai/test_repo/contents/test.txt with new contents.
```
```
test_delete():
  Test HTTP DELETE on https://api.github.com/repos/:owner/:repo/contents/:path by adding a new file
  /repos/weishiuntsai/test_repo/contents/new_test.txt first and then deleting it.
```

## How to run the tests
The tests were implemented using maven and JUnit.  Follow the following steps to set up the environment on a Linux machine. (NOTE: test_put() and test_delete() require providing password in userPass variable in myapp/src/test/java/io/my/app/AppTest.java.  It currently would fail without the password.)
1. Download the GitHub repository https://github.com/weishiuntsai/GitHubAPITest/ as a zip file.
2. `unzip GitHubAPITest-master.zip`
3. `cd GitHubAPITest-master/myapp`
4. `mvn test`

## Test layout
This test contains a test file **myapp/src/test/java/io/my/app/AppTest.java** and a pom.xml file **myapp/pom.xml**.  There is also a mock file  **jenkinsfile** that describes steps in the test stage for this test to be automatically downloaded and triggered by Jenkins. 

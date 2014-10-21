Meta:
@category api
@author dennis.schulte<at>codecentric.de

Scenario: API Test

Given a Request request.xml

When the API example-service is called

Then the response matches response.xml

*** Settings ***
Library           requests

*** Test Cases ***
case 1
    ${res}    requests.get    http://172.29.4.47:36000/hello    text
    should contain    ${res.text}    Hello

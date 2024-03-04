# Microservices with Spring Boot and Spring Cloud
## Modules

* [Config Server] ( this module is used to configure the properties of the microservices)
* [Eureka Server] ( this module is used to register the microservices)
* [api-gateway] ( this module is used to route the requests to the microservices)
* [authenticationservice] ( this module is used to authenticate the user)
* [userservice] ( this module is used to manage the users)

## How to run the application

* Clone the repository
* Run the config server
* Run the eureka server
* Run the api-gateway
* Run the userservice
* Run the authenticationservice

.[!IMPORTANT].
YO HAVE TO RUN THE MODULES IN THE ORDER THAT IS DESCRIBED ABOVE

## How to test the application

* enter to the url http://localhost:8583/Login with the user sotoleal and the password sotoleal123 just like this:
{
    "userName": "sotoleal",
    "password": "sotoleal123"
}
* then use the token to access to the userservice

## How to run the application with docker

* use mvn clean package to generate the jar files
* use docker-compose up -d build to run the application
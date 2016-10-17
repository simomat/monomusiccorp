# monomusiccorp

... slice it down!


So I stumbled into microservices recently and found some books and articles about it. When I decided to get my hands wet, I looked around for a dojo.
But most stuff out there is more about the ops part of devops - service creation, dockerizing, ... etc. (like http://accordance.github.io/microservice-dojo/).

Instead of writing a microservices application on the green, I also wanted to make some experience with transforming a monolith into a microservices based application and also learn more about the DDD approach.

Inspired by the example domain of "MusicCorp" appearing in Sam Newmans "Building Microservices", I present you the _Monolithic Music Corporation_ - monomusiccorp.


#### Tech stack
- Spring Boot serving with JSON/REST interfaces; right now with HTTP Basic Auth managed by Spring Security
- Spring Data JPA providing repositories etc. on top of JPA2 defined Entities
- EclipseLink as JPA implementation
- Apache Derby as database
- UI: no UI, consider the level3 REST API as top level. See below for detailed description.

#### REST API under the hood
To reduce the overhead of maintaining a UI, `monomusiccorp` provides a HAL-based REST API as top-level. Because it is self-describing, this standard allows to "browse" the API for humans. Therefor, the REST+HAL browser borrowed from [Mike Melly](https://github.com/mikekelly/hal-browser) is included. It allows to follow related transitions and to customise HTTP methods and parameters. 

###### HAL implementation
In general, Spring-HATEOAS helps to manage resources and links, but link-building still lacks support of templates (which are described in the HAL standard). Also, a general support for an API description service ("CURIEs") is provided, but there's no automatic information gathering mechanism.

To serve a well self-describing interface, both link building and CURIE information gathering is (re-)implemented in `monomusiccorp`. 

#### Run it
- Check out the repository, go to path
- Having `gradle` installed, run `./gradlew run` 
- Go to `http://localhost:8080`, log in with user 'admin', password 'admin'

Things to be done
-----------------
- [ ] Add more business behavior. Billing, business reports, ...


# monomusiccorp

... slice it down!


So I stumbled into microservices recently and found some books and articles about it. When I decided to get my hands wet, I looked around for a dojo.
But most stuff out there is more about the ops part of devops - service creation, dockerizing, ... etc. (like http://accordance.github.io/microservice-dojo/).

Instead of writing a microservices application on the green, I also wanted to make some experience with transforming a monolith into a microservices based application and also learn about the DDD approach.

Inspired by the example domain of "MusicCorp" appearing in Sam Newmans "Building Microservices", I present you the Monolithic Music Corporation - monomusiccorp.


#### Tech stack
- Spring Boot serving with JSON/REST interfaces; right now with HTTP Basic Auth managed by Spring Security
- Spring Data JPA providing repositories etc. on top of JPA2 defined Entities
- EclipseLink as JPA implementation
- Apache Derby as database
- UI: no UI, consider the level3 REST API as top level. To simplify testing of REST APIs and not using cUrl all the way, there is a REST+HAL browser included, borrowed from [Mike Melly](https://github.com/mikekelly/hal-browser).

#### Run it
- Check it out the repository, go to path
- Having `gradle` installed, run `./gradlew run` 
- Go to `http://localhost:8080`, log in with user 'admin', password 'admin'

Things to be done
-----------------
- [ ] Add more business behavior. Billing, business reports, ...


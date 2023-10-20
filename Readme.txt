Orac recommendation system.

Users rate and recommend resources.

Descripition: Recommender.pdf

1. Add users.
2. Users rate resources.
3. Update users' friends to be those users that have similar resource ratings.
4. Use friends' ratings to recommend resources to user.
5. Rate recommended resources.
6. Repeat.

See Orac.java for the java API.
See rest/Orac.java and REST_API.txt for the REST API.
See rest/client/OracClient.java for the REST client.

Server:
Import and build Orac Eclipse project.
Export Orac.war tomcat servlet.
Deploy Orac.war to tomcat.

Client:
Select OracClient.java and run as application.

HTML:
Load Orac.html in browser.

REST prefix for localhost: http://localhost:8080/Orac/rest/service/

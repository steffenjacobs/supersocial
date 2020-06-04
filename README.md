# Supersocial

## Supersocial — Social Media Marketing on a new Level
[Supersocial](https://supersocial.cloud) helps you to manage your social media pages and kick off marketing campaigns. 

## Prerequisites
[Docker](https://www.docker.com/), [Docker-Compose](https://docs.docker.com/compose/) and [Git](https://git-scm.com/) need to be installed.

## Deployment
1. Clone the project: `git clone https://github.com/steffenjacobs/supersocial.git`.
2. Build and start Supersocial with `docker-compose up --build`.
3. Open `localhost:9001` in your web browser.
4. Set up a system twitter account to fetch the twitter trends and convert user locations.
5. Enjoy!

## Local Development
To setup supersocial locally for development purposes, this can be done with Docker and Docker-Compose.

    $ docker-compose up --build
    # stop
    $ docker-compose down
----

For development purposes, frontend and backend can be started separately.
1. Case 1: Start **Frontend** locally:
    * Use `npm start` to start the frontend on http://localhost:3000
    * For the frontend to connec to the backend inside the docker-compose, the file `./frontend/src/DeploymentManager.tsx` has to be adjusted and the following two properties need to be modified 
        * `local` has to be set to `true` and 
        * `local8080` has to be set to `false`.
2. Case 2: Start **Backend** locally:
    * The backend can be started via maven or directly from an IDE.
    * For the frontend to connect to the local backend, the file `./frontend/src/DeploymentManager.tsx` has to be modified again:
        * `local` and 
        * `local8080` have to be set to `true`.

----
Warning: All persistent data are stored in volumes. For a clean reboot, these has to be pruned:

    $ docker-compose down
    $ docker volume prune

## Access
Supersocial is available under the following URLs:
* http://localhost:9001/ - React Frontend
* http://localhost:9002 - Spring Boot Backend

## Further Documentation
Further documentation can be found [here](https://confluence.supersocial.cloud/display/SP/User+Guide).

# Supersocial

## Supersocial - Social Media Marketing on a new Level
Supersocial helps you to manage your social media pages and kick off marketing campaigns. 

## Local Development
To setup supersocial locally for development purrposes, this can be done with Docker and Docker-Compose.

    $ docker-compose up --build
    # stoppen
    $ docker-compose stop
----

For development purposes, frontend and backend can be started sepeartely.
1. Case 1: Start **Frontend** locally:
    * Use `npm start` to start the frontend on http://localhost:3000
    * For the frontend to connecto the backend inside the docker-compose, the file `./frontend/src/DeploymentManager.tsx` has to be adjusted and the following two properties need to be modeified 
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
* http://localhost:9001/  -  React Frontend
* http://localhost:9002 - Spring Boot Backend



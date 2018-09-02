# ArgusInterview

## Implementation of a service that will run on two seperate servers and will expose an HTTP api and respond to HTTP GET requests with the payload in the latest POST requests received

### Goal:
* Create a service that will run on 2 machines. 
* The user will send an HTTP POST request to one of the instances, followed by an HTTP GET request to either instance of the service. 
* The HTTP response to the HTTP GET request will include the payload sent in the previous HTTP POST request.

### Service Requirements:
* Use Git for the source control.
* Nice to have: 
  * Use Docker for the deployment.

### Grading:
* 30% - doing the job.
* 30% - time (100% <= 3 days; 0% after 1 week).
* 20% - tests and security.
* 20% - style (implementation choices, readability, cleanness).

### Running:
* Deploy jar file to target machine.
* Create config\application.properties in the same library as the jar file.
* Add a new property to the application.properties file: "application.neighbor.ip=<other host>".
* The property should store the ip address or hostname of the other machine running the service.
* Run in command line (replace <PATH_TO_JAR> with the full path to the located jar filew from the previous bullet):
```
java -jar <PATH_TO_JAR>
```

### Interacting With The Service:
#### GET Request:
* Send the following GET request to either instance of the service:
```
http://<host>:8080/api/resource
```
Or:
```
GET /api/resource HTTP/1.1
Host: <host>:8080
Cache-Control: no-cache
```
#### POST Request:
* Send the following POST request to either instance of the service:
```
http://<host>:8080/api/resource
```
Or:
```
POST /api/resource HTTP/1.1
Host: <host>:8080
Content-Type: application/json
Cache-Control: no-cache

<payload>
```

## Under The Hood:
* When started, the services have the IP address/ hostname of the other service.
* When an HTTP POST request is received by an instance of the service, the service will update it's cache with the new payload it received.

![Stage 1](stage1.png?raw=true "Stage 1")

* The service will then create and send a JSON message to the other instance of the service.
* The sent JSON message will include the following fields:
  * timestamp: a long representing the time in which the POST request payload was updated in the cache in ms since epoch.
  * payload: a string which includes the payload received in the POST request.
* The service will then send the JSON message as part of an HTTP POST request to the other instance of the service like so:
```
POST /api/sync HTTP/1.1
Host: <host>:8080
Content-Type: application/json
Cache-Control: no-cache

{"timestamp":1535913184442,"payload":"{ABC: 123}"}
```
![Stage 2](stage2.png?raw=true "Stage 2")

* Upon receiving an HTTP POST request with a sync payload, the service will parse the received JSON message, check if the timestamp is newer than the one currently in the cache, and only if so, will it update the cache with the received payload.
* Any subsequent HTTP GET requests received by either of the instances will be answered with the payload sent in the last HTTP POST request received.

### Notes:
* The service is currently run as an application and not a service 
* The service has very rich logs which are configured to be printed to the console.
* The logs include:
 * Incoming messages.
 * Outgoing messages.
 * What stage the service is in.
 * Loaded configurations.
 * Errors

![Stage 3](stage3.png?raw=true "Stage 3")

### Future improvements:
- [ ] Add security features to service.
- [ ] Dockerize service.
- [ ] Add tests to project.
- [ ] Support syncing between multiple services.
- [ ] Add auto update to connected clients when new payload has been sent to service.
- [ ] Improve code efficiency.
- [ ] Run as service instead of application.

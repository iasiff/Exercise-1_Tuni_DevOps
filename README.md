
# Docker-compose an microservices hands-on

The purpose of this exercise is to recap how to create a system of two interworking
services that are started up and stopped together. This requires creation of your own Dockerfiles
and docker-compose.yaml, and also development of simple applications code.

# Task definition
In this exercise we will build a simple system composed of three small services. Two of them
(Service1 and Service2) implemented in different programming languages. A
third service (Storage) contain one implementation of shared persistent storage.

#### Service1


1. The only service that can be called from outside.

2. Analyses its state and creates the following record:
Timestamp1: uptime <X> hours, free disk in root: <X>
MBytes.

3. Works as proxy: forwards the received requests to Service2 and Storage /status => Service2 and /log => Storage

4. Stores a log of the incoming requests to two alternative persistent storages: The one maintained by Storage-service. The other implemented with a volume named vStorage

#### Service2


1. Analyses its state and creates the following record:
Timestamp2: uptime <X> hours, free disk in root: <X>
MBytes.

2. Stores a log of the incoming requests to two alternative persistent storages: The one maintained by Storage-service. The other implemented with a volume named vStorage
 
#### Storage


Implements a simple REST-interface:


1. HTTP POST /log => append the incoming record persistently (the log does not disappear when the container is stopped); text/plain is used as a MIME-type

2. HTTP GET /log => gets the content of whole stored log; text/plain is used as MIME-types

**The REST interface and expected behaviour of whole system is the following:**

**GET localhost:8199/status:**



1. Service1 analyses its status and creates the above-described record

2. Service1 sends the created record to Storage (HTTP POST Storage)

3. Service1 writes the record to at the end of vStorage
Service1 forward the request to Service2 (HTTP GET Service2)

4. Service2 analyses its status and creates the above-described record

5. Service2 sends the created record to Storage (HTTP POST Storage)

6. Service2 writes the record to at the end of vStorage
Service2 sends the record as a response to Service1. (text/plain)

7. Service1 combines the records (record1\nrecord2) end returns as a response (text/plain)

**GET localhost:8199/log:**


1. Service1 forwards the request to Storage

2. Returns the content of the log in text/plain.


The persistent storages have two purposes: This exercise we have two implementations of the storage:


1. Simple that mounts the vStorage in container to local file ./vstorage of the host.

2. One that is based on a separate container. The internal implementation of this container is not specified by the teaching staff.

Note that the outputs of these commands

cat ./vstorage

curl localhost:8199/log

should be equal, and contain two (2) lines per received /status-request.
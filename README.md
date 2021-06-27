# PushBullet demo

This is a little repo for demonstrating integrating with PushBullet using a simple Spring app. It is
a little over-engineered, but the idea is to create something that looks like a real production app.

## Requirements

Make sure you have the following locally
```
Maven
Java 11
Bash
Curl
```

## API overview

### `GET /users`
List all users in the app

Response body:
```json
[
  {
    "name": "...",
    "accessToken": "...",
    "creationTime": "...",
    "numOfNotifiactionsPushed": 0
  }
]
```

### `POST /users`
Create a user. 

Request body:
```json
{
  "name": "...",
  "accessToken": "..."
}
```
Response body:
```json
{
  "name": "...",
  "accessToken": "...",
  "creationTime": "...",
  "numOfNotifiactionsPushed": 0
}
```

### `POST /push/{username}`
Send a message to a user with PushBullet

Request body:
```json
{
  "name": "...",
  "title": "...",
  "body": "..."
}
```
Response body:
```json
{
  "name": "...",
  "accessToken": "...",
  "creationTime": "...",
  "numOfNotifiactionsPushed": 1
}
```

## How to run it

### Tests
First, you can try installing the app and running the included unit and integration tests. Just run
```bash
mvn verify
```

There is an extra integration test that is disabled by default at `PushResourceIntTest`. You will
need to provide your own PushBullet access token for this.

### Starting the server
Easy, thanks to Spring-Boot, just run
```bash
mvn spring-boot:run
```

### Running the demo
I've included a little demo script at `./demo.sh`. If you have started the server up in another 
shell, then it will run you through each of the APIs and prompt you for input. Have a PushBullet
access token ready to go!

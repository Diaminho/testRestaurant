# testRestaurant

CRUD service for Recipe and Ingredient.

## Getting Started

Project needed to be imported as gradle project

### Examples

There are several example requests

- **Receive list of all ingredients**

  ```
  $ curl -X GET http://localhost:8080/ingredients
  ```
  Result
  
  ```json
  [ 
    {
      "id":1,
      "name":"Tomato",
      "recipe": {
        "id":1,
        "name":"Sauce",
        "description":"Sauce Description"
      }
    }
  ]
  ```
  
- **Receive ingredient by id**

  ```
  $ curl -X GET http://localhost:8080/ingredients/1
  ```
  Result
  
  ```json
  {
    "id":1,
    "name":"Tomato",
    "recipe": {
      "id":1,
      "name":"Sauce",
      "description":
      "Sauce Description"
    }
  }
  ```  
- **Create ingredient**

  ```
  $ curl -X POST http://localhost:8080/ingredients --data '{"name":"TEST","recipe":{"id":1}}' --header 'Content-Type: application/json' 
  ```
  Result
  
  ```json
  {
    "id":5,
    "name":"TEST",
    "recipe": {
      "id":1,
      "name":"Sauce",
      "description":"Sauce Description"
    }
  }
  ```    
- **Update existing ingredient**

  ```
  $ curl -X PUT http://localhost:8080/ingredients/5 --data '{"name":"TEST_NEW","recipe":{"id":2}}' --header 'Content-Type: application/json' 
  ```
  Result
  
  ```json
  {
    "id":5,
    "name":"TEST_NEW",
    "recipe": {
      "id":2,
      "name":"Steak",
      "description":"Steak Description"
    }
  }
  ```   
- **Delete existing ingredient**

  ```
  $ curl -X DELETE http://localhost:8080/ingredients/5 
  ```
  Result wil be ResponseEntity with 200 status and empty body
  

## Building for production

### Packaging as jar

To build the final jar run:

```
$ ./gradlew clean bootJar
```
## Run
To run the application:

```
$ ./gradlew clean bootRun
```

## Testing

To run tests:

```
$ ./gradlew clean test
```

## Docker

Docker image can be created and pushed to dockerhub via gradle task buildAndPublish

```
$ ./gradlew clean buildAndPublish
```
For running docker container with created image the following command can be used

```
$ docker run -p 8080:8080 diaminho/test-restaurant:latest
```

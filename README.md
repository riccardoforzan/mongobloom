# mongobloom

Simple project for testing using:
- MongoDB & SpringData
- Automagically generated Swagger UI
- Java 21 Record Class (no more Lombok)

[Default Swagger UI documentation](http://localhost:8080/swagger-ui/index.html)

### How to start a local instance of MongoDB 
```shell
docker run  -d -p 27017:27017 \
            -e MONGO_INITDB_ROOT_USERNAME=admin \
            -e MONGO_INITDB_ROOT_PASSWORD=admin \
            mongo:latest
```
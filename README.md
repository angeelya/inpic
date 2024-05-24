# Service for Publishing Images

Implementation of the project "Service for Publishing Images", allowing users to add images online and place them in thematic collections.
The project consists of several modules, which implement the management of pictures, 
user data, subscriptions to other users, notifications and data collection to predict recommendations for the user by pictures and categories.
 
  As a result of implementation:
  -Design and creation of a database. Creating entity classes.
  -Creation of a repository layer that describes methods with Hibernate Session CRUD operations that are subsequently used in services.
  -Creating a layer of services that represents the main logic of the application.
  -Creating a layer of controllers in which endpoints are developed to process requests from clients and return results. 
  -Provided for handling ControllerAdvice exceptions.
  -Creation of DTO for the convenience of receiving data from the client and returning data.
  -Data validation has been implemented.
  -Added custom exception situations.
  -Performed Unit tests.
  -The project is provided with the Security block.
  -Creation of Dockerfile.
  -Swagger documentation.

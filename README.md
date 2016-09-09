# akka-http-multipart-form-data
A basic application to handle multipart form data in akka-htttp

### Run application
sbt run

### Run test cases
sbt test

### Test application
After start the application hit "http://localhost:9000/process/multipart/data" in any rest client with a file and form data, you will get your file uploaded in temp folder of your system and a success response in rest client with the details of your multi part data.

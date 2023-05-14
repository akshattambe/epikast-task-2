# Upload Logfile TO S3 bucket - Project Documentation

### Description
Fetch a publicly available log file, and store it in an existing S3 bucket. Access keys to the S3 bucket is pre-requisite for this project.

### Tools used:
1. junit version 4.13.1
2. mockito version 5.3.1
3. aws-java-sdk-s3 version 1.12.429
4. jdk 17

### How to build and run:

1. Create an AWS account and role for a user.
2. Create a S3 bucket and generate the access key for the role.
3. Create a `profile.properties` file that contains the access key for the role. An example is give below.
4. Clone the `epikast-task-2` repo from GitHub.
5. You will find the project `epikast-log-to-s3` inside `epikast-task-2` repo.
6. Place the `profile.properties` file at the root level of the project `epikast-log-to-s3`.
7. run `mvn clean install` to build the project.
8. Run the CLI command given below.

#### profile.properties file example:
```
[role-name]
aws_access_key_id = <aws_access_key>
aws_secret_access_key = <aws_secret_access_key>
```

### CLI Command:

```
java -jar target/epikast-log-to-s3-0.1.jar --url <url>
```


### Valid Url Path for testing:
```
1. http://80.90.47.7/anupam.acrylic_16.apk.diffoscope.txt
```
### Exception handling

1. MalformedURLException
```
   1. example.com
   2. htt://example.com
   3. exa@mple.com
```
2. UnknownHostException
```
   1. https://notarealhostname.com
   2. https://hostname-that-cannot-be-resolved.com/test.log
```
3. NullPointerException
```
   1. When Amazon S3 client is not initialised due to missing profile prop. file.
```
4. SdkClientException
```
   1. When `aws_access_key_id` & `aws_secret_access_key` is missing from the secrets file i.e. profile.properties file.
   2. When `aws_access_key_id` & `aws_secret_access_key` is incorrect. 
```
5. IllegalArgumentException
```
   1. When config values for the keys like aws.profile.path or aws.region in config YAML is incorrectly defined.
   2. When `profile name` is missing from the secrets file.
```
6. Invalid Inputs:
```
1. null
2. " "
```


### Enhancements:
1. S3 bucket clean up at teardown.
2. Integration tests validating the uploads to S3
3. More Unit tests
4. Performance monitoring
5. Configuration file validation, for e.g using static code analysis.
6. Custom exception

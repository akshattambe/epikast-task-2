## Micronaut 3.9.1 Documentation

- [User Guide](https://docs.micronaut.io/3.9.1/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.9.1/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.9.1/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

- [Micronaut Maven Plugin documentation](https://micronaut-projects.github.io/micronaut-maven-plugin/latest/)

**PROJECTS DETAILS**

Fetch a publicly available log file, and store it in a S3 bucket you will create.

**Tools used:**
1. junit version 4.13.1
2. mockito version 5.3.1
3. aws-java-sdk-s3 version 1.12.429
4. jdk 17

**How to build and run:**

1. Create an AWS account and role for a user.
2. Create a S3 bucket and generate the access key for the role.
3. Create a `profile.properties` file that contains the access key for the role. An example is give below.
4. Clone the `epikast-task-2` repo from GitHub.
5. You will find the project `epikast-log-to-s3` inside `epikast-task-2` repo.
6. Place the `profile.properties` file at the root level of the project `epikast-log-to-s3`.
7. run `mvn clean install` to build the project.
8. Run the CLI command given below.

_profile.properties example:_
```
[role-name]
aws_access_key_id = <aws_access_key>
aws_secret_access_key = <aws_secret_access_key>
```

**CLI Command:**

```
java -jar target/epikast-log-to-s3-0.1.jar --url <url>
```


**Valid Url Path for testing:**
```
1. http://80.90.47.7/anupam.acrylic_16.apk.diffoscope.txt
```
**Exception handling**

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

3. Invalid Inputs:
```
1. null
2. " "
```




**Enhancements:**
1. S3 bucket clean up at teardown.
2. Integration tests validating the uploads to S3
3. More Unit tests
4. Performance monitoring
package com.example.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ErrorMessageList {

    public static List<String> getErrorMessageList(){
        return Arrays.asList("`profile name` field is missing from the secrets file",
                "`profile name` is set incorrect in the secrets file",
                "AWS access key field is not specified in the secrets file",
                "Incorrect AWS access key-Id specified in the secrets file",
                "Incorrect AWS secret-access key specified in the secrets file",
                "Endpoint does not contain a valid host name: null",
                "asdad");
    }
}

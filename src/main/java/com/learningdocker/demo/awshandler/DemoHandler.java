package com.learningdocker.demo.awshandler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.S3Event;

public final class DemoHandler {
    //Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public String handleRequest(S3Event event, Context context) {
        System.out.println("event received");
        LambdaLogger logger = context.getLogger();

        // log execution details
        //logger.log("ENVIRONMENT VARIABLES: " + gson.toJson(System.getenv()));
        //logger.log("CONTEXT: " + gson.toJson(context));
        // process event
        //logger.log("EVENT: " + gson.toJson(event));
        //logger.log("EVENT TYPE: " + event.getClass().toString());
        return "{\n" +
                "  \"name\": \"Hello World\"\n" +
                "}";
    }
}

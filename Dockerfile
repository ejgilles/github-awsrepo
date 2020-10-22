From java:8
EXPOSE 8080
ADD target/learningDockerDemo.jar learningDockerDemo.jar
ENTRYPOINT ["java", "-jar", "learningDockerDemo.jar"]
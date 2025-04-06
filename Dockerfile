# Stage 1: Build
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copy the Maven project files
COPY libs/softnerve-1.0.jar ./libs/
COPY libs/RedisCache-1.0.jar ./libs/


COPY pom.xml .

# Install the local JAR into the local Maven repository (this step gets cached)
RUN mvn install:install-file \
    -Dfile=libs/softnerve-1.0.jar \
    -DgroupId=org.attachment \
    -DartifactId=softnerve \
    -Dversion=1.0 \
    -Dpackaging=jar

RUN mvn install:install-file \
    -Dfile=libs/RedisCache-1.0.jar \
    -DgroupId=com.Ashutosh \
    -DartifactId=RedisCache \
    -Dversion=1.0 \
    -Dpackaging=jar

# Download dependencies based on the pom.xml (caching will be used if no changs)
RUN mvn dependency:go-offline

# Copy the source files after dependencies are set up
COPY src ./src

# Build the application, skipping tests (this step will be cached if src/ doesn't change)
RUN mvn clean package -DskipTests
#RUN mvn clean package


# Stage 2: Runtime
FROM gcr.io/distroless/java21-debian12 AS final
LABEL key="patient"

# Copy the built JAR from the build stage to the runtime stage
COPY --from=build /app/target/UserInfo.jar /app/UserInfo.jar

# Copy Google Cloud credentials to the container
#COPY src/main/resources/sanjeev-440813-8d4d0a091c7e.json /app/sanjeev-440813-8d4d0a091c7e.json

# Expose the application port
EXPOSE 8282
# Define an ARG for the profile
#ARG PROFILE=prod,utho
# Set environment variables
#ENV SPRING_PROFILES_ACTIVE=${PROFILE}
#ENV GOOGLE_APPLICATION_CREDENTIALS=/app/sanjeev-440813-8d4d0a091c7e.json

# Define the entry point to run the application with the prod profile
ENTRYPOINT ["java", "-jar", "/app/UserInfo.jar"]

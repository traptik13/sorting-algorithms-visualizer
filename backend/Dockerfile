FROM openjdk:17-jdk-slim

# Create working directory
WORKDIR /app

# Copy backend files
COPY . .

# Compile Java files
RUN javac -cp ".:gson-2.8.9.jar:mysql-connector-j-9.3.0.jar" *.java

# Expose backend port
EXPOSE 8081

# Run the main server
CMD ["java", "-cp", ".:gson-2.8.9.jar:mysql-connector-j-9.3.0.jar", "MainServer"]

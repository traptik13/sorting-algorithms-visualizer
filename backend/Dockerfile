# Use a base Java image
FROM openjdk:17-jdk-slim

# Set working directory inside container
WORKDIR /app

#  Copy contents of the backend folder from project root into container
COPY backend/ .

# Compile Java files
RUN javac -cp ".:gson-2.8.9.jar:mysql-connector-j-9.3.0.jar" *.java

# Expose backend port
EXPOSE 8081

# Run the backend server
CMD ["java", "-cp", ".:gson-2.8.9.jar:mysql-connector-j-9.3.0.jar", "MainServer"]




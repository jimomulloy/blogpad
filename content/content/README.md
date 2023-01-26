# Build
mvn clean package && docker build -t com.airhacks/content .

# RUN

docker rm -f content || true && docker run -d -p 8080:8080 -p 4848:4848 --name content com.airhacks/content 

# System Test

Switch to the "-st" module and perform:

mvn compile failsafe:integration-test
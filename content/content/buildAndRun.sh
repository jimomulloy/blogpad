#!/bin/sh
mvn clean package && docker build -t com.airhacks/content .
docker rm -f content || true && docker run -d -p 8080:8080 -p 4848:4848 --name content com.airhacks/content 

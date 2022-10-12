from openjdk:13-alpine

run mkdir -p /root/urna/

workdir /root/urna/

add target/urna.jar urna.jar

expose 8080

cmd java -jar urna.jar

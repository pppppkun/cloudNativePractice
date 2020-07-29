FROM openjdk:8u262-jre-slim-buster
ADD ./target/cloud.jar /app/cloud.jar
ADD runboot.sh /app/
WORKDIR /app
RUN chmod a+x runboot.sh
CMD /app/runboot.sh


37993474ee5d283f6b9ef4c05041dc28904e2039
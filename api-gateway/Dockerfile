FROM openjdk:8-jdk-alpine
MAINTAINER dibo.ltd

# Install cURL
RUN echo -e "https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/main\n\
https://mirror.tuna.tsinghua.edu.cn/alpine/v3.4/community" > /etc/apk/repositories

RUN apk --update add curl bash openjdk8 ttf-dejavu git && \
      rm -rf /var/cache/apk/*

ENV TZ=Asia/Shanghai
VOLUME /tmp
ADD api-gateway-2.2.0.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 时区与字符
ENV TZ=Asia/Shanghai
ENV LANG=C.UTF-8

# 把打包好的 jar 拷进来。打包方式：把 ai-agent-station-study-app-1.0-SNAPSHOT.jar 重命名为 app.jar
COPY app.jar /app/app.jar

EXPOSE 8091

ENV JAVA_OPTS="-Xms1g -Xmx2g -XX:+UseG1GC -Dspring.profiles.active=prod --spring.config.location=/app/config/"

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]

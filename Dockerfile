FROM clojure as builder
WORKDIR /jssp/
COPY project.clj /jssp/
COPY src /jssp/src
RUN lein uberjar

FROM openjdk
MAINTAINER "Zhang, Zepeng <redraiment@gmail.com>"
LABEL version="0.2.1"
LABEL description="JVM Scripting Server page (also shortened as JSSP) is a templating system that embeds JVM scripting language into a text document, similar to JSP, PHP, ASP, and other server-side scripting languages."
COPY --from=builder /jssp/target/default+uberjar/jssp-0.2.1-standalone.jar /usr/local/libexec/
WORKDIR /jssp/
ENTRYPOINT ["java", "--add-opens", "java.base/sun.nio.ch=ALL-UNNAMED", "--add-opens", "java.base/java.io=ALL-UNNAMED", "-jar", "/usr/local/libexec/jssp-0.2.1-standalone.jar"]
CMD ["-h"]

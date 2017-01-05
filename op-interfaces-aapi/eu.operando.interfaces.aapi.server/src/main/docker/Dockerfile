FROM tomcat:8

ADD *.war /usr/local/tomcat/webapps/
RUN wget https://raw.githubusercontent.com/osixia/docker-light-baseimage/stable/image/service-available/%3Assl-tools/assets/default-ca/default-ca.pem
RUN keytool -alias openldapsrv -import -file default-ca.pem -keystore $JAVA_HOME/lib/security/cacerts -storepass changeit -noprompt


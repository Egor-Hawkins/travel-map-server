web: java -Dserver.port=$PORT $JAVA_OPTS -jar build/libs/travel-map-0.0.1-SNAPSHOT.jar
release: ./gradlew -Dliquibase.changeLogFile=src/main/resources/db/changelog/db.changelog-master.xml -Dliquibase.url=$JDBC_DATABASE_URL -Dliquibase.promptOnNonLocalDatabase=false liquibase:update

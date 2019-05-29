export JAVA_HOME="/c/program files/java/jdk1.8.0_181"
mvn clean compile assembly:single
cp -rf lib target
cp -rf daemons target

mkdir target/static
cp -rf static/build target/static

zip target/*.jar lib/license.jar
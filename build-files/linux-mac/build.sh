cd ../..
mvn clean compile assembly:single
cd -
cat mksh ../../target/ps3upkg*.jar > ps3upkg
chmod +x ps3upkg
mv ps3upkg ../../ps3upkg

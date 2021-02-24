echo 'Starting elasticsearch'
docker run -d -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.10.1
sleep 10
echo 'Starting super-street-finder'
mvn spring-boot:run -DskipTests

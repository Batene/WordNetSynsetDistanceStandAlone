## Calculating k shortest pathes between two synsets in WordNet

(TODO: Prerequisites, installation, config file content description (src\main\resources\application.properties) etc. )

After mvn install, you can run the jar, optionally determining another port than the default one (8080)
java -jar wordnet.topk-0.0.1-SNAPSHOT.jar --server.port=8081 

(specifying config file location does not work yet: --spring.config.location=file:...)

After running the jar, use a browser or curl to query, e.g.
enter http://localhost:8081/query?s=100017402-n&t=111996783-n&k=4 in a browser
or
open command line and type 
curl -X GET "http://localhost:8081/query?s=100017402-n&t=111996783-n&k=3"

# Calculating k shortest pathes between two synsets in WordNet

We calculate the k shortest pathes between two synsets in WordNet using the Sparql Extension by Vadim Savenkov (https://bitbucket.org/vadim_savenkov/topk-pfn/src/master/). This extension is described in "Counting to k or how SPARQL1.1 Property Paths Can Be Extended to Top-k Path Queries" (Savenkov et al., 2017) (https://dl.acm.org/ft_gateway.cfm?id=3132239&ftid=1930761&dwn=1&CFID=79568758&CFTOKEN=8c313f28073c74d9-F255EF5F-E43A-FDCC-2F86C50671D41DBC). 

## Prerequisites

1) A working Virtuoso Sparql Endpoint
2) WordNet: Obtain the RDF-Version of WordNet under https://wordnet-rdf.princeton.edu/about -> Download . 
WordNet License Information: https://wordnet-rdf.princeton.edu/license
Load this RDF data into Virtuoso (http://vos.openlinksw.com/owiki/wiki/VOS/VirtBulkRDFLoader).
3) topk jar (topk-ce0c13a378.jar) (Apache2.0 License)
How to obtain: 
    1) Clone the project https://bitbucket.org/vadim_savenkov/topk-pfn/src/master/
    2) run ``` mvn install ``` (Note: ``` mvn install ``` of the current version does not work properly. In order to generate the jar, you will therefore need the following work-around: 
        * add dependency to pom.xml
        ```
        <dependency>
          <groupId>com.github.jsonld-java</groupId>
          <artifactId>jsonld-java</artifactId>
          <version>0.12.4</version>
        </dependency>
        ```
        * run 
        ```
        mvn install -Dmaven.test.skip=true 
        ``` 
      after that, you will find the jar in the target/lib directory
      
4) Virtuoso Jena Provider jars (virt_jena3.jar & virtjdbc4.jar) which come with VirtJenaProvider (http://vos.openlinksw.com/owiki/wiki/VOS/VirtJenaProvider) (License?)

You will need to install these 3 jars (topk-ce0c13a378.jar, virt_jena3.jar, virtjdbc4.jar) in your local maven repository (https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html), using the following groupIds, artifactIds and versions:

```
mvn install:install-file -Dfile=<path-to-topk-jar-file> -DgroupId=org.bitbucket.vadim_savenkov -DartifactId=topk -Dversion=ce0c13a378 -Dpackaging=jar
		
mvn install:install-file -Dfile=<path-to-virt_jena-jar-file> -DgroupId=org.apache.jena.virtuoso -DartifactId=virt_jena -Dversion=3.0 -Dpackaging=jar

mvn install:install-file -Dfile=<path-to-virtjdbc-jar-file> -DgroupId=org.apache.jena.virtuoso -DartifactId=virtjdbc -Dversion=4.0 -Dpackaging=jar
```

## Usage

1) Clone this project
2) Import it into eclipse: 
```
Import project from File System -> Browse
```
Select the directory "WordNetSynsetDistanceStandAlone"
3) Edit the configuration file applications.properties (in src/main/resources): Add the missing values and change those values which are given, if you need another value there (e.g. another port)
```virtuosoURLSQLport``` is the url of your Virtuoso Endpoint, together with the port number for the SQL data access (by default 1111, but you might have changed it in your virtuoso.ini file)
4) Select 
```Run as -> Java Application ```
Select "Application - acoli.uni.frankfurt.wordnet.topk" when the window "Select Java Application" pops up.
Wait until the application starts, open browser and start querying , e.g.  http://localhost:8080/query?s=100017402-n&t=111996783-n&k=3 (or use curl, see below)

Instead of running from Eclipse, you can also generate a jar and run it from the command line:
Run ```mvn install```, find the jar that was generated in the target/lib directory, and run it from the command line, optionally determining another port than the default one (8080):
```
java -jar wordnet.topk-0.0.1-SNAPSHOT.jar --server.port=8081 
```

(specifying config file location does not work yet: --spring.config.location=file:...)

After running the jar, use a browser or curl to query, e.g.
enter http://localhost:8081/query?s=100017402-n&t=111996783-n&k=4 in a browser
or
open command line and type 
curl -X GET "http://localhost:8081/query?s=100017402-n&t=111996783-n&k=3"

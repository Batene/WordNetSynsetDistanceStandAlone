# Calculating k shortest pathes between two synsets in WordNet

We calculate the k shortest pathes between two synsets in WordNet using the Sparql Extension by Vadim Savenkov (https://bitbucket.org/vadim_savenkov/topk-pfn/src/master/). This extension is described in "Counting to k or how SPARQL1.1 Property Paths Can Be Extended to Top-k Path Queries" (Savenkov et al., 2017) (https://dl.acm.org/ft_gateway.cfm?id=3132239&ftid=1930761&dwn=1&CFID=79568758&CFTOKEN=8c313f28073c74d9-F255EF5F-E43A-FDCC-2F86C50671D41DBC). 

(TODO: Prerequisites, installation, config file content description (src\main\resources\application.properties) etc. )

## Prerequisites

1) A working Virtuoso Sparql Endpoint
2) WordNet: Obtain the RDF-Version of WordNet under https://wordnet-rdf.princeton.edu/about -> Download . 
WordNet License Information: https://wordnet-rdf.princeton.edu/license
Load this RDF data into Virtuoso (http://vos.openlinksw.com/owiki/wiki/VOS/VirtBulkRDFLoader).
3) topk and topk-pfn jars (https://bitbucket.org/vadim_savenkov/topk-pfn/src/master/) (todo: how to generate the jars and put them into pom.xml), License Information not available


## Usage

After mvn install, you can run the jar, optionally determining another port than the default one (8080)
java -jar wordnet.topk-0.0.1-SNAPSHOT.jar --server.port=8081 

(specifying config file location does not work yet: --spring.config.location=file:...)

After running the jar, use a browser or curl to query, e.g.
enter http://localhost:8081/query?s=100017402-n&t=111996783-n&k=4 in a browser
or
open command line and type 
curl -X GET "http://localhost:8081/query?s=100017402-n&t=111996783-n&k=3"

/*
 *  Copyright 2019 Kathrin Donandt & Antonio Mabiala
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and 
 *  limitations under the License.
 */

package acoli.uni.frankfurt.wordnet.topk;

import java.io.ByteArrayOutputStream;

//import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;

import org.apache.jena.query.ARQ;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.pfunction.PropertyFunctionRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import at.ac.wu.arqext.path.PathPropertyFunctionFactory;
import at.ac.wu.arqext.path.topk;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

@RestController
public class Controller {

	@Value("${virtuosoURLSQLport}")
	String virturlsqlport;
	@Value("${virtuosoUID}")
	String uid;
	@Value("${virtuosoPWD}")
	String pwd;
	@Value("${wordnetontologyURI}")
	String wnontoURI;
	@Value("${wordnetGraphURI}")
	String wnGraphURI;
	@Value("${wordnetSynsetPrefix}")
	String wnSynsetPrefix;	

	public static Model m = null;

	
	@GetMapping("modelStatus")
	public String modelStatus() {//HttpSession session) {
		System.out.println("virturlsqlport: "+virturlsqlport);
		System.out.println("uid: "+ uid);
		System.out.println("pwd: "+pwd);
		//m = (Model)session.getAttribute("model");

		if(m!=null) {
			return "Model initialized successfully!";
		}
		return "Model not loaded :( ";

	}

	@GetMapping("/")
	public String initializeModel() {//HttpServletRequest request) {
		
		
		if(m!=null) {
			return "Model already loaded before"; //+ request.
		}
//		HttpSession session=request.getSession();
//		if(session.getAttribute("model")!=null) {
//			return "Model already loaded in active session";
//		}

		String virtuosojdbc = "jdbc:virtuoso://" + virturlsqlport; /**+ "/charset=UTF-8/log_enable=2";**/
		VirtGraph setConstruct = new VirtGraph (virtuosojdbc,uid,pwd);

		Query constructQuery = QueryFactory.create("PREFIX wordnet-ontology: "
				+ wnontoURI /**"<http://wordnet-rdf.princeton.edu/ontology#>"**/
				+ "PREFIX lemon: <http://lemon-model.net/lemon#>"
				+ "CONSTRUCT { ?x wordnet-ontology:hyponym ?y } "
				+ "FROM "
				+ wnGraphURI /**"<http://wordnet-rdf.princeton.edu> "**/
				+ "WHERE { Graph ?graph {"
				+ "?x wordnet-ontology:hyponym ?y }}");    

		VirtuosoQueryExecution vqeConstruct = VirtuosoQueryExecutionFactory.create (constructQuery, setConstruct);
		System.out.println("Retrieving wordnet subgraph...");
		Model resultModel = vqeConstruct.execConstruct();
		vqeConstruct.close();
		m = resultModel;
//		session.setAttribute("model", m);
		return "Model loaded"; //and set as new session attribute!";
	}

	
	@GetMapping(value="/query",produces = "application/json")
	public String queryModel(HttpServletRequest request) {
		if(m==null) {
			this.initializeModel();
		}
		String s1 = request.getParameter("s");
		String s2 = request.getParameter("t");
		int k = Integer.parseInt(request.getParameter("k"));
		String synset1 = wnSynsetPrefix +s1; //"http://wordnet-rdf.princeton.edu/wn31/"+s1;
		String synset2 = wnSynsetPrefix + s2; //"http://wordnet-rdf.princeton.edu/wn31/"+s2;
		
		
		PropertyFunctionRegistry reg = PropertyFunctionRegistry.chooseRegistry(ARQ.getContext());
		reg.put(topk.URI, new PathPropertyFunctionFactory());
		
		
    	Query queryOnResult = QueryFactory.create(""
        		+ "PREFIX wordnet-ontology: "
        		+ wnontoURI /**"<http://wordnet-rdf.princeton.edu/ontology#>"**/
        		+ "PREFIX ppf:<java:at.ac.wu.arqext.path.>"
        		+ "Select ?path ?length "
        		+ "where  {"
          		+ "BIND(<"
          		+ synset1 /**"<http://wordnet-rdf.princeton.edu/wn31/100017402-n>"**/
          		+ "> as ?a)"
          		+ "BIND(<"
          		+ synset2 /**"<http://wordnet-rdf.princeton.edu/wn31/111692851-n>"**/
          		+ "> as ?b)"
        		+ "?a wordnet-ontology:hyponym+ ?b ."
        		+ "?path ppf:topk (?a ?b "
        		+ k /**"1"**/
        		+ ")"
        		+ "bind("
        		+ "strlen("
        		+ "replace("
        		+ "replace(?path, \"hyponym\", \"Å\")"
        		+ ", \"[^Å]\", \"\""
        		+ ")) as ?length)"
        		+ "} ");

        try ( QueryExecution qExec = QueryExecutionFactory.create(queryOnResult, m) ) {
            ResultSet rsConstruct = qExec.execSelect() ;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ResultSetFormatter.outputAsJSON(outputStream, rsConstruct);
            String json = new String(outputStream.toByteArray());
            System.out.println("result json: "+json);
            return json;            
        }
        
	}
}

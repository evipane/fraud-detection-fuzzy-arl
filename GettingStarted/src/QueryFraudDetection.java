import java.io.InputStream;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class QueryFraudDetection {
	
	public void checkDecision(String firstTransition, String nextTransition, String attribute, String typeAttribute, String predicate, String Value, List<String> frauds, String owlPath)
	{
		OntModel model = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM_MICRO_RULE_INF);
		String inputFileName = owlPath;
		//String inputFileName = "D:\\example.owl";
		InputStream in = FileManager.get().open( inputFileName );
		if (in != null) {
		    System.out.println("File Founded");
		}
		model.read(in, null);
		
		String revPredicate = reversePredicate(predicate);
		
		System.out.println("attribute: " + attribute);
		System.out.println("revPredicate: " + revPredicate);
		System.out.println("Value: " + Value);
		System.out.println("nextTransition: " + nextTransition);
		
		String queryString = "PREFIX bc: <http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#>" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
				"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"SELECT ?x ?y ?c ?z ?a" +
				"WHERE { ?x bc:has_event ?y." + 
				"?y bc:has_"+ attribute +" ?c." +
				"FILTER(?c "+ revPredicate + "\""+ Value +"\")." +
				"?y bc:has_event_id ?z." +
				"?x bc:has_event ?y1." +
				"?y1 bc:has_event_id ?z1." +
				"?y1 bc:has_activity ?a." +
				"FILTER(?z1 = ?z+1)." +
				"FILTER(?a = \""+ nextTransition +"\")}" +
				"ORDER BY(?z)";
		
		/*String queryString = "PREFIX bc: <http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#>" +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
			"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
			"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
			"SELECT ?x ?y ?z ?d ?e" +
			"WHERE { ?x bc:has_event ?y." +
			"?y bc:has_activity ?z." +
			"?y bc:has_duration ?d." +
			"?y bc:done_by ?e." +
			"FILTER(?z = \"check_completeness\")." +
			"FILTER(?d < 30)}";*/
		
		Query query = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();
		String[] Cases;
		while (results.hasNext()) 
		{
			QuerySolution binding = results.nextSolution();
			Cases = binding.get("x").toString().split("#Case_");
			System.out.println(Cases[1]); 
			frauds.add(Cases[1]);
		}
		//ResultSetFormatter.out(System.out, results, query);
		qe.close();
		
		/*FileManager.get().addLocatorClassLoader(Main.class.getClassLoader());
		Model model = FileManager.get().loadModel("example.owl");
		String queryString = "PREFIX bc: <http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#>" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
				"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"SELECT ?x ?y ?z ?d" +
				"WHERE { ?x bc:has_event ?y." +
				"?y bc:has_activity ?z." +
				"?y bc:has_duration ?d." +
				"FILTER(?z = \"check_completeness\")." +
				"FILTER(?d < 30)}" +
				"}";
		Query query = QueryFactory.create(queryString);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		try{
			ResultSet results = qexec.execSelect();
			while( results.hasNext()){
				QuerySolution soln = results.nextSolution();
				Literal name = soln.getLiteral("x");
				System.out.println(name);
			}
		} finally {
			qexec.close();
		}*/
	}
	
	public String reversePredicate(String Predicate)
	{
		if(Predicate.equals("="))
		{
			return "!=";
		}
		else if(Predicate.equals("more than"))
		{
			return "<=";
		}
		else if(Predicate.equals("less than"))
		{
			return ">=";
		}
		else if(Predicate.equals("more than equals"))
		{
			return "<";
		}
		else if(Predicate.equals("less than equals"))
		{
			return ">";
		}
		return Predicate;
	}
}

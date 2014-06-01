import java.io.InputStream;
import java.util.Arrays;
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
	
	public void checkDecision(String firstTransition, String nextTransition, String attribute, String typeAttribute, String predicate, String Value, String nextAttribute, List<String> frauds, String owlPath)
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

		System.out.println("firstTransition: " + firstTransition);
		System.out.println("attribute: " + attribute);
		System.out.println("revPredicate: " + revPredicate);
		System.out.println("Value: " + Value);
		System.out.println("nextTransition: " + nextTransition);
		System.out.println("nextAttribute: " + nextAttribute);
		
		String queryString = "";
		
		if(nextAttribute != "" && nextTransition == "")
		{
			String[] Atr = attribute.split(" ");
			List<String> wordList = Arrays.asList(Atr);
			
			if(wordList.size() > 1)
			{
				System.out.println("Banyak Atribut: " + wordList.size());
				for(String e : wordList)
					System.out.println(e);
				
				queryString = "PREFIX bc: <http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#>" +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
				"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
				"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
				"SELECT ?x ?y0 ?c ?a" +
				"WHERE { ";
				
				for(int i = 0; i < wordList.size(); i++)
				{
					queryString += 	"?x bc:has_event ?y" + i + "." +
						"?y" + i + " bc:has_activity ?b" + i + "." +
						"?y" + i + " bc:has_"+ Atr[i] + " ?c" + i + ".";
				}
				
				queryString += "FILTER(?c0 != ?c1 || ?c2 > ?c3)" + 
							"FILTER(?c4 != \""+ Value +"\")}";
			}
			
			else 
			{
				String[] Val = Value.split(" ");
				System.out.println(Val[0] + " " + Val[1]);
				queryString = "PREFIX bc: <http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#>" +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
						"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
						"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						"SELECT ?x ?y ?c ?a" +
						"WHERE { ?x bc:has_event ?y." +
						"?y bc:has_activity ?b." +
						"?y bc:has_"+ attribute +" ?c." +
						"FILTER(?b= \""+ firstTransition +"\")" +
						"FILTER(?c "+ revPredicate + "\""+ Val[1] +"\")." +
						"?x bc:has_event ?y1." +
						"?y1 bc:has_"+ nextAttribute +" ?a." +
						"FILTER(?a != \""+ Val[0] +"\")" +
						"}";
			}
		}
		
		else if(nextAttribute == ""){
			System.out.println("nextAttribute tidak ada");
			queryString = "PREFIX bc: <http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#>" +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
					"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
					"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
					"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
					"SELECT ?x ?y ?c ?z ?a" +
					"WHERE { ?x bc:has_event ?y." + 
					"?y bc:has_activity ?b." +
					"?y bc:has_"+ attribute +" ?c." +
					"FILTER(?b = \""+ firstTransition +"\")" +
					"FILTER(?c "+ revPredicate + "\""+ Value +"\")." +
					"?y bc:has_event_id ?z." +
					"?x bc:has_event ?y1." +
					"?y1 bc:has_event_id ?z1." +
					"?y1 bc:has_activity ?a." +
					"FILTER(?z1 = ?z+1)." +
					"FILTER(?a != \""+ nextTransition +"\")}" +
					"ORDER BY(?z)";
		}
		
		else if(nextTransition != "" && nextAttribute != ""){
			System.out.println("nextAttribute dan nextTransition ada");
			queryString = "PREFIX bc: <http://www.semanticweb.org/naufal/ontologies/2014/1/untitled-ontology-128#>" +
						"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
						"PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
						"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>" +
						"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
						"SELECT ?x ?y ?c ?a" +
						"WHERE { ?x bc:has_event ?y." +
						"?y bc:has_activity ?b." +
						"?y bc:has_event_id ?z." +
						"?y bc:has_"+ attribute +" ?c." +
						"FILTER(?b= \""+ firstTransition +"\")" +
						"?x bc:has_event ?y0." +
						"?y0 bc:has_"+ nextAttribute +" ?d." +
						"FILTER (?d "+ revPredicate +" ?c)" +
						"?x bc:has_event ?y1." +
						"?y1 bc:has_event_id ?z1." +
						"?y1 bc:has_activity ?a." +
						"FILTER(?z1 = ?z+1)." +
						"FILTER(?a != \""+ nextTransition +"\")" +
						"}";
		}
		
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
	
	}
	
	public String reversePredicate(String Predicate)
	{
		if(Predicate.equals("="))
		{
			return "=";
		}
		else if(Predicate.equals("more than"))
		{
			return ">";
		}
		else if(Predicate.equals("less than"))
		{
			return "<";
		}
		else if(Predicate.equals("more than equals"))
		{
			return ">=";
		}
		else if(Predicate.equals("less than equals"))
		{
			return "<=";
		}
		else if(Predicate.equals("not equals"))
		{
			return "!=";
		}
		return Predicate;
	}
}

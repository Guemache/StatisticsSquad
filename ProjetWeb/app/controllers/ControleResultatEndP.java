package controllers;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.endpoint;

public class ControleResultatEndP extends Controller {
	private static String requete;
	private static String prefixe = "PREFIX sdmx-dimension:  <http://purl.org/linked-data/sdmx/2009/dimension#>"
			+ "PREFIX property:  <http://eurostat.linked-statistics.org/property#>"
			+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "PREFIX sdmx-measure:  <http://purl.org/linked-data/sdmx/2009/measure#>"
			+ "PREFIX skos:    <http://www.w3.org/2004/02/skos/core#>"
			+ "PREFIX xsd:  <http://www.w3.org/2001/XMLSchema#>"
			+ "PREFIX qb:      <http://purl.org/linked-data/cube#>"
			+ "PREFIX  rdfs:    <http://www.w3.org/2000/01/rdf-schema#>"
			+ "PREFIX StatisticSquade: <http://www.StatisticSquade.fr#>"
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			+ "PREFIX dc: <http://purl.org/dc/elements/1.1/> ";

	private static InfModel infModel = null;
	public static final String rdf_file0 = "resources/local-data-base.rdf";
	public static final String rdf_file1 = "input/crim_gen.ttl";
	public static final String rdf_file2 = "input/crim_gen.rdf";
	private static final String test = "http://xmlns.com/foaf/0.1/";
	private static final GraphCreation gc = new GraphCreation("crim_gen");
	private static StatisticsOntology st = StatisticsOntology
			.getInstance("crim_gen");;

	public static Result result() {

		requete = Form.form().bindFromRequest().get("requete");
		
		String req = prefixe + requete;
		System.out.println(req);
		infModel = st.getInfModel();
		// je lis les deus fichier .RDF et .ttl pour le sujet crime
		// String ns = "http://www.statisticSquad.fr/";
		// infModel.setNsPrefix("StatisticSquade", ns);
		// / name space de eurostat
		// String nsEuro = "http://eurostat.linked-statistics.org/data/";
		// infModel.setNsPrefix("Eurostat", nsEuro);
		FileManager.get().readModel(infModel, rdf_file0);
		FileManager.get().readModel(infModel, rdf_file1);

		Query query = QueryFactory.create(req); // rdq2 et rdq1 les noms des
												// deux requÈtes
		QueryExecution qexec = QueryExecutionFactory.create(query, infModel);

		// formatage de la requÈte ;)
		ResultSet rs = qexec.execSelect();
		String resultat = ResultSetFormatter.toList(rs).toString();

		qexec.close();

		return ok(endpoint.render(resultat));

	}

}

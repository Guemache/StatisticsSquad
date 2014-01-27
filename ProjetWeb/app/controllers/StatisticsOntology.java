package controllers;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Alt;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Bag;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelChangedListener;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.NsIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.RDFWriter;
import com.hp.hpl.jena.rdf.model.RSIterator;
import com.hp.hpl.jena.rdf.model.ReifiedStatement;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceF;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Seq;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Command;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.shared.ReificationStyle;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;


public class StatisticsOntology  {
	private static String subject = "";
	private static StatisticsOntology instance = null;
	public static final String rdf_file0 = "http://eurostat.linked-statistics.org/data/crim_gen.rdf";
	// public static final String rdf_file1 =
	// "http://eurostat.linked-statistics.org/dsd/crim_gen.ttl";
	// public static final String rdf_foaf =
	// "/home/liquideath/workspace-indigo-EE/TawebFragment/index.rdf";
	public static final String ns = "http://www.statisticSquad.fr/";
	public static final String euroNs = "http://eurostat.linked-statistics.org/data/";
	// public static final String foafNs = "http://xmlns.com/foaf/spec/";
	private static Resource rootResource = null;
	private static InfModel infModel = null;
	private static Model model = null;
	private static final String foafNs = "http://xmlns.com/foaf/0.1/";

	private StatisticsOntology(String s) {
		/*
		 * javax.xml.namespace.QName qn= new QName("http://statisticSquade.fr/",
		 * "statisticSquade"); javax.xml.namespace.QName qn2= new QName(euroNs,
		 * "eurostat");
		 */
		subject = s;
		// Création du modèle
		Model m = ModelFactory.createDefaultModel();
		model = m;
		// Intégration du modèle dans un modèle inféré
		infModel = ModelFactory.createRDFSModel(m);
		// Définition des namespaces des fichiers à importer
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("statisticSquad", ns);
		map.put("Eurostat", euroNs);
		map.put("Foaf", foafNs);
		/*
		 * infModel.setNsPrefix("StatisticSquade",ns);
		 * infModel.setNsPrefix("Eurostat", euroNs);
		 */
		infModel.setNsPrefixes(map);
		// Définition de notre namespace
		// String ns = "http://www.statisticSquade.fr#";

		// Reading rdf files
		FileManager.get().readModel(infModel, rdf_file0);
		// FileManager.get().readModel(infModel, rdf_file1);
		// FileManager.get().readModel(infModel, rdf_foaf);

		// recuperation de la classe crim-gen depuis eurostat et sur la
		// quelle on va se basé pour faire la liaison
		// Resource SE = infm.getResource(nsEuro+"crim_gen");
		Resource rootResource = infModel.getResource(euroNs + subject);
		// Classes définies

		// Stats Data
		Resource covariance = infModel.createResource(ns + "Covariance");
		Resource correlation = infModel.createResource(ns + "Correlation");
		Resource standardSeviation = infModel.createResource(ns
				+ "StandardSeviation");
		Resource moyen = infModel.createResource(ns + "Moyen");
		infModel.add(correlation, RDF.type, RDFS.Class);
		infModel.add(moyen, RDF.type, RDFS.Class);
		infModel.add(covariance, RDF.type, RDFS.Class);
		infModel.add(standardSeviation, RDF.type, RDFS.Class);

		//

		Resource graph = infModel.createResource(ns + "Graph");
		Resource subj = infModel.createResource(ns + "Subject");
		Resource comment = infModel.createResource(ns + "Comment");
		Resource report = infModel.createResource(ns + "Report");
		Resource study = infModel.createResource(ns + "Study");
		Resource user = infModel.createResource(ns + "User");
		// Resource personClass = infModel.getResource(FOAF.Person);
		Resource beginDate = infModel.createResource(ns + "BeginDate");
		Resource finishDate = infModel.createResource(ns + "FinishDate");
		Resource date = infModel.createResource(ns + "Date");
		Resource value = infModel.createResource(ns + "Value");

		Resource ss = infModel.createResource(ns + "Subject#" + subject);
		infModel.add(ss, RDF.type, subj);
		infModel.add(ss, OWL.sameAs, rootResource);

		// ----------------TESTS------------------

		Resource g1 = infModel.createResource(ns + "Graph#G1");
		infModel.add(g1, RDF.type, graph);
		infModel.add(g1, DC.subject, ss);

		// ---------------------------------------

		infModel.add(user, RDF.type, RDFS.Class);
		infModel.add(value, RDF.type, RDFS.Class);
		infModel.add(beginDate, RDF.type, RDFS.Literal);
		infModel.add(finishDate, RDF.type, RDFS.Literal);
		infModel.add(date, RDF.type, RDFS.Class);
		infModel.add(graph, RDF.type, RDFS.Class);
		infModel.add(subj, RDF.type, RDFS.Class);
		infModel.add(comment, RDF.type, RDFS.Class);
		infModel.add(report, RDF.type, RDFS.Class);
		infModel.add(study, RDF.type, RDFS.Class);
		// Resource documentClass = infModel.getResource(foaf + "Document");

		// Subclasses

		infModel.add(user, RDFS.subClassOf, FOAF.Person);
		infModel.add(comment, RDFS.subClassOf, FOAF.Document);

		//

		// Properties

		Property hasCom = infModel.createProperty(ns + "hasComment");
		Property bDate = infModel.createProperty(ns + "beginDate");
		Property fDate = infModel.createProperty(ns + "finishDate");
		Property hasCovariance = infModel.createProperty(ns + "hasCovariance");
		Property hasStandardSeviation = infModel.createProperty(ns
				+ "hasStandardSeviation");
		Property hasMoyen = infModel.createProperty(ns + "hasMoyen");
		Property aDate = infModel.createProperty(ns + "aDate");
		Property aValue = infModel.createProperty(ns + "aValue");
		Property hasSubject = infModel.createProperty(ns + "hasSubject");
		Property hasLiteral = infModel.createProperty(ns + "hasLiteral");
		Property relatedToGraph = infModel
				.createProperty(ns + "relatedToGraph");
		Property hasCorr = infModel.createProperty(ns + "hasCorrelation");
		Property postDate = infModel.createProperty(ns + "postDate");

		// graph.addProperty(aDate, date);
		// Liaisons
		infModel.add(comment, postDate, RDFS.Literal);
		infModel.add(study, relatedToGraph, graph);
		infModel.add(graph, bDate, beginDate);
		infModel.add(graph, fDate, finishDate);
		infModel.add(graph, hasCom, comment);
		infModel.add(study, hasCovariance, covariance);
		infModel.add(study, hasStandardSeviation, standardSeviation);
		infModel.add(study, hasMoyen, moyen);
		infModel.add(study, hasCorr, correlation);
		infModel.add(graph, aDate, date);
		infModel.add(date, aValue, value);
		infModel.add(graph, hasSubject, subj);
		infModel.add(date, hasLiteral, RDFS.Literal);
		infModel.add(value, hasLiteral, RDFS.Literal);
		infModel.add(comment, hasLiteral, RDFS.Literal);
		infModel.add(covariance, hasLiteral, RDFS.Literal);
		infModel.add(moyen, hasLiteral, RDFS.Literal);
		infModel.add(correlation, hasLiteral, RDFS.Literal);
		infModel.add(standardSeviation, hasLiteral, RDFS.Literal);
		infModel.add(study, hasCom, comment);
		infModel.add(study, hasSubject, subject);

	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public static StatisticsOntology getInstance(String s) {
		if (instance == null)
			return new StatisticsOntology(s);
		else {
			return instance;
		}
		
	}

	public InfModel getInfModel() {
		return this.infModel;
	}

	public Resource getRootResource() {
		return rootResource;
	}

	/*
	 * public static String getRdfFile0() { return rdf_file0; }
	 * 
	 * public static String getRdfFile1() { return rdf_file1; }
	 */

	public static String getSubject() {
		return subject;
	}

	
}


package controllers;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;




public class GraphCreation implements PostGraph, GetGraph {
	
	private StatisticsOntology ontology = null;
	private static final String ns = "http://www.statisticSquad.fr/";
	// private static final String test = "http://xmlns.com/foaf/0.1/";
	private static final String localData = "resources/local-data-base.rdf";
	//private static final String euroNS = "http://eurostat.linked-statistics.org/data/";
	//private static final String foaf = "http://xmlns.com/foaf/0.1/";

	// public static final String foafNs = "http://xmlns.com/foaf/spec/";

	// public static final String rdf_foaf =
	// "/home/liquideath/workspace-indigo-EE/TawebFragment/index.rdf";

	public GraphCreation(String subject) {
		this.ontology = StatisticsOntology.getInstance(subject);
	}

	@Override
	public boolean postGraph(String subject, String country, String values,
			String years) {
		InfModel infModel = ontology.getInfModel();
		FileManager.get().readModel(infModel, localData);
		String graphId = subject + "-" + country;
		Resource graphClass = infModel.getResource(ns + "Graph");
		Resource subjectClass = infModel.getResource(ns + "Subject");
		Resource dateClass = infModel.getResource(ns + "Date");
		Resource valueClass = infModel.getResource(ns + "Value");
		Property aDate = infModel.getProperty(ns + "aDate");
		Property aValue = infModel.getProperty(ns + "aValue");
		// Property hasSubject = infModel.getProperty(ns + "hasSubject");
		Property hasLiteral = infModel.getProperty(ns + "hasLiteral");

		Resource graph = infModel.getResource(ns + "Graph#" + graphId);
		Resource sub = infModel.getResource(ns + "Subject#" + subject);
		if (graph == null && sub != null) {
			graph = infModel.createResource(ns + "Graph#" + graphId);
			infModel.add(graph, RDF.type, graphClass);
			// /////////////////// Will have to test if the subject given by the
			// user is the same as crim_gen
			Resource subj = infModel.getResource(ns + "/Subject#crim_gen");
			infModel.add(graph, DC.subject, subj);
			// /////////////////////

		}
		if (sub == null && graph != null) {
			sub = infModel.createResource(ns + "Subject#" + subject);
			infModel.add(sub, RDF.type, subjectClass);
		}
		if (sub != null && graph != null) {
			// enrichir les données
		}
		if (sub == null && graph == null) {
			graph = infModel.createResource(ns + "Graph#" + graphId);
			infModel.add(graph, RDF.type, graphClass);
			sub = infModel.createResource(ns + "Subject#" + subject);
			infModel.add(sub, RDF.type, subjectClass);
		}
		infModel.add(graph, DC.subject, sub);
		String[] dates = years.split("-");
		String[] valuesT = values.split("-");
		if (dates.length == valuesT.length) {
			for (int i = 0; i < dates.length; i++) {
				String idDate = "Date#" + graphId + "---" + dates[i];
				Resource d = infModel.getResource(ns + idDate);
				if (d == null) {
					d = infModel.createResource(ns + idDate);
					infModel.add(d, RDF.type, dateClass);
					Literal ld = infModel.createLiteral(dates[i]);
					infModel.add(d, hasLiteral, ld);
				}
				String idValue = "Value#" + valuesT[i];
				Resource v = infModel.getResource(ns + idValue);
				Literal lv = infModel.createLiteral(valuesT[i]);
				if (v == null) {
					v = infModel.createResource(ns + idValue);
					infModel.add(v, RDF.type, valueClass);
					infModel.add(v, hasLiteral, lv);
				}
				infModel.add(graph, aDate, d);
				infModel.add(d, aValue, v);
			}
			return true;
		}
		return false;
	}

	public boolean save() {
		InfModel infModel = ontology.getInfModel();
		FileManager.get().readModel(infModel, localData);
		try {
			FileOutputStream outStream = new FileOutputStream(
					"local-data-base.rdf");
			infModel.write(outStream, "RDF/XML");
			outStream.close();
			/*
			 * FileOutputStream outStream1 = new FileOutputStream(
			 * "local-data-base.n3"); infModel.write(outStream1, "N3");
			 * outStream1.close();
			 */
			return true;
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
			return false;
		} catch (IOException e) {
			System.out.println("IO problem");
			return false;
		}
	}

	@Override
	public boolean postComment(String uri, String u, String c,
			boolean isSimpleGraph) {
		InfModel infModel = ontology.getInfModel();
		FileManager.get().readModel(infModel, localData);
		if (isSimpleGraph) {
			Resource graph = infModel.getResource(ns + "Graph#" + uri);
			if (graph != null) {
				Resource commentClass = infModel.getResource(ns + "Comment");
				Property hasComment = infModel.getProperty(ns + "hasComment");
				Property hasLiteral = infModel.getProperty(ns + "hasLiteral");
				Resource userClass = infModel.getResource(ns + "User");
				Resource user = infModel.createResource(ns + "User#" + u);
				// Property foafMaker = infModel.getProperty(foaf + "maker");
				// Property nick = infModel.getProperty(foaf + "nick");
				Property postDate = infModel.getProperty(ns + "postDate");
				Literal text = infModel.createLiteral(c);
				Date d = new Date();
				Literal pDate = infModel.createLiteral(d.toString());
				Resource comment = infModel.createResource(ns + "Comment#" + u
						+ "---" + d.toString());
				infModel.add(comment, RDF.type, commentClass);
				infModel.add(comment, hasLiteral, text);
				infModel.add(comment, postDate, pDate);
				infModel.add(user, RDF.type, userClass);
				infModel.add(user, FOAF.nick, u);
				infModel.add(graph, hasComment, comment);
				infModel.add(comment, FOAF.maker, user);
				return true;
			} else {
				return false;
			}
		} else {
			Resource study = infModel.getResource(ns + "Study#" + uri);
			if (study != null) {
				Resource commentClass = infModel.getResource(ns + "Comment");
				Property hasComment = infModel.getProperty(ns + "hasComment");
				Property hasLiteral = infModel.getProperty(ns + "hasLiteral");
				Resource userClass = infModel.getResource(ns + "User");
				Resource user = infModel.createResource(ns + "User#" + u);
				// Property foafMaker = infModel.getProperty(foaf + "maker");
				// Property nick = infModel.getProperty(foaf + "nick");
				Property postDate = infModel.getProperty(ns + "postDate");
				Literal text = infModel.createLiteral(c);
				Date d = new Date();
				Literal pDate = infModel.createLiteral(d.toString());
				Resource comment = infModel.createResource(ns + "Comment#" + u
						+ "---" + d.toString());
				infModel.add(comment, RDF.type, commentClass);
				infModel.add(comment, hasLiteral, text);
				infModel.add(comment, postDate, pDate);
				infModel.add(user, RDF.type, userClass);
				infModel.add(user, FOAF.nick, u);
				infModel.add(study, hasComment, comment);
				infModel.add(comment, FOAF.maker, user);
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public ArrayList<String> getComments(String uri, boolean isSimpleGraph) {
		InfModel infModel = ontology.getInfModel();
		FileManager.get().readModel(infModel, localData);
		ArrayList<String> comments = new ArrayList<String>();
		if (isSimpleGraph) {
			Resource graph = infModel.getResource(ns + "Graph#" + uri);
			if (graph == null) {
				System.out
						.println("Erreur de recuperation du graph a partir de l'uri!");
				return null;
			} else {
				Property hasComment = infModel.getProperty(ns + "hasComment");
				Property hasLiteral = infModel.getProperty(ns + "hasLiteral");
				//Property maker = infModel.getProperty(foaf + "maker");
				//Property nick = infModel.getProperty(foaf + "nick");
				Property postDate = infModel.getProperty(ns + "postDate");

				StmtIterator i = graph.listProperties(hasComment);
				while (i.hasNext()) {
					Statement s = i.nextStatement();
					Resource r = (Resource) s.getObject();
					String makerS = "";
					String postD = "";
					StmtIterator id = r.listProperties(postDate);
					while (id.hasNext()) {
						Statement ds = id.nextStatement();
						Literal dd = (Literal) ds.getObject();
						postD = dd.getString();
					}
					StmtIterator iu = r.listProperties(FOAF.maker);
					while (iu.hasNext()) {
						Statement us = iu.nextStatement();
						Resource use = (Resource) us.getObject();
						StmtIterator in = use.listProperties(FOAF.nick);
						while (in.hasNext()) {
							Statement userS = in.nextStatement();
							Literal nn = (Literal) userS.getObject();
							makerS = nn.getString();
						}
					}
					StmtIterator ii = r.listProperties(hasLiteral);
					while (ii.hasNext()) {
						Statement ss = ii.nextStatement();
						Literal comm = (Literal) ss.getObject();
						comments.add(makerS + ";" + postD + ";"
								+ comm.getString());
					}
				}
				return comments;
			}
		} else {
			Resource study = infModel.getResource(ns + "Study#" + uri);
			if (study == null) {
				System.out
						.println("Erreur de recuperation de l'etude a partir de l'uri!");
				return null;
			} else {
				Property hasComment = infModel.getProperty(ns + "hasComment");
				Property hasLiteral = infModel.getProperty(ns + "hasLiteral");
				//Property maker = infModel.getProperty(foaf + "maker");
				//Property nick = infModel.getProperty(foaf + "nick");
				Property postDate = infModel.getProperty(ns + "postDate");

				StmtIterator i = study.listProperties(hasComment);
				while (i.hasNext()) {
					Statement s = i.nextStatement();
					Resource r = (Resource) s.getObject();
					String makerS = "";
					String postD = "";
					StmtIterator id = r.listProperties(postDate);
					while (id.hasNext()) {
						Statement ds = id.nextStatement();
						Literal dd = (Literal) ds.getObject();
						postD = dd.getString();
					}
					StmtIterator iu = r.listProperties(FOAF.maker);
					while (iu.hasNext()) {
						Statement us = iu.nextStatement();
						Resource use = (Resource) us.getObject();
						StmtIterator in = use.listProperties(FOAF.nick);
						while (in.hasNext()) {
							Statement userS = in.nextStatement();
							Literal nn = (Literal) userS.getObject();
							makerS = nn.getString();
						}
					}
					StmtIterator ii = r.listProperties(hasLiteral);
					while (ii.hasNext()) {
						Statement ss = ii.nextStatement();
						Literal comm = (Literal) ss.getObject();
						comments.add(makerS + ";" + postD + ";"
								+ comm.getString());
					}
				}
				return comments;
			}
		}
	}

	@Override
	public boolean postGraph(String graphUri, String anneeDebut, String anneeFin) {
		InfModel infModel = ontology.getInfModel();
		FileManager.get().readModel(infModel, localData);
		Resource graph = infModel.getResource(ns + "Graph#" + graphUri);
		Resource graphClass = infModel.getResource(ns + "Graph");
		Resource beginClass = infModel.getResource(ns + "BeginDate");
		Resource finishClass = infModel.getResource(ns + "FinishDate");
		Property beginDate = infModel.getProperty(ns + "beginDate");
		Property finishDate = infModel.getProperty(ns + "finishDate");
		if (graph == null) {
			graph = infModel.createResource(ns + "Graph#" + graphUri);
			// ///////////////////
			Resource subj = infModel.getResource(ns + "/Subject#crim_gen");
			infModel.add(graph, DC.subject, subj);
			// /////////////////////
			infModel.add(graph, RDF.type, graphClass);
			Resource begin = infModel.createResource(ns + "BeginDate#"
					+ graphUri + "-beginDate");
			Resource finish = infModel.createResource(ns + "FinishDate#"
					+ graphUri + "-finishDate");
			infModel.add(begin, RDF.type, beginClass);
			infModel.add(finish, RDF.type, finishClass);
			Literal newBegin = infModel.createLiteral(anneeDebut);
			Literal newFinish = infModel.createLiteral(anneeFin);
			infModel.add(graph, beginDate, newBegin);
			infModel.add(graph, finishDate, newFinish);
		} else {
			graph.removeAll(beginDate);
			graph.removeAll(finishDate);
			Literal newBegin = infModel.createLiteral(anneeDebut);
			Literal newFinish = infModel.createLiteral(anneeFin);
			infModel.add(graph, beginDate, newBegin);
			infModel.add(graph, finishDate, newFinish);
		}
		return true;
	}

	@Override
	public boolean postGraph(String val1, String val2, String val3,
			String values1, String dates1, String values2, String dates2) {
		postGraph(val1, val2, values1, dates1);
		postGraph(val1, val2, values2, dates2);
		postGraph(val1 + "-" + val2 + "-" + val3, val1 + "-" + val2, val1 + "-"
				+ val3, "", "", "", "", "", "");
		return true;
	}

	@Override
	public boolean postGraph(String graphUri, String subGraphUri1,
			String subGraphUri2, String anneeDebut, String anneeFin,
			String corr, String moy, String ecart, String cov) {
		postGraph(subGraphUri2, anneeDebut, anneeFin);
		postGraph(subGraphUri1, anneeDebut, anneeFin);
		InfModel infModel = ontology.getInfModel();
		FileManager.get().readModel(infModel, localData);
		Resource studyClass = infModel.getResource(ns + "Study");
		Resource study = infModel.getResource(ns + "Study#" + graphUri);
		if (study == null) {
			Resource covClass = infModel.getResource(ns + "Covariance");
			Resource corrClass = infModel.getResource(ns + "Correlation");
			Resource moyClass = infModel.getResource(ns + "Moyen");
			Resource etClass = infModel.getResource(ns + "StandardSeviation");

			Resource covar = infModel.createResource(ns + "Covariance#"
					+ graphUri + "-covariance");
			Resource corre = infModel.createResource(ns + "Correlation#"
					+ graphUri + "-correlation");
			Resource moyen = infModel.createResource(ns + "Moyen#" + graphUri
					+ "-moyen");
			Resource ecartT = infModel.createResource(ns + "StandardSeviation#"
					+ graphUri + "-stSeviation");

			infModel.add(covar, RDF.type, covClass);
			infModel.add(corre, RDF.type, corrClass);
			infModel.add(moyen, RDF.type, moyClass);
			infModel.add(ecartT, RDF.type, etClass);

			Property hasCov = infModel.getProperty(ns + "hasCovariance");
			Property hasCorr = infModel.getProperty(ns + "hasCorrelation");
			Property hasMoy = infModel.getProperty(ns + "hasMoyen");
			Property hasSS = infModel.getProperty(ns + "hasStandardSeviation");
			Property hasLit = infModel.getProperty(ns + "hasLiteral");

			infModel.add(covar, hasLit, cov);
			infModel.add(corre, hasLit, corr);
			infModel.add(moyen, hasLit, moy);
			infModel.add(ecartT, hasLit, ecart);

			study = infModel.createResource(ns + "Study#" + graphUri);
			infModel.add(study, RDF.type, studyClass);
			// ///////////////////
			Resource subj = infModel.getResource(ns + "/Subject#crim_gen");
			infModel.add(study, DC.subject, subj);
			// /////////////////////
			Property graph = infModel.getProperty(ns + "relatedToGraph");
			Resource graph1I = infModel.getResource(ns + "Graph#"
					+ subGraphUri1);
			Resource graph2I = infModel.getResource(ns + "Graph#"
					+ subGraphUri2);
			infModel.add(study, graph, graph1I);
			infModel.add(study, graph, graph2I);

			infModel.add(study, hasCov, covar);
			infModel.add(study, hasCorr, corre);
			infModel.add(study, hasMoy, moyen);
			infModel.add(study, hasSS, ecartT);
			return true;
		}
		return false;
	}


}

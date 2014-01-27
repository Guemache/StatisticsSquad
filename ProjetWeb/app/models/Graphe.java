package models;

import java.util.List;


import play.db.ebean.Model;

public class Graphe extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String graphe;
	public double covariance;
	public double correlation;
	public double moyenne;
	public double ecartType;
	public List<Comment> comments;
	public String ipAdress;
	public String uri;
	public String date;
	
	public Graphe(String graphe,double cov,double cor,double moy, double ecartType,List<Comment> comments,String ip,String uri,String date){
		this.graphe=graphe;
		this.covariance=cov;
		this.correlation=cor;
		this.moyenne=moy;
		this.ecartType=ecartType;
		this.comments=comments;
		this.ipAdress=ip;
		this.uri=uri;
		this.date=date;
	}

	
}

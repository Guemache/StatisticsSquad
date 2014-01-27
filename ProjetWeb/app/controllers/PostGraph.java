package controllers;

public interface PostGraph {
	
	public boolean postGraph(String subject, String country, String values,
			String years);

	public boolean postGraph(String graphUri, String subGraphUri1,
			String subGraphUri2, String values1, String dates1, String values2,
			String dates2);

	public boolean postGraph(String graphUri, String subGraphUri1,
			String subGraphUri2, String anneeDebut, String anneeFin,String corr,String moy,String ecart,String cov);

	public boolean postGraph(String graphUri, String anneeDebut, String anneeFin);

	public boolean postComment(String uri, String user, String comment,boolean isSimpleGraph);

	public boolean save();

}

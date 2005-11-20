package de.jalin.fibu.server.customer;

import net.hostsharing.admin.runtime.GenericData;

public class CustomerData extends GenericData {

	private Integer custid;
	private String firma;
	private Integer bilanzkonto;
	private Integer guvkonto;
	private String jahr;
	private String periode;
	private java.util.Date since;
	private java.util.Date lastupdate;

	public CustomerData() {
		custid = null;
		firma = null;
		bilanzkonto = null;
		guvkonto = null;
		jahr = null;
		periode = null;
		since = null;
		lastupdate = null;
	}

	public Integer getCustid() {
		return custid;
	}
	
	public void setCustid(Integer custid) {
		this.custid = custid;
	}
	
	public String getFirma() {
		return firma;
	}
	
	public void setFirma(String firma) {
		this.firma = firma;
	}
	
	public Integer getBilanzkonto() {
		return bilanzkonto;
	}
	
	public void setBilanzkonto(Integer bilanzkonto) {
		this.bilanzkonto = bilanzkonto;
	}
	
	public Integer getGuvkonto() {
		return guvkonto;
	}
	
	public void setGuvkonto(Integer guvkonto) {
		this.guvkonto = guvkonto;
	}
	
	public String getJahr() {
		return jahr;
	}
	
	public void setJahr(String jahr) {
		this.jahr = jahr;
	}
	
	public String getPeriode() {
		return periode;
	}
	
	public void setPeriode(String periode) {
		this.periode = periode;
	}
	
	public java.util.Date getSince() {
		return since;
	}
	
	public void setSince(java.util.Date since) {
		this.since = since;
	}
	
	public java.util.Date getLastupdate() {
		return lastupdate;
	}
	
	public void setLastupdate(java.util.Date lastupdate) {
		this.lastupdate = lastupdate;
	}
	
	public String[] getAttributeNames() {
		return new String[] { 
				   "custid"
				,  "firma"
				,  "bilanzkonto"
				,  "guvkonto"
				,  "jahr"
				,  "periode"
				,  "since"
				,  "lastupdate"
			};
	}
	
}

// Generiert mit xmlrpcgen

package de.jalin.fibu.server.buchungszeile;

import net.hostsharing.admin.runtime.GenericData;

public class BuchungszeileData extends GenericData {

	private Integer buzlid;
	private Integer buchid;
	private Integer kontoid;
	private Integer betrag;
	private Boolean soll;
	private Boolean haben;

	public BuchungszeileData() {
		buzlid = null;
		buchid = null;
		kontoid = null;
		betrag = null;
		soll = null;
		haben = null;
	}

	public Integer getBuzlid() {
		return buzlid;
	}
	
	public void setBuzlid(Integer buzlid) {
		this.buzlid = buzlid;
	}
	
	public Integer getBuchid() {
		return buchid;
	}
	
	public void setBuchid(Integer buchid) {
		this.buchid = buchid;
	}
	
	public Integer getKontoid() {
		return kontoid;
	}
	
	public void setKontoid(Integer kontoid) {
		this.kontoid = kontoid;
	}
	
	public Integer getBetrag() {
		return betrag;
	}
	
	public void setBetrag(Integer betrag) {
		this.betrag = betrag;
	}
	
	public Boolean getSoll() {
		return soll;
	}
	
	public void setSoll(Boolean soll) {
		this.soll = soll;
	}
	
	public Boolean getHaben() {
		return haben;
	}
	
	public void setHaben(Boolean haben) {
		this.haben = haben;
	}
	
	public String[] getAttributeNames() {
		return new String[] { 
				   "buzlid"
				,  "buchid"
				,  "kontoid"
				,  "betrag"
				,  "soll"
				,  "haben"
			};
	}
	
}

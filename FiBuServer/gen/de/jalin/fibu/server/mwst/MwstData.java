// Generiert mit xmlrpcgen

package de.jalin.fibu.server.mwst;

import net.hostsharing.admin.runtime.GenericData;

public class MwstData extends GenericData {

	private Integer mwstid;
	private Integer mwstsatz;
	private String mwsttext;
	private Integer mwstkontosoll;
	private Integer mwstkontohaben;
	private Boolean mwstsatzaktiv;

	public MwstData() {
		mwstid = null;
		mwstsatz = null;
		mwsttext = null;
		mwstkontosoll = null;
		mwstkontohaben = null;
		mwstsatzaktiv = null;
	}

	public Integer getMwstid() {
		return mwstid;
	}
	
	public void setMwstid(Integer mwstid) {
		this.mwstid = mwstid;
	}
	
	public Integer getMwstsatz() {
		return mwstsatz;
	}
	
	public void setMwstsatz(Integer mwstsatz) {
		this.mwstsatz = mwstsatz;
	}
	
	public String getMwsttext() {
		return mwsttext;
	}
	
	public void setMwsttext(String mwsttext) {
		this.mwsttext = mwsttext;
	}
	
	public Integer getMwstkontosoll() {
		return mwstkontosoll;
	}
	
	public void setMwstkontosoll(Integer mwstkontosoll) {
		this.mwstkontosoll = mwstkontosoll;
	}
	
	public Integer getMwstkontohaben() {
		return mwstkontohaben;
	}
	
	public void setMwstkontohaben(Integer mwstkontohaben) {
		this.mwstkontohaben = mwstkontohaben;
	}
	
	public Boolean getMwstsatzaktiv() {
		return mwstsatzaktiv;
	}
	
	public void setMwstsatzaktiv(Boolean mwstsatzaktiv) {
		this.mwstsatzaktiv = mwstsatzaktiv;
	}
	
	public String[] getAttributeNames() {
		return new String[] { 
				   "mwstid"
				,  "mwstsatz"
				,  "mwsttext"
				,  "mwstkontosoll"
				,  "mwstkontohaben"
				,  "mwstsatzaktiv"
			};
	}
	
}

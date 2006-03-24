// Generiert mit xmlrpcgen

package de.jalin.fibu.server.customer;

import net.hostsharing.admin.runtime.*;

public class CustomerUpdateCall extends AbstractCall {

	private CustomerData setData;
	private CustomerData whereData;

	public CustomerUpdateCall (
			   CustomerData setData
			,  CustomerData whereData
		)
	{
		super("customer", "update");
		this.setData = setData;
		this.whereData = whereData;
	}
	
	public void prepare() {
		addSetProperty("firma", setData.getFirma());
		addSetProperty("bilanzkonto", setData.getBilanzkonto());
		addSetProperty("guvkonto", setData.getGuvkonto());
		addSetProperty("jahr", setData.getJahr());
		addSetProperty("periode", setData.getPeriode());
		addWhereProperty("custid", whereData.getCustid());
		addWhereProperty("firma", whereData.getFirma());
		addWhereProperty("bilanzkonto", whereData.getBilanzkonto());
		addWhereProperty("guvkonto", whereData.getGuvkonto());
		addWhereProperty("jahr", whereData.getJahr());
		addWhereProperty("periode", whereData.getPeriode());
	}
}

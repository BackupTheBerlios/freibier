package de.jalin.fibu.server.customer;

import java.util.*;
import net.hostsharing.admin.runtime.*;

public class CustomerListCall extends AbstractCall {

	private CustomerData whereData;

	public CustomerListCall (
			   CustomerData whereData
		)
	{
		super("customer", "list");
		this.whereData = whereData;
	}
	
	public void prepare() {
		addWhereProperty("custid", whereData.getCustid());
		addWhereProperty("firma", whereData.getFirma());
		addWhereProperty("bilanzkonto", whereData.getBilanzkonto());
		addWhereProperty("guvkonto", whereData.getGuvkonto());
		addWhereProperty("jahr", whereData.getJahr());
		addWhereProperty("periode", whereData.getPeriode());
	}
}

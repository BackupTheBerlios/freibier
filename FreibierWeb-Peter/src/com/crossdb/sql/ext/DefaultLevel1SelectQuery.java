/**
 * @author Travis Reeder - travis@spaceprogram.com
 * Date: Jun 27, 2002
 * Time: 6:57:02 PM
 * 
 */
package com.crossdb.sql.ext;

import com.crossdb.sql.DefaultSelectQuery;

public abstract class DefaultLevel1SelectQuery extends DefaultSelectQuery implements Level1SelectQuery {

	protected int limit[]; // 2 max that will be offset, count

	public void setLimit(int count){
		if(limit == null){
			limit = new int[1];
		}
		limit[0] = count;
	}

	public void setLimit(int offset, int count){
		if(limit == null){
			limit = new int[2];
		}
		limit[0] = offset;
		limit[1] = count;
	}
}

package com.riversoft.platform.mail;

import java.util.Date;

import javax.mail.search.ComparisonTerm;
import javax.mail.search.IntegerComparisonTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SentDateTerm;
import javax.mail.search.SizeTerm;

import jodd.mail.EmailFilter;

/**
 * Created by exizhai on 24/12/2014.
 */
public class CustomerEmailFilter extends EmailFilter {

	/**
	 * 接收时间
	 * 
	 * @param date
	 * @return
	 */
	public CustomerEmailFilter after(Date date) {
		SearchTerm dateTerm = new SentDateTerm(ComparisonTerm.GE, date);
		this.concat(dateTerm);
		SearchTerm intComparisonTerm = new SizeTerm(IntegerComparisonTerm.GE, 1024);
		this.concat(intComparisonTerm);
		return this;
	}

}

/*******************************************************************************
 * Copyright 2018 The MIT Internet Trust Consortium
 *
 * Portions copyright 2011-2013 The MITRE Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
/**
 *
 */
package org.mitre.openid.connect.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import java.util.Date;

/**
 * @author Bejond
 *
 */
@Entity
@Table(name="validate_code")
@NamedQueries({
	@NamedQuery(name = ValidateCode.QUERY_ALL, query = "select vc from ValidateCode vc"),
	@NamedQuery(name = ValidateCode.QUERY_BY_PHONE_NUMBER_AND_APPROVED, query = "select d from ValidateCode d where d.phoneNumber = :" + ValidateCode.PARAM_PHONE_NUMBER + " and d.approved = false"),
	@NamedQuery(name = ValidateCode.QUERY_EXPIRED_BY_DATE, query = "select d from ValidateCode d where d.expiration <= :" + ValidateCode.PARAM_DATE)
})
public class ValidateCode {

	public static final String QUERY_ALL = "ValidateCode.getAll";
	public static final String QUERY_BY_PHONE_NUMBER_AND_APPROVED = "ValidateCode.getByPhoneNumberAndApproved";
	public static final String QUERY_EXPIRED_BY_DATE = "ValidateCode.getByDate";


	public static final String PARAM_PHONE_NUMBER = "phoneNumber";
	public static final String PARAM_DATE = "date";
	// unique id
	private Long id;

	private String phoneNumber;

	private String code;

	private boolean approved;

	private Date createDate;

	private Date expiration;

	public ValidateCode() {
	}

	public ValidateCode(String code, Date createDate, Date expiration) {
		this.code = code;
		this.createDate = createDate;
		this.expiration = expiration;
	}

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	@Basic
	@Column(name = "phone_number")
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	@Basic
	@Column(name = "code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Basic
	@Column(name = "approved")
	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	@Basic
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Column(name = "create_date")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Basic
	@Temporal (javax.persistence.TemporalType.TIMESTAMP)
	@Column(name = "expiration")
	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
}

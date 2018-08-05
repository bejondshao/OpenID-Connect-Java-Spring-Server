/*******************************************************************************
 * Copyright 2018 The MIT Internet Trust Consortium
 * <p/>
 * Portions copyright 2011-2013 The MITRE Corporation
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.mitre.openid.connect.repository.impl;

import org.mitre.openid.connect.model.ValidateCode;
import org.mitre.openid.connect.repository.ValidateCodeRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import static org.mitre.util.jpa.JpaUtil.getSingleResult;
import static org.mitre.util.jpa.JpaUtil.saveOrUpdate;

/**
 * JPA ValidateCode repository implementation
 *
 * @author Bejond
 */
@Repository ("jpaValidateCodeRepository")
public class JpaValidateCodeRepository implements ValidateCodeRepository {

	@PersistenceContext (unitName = "defaultPersistenceUnit")
	private EntityManager manager;

	/**
	 * @param phoneNumber
	 * @return a valid ValidateCode if it exists, null otherwise
	 */
	@Override
	public ValidateCode getByPhoneNumber(String phoneNumber) {
		TypedQuery<ValidateCode> query = manager.createNamedQuery(ValidateCode.QUERY_BY_PHONE_NUMBER_AND_APPROVED, ValidateCode.class);
		query.setParameter(ValidateCode.PARAM_PHONE_NUMBER, phoneNumber);

		return getSingleResult(query.getResultList());
	}

	@Override
	public ValidateCode save(ValidateCode validateCode) {
		return saveOrUpdate(validateCode.getId(), manager, validateCode);
	}

	@Override
	public void remove(ValidateCode validateCode) {
		ValidateCode found = manager.find(ValidateCode.class, validateCode.getId());

		if (found != null) {
			manager.remove(found);
		} else {
			throw new IllegalArgumentException();
		}
	}
}

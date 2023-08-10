/*******************************************************************************
 * Copyright 2019, 2023 Aranjuez Poon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package com.pyrube.one.app.persistence;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.hibernate.query.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.pyrube.one.app.AppException;
import com.pyrube.one.app.Apps;
import com.pyrube.one.app.inquiry.SearchCriteria;
import com.pyrube.one.app.logging.Logger;
import com.pyrube.one.lang.Strings;

/**
 * the <code>Dao</code> contains various methods for DAO layer.
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Dao<D extends Data<?>, ID extends Serializable> {
	
	/**
	 * logger
	 */
	private static Logger logger = Apps.a.logger.named(Dao.class.getName());

	@Resource
	private SessionFactory sessionFactory;
	
	protected Class<D> entityClass;

	/**
	 * constructor
	 */
	public Dao() {}

	protected Class getEntityClass() {
		if (entityClass == null) {
			entityClass = (Class<D>) ((ParameterizedType) getClass()
					.getGenericSuperclass()).getActualTypeArguments()[0];
		}
		return entityClass;
	}

	/**
	 * Finds a data entity by ID
	 * @param id the data entity ID
	 * @return a data entity
	 */
	
	public D find(ID id) throws AppException {
		try {
			D d = (D) this.getSession().find(getEntityClass(), id);
			return d;
		} catch (Exception e) {
			logger.error("SQL error (" + e.getMessage() + ") occurs.", e);
			throw AppException.due("message.error.dao-exception", e);
		}
	}

	/**
	 * Finds a data entity with script
	 * @param script
	 * @param values 
	 * @return a data entity
	 */
	
	public D find(String script, Object... values) {
		Query query = this.getSession().createQuery(script);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		try {
			return (D) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Queries a list of entity with script
	 * @param script
	 * @param values
	 * @return a list
	 */
	
	public List<D> query(String script, Object... values) {
		Query query = this.getSession().createQuery(script);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				query.setParameter(i, values[i]);
			}
		}
		return query.getResultList();
	}

	/**
	 * Queries a page of entities with script
	 * @param script
	 * @param params
	 * @return a list
	 */
	public SearchCriteria<D> query(String script, SearchCriteria criteria, Map<String, ?> params) {
		if (criteria.isSortable()) {
			String alias = criteria.getAlias();
			String sortBy = criteria.getSortBy();
			String altSortBy = criteria.getAltSortBy();
			script += (" order by " + (Strings.isEmpty(alias) || sortBy.contains(".") ? sortBy : alias + "." + sortBy) + " "
					+ (SearchCriteria.DESC.equalsIgnoreCase(criteria.getSortDir()) ? SearchCriteria.DESC : SearchCriteria.ASC)
					+ (Strings.isEmpty(altSortBy) ? Strings.EMPTY 
						: ", " + (Strings.isEmpty(alias) || altSortBy.contains(".") ? altSortBy : alias + "." + altSortBy) + " asc"));
		}
		Query query = this.getSession().createQuery(script);
		if (params != null) {
			query.setProperties(params);
		}
		ScrollableResults results = query.scroll();
		results.last();
		criteria.setTotalRows(results.getRowNumber() + 1);

		List<D> rs = query
				.setFirstResult(criteria.getFirstResult())
				.setMaxResults(criteria.isPageable() ? criteria.getPageSize() : SearchCriteria.MAX_PAGE_SIZE)
				.getResultList();
		if (rs == null) { rs = new ArrayList<D>(); }
		criteria.setResults(rs);
		return criteria;
	}

	/**
	 * Queries a page of entities with script
	 * @param script
	 * @param params
	 * @return a list
	 */
	public SearchCriteria<D> query(String script, SearchCriteria criteria, List<?> params) {
		if (criteria.isSortable()) {
			String alias = criteria.getAlias();
			String sortBy = criteria.getSortBy();
			String altSortBy = criteria.getAltSortBy();
			script += (" order by " + (Strings.isEmpty(alias) || sortBy.contains(".") ? sortBy : alias + "." + sortBy) + " "
					+ (SearchCriteria.DESC.equalsIgnoreCase(criteria.getSortDir()) ? SearchCriteria.DESC : SearchCriteria.ASC)
					+ (Strings.isEmpty(altSortBy) ? Strings.EMPTY 
						: ", " + (Strings.isEmpty(alias) || altSortBy.contains(".") ? altSortBy : alias + "." + altSortBy) + " asc"));
		}
		Query query = this.getSession().createQuery(script);
		if (params != null) {
			for (int i = 0; i < params.size(); i++) {
				query.setParameter(i, params.get(i));
			}
		}
		ScrollableResults results = query.scroll();
		results.last();
		criteria.setTotalRows(results.getRowNumber() + 1);

		List<D> rs = query
				.setFirstResult(criteria.getFirstResult())
				.setMaxResults(criteria.isPageable() ? criteria.getPageSize() : SearchCriteria.MAX_PAGE_SIZE)
				.getResultList();
		if (rs == null) { rs = new ArrayList<D>(); }
		criteria.setResults(rs);
		return criteria;
	}

	/**
	 * <contains>
	 * @param t
	 * @return
	 */
	
	public boolean contains(D t) {
		return this.getSession().contains(t);
	}

	/**
	 * Saves the data entity
	 * @param d
	 */
	public void save(D d) throws AppException {
		try {
			this.getSession().save(d);
		} catch (Exception e) {
			logger.error("SQL error (" + e.getMessage() + ") occurs.", e);
			throw AppException.due("message.error.dao-exception", e);
		}
	}

	/**
	 * Saves/updates the data entity
	 * @param d
	 */
	
	public void saveOrUpdate(D d) throws AppException {
		try {
			this.getSession().saveOrUpdate(d);
		} catch (Exception e) {
			logger.error("SQL error (" + e.getMessage() + ") occurs.", e);
			throw AppException.due("message.error.dao-exception", e);
		}
	}

	/**
	 * Deletes the data entity
	 * @param d
	 */
	
	public void delete(D d) throws AppException {
		try {
			this.getSession().delete(d);
		} catch (Exception e) {
			logger.error("SQL error (" + e.getMessage() + ") occurs.", e);
			throw AppException.due("message.error.dao-exception", e);
		}
	}

	/**
	 * Deletes the entity by ID
	 * @param id the entity ID
	 * @return 
	 */
	
	public boolean deleteById(ID id) throws AppException {
		D t = find(id);
		if (t == null) {
			return false;
		}
		delete(t);
		return true;
	}

	/**
	 * 
	 * @param entities 
	 */
	
	public void deleteAll(Collection<D> entities) {
		for (Object entity : entities) {
			this.getSession().delete(entity);
		}
	}

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @return session
	 */
	public Session getSession() {
		// transaction on, and the CurrentSession
		return sessionFactory.getCurrentSession();
	}

}

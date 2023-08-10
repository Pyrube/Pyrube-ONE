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

package com.pyrube.one.app.inquiry;

import java.io.Serializable;
import java.util.List;

import com.pyrube.one.app.AppConfig;
import com.pyrube.one.app.persistence.Data;

/**
 * <code>SearchCriteria</code> is to query the paging data. it stores the base 
 * search criteria, such as, page no, page size, total pages, sort by, sort 
 * direction, etc.
 * 
 * @author Aranjuez
 * @since Pyrube-ONE 1.0
 */
public class SearchCriteria<D extends Data<?>> implements Serializable {

	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 2381101351675350368L;

	/**
	 * the constants for paging size
	 */
	public static final int DEFAULT_PAGE_SIZE = Integer.parseInt((String) AppConfig.getAppConfig().getAppProperty("PAGE_SIZE_DEFAULT"));
	public static final int MAX_PAGE_SIZE = 200000;
	
	/**
	 * sort ascending
	 */
	public static final String ASC = "asc";

	/**
	 * sort descending
	 */
	public static final String DESC = "desc";
	
	/**
	 * table alias
	 */
	protected String alias;

	/**
	 * indicates whether it is pageable
	 */
	protected boolean pageable = true;

	/**
	 * the first result index on current page. 
	 * it starts from 1.
	 */
	protected int firstResult = 1;

	/**
	 * the current page no, which starts from 1
	 */
	protected int pageNo = 1;

	/**
	 * the size for the current page.
	 */
	protected int pageSize = DEFAULT_PAGE_SIZE;
	
	/**
	 * indicates whether it is sortable
	 */
	protected boolean sortable = true;

	/**
	 * what sort by 
	 */
	protected String sortBy;
	
	/**
	 * what sort by alternatively
	 */
	protected String altSortBy;

	/**
	 * sort direction
	 */
	protected String sortDir = ASC;

	/**
	 * total number of pages
	 */
	protected int totalPages = 0;

	/**
	 * total row number of all records
	 */
	protected int totalRows = 0;

	/** 
	 * search criteria 
	 */
	private D criteria;

	/** 
	 * an array of result 
	 */
	private List<D> results;

	/**
	 * constructor
	 */
	public SearchCriteria() {
		this(null, ASC);
	}

	/**
	 * returns the <code>SearchCriteria</code> instance with the default sort-by
	 * @param defaultSortBy String. the default sort-by
	 */
	public SearchCriteria(String defaultSortBy) {
		this(defaultSortBy, ASC);
	}

	/**
	 * returns the <code>SearchCriteria</code> instance with the default sort-by 
	 * and the sort direction.
	 * @param defaultSortBy String. the default sort-by
	 * @param sortDir String. the sort direction, which could be asc or desc
	 */
	public SearchCriteria(String defaultSortBy, String sortDir) {
		super();
		this.sortBy = defaultSortBy;
		this.sortDir = sortDir;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return the pageable
	 */
	public boolean isPageable() {
		return pageable;
	}

	/**
	 * @param pageable the pageable to set
	 */
	public void setPageable(boolean pageable) {
		this.pageable = pageable;
	}

	/**
	 * @reutrn the first result index
	 */
	public int getFirstResult() {
		return(pageSize * (pageNo - 1));
	}

	/**
	 * @param firstResult
	 */
	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	/**
	 * @return the pageNo
	 */
	public int getPageNo() {
		return pageNo;
	}

	/**
	 * @param pageNo the pageNo to set
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * @return the sortable
	 */
	public boolean isSortable() {
		return sortable;
	}

	/**
	 * @param sortable the sortable to set
	 */
	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}

	/**
	 * @return the sortBy
	 */
	public String getSortBy() {
		return sortBy;
	}

	/**
	 * @param sortBy the sortBy to set
	 */
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	/**
	 * @return the sortDir
	 */
	public String getSortDir() {
		return sortDir;
	}

	/**
	 * @return the altSortBy
	 */
	public String getAltSortBy() {
		return altSortBy;
	}

	/**
	 * @param altSortBy the altSortBy to set
	 */
	public void setAltSortBy(String altSortBy) {
		this.altSortBy = altSortBy;
	}

	/**
	 * @param sortDir the sortDir to set
	 */
	public void setSortDir(String sortDir) {
		this.sortDir = sortDir;
	}

	/**
	 * @return
	 */
	public int getTotalPages() {
		if (pageSize > 0) {
			int mod = (totalRows % pageSize);
			totalPages = (totalRows / pageSize);
			if (mod > 0) {
				totalPages += 1;
			}
		}
		return totalPages;
	}

	/**
	 * @param totalPages
	 */
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	/**
	 * @return
	 */
	public int getTotalRows() {
		return totalRows;
	}

	/**
	 * @param totalRows
	 */
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	/**
	 * @return the criteria
	 */
	public D getCriteria() {
		return criteria;
	}

	/**
	 * @param criteria the criteria to set
	 */
	public void setCriteria(D criteria) {
		this.criteria = criteria;
	}

	/**
	 * @return the results
	 */
	public List<D> getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(List<D> results) {
		this.results = results;
	}
	
	@Override
	public String toString() {
		return(this.getClass().getSimpleName()+ "[" +
			"firstResult=" + firstResult +
			",pageSize=" + pageSize +
			",pageNo=" + pageNo +
			",totalPages=" + totalPages +
			",totalRows=" + totalRows +
			",sortBy=" + sortBy +
			",sortDir=" + sortDir + "]");
	}
}
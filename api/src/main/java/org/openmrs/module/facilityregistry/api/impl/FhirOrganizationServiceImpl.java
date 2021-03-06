/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.facilityregistry.api.impl;

import java.util.HashSet;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hl7.fhir.r4.model.Organization;
import org.openmrs.module.facilityregistry.api.FhirOrganizationService;
import org.openmrs.module.facilityregistry.api.dao.FhirOrganizationDao;
import org.openmrs.module.facilityregistry.api.translators.OrganizationTranslator;
import org.openmrs.module.facilityregistry.model.FhirOrganization;
import org.openmrs.module.fhir2.FhirConstants;
import org.openmrs.module.fhir2.api.impl.BaseFhirService;
import org.openmrs.module.fhir2.api.search.SearchQuery;
import org.openmrs.module.fhir2.api.search.SearchQueryInclude;
import org.openmrs.module.fhir2.api.search.param.SearchParameterMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PROTECTED)
public class FhirOrganizationServiceImpl extends BaseFhirService<Organization, FhirOrganization> implements FhirOrganizationService {
	
	@Autowired
	private FhirOrganizationDao dao;
	
	@Autowired
	private OrganizationTranslator translator;
	
	@Autowired
	private SearchQuery<FhirOrganization, Organization, FhirOrganizationDao, OrganizationTranslator, SearchQueryInclude<Organization>> searchQuery;
	
	@Autowired
	private SearchQueryInclude<Organization> searchQueryInclude;
	
	@Override
	@Transactional(readOnly = true)
	public IBundleProvider searchForOrganizations(StringAndListParam name, StringAndListParam city,
	        StringAndListParam country, StringAndListParam postalCode, StringAndListParam state, TokenAndListParam id,
	        DateRangeParam lastUpdated, HashSet<Include> includes, SortSpec sort) {
		
		SearchParameterMap theParams = new SearchParameterMap().addParameter(FhirConstants.NAME_SEARCH_HANDLER, name)
		        .addParameter(FhirConstants.CITY_SEARCH_HANDLER, city)
		        .addParameter(FhirConstants.STATE_SEARCH_HANDLER, state)
		        .addParameter(FhirConstants.COUNTRY_SEARCH_HANDLER, country)
		        .addParameter(FhirConstants.POSTALCODE_SEARCH_HANDLER, postalCode)
		        .addParameter(FhirConstants.COMMON_SEARCH_HANDLER, FhirConstants.ID_PROPERTY, id)
		        .addParameter(FhirConstants.COMMON_SEARCH_HANDLER, FhirConstants.LAST_UPDATED_PROPERTY, lastUpdated)
		        .addParameter(FhirConstants.INCLUDE_SEARCH_HANDLER, includes).setSortSpec(sort);
		
		return searchQuery.getQueryResults(theParams, dao, translator, searchQueryInclude);
	}
}

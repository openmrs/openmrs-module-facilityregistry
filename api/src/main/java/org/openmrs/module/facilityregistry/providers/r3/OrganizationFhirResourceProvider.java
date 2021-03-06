/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.facilityregistry.providers.r3;

import static lombok.AccessLevel.PACKAGE;

import javax.annotation.Nonnull;

import java.util.HashSet;
import java.util.List;

import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
import ca.uhn.fhir.rest.annotation.History;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Sort;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SortSpec;
import ca.uhn.fhir.rest.api.server.IBundleProvider;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.StringAndListParam;
import ca.uhn.fhir.rest.param.TokenAndListParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.hl7.fhir.convertors.conv30_40.Organization30_40;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Resource;
import org.openmrs.module.facilityregistry.api.FhirOrganizationService;
import org.openmrs.module.fhir2.api.annotations.R3Provider;
import org.openmrs.module.fhir2.api.search.SearchQueryBundleProviderR3Wrapper;
import org.openmrs.module.fhir2.providers.util.FhirProviderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("organizationFhirR3ResourceProvider")
@R3Provider
@Setter(PACKAGE)
public class OrganizationFhirResourceProvider implements IResourceProvider {
	
	@Autowired
	private FhirOrganizationService fhirOrganizationService;
	
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Organization.class;
	}
	
	@Read
	@SuppressWarnings("unused")
	public Organization getOrganizationById(@IdParam @Nonnull IdType id) {
		org.hl7.fhir.r4.model.Organization organization = fhirOrganizationService.get(id.getIdPart());
		if (organization == null) {
			throw new ResourceNotFoundException("Could not find Organization with Id " + id.getIdPart());
		}
		return Organization30_40.convertOrganization(organization);
	}
	
	@Create
	public MethodOutcome createOrganization(@ResourceParam Organization organization) {
		return FhirProviderUtils.buildCreate(Organization30_40
		        .convertOrganization(fhirOrganizationService.create(Organization30_40.convertOrganization(organization))));
	}
	
	@Update
	@SuppressWarnings("unused")
	public MethodOutcome updateOrganization(@IdParam IdType id, @ResourceParam Organization organization) {
		if (id == null || id.getIdPart() == null) {
			throw new InvalidRequestException("id must be specified to update");
		}
		return FhirProviderUtils.buildUpdate(Organization30_40.convertOrganization(
		    fhirOrganizationService.update(id.getIdPart(), Organization30_40.convertOrganization(organization))));
	}
	
	@Delete
	@SuppressWarnings("unused")
	public OperationOutcome deleteOrganization(@IdParam @Nonnull IdType id) {
		org.hl7.fhir.r4.model.Organization organization = fhirOrganizationService.delete(id.getIdPart());
		if (organization == null) {
			throw new ResourceNotFoundException("Could not find Organization to delete with id " + id.getIdPart());
		}
		return FhirProviderUtils.buildDelete(Organization30_40.convertOrganization(organization));
	}
	
	@History
	@SuppressWarnings("unused")
	public List<Resource> getOrganizationHistoryById(@IdParam @Nonnull IdType id) {
		org.hl7.fhir.r4.model.Organization organization = fhirOrganizationService.get(id.getIdPart());
		if (organization == null) {
			throw new ResourceNotFoundException("Could not find Organization with Id " + id.getIdPart());
		}
		return Organization30_40.convertOrganization(organization).getContained();
	}
	
	@Search
	public IBundleProvider searchOrganizations(@OptionalParam(name = Organization.SP_NAME) StringAndListParam name,
	        @OptionalParam(name = Organization.SP_ADDRESS_CITY) StringAndListParam city,
	        @OptionalParam(name = Organization.SP_ADDRESS_COUNTRY) StringAndListParam country,
	        @OptionalParam(name = Organization.SP_ADDRESS_POSTALCODE) StringAndListParam postalCode,
	        @OptionalParam(name = Organization.SP_ADDRESS_STATE) StringAndListParam state,
	        @OptionalParam(name = Organization.SP_RES_ID) TokenAndListParam id,
	        @OptionalParam(name = "_lastUpdated") DateRangeParam lastUpdated,
	        @IncludeParam(allow = { "Organization:" + Organization.SP_PARTOF }) HashSet<Include> includes,
	        @Sort SortSpec sort) {
		
		if (CollectionUtils.isEmpty(includes)) {
			includes = null;
		}
		
		return new SearchQueryBundleProviderR3Wrapper(fhirOrganizationService.searchForOrganizations(name, city, country,
		    postalCode, state, id, lastUpdated, includes, sort));
	}
}

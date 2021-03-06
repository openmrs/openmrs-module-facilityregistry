/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.facilityregistry.utils;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;

public class FhirUtilsTest {
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(5000);
	
	@Test
	public void getAccesTokenShouldGetBearerTokenFromFacilityRegistryServer() throws Exception {
		String authUrl = "http://localhost:5000/auth/token";
		String authUserName = "root@gofr.org";
		String authPassword = "gofr";
		
		wireMockRule.stubFor(post(urlPathMatching("/auth/token")).willReturn(aResponse().withStatus(200)
		        .withHeader("Content-Type", "application/json").withBody("{\"access_token\" :\"test-token\"}")));
		
		String token = FhirUtils.getAccesToken(authUrl, authUserName, authPassword);
		assertEquals("test-token", token);
		verify(postRequestedFor(urlEqualTo("/auth/token")));
	}
	
}

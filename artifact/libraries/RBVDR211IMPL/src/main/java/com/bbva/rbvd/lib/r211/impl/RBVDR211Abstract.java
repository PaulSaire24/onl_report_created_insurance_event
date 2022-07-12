package com.bbva.rbvd.lib.r211.impl;

import com.bbva.elara.configuration.manager.application.ApplicationConfigurationService;
import com.bbva.elara.library.AbstractLibrary;
import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.elara.utility.api.connector.APIConnectorBuilder;
import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.rbvd.lib.r211.RBVDR211;
import com.bbva.rbvd.lib.r211.impl.util.HttpClient;
import com.bbva.rbvd.lib.r211.impl.util.MapperHelper;

/**
 * This class automatically defines the libraries and utilities that it will use.
 */
public abstract class RBVDR211Abstract extends AbstractLibrary implements RBVDR211 {

	protected ApplicationConfigurationService applicationConfigurationService;

	protected APIConnector internalApiConnector;

	protected APIConnectorBuilder apiConnectorBuilder;

	protected PISDR012 pisdR012;

	protected MapperHelper mapperHelper;

	protected HttpClient httpClient;

	/**
	* @param applicationConfigurationService the this.applicationConfigurationService to set
	*/
	public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
		this.applicationConfigurationService = applicationConfigurationService;
	}

	/**
	* @param internalApiConnector the this.internalApiConnector to set
	*/
	public void setInternalApiConnector(APIConnector internalApiConnector) {
		this.internalApiConnector = internalApiConnector;
	}

	/**
	* @param apiConnectorBuilder the this.apiConnectorBuilder to set
	*/
	public void setApiConnectorBuilder(APIConnectorBuilder apiConnectorBuilder) {
		this.apiConnectorBuilder = apiConnectorBuilder;
	}

	/**
	* @param pisdR012 the this.pisdR012 to set
	*/
	public void setPisdR012(PISDR012 pisdR012) {
		this.pisdR012 = pisdR012;
	}

	public void setMapperHelper(MapperHelper mapperHelper) {
		this.mapperHelper = mapperHelper;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

}
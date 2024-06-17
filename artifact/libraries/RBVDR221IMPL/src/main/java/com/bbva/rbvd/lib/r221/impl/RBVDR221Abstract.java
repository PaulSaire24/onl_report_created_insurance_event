package com.bbva.rbvd.lib.r221.impl;

import com.bbva.elara.library.AbstractLibrary;
import com.bbva.elara.utility.api.connector.APIConnector;
import com.bbva.elara.utility.api.connector.APIConnectorBuilder;
import com.bbva.pdwy.lib.r008.PDWYR008;
import com.bbva.pisd.lib.r012.PISDR012;
import com.bbva.pisd.lib.r021.PISDR021;
import com.bbva.pisd.lib.r601.PISDR601;
import com.bbva.rbvd.lib.r221.RBVDR221;
import com.bbva.rbvd.lib.r221.impl.util.HttpClient;
import com.bbva.rbvd.lib.r221.impl.util.MapperHelper;

/**
 * This class automatically defines the libraries and utilities that it will use.
 */
public abstract class RBVDR221Abstract extends AbstractLibrary implements RBVDR221 {

	protected APIConnector internalApiConnector;

	protected APIConnectorBuilder apiConnectorBuilder;

	protected APIConnector externalApiConnector;

	protected PISDR012 pisdR012;

	protected PISDR021 pisdR021;

	protected PDWYR008 pdwyR008;

	protected PISDR601 pisdR601;

	protected MapperHelper mapperHelper;
	protected HttpClient httpClient;


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
	* @param externalApiConnector the this.externalApiConnector to set
	*/
	public void setExternalApiConnector(APIConnector externalApiConnector) {
		this.externalApiConnector = externalApiConnector;
	}

	/**
	* @param pisdR012 the this.pisdR012 to set
	*/
	public void setPisdR012(PISDR012 pisdR012) {
		this.pisdR012 = pisdR012;
	}

	/**
	* @param pisdR021 the this.pisdR021 to set
	*/
	public void setPisdR021(PISDR021 pisdR021) {
		this.pisdR021 = pisdR021;
	}

	/**
	* @param pdwyR008 the this.pdwyR008 to set
	*/
	public void setPdwyR008(PDWYR008 pdwyR008) {
		this.pdwyR008 = pdwyR008;
	}

	/**
	* @param pisdR601 the this.pisdR601 to set
	*/
	public void setPisdR601(PISDR601 pisdR601) {
		this.pisdR601 = pisdR601;
	}

	public void setMapperHelper(MapperHelper mapperHelper) {
		this.mapperHelper = mapperHelper;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
}
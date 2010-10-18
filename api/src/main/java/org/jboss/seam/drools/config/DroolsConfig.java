/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.drools.config;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Drools Integration Configuration.
 * 
 * @author Tihomir Surdilovic
 * 
 */
public class DroolsConfig {
	private List<RuleResource> ruleResources = new ArrayList<RuleResource>();
	private List<DroolsProperty> kbuilderProperties = new ArrayList<DroolsProperty>();
	private List<DroolsProperty> kbaseProperties = new ArrayList<DroolsProperty>();
	private List<DroolsProperty> ksessionProperties = new ArrayList<DroolsProperty>();
	private List<DroolsProperty> kagentProperties = new ArrayList<DroolsProperty>();
	private List<DroolsProperty> serializationSigningProperties = new ArrayList<DroolsProperty>();
	private List<DroolsProperty> systemProperties = new ArrayList<DroolsProperty>();
	
	private String kbuilderConfigFile;
	private String kbaseConfigFile;
	private String ksessionConfigFile;
	private String kagentConfigFile;
	private String serializationSigningConfigFile;
	private boolean startChangeNotifierService;
	private boolean startChangeScannerService;
	private boolean disableSeamDelegate;
	private int scannerInterval;
	private String agentName;
	private String loggerName;
	private String loggerType;
	private String loggerPath;
	private int loggerInterval;

	public DroolsConfig addAgentName(String agentName) {
		this.agentName = agentName;
		return this;
	}

	public DroolsConfig addLoggerInfo(String loggerName, String loggerType,
			String loggerPath, int loggerInterval) {
		this.loggerName = loggerName;
		this.loggerType = loggerType;
		this.loggerPath = loggerPath;
		this.loggerInterval = loggerInterval;
		return this;
	}

	public DroolsConfig addKBaseConfig(String kbaseConfigFile) {
		this.kbaseConfigFile = kbaseConfigFile;
		return this;
	}
	
	public DroolsConfig addKBuilderConfig(String kbuilderConfigFile) {
		this.kbuilderConfigFile = kbuilderConfigFile;
		return this;
	}

	public DroolsConfig addKSessionConfig(String ksessionConfigFile) {
		this.ksessionConfigFile = ksessionConfigFile;
		return this;
	}

	public DroolsConfig addKagentConfig(String kagentConfigFile) {
		this.kagentConfigFile = kagentConfigFile;
		return this;
	}

	public DroolsConfig addSerializationSigningConfig(
			String serializationSigningConfigFile) {
		this.serializationSigningConfigFile = serializationSigningConfigFile;
		return this;
	}

	public DroolsConfig startChangeNotifierService(
			boolean startChangeNotifierService) {
		this.startChangeNotifierService = startChangeNotifierService;
		return this;
	}

	public DroolsConfig disableSeamDelegate( boolean disableSeamDelegate) {
		this.disableSeamDelegate = disableSeamDelegate;
		return this;
	}
	
	public DroolsConfig startChangeScannerService(
			boolean startChangeScannerService, int scannerInterval) {
		this.startChangeScannerService = startChangeScannerService;
		this.scannerInterval = scannerInterval;
		return this;
	}

	public DroolsConfig addResource(RuleResource ruleResource) {
		ruleResources.add(ruleResource);
		return this;
	}

	public DroolsConfig addAllResources(RuleResource... ruleResources) {
		for (RuleResource rl : ruleResources) {
			this.ruleResources.add(rl);
		}
		return this;
	}

	public int resourceSize() {
		return ruleResources.size();
	}

	public Iterator<RuleResource> resourcesIterator() {
		return ruleResources.iterator();
	}

	public List<RuleResource> getRuleResources() {
		return ruleResources;
	}

	public String getKbuilderConfigFile() {
		return kbuilderConfigFile;
	}

	public String getKbaseConfigFile() {
		return kbaseConfigFile;
	}

	public String getKsessionConfigFile() {
		return ksessionConfigFile;
	}

	public String getKagentConfigFile() {
		return kagentConfigFile;
	}

	public String getSerializationSigningConfigFile() {
		return serializationSigningConfigFile;
	}

	public boolean isStartChangeNotifierService() {
		return startChangeNotifierService;
	}

	public boolean isStartChangeScannerService() {
		return startChangeScannerService;
	}

	public int getScannerInterval() {
		return scannerInterval;
	}

	public String getAgentName() {
		return agentName;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public String getLoggerType() {
		return loggerType;
	}

	public String getLoggerPath() {
		return loggerPath;
	}

	public int getLoggerInterval() {
		return loggerInterval;
	}

	public boolean isDisableSeamDelegate() {
		return disableSeamDelegate;
	}
	
	public DroolsConfig addKBuilderProperty(DroolsProperty property) {
		this.kbuilderProperties.add(property);
		return this;
	}

	public DroolsConfig addAllKBuilderProperties(DroolsProperty... properties) {
		for (DroolsProperty dp : properties) {
			this.kbuilderProperties.add(dp);
		}
		return this;
	}

	public int kBuilderPropertiesSize() {
		return kbuilderProperties.size();
	}

	public Iterator<DroolsProperty> kbuilderPropertiesIterator() {
		return kbuilderProperties.iterator();
	}
	
	public DroolsConfig addKBaseProperty(DroolsProperty property) {
		this.kbaseProperties.add(property);
		return this;
	}

	public DroolsConfig addAllKBaseProperties(DroolsProperty... properties) {
		for (DroolsProperty dp : properties) {
			this.kbaseProperties.add(dp);
		}
		return this;
	}

	public int kbasePropertiesSize() {
		return kbaseProperties.size();
	}

	public Iterator<DroolsProperty> kbasePropertiesIterator() {
		return kbaseProperties.iterator();
	}
	
	public DroolsConfig addKSessionProperty(DroolsProperty property) {
		this.ksessionProperties.add(property);
		return this;
	}

	public DroolsConfig addAllKSessionProperties(DroolsProperty... properties) {
		for (DroolsProperty dp : properties) {
			this.ksessionProperties.add(dp);
		}
		return this;
	}

	public int ksessionPropertiesSize() {
		return ksessionProperties.size();
	}

	public Iterator<DroolsProperty> ksessionPropertiesIterator() {
		return ksessionProperties.iterator();
	}
	
	public DroolsConfig addKAgentProperty(DroolsProperty property) {
		this.kagentProperties.add(property);
		return this;
	}

	public DroolsConfig addAllKAgentProperties(DroolsProperty... properties) {
		for (DroolsProperty dp : properties) {
			this.kagentProperties.add(dp);
		}
		return this;
	}

	public int kagentPropertiesSize() {
		return kagentProperties.size();
	}

	public Iterator<DroolsProperty> kagentPropertiesIterator() {
		return kagentProperties.iterator();
	}
	
	public DroolsConfig addSerializationSigningProperty(DroolsProperty property) {
		this.serializationSigningProperties.add(property);
		return this;
	}

	public DroolsConfig addAllSerializationSigningProperties(DroolsProperty... properties) {
		for (DroolsProperty dp : properties) {
			this.serializationSigningProperties.add(dp);
		}
		return this;
	}

	public int serializationSigningPropertiesSize() {
		return serializationSigningProperties.size();
	}

	public Iterator<DroolsProperty> serializationSigningPropertiesIterator() {
		return serializationSigningProperties.iterator();
	}
	
	public DroolsConfig addSystemProperty(DroolsProperty property) {
		this.systemProperties.add(property);
		return this;
	}

	public DroolsConfig addAllSystemProperties(DroolsProperty... properties) {
		for (DroolsProperty dp : properties) {
			this.systemProperties.add(dp);
		}
		return this;
	}

	public int systemPropertiesSize() {
		return systemProperties.size();
	}

	public Iterator<DroolsProperty> systemPropertiesIterator() {
		return systemProperties.iterator();
	}
	
}

package org.jboss.seam.drools.configutil;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.jboss.seam.drools.config.Drools;
import org.jboss.seam.drools.config.DroolsConfig;
import org.jboss.seam.drools.config.DroolsProperty;
import org.jboss.seam.drools.utils.ConfigUtils;
import org.jboss.weld.extensions.bean.generic.GenericConfiguration;
import org.jboss.weld.extensions.resourceLoader.ResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@GenericConfiguration(Drools.class)
public class DroolsConfigUtil implements Serializable {
	private static final Logger log = LoggerFactory
			.getLogger(DroolsConfigUtil.class);

	@Inject
	ResourceProvider resourceProvider;

	@Inject
	DroolsConfig config;

	private final Map<String, String> kbuilderPropertiesMap = new HashMap<String, String>();
	private final Map<String, String> kbasePropertiesMap = new HashMap<String, String>();
	private final Map<String, String> ksessionPropertiesMap = new HashMap<String, String>();
	private final Map<String, String> kagentPropertiestMap = new HashMap<String, String>();
	private final Map<String, String> serializationSigningPropertiesMap = new HashMap<String, String>();

	@PostConstruct
	public void setup() {
		readProperties(kbuilderPropertiesMap,
				config.kbuilderPropertiesIterator(),
				config.getKbuilderConfigFile());
		readProperties(kbasePropertiesMap, config.kbasePropertiesIterator(),
				config.getKbaseConfigFile());
		readProperties(ksessionPropertiesMap,
				config.ksessionPropertiesIterator(),
				config.getKsessionConfigFile());
		readProperties(kagentPropertiestMap, config.kagentPropertiesIterator(),
				config.getKagentConfigFile());
		readProperties(serializationSigningPropertiesMap,
				config.serializationSigningPropertiesIterator(),
				config.getSerializationSigningConfigFile());
		setSystemProperties(config.systemPropertiesIterator());
		setSystemProperties(serializationSigningPropertiesMap);
	}

	public ResourceChangeScannerConfiguration getResourceChangeScannerConfiguration() {
		ResourceChangeScannerConfiguration sconf = ResourceFactory
				.getResourceChangeScannerService()
				.newResourceChangeScannerConfiguration();
		if (config.getScannerInterval() >= 0) {
			sconf.setProperty("drools.resource.scanner.interval",
					String.valueOf(config.getScannerInterval()));
		}
		return sconf;
	}

	public KnowledgeAgentConfiguration getKnowledgeAgentConfiguration() {
		KnowledgeAgentConfiguration config = KnowledgeAgentFactory
				.newKnowledgeAgentConfiguration();
		Iterator<Entry<String, String>> it = kagentPropertiestMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> nextExtry = it.next();
			config.setProperty(nextExtry.getKey(), nextExtry.getValue());
		}
		return config;
	}

	public KnowledgeSessionConfiguration getKnowledgeSessionConfiguration() {
		KnowledgeSessionConfiguration config = KnowledgeBaseFactory
				.newKnowledgeSessionConfiguration();
		Iterator<Entry<String, String>> it = ksessionPropertiesMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> nextExtry = it.next();
			config.setProperty(nextExtry.getKey(), nextExtry.getValue());
		}
		return config;
	}

	public KnowledgeBaseConfiguration getKnowledgeBaseConfiguration() {
		KnowledgeBaseConfiguration config = KnowledgeBaseFactory
				.newKnowledgeBaseConfiguration();
		Iterator<Entry<String, String>> it = kbasePropertiesMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> nextExtry = it.next();
			config.setProperty(nextExtry.getKey(), nextExtry.getValue());
		}
		return config;
	}

	public KnowledgeBuilderConfiguration getKnowledgeBuilderConfiguration() {
		KnowledgeBuilderConfiguration config = KnowledgeBuilderFactory
				.newKnowledgeBuilderConfiguration();
		Iterator<Entry<String, String>> it = kbuilderPropertiesMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> nextExtry = it.next();
			config.setProperty(nextExtry.getKey(), nextExtry.getValue());
		}
		return config;
	}

	private void setSystemProperties(Iterator<DroolsProperty> iter) {
		while (iter.hasNext()) {
			DroolsProperty prop = iter.next();
			System.setProperty(prop.getName(), prop.getValue());
		}
	} 
	
	private void setSystemProperties(Map<String, String> serializationSigningMap) {
		for(String key : serializationSigningMap.keySet()) {
			System.setProperty(key, serializationSigningMap.get(key));
		}
	}

	private void readProperties(Map<String, String> propertiesMap,
			Iterator<DroolsProperty> iter, String propertiesPath) {
		while (iter.hasNext()) {
			DroolsProperty prop = iter.next();
			propertiesMap.put(prop.getName(), prop.getValue());
		}

		if (propertiesPath != null && !propertiesPath.equals("")) {
			try {
				Properties kbuilderProp = ConfigUtils.loadProperties(
						resourceProvider, propertiesPath);
				for (Object key : kbuilderProp.keySet()) {
					propertiesMap.put((String) key,
							(String) kbuilderProp.get(key));
				}
			} catch (IOException e) {
				log.error("Unable to read configuration properties file: "
						+ propertiesPath);
			}
		} else {
			log.debug("NULL properties path specified, bypassing reading properties");
		}
	}

}

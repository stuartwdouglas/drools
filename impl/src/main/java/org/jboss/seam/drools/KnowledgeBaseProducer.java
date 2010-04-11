package org.jboss.seam.drools;

import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.event.knowledgebase.KnowledgeBaseEventListener;
import org.drools.io.ResourceFactory;
import org.jboss.seam.drools.config.KnowledgeBaseConfig;
import org.jboss.seam.drools.events.KnowledgeBuilderErrorsEvent;
import org.jboss.seam.drools.events.RuleResourceAddedEvent;
import org.jboss.seam.drools.qualifiers.kbase.KBaseConfigured;
import org.jboss.seam.drools.utils.ConfigUtils;
import org.jboss.weld.extensions.resources.ResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Tihomir Surdilovic
 */
public class KnowledgeBaseProducer
{
   private static final Logger log = LoggerFactory.getLogger(KnowledgeBaseProducer.class);

   @Inject
   BeanManager manager;
   @Inject
   ResourceProvider resourceProvider;

   @Produces @KBaseConfigured
   public KnowledgeBase produceKBase(KnowledgeBaseConfig kbaseConfig) throws Exception
   {
      KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(getKnowledgeBuilderConfiguration(kbaseConfig.getKnowledgeBuilderConfigPath()));

      for (String nextResource : kbaseConfig.getRuleResources())
      {
         addResource(kbuilder, nextResource);
      }

      KnowledgeBuilderErrors kbuildererrors = kbuilder.getErrors();
      if (kbuildererrors.size() > 0)
      {
         for (KnowledgeBuilderError kbuildererror : kbuildererrors)
         {
            log.error(kbuildererror.getMessage());
         }
         manager.fireEvent(new KnowledgeBuilderErrorsEvent(kbuildererrors));
      }

      KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(getKnowledgeBaseConfiguration(kbaseConfig.getKnowledgeBaseConfigPath()));
      kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

      if (kbaseConfig.getEventListeners() != null)
      {
         for (String eventListener : kbaseConfig.getEventListeners())
         {
            addEventListener(kbase, eventListener);
         }
      }
      return kbase;
   }

   private KnowledgeBuilderConfiguration getKnowledgeBuilderConfiguration(String knowledgeBuilderConfigPath) throws Exception
   {
      KnowledgeBuilderConfiguration droolsKbuilderConfig = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
      if (knowledgeBuilderConfigPath != null && knowledgeBuilderConfigPath.endsWith(".properties"))
      {
         
         Properties kbuilderProp = ConfigUtils.loadProperties(resourceProvider, knowledgeBuilderConfigPath);
         droolsKbuilderConfig = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(kbuilderProp, null);
         log.debug("KnowledgeBuilderConfiguration loaded: " + knowledgeBuilderConfigPath);
      }
      else
      {
         log.warn("Invalid config type: " + knowledgeBuilderConfigPath);
      }
      return droolsKbuilderConfig;
   }

   public KnowledgeBaseConfiguration getKnowledgeBaseConfiguration(String knowledgeBaseConfigPath) throws Exception
   {
      KnowledgeBaseConfiguration droolsKbaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
      if (knowledgeBaseConfigPath != null && knowledgeBaseConfigPath.endsWith(".properties"))
      {
         Properties kbaseProp = ConfigUtils.loadProperties(resourceProvider, knowledgeBaseConfigPath);
         droolsKbaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(kbaseProp, null);
         log.debug("KnowledgeBaseConfiguration loaded: " + knowledgeBaseConfigPath);
      }
      return droolsKbaseConfig;
   }

   private void addResource(KnowledgeBuilder kbuilder, String resource) throws Exception
   {
      // TODO add support for drools templates definition!
      if (ConfigUtils.isValidResource(resource))
      {
         ResourceType resourceType = ResourceType.getResourceType(ConfigUtils.getResourceType(resource));
         if (ConfigUtils.getResourcePath(resource).equals(ConfigUtils.RESOURCE_TYPE_URL))
         {
            kbuilder.add(ResourceFactory.newUrlResource(ConfigUtils.getRuleResource(resource)), resourceType);
            manager.fireEvent(new RuleResourceAddedEvent(ConfigUtils.getRuleResource(resource)));
         }
         else if (ConfigUtils.getResourcePath(resource).equals(ConfigUtils.RESOURCE_TYPE_FILE))
         {
            kbuilder.add(ResourceFactory.newFileResource(ConfigUtils.getRuleResource(resource)), resourceType);
            manager.fireEvent(new RuleResourceAddedEvent(ConfigUtils.getRuleResource(resource)));
         }
         else if (ConfigUtils.getResourcePath(resource).equals(ConfigUtils.RESOURCE_TYPE_CLASSPATH))
         {
            kbuilder.add(ResourceFactory.newClassPathResource(ConfigUtils.getRuleResource(resource)), resourceType);
            manager.fireEvent(new RuleResourceAddedEvent(ConfigUtils.getRuleResource(resource)));
         }
         else
         {
            log.error("Invalid resource: " + ConfigUtils.getResourcePath(resource));
         }
      }
      else
      {
         log.error("Invalid resource definition: " + resource);
      }
   }

   private void addEventListener(org.drools.KnowledgeBase kbase, String eventListener)
   {
      try
      {
         @SuppressWarnings("unchecked")
         Class eventListenerClass = Class.forName(eventListener);
         Object eventListenerObject = eventListenerClass.newInstance();

         if (eventListenerObject instanceof KnowledgeBaseEventListener)
         {
            kbase.addEventListener((KnowledgeBaseEventListener) eventListenerObject);
         }
         else
         {
            log.debug("Event Listener " + eventListener + " is not of type KnowledgeBaseEventListener");
         }
      }
      catch (Exception e)
      {
         log.error("Error adding event listener " + e.getMessage());
      }
   }
}

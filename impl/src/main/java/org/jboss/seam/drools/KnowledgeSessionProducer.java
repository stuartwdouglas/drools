/*
 * JBoss, Home of Professional Open Source
 * Copyright ${year}, Red Hat, Inc., and individual contributors
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
package org.jboss.seam.drools;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.drools.KnowledgeBase;
import org.drools.event.KnowledgeRuntimeEventManager;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.jboss.seam.drools.bootstrap.DroolsExtension;
import org.jboss.seam.drools.config.Drools;
import org.jboss.seam.drools.configutil.DroolsConfigUtil;
import org.jboss.seam.drools.qualifiers.Persisted;
import org.jboss.seam.drools.qualifiers.Scanned;
import org.jboss.weld.extensions.bean.generic.Generic;
import org.jboss.weld.extensions.bean.generic.GenericConfiguration;
import org.jboss.weld.extensions.core.Veto;
import org.jboss.weld.extensions.resourceLoader.ResourceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Tihomir Surdilovic
 */
@Veto
@Dependent
@GenericConfiguration(Drools.class)
public class KnowledgeSessionProducer implements Serializable
{
   private static final Logger log = LoggerFactory.getLogger(KnowledgeSessionProducer.class);

   @Inject
   BeanManager manager;
     
   @Inject
   DroolsExtension droolsExtension;
   
   @Inject
   ResourceProvider resourceProvider;
   
   @Inject 
   SeamDelegate delegate;
   
   @Inject
   @Generic
   Drools drools;
   
   @Inject
   @Generic
   DroolsConfigUtil configUtils;
      
   @Inject
   @Default
   @Generic
   KnowledgeBase kbase;
      
   @Inject
   @Scanned
   @Generic
   KnowledgeBase scannedKbase;
   
   @Produces
   @Default
   @SessionScoped
   public StatefulKnowledgeSession produceStatefulSession() throws Exception
   {
      StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(configUtils.getKnowledgeSessionConfiguration(), null);
      if (!drools.disableSeamDelegate())
      {
         addSeamDelegate(ksession);
      }
      addEventListeners(ksession);
      addWorkItemHandlers(ksession);
      addFactProviders(ksession);
      addChannels(ksession);
      
      return ksession;
   }
   
   @Produces 
   @Persisted
   @SessionScoped
   public StatefulKnowledgeSession producePersistedStatefulKnowledgeSession() throws Exception
   {
      // TODO finish this
      return null;
   }


   @Produces
   @Scanned
   @SessionScoped
   public StatefulKnowledgeSession produceScannedStatefulSession() throws Exception
   {
      StatefulKnowledgeSession ksession = scannedKbase.newStatefulKnowledgeSession(configUtils.getKnowledgeSessionConfiguration(), null);
      if (!drools.disableSeamDelegate())
      {
         addSeamDelegate(ksession);
      }
      addEventListeners(ksession);
      addWorkItemHandlers(ksession);
      addFactProviders(ksession);
      addChannels(ksession);

      return ksession;
   }

   @Produces
   @Scanned
   @RequestScoped
   public StatelessKnowledgeSession produceScannedStatelessSession() throws Exception
   {
      StatelessKnowledgeSession ksession = scannedKbase.newStatelessKnowledgeSession(configUtils.getKnowledgeSessionConfiguration());
      if (!drools.disableSeamDelegate())
      {
         addSeamDelegate(ksession);
      }
      addEventListeners(ksession);
      
      return ksession;
   }

   @Produces
   @Default
   @RequestScoped
   public StatelessKnowledgeSession produceStatelessSession() throws Exception
   {
      StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession(configUtils.getKnowledgeSessionConfiguration());
      if (!drools.disableSeamDelegate())
      {
         addSeamDelegate(ksession);
      }
      addEventListeners(ksession);
      
      return ksession;
   }
   
   

   public void disposeStatefulSession( /** @Disposes @Default **/ StatefulKnowledgeSession session)
   {
      session.dispose();
   }
   
   public void disposeScannedStatefulSession( /** @Disposes @Scanned **/ StatefulKnowledgeSession session)
   {
      session.dispose();
   }
   
   private void addSeamDelegate(StatefulKnowledgeSession  ksession) {
      ksession.getGlobals().setDelegate(delegate);
   }
   
   private void addSeamDelegate(StatelessKnowledgeSession ksession) {
      ksession.getGlobals().setDelegate(delegate);
   }
   
   private void addEventListeners(KnowledgeRuntimeEventManager ksession)
   {
      Iterator<Object> iter = droolsExtension.getKsessionEventListenerSet().iterator();
      while (iter.hasNext())
      {
         Object eventListenerInstance = iter.next();

         if (eventListenerInstance instanceof WorkingMemoryEventListener)
         {
            ksession.addEventListener((WorkingMemoryEventListener) eventListenerInstance);
         }
         else if (eventListenerInstance instanceof AgendaEventListener)
         {
            ksession.addEventListener((AgendaEventListener) eventListenerInstance);
         }
         else if (eventListenerInstance instanceof ProcessEventListener)
         {
            ksession.addEventListener((ProcessEventListener) eventListenerInstance);
         }
         else
         {
            log.debug("Invalid Event Listener: " + eventListenerInstance);
         }
      }
   }

   private void addWorkItemHandlers(StatefulKnowledgeSession ksession)
   {
      Iterator<String> iter = droolsExtension.getWorkItemHandlers().keySet().iterator();
      while (iter.hasNext())
      {
         String name = iter.next();
         ksession.getWorkItemManager().registerWorkItemHandler(name, droolsExtension.getWorkItemHandlers().get(name));
      }
   }
   
   private void addChannels(StatefulKnowledgeSession ksession) {
      Iterator<String> iter = droolsExtension.getChannels().keySet().iterator();
      while(iter.hasNext())
      {
         String channelName = iter.next();
         ksession.registerChannel(channelName, droolsExtension.getChannels().get(channelName));
      }
   }
   
   private void addFactProviders(StatefulKnowledgeSession ksession) {
      Iterator<FactProvider> iter = droolsExtension.getFactProviderSet().iterator();
      while(iter.hasNext())
      {
         FactProvider factProvider = iter.next();
         if(factProvider.getGlobals() != null) {
            Iterator<Entry<String, Object>> globalIterator = factProvider.getGlobals().entrySet().iterator();
            while(globalIterator.hasNext()) {
               Entry<String, Object> nextEntry = globalIterator.next();
               ksession.setGlobal(nextEntry.getKey(), nextEntry.getValue());
            }
         }
         
         if(factProvider.getFacts() != null) {
            for(Object nextFact : factProvider.getFacts()) {
               ksession.insert(nextFact);
            }
         }
      }
   }
}

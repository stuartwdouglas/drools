package org.jboss.seam.drools.test.kbase;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.jboss.seam.drools.config.Drools;
import org.jboss.seam.drools.config.DroolsConfig;
import org.jboss.seam.drools.config.DroolsProperty;
import org.jboss.seam.drools.config.RuleResource;

public class KBaseTestRuleResources
{
   @Drools("creditRules")
   @Produces
   @ApplicationScoped
   @Credit
   public DroolsConfig produceCreditRules()
   {
	  return new DroolsConfig().addResource(new RuleResource("classpath:kbasetest.drl", "DRL", "forkbasetest"))
	                           .addKBuilderConfig("kbuilderconfig.properties")
	                           .addKBaseConfig("kbaseconfig.properties");
   }
   
   @Drools("debitRules")
   @Produces
   @ApplicationScoped
   @Debit
   public DroolsConfig configureDebitRules()
   {
	   return new DroolsConfig().addResource(new RuleResource("classpath:kbasetest.xls", "DTABLE", "XLS", "Tables_2"))
	                            .addKBuilderProperty(new DroolsProperty("drools.dialect.default", "java"));
   }
}

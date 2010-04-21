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
package org.jboss.seam.drools.test.interceptors;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNotSame;

import javax.inject.Inject;
import javax.validation.constraints.AssertTrue;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.drools.KnowledgeBaseProducer;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.weld.extensions.resources.ResourceProvider;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class InterceptorsTest
{
   @Deployment
   public static JavaArchive createTestArchive()
   {
      String pkgPath = InterceptorsTest.class.getPackage().getName().replaceAll("\\.", "/");
      JavaArchive archive = Archives.create("test.jar", JavaArchive.class)
      .addPackages(true, new InterceptorsTestFilter(), KnowledgeBaseProducer.class.getPackage())
      .addPackages(true, ResourceProvider.class.getPackage())
      .addClass(Person.class)
      .addClass(InterceptorsTestBean.class)
      .addResource(pkgPath + "/interceptorstest.drl", ArchivePaths.create("interceptorstest.drl"))
      .addManifestResource(pkgPath + "/InterceptorsTest-beans.xml", ArchivePaths.create("beans.xml"));
      //System.out.println(archive.toString(Formatters.VERBOSE));
      return archive;
   }
   
   @Inject InterceptorsTestBean ibean;
   
   @Test
   public void testInterceptors() {
      assertNotNull(ibean);
      
      ibean.getPerson();
      assertTrue(true);
   }
}

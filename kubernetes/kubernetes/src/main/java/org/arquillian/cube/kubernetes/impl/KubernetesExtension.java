/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package org.arquillian.cube.kubernetes.impl;


import io.fabric8.kubernetes.api.builder.Visitor;
import org.arquillian.cube.impl.util.Strings;
import org.arquillian.cube.kubernetes.api.AnnotationProvider;
import org.arquillian.cube.kubernetes.api.DependencyResolver;
import org.arquillian.cube.kubernetes.api.LabelProvider;
import org.arquillian.cube.kubernetes.api.NamespaceService;
import org.arquillian.cube.kubernetes.impl.annotation.AnnotationProviderRegistar;
import org.arquillian.cube.kubernetes.impl.annotation.DefaultAnnotationProvider;
import org.arquillian.cube.kubernetes.impl.enricher.ClientResourceProvider;
import org.arquillian.cube.kubernetes.impl.enricher.DeploymentListResourceProvider;
import org.arquillian.cube.kubernetes.impl.enricher.DeploymentResourceProvider;
import org.arquillian.cube.kubernetes.impl.enricher.PodListResourceProvider;
import org.arquillian.cube.kubernetes.impl.enricher.PodResourceProvider;
import org.arquillian.cube.kubernetes.impl.enricher.ReplicaSetListResourceProvider;
import org.arquillian.cube.kubernetes.impl.enricher.ReplicaSetResourceProvider;
import org.arquillian.cube.kubernetes.impl.enricher.ReplicationControllerListResourceProvider;
import org.arquillian.cube.kubernetes.impl.enricher.ReplicationControllerResourceProvider;
import org.arquillian.cube.kubernetes.impl.enricher.ServiceListResourceProvider;
import org.arquillian.cube.kubernetes.impl.enricher.ServiceResourceProvider;
import org.arquillian.cube.kubernetes.impl.enricher.SessionResourceProvider;
import org.arquillian.cube.kubernetes.impl.enricher.UrlResourceProvider;
import org.arquillian.cube.kubernetes.impl.label.DefaultLabelProvider;
import org.arquillian.cube.kubernetes.impl.label.LabelProviderRegistar;
import org.arquillian.cube.kubernetes.impl.locator.KubernetesResourceLocatorRegistar;
import org.arquillian.cube.kubernetes.impl.log.LoggerRegistar;
import org.arquillian.cube.kubernetes.impl.namespace.DefaultNamespaceService;
import org.arquillian.cube.kubernetes.impl.namespace.NamespaceServiceRegistar;
import org.arquillian.cube.kubernetes.impl.resolve.DependencyResolverRegistar;
import org.arquillian.cube.kubernetes.impl.resolve.ShrinkwrapResolver;
import org.arquillian.cube.kubernetes.impl.visitor.DockerRegistryVisitor;
import org.arquillian.cube.kubernetes.impl.visitor.LoggingVisitor;
import org.arquillian.cube.kubernetes.impl.visitor.ServiceAccountVisitor;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

/**
 * An Arquillian extension for Kubernetes.
 */
public class KubernetesExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.observer(ConfigurationRegistar.class)
                .observer(NamespaceServiceRegistar.class)
                .observer(KubernetesResourceLocatorRegistar.class)
                .observer(LabelProviderRegistar.class)
                .observer(DependencyResolverRegistar.class)
                .observer(AnnotationProviderRegistar.class)
                .observer(LoggerRegistar.class)
                .observer(getClientCreator())
                .observer(SuiteListener.class)
                .observer(TestListener.class)
                .observer(SessionManagerLifecycle.class);

        builder.service(NamespaceService.class, DefaultNamespaceService.class)
                .service(LabelProvider.class, DefaultLabelProvider.class)
                .service(DependencyResolver.class, ShrinkwrapResolver.class)
                .service(AnnotationProvider.class, DefaultAnnotationProvider.class)
                .service(Visitor.class, LoggingVisitor.class)
                .service(Visitor.class, DockerRegistryVisitor.class)
                .service(Visitor.class, ServiceAccountVisitor.class)

                .service(ResourceProvider.class, ClientResourceProvider.class)
                .service(ResourceProvider.class, PodListResourceProvider.class)
                .service(ResourceProvider.class, PodResourceProvider.class)
                .service(ResourceProvider.class, DeploymentResourceProvider.class)
                .service(ResourceProvider.class, DeploymentListResourceProvider.class)
                .service(ResourceProvider.class, ReplicaSetResourceProvider.class)
                .service(ResourceProvider.class, ReplicaSetListResourceProvider.class)
                .service(ResourceProvider.class, ReplicationControllerListResourceProvider.class)
                .service(ResourceProvider.class, ReplicationControllerResourceProvider.class)
                .service(ResourceProvider.class, ServiceListResourceProvider.class)
                .service(ResourceProvider.class, ServiceResourceProvider.class)
                .service(ResourceProvider.class, SessionResourceProvider.class)
                .service(ResourceProvider.class, UrlResourceProvider.class);
    }

    private Class getClientCreator() {
        Class creatorClass = null;
        String creatorClassName = System.getProperty(Constants.CLIENT_CREATOR_CLASS_NAME);
        try {
            if (Strings.isNotNullOrEmpty(creatorClassName))
                creatorClass = KubernetesExtension.class.getClassLoader().loadClass(creatorClassName);
        } catch (Throwable t) {
            //fallback to default
        }
        return creatorClass != null ? creatorClass : ClientCreator.class;
    }
}

/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.handler.link.impl;

import org.apache.sling.api.adapter.Adaptable;
import org.apache.sling.api.adapter.AdapterFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import io.wcm.handler.link.spi.LinkHandlerConfig;
import io.wcm.sling.commons.caservice.ContextAwareServiceResolver;

/**
 * Adapts resources or requests to {@link LinkHandlerConfig} via {@link ContextAwareServiceResolver}.
 */
@Component(service = AdapterFactory.class,
    property = {
        AdapterFactory.ADAPTABLE_CLASSES + "=org.apache.sling.api.resource.Resource",
        AdapterFactory.ADAPTABLE_CLASSES + "=org.apache.sling.api.SlingHttpServletRequest",
        AdapterFactory.ADAPTER_CLASSES + "=io.wcm.handler.link.spi.LinkHandlerConfig"
    })
public class LinkHandlerConfigAdapterFactory implements AdapterFactory {

  @Reference
  private ContextAwareServiceResolver serviceResolver;

  @SuppressWarnings("unchecked")
  @Override
  public <AdapterType> AdapterType getAdapter(Object adaptable, Class<AdapterType> type) {
    if (type == LinkHandlerConfig.class) {
      return (AdapterType)serviceResolver.resolve(LinkHandlerConfig.class, (Adaptable)adaptable);
    }
    return null;
  }

}

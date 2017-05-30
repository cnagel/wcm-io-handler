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
package io.wcm.handler.url.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.caconfig.resource.ConfigurationResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import io.wcm.handler.url.SiteRootDetector;
import io.wcm.sling.commons.resource.ResourcePath;

/**
 * Implements {@link SiteRootDetector}.
 */
@Component(service = SiteRootDetector.class)
public class SiteRootDetectorImpl implements SiteRootDetector {

  @Reference
  private ConfigurationResourceResolver configurationResourceResolver;

  private static final Logger log = LoggerFactory.getLogger(SiteRootDetectorImpl.class);

  // cache resolving of site root level per resource path
  private final Cache<String, Integer> cache = CacheBuilder.newBuilder()
      .expireAfterWrite(1, TimeUnit.MINUTES)
      .maximumSize(10000)
      .build();

  @Override
  public int getSiteRootLevel(Resource contextResource) {
    if (contextResource == null) {
      return 0;
    }
    try {
      return cache.get(contextResource.getPath(), () -> detectSiteRootLevel(contextResource));
    }
    catch (ExecutionException ex) {
      log.warn("Unexpected exception.", ex);
      return 0;
    }
  }

  private int detectSiteRootLevel(Resource contextResource) {
    // assumption: inner-most context-aware configuration context path is site root path
    String siteRootpath = configurationResourceResolver.getContextPath(contextResource);
    if (siteRootpath != null) {
      int level = ResourcePath.getAbsoluteLevel(siteRootpath);
      if (log.isDebugEnabled()) {
        log.debug("Detect site root level for {}: {}", contextResource.getPath(), level);
      }
      return level;
    }
    return 0;
  }

}
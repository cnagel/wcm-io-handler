/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2014 wcm.io
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
package io.wcm.handler.url.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.sling.api.resource.Resource;
import org.junit.Test;

import io.wcm.handler.url.UrlModes;

public class UrlHandlerConfigTest {

  private UrlHandlerConfig underTest = new UrlHandlerConfig() {
    @Override
    public int getSiteRootLevel(Resource contextResource) {
      return 0;
    }
  };

  @Test
  public void testDefaultValues() {
    assertEquals(0, underTest.getSiteRootLevel(null));
    assertFalse(underTest.isSecure(null));
    assertFalse(underTest.isIntegrator(null));
    assertEquals(UrlModes.DEFAULT, underTest.getDefaultUrlMode());
    assertTrue(underTest.getIntegratorModes().isEmpty());
  }

}

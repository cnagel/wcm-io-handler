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
package io.wcm.handler.url.rewriter.impl;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import io.wcm.sling.commons.resource.ImmutableValueMap;

public class UrlExternalizerTransformerConfigTest {

  private UrlExternalizerTransformerConfig underTest;

  @Before
  public void setUp() {
    underTest = new UrlExternalizerTransformerConfig(ImmutableValueMap.builder()
        .put(UrlExternalizerTransformerConfig.PN_REWRITE_ELEMENTS, new String[] {
            "element1:attr1",
            "element2:attr2",
            "",
            null,
            "element3",
            "element1:attr4"
        }).build());
  }

  @Test
  public void testGetElementAttributeNames() {
    Map<String, String> expected = ImmutableMap.<String, String>builder()
        .put("element1", "attr1")
        .put("element2", "attr2")
        .build();
    assertEquals(expected, ImmutableMap.copyOf(underTest.getElementAttributeNames()));
  }

}

/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2015 wcm.io
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
package io.wcm.handler.link.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.servlet.http.HttpServletResponse;

import org.junit.Rule;
import org.junit.Test;

import com.day.cq.wcm.api.WCMMode;

import io.wcm.handler.link.LinkNameConstants;
import io.wcm.handler.link.testcontext.AppAemContext;
import io.wcm.handler.link.testcontext.DummyAppTemplate;
import io.wcm.handler.link.type.ExternalLinkType;
import io.wcm.sling.commons.resource.ImmutableValueMap;
import io.wcm.testing.mock.aem.junit.AemContext;

public class RedirectTest {

  @Rule
  public final AemContext context = AppAemContext.newAemContext();

  @Test
  public void testRedirectDefault() {
    context.currentPage(context.create().page("/content/redirect", DummyAppTemplate.REDIRECT.getTemplatePath(),
        ImmutableValueMap.builder()
        .put(LinkNameConstants.PN_LINK_TYPE, ExternalLinkType.ID)
        .put(LinkNameConstants.PN_LINK_EXTERNAL_REF, "http://mysite.com")
        .build()));

    Redirect redirect = context.request().adaptTo(Redirect.class);

    assertEquals(HttpServletResponse.SC_MOVED_PERMANENTLY, context.response().getStatus());
    assertEquals("http://mysite.com", context.response().getHeader("Location"));
    assertFalse(redirect.isRenderPage());
  }

  @Test
  public void testRedirect302() {
    context.currentPage(context.create().page("/content/redirect", DummyAppTemplate.REDIRECT.getTemplatePath(),
        ImmutableValueMap.builder()
        .put(LinkNameConstants.PN_LINK_TYPE, ExternalLinkType.ID)
        .put(LinkNameConstants.PN_LINK_EXTERNAL_REF, "http://mysite.com")
        .put("redirectStatus", Integer.toString(HttpServletResponse.SC_MOVED_TEMPORARILY))
        .build()));

    Redirect redirect = context.request().adaptTo(Redirect.class);

    assertEquals(HttpServletResponse.SC_MOVED_TEMPORARILY, context.response().getStatus());
    assertEquals("http://mysite.com", context.response().getHeader("Location"));
    assertFalse(redirect.isRenderPage());
  }

  @Test
  public void testRedirectInvalid() {
    context.currentPage(context.create().page("/content/redirect", DummyAppTemplate.REDIRECT.getTemplatePath()));

    Redirect redirect = context.request().adaptTo(Redirect.class);

    assertEquals(HttpServletResponse.SC_NOT_FOUND, context.response().getStatus());
    assertFalse(redirect.isRenderPage());
  }

  @Test
  public void testRedirectEditMode() {
    WCMMode.EDIT.toRequest(context.request());
    context.currentPage(context.create().page("/content/redirect", DummyAppTemplate.REDIRECT.getTemplatePath()));

    Redirect redirect = context.request().adaptTo(Redirect.class);

    assertEquals(HttpServletResponse.SC_OK, context.response().getStatus());
    assertTrue(redirect.isRenderPage());
  }

  @Test
  public void testRedirectPreviewMode() {
    WCMMode.PREVIEW.toRequest(context.request());
    context.currentPage(context.create().page("/content/redirect", DummyAppTemplate.REDIRECT.getTemplatePath()));

    Redirect redirect = context.request().adaptTo(Redirect.class);

    assertEquals(HttpServletResponse.SC_OK, context.response().getStatus());
    assertTrue(redirect.isRenderPage());
  }

}

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
package io.wcm.handler.media;

import org.apache.sling.api.resource.Resource;
import org.osgi.annotation.versioning.ProviderType;

import io.wcm.handler.commons.dom.HtmlElement;
import io.wcm.handler.media.format.MediaFormat;

/**
 * Manages media resolving and markup generation.
 * <p>
 * The interface is implemented by a Sling Model. You can adapt from
 * {@link org.apache.sling.api.SlingHttpServletRequest} or {@link org.apache.sling.api.resource.Resource} to get a
 * context-specific handler instance.
 * </p>
 */
@ProviderType
public interface MediaHandler {

  /**
   * Build media which is referenced in the resource (as property or inline binary data).
   * @param resource Resource containing reference to media asset and optionally futher properties like alt. text,
   *          cropping information etc. Alternatively it can contain an inline binary asset uploaded directly to the
   *          resource.
   * @return Media builder
   */
  MediaBuilder get(Resource resource);

  /**
   * Build media which is referenced in the resource (as property or inline binary data).
   * @param resource Resource containing reference to media asset and optionally futher properties like alt. text,
   *          cropping information etc. Alternatively it can contain an inline binary asset uploaded directly to the
   *          resource.
   * @param mediaArgs Additional arguments affecting media resolving
   * @return Media builder
   */
  MediaBuilder get(Resource resource, MediaArgs mediaArgs);

  /**
   * Build media which is referenced in the resource (as property or inline binary data).
   * @param resource Resource containing reference to media asset and optionally futher properties like alt. text,
   *          cropping information etc. Alternatively it can contain an inline binary asset uploaded directly to the
   *          resource.
   * @param mediaFormats Media format(s)
   * @return Media builder
   */
  MediaBuilder get(Resource resource, MediaFormat... mediaFormats);

  /**
   * Build media which is referenced via its string address.
   * @param mediaRef Reference to media item (in most cases a repository path to the DAM asset).
   * @return Media builder
   */
  MediaBuilder get(String mediaRef);

  /**
   * Build media which is referenced via its string address.
   * @param mediaRef Reference to media item (in most cases a repository path to the DAM asset).
   * @param mediaArgs Additional arguments affecting media resolving
   * @return Media builder
   */
  MediaBuilder get(String mediaRef, MediaArgs mediaArgs);

  /**
   * Build media which is referenced via its string address.
   * @param mediaRef Reference to media item (in most cases a repository path to the DAM asset).
   * @param mediaFormats Media format(s)
   * @return Media builder
   */
  MediaBuilder get(String mediaRef, MediaFormat... mediaFormats);

  /**
   * Build media for the given request holding all further request properties.
   * @param mediaRequest Media handling request
   * @return Media builder
   */
  MediaBuilder get(MediaRequest mediaRequest);

  /**
   * Checks if the given HTML element is valid.
   * It is treated as invalid if it is null, or if it e.g. contains only a dummy image (depending on markup builder).
   * @param element Media markup element.
   * @return true if media element is invalid
   */
  boolean isValidElement(HtmlElement<?> element);

}

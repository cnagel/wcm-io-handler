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
package io.wcm.handler.media.format;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class MediaFormatBuilderTest {

  @Test
  public void testBuilder_variant1() {
    MediaFormat mf = MediaFormatBuilder.create("name1")
        .label("label1")
        .description("description1")
        .width(800)
        .height(600)
        .ratio(1.333d)
        .fileSizeMax(10000L)
        .extensions("gif", "png")
        .renditionGroup("group1")
        .download(true)
        .internal(true)
        .ranking(500)
        .build();

    assertEquals("name1", mf.getName());
    assertEquals("label1", mf.getLabel());
    assertEquals("description1", mf.getDescription());
    assertEquals(800, mf.getWidth());
    assertEquals(600, mf.getHeight());
    assertEquals(1.333d, mf.getRatio(), 0.0001d);
    assertEquals(10000L, mf.getFileSizeMax());
    assertArrayEquals(new String[] {
        "gif", "png"
    }, mf.getExtensions());
    assertEquals("group1", mf.getRenditionGroup());
    assertTrue(mf.isDownload());
    assertTrue(mf.isInternal());
    assertEquals(500, mf.getRanking());
  }

  @Test
  public void testBuilder_variant2() {
    MediaFormat mf = MediaFormatBuilder.create("name2")
        .width(400, 800)
        .height(300, 600)
        .ratio(100, 50)
        .build();

    assertEquals("name2", mf.getName());
    assertEquals("name2", mf.getLabel());
    assertNull(mf.getDescription());
    assertEquals(0, mf.getWidth());
    assertEquals(400, mf.getMinWidth());
    assertEquals(800, mf.getMaxWidth());
    assertEquals(0, mf.getHeight());
    assertEquals(300, mf.getMinHeight());
    assertEquals(600, mf.getMaxHeight());
    assertEquals(2d, mf.getRatio(), 0.0001d);
    assertEquals(100, mf.getRatioWidth());
    assertEquals(50, mf.getRatioHeight());
    assertEquals(0L, mf.getFileSizeMax());
    assertArrayEquals(new String[0], mf.getExtensions());
    assertNull("group1", mf.getRenditionGroup());
    assertFalse(mf.isDownload());
    assertFalse(mf.isInternal());
    assertEquals(0, mf.getRanking());
  }

  @Test
  public void testBuilder_variant3() {
    MediaFormat mf = MediaFormatBuilder.create("name2")
        .minWidth(400)
        .maxWidth(800)
        .minHeight(300)
        .maxHeight(600)
        .build();

    assertEquals(0, mf.getWidth());
    assertEquals(400, mf.getMinWidth());
    assertEquals(800, mf.getMaxWidth());
    assertEquals(0, mf.getHeight());
    assertEquals(300, mf.getMinHeight());
    assertEquals(600, mf.getMaxHeight());
  }

  @Test
  public void testBuilder_variant4() {
    MediaFormat mf = MediaFormatBuilder.create("name3")
        .fixedDimension(1000, 500)
        .build();

    assertEquals("name3", mf.getName());
    assertEquals(1000, mf.getWidth());
    assertEquals(500, mf.getHeight());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testNullName() {
    MediaFormatBuilder.create(null).build();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalName() {
    MediaFormatBuilder.create("name with spaces").build();
  }

  @Test
  public void testProperties() {
    Map<String, Object> props = ImmutableMap.<String, Object>of("prop1", "value1");

    MediaFormat mf = MediaFormatBuilder.create("name1")
        .property("prop3", "value3")
        .properties(props)
        .property("prop2", "value2")
        .build();

    assertEquals(3, mf.getProperties().size());
    assertEquals("value1", mf.getProperties().get("prop1", String.class));
    assertEquals("value2", mf.getProperties().get("prop2", String.class));
    assertEquals("value3", mf.getProperties().get("prop3", String.class));
  }

  /**
   * test unmodifiable extensions
   */
  @Test
  public void testExtensions() {
    final String unmodifiableExtension = "png";
    String[] extensionsSource = {
        unmodifiableExtension
    };

    MediaFormat mf = MediaFormatBuilder.create("name")
        .extensions(extensionsSource)
        .build();

    // source modification should have no effect
    extensionsSource[0] = "ThisModificationShouldHaveNoEffect";
    assertEquals(unmodifiableExtension, mf.getExtensions()[0]);

    // now modify the returned extensions, should have no effect
    mf.getExtensions()[0] = "ThisModificationShouldHaveNoEffect";
    assertEquals(unmodifiableExtension, mf.getExtensions()[0]);
  }

}

## Media Handler Usage


### Building media

The [Media Handler][media-handler] is a Sling Model and can be adapted either from a request or a resource. It automatically reads the context-specific configuration for the Site URLs of the [URL Handler][url-handler] based on the resource path of the current request or the path of the resource adapted from.

Example:

```java
MediaHandler mediaHandler = request.adaptTo(MediaHandler.class);

// build media referenced in current resource
Media media = mediaHandler.get(resource).build();

// build media referenced by path with a specific media format
Media media = mediaHandler.get("/content/dam/sample/asset1.jpg").mediaFormat(MediaFormats.MF_16_9).build();

// check if media is valid and get markup
if (media.isValid()) {
  String markup = media.getMarkup();
  // ...
}
```

Alternatively you can inject the `MediaHandler` into your Sling Model using the `@Self` annotation if the model itself adapts from request or resource.

The media handler uses a "builder pattern" so you can flexibly combine the different media generation options.
See [MediaBuilder][media-builder] for all options.


### Media properties in resource

When referencing a media in a resource multiple properties are used to describe the media reference. Some of the properties depend on the media source implementation. These are the most common properties:

* `mediaRef`: Reference/path to the media asset
* `mediaCrop`: Cropping parameters for image
* `mediaAltText`: Alternative text for media

Further properties are defined in [MediaNameConstants][media-name-constants]. It is recommended to define an edit dialog which either allows selection a media asset from repository (e.g. browsing DAM hierarchy), or to upload a binary file to the page.


### Media formats

Media formats describe expected output formats of media assets or images. They are defined as constants using a builder pattern. The most simple type of media format is defining an image with a  fixed dimension:

```java
public static final MediaFormat CONTENT_480 = MediaFormatBuilder.create("content_480")
    .label("Content Standard")
    .fixedDimension(480, 270)
    .extensions("gif", "jpg", "png")
    .build();
```

It is also possible to define a format which matches certain min/max-sizes and a ratio:

```java
public static final MediaFormat MF_16_9 = create("mf_16_9")
    .label("16:9 Image")
    .minWidth(1000)
    .minHeight(500)
    .ratio(2.0d)
    .extensions("gif", "jpg", "png")
    .build();
```

Or a media format defining downloads only restricting by file extensions:

```java
public static final MediaFormat DOWNLOAD = create("download")
    .label("Download")
    .extensions("pdf", "zip", "ppt", "pptx", "doc", "docx")
    .download(true)
    .build();
```

These media format definitions have to be provided to the media handling using an OSGi service extending the [MediaFormatProvider][media-format-provider] class. The default implementation supports extracting the defined formats from the public static fields of a class. Via [Context-Aware Services][sling-commons-caservices] you can make sure the media formats affect only resources (content pages, DAM assets) that are relevant for your application. Thus it is possible to provide different media formats for different applications running in the same AEM instance.

When resolving a media reference it is possible to specify one or multiple media formats. If the media asset contains a rendition that exactly matches the format it is returned. If it contains a rendition that is bigger but has the requested ratio a dynamically downscaled rendition is returned. If cropping parameters are defined they are applied before checking against the media format. If no rendition matches or can be rescaled the resolving process failed and the returned media is invalid. In Edit Mode `DummyImageMediaMarkupBuilder` is then used to render a drop area instead to which an DAM asset can be assigned via drag&drop.

To resolve multiple renditions at once for a responsive image it is possible to specify a list of mandatory media formats:

```java
MediaHandler mediaHandler = request.adaptTo(MediaHandler.class);

// build media with a list of mandatory media formats
Media media = mediaHandler.get(resource)
    .mandatoryMediaFormats(MediaFormats.FORMAT1, MediaFormats.FORMAT2).build();

// get all renditions
if (media.isValid()) {
  for (Rendition rendition : renditions) {
    // check rendition
  }
}
```

A custom markup builder can then generated the image tag with metadata for all breakpoints (depending on the frontend solution).


### Using media in Sightly template

To resolve a media inside a Sightly template you can use a generic Sling Model for calling the handler: [ResourceMedia](apidocs/io/wcm/handler/media/ui/ResourceMedia.html)

Sightly template example:

```html
<sly data-sly-use.media="${'io.wcm.handler.media.ui.ResourceMedia' @ mediaFormat='content_480'}"/>
<figure class="image-small" data-sly-test="${media.valid}">
  <a href="conference.shtml">
    ${media.markup @ context='html'}
  </a>
</figure>
```

In this case the `${media.markup ...}` is replaced with the media markup of the media handler, which is not necessarily is an `img` element, but may be any markup (e.g. a `video` or `audio` or `div` element with custom markup).


### Configuring and tailoring the media resolving process

Optionally you can provide an OSGi service to specify in more detail the media resolving needs of your application. For this you have to extend the [MediaHandlerConfig][media-handler-config] class. Via [Context-Aware Services][sling-commons-caservices] you can make sure the SPI customization affects only resources (content pages, DAM assets) that are relevant for your application. Thus it is possible to provide different customizations for different applications running in the same AEM instance.

With this you can:

* Define which media sources are supported by your application or include your own ones
* Define which markup builders are supported by your application or include your own ones
* Define custom pre- and postprocessors that are called before and after the media resolving takes place
* Implement a method which returns the default quality when writing images with lossy compression

Example:

```java
@Component(service = MediaHandlerConfig.class, property = {
    ContextAwareService.PROPERTY_CONTEXT_PATH_PATTERN + "=^/content/(dam/)?myapp(/.*)?$"
})
public class MediaHandlerConfigImpl extends MediaHandlerConfig {

  private static final List<Class<? extends MediaSource>> MEDIA_SOURCES =
      ImmutableList.<Class<? extends MediaSource>>of(
          DamMediaSource.class,
          InlineMediaSource.class
      );

  private static final List<Class<? extends MediaMarkupBuilder>> MEDIA_MARKUP_BUILDERS =
      ImmutableList.<Class<? extends MediaMarkupBuilder>>of(
          SimpleImageMediaMarkupBuilder.class,
          DummyImageMediaMarkupBuilder.class
      );

  @Override
  public List<Class<? extends MediaSource>> getSources() {
    return MEDIA_SOURCES;
  }

  @Override
  public List<Class<? extends MediaMarkupBuilder>> getMarkupBuilders() {
    return MEDIA_MARKUP_BUILDERS;
  }

}
```

Schematic flow of media handling process:

1. Start media handler processing
2. Detect media source, store result in media request
3. Apply preprocessors on media request
4. Resolve media using media source, store result in media request
5. Generate markup using markup builder, store result in media request
6. Apply postprocessors on media request


### Responsive Images

In a responsive web project there is often the need to show images with same ratio but different resolutions depending on the target device, screen size and pixel depth. The Media Handler helps you on this with special markup builders.

Example - define a media format with a certain ratio and minimum sizes (big enough for for the largest resolution on the website):

```
public static final MediaFormat MF_16_9 = create("mf_16_9")
    .label("Media 16:9")
    .minWidth(1600)
    .minHeight(900)
    .ratio(16, 9)
    .extensions("gif", "jpg", "jpeg", "png")
    .build();
```

Configure the media handling process to use special markup builders:

```java
@Component(service = MediaHandlerConfig.class, property = {
    ContextAwareService.PROPERTY_CONTEXT_PATH_PATTERN + "=^/content/(dam/)?myapp(/.*)?$"
})
public class MediaHandlerConfigImpl extends AbstractMediaHandlerConfig {

  private static final List<Class<? extends MediaMarkupBuilder>> MEDIA_MARKUP_BUILDERS =
      ImmutableList.<Class<? extends MediaMarkupBuilder>>of(
          ResponsiveImageMediaMarkupBuilder.class,
          DummyResponsiveImageMediaMarkupBuilder.class
      );

  @Override
  public List<Class<? extends MediaMarkupBuilder>> getMarkupBuilders() {
    return MEDIA_MARKUP_BUILDERS;
  }

}
```

When building the markup for the media element define the required resolutions for each breakpoint:

```java
Media media = mediaHandler.get(resource)
    .mandatoryMediaFormats(new ResponsiveMediaFormatsBuilder(MediaFormats.MF_16_9)
        .breakpoint("S0", 712, 400)
        .breakpoint("M1", 401, 225)
        .breakpoint("L1", 837, 471)
        .build())
    .build();
```

This results in a markup like this:

```html
<img alt="Alt. Text" data-resp-src="[
  {'mq':'S0','src':'/path/mymedia.712.400.jpg'},
  {'mq':'M1','src':'/path/mymedia.401.225.jpg'},
  {'mq':'L1','src':'/path/mymedia.837.471.jpg'}]"/>
```

You can customize the markup that is generated by subclassing `io.wcm.handler.media.markup.AbstractImageMediaMarkupBuilder`, bring in your own logic and register it in the `MediaHandlerConfig`.




[media-handler]: apidocs/io/wcm/handler/media/MediaHandler.html
[media-builder]: apidocs/io/wcm/handler/media/MediaBuilder.html
[media-name-constants]: apidocs/io/wcm/handler/media/MediaNameConstants.html
[media-handler-config]: apidocs/io/wcm/handler/media/spi/MediaHandlerConfig.html
[media-format-provider]: apidocs/io/wcm/handler/media/spi/MediaFormatProvider.html
[url-handler]: ../url/
[link-handler]: ../link/
[sling-commons-caservices]: ../../sling/commons/context-aware-services.html

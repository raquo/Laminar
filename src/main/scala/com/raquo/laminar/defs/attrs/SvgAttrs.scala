package com.raquo.laminar.defs.attrs

import com.raquo.laminar.keys.SvgAttr
import com.raquo.laminar.codecs.Codec

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait SvgAttrs {


  /**
    * Create SVG attribute (Note: for HTML attrs, use L.htmlAttr)
    *
    * @param name  - name of the attribute, e.g. "value"
    * @param codec - used to encode V into String, e.g. StringAsIsCodec
    *
    * @tparam V    - value type for this attr in Scala
    */
  def svgAttr[V](name: String, codec: Codec[V, String], namespace: Option[String]): SvgAttr[V] = new SvgAttr(name, codec, namespace)


  @inline protected def doubleSvgAttr(name: String): SvgAttr[Double] = svgAttr(name, Codec.doubleAsString, namespace = None)

  @inline protected def intSvgAttr(name: String): SvgAttr[Int] = svgAttr(name, Codec.intAsString, namespace = None)

  @inline protected def stringSvgAttr(name: String): SvgAttr[String] = svgAttr(name, Codec.stringAsIs, namespace = None)

  @inline protected def stringSvgAttr(name: String, namespace: String): SvgAttr[String] = svgAttr(name, Codec.stringAsIs, Option(namespace))



  /**
    * This attribute defines the distance from the origin to the top of accent characters,
    * measured by a distance within the font coordinate system.
    * If the attribute is not specified, the effect is as if the attribute
    * were set to the value of the ascent attribute.
    *
    * Value 	`<number>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/accent-height accent-height @ MDN]]
    */
  lazy val accentHeight: SvgAttr[Double] = doubleSvgAttr("accent-height")


  /**
    * This attribute controls whether or not the animation is cumulative.
    * It is frequently useful for repeated animations to build upon the previous results,
    * accumulating with each iteration. This attribute said to the animation if the value is added to
    * the previous animated attribute's value on each iteration.
    *
    * Value 	none | sum
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/accumulate accumulate @ MDN]]
    */
  lazy val accumulate: SvgAttr[String] = stringSvgAttr("accumulate")


  /**
    * This attribute controls whether or not the animation is additive.
    * It is frequently useful to define animation as an offset or delta
    * to an attribute's value, rather than as absolute values. This
    * attribute said to the animation if their values are added to the
    * original animated attribute's value.
    *
    * Value 	replace | sum
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/additive additive @ MDN]]
    */
  lazy val additive: SvgAttr[String] = stringSvgAttr("additive")


  /**
    * The alignment-baseline attribute specifies how an object is aligned
    * with respect to its parent. This property specifies which baseline
    * of this element is to be aligned with the corresponding baseline of
    * the parent. For example, this allows alphabetic baselines in Roman
    * text to stay aligned across font size changes. It defaults to the
    * baseline with the same name as the computed value of the
    * alignment-baseline property. As a presentation attribute, it also
    * can be used as a property directly inside a CSS stylesheet, see css
    * alignment-baseline for further information.
    *
    * Value: 	auto | baseline | before-edge | text-before-edge | middle | central | after-edge |
    * text-after-edge | ideographic | alphabetic | hanging | mathematical | inherit
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/alignment-baseline alignment-baseline @ MDN]]
    */
  lazy val alignmentBaseline: SvgAttr[String] = stringSvgAttr("alignment-baseline")


  /**
    * This attribute defines the maximum unaccented depth of the font
    * within the font coordinate system. If the attribute is not specified,
    * the effect is as if the attribute were set to the vert-origin-y value
    * for the corresponding font.
    *
    * Value 	`<number>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/ascent ascent @ MDN]]
    */
  lazy val ascent: SvgAttr[Double] = doubleSvgAttr("ascent")


  /**
    * This attribute indicates the name of the attribute in the parent element
    * that is going to be changed during an animation.
    *
    * Value 	`<attributeName>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/attributeName attributeName @ MDN]]
    */
  lazy val attributeName: SvgAttr[String] = stringSvgAttr("attributeName")


  /**
    * This attribute specifies the namespace in which the target attribute
    * and its associated values are defined.
    *
    * Value 	CSS | XML | auto
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/attributeType attributeType @ MDN]]
    */
  lazy val attributeType: SvgAttr[String] = stringSvgAttr("attributeType")


  /**
    * The azimuth attribute represent the direction angle for the light
    * source on the XY plane (clockwise), in degrees from the x axis.
    * If the attribute is not specified, then the effect is as if a
    * value of 0 were specified.
    *
    * Value 	`<number>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/azimuth azimuth @ MDN]]
    */
  lazy val azimuth: SvgAttr[Double] = doubleSvgAttr("azimuth")


  /**
    * The baseFrequency attribute represent The base frequencies parameter
    * for the noise function of the `<feturbulence>` primitive. If two `<number>`s
    * are provided, the first number represents a base frequency in the X
    * direction and the second value represents a base frequency in the Y direction.
    * If one number is provided, then that value is used for both X and Y.
    * Negative values are forbidden.
    * If the attribute is not specified, then the effect is as if a value
    * of 0 were specified.
    *
    * Value 	`<number-optional-number>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/baseFrequency baseFrequency @ MDN]]
    */
  lazy val baseFrequency: SvgAttr[String] = stringSvgAttr("baseFrequency")


  /**
    * The baseline-shift attribute allows repositioning of the dominant-baseline
    * relative to the dominant-baseline of the parent text content element.
    * The shifted object might be a sub- or superscript.
    * As a presentation attribute, it also can be used as a property directly
    * inside a CSS stylesheet, see css baseline-shift for further information.
    *
    * Value 	auto | baseline | sup | sub | <percentage> | <length> | inherit
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/baseline-shift baseline-shift @ MDN]]
    */
  lazy val baselineShift: SvgAttr[String] = stringSvgAttr("baseline-shift")


  /**
    * This attribute defines when an animation should begin.
    * The attribute value is a semicolon separated list of values. The interpretation
    * of a list of start times is detailed in the SMIL specification in "Evaluation
    * of begin and end time lists". Each individual value can be one of the following:
    * `<offset-value>`, `<syncbase-value>`, `<event-value>`, `<repeat-value>`, `<accessKey-value>`,
    * `<wallclock-sync-value>` or the keyword indefinite.
    *
    * Value 	`<begin-value-list>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/begin begin @ MDN]]
    */
  lazy val begin: SvgAttr[String] = stringSvgAttr("begin")


  /**
    * The bias attribute shifts the range of the filter. After applying the kernelMatrix
    * of the `<feConvolveMatrix>` element to the input image to yield a number and applied
    * the divisor attribute, the bias attribute is added to each component. This allows
    * representation of values that would otherwise be clamped to 0 or 1.
    * If bias is not specified, then the effect is as if a value of 0 were specified.
    *
    * Value 	`<number>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/bias bias @ MDN]]
    */
  lazy val bias: SvgAttr[Double] = doubleSvgAttr("bias")


  /**
    * This attribute specifies the interpolation mode for the animation. The default
    * mode is linear, however if the attribute does not support linear interpolation
    * (e.g. for strings), the calcMode attribute is ignored and discrete interpolation is used.
    *
    * Value 	discrete | linear | paced | spline
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/calcMode calcMode @ MDN]]
    */
  lazy val calcMode: SvgAttr[String] = stringSvgAttr("calcMode")


  /**
    * The clip attribute has the same parameter values as defined for the css clip property.
    * Unitless values, which indicate current user coordinates, are permitted on the coordinate
    * values on the `<shape>`. The value of auto defines a clipping path along the bounds of
    * the viewport created by the given element.
    * As a presentation attribute, it also can be used as a property directly inside a
    * CSS stylesheet, see css clip for further information.
    *
    * Value 	`auto | <shape> | inherit`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/clip clip @ MDN]]
    */
  lazy val clip: SvgAttr[String] = stringSvgAttr("clip")


  /**
    * The clip-path attribute bind the element is applied to with a given `<clipPath>` element
    * As a presentation attribute, it also can be used as a property directly inside a CSS stylesheet
    *
    * Value 	`<FuncIRI> | none | inherit`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/clip-path clip-path @ MDN]]
    */
  lazy val clipPathAttr: SvgAttr[String] = stringSvgAttr("clip-path")


  /**
    * The clipPathUnits attribute defines the coordinate system for the contents
    * of the `<clipPath>` element. the clipPathUnits attribute is not specified,
    * then the effect is as if a value of userSpaceOnUse were specified.
    * Note that values defined as a percentage inside the content of the `<clipPath>`
    * are not affected by this attribute. It means that even if you set the value of
    * maskContentUnits to objectBoundingBox, percentage values will be calculated as
    * if the value of the attribute were userSpaceOnUse.
    *
    * Value 	userSpaceOnUse | objectBoundingBox
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/clipPathUnits clipPathUnits @ MDN]]
    */
  lazy val clipPathUnits: SvgAttr[String] = stringSvgAttr("clipPathUnits")


  /**
    * The clip-rule attribute only applies to graphics elements that are contained within a
    * `<clipPath>` element. The clip-rule attribute basically works as the fill-rule attribute,
    * except that it applies to `<clipPath>` definitions.
    *
    * Value 	nonezero | evenodd | inherit
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/clip-rule clip-rule @ MDN]]
    */
  lazy val clipRule: SvgAttr[String] = stringSvgAttr("clip-rule")


  /**
    * The color attribute is used to provide a potential indirect value (currentColor)
    * for the fill, stroke, stop-color, flood-color and lighting-color attributes.
    * As a presentation attribute, it also can be used as a property directly inside a CSS
    * stylesheet, see css color for further information.
    *
    * Value 	`<color> | inherit`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/color color @ MDN]]
    */
  lazy val color: SvgAttr[String] = stringSvgAttr("color")


  /**
    * The color-interpolation attribute specifies the color space for gradient interpolations,
    * color animations and alpha compositing.When a child element is blended into a background,
    * the value of the color-interpolation attribute on the child determines the type of
    * blending, not the value of the color-interpolation on the parent. For gradients which
    * make use of the xlink:href attribute to reference another gradient, the gradient uses
    * the color-interpolation attribute value from the gradient element which is directly
    * referenced by the fill or stroke attribute. When animating colors, color interpolation
    * is performed according to the value of the color-interpolation attribute on the element
    * being animated.
    * As a presentation attribute, it also can be used as a property directly inside a CSS
    * stylesheet, see css color-interpolation for further information
    *
    * Value 	auto | sRGB | linearRGB | inherit
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/color-interpolation color-interpolation @ MDN]]
    */
  lazy val colorInterpolation: SvgAttr[String] = stringSvgAttr("color-interpolation")


  /**
    * The color-interpolation-filters attribute specifies the color space for imaging operations
    * performed via filter effects. Note that color-interpolation-filters has a different
    * initial value than color-interpolation. color-interpolation-filters has an initial
    * value of linearRGB, whereas color-interpolation has an initial value of sRGB. Thus,
    * in the default case, filter effects operations occur in the linearRGB color space,
    * whereas all other color interpolations occur by default in the sRGB color space.
    * As a presentation attribute, it also can be used as a property directly inside a
    * CSS stylesheet, see css color-interpolation-filters for further information
    *
    * Value 	auto | sRGB | linearRGB | inherit
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/color-interpolation-filters color-interpolation-filters @ MDN]]
    */
  lazy val colorInterpolationFilters: SvgAttr[String] = stringSvgAttr("color-interpolation-filters")


  /**
    * The color-profile attribute is used to define which color profile a raster image
    * included through the `<image>` element should use. As a presentation attribute, it
    * also can be used as a property directly inside a CSS stylesheet, see css color-profile
    * for further information.
    *
    * Value 	`auto | sRGB | <name> | <IRI> | inherit`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/color-profile color-profile @ MDN]]
    */
  lazy val colorProfileAttr: SvgAttr[String] = stringSvgAttr("color-profile")


  /**
    * The color-rendering attribute provides a hint to the SVG user agent about how to
    * optimize its color interpolation and compositing operations. color-rendering
    * takes precedence over color-interpolation-filters. For example, assume color-rendering:
    * optimizeSpeed and color-interpolation-filters: linearRGB. In this case, the SVG user
    * agent should perform color operations in a way that optimizes performance, which might
    * mean sacrificing the color interpolation precision as specified by
    * color-interpolation-filters: linearRGB.
    * As a presentation attribute, it also can be used as a property directly inside
    * a CSS stylesheet, see css color-rendering for further information
    *
    * Value 	auto | optimizeSpeed | optimizeQuality | inherit
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/color-rendering color-rendering @ MDN]]
    */
  lazy val colorRendering: SvgAttr[String] = stringSvgAttr("color-rendering")


  /**
    * The contentScriptType attribute on the `<svg>` element specifies the default scripting
    * language for the given document fragment.
    * This attribute sets the default scripting language used to process the value strings
    * in event attributes. This language must be used for all instances of script that do not
    * specify their own scripting language. The value content-type specifies a media type,
    * per MIME Part Two: Media Types [RFC2046]. The default value is application/ecmascript
    *
    * Value 	`<content-type>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/contentScriptType contentScriptType @ MDN]]
    */
  lazy val contentScriptType: SvgAttr[String] = stringSvgAttr("contentScriptType")


  /**
    * This attribute specifies the style sheet language for the given document fragment.
    * The contentStyleType is specified on the `<svg>` element. By default, if it's not defined,
    * the value is text/css
    *
    * Value 	`<content-type>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/contentStyleType contentStyleType @ MDN]]
    */
  lazy val contentStyleType: SvgAttr[String] = stringSvgAttr("contentStyleType")


  /**
    * The cursor attribute specifies the mouse cursor displayed when the mouse pointer
    * is over an element.This attribute behave exactly like the css cursor property except
    * that if the browser suport the `<cursor>` element, it should allow to use it with the
    * `<funciri>` notation. As a presentation attribute, it also can be used as a property
    * directly inside a CSS stylesheet, see css cursor for further information.
    *
    * Value 	 auto | crosshair | default | pointer | move | e-resize |
    * ne-resize | nw-resize | n-resize | se-resize | sw-resize | s-resize | w-resize| text |
    * wait | help | inherit
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/cursor cursor @ MDN]]
    */
  lazy val cursorAttr: SvgAttr[String] = stringSvgAttr("cursor")


  /**
    * For the `<circle>` and the `<ellipse>` element, this attribute define the x-axis coordinate
    * of the center of the element. If the attribute is not specified, the effect is as if a
    * value of "0" were specified.For the `<radialGradient>` element, this attribute define
    * the x-axis coordinate of the largest (i.e., outermost) circle for the radial gradient.
    * The gradient will be drawn such that the 100% gradient stop is mapped to the perimeter
    * of this largest (i.e., outermost) circle. If the attribute is not specified, the effect
    * is as if a value of 50% were specified
    *
    * Value 	`<coordinate>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/cx cx @ MDN]]
    */
  lazy val cx: SvgAttr[String] = stringSvgAttr("cx")


  /**
    * For the `<circle>` and the `<ellipse>` element, this attribute define the y-axis coordinate
    * of the center of the element. If the attribute is not specified, the effect is as if a
    * value of "0" were specified.For the `<radialGradient>` element, this attribute define
    * the x-axis coordinate of the largest (i.e., outermost) circle for the radial gradient.
    * The gradient will be drawn such that the 100% gradient stop is mapped to the perimeter
    * of this largest (i.e., outermost) circle. If the attribute is not specified, the effect
    * is as if a value of 50% were specified
    *
    * Value 	`<coordinate>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/cy cy @ MDN]]
    */
  lazy val cy: SvgAttr[String] = stringSvgAttr("cy")


  /**
    * The d attribute defines a path to be drawn. It contains a series of commands and parameters
    * used by the path element. Each command is designated by a specific letter (e.g., M for 'move to',
    * L for 'line to', C for 'curve to', etc.). Following the command letter are the parameters for
    * that command, typically coordinate pairs or triplets.
    *
    * Value: `<path-data>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/d d @ MDN]]
    */
  lazy val d: SvgAttr[String] = stringSvgAttr("d")


  /**
    * The diffuseConstant attribute represents the kd value in the Phong lighting model.
    * In SVG, this can be any non-negative number.
    * If the attribute is not specified, then the effect is as if a value of 1 were specified.
    *
    * Value: `<number>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/diffuseConstant diffuseConstant @ MDN]]
    */
  lazy val diffuseConstant: SvgAttr[String] = stringSvgAttr("diffuseConstant")


  /**
    * The direction attribute specifies the base writing direction of text and the direction of embeddings
    * and overrides for the Unicode bidirectional algorithm. It defines whether the text is rendered
    * left-to-right (ltr) or right-to-left (rtl).
    * As a presentation attribute, it also can be used as a property directly inside a CSS stylesheet.
    *
    * Value: ltr | rtl | inherit
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/direction direction @ MDN]]
    */
  lazy val direction: SvgAttr[String] = stringSvgAttr("direction")


  /**
    * The display attribute lets you control the rendering of graphical or container elements.
    * A value of display="none" indicates that the given element and its children will not be rendered.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/display display @ MDN]]
    */
  lazy val display: SvgAttr[String] = stringSvgAttr("display")


  /**
    * The divisor attribute specifies the value by which the resulting number of applying the kernelMatrix
    * of the `<feConvolveMatrix>` element to the input image color value is divided to yield the destination
    * color value.
    *
    * Value: `<number>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/divisor divisor @ MDN]]
    */
  lazy val divisor: SvgAttr[String] = stringSvgAttr("divisor")


  /**
    * The dominant-baseline attribute specifies the dominant baseline, which is the baseline used to align
    * the box's text and inline-level contents. It also indicates the default alignment baseline of any boxes
    * participating in baseline alignment in the box's alignment context.
    *
    * Value: auto | use-script | no-change | reset-size | ideographic | alphabetic | hanging | mathematical |
    * central | middle | text-after-edge | text-before-edge | inherit
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/dominant-baseline dominant-baseline @ MDN]]
    */
  lazy val dominantBaseline: SvgAttr[String] = stringSvgAttr("dominant-baseline")


  /**
    * The dur attribute indicates the simple duration of an animation. The attribute value can be a
    * `<clock-value>` or the indefinite keyword. A clock-value is a time value that can be used to control
    * the timeline of the animation. It can be specified as hh:mm:ss.s or ms, where the former indicates
    * hours, minutes, seconds, and milliseconds, and the latter indicates milliseconds.
    *
    * Value: `<clock-value>` | media | indefinite
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/dur dur @ MDN]]
    */
  lazy val dur: SvgAttr[String] = stringSvgAttr("dur")


  /**
    * The dx attribute indicates a shift along the x-axis on the position of an element or its content.
    * The exact effect of this attribute is dependent on the element for which it's being used.
    * For `<text>` elements, dx defines the distance to offset the current text position along the x-axis.
    * For `<feOffset>`, dx defines the x offset of the filter input graphic.
    *
    * Value: `<number>` | `<percentage>` | `<length>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/dx dx @ MDN]]
    */
  lazy val dx: SvgAttr[String] = stringSvgAttr("dx")


  /**
    * The dy attribute indicates a shift along the y-axis on the position of an element or its content.
    * The exact effect of this attribute is dependent on the element for which it's being used.
    * For `<text>` elements, dy defines the distance to offset the current text position along the y-axis.
    * For `<feOffset>`, dy defines the y offset of the filter input graphic.
    *
    * Value: `<number>` | `<percentage>` | `<length>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/dy dy @ MDN]]
    */
  lazy val dy: SvgAttr[String] = stringSvgAttr("dy")


  /**
    * The edgeMode attribute determines how to extend the input image as necessary with color values so that
    * the matrix operations can be applied when the kernel is positioned at or near the edge of the input image.
    * If attribute is not specified, then the effect is as if a value of duplicate were specified.
    *
    * Value: duplicate | wrap | none
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/edgeMode edgeMode @ MDN]]
    */
  lazy val edgeMode: SvgAttr[String] = stringSvgAttr("edgeMode")


  /**
    * The elevation attribute specifies the direction angle for a light source from the XY plane towards the Z-axis,
    * in degrees. Note that the positive Z-axis points towards the viewer of the content.
    * If the attribute is not specified, then the effect is as if a value of 0 were specified.
    *
    * Value: `<number>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/elevation elevation @ MDN]]
    */
  lazy val elevation: SvgAttr[Double] = doubleSvgAttr("elevation")


  /**
    * The end attribute defines an end value for the animation that can constrain the active duration.
    * The attribute value is a semicolon-separated list of values. Each value can be a `<clock-value>`,
    * a `<syncbase-value>`, an `<event-value>`, a `<repeat-value>`, an `<accessKey-value>`,
    * a `<wallclock-sync-value>` or the keyword indefinite.
    *
    * Value: `<end-value-list>`
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/end end @ MDN]]
    */
  lazy val end: SvgAttr[String] = stringSvgAttr("end")


  /**
    * The externalResourcesRequired attribute is a hint to browsers about whether external resources
    * need to be fetched for the current element to render correctly.
    * Note: This attribute is deprecated and browsers may ignore it.
    *
    * Value: true | false
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/externalResourcesRequired externalResourcesRequired @ MDN]]
    */
  lazy val externalResourcesRequired: SvgAttr[String] = stringSvgAttr("externalResourcesRequired")


  /**
    * The fill attribute is a presentation attribute defining the color used to paint the element's interior.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: <color> | <FuncIRI> | none | currentColor
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill fill @ MDN]]
    */
  lazy val fill: SvgAttr[String] = stringSvgAttr("fill")


  /**
    * The fill-opacity attribute is a presentation attribute defining the opacity of the paint
    * server (color, gradient, pattern, etc.) applied to the fill of a shape.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: [0-1] | <percentage>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill-opacity fill-opacity @ MDN]]
    */
  lazy val fillOpacity: SvgAttr[String] = stringSvgAttr("fill-opacity")


  /**
    * The fill-rule attribute is a presentation attribute defining the algorithm to use to determine
    * the inside part of a shape. It's used by the fill operation to determine which areas of a shape
    * are filled when the shape overlaps itself.
    *
    * Value: nonzero | evenodd | inherit
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill-rule fill-rule @ MDN]]
    */
  lazy val fillRule: SvgAttr[String] = stringSvgAttr("fill-rule")


  /**
    * The filter attribute defines the filter effects to apply to the element.
    * It contains a reference to a <filter> element which defines the filter to use.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: none | <FuncIRI>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/filter filter @ MDN]]
    */
  lazy val filterAttr: SvgAttr[String] = stringSvgAttr("filter")


  /**
    * The filterRes attribute indicates the width and height of the intermediate images in pixels
    * for a filter primitive. It takes the form of two numbers separated by whitespace.
    * Note: This attribute is deprecated and may be removed in future SVG versions.
    *
    * Value: <number-optional-number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/filterRes filterRes @ MDN]]
    */
  lazy val filterRes: SvgAttr[String] = stringSvgAttr("filterRes")


  /**
    * The filterUnits attribute defines the coordinate system for the attributes x, y, width and height.
    * If not specified, the effect is as if a value of objectBoundingBox were specified.
    *
    * Value: userSpaceOnUse | objectBoundingBox
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/filterUnits filterUnits @ MDN]]
    */
  lazy val filterUnits: SvgAttr[String] = stringSvgAttr("filterUnits")


  /**
    * The flood-color attribute indicates what color to use to flood the current filter primitive.
    * The keyword currentColor and ICC colors can be specified in the same manner as within a
    * <paint> specification for the fill and stroke attributes.
    *
    * Value: currentColor | <color> | <icccolor>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/flood-color flood-color @ MDN]]
    */
  lazy val floodColor: SvgAttr[String] = stringSvgAttr("flood-color")


  /**
    * The flood-opacity attribute indicates the opacity value to use across the current filter
    * primitive subregion. If a value is outside the range 0.0 to 1.0, it is clamped to the nearest
    * valid value.
    *
    * Value: <alpha-value>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/flood-opacity flood-opacity @ MDN]]
    */
  lazy val floodOpacity: SvgAttr[String] = stringSvgAttr("flood-opacity")


  /**
    * The font-family attribute indicates which font family will be used to render the text.
    * It works identically to the CSS font-family property.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: [[<family-name> | <generic-family>],]* [<family-name> | <generic-family>]
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-family font-family @ MDN]]
    */
  lazy val fontFamily: SvgAttr[String] = stringSvgAttr("font-family")


  /**
    * The font-size attribute refers to the size of the font from baseline to baseline when
    * multiple lines of text are set solid in a multiline layout environment.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: <absolute-size> | <relative-size> | <length> | <percentage>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-size font-size @ MDN]]
    */
  lazy val fontSize: SvgAttr[String] = stringSvgAttr("font-size")


  /**
    * The font-size-adjust attribute allows authors to specify an aspect value for an element that will
    * preserve the x-height of the first choice font in a substitute font.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: none | <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-size-adjust font-size-adjust @ MDN]]
    */
  lazy val fontSizeAdjust: SvgAttr[String] = stringSvgAttr("font-size-adjust")


  /**
    * The font-stretch attribute indicates the desired amount of condensing or expansion in the glyphs
    * used to render the text.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: normal | wider | narrower | ultra-condensed | extra-condensed | condensed |
    * semi-condensed | semi-expanded | expanded | extra-expanded | ultra-expanded | inherit
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-stretch font-stretch @ MDN]]
    */
  lazy val fontStretch: SvgAttr[String] = stringSvgAttr("font-stretch")


  /**
    * The font-variant attribute indicates whether the text is to be rendered using variations of the
    * font's glyphs, such as small caps or ligatures.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: normal | small-caps | inherit
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-variant font-variant @ MDN]]
    */
  lazy val fontVariant: SvgAttr[String] = stringSvgAttr("font-variant")


  /**
    * The font-weight attribute refers to the boldness or lightness of the glyphs used to render the text.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: normal | bold | bolder | lighter | 100 | 200 | 300 | 400 | 500 | 600 | 700 | 800 | 900
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-weight font-weight @ MDN]]
    */
  lazy val fontWeight: SvgAttr[String] = stringSvgAttr("font-weight")


  /**
    * The from attribute indicates the initial value of the attribute that will be modified during the
    * animation. When used with the to attribute, the animation will change the modified attribute
    * from the from value to the to value.
    *
    * Value: <value>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/from from @ MDN]]
    */
  lazy val from: SvgAttr[String] = stringSvgAttr("from")


  /**
    * The gradientTransform attribute contains the definition of an optional additional transformation
    * from the gradient coordinate system onto the target coordinate system.
    * This allows for things such as skewing the gradient or rotating it.
    *
    * Value: <transform-list>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/gradientTransform gradientTransform @ MDN]]
    */
  lazy val gradientTransform: SvgAttr[String] = stringSvgAttr("gradientTransform")


  /**
    * The gradientUnits attribute defines the coordinate system used for attributes specified on the
    * gradient elements. If not specified, the effect is as if a value of objectBoundingBox were specified.
    *
    * Value: userSpaceOnUse | objectBoundingBox
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/gradientUnits gradientUnits @ MDN]]
    */
  lazy val gradientUnits: SvgAttr[String] = stringSvgAttr("gradientUnits")


  /**
    * The height attribute defines the vertical length of an element in the user coordinate system.
    * For SVG elements, it determines the height of the viewport or viewBox.
    *
    * Value: <length> | <percentage> | auto
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/height height @ MDN]]
    */
  lazy val height: SvgAttr[String] = stringSvgAttr("height")


  /**
    * The href attribute defines a link to a resource as a reference URL. The exact meaning of that
    * link depends on the context of each element using it.
    * Note: SVG 2 removed the xlink:href attribute in favor of simply href.
    *
    * Value: <URL>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/href href @ MDN]]
    */
  lazy val href: SvgAttr[String] = stringSvgAttr("href")


  /**
    * The imageRendering attribute provides a hint to the browser about how to make speed vs. quality
    * tradeoffs as it performs image processing.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: auto | optimizeSpeed | optimizeQuality
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/imageRendering imageRendering @ MDN]]
    */
  lazy val imageRendering: SvgAttr[String] = stringSvgAttr("imageRendering")


  /**
    * The id attribute assigns a unique name to an element. This can be used for referencing the element
    * in JavaScript, CSS, and SVG animations. It's also used with fragment identifiers in URLs.
    *
    * Value: <id>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/id id @ MDN]]
    */
  lazy val idAttr: SvgAttr[String] = stringSvgAttr("id")


  /**
    * The in attribute identifies input for the given filter primitive.
    * The value can be either a reference to a result from a previous filter primitive,
    * or one of the standard filter input keywords.
    *
    * Value: SourceGraphic | SourceAlpha | BackgroundImage | BackgroundAlpha | FillPaint |
    * StrokePaint | <filter-primitive-reference>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/in in @ MDN]]
    */
  lazy val in: SvgAttr[String] = stringSvgAttr("in")


  /**
    * The in2 attribute identifies the second input for the given filter primitive. It works exactly
    * like the in attribute.
    * This attribute is only valid for the feBlend, feComposite, feDisplacementMap, and feMorphology
    * filter primitives.
    *
    * Value: SourceGraphic | SourceAlpha | BackgroundImage | BackgroundAlpha | FillPaint |
    * StrokePaint | <filter-primitive-reference>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/in2 in2 @ MDN]]
    */
  lazy val in2: SvgAttr[String] = stringSvgAttr("in2")


  /**
    * The k1 attribute defines one of the values to be used within the arithmetic operation of the
    * <feComposite> filter primitive.
    * If this attribute is not set, the effect is as if a value of 0 were used.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/k1 k1 @ MDN]]
    */
  lazy val k1: SvgAttr[Double] = doubleSvgAttr("k1")


  /**
    * The k2 attribute defines one of the values to be used within the arithmetic operation of the
    * <feComposite> filter primitive.
    * If this attribute is not set, the effect is as if a value of 0 were used.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/k2 k2 @ MDN]]
    */
  lazy val k2: SvgAttr[Double] = doubleSvgAttr("k2")


  /**
    * The k3 attribute defines one of the values to be used within the arithmetic operation of the
    * <feComposite> filter primitive.
    * If this attribute is not set, the effect is as if a value of 0 were used.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/k3 k3 @ MDN]]
    */
  lazy val k3: SvgAttr[Double] = doubleSvgAttr("k3")


  /**
    * The k4 attribute defines one of the values to be used within the arithmetic operation of the
    * <feComposite> filter primitive.
    * If this attribute is not set, the effect is as if a value of 0 were used.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/k4 k4 @ MDN]]
    */
  lazy val k4: SvgAttr[Double] = doubleSvgAttr("k4")


  /**
    * The kernelMatrix attribute defines the list of numbers that make up the kernel matrix for the
    * <feConvolveMatrix> element.
    * The number of entries in the list must equal to orderX times orderY.
    *
    * Value: <list-of-numbers>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/kernelMatrix kernelMatrix @ MDN]]
    */
  lazy val kernelMatrix: SvgAttr[String] = stringSvgAttr("kernelMatrix")


  /**
    * The kernelUnitLength attribute has two meanings based on the context it's used in.
    * For lighting filter primitives, it represents the x and y coordinates for the distance from the
    * source to the surface. For feConvolveMatrix, it defines the intended distance in current filter
    * units between successive columns and rows in the kernelMatrix.
    *
    * Value: <number-optional-number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/kernelUnitLength kernelUnitLength @ MDN]]
    */
  lazy val kernelUnitLength: SvgAttr[String] = stringSvgAttr("kernelUnitLength")


  /**
    * The kerning attribute indicates whether the browser should adjust inter-glyph spacing based on
    * kerning tables in the font or not.
    * Note: This attribute is deprecated and may be removed in future SVG versions.
    *
    * Value: auto | <length>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/kerning kerning @ MDN]]
    */
  lazy val kerning: SvgAttr[String] = stringSvgAttr("kerning")


  /**
    * The keySplines attribute defines a set of Bézier control points associated with the keyTimes list,
    * defining a cubic Bézier function that controls interval pacing.
    * This attribute is ignored unless the calcMode attribute is set to spline.
    *
    * Value: <list-of-control-points>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/keySplines keySplines @ MDN]]
    */
  lazy val keySplines: SvgAttr[String] = stringSvgAttr("keySplines")


  /**
    * The keyTimes attribute represents a list of time values used to control the pacing of the animation.
    * Each time in the list corresponds to a value in the values attribute list, and defines when
    * the value is used in the animation.
    *
    * Value: <list-of-times>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/keyTimes keyTimes @ MDN]]
    */
  lazy val keyTimes: SvgAttr[String] = stringSvgAttr("keyTimes")


  /**
    * The letter-spacing attribute controls spacing between text characters, in addition to any spacing
    * from the kerning attribute.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: normal | <length>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/letter-spacing letter-spacing @ MDN]]
    */
  lazy val letterSpacing: SvgAttr[String] = stringSvgAttr("letter-spacing")


  /**
    * The lighting-color attribute defines the color of the light source for filter primitives
    * <feDiffuseLighting> and <feSpecularLighting>.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: currentColor | <color> | <icccolor>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/lighting-color lighting-color @ MDN]]
    */
  lazy val lightingColor: SvgAttr[String] = stringSvgAttr("lighting-color")


  /**
    * The limitingConeAngle attribute represents the angle in degrees between the spot light axis
    * (i.e., the axis between the light source and the point to which it is pointing at) and the
    * spot light cone. If no value is specified, then the effect is as if a value of 90 were specified.
    *
    * Value: <angle>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/limitingConeAngle limitingConeAngle @ MDN]]
    */
  lazy val limitingConeAngle: SvgAttr[String] = stringSvgAttr("limitingConeAngle")


  /**
    * The local attribute defines one side of a coordinate transformation to be applied to an element.
    * It's used along with the 'from' attribute, which defines the other side of the transformation.
    *
    * Value: <string>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/local local @ MDN]]
    */
  lazy val local: SvgAttr[String] = stringSvgAttr("local")


  /**
    * The marker-end attribute defines the arrowhead or polymarker that will be drawn at the final
    * vertex of the given shape.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: none | <FuncIRI>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/marker-end marker-end @ MDN]]
    */
  lazy val markerEnd: SvgAttr[String] = stringSvgAttr("marker-end")


  /**
    * The marker-mid attribute defines the arrowhead or polymarker that will be drawn at every vertex
    * other than the first and last vertex of the given shape.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: none | <FuncIRI>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/marker-mid marker-mid @ MDN]]
    */
  lazy val markerMid: SvgAttr[String] = stringSvgAttr("marker-mid")


  /**
    * The marker-start attribute defines the arrowhead or polymarker that will be drawn at the first
    * vertex of the given shape.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: none | <FuncIRI>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/marker-start marker-start @ MDN]]
    */
  lazy val markerStart: SvgAttr[String] = stringSvgAttr("marker-start")


  /**
    * The markerHeight attribute represents the height of the viewport into which the marker is to be
    * fitted when it is rendered according to the viewBox and preserveAspectRatio attributes.
    *
    * Value: <length>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/markerHeight markerHeight @ MDN]]
    */
  lazy val markerHeight: SvgAttr[String] = stringSvgAttr("markerHeight")


  /**
    * The markerUnits attribute defines the coordinate system for the markerWidth and markerHeight
    * attributes and the contents of the <marker>.
    * If not specified, the effect is as if a value of strokeWidth were specified.
    *
    * Value: userSpaceOnUse | strokeWidth
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/markerUnits markerUnits @ MDN]]
    */
  lazy val markerUnits: SvgAttr[String] = stringSvgAttr("markerUnits")


  /**
    * The markerWidth attribute represents the width of the viewport into which the marker is to be
    * fitted when it is rendered according to the viewBox and preserveAspectRatio attributes.
    *
    * Value: <length>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/markerWidth markerWidth @ MDN]]
    */
  lazy val markerWidth: SvgAttr[String] = stringSvgAttr("markerWidth")


  /**
    * The maskContentUnits attribute indicates which coordinate system to use for the contents of the
    * <mask> element.
    * If not specified, the effect is as if a value of userSpaceOnUse were specified.
    *
    * Value: userSpaceOnUse | objectBoundingBox
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/maskContentUnits maskContentUnits @ MDN]]
    */
  lazy val maskContentUnits: SvgAttr[String] = stringSvgAttr("maskContentUnits")


  /**
    * The maskUnits attribute indicates which coordinate system to use for the attributes x, y, width
    * and height on the <mask> element.
    * If not specified, the effect is as if a value of objectBoundingBox were specified.
    *
    * Value: userSpaceOnUse | objectBoundingBox
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/maskUnits maskUnits @ MDN]]
    */
  lazy val maskUnits: SvgAttr[String] = stringSvgAttr("maskUnits")


  /**
    * The mask attribute is a presentation attribute mainly used to bind a given <mask> element with
    * the element the attribute belongs to.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: none | <FuncIRI>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/mask mask @ MDN]]
    */
  lazy val maskAttr: SvgAttr[String] = stringSvgAttr("mask")


  /**
    * The max attribute defines the maximum value of the range input. It must be greater than or equal
    * to the value of the min attribute.
    * If the value is less than the minimum value, the minimum value is used instead.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/max max @ MDN]]
    */
  lazy val maxAttr: SvgAttr[String] = stringSvgAttr("max")


  /**
    * The min attribute defines the minimum value of the range input. It must be less than or equal
    * to the value of the max attribute.
    * If the value is greater than the maximum value, the maximum value is used instead.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/min min @ MDN]]
    */
  lazy val minAttr: SvgAttr[String] = stringSvgAttr("min")


  /**
    * The mode attribute defines the blending mode on the <feBlend> filter primitive.
    *
    * Value: normal | multiply | screen | darken | lighten
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/mode mode @ MDN]]
    */
  lazy val mode: SvgAttr[String] = stringSvgAttr("mode")


  /**
    * The numOctaves attribute defines the number of octaves for the noise function of the
    * <feTurbulence> primitive.
    * If not specified, the effect is as if a value of 1 were specified.
    *
    * Value: <integer>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/numOctaves numOctaves @ MDN]]
    */
  lazy val numOctaves: SvgAttr[Int] = intSvgAttr("numOctaves")


  /**
    * The offset attribute either defines the distance of an <stop> element from the start of the
    * gradient, or defines the displacement value for a <feOffset> element.
    * For <stop> elements, it's a percentage or a number between 0 and 1.
    * For <feOffset>, it represents the displacement in the x or y direction.
    *
    * Value: <number> | <percentage>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/offset offset @ MDN]]
    */
  lazy val offsetAttr: SvgAttr[String] = stringSvgAttr("offset")


  /**
    * This attribute defines the orientation of the marker relative to the shape it is attached to.
    *
    * Value type: `auto|auto-start-reverse|<angle>` ; Default value: 0; Animatable: yes
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/orient orient @ MDN]]
    */
  lazy val orient: SvgAttr[String] = stringSvgAttr("orient")


  /**
    * The opacity attribute specifies the transparency of an object or of a group of objects.
    * It's a multiplier on the alpha channel and can take a value between 0 and 1 (or a percentage).
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: [0-1] | <percentage>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/opacity opacity @ MDN]]
    */
  lazy val opacity: SvgAttr[String] = stringSvgAttr("opacity")


  /**
    * The operator attribute has different meanings based on the context where it's used.
    * For feComposite, it defines the compositing operation to be performed.
    * For feMorphology, it defines whether to erode or dilate the source graphic.
    *
    * Value: over | in | out | atop | xor | arithmetic (for feComposite)
    * Value: erode | dilate (for feMorphology)
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/operator operator @ MDN]]
    */
  lazy val operator: SvgAttr[String] = stringSvgAttr("operator")


  /**
    * The order attribute indicates the size of the matrix to be used by the <feConvolveMatrix> element.
    * It contains two numbers separated by whitespace which define the number of columns and rows
    * in the matrix.
    *
    * Value: <number-pair>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/order order @ MDN]]
    */
  lazy val order: SvgAttr[String] = stringSvgAttr("order")


  /**
    * The overflow attribute sets what to do when an element's content is too big to fit in its block
    * formatting context. This attribute has the same parameter values as the CSS overflow property.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: visible | hidden | scroll | auto
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/overflow overflow @ MDN]]
    */
  lazy val overflow: SvgAttr[String] = stringSvgAttr("overflow")


  /**
    * The paint-order attribute specifies the order in which the fill, stroke, and markers of a given
    * shape or text element are painted. By default, the order is: fill, stroke, markers.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: normal | [ fill || stroke || markers ]
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/paint-order paint-order @ MDN]]
    */
  lazy val paintOrder: SvgAttr[String] = stringSvgAttr("paint-order")


  /**
    * The pathLength attribute lets authors specify a total length for the path, in user units.
    * This value is used to calibrate the browser's distance calculations with those of the author,
    * particularly useful for text on a path or motion animations.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pathLength pathLength @ MDN]]
    */
  lazy val pathLength: SvgAttr[String] = stringSvgAttr("pathLength")


  /**
    * The patternContentUnits attribute defines the coordinate system for the contents of the <pattern>.
    * If not specified, the effect is as if a value of userSpaceOnUse were specified.
    *
    * Value: userSpaceOnUse | objectBoundingBox
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/patternContentUnits patternContentUnits @ MDN]]
    */
  lazy val patternContentUnits: SvgAttr[String] = stringSvgAttr("patternContentUnits")


  /**
    * The patternTransform attribute contains the definition of an optional additional transformation
    * from the pattern coordinate system onto the target coordinate system.
    * This allows for effects like rotating or skewing the pattern tiles.
    *
    * Value: <transform-list>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/patternTransform patternTransform @ MDN]]
    */
  lazy val patternTransform: SvgAttr[String] = stringSvgAttr("patternTransform")


  /**
    * The patternUnits attribute defines the coordinate system for attributes x, y, width, and height.
    * If not specified, the effect is as if a value of objectBoundingBox were specified.
    *
    * Value: userSpaceOnUse | objectBoundingBox
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/patternUnits patternUnits @ MDN]]
    */
  lazy val patternUnits: SvgAttr[String] = stringSvgAttr("patternUnits")


  /**
    * The pointer-events attribute defines whether or when an element may be the target of a mouse event.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: bounding-box | visiblePainted | visibleFill | visibleStroke | visible |
    * painted | fill | stroke | all | none
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pointer-events pointer-events @ MDN]]
    */
  lazy val pointerEvents: SvgAttr[String] = stringSvgAttr("pointer-events")


  /**
    * The points attribute defines a list of points required to draw a <polyline> or <polygon> element.
    * Each point is defined by a pair of X and Y coordinates in the user coordinate system.
    *
    * Value: <list-of-points>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/points points @ MDN]]
    */
  lazy val points: SvgAttr[String] = stringSvgAttr("points")


  /**
    * The pointsAtX attribute represents the X location in the coordinate system established by the
    * primitiveUnits attribute on the <filter> element of the point at which the light source is
    * pointing.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pointsAtX pointsAtX @ MDN]]
    */
  lazy val pointsAtX: SvgAttr[String] = stringSvgAttr("pointsAtX")


  /**
    * The pointsAtY attribute represents the Y location in the coordinate system established by the
    * primitiveUnits attribute on the <filter> element of the point at which the light source is
    * pointing.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pointsAtY pointsAtY @ MDN]]
    */
  lazy val pointsAtY: SvgAttr[String] = stringSvgAttr("pointsAtY")


  /**
    * The pointsAtZ attribute represents the Z location in the coordinate system established by the
    * primitiveUnits attribute on the <filter> element of the point at which the light source is
    * pointing.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pointsAtZ pointsAtZ @ MDN]]
    */
  lazy val pointsAtZ: SvgAttr[String] = stringSvgAttr("pointsAtZ")


  /**
    * The preserveAlpha attribute indicates how a <feConvolveMatrix> element handles alpha transparency.
    * If set to false, the convolution matrix is applied to the alpha channel in the same way as the
    * RGB color channels. If set to true, the alpha channel is kept unchanged.
    *
    * Value: true | false
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/preserveAlpha preserveAlpha @ MDN]]
    */
  lazy val preserveAlpha: SvgAttr[String] = stringSvgAttr("preserveAlpha")


  /**
    * The preserveAspectRatio attribute indicates how an element with a viewBox providing a given
    * aspect ratio should fit into a viewport with a different aspect ratio.
    * It consists of an optional alignment parameter and an optional 'meet or slice' reference.
    *
    * Value: [none | xMinYMin | xMidYMin | xMaxYMin | xMinYMid | xMidYMid | xMaxYMid |
    * xMinYMax | xMidYMax | xMaxYMax] [meet | slice]
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/preserveAspectRatio preserveAspectRatio @ MDN]]
    */
  lazy val preserveAspectRatio: SvgAttr[String] = stringSvgAttr("preserveAspectRatio")


  /**
    * The primitiveUnits attribute defines the coordinate system for the various length values within
    * the filter primitives and for the attributes that define the filter primitive subregion.
    * If not specified, the effect is as if a value of userSpaceOnUse were specified.
    *
    * Value: userSpaceOnUse | objectBoundingBox
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/primitiveUnits primitiveUnits @ MDN]]
    */
  lazy val primitiveUnits: SvgAttr[String] = stringSvgAttr("primitiveUnits")


  /**
    * The r attribute defines the radius of a circle. If the attribute is not specified, the effect
    * is as if a value of 0 were specified.
    *
    * Value: <length> | <percentage>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/r r @ MDN]]
    */
  lazy val r: SvgAttr[String] = stringSvgAttr("r")


  /**
    * The radius attribute represents the radius (or radii) for the operation on a given
    * <feMorphology> filter primitive. If two numbers are provided, the first represents the
    * x-radius and the second the y-radius. If one number is provided, then that value is used
    * for both x and y.
    *
    * Value: <number-optional-number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/radius radius @ MDN]]
    */
  lazy val radius: SvgAttr[String] = stringSvgAttr("radius")


  /**
    * The refX attribute is used alongside the refY attribute to provide coordinates for the location on the
    * marker where it will be joined to its markable element. Coordinates are relative to the marker's
    * coordinate system (after application of the ‘viewBox’ and ‘preserveAspectRatio’ attributes), and not
    * the markable element it is placed on.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/refX refX @ MDN]]
    */
  lazy val refX: SvgAttr[String] = stringSvgAttr("refX")


  /**
    * The refY attribute is used alongside the refY attribute to provide coordinates for the location on the
    * marker where it will be joined to its markable element. Coordinates are relative to the marker's
    * coordinate system (after application of the ‘viewBox’ and ‘preserveAspectRatio’ attributes), and not
    * the markable element it is placed on.
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/refY refY @ MDN]]
    */
  lazy val refY: SvgAttr[String] = stringSvgAttr("refY")


  /**
    * The repeatCount attribute indicates the number of times an animation will repeat.
    * It can be a simple count or the keyword 'indefinite', which specifies that the animation
    * will repeat indefinitely.
    *
    * Value: <number> | indefinite
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/repeatCount repeatCount @ MDN]]
    */
  lazy val repeatCount: SvgAttr[String] = stringSvgAttr("repeatCount")


  /**
    * The repeatDur attribute specifies the total duration for repeating an animation.
    * It can be a time value or the keyword 'indefinite', which specifies that the animation
    * will repeat indefinitely.
    *
    * Value: <clock-value> | indefinite
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/repeatDur repeatDur @ MDN]]
    */
  lazy val repeatDur: SvgAttr[String] = stringSvgAttr("repeatDur")


  /**
    * The requiredFeatures attribute takes a list of feature strings, with the element only being
    * rendered if all features are supported by the browser.
    * Note: This attribute is deprecated and may be removed in future SVG versions.
    *
    * Value: <list-of-features>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/requiredFeatures requiredFeatures @ MDN]]
    */
  lazy val requiredFeatures: SvgAttr[String] = stringSvgAttr("requiredFeatures")


  /**
    * The restart attribute specifies whether or not an animation can restart.
    * It can be set to always, never, or whenNotActive, with always being the default.
    *
    * Value: always | never | whenNotActive
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/restart restart @ MDN]]
    */
  lazy val restart: SvgAttr[String] = stringSvgAttr("restart")


  /**
    * The result attribute defines the assigned name for this filter primitive. If supplied, then
    * graphics that result from processing this filter primitive can be referenced by an in attribute
    * on a subsequent filter primitive within the same <filter> element.
    *
    * Value: <filter-primitive-reference>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/result result @ MDN]]
    */
  lazy val resultAttr: SvgAttr[String] = stringSvgAttr("result")


  /**
    * The rx attribute defines the x-radius of a rounded corner for the element.
    * For <rect> elements, this is used for rounded corners. For <ellipse> elements, this defines
    * the x-radius of the ellipse. If the attribute is not specified, the effect is as if a value
    * of 0 were specified.
    *
    * Value: <length> | <percentage> | auto
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/rx rx @ MDN]]
    */
  lazy val rx: SvgAttr[String] = stringSvgAttr("rx")


  /**
    * The ry attribute defines the y-radius of a rounded corner for the element.
    * For <rect> elements, this is used for rounded corners. For <ellipse> elements, this defines
    * the y-radius of the ellipse. If the attribute is not specified, the effect is as if a value
    * of 0 were specified.
    *
    * Value: <length> | <percentage> | auto
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/ry ry @ MDN]]
    */
  lazy val ry: SvgAttr[String] = stringSvgAttr("ry")


  /**
    * The scale attribute defines the displacement scale factor to be used on a <feDisplacementMap>
    * filter primitive. The amount is expressed in the coordinate system established by the
    * primitiveUnits attribute on the <filter> element.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/scale scale @ MDN]]
    */
  lazy val scale: SvgAttr[String] = stringSvgAttr("scale")


  /**
    * The seed attribute represents the starting number for the pseudo random number generator of the
    * <feTurbulence> primitive. If the attribute is not specified, the effect is as if a value of 0
    * were specified.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/seed seed @ MDN]]
    */
  lazy val seed: SvgAttr[Double] = doubleSvgAttr("seed")


  /**
    * The shape-rendering attribute provides hints to the renderer about what tradeoffs to make when
    * rendering shapes like paths, circles, or rectangles.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: auto | optimizeSpeed | crispEdges | geometricPrecision
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/shape-rendering shape-rendering @ MDN]]
    */
  lazy val shapeRendering: SvgAttr[String] = stringSvgAttr("shape-rendering")


  /**
    * The specularConstant attribute controls the ratio of reflection of the specular lighting.
    * It represents the ks value in the Phong lighting model. The bigger the value, the more
    * light is reflected. If not specified, the effect is as if a value of 1 were specified.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/specularConstant specularConstant @ MDN]]
    */
  lazy val specularConstant: SvgAttr[Double] = doubleSvgAttr("specularConstant")


  /**
    * The specularExponent attribute controls the focus for the light source. The larger the value,
    * the more focused the light source becomes. It represents the shininess of the surface and
    * corresponds to the n value in the Phong lighting model.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/specularExponent specularExponent @ MDN]]
    */
  lazy val specularExponent: SvgAttr[Double] = doubleSvgAttr("specularExponent")


  /**
    * The spreadMethod attribute determines how a gradient behaves if it starts or ends inside the
    * bounds of the target rectangle but the gradient does not fill the entire rectangle.
    * If not specified, the effect is as if a value of pad were specified.
    *
    * Value: pad | reflect | repeat
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/spreadMethod spreadMethod @ MDN]]
    */
  lazy val spreadMethod: SvgAttr[String] = stringSvgAttr("spreadMethod")


  /**
    * The stdDeviation attribute defines the standard deviation for the blur operation in a Gaussian
    * blur filter. If two <number>s are provided, the first number represents the standard deviation
    * along the x-axis. The second value represents the standard deviation along the y-axis.
    *
    * Value: <number-optional-number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stdDeviation stdDeviation @ MDN]]
    */
  lazy val stdDeviation: SvgAttr[String] = stringSvgAttr("stdDeviation")


  /**
    * The stitchTiles attribute defines how the Perlin Noise tiles behave at the border.
    * If not specified, the effect is as if a value of noStitch were specified.
    *
    * Value: stitch | noStitch
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stitchTiles stitchTiles @ MDN]]
    */
  lazy val stitchTiles: SvgAttr[String] = stringSvgAttr("stitchTiles")


  /**
    * The stop-color attribute indicates what color to use at a gradient stop.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: currentColor | <color> | <icccolor>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stop-color stop-color @ MDN]]
    */
  lazy val stopColor: SvgAttr[String] = stringSvgAttr("stop-color")


  /**
    * The stop-opacity attribute defines the opacity of a given color gradient stop.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: <opacity-value>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stop-opacity stop-opacity @ MDN]]
    */
  lazy val stopOpacity: SvgAttr[String] = stringSvgAttr("stop-opacity")


  /**
    * The stroke attribute defines the color of the outline on a given graphical element.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: <paint>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke stroke @ MDN]]
    */
  lazy val stroke: SvgAttr[String] = stringSvgAttr("stroke")


  /**
    * The stroke-dasharray attribute defines the pattern of dashes and gaps used to paint the outline
    * of the shape. It's a list of comma and/or white space separated <length>s and <percentage>s.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: none | <dasharray>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-dasharray stroke-dasharray @ MDN]]
    */
  lazy val strokeDashArray: SvgAttr[String] = stringSvgAttr("stroke-dasharray")


  /**
    * The stroke-dashoffset attribute specifies the distance into the dash pattern to start the dash.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: <percentage> | <length>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-dashoffset stroke-dashoffset @ MDN]]
    */
  lazy val strokeDashOffset: SvgAttr[String] = stringSvgAttr("stroke-dashoffset")


  /**
    * The stroke-linecap attribute specifies the shape to be used at the end of open subpaths
    * when they are stroked.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: butt | round | square
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-linecap stroke-linecap @ MDN]]
    */
  lazy val strokeLineCap: SvgAttr[String] = stringSvgAttr("stroke-linecap")


  /**
    * The stroke-linejoin attribute specifies the shape to be used at the corners of paths
    * when they are stroked.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: arcs | bevel | miter | miter-clip | round
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-linejoin stroke-linejoin @ MDN]]
    */
  lazy val strokeLineJoin: SvgAttr[String] = stringSvgAttr("stroke-linejoin")


  /**
    * The stroke-miterlimit attribute specifies the limit on the ratio of the miter length to the
    * stroke-width when two line segments meet at a sharp angle and miter joins have been specified.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-miterlimit stroke-miterlimit @ MDN]]
    */
  lazy val strokeMiterLimit: SvgAttr[String] = stringSvgAttr("stroke-miterlimit")


  /**
    * The stroke-opacity attribute specifies the opacity of the outline on the current object.
    * Its value ranges from 0 to 1 or as a percentage.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: [0-1] | <percentage>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-opacity stroke-opacity @ MDN]]
    */
  lazy val strokeOpacity: SvgAttr[String] = stringSvgAttr("stroke-opacity")


  /**
    * The stroke-width attribute specifies the width of the outline on the current object.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: <length> | <percentage>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-width stroke-width @ MDN]]
    */
  lazy val strokeWidth: SvgAttr[String] = stringSvgAttr("stroke-width")


  /**
    * The style attribute allows to style an element using CSS declarations. It functions identically
    * to the style attribute in HTML.
    *
    * Value: <style>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/style style @ MDN]]
    */
  lazy val style: SvgAttr[String] = stringSvgAttr("style")


  /**
    * The surfaceScale attribute represents the height of the surface for a light filter primitive.
    * If the attribute is not specified, then the effect is as if a value of 1 were specified.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/surfaceScale surfaceScale @ MDN]]
    */
  lazy val surfaceScale: SvgAttr[String] = stringSvgAttr("surfaceScale")


  /**
    * The tabindex attribute allows you to control whether an element is focusable and to define
    * the relative order of the element for the purposes of sequential focus navigation.
    *
    * Value: <integer>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/tabindex tabindex @ MDN]]
    */
  lazy val tabIndex: SvgAttr[String] = stringSvgAttr("tabindex")


  /**
    * The target attribute specifies where to open the link referenced by the <a> element.
    * It's similar to the target attribute in HTML.
    *
    * Value: _self | _parent | _top | _blank | <name>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/target target @ MDN]]
    */
  lazy val target: SvgAttr[String] = stringSvgAttr("target")


  /**
    * The targetX attribute determines the positioning in X of the convolution matrix relative
    * to a given target pixel in the input image. The leftmost column of the matrix is column number
    * zero. The value must be such that: 0 <= targetX < orderX.
    *
    * Value: <integer>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/targetX targetX @ MDN]]
    */
  lazy val targetX: SvgAttr[String] = stringSvgAttr("targetX")


  /**
    * The targetY attribute determines the positioning in Y of the convolution matrix relative
    * to a given target pixel in the input image. The topmost row of the matrix is row number zero.
    * The value must be such that: 0 <= targetY < orderY.
    *
    * Value: <integer>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/targetY targetY @ MDN]]
    */
  lazy val targetY: SvgAttr[String] = stringSvgAttr("targetY")


  /**
    * The text-anchor attribute is used to align (start-, middle- or end-alignment) a string of
    * pre-formatted text or auto-wrapped text where the wrapping area is determined from the
    * inline-size property relative to a given point.
    *
    * Value: start | middle | end
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/text-anchor text-anchor @ MDN]]
    */
  lazy val textAnchor: SvgAttr[String] = stringSvgAttr("text-anchor")


  /**
    * The text-decoration attribute defines whether text is decorated with an underline, overline
    * and/or strike-through. It is a shorthand for the text-decoration-line and
    * text-decoration-style properties.
    *
    * Value: none | [ underline || overline || line-through || blink ] | inherit
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/text-decoration text-decoration @ MDN]]
    */
  lazy val textDecoration: SvgAttr[String] = stringSvgAttr("text-decoration")


  /**
    * The text-rendering attribute provides hints to the renderer about what tradeoffs to make
    * when rendering text.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: auto | optimizeSpeed | optimizeLegibility | geometricPrecision
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/text-rendering text-rendering @ MDN]]
    */
  lazy val textRendering: SvgAttr[String] = stringSvgAttr("text-rendering")


  /**
    * The to attribute indicates the final value of the attribute that will be modified during the
    * animation. The value of the attribute will change between the from attribute value and this
    * value.
    *
    * Value: <value>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/to to @ MDN]]
    */
  lazy val to: SvgAttr[String] = stringSvgAttr("to")


  /**
    * The transform attribute defines a list of transform definitions that are applied to an element
    * and the element's children. Transforms include rotate, scale, translate, skewX, skewY, and matrix.
    *
    * Value: <transform-list>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/transform transform @ MDN]]
    */
  lazy val transform: SvgAttr[String] = stringSvgAttr("transform")


  /**
    * The type attribute specifies the type of animation or filter. The meaning and allowed values
    * depend on the element it's used on. For <animateTransform>, it defines the type of transformation.
    * For <feColorMatrix>, it defines the type of matrix operation.
    *
    * Value: varies by element
    *
    * Aliases: [[typ]], [[tpe]]
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/type type @ MDN]]
    */
  lazy val `type`: SvgAttr[String] = stringSvgAttr("type")


  lazy val typ: SvgAttr[String] = `type`


  lazy val tpe: SvgAttr[String] = `type`


  /**
    * The values attribute has different meanings depending on the context where it's used.
    * For animation elements, it defines a list of values to animate through. For <feColorMatrix>,
    * it defines a list of values for the color transformation matrix.
    *
    * Value: <list-of-values>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/values values @ MDN]]
    */
  lazy val values: SvgAttr[String] = stringSvgAttr("values")


  /**
    * The viewBox attribute defines the position and dimension, in user space, of an SVG viewport.
    * It's specified as four numbers: min-x, min-y, width and height, separated by whitespace
    * and/or a comma.
    *
    * Value: <min-x> <min-y> <width> <height>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/viewBox viewBox @ MDN]]
    */
  lazy val viewBox: SvgAttr[String] = stringSvgAttr("viewBox")


  /**
    * The visibility attribute lets you control the visibility of graphical elements.
    * With a value of hidden or collapse, the element is not drawn. With a value of visible,
    * the element is drawn.
    *
    * Value: visible | hidden | collapse | inherit
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/visibility visibility @ MDN]]
    */
  lazy val visibility: SvgAttr[String] = stringSvgAttr("visibility")


  /**
    * The width attribute defines the horizontal length of an element in the user coordinate system.
    * For SVG elements, it determines the width of the viewport or viewBox.
    *
    * Value: <length> | <percentage> | auto
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/width width @ MDN]]
    */
  lazy val width: SvgAttr[String] = stringSvgAttr("width")


  /**
    * The word-spacing attribute specifies spacing behavior between words.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: normal | <length>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/word-spacing word-spacing @ MDN]]
    */
  lazy val wordSpacing: SvgAttr[String] = stringSvgAttr("word-spacing")


  /**
    * The writing-mode attribute specifies whether the initial inline-progression-direction for a
    * text element is left-to-right, right-to-left, or top-to-bottom.
    * As a presentation attribute, it can also be used as a property directly inside a CSS stylesheet.
    *
    * Value: lr-tb | rl-tb | tb-rl | lr | rl | tb | horizontal-tb | vertical-rl | vertical-lr
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/writing-mode writing-mode @ MDN]]
    */
  lazy val writingMode: SvgAttr[String] = stringSvgAttr("writing-mode")


  /**
    * The x attribute defines an x-axis coordinate in the user coordinate system.
    * The exact effect of this coordinate depends on the element it's used on.
    *
    * Value: <coordinate>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/x x @ MDN]]
    */
  lazy val x: SvgAttr[String] = stringSvgAttr("x")


  /**
    * The x1 attribute is used to specify the first x-coordinate for drawing an SVG element that
    * requires more than one coordinate. Elements that use x1 include <line>, <linearGradient>,
    * and other gradient elements.
    *
    * Value: <coordinate>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/x1 x1 @ MDN]]
    */
  lazy val x1: SvgAttr[String] = stringSvgAttr("x1")


  /**
    * The x2 attribute is used to specify the second x-coordinate for drawing an SVG element that
    * requires more than one coordinate. Elements that use x2 include <line>, <linearGradient>,
    * and other gradient elements.
    *
    * Value: <coordinate>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/x2 x2 @ MDN]]
    */
  lazy val x2: SvgAttr[String] = stringSvgAttr("x2")


  /**
    * The xChannelSelector attribute indicates which color channel from the input image to use
    * to displace the pixels in the <feDisplacementMap> filter primitive.
    * If not specified, the effect is as if a value of A were specified.
    *
    * Value: R | G | B | A
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xChannelSelector xChannelSelector @ MDN]]
    */
  lazy val xChannelSelector: SvgAttr[String] = stringSvgAttr("xChannelSelector")


  /**
    * The xlink:href attribute defines a reference to a resource as a reference IRI. The exact
    * meaning of that link depends on the context of each element using it.
    * Note: This attribute is deprecated in SVG 2 in favor of simply using href.
    *
    * Value: <IRI>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xlink:href xlink:href @ MDN]]
    */
  lazy val xlinkHref: SvgAttr[String] = stringSvgAttr("href", namespace = "xlink")


  /**
    * The xlink:role attribute indicates the role that the linked resource plays in the link
    * relationship. The exact meaning of this attribute is defined by the language of the linked
    * resource.
    * Note: This attribute is deprecated in SVG 2.
    *
    * Value: <IRI>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xlink:role xlink:role @ MDN]]
    */
  lazy val xlinkRole: SvgAttr[String] = stringSvgAttr("role", namespace = "xlink")


  /**
    * The xlink:title attribute provides a human-readable title for the link, which can be used
    * by browsers or other user agents to provide additional information about the link.
    * Note: This attribute is deprecated in SVG 2.
    *
    * Value: <string>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xlink:title xlink:title @ MDN]]
    */
  lazy val xlinkTitle: SvgAttr[String] = stringSvgAttr("title", namespace = "xlink")


  /**
    * The xml:space attribute specifies how white space inside the element is handled.
    * Note: This attribute is deprecated in SVG 2. Use CSS white-space property instead.
    *
    * Value: default | preserve
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xml:space xml:space @ MDN]]
    */
  lazy val xmlSpace: SvgAttr[String] = stringSvgAttr("space", namespace = "xml")


  /**
    * The xmlns attribute specifies the XML namespace for the SVG document.
    * For SVG, the namespace is always http://www.w3.org/2000/svg.
    *
    * Value: http://www.w3.org/2000/svg
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/svg svg @ MDN]]
    */
  lazy val xmlns: SvgAttr[String] = stringSvgAttr("xmlns")


  /**
    * The xmlns:xlink attribute specifies the XML namespace for the XLink attributes used by SVG.
    * Note: This attribute is deprecated in SVG 2 as XLink features are now available in core XML.
    *
    * Value: http://www.w3.org/1999/xlink
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xmlns:xlink xmlns:xlink @ MDN]]
    */
  lazy val xmlnsXlink: SvgAttr[String] = stringSvgAttr("xlink", namespace = "xmlns")


  /**
    * The y attribute defines a y-axis coordinate in the user coordinate system.
    * The exact effect of this coordinate depends on the element it's used on.
    *
    * Value: <coordinate>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/y y @ MDN]]
    */
  lazy val y: SvgAttr[String] = stringSvgAttr("y")


  /**
    * The y1 attribute is used to specify the first y-coordinate for drawing an SVG element that
    * requires more than one coordinate. Elements that use y1 include <line>, <linearGradient>,
    * and other gradient elements.
    *
    * Value: <coordinate>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/y1 y1 @ MDN]]
    */
  lazy val y1: SvgAttr[String] = stringSvgAttr("y1")


  /**
    * The y2 attribute is used to specify the second y-coordinate for drawing an SVG element that
    * requires more than one coordinate. Elements that use y2 include <line>, <linearGradient>,
    * and other gradient elements.
    *
    * Value: <coordinate>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/y2 y2 @ MDN]]
    */
  lazy val y2: SvgAttr[String] = stringSvgAttr("y2")


  /**
    * The yChannelSelector attribute indicates which color channel from the input image to use
    * to displace the pixels in the <feDisplacementMap> filter primitive.
    * If not specified, the effect is as if a value of A were specified.
    *
    * Value: R | G | B | A
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/yChannelSelector yChannelSelector @ MDN]]
    */
  lazy val yChannelSelector: SvgAttr[String] = stringSvgAttr("yChannelSelector")


  /**
    * The z attribute defines the location along the Z-axis for a light source in the coordinate
    * system established by the primitiveUnits attribute on the <filter> element.
    * If the attribute is not specified, the effect is as if a value of 0 were specified.
    *
    * Value: <number>
    *
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/z z @ MDN]]
    */
  lazy val z: SvgAttr[String] = stringSvgAttr("z")


}

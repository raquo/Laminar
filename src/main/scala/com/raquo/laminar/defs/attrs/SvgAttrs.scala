package com.raquo.laminar.defs.attrs

import com.raquo.laminar.keys.SvgAttr
import com.raquo.laminar.codecs._

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


  @inline protected def doubleSvgAttr(name: String): SvgAttr[Double] = svgAttr(name, DoubleAsStringCodec, namespace = None)

  @inline protected def intSvgAttr(name: String): SvgAttr[Int] = svgAttr(name, IntAsStringCodec, namespace = None)

  @inline protected def stringSvgAttr(name: String): SvgAttr[String] = svgAttr(name, StringAsIsCodec, namespace = None)

  @inline protected def stringSvgAttr(name: String, namespace: String): SvgAttr[String] = svgAttr(name, StringAsIsCodec, Some(namespace))



  /**
    * This attribute defines the distance from the origin to the top of accent characters,
    * measured by a distance within the font coordinate system.
    * If the attribute is not specified, the effect is as if the attribute
    * were set to the value of the ascent attribute.
    * 
    * Value 	`<number>`
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/accent-height
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/accumulate
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/additive
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/alignment-baseline
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/ascent
    */
  lazy val ascent: SvgAttr[Double] = doubleSvgAttr("ascent")


  /**
    * This attribute indicates the name of the attribute in the parent element
    * that is going to be changed during an animation.
    * 
    * Value 	`<attributeName>`
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/attributeName
    */
  lazy val attributeName: SvgAttr[String] = stringSvgAttr("attributeName")


  /**
    * This attribute specifies the namespace in which the target attribute
    * and its associated values are defined.
    * 
    * Value 	CSS | XML | auto
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/attributeType
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/azimuth
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/baseFrequency
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/baseline-shift
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/begin
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/bias
    */
  lazy val bias: SvgAttr[Double] = doubleSvgAttr("bias")


  /**
    * This attribute specifies the interpolation mode for the animation. The default
    * mode is linear, however if the attribute does not support linear interpolation
    * (e.g. for strings), the calcMode attribute is ignored and discrete interpolation is used.
    * 
    * Value 	discrete | linear | paced | spline
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/calcMode
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/clip
    */
  lazy val clip: SvgAttr[String] = stringSvgAttr("clip")


  /**
    * The clip-path attribute bind the element is applied to with a given `<clipPath>` element
    * As a presentation attribute, it also can be used as a property directly inside a CSS stylesheet
    * 
    * Value 	`<FuncIRI> | none | inherit`
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/clip-path
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/clipPathUnits
    */
  lazy val clipPathUnits: SvgAttr[String] = stringSvgAttr("clipPathUnits")


  /**
    * The clip-rule attribute only applies to graphics elements that are contained within a
    * `<clipPath>` element. The clip-rule attribute basically works as the fill-rule attribute,
    * except that it applies to `<clipPath>` definitions.
    * 
    * Value 	nonezero | evenodd | inherit
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/clip-rule
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/color
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/color-interpolation
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/color-interpolation-filters
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/color-profile
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/color-rendering
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/contentScriptType
    */
  lazy val contentScriptType: SvgAttr[String] = stringSvgAttr("contentScriptType")


  /**
    * This attribute specifies the style sheet language for the given document fragment.
    * The contentStyleType is specified on the `<svg>` element. By default, if it's not defined,
    * the value is text/css
    * 
    * Value 	`<content-type>`
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/contentStyleType
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/cursor
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/cx
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
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/cy
    */
  lazy val cy: SvgAttr[String] = stringSvgAttr("cy")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/d */
  lazy val d: SvgAttr[String] = stringSvgAttr("d")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/diffuseConstant */
  lazy val diffuseConstant: SvgAttr[String] = stringSvgAttr("diffuseConstant")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/direction */
  lazy val direction: SvgAttr[String] = stringSvgAttr("direction")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/display */
  lazy val display: SvgAttr[String] = stringSvgAttr("display")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/divisor */
  lazy val divisor: SvgAttr[String] = stringSvgAttr("divisor")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/dominant-baseline */
  lazy val dominantBaseline: SvgAttr[String] = stringSvgAttr("dominant-baseline")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/dur */
  lazy val dur: SvgAttr[String] = stringSvgAttr("dur")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/dx */
  lazy val dx: SvgAttr[String] = stringSvgAttr("dx")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/dy */
  lazy val dy: SvgAttr[String] = stringSvgAttr("dy")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/edgeMode */
  lazy val edgeMode: SvgAttr[String] = stringSvgAttr("edgeMode")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/elevation */
  lazy val elevation: SvgAttr[Double] = doubleSvgAttr("elevation")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/end */
  lazy val end: SvgAttr[String] = stringSvgAttr("end")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/externalResourcesRequired */
  lazy val externalResourcesRequired: SvgAttr[String] = stringSvgAttr("externalResourcesRequired")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill */
  lazy val fill: SvgAttr[String] = stringSvgAttr("fill")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill-opacity */
  lazy val fillOpacity: SvgAttr[String] = stringSvgAttr("fill-opacity")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill-rule */
  lazy val fillRule: SvgAttr[String] = stringSvgAttr("fill-rule")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/filter */
  lazy val filterAttr: SvgAttr[String] = stringSvgAttr("filter")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/filterRes */
  lazy val filterRes: SvgAttr[String] = stringSvgAttr("filterRes")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/filterUnits */
  lazy val filterUnits: SvgAttr[String] = stringSvgAttr("filterUnits")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/flood-color */
  lazy val floodColor: SvgAttr[String] = stringSvgAttr("flood-color")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/flood-opacity */
  lazy val floodOpacity: SvgAttr[String] = stringSvgAttr("flood-opacity")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-family */
  lazy val fontFamily: SvgAttr[String] = stringSvgAttr("font-family")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-size */
  lazy val fontSize: SvgAttr[String] = stringSvgAttr("font-size")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-size-adjust */
  lazy val fontSizeAdjust: SvgAttr[String] = stringSvgAttr("font-size-adjust")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-stretch */
  lazy val fontStretch: SvgAttr[String] = stringSvgAttr("font-stretch")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-variant */
  lazy val fontVariant: SvgAttr[String] = stringSvgAttr("font-variant")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-weight */
  lazy val fontWeight: SvgAttr[String] = stringSvgAttr("font-weight")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/from */
  lazy val from: SvgAttr[String] = stringSvgAttr("from")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/gradientTransform */
  lazy val gradientTransform: SvgAttr[String] = stringSvgAttr("gradientTransform")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/gradientUnits */
  lazy val gradientUnits: SvgAttr[String] = stringSvgAttr("gradientUnits")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/height */
  lazy val height: SvgAttr[String] = stringSvgAttr("height")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/href */
  lazy val href: SvgAttr[String] = stringSvgAttr("href")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/imageRendering */
  lazy val imageRendering: SvgAttr[String] = stringSvgAttr("imageRendering")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/id */
  lazy val idAttr: SvgAttr[String] = stringSvgAttr("id")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/in */
  lazy val in: SvgAttr[String] = stringSvgAttr("in")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/in2 */
  lazy val in2: SvgAttr[String] = stringSvgAttr("in2")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/k1 */
  lazy val k1: SvgAttr[Double] = doubleSvgAttr("k1")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/k2 */
  lazy val k2: SvgAttr[Double] = doubleSvgAttr("k2")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/k3 */
  lazy val k3: SvgAttr[Double] = doubleSvgAttr("k3")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/k4 */
  lazy val k4: SvgAttr[Double] = doubleSvgAttr("k4")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/kernelMatrix */
  lazy val kernelMatrix: SvgAttr[String] = stringSvgAttr("kernelMatrix")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/kernelUnitLength */
  lazy val kernelUnitLength: SvgAttr[String] = stringSvgAttr("kernelUnitLength")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/kerning */
  lazy val kerning: SvgAttr[String] = stringSvgAttr("kerning")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/keySplines */
  lazy val keySplines: SvgAttr[String] = stringSvgAttr("keySplines")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/keyTimes */
  lazy val keyTimes: SvgAttr[String] = stringSvgAttr("keyTimes")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/letter-spacing */
  lazy val letterSpacing: SvgAttr[String] = stringSvgAttr("letter-spacing")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/lighting-color */
  lazy val lightingColor: SvgAttr[String] = stringSvgAttr("lighting-color")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/limitingConeAngle */
  lazy val limitingConeAngle: SvgAttr[String] = stringSvgAttr("limitingConeAngle")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/local */
  lazy val local: SvgAttr[String] = stringSvgAttr("local")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/marker-end */
  lazy val markerEnd: SvgAttr[String] = stringSvgAttr("marker-end")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/marker-mid */
  lazy val markerMid: SvgAttr[String] = stringSvgAttr("marker-mid")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/marker-start */
  lazy val markerStart: SvgAttr[String] = stringSvgAttr("marker-start")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/markerHeight */
  lazy val markerHeight: SvgAttr[String] = stringSvgAttr("markerHeight")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/markerUnits */
  lazy val markerUnits: SvgAttr[String] = stringSvgAttr("markerUnits")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/markerWidth */
  lazy val markerWidth: SvgAttr[String] = stringSvgAttr("markerWidth")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/maskContentUnits */
  lazy val maskContentUnits: SvgAttr[String] = stringSvgAttr("maskContentUnits")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/maskUnits */
  lazy val maskUnits: SvgAttr[String] = stringSvgAttr("maskUnits")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/mask */
  lazy val maskAttr: SvgAttr[String] = stringSvgAttr("mask")


  /** @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/max */
  lazy val maxAttr: SvgAttr[String] = stringSvgAttr("max")


  /** @see https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/min */
  lazy val minAttr: SvgAttr[String] = stringSvgAttr("min")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/mode */
  lazy val mode: SvgAttr[String] = stringSvgAttr("mode")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/numOctaves */
  lazy val numOctaves: SvgAttr[Int] = intSvgAttr("numOctaves")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/offset */
  lazy val offsetAttr: SvgAttr[String] = stringSvgAttr("offset")


  /**
    * This attribute defines the orientation of the marker relative to the shape it is attached to.
    * 
    * Value type: `auto|auto-start-reverse|<angle>` ; Default value: 0; Animatable: yes
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/orient
    */
  lazy val orient: SvgAttr[String] = stringSvgAttr("orient")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/opacity */
  lazy val opacity: SvgAttr[String] = stringSvgAttr("opacity")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/operator */
  lazy val operator: SvgAttr[String] = stringSvgAttr("operator")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/order */
  lazy val order: SvgAttr[String] = stringSvgAttr("order")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/overflow */
  lazy val overflow: SvgAttr[String] = stringSvgAttr("overflow")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/paint-order */
  lazy val paintOrder: SvgAttr[String] = stringSvgAttr("paint-order")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pathLength */
  lazy val pathLength: SvgAttr[String] = stringSvgAttr("pathLength")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/patternContentUnits */
  lazy val patternContentUnits: SvgAttr[String] = stringSvgAttr("patternContentUnits")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/patternTransform */
  lazy val patternTransform: SvgAttr[String] = stringSvgAttr("patternTransform")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/patternUnits */
  lazy val patternUnits: SvgAttr[String] = stringSvgAttr("patternUnits")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pointer-events */
  lazy val pointerEvents: SvgAttr[String] = stringSvgAttr("pointer-events")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/points */
  lazy val points: SvgAttr[String] = stringSvgAttr("points")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pointsAtX */
  lazy val pointsAtX: SvgAttr[String] = stringSvgAttr("pointsAtX")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pointsAtY */
  lazy val pointsAtY: SvgAttr[String] = stringSvgAttr("pointsAtY")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pointsAtZ */
  lazy val pointsAtZ: SvgAttr[String] = stringSvgAttr("pointsAtZ")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/preserveAlpha */
  lazy val preserveAlpha: SvgAttr[String] = stringSvgAttr("preserveAlpha")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/preserveAspectRatio */
  lazy val preserveAspectRatio: SvgAttr[String] = stringSvgAttr("preserveAspectRatio")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/primitiveUnits */
  lazy val primitiveUnits: SvgAttr[String] = stringSvgAttr("primitiveUnits")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/r */
  lazy val r: SvgAttr[String] = stringSvgAttr("r")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/radius */
  lazy val radius: SvgAttr[String] = stringSvgAttr("radius")


  /**
    * The refX attribute is used alongside the refY attribute to provide coordinates for the location on the
    * marker where it will be joined to its markable element. Coordinates are relative to the marker's
    * coordinate system (after application of the ‘viewBox’ and ‘preserveAspectRatio’ attributes), and not
    * the markable element it is placed on.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/refX
    */
  lazy val refX: SvgAttr[String] = stringSvgAttr("refX")


  /**
    * The refY attribute is used alongside the refY attribute to provide coordinates for the location on the
    * marker where it will be joined to its markable element. Coordinates are relative to the marker's
    * coordinate system (after application of the ‘viewBox’ and ‘preserveAspectRatio’ attributes), and not
    * the markable element it is placed on.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/refY
    */
  lazy val refY: SvgAttr[String] = stringSvgAttr("refY")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/repeatCount */
  lazy val repeatCount: SvgAttr[String] = stringSvgAttr("repeatCount")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/repeatDur */
  lazy val repeatDur: SvgAttr[String] = stringSvgAttr("repeatDur")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/requiredFeatures */
  lazy val requiredFeatures: SvgAttr[String] = stringSvgAttr("requiredFeatures")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/restart */
  lazy val restart: SvgAttr[String] = stringSvgAttr("restart")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/result */
  lazy val resultAttr: SvgAttr[String] = stringSvgAttr("result")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/rx */
  lazy val rx: SvgAttr[String] = stringSvgAttr("rx")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/ry */
  lazy val ry: SvgAttr[String] = stringSvgAttr("ry")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/scale */
  lazy val scale: SvgAttr[String] = stringSvgAttr("scale")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/seed */
  lazy val seed: SvgAttr[Double] = doubleSvgAttr("seed")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/shape-rendering */
  lazy val shapeRendering: SvgAttr[String] = stringSvgAttr("shape-rendering")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/specularConstant */
  lazy val specularConstant: SvgAttr[Double] = doubleSvgAttr("specularConstant")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/specularExponent */
  lazy val specularExponent: SvgAttr[Double] = doubleSvgAttr("specularExponent")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/spreadMethod */
  lazy val spreadMethod: SvgAttr[String] = stringSvgAttr("spreadMethod")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stdDeviation */
  lazy val stdDeviation: SvgAttr[String] = stringSvgAttr("stdDeviation")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stitchTiles */
  lazy val stitchTiles: SvgAttr[String] = stringSvgAttr("stitchTiles")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stop-color */
  lazy val stopColor: SvgAttr[String] = stringSvgAttr("stop-color")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stop-opacity */
  lazy val stopOpacity: SvgAttr[String] = stringSvgAttr("stop-opacity")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke */
  lazy val stroke: SvgAttr[String] = stringSvgAttr("stroke")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-dasharray */
  lazy val strokeDashArray: SvgAttr[String] = stringSvgAttr("stroke-dasharray")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-dashoffset */
  lazy val strokeDashOffset: SvgAttr[String] = stringSvgAttr("stroke-dashoffset")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-linecap */
  lazy val strokeLineCap: SvgAttr[String] = stringSvgAttr("stroke-linecap")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-linejoin */
  lazy val strokeLineJoin: SvgAttr[String] = stringSvgAttr("stroke-linejoin")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-miterlimit */
  lazy val strokeMiterLimit: SvgAttr[String] = stringSvgAttr("stroke-miterlimit")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-opacity */
  lazy val strokeOpacity: SvgAttr[String] = stringSvgAttr("stroke-opacity")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-width */
  lazy val strokeWidth: SvgAttr[String] = stringSvgAttr("stroke-width")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/style */
  lazy val style: SvgAttr[String] = stringSvgAttr("style")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/surfaceScale */
  lazy val surfaceScale: SvgAttr[String] = stringSvgAttr("surfaceScale")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/target */
  lazy val target: SvgAttr[String] = stringSvgAttr("target")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/targetX */
  lazy val targetX: SvgAttr[String] = stringSvgAttr("targetX")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/targetY */
  lazy val targetY: SvgAttr[String] = stringSvgAttr("targetY")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/text-anchor */
  lazy val textAnchor: SvgAttr[String] = stringSvgAttr("text-anchor")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/text-decoration */
  lazy val textDecoration: SvgAttr[String] = stringSvgAttr("text-decoration")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/text-rendering */
  lazy val textRendering: SvgAttr[String] = stringSvgAttr("text-rendering")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/to */
  lazy val to: SvgAttr[String] = stringSvgAttr("to")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/transform */
  lazy val transform: SvgAttr[String] = stringSvgAttr("transform")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/type */
  lazy val `type`: SvgAttr[String] = stringSvgAttr("type")


  lazy val typ: SvgAttr[String] = `type`


  lazy val tpe: SvgAttr[String] = `type`


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/values */
  lazy val values: SvgAttr[String] = stringSvgAttr("values")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/viewBox */
  lazy val viewBox: SvgAttr[String] = stringSvgAttr("viewBox")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/visibility */
  lazy val visibility: SvgAttr[String] = stringSvgAttr("visibility")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/width */
  lazy val width: SvgAttr[String] = stringSvgAttr("width")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/word-spacing */
  lazy val wordSpacing: SvgAttr[String] = stringSvgAttr("word-spacing")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/writing-mode */
  lazy val writingMode: SvgAttr[String] = stringSvgAttr("writing-mode")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/x */
  lazy val x: SvgAttr[String] = stringSvgAttr("x")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/x1 */
  lazy val x1: SvgAttr[String] = stringSvgAttr("x1")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/x2 */
  lazy val x2: SvgAttr[String] = stringSvgAttr("x2")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xChannelSelector */
  lazy val xChannelSelector: SvgAttr[String] = stringSvgAttr("xChannelSelector")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xlink:href */
  lazy val xlinkHref: SvgAttr[String] = stringSvgAttr("href", namespace = "xlink")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xlink:role */
  lazy val xlinkRole: SvgAttr[String] = stringSvgAttr("role", namespace = "xlink")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xlink:title */
  lazy val xlinkTitle: SvgAttr[String] = stringSvgAttr("title", namespace = "xlink")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xml:space */
  lazy val xmlSpace: SvgAttr[String] = stringSvgAttr("space", namespace = "xml")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xmlns */
  lazy val xmlns: SvgAttr[String] = stringSvgAttr("xmlns", namespace = "xmlns")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xmlns:xlink */
  lazy val xmlnsXlink: SvgAttr[String] = stringSvgAttr("xlink", namespace = "xmlns")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/y */
  lazy val y: SvgAttr[String] = stringSvgAttr("y")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/y1 */
  lazy val y1: SvgAttr[String] = stringSvgAttr("y1")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/y2 */
  lazy val y2: SvgAttr[String] = stringSvgAttr("y2")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/yChannelSelector */
  lazy val yChannelSelector: SvgAttr[String] = stringSvgAttr("yChannelSelector")


  /** @see https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/z */
  lazy val z: SvgAttr[String] = stringSvgAttr("z")


}

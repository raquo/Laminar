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


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/d d @ MDN]] */
  lazy val d: SvgAttr[String] = stringSvgAttr("d")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/diffuseConstant diffuseConstant @ MDN]] */
  lazy val diffuseConstant: SvgAttr[String] = stringSvgAttr("diffuseConstant")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/direction direction @ MDN]] */
  lazy val direction: SvgAttr[String] = stringSvgAttr("direction")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/display display @ MDN]] */
  lazy val display: SvgAttr[String] = stringSvgAttr("display")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/divisor divisor @ MDN]] */
  lazy val divisor: SvgAttr[String] = stringSvgAttr("divisor")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/dominant-baseline dominant-baseline @ MDN]] */
  lazy val dominantBaseline: SvgAttr[String] = stringSvgAttr("dominant-baseline")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/dur dur @ MDN]] */
  lazy val dur: SvgAttr[String] = stringSvgAttr("dur")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/dx dx @ MDN]] */
  lazy val dx: SvgAttr[String] = stringSvgAttr("dx")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/dy dy @ MDN]] */
  lazy val dy: SvgAttr[String] = stringSvgAttr("dy")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/edgeMode edgeMode @ MDN]] */
  lazy val edgeMode: SvgAttr[String] = stringSvgAttr("edgeMode")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/elevation elevation @ MDN]] */
  lazy val elevation: SvgAttr[Double] = doubleSvgAttr("elevation")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/end end @ MDN]] */
  lazy val end: SvgAttr[String] = stringSvgAttr("end")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/externalResourcesRequired externalResourcesRequired @ MDN]] */
  lazy val externalResourcesRequired: SvgAttr[String] = stringSvgAttr("externalResourcesRequired")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill fill @ MDN]] */
  lazy val fill: SvgAttr[String] = stringSvgAttr("fill")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill-opacity fill-opacity @ MDN]] */
  lazy val fillOpacity: SvgAttr[String] = stringSvgAttr("fill-opacity")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill-rule fill-rule @ MDN]] */
  lazy val fillRule: SvgAttr[String] = stringSvgAttr("fill-rule")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/filter filter @ MDN]] */
  lazy val filterAttr: SvgAttr[String] = stringSvgAttr("filter")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/filterRes filterRes @ MDN]] */
  lazy val filterRes: SvgAttr[String] = stringSvgAttr("filterRes")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/filterUnits filterUnits @ MDN]] */
  lazy val filterUnits: SvgAttr[String] = stringSvgAttr("filterUnits")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/flood-color flood-color @ MDN]] */
  lazy val floodColor: SvgAttr[String] = stringSvgAttr("flood-color")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/flood-opacity flood-opacity @ MDN]] */
  lazy val floodOpacity: SvgAttr[String] = stringSvgAttr("flood-opacity")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-family font-family @ MDN]] */
  lazy val fontFamily: SvgAttr[String] = stringSvgAttr("font-family")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-size font-size @ MDN]] */
  lazy val fontSize: SvgAttr[String] = stringSvgAttr("font-size")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-size-adjust font-size-adjust @ MDN]] */
  lazy val fontSizeAdjust: SvgAttr[String] = stringSvgAttr("font-size-adjust")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-stretch font-stretch @ MDN]] */
  lazy val fontStretch: SvgAttr[String] = stringSvgAttr("font-stretch")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-variant font-variant @ MDN]] */
  lazy val fontVariant: SvgAttr[String] = stringSvgAttr("font-variant")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/font-weight font-weight @ MDN]] */
  lazy val fontWeight: SvgAttr[String] = stringSvgAttr("font-weight")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/from from @ MDN]] */
  lazy val from: SvgAttr[String] = stringSvgAttr("from")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/gradientTransform gradientTransform @ MDN]] */
  lazy val gradientTransform: SvgAttr[String] = stringSvgAttr("gradientTransform")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/gradientUnits gradientUnits @ MDN]] */
  lazy val gradientUnits: SvgAttr[String] = stringSvgAttr("gradientUnits")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/height height @ MDN]] */
  lazy val height: SvgAttr[String] = stringSvgAttr("height")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/href href @ MDN]] */
  lazy val href: SvgAttr[String] = stringSvgAttr("href")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/imageRendering imageRendering @ MDN]] */
  lazy val imageRendering: SvgAttr[String] = stringSvgAttr("imageRendering")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/id id @ MDN]] */
  lazy val idAttr: SvgAttr[String] = stringSvgAttr("id")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/in in @ MDN]] */
  lazy val in: SvgAttr[String] = stringSvgAttr("in")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/in2 in2 @ MDN]] */
  lazy val in2: SvgAttr[String] = stringSvgAttr("in2")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/k1 k1 @ MDN]] */
  lazy val k1: SvgAttr[Double] = doubleSvgAttr("k1")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/k2 k2 @ MDN]] */
  lazy val k2: SvgAttr[Double] = doubleSvgAttr("k2")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/k3 k3 @ MDN]] */
  lazy val k3: SvgAttr[Double] = doubleSvgAttr("k3")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/k4 k4 @ MDN]] */
  lazy val k4: SvgAttr[Double] = doubleSvgAttr("k4")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/kernelMatrix kernelMatrix @ MDN]] */
  lazy val kernelMatrix: SvgAttr[String] = stringSvgAttr("kernelMatrix")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/kernelUnitLength kernelUnitLength @ MDN]] */
  lazy val kernelUnitLength: SvgAttr[String] = stringSvgAttr("kernelUnitLength")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/kerning kerning @ MDN]] */
  lazy val kerning: SvgAttr[String] = stringSvgAttr("kerning")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/keySplines keySplines @ MDN]] */
  lazy val keySplines: SvgAttr[String] = stringSvgAttr("keySplines")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/keyTimes keyTimes @ MDN]] */
  lazy val keyTimes: SvgAttr[String] = stringSvgAttr("keyTimes")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/letter-spacing letter-spacing @ MDN]] */
  lazy val letterSpacing: SvgAttr[String] = stringSvgAttr("letter-spacing")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/lighting-color lighting-color @ MDN]] */
  lazy val lightingColor: SvgAttr[String] = stringSvgAttr("lighting-color")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/limitingConeAngle limitingConeAngle @ MDN]] */
  lazy val limitingConeAngle: SvgAttr[String] = stringSvgAttr("limitingConeAngle")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/local local @ MDN]] */
  lazy val local: SvgAttr[String] = stringSvgAttr("local")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/marker-end marker-end @ MDN]] */
  lazy val markerEnd: SvgAttr[String] = stringSvgAttr("marker-end")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/marker-mid marker-mid @ MDN]] */
  lazy val markerMid: SvgAttr[String] = stringSvgAttr("marker-mid")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/marker-start marker-start @ MDN]] */
  lazy val markerStart: SvgAttr[String] = stringSvgAttr("marker-start")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/markerHeight markerHeight @ MDN]] */
  lazy val markerHeight: SvgAttr[String] = stringSvgAttr("markerHeight")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/markerUnits markerUnits @ MDN]] */
  lazy val markerUnits: SvgAttr[String] = stringSvgAttr("markerUnits")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/markerWidth markerWidth @ MDN]] */
  lazy val markerWidth: SvgAttr[String] = stringSvgAttr("markerWidth")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/maskContentUnits maskContentUnits @ MDN]] */
  lazy val maskContentUnits: SvgAttr[String] = stringSvgAttr("maskContentUnits")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/maskUnits maskUnits @ MDN]] */
  lazy val maskUnits: SvgAttr[String] = stringSvgAttr("maskUnits")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/mask mask @ MDN]] */
  lazy val maskAttr: SvgAttr[String] = stringSvgAttr("mask")


  /** [[https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/max max @ MDN]] */
  lazy val maxAttr: SvgAttr[String] = stringSvgAttr("max")


  /** [[https://developer.mozilla.org/en-US/docs/Web/HTML/Attributes/min min @ MDN]] */
  lazy val minAttr: SvgAttr[String] = stringSvgAttr("min")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/mode mode @ MDN]] */
  lazy val mode: SvgAttr[String] = stringSvgAttr("mode")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/numOctaves numOctaves @ MDN]] */
  lazy val numOctaves: SvgAttr[Int] = intSvgAttr("numOctaves")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/offset offset @ MDN]] */
  lazy val offsetAttr: SvgAttr[String] = stringSvgAttr("offset")


  /**
    * This attribute defines the orientation of the marker relative to the shape it is attached to.
    * 
    * Value type: `auto|auto-start-reverse|<angle>` ; Default value: 0; Animatable: yes
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/orient orient @ MDN]]
    */
  lazy val orient: SvgAttr[String] = stringSvgAttr("orient")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/opacity opacity @ MDN]] */
  lazy val opacity: SvgAttr[String] = stringSvgAttr("opacity")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/operator operator @ MDN]] */
  lazy val operator: SvgAttr[String] = stringSvgAttr("operator")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/order order @ MDN]] */
  lazy val order: SvgAttr[String] = stringSvgAttr("order")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/overflow overflow @ MDN]] */
  lazy val overflow: SvgAttr[String] = stringSvgAttr("overflow")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/paint-order paint-order @ MDN]] */
  lazy val paintOrder: SvgAttr[String] = stringSvgAttr("paint-order")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pathLength pathLength @ MDN]] */
  lazy val pathLength: SvgAttr[String] = stringSvgAttr("pathLength")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/patternContentUnits patternContentUnits @ MDN]] */
  lazy val patternContentUnits: SvgAttr[String] = stringSvgAttr("patternContentUnits")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/patternTransform patternTransform @ MDN]] */
  lazy val patternTransform: SvgAttr[String] = stringSvgAttr("patternTransform")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/patternUnits patternUnits @ MDN]] */
  lazy val patternUnits: SvgAttr[String] = stringSvgAttr("patternUnits")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pointer-events pointer-events @ MDN]] */
  lazy val pointerEvents: SvgAttr[String] = stringSvgAttr("pointer-events")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/points points @ MDN]] */
  lazy val points: SvgAttr[String] = stringSvgAttr("points")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pointsAtX pointsAtX @ MDN]] */
  lazy val pointsAtX: SvgAttr[String] = stringSvgAttr("pointsAtX")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pointsAtY pointsAtY @ MDN]] */
  lazy val pointsAtY: SvgAttr[String] = stringSvgAttr("pointsAtY")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/pointsAtZ pointsAtZ @ MDN]] */
  lazy val pointsAtZ: SvgAttr[String] = stringSvgAttr("pointsAtZ")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/preserveAlpha preserveAlpha @ MDN]] */
  lazy val preserveAlpha: SvgAttr[String] = stringSvgAttr("preserveAlpha")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/preserveAspectRatio preserveAspectRatio @ MDN]] */
  lazy val preserveAspectRatio: SvgAttr[String] = stringSvgAttr("preserveAspectRatio")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/primitiveUnits primitiveUnits @ MDN]] */
  lazy val primitiveUnits: SvgAttr[String] = stringSvgAttr("primitiveUnits")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/r r @ MDN]] */
  lazy val r: SvgAttr[String] = stringSvgAttr("r")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/radius radius @ MDN]] */
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


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/repeatCount repeatCount @ MDN]] */
  lazy val repeatCount: SvgAttr[String] = stringSvgAttr("repeatCount")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/repeatDur repeatDur @ MDN]] */
  lazy val repeatDur: SvgAttr[String] = stringSvgAttr("repeatDur")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/requiredFeatures requiredFeatures @ MDN]] */
  lazy val requiredFeatures: SvgAttr[String] = stringSvgAttr("requiredFeatures")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/restart restart @ MDN]] */
  lazy val restart: SvgAttr[String] = stringSvgAttr("restart")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/result result @ MDN]] */
  lazy val resultAttr: SvgAttr[String] = stringSvgAttr("result")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/rx rx @ MDN]] */
  lazy val rx: SvgAttr[String] = stringSvgAttr("rx")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/ry ry @ MDN]] */
  lazy val ry: SvgAttr[String] = stringSvgAttr("ry")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/scale scale @ MDN]] */
  lazy val scale: SvgAttr[String] = stringSvgAttr("scale")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/seed seed @ MDN]] */
  lazy val seed: SvgAttr[Double] = doubleSvgAttr("seed")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/shape-rendering shape-rendering @ MDN]] */
  lazy val shapeRendering: SvgAttr[String] = stringSvgAttr("shape-rendering")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/specularConstant specularConstant @ MDN]] */
  lazy val specularConstant: SvgAttr[Double] = doubleSvgAttr("specularConstant")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/specularExponent specularExponent @ MDN]] */
  lazy val specularExponent: SvgAttr[Double] = doubleSvgAttr("specularExponent")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/spreadMethod spreadMethod @ MDN]] */
  lazy val spreadMethod: SvgAttr[String] = stringSvgAttr("spreadMethod")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stdDeviation stdDeviation @ MDN]] */
  lazy val stdDeviation: SvgAttr[String] = stringSvgAttr("stdDeviation")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stitchTiles stitchTiles @ MDN]] */
  lazy val stitchTiles: SvgAttr[String] = stringSvgAttr("stitchTiles")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stop-color stop-color @ MDN]] */
  lazy val stopColor: SvgAttr[String] = stringSvgAttr("stop-color")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stop-opacity stop-opacity @ MDN]] */
  lazy val stopOpacity: SvgAttr[String] = stringSvgAttr("stop-opacity")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke stroke @ MDN]] */
  lazy val stroke: SvgAttr[String] = stringSvgAttr("stroke")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-dasharray stroke-dasharray @ MDN]] */
  lazy val strokeDashArray: SvgAttr[String] = stringSvgAttr("stroke-dasharray")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-dashoffset stroke-dashoffset @ MDN]] */
  lazy val strokeDashOffset: SvgAttr[String] = stringSvgAttr("stroke-dashoffset")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-linecap stroke-linecap @ MDN]] */
  lazy val strokeLineCap: SvgAttr[String] = stringSvgAttr("stroke-linecap")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-linejoin stroke-linejoin @ MDN]] */
  lazy val strokeLineJoin: SvgAttr[String] = stringSvgAttr("stroke-linejoin")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-miterlimit stroke-miterlimit @ MDN]] */
  lazy val strokeMiterLimit: SvgAttr[String] = stringSvgAttr("stroke-miterlimit")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-opacity stroke-opacity @ MDN]] */
  lazy val strokeOpacity: SvgAttr[String] = stringSvgAttr("stroke-opacity")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/stroke-width stroke-width @ MDN]] */
  lazy val strokeWidth: SvgAttr[String] = stringSvgAttr("stroke-width")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/style style @ MDN]] */
  lazy val style: SvgAttr[String] = stringSvgAttr("style")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/surfaceScale surfaceScale @ MDN]] */
  lazy val surfaceScale: SvgAttr[String] = stringSvgAttr("surfaceScale")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/tabindex tabindex @ MDN]] */
  lazy val tabIndex: SvgAttr[String] = stringSvgAttr("tabindex")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/target target @ MDN]] */
  lazy val target: SvgAttr[String] = stringSvgAttr("target")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/targetX targetX @ MDN]] */
  lazy val targetX: SvgAttr[String] = stringSvgAttr("targetX")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/targetY targetY @ MDN]] */
  lazy val targetY: SvgAttr[String] = stringSvgAttr("targetY")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/text-anchor text-anchor @ MDN]] */
  lazy val textAnchor: SvgAttr[String] = stringSvgAttr("text-anchor")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/text-decoration text-decoration @ MDN]] */
  lazy val textDecoration: SvgAttr[String] = stringSvgAttr("text-decoration")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/text-rendering text-rendering @ MDN]] */
  lazy val textRendering: SvgAttr[String] = stringSvgAttr("text-rendering")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/to to @ MDN]] */
  lazy val to: SvgAttr[String] = stringSvgAttr("to")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/transform transform @ MDN]] */
  lazy val transform: SvgAttr[String] = stringSvgAttr("transform")


  /**
    * Aliases: [[typ]], [[tpe]]
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/type type @ MDN]]
    */
  lazy val `type`: SvgAttr[String] = stringSvgAttr("type")


  lazy val typ: SvgAttr[String] = `type`


  lazy val tpe: SvgAttr[String] = `type`


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/values values @ MDN]] */
  lazy val values: SvgAttr[String] = stringSvgAttr("values")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/viewBox viewBox @ MDN]] */
  lazy val viewBox: SvgAttr[String] = stringSvgAttr("viewBox")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/visibility visibility @ MDN]] */
  lazy val visibility: SvgAttr[String] = stringSvgAttr("visibility")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/width width @ MDN]] */
  lazy val width: SvgAttr[String] = stringSvgAttr("width")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/word-spacing word-spacing @ MDN]] */
  lazy val wordSpacing: SvgAttr[String] = stringSvgAttr("word-spacing")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/writing-mode writing-mode @ MDN]] */
  lazy val writingMode: SvgAttr[String] = stringSvgAttr("writing-mode")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/x x @ MDN]] */
  lazy val x: SvgAttr[String] = stringSvgAttr("x")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/x1 x1 @ MDN]] */
  lazy val x1: SvgAttr[String] = stringSvgAttr("x1")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/x2 x2 @ MDN]] */
  lazy val x2: SvgAttr[String] = stringSvgAttr("x2")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xChannelSelector xChannelSelector @ MDN]] */
  lazy val xChannelSelector: SvgAttr[String] = stringSvgAttr("xChannelSelector")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xlink:href xlink:href @ MDN]] */
  lazy val xlinkHref: SvgAttr[String] = stringSvgAttr("href", namespace = "xlink")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xlink:role xlink:role @ MDN]] */
  lazy val xlinkRole: SvgAttr[String] = stringSvgAttr("role", namespace = "xlink")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xlink:title xlink:title @ MDN]] */
  lazy val xlinkTitle: SvgAttr[String] = stringSvgAttr("title", namespace = "xlink")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xml:space xml:space @ MDN]] */
  lazy val xmlSpace: SvgAttr[String] = stringSvgAttr("space", namespace = "xml")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/svg svg @ MDN]] */
  lazy val xmlns: SvgAttr[String] = stringSvgAttr("xmlns")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/xmlns:xlink xmlns:xlink @ MDN]] */
  lazy val xmlnsXlink: SvgAttr[String] = stringSvgAttr("xlink", namespace = "xmlns")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/y y @ MDN]] */
  lazy val y: SvgAttr[String] = stringSvgAttr("y")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/y1 y1 @ MDN]] */
  lazy val y1: SvgAttr[String] = stringSvgAttr("y1")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/y2 y2 @ MDN]] */
  lazy val y2: SvgAttr[String] = stringSvgAttr("y2")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/yChannelSelector yChannelSelector @ MDN]] */
  lazy val yChannelSelector: SvgAttr[String] = stringSvgAttr("yChannelSelector")


  /** [[https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/z z @ MDN]] */
  lazy val z: SvgAttr[String] = stringSvgAttr("z")


}

package com.raquo.laminar.defs.tags

import com.raquo.laminar.tags.SvgTag
import org.scalajs.dom

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait SvgTags {


  /**
    * Create SVG tag
    * 
    * Note: this simply creates an instance of HtmlTag.
    *  - This does not create the element (to do that, call .apply() on the returned tag instance)
    * 
    * @param name - e.g. "circle"
    * 
    * @tparam Ref    - type of elements with this tag, e.g. dom.svg.Circle for "circle" tag
    */
  def svgTag[Ref <: dom.svg.Element](name: String): SvgTag[Ref] = new SvgTag(name)


  /**
    * Represents a hyperlink, linking to another resource.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/a a @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGAElement SVGAElement @ MDN]]
    */
  lazy val a: SvgTag[dom.SVGAElement] = svgTag("a")


  /**
    * The altGlyph element allows sophisticated selection of the glyphs used to
    * render its child character data.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/altGlyph altGlyph @ MDN]]
    */
  lazy val altGlyph: SvgTag[dom.SVGElement] = svgTag("altGlyph")


  /**
    * The altGlyphDef element defines a substitution representation for glyphs.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/altGlyphDef altGlyphDef @ MDN]]
    */
  lazy val altGlyphDef: SvgTag[dom.SVGElement] = svgTag("altGlyphDef")


  /**
    * The altGlyphItem element provides a set of candidates for glyph substitution
    * by the altGlyph element.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/altGlyphItem altGlyphItem @ MDN]]
    */
  lazy val altGlyphItem: SvgTag[dom.SVGElement] = svgTag("altGlyphItem")


  /**
    * The animate element is put inside a shape element and defines how an
    * attribute of an element changes over the animation
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/animate animate @ MDN]]
    */
  lazy val animate: SvgTag[dom.SVGElement] = svgTag("animate")


  /**
    * The animateMotion element causes a referenced element to move along a
    * motion path.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/animateMotion animateMotion @ MDN]]
    */
  lazy val animateMotion: SvgTag[dom.SVGElement] = svgTag("animateMotion")


  /**
    * The animateTransform element animates a transformation attribute on a target
    * element, thereby allowing animations to control translation, scaling,
    * rotation and/or skewing.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/animateTransform animateTransform @ MDN]]
    */
  lazy val animateTransform: SvgTag[dom.SVGElement] = svgTag("animateTransform")


  /**
    * The circle element is an SVG basic shape, used to create circles based on a
    * center point and a radius.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/circle circle @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGCircleElement SVGCircleElement @ MDN]]
    */
  lazy val circle: SvgTag[dom.SVGCircleElement] = svgTag("circle")


  /**
    * The clipping path restricts the region to which paint can be applied.
    * Conceptually, any parts of the drawing that lie outside of the region
    * bounded by the currently active clipping path are not drawn.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/clipPath clipPath @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGClipPathElement SVGClipPathElement @ MDN]]
    */
  lazy val clipPathTag: SvgTag[dom.SVGClipPathElement] = svgTag("clipPath")


  /**
    * The element allows describing the color profile used for the image.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/color-profile color-profile @ MDN]]
    */
  lazy val colorProfileTag: SvgTag[dom.SVGElement] = svgTag("color-profile")


  /**
    * The cursor element can be used to define a platform-independent custom
    * cursor. A recommended approach for defining a platform-independent custom
    * cursor is to create a PNG image and define a cursor element that references
    * the PNG image and identifies the exact position within the image which is
    * the pointer position (i.e., the hot spot).
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/cursor cursor @ MDN]]
    */
  lazy val cursor: SvgTag[dom.SVGElement] = svgTag("cursor")


  /**
    * SVG allows graphical objects to be defined for later reuse. It is
    * recommended that, wherever possible, referenced elements be defined inside
    * of a defs element. Defining these elements inside of a defs element
    * promotes understandability of the SVG content and thus promotes
    * accessibility. Graphical elements defined in a defs will not be directly
    * rendered. You can use a use element to render those elements wherever you
    * want on the viewport.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/defs defs @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGDefsElement SVGDefsElement @ MDN]]
    */
  lazy val defs: SvgTag[dom.SVGDefsElement] = svgTag("defs")


  /**
    * Each container element or graphics element in an SVG drawing can supply a
    * desc description string where the description is text-only. When the
    * current SVG document fragment is rendered as SVG on visual media, desc
    * elements are not rendered as part of the graphics. Alternate presentations
    * are possible, both visual and aural, which display the desc element but do
    * not display path elements or other graphics elements. The desc element
    * generally improve accessibility of SVG documents
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/desc desc @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGDescElement SVGDescElement @ MDN]]
    */
  lazy val desc: SvgTag[dom.SVGDescElement] = svgTag("desc")


  /**
    * The ellipse element is an SVG basic shape, used to create ellipses based
    * on a center coordinate, and both their x and y radius.
    * 
    * Ellipses are unable to specify the exact orientation of the ellipse (if,
    * for example, you wanted to draw an ellipse titled at a 45 degree angle),
    * but can be rotated by using the transform attribute.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/ellipse ellipse @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGEllipseElement SVGEllipseElement @ MDN]]
    */
  lazy val ellipse: SvgTag[dom.SVGEllipseElement] = svgTag("ellipse")


  /**
    * The feBlend filter composes two objects together ruled by a certain blending
    * mode. This is similar to what is known from image editing software when
    * blending two layers. The mode is defined by the mode attribute.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feBlend feBlend @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEBlendElement SVGFEBlendElement @ MDN]]
    */
  lazy val feBlend: SvgTag[dom.SVGFEBlendElement] = svgTag("feBlend")


  /**
    * This filter changes colors based on a transformation matrix. Every pixel's
    * color value (represented by an [R,G,B,A] vector) is matrix multiplied to
    * create a new color.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feColorMatrix feColorMatrix @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEColorMatrixElement SVGFEColorMatrixElement @ MDN]]
    */
  lazy val feColorMatrix: SvgTag[dom.SVGFEColorMatrixElement] = svgTag("feColorMatrix")


  /**
    * The color of each pixel is modified by changing each channel (R, G, B, and
    * A) to the result of what the children feFuncR, feFuncB, feFuncG,
    * and feFuncA return.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feComponentTransfer feComponentTransfer @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGComponentTransferFunctionElement SVGComponentTransferFunctionElement @ MDN]]
    */
  lazy val feComponentTransfer: SvgTag[dom.SVGComponentTransferFunctionElement] = svgTag("feComponentTransfer")


  /**
    * This filter primitive performs the combination of two input images pixel-wise
    * in image space using one of the Porter-Duff compositing operations: over,
    * in, atop, out, xor. Additionally, a component-wise arithmetic operation
    * (with the result clamped between [0..1]) can be applied.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feComposite feComposite @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFECompositeElement SVGFECompositeElement @ MDN]]
    */
  lazy val feComposite: SvgTag[dom.SVGFECompositeElement] = svgTag("feComposite")


  /**
    * the feConvolveMatrix element applies a matrix convolution filter effect.
    * A convolution combines pixels in the input image with neighboring pixels
    * to produce a resulting image. A wide variety of imaging operations can be
    * achieved through convolutions, including blurring, edge detection,
    * sharpening, embossing and beveling.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feConvolveMatrix feConvolveMatrix @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEConvolveMatrixElement SVGFEConvolveMatrixElement @ MDN]]
    */
  lazy val feConvolveMatrix: SvgTag[dom.SVGFEConvolveMatrixElement] = svgTag("feConvolveMatrix")


  /**
    * This filter primitive lights an image using the alpha channel as a bump map.
    * The resulting image, which is an RGBA opaque image, depends on the light
    * color, light position and surface geometry of the input bump map.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feDiffuseLighting feDiffuseLighting @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEDiffuseLightingElement SVGFEDiffuseLightingElement @ MDN]]
    */
  lazy val feDiffuseLighting: SvgTag[dom.SVGFEDiffuseLightingElement] = svgTag("feDiffuseLighting")


  /**
    * This filter primitive uses the pixels values from the image from in2 to
    * spatially displace the image from in.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feDisplacementMap feDisplacementMap @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEDisplacementMapElement SVGFEDisplacementMapElement @ MDN]]
    */
  lazy val feDisplacementMap: SvgTag[dom.SVGFEDisplacementMapElement] = svgTag("feDisplacementMap")


  /**
    * This filter primitive define a distant light source that can be used
    * within a lighting filter primitive: feDiffuseLighting or
    * feSpecularLighting.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feDistantLighting feDistantLighting @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEDistantLightElement SVGFEDistantLightElement @ MDN]]
    */
  lazy val feDistantLighting: SvgTag[dom.SVGFEDistantLightElement] = svgTag("feDistantLighting")


  /**
    * The filter fills the filter subregion with the color and opacity defined by
    * flood-color and flood-opacity.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feFlood feFlood @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEFloodElement SVGFEFloodElement @ MDN]]
    */
  lazy val feFlood: SvgTag[dom.SVGFEFloodElement] = svgTag("feFlood")


  /**
    * This filter primitive defines the transfer function for the alpha component
    * of the input graphic of its parent feComponentTransfer element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feFuncA feFuncA @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEFuncAElement SVGFEFuncAElement @ MDN]]
    */
  lazy val feFuncA: SvgTag[dom.SVGFEFuncAElement] = svgTag("feFuncA")


  /**
    * This filter primitive defines the transfer function for the blue component
    * of the input graphic of its parent feComponentTransfer element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feFuncB feFuncB @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEFuncBElement SVGFEFuncBElement @ MDN]]
    */
  lazy val feFuncB: SvgTag[dom.SVGFEFuncBElement] = svgTag("feFuncB")


  /**
    * This filter primitive defines the transfer function for the green component
    * of the input graphic of its parent feComponentTransfer element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feFuncG feFuncG @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEFuncGElement SVGFEFuncGElement @ MDN]]
    */
  lazy val feFuncG: SvgTag[dom.SVGFEFuncGElement] = svgTag("feFuncG")


  /**
    * This filter primitive defines the transfer function for the red component
    * of the input graphic of its parent feComponentTransfer element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feFuncR feFuncR @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEFuncRElement SVGFEFuncRElement @ MDN]]
    */
  lazy val feFuncR: SvgTag[dom.SVGFEFuncRElement] = svgTag("feFuncR")


  /**
    * The filter blurs the input image by the amount specified in stdDeviation,
    * which defines the bell-curve.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feGaussianBlur feGaussianBlur @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEGaussianBlurElement SVGFEGaussianBlurElement @ MDN]]
    */
  lazy val feGaussianBlur: SvgTag[dom.SVGFEGaussianBlurElement] = svgTag("feGaussianBlur")


  /**
    * The feImage filter fetches image data from an external source and provides
    * the pixel data as output (meaning, if the external source is an SVG image,
    * it is rasterize).
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feImage feImage @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEImageElement SVGFEImageElement @ MDN]]
    */
  lazy val feImage: SvgTag[dom.SVGFEImageElement] = svgTag("feImage")


  /**
    * The feMerge filter allows filter effects to be applied concurrently
    * instead of sequentially. This is achieved by other filters storing their
    * output via the result attribute and then accessing it in a feMergeNode
    * child.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feMerge feMerge @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEMergeElement SVGFEMergeElement @ MDN]]
    */
  lazy val feMerge: SvgTag[dom.SVGFEMergeElement] = svgTag("feMerge")


  /**
    * The feMergeNode takes the result of another filter to be processed by its
    * parent feMerge.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feMergeNode feMergeNode @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEMergeNodeElement SVGFEMergeNodeElement @ MDN]]
    */
  lazy val feMergeNode: SvgTag[dom.SVGFEMergeNodeElement] = svgTag("feMergeNode")


  /**
    * This filter is used to erode or dilate the input image. It's usefulness
    * lies especially in fattening or thinning effects.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feMorphology feMorphology @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEMorphologyElement SVGFEMorphologyElement @ MDN]]
    */
  lazy val feMorphology: SvgTag[dom.SVGFEMorphologyElement] = svgTag("feMorphology")


  /**
    * The input image as a whole is offset by the values specified in the dx
    * and dy attributes. It's used in creating drop-shadows.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feOffset feOffset @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEOffsetElement SVGFEOffsetElement @ MDN]]
    */
  lazy val feOffset: SvgTag[dom.SVGFEOffsetElement] = svgTag("feOffset")


  /**
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/fePointLight fePointLight @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFEPointLightElement SVGFEPointLightElement @ MDN]]
    */
  lazy val fePointLight: SvgTag[dom.SVGFEPointLightElement] = svgTag("fePointLight")


  /**
    * This filter primitive lights a source graphic using the alpha channel as a
    * bump map. The resulting image is an RGBA image based on the light color.
    * The lighting calculation follows the standard specular component of the
    * Phong lighting model. The resulting image depends on the light color, light
    * position and surface geometry of the input bump map. The result of the
    * lighting calculation is added. The filter primitive assumes that the viewer
    * is at infinity in the z direction.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feSpecularLighting feSpecularLighting @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFESpecularLightingElement SVGFESpecularLightingElement @ MDN]]
    */
  lazy val feSpecularLighting: SvgTag[dom.SVGFESpecularLightingElement] = svgTag("feSpecularLighting")


  /**
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feSpotlight feSpotlight @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFESpotLightElement SVGFESpotLightElement @ MDN]]
    */
  lazy val feSpotlight: SvgTag[dom.SVGFESpotLightElement] = svgTag("feSpotlight")


  /**
    * An input image is tiled and the result used to fill a target. The effect
    * is similar to the one of a pattern.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feTile feTile @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFETileElement SVGFETileElement @ MDN]]
    */
  lazy val feTile: SvgTag[dom.SVGFETileElement] = svgTag("feTile")


  /**
    * This filter primitive creates an image using the Perlin turbulence
    * function. It allows the synthesis of artificial textures like clouds or
    * marble.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/feTurbulence feTurbulence @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFETurbulenceElement SVGFETurbulenceElement @ MDN]]
    */
  lazy val feTurbulence: SvgTag[dom.SVGFETurbulenceElement] = svgTag("feTurbulence")


  /**
    * The filter element serves as container for atomic filter operations. It is
    * never rendered directly. A filter is referenced by using the filter
    * attribute on the target SVG element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/filter filter @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGFilterElement SVGFilterElement @ MDN]]
    */
  lazy val filter: SvgTag[dom.SVGFilterElement] = svgTag("filter")


  /**
    * The font element defines a font to be used for text layout.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/font font @ MDN]]
    */
  lazy val font: SvgTag[dom.SVGElement] = svgTag("font")


  /**
    * The font-face element corresponds to the CSS @font-face declaration. It
    * defines a font's outer properties.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/font-face font-face @ MDN]]
    */
  lazy val fontFace: SvgTag[dom.SVGElement] = svgTag("font-face")


  /**
    * The font-face-format element describes the type of font referenced by its
    * parent font-face-uri.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/font-face-format font-face-format @ MDN]]
    */
  lazy val fontFaceFormat: SvgTag[dom.SVGElement] = svgTag("font-face-format")


  /**
    * The font-face-name element points to a locally installed copy of this font,
    * identified by its name.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/font-face-name font-face-name @ MDN]]
    */
  lazy val fontFaceName: SvgTag[dom.SVGElement] = svgTag("font-face-name")


  /**
    * The font-face-src element corresponds to the src property in CSS @font-face
    * descriptions. It serves as container for font-face-name, pointing to
    * locally installed copies of this font, and font-face-uri, utilizing
    * remotely defined fonts.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/font-face-src font-face-src @ MDN]]
    */
  lazy val fontFaceSrc: SvgTag[dom.SVGElement] = svgTag("font-face-src")


  /**
    * The font-face-uri element points to a remote definition of the current font.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/font-face-uri font-face-uri @ MDN]]
    */
  lazy val fontFaceUri: SvgTag[dom.SVGElement] = svgTag("font-face-uri")


  /**
    * The foreignObject element allows for inclusion of a foreign XML namespace
    * which has its graphical content drawn by a different user agent. The
    * included foreign graphical content is subject to SVG transformations and
    * compositing.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/foreignObject foreignObject @ MDN]]
    */
  lazy val foreignObject: SvgTag[dom.SVGElement] = svgTag("foreignObject")


  /**
    * The g element is a container used to group objects. Transformations applied
    * to the g element are performed on all of its child elements. Attributes
    * applied are inherited by child elements. In addition, it can be used to
    * define complex objects that can later be referenced with the use element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/g g @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGGElement SVGGElement @ MDN]]
    */
  lazy val g: SvgTag[dom.SVGGElement] = svgTag("g")


  /**
    * A glyph defines a single glyph in an SVG font.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/glyph glyph @ MDN]]
    */
  lazy val glyph: SvgTag[dom.SVGElement] = svgTag("glyph")


  /**
    * The glyphRef element provides a single possible glyph to the referencing
    * altGlyph substitution.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/glyphRef glyphRef @ MDN]]
    */
  lazy val glyphRef: SvgTag[dom.SVGElement] = svgTag("glyphRef")


  /**
    * The horizontal distance between two glyphs can be fine-tweaked with an
    * hkern Element. This process is known as Kerning.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/hkern hkern @ MDN]]
    */
  lazy val hkern: SvgTag[dom.SVGElement] = svgTag("hkern")


  /**
    * The SVG Image Element (image) allows a raster image into be included in
    * an SVG document.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/image image @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGImageElement SVGImageElement @ MDN]]
    */
  lazy val image: SvgTag[dom.SVGImageElement] = svgTag("image")


  /**
    * The line element is an SVG basic shape, used to create a line connecting
    * two points.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/line line @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGLineElement SVGLineElement @ MDN]]
    */
  lazy val line: SvgTag[dom.SVGLineElement] = svgTag("line")


  /**
    * linearGradient lets authors define linear gradients to fill or stroke
    * graphical elements.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/linearGradient linearGradient @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGLinearGradientElement SVGLinearGradientElement @ MDN]]
    */
  lazy val linearGradient: SvgTag[dom.SVGLinearGradientElement] = svgTag("linearGradient")


  /**
    * The marker element defines the graphics that is to be used for drawing
    * arrowheads or polymarkers on a given path, line, polyline or
    * polygon element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/marker marker @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGMarkerElement SVGMarkerElement @ MDN]]
    */
  lazy val marker: SvgTag[dom.SVGMarkerElement] = svgTag("marker")


  /**
    * In SVG, you can specify that any other graphics object or g element can
    * be used as an alpha mask for compositing the current object into the
    * background. A mask is defined with the mask element. A mask is
    * used/referenced using the mask property.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/mask mask @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGMaskElement SVGMaskElement @ MDN]]
    */
  lazy val mask: SvgTag[dom.SVGMaskElement] = svgTag("mask")


  /**
    * Metadata is structured data about data. Metadata which is included with SVG
    * content should be specified within metadata elements. The contents of the
    * metadata should be elements from other XML namespaces such as RDF, FOAF,
    * etc.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/metadata metadata @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGMetadataElement SVGMetadataElement @ MDN]]
    */
  lazy val metadata: SvgTag[dom.SVGMetadataElement] = svgTag("metadata")


  /**
    * The missing-glyph's content is rendered, if for a given character the font
    * doesn't define an appropriate glyph.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/missing-glyph missing-glyph @ MDN]]
    */
  lazy val missingGlyph: SvgTag[dom.SVGElement] = svgTag("missing-glyph")


  /**
    * the mpath sub-element for the animateMotion element provides the ability
    * to reference an external path element as the definition of a motion path.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/mpath mpath @ MDN]]
    */
  lazy val mpath: SvgTag[dom.SVGElement] = svgTag("mpath")


  /**
    * The path element is the generic element to define a shape. All the basic
    * shapes can be created with a path element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/path path @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGPathElement SVGPathElement @ MDN]]
    */
  lazy val path: SvgTag[dom.SVGPathElement] = svgTag("path")


  /**
    * A pattern is used to fill or stroke an object using a pre-defined graphic
    * object which can be replicated ("tiled") at fixed intervals in x and y to
    * cover the areas to be painted. Patterns are defined using the pattern
    * element and then referenced by properties fill and stroke on a given
    * graphics element to indicate that the given element shall be filled or
    * stroked with the referenced pattern.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/pattern pattern @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGPatternElement SVGPatternElement @ MDN]]
    */
  lazy val pattern: SvgTag[dom.SVGPatternElement] = svgTag("pattern")


  /**
    * The polygon element defines a closed shape consisting of a set of connected
    * straight line segments.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/polygon polygon @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGPolygonElement SVGPolygonElement @ MDN]]
    */
  lazy val polygon: SvgTag[dom.SVGPolygonElement] = svgTag("polygon")


  /**
    * The polyline element is an SVG basic shape, used to create a series of
    * straight lines connecting several points. Typically a polyline is used to
    * create open shapes
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/polyline polyline @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGPolylineElement SVGPolylineElement @ MDN]]
    */
  lazy val polyline: SvgTag[dom.SVGPolylineElement] = svgTag("polyline")


  /**
    * radialGradient lets authors define radial gradients to fill or stroke
    * graphical elements.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/radialGradient radialGradient @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGRadialGradientElement SVGRadialGradientElement @ MDN]]
    */
  lazy val radialGradient: SvgTag[dom.SVGRadialGradientElement] = svgTag("radialGradient")


  /**
    * The rect element is an SVG basic shape, used to create rectangles based on
    * the position of a corner and their width and height. It may also be used to
    * create rectangles with rounded corners.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/rect rect @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGRectElement SVGRectElement @ MDN]]
    */
  lazy val rect: SvgTag[dom.SVGRectElement] = svgTag("rect")


  /**
    * The set element provides a simple means of just setting the value of an
    * attribute for a specified duration. It supports all attribute types,
    * including those that cannot reasonably be interpolated, such as string and
    * boolean values. The set element is non-additive. The additive and
    * accumulate attributes are not allowed, and will be ignored if specified.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/set set @ MDN]]
    */
  lazy val set: SvgTag[dom.SVGElement] = svgTag("set")


  /**
    * The ramp of colors to use on a gradient is defined by the stop elements
    * that are child elements to either the lineargradient element or the
    * radialGradient element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/stop stop @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGStopElement SVGStopElement @ MDN]]
    */
  lazy val stop: SvgTag[dom.SVGStopElement] = svgTag("stop")


  /**
    * When it is not the root element, the svg element can be used to nest a
    * standalone SVG fragment inside the current document (which can be an HTML
    * document). This standalone fragment has its own viewPort and its own
    * coordinate system.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/svg svg @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGSVGElement SVGSVGElement @ MDN]]
    */
  lazy val svg: SvgTag[dom.SVGSVGElement] = svgTag("svg")


  /**
    * The switch element evaluates the requiredFeatures, requiredExtensions and
    * systemLanguage attributes on its direct child elements in order, and then
    * processes and renders the first child for which these attributes evaluate
    * to true. All others will be bypassed and therefore not rendered. If the
    * child element is a container element such as a g, then the entire
    * subtree is either processed/rendered or bypassed/not rendered.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/switch switch @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGSwitchElement SVGSwitchElement @ MDN]]
    */
  lazy val switch: SvgTag[dom.SVGSwitchElement] = svgTag("switch")


  /**
    * The symbol element is used to define graphical template objects which can
    * be instantiated by a use element. The use of symbol elements for
    * graphics that are used multiple times in the same document adds structure
    * and semantics. Documents that are rich in structure may be rendered
    * graphically, as speech, or as braille, and thus promote accessibility.
    * note that a symbol element itself is not rendered. Only instances of a
    * symbol element (i.e., a reference to a symbol by a use element) are
    * rendered.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/symbol symbol @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGSymbolElement SVGSymbolElement @ MDN]]
    */
  lazy val symbol: SvgTag[dom.SVGSymbolElement] = svgTag("symbol")


  /**
    * The text element defines a graphics element consisting of text. Note that
    * it is possible to apply a gradient, pattern, clipping path, mask or filter
    * to text.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/text text @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGTextElement SVGTextElement @ MDN]]
    */
  lazy val text: SvgTag[dom.SVGTextElement] = svgTag("text")


  /**
    * In addition to text drawn in a straight line, SVG also includes the
    * ability to place text along the shape of a path element. To specify that
    * a block of text is to be rendered along the shape of a path, include
    * the given text within a textPath element which includes an xlink:href
    * attribute with a reference to a path element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/textPath textPath @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGTextPathElement SVGTextPathElement @ MDN]]
    */
  lazy val textPath: SvgTag[dom.SVGTextPathElement] = svgTag("textPath")


  /**
    * Each container element or graphics element in an SVG drawing can supply a
    * title description string where the description is text-only. When the
    * current SVG document fragment is rendered as SVG on visual media, title
    * elements are not rendered as part of the graphics. Alternate presentations
    * are possible, both visual and aural, which display the title element but do
    * not display path elements or other graphics elements. The title element
    * generally improve accessibility of SVG documents.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/title title @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGTextPathElement SVGTextPathElement @ MDN]]
    */
  lazy val titleTag: SvgTag[dom.SVGTextPathElement] = svgTag("title")


  /**
    * The textual content for a text can be either character data directly
    * embedded within the text element or the character data content of a
    * referenced element, where the referencing is specified with a tref element.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/tref tref @ MDN]]
    */
  lazy val tref: SvgTag[dom.SVGElement] = svgTag("tref")


  /**
    * Within a text element, text and font properties and the current text
    * position can be adjusted with absolute or relative coordinate values by
    * including a tspan element.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/tspan tspan @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGTSpanElement SVGTSpanElement @ MDN]]
    */
  lazy val tspan: SvgTag[dom.SVGTSpanElement] = svgTag("tspan")


  /**
    * The use element takes nodes from within the SVG document, and duplicates
    * them somewhere else. The effect is the same as if the nodes were deeply
    * cloned into a non-exposed DOM, and then pasted where the use element is,
    * much like anonymous content and XBL. Since the cloned nodes are not exposed,
    * care must be taken when using CSS to style a use element and its hidden
    * descendants. CSS attributes are not guaranteed to be inherited by the
    * hidden, cloned DOM unless you explicitly request it using CSS inheritance.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/use use @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGUseElement SVGUseElement @ MDN]]
    */
  lazy val use: SvgTag[dom.SVGUseElement] = svgTag("use")


  /**
    * A view is a defined way to view the image, like a zoom level or a detail
    * view.
    * 
    *  - [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/view view @ MDN]]
    *  - [[https://developer.mozilla.org/en-US/docs/Web/API/SVGViewElement SVGViewElement @ MDN]]
    */
  lazy val view: SvgTag[dom.SVGViewElement] = svgTag("view")


  /**
    * The vertical distance between two glyphs in top-to-bottom fonts can be
    * fine-tweaked with an vkern Element. This process is known as Kerning.
    * 
    * [[https://developer.mozilla.org/en-US/docs/Web/SVG/Element/vkern vkern @ MDN]]
    */
  lazy val vkern: SvgTag[dom.SVGElement] = svgTag("vkern")


}

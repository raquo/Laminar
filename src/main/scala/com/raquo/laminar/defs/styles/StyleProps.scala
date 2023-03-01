package com.raquo.laminar.defs.styles

import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.keys.DerivedStyleProp
import com.raquo.laminar.defs.styles.{traits => s}
import com.raquo.laminar.defs.styles.{units => u}
import com.raquo.laminar.modifiers.KeySetter.StyleSetter

// #NOTE: GENERATED CODE
//  - This file is generated at compile time from the data in Scala DOM Types
//  - See `project/DomDefsGenerator.scala` for code generation params
//  - Contribute to https://github.com/raquo/scala-dom-types to add missing tags / attrs / props / etc.

trait StyleProps {


  protected type DSP[V] = DerivedStyleProp[V]

  protected type SS = StyleSetter


  /**
    * Create custom CSS property
    * 
    * @param name - name of CSS property, e.g. "font-weight"
    * 
    * @tparam V   - type of values recognized by JS for this property, e.g. Int
    *               Note: String is always allowed regardless of the type you put here.
    *               If unsure, use String type as V.
    */
  def styleProp[V](name: String): StyleProp[V] = new StyleProp(name)


  // -- Basic types --

  protected def doubleStyle(key: String): StyleProp[Double] = 
    new StyleProp[Double](key)

  protected def intStyle(key: String): StyleProp[Int] = 
    new StyleProp[Int](key)

  protected def stringStyle(key: String): StyleProp[String] = 
    new StyleProp[String](key)


  // -- Shared types --

  protected def autoStyle[V](key: String): StyleProp[V] with s.Auto = 
    new StyleProp[V](key) with s.Auto

  protected def colorStyle(key: String): StyleProp[String] with s.Color with u.Color[SS, DSP] = 
    new StyleProp[String](key) with s.Color with u.Color[SS, DSP]

  protected def flexPositionStyle(key: String): StyleProp[String] with s.FlexPosition = 
    new StyleProp[String](key) with s.FlexPosition

  protected def lengthAutoStyle(key: String): StyleProp[String] with s.Auto with u.Length[DSP, Int] = 
    new StyleProp[String](key) with s.Auto with u.Length[DSP, Int]

  protected def lengthStyle(key: String): StyleProp[String] with u.Length[DSP, Int] = 
    new StyleProp[String](key) with u.Length[DSP, Int]

  protected def lineStyle(key: String): StyleProp[String] with s.Line = 
    new StyleProp[String](key) with s.Line

  protected def maxLengthStyle(key: String): StyleProp[String] with s.MinMaxLength with s.None = 
    new StyleProp[String](key) with s.MinMaxLength with s.None

  protected def minLengthStyle(key: String): StyleProp[String] with s.MinMaxLength with s.Auto = 
    new StyleProp[String](key) with s.MinMaxLength with s.Auto

  protected def noneStyle[V](key: String): StyleProp[V] with s.None = 
    new StyleProp[V](key) with s.None

  protected def normalStyle[V](key: String): StyleProp[V] with s.Normal = 
    new StyleProp[V](key) with s.Normal

  protected def overflowStyle(key: String): StyleProp[String] with s.Overflow = 
    new StyleProp[String](key) with s.Overflow

  protected def paddingBoxSizingStyle(key: String): StyleProp[String] with s.PaddingBoxSizing = 
    new StyleProp[String](key) with s.PaddingBoxSizing

  protected def pageBreakStyle(key: String): StyleProp[String] with s.PageBreak = 
    new StyleProp[String](key) with s.PageBreak

  protected def textAlignStyle(key: String): StyleProp[String] with s.TextAlign = 
    new StyleProp[String](key) with s.TextAlign

  protected def timeStyle(key: String): StyleProp[String] with u.Time[DSP] = 
    new StyleProp[String](key) with u.Time[DSP]

  protected def urlNoneStyle(key: String): StyleProp[String] with s.None with u.Url[DSP] = 
    new StyleProp[String](key) with s.None with u.Url[DSP]

  protected def urlStyle(key: String): StyleProp[String] with u.Url[DSP] = 
    new StyleProp[String](key) with u.Url[DSP]


  // -- Unique types --

  protected def alignContentStyle(key: String): StyleProp[String] with s.AlignContent = 
    new StyleProp[String](key) with s.AlignContent

  protected def backfaceVisibilityStyle(key: String): StyleProp[String] with s.BackfaceVisibility = 
    new StyleProp[String](key) with s.BackfaceVisibility

  protected def backgroundAttachmentStyle(key: String): StyleProp[String] with s.BackgroundAttachment = 
    new StyleProp[String](key) with s.BackgroundAttachment

  protected def backgroundSizeStyle(key: String): StyleProp[String] with s.BackgroundSize = 
    new StyleProp[String](key) with s.BackgroundSize

  protected def borderCollapseStyle(key: String): StyleProp[String] with s.BorderCollapse = 
    new StyleProp[String](key) with s.BorderCollapse

  protected def boxSizingStyle(key: String): StyleProp[String] with s.BoxSizing = 
    new StyleProp[String](key) with s.BoxSizing

  protected def clearStyle(key: String): StyleProp[String] with s.Clear = 
    new StyleProp[String](key) with s.Clear

  protected def colorUrlStyle(key: String): StyleProp[String] with s.Color with u.Color[SS, DSP] with u.Url[DSP] = 
    new StyleProp[String](key) with s.Color with u.Color[SS, DSP] with u.Url[DSP]

  protected def cursorStyle(key: String): StyleProp[String] with s.Cursor = 
    new StyleProp[String](key) with s.Cursor

  protected def directionStyle(key: String): StyleProp[String] with s.Direction = 
    new StyleProp[String](key) with s.Direction

  protected def displayStyle(key: String): StyleProp[String] with s.Display = 
    new StyleProp[String](key) with s.Display

  protected def emptyCellsStyle(key: String): StyleProp[String] with s.EmptyCells = 
    new StyleProp[String](key) with s.EmptyCells

  protected def flexDirectionStyle(key: String): StyleProp[String] with s.FlexDirection = 
    new StyleProp[String](key) with s.FlexDirection

  protected def flexWrapStyle(key: String): StyleProp[String] with s.FlexWrap = 
    new StyleProp[String](key) with s.FlexWrap

  protected def floatStyle(key: String): StyleProp[String] with s.Float = 
    new StyleProp[String](key) with s.Float

  protected def fontSizeStyle(key: String): StyleProp[String] with s.FontSize = 
    new StyleProp[String](key) with s.FontSize

  protected def fontStyleStyle(key: String): StyleProp[String] with s.FontStyle = 
    new StyleProp[String](key) with s.FontStyle

  protected def fontWeightStyle(key: String): StyleProp[String] with s.FontWeight = 
    new StyleProp[String](key) with s.FontWeight

  protected def justifyContentStyle(key: String): StyleProp[String] with s.JustifyContent = 
    new StyleProp[String](key) with s.JustifyContent

  protected def lengthNormalStyle(key: String): StyleProp[String] with s.Normal with u.Length[DSP, Int] = 
    new StyleProp[String](key) with s.Normal with u.Length[DSP, Int]

  protected def listStylePositionStyle(key: String): StyleProp[String] with s.ListStylePosition = 
    new StyleProp[String](key) with s.ListStylePosition

  protected def listStyleTypeStyle(key: String): StyleProp[String] with s.ListStyleType = 
    new StyleProp[String](key) with s.ListStyleType

  protected def mixBlendModeStyle(key: String): StyleProp[String] with s.MixBlendMode = 
    new StyleProp[String](key) with s.MixBlendMode

  protected def overflowWrapStyle(key: String): StyleProp[String] with s.OverflowWrap = 
    new StyleProp[String](key) with s.OverflowWrap

  protected def pointerEventsStyle(key: String): StyleProp[String] with s.PointerEvents = 
    new StyleProp[String](key) with s.PointerEvents

  protected def positionStyle(key: String): StyleProp[String] with s.Position = 
    new StyleProp[String](key) with s.Position

  protected def tableLayoutStyle(key: String): StyleProp[String] with s.TableLayout = 
    new StyleProp[String](key) with s.TableLayout

  protected def textDecorationStyle(key: String): StyleProp[String] with s.TextDecoration = 
    new StyleProp[String](key) with s.TextDecoration

  protected def textOverflowStyle(key: String): StyleProp[String] with s.TextOverflow = 
    new StyleProp[String](key) with s.TextOverflow

  protected def textTransformStyle(key: String): StyleProp[String] with s.TextTransform = 
    new StyleProp[String](key) with s.TextTransform

  protected def textUnderlinePositionStyle(key: String): StyleProp[String] with s.TextUnderlinePosition = 
    new StyleProp[String](key) with s.TextUnderlinePosition

  protected def verticalAlignStyle(key: String): StyleProp[String] with s.VerticalAlign = 
    new StyleProp[String](key) with s.VerticalAlign

  protected def visibilityStyle(key: String): StyleProp[String] with s.Visibility = 
    new StyleProp[String](key) with s.Visibility

  protected def whiteSpaceStyle(key: String): StyleProp[String] with s.WhiteSpace = 
    new StyleProp[String](key) with s.WhiteSpace

  protected def wordBreakStyle(key: String): StyleProp[String] with s.WordBreak = 
    new StyleProp[String](key) with s.WordBreak



  // -- Style Props --


  /**
    * The all shorthand CSS property resets all of an element's properties except
    * unicode-bidi, direction, and CSS Custom Properties. It can set properties to
    * their initial or inherited values, or to the values specified in another
    * stylesheet origin.
    * 
    * Note: IE does not support this property
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/all
    */
  lazy val all: StyleProp[String] = stringStyle("all")


  /**
    * The animation CSS property is a shorthand property for animation-name,
    * animation-duration, animation-timing-function, animation-delay,
    * animation-iteration-count and animation-direction.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/animation
    */
  lazy val animation: StyleProp[String] = stringStyle("animation")


  /**
    * The animation-delay CSS property specifies when the animation should start.
    * This lets the animation sequence begin some time after it's applied to an
    * element.
    * 
    * A value of 0s, which is the default value of the property, indicates that
    * the animation should begin as soon as it's applied. Otherwise, the value
    * specifies an offset from the moment the animation is applied to the element;
    * animation will begin that amount of time after being applied.
    * 
    * Specifying a negative value for the animation delay causes the animation to
    * begin executing immediately. However, it will appear to have begun executing
    * partway through its cycle. For example, if you specify -1s as the animation
    * delay time, the animation will begin immediately but will start 1 second
    * into the animation sequence.
    * 
    * If you specify a negative value for the animation delay, but the starting
    * value is implicit, the starting value is taken from the moment the animation
    * is applied to the element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/animation-delay
    */
  lazy val animationDelay: StyleProp[String] with u.Time[DSP] = timeStyle("animation-delay")


  /**
    * The animation-direction CSS property indicates whether the animation should
    * play in reverse on alternate cycles.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/animation-direction
    */
  lazy val animationDirection: StyleProp[String] = stringStyle("animation-direction")


  /**
    * The animation-duration CSS property specifies the Length of time that an
    * animation should take to complete one cycle.
    * 
    * A value of 0s, which is the default value, indicates that no animation should
    * occur.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/animation-duration
    */
  lazy val animationDuration: StyleProp[String] with u.Time[DSP] = timeStyle("animation-duration")


  /**
    * The animation-fill-mode CSS property specifies how a CSS animation should
    * apply styles to its target before and after it is executing.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/animation-fill-mode
    */
  lazy val animationFillMode: StyleProp[String] = stringStyle("animation-fill-mode")


  /**
    * The animation-iteration-count CSS property defines the number of times an
    * animation cycle should be played before stopping.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/animation-iteration-count
    */
  lazy val animationIterationCount: StyleProp[Double] = doubleStyle("animation-iteration-count")


  /**
    * The animation-name CSS property specifies a list of animations that should
    * be applied to the selected element. Each name indicates a @keyframes at-rule
    * that defines the property values for the animation sequence.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/animation-name
    */
  lazy val animationName: StyleProp[String] = stringStyle("animation-name")


  /**
    * The animation-play-state CSS property determines whether an animation is
    * running or paused. You can query this property's value to determine whether
    * or not the animation is currently running; in addition, you can set its
    * value to pause and resume playback of an animation.
    * 
    * Resuming a paused animation will start the animation from where it left off
    * at the time it was paused, rather than starting over from the beginning of
    * the animation sequence.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/animation-play-state
    */
  lazy val animationPlayState: StyleProp[String] = stringStyle("animation-play-state")


  /**
    * The CSS animation-timing-function property specifies how a CSS animation
    * should progress over the duration of each cycle. The possible values are
    * one or several `<timing-function>`.
    * 
    * For keyframed animations, the timing function applies between keyframes
    * rather than over the entire animation. In other words, the timing function
    * is applied at the start of the keyframe and at the end of the keyframe.
    * 
    * An animation timing function defined within a keyframe block applies to that
    * keyframe; otherwise. If no timing function is specified for the keyframe,
    * the timing function specified for the overall animation is used.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/animation-timing-function
    */
  lazy val animationTimingFunction: StyleProp[String] = stringStyle("animation-timing-function")


  /**
    * The CSS align-content property sets the distribution of space between and
    * around content items along a flexbox's cross-axis or a grid's block axis.
    * 
    * 
    * @see https://css-tricks.com/snippets/css/a-guide-to-flexbox/
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/align-content
    */
  lazy val alignContent: StyleProp[String] with s.AlignContent = alignContentStyle("align-content")


  /**
    * The CSS align-items property sets the align-self value on all direct children
    * as a group. In Flexbox, it controls the alignment of items on the Cross Axis.
    * In Grid Layout, it controls the alignment of items on the Block Axis within
    * their grid area.
    * 
    * @see https://css-tricks.com/snippets/css/a-guide-to-flexbox/
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/align-items
    */
  lazy val alignItems: StyleProp[String] with s.FlexPosition = flexPositionStyle("align-items")


  /**
    * The align-self CSS property overrides a grid or flex item's align-items
    * value. In Grid, it aligns the item inside the grid area. In Flexbox,
    * it aligns the item on the cross axis.
    * 
    * @see https://css-tricks.com/snippets/css/a-guide-to-flexbox/
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/align-self
    */
  lazy val alignSelf: StyleProp[String] with s.FlexPosition = flexPositionStyle("align-self")


  /**
    * The background CSS property is a shorthand for setting the individual
    * background values in a single place in the style sheet. background can be
    * used to set the values for one or more of: background-clip, background-color,
    * background-image, background-origin, background-position, background-repeat,
    * background-size, and background-attachment.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/background
    */
  lazy val background: StyleProp[String] with s.Color with u.Color[SS, DSP] with u.Url[DSP] = colorUrlStyle("background")


  /**
    * If a background-image is specified, the background-attachment CSS
    * property determines whether that image's position is fixed within
    * the viewport, or scrolls along with its containing block.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/background-attachment
    */
  lazy val backgroundAttachment: StyleProp[String] with s.BackgroundAttachment = backgroundAttachmentStyle("background-attachment")


  /**
    * The background-clip CSS property specifies whether an element's background,
    * either the color or image, extends underneath its border.
    * 
    * If there is no background image, this property has only visual effect when
    * the border has transparent regions (because of border-style) or partially
    * opaque regions; otherwise the border covers up the difference.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/background-clip
    */
  lazy val backgroundClip: StyleProp[String] with s.PaddingBoxSizing = paddingBoxSizingStyle("background-clip")


  /**
    * The background-color CSS property sets the background color of an element,
    * either through a color value or the keyword transparent.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/background-color
    */
  lazy val backgroundColor: StyleProp[String] with s.Color with u.Color[SS, DSP] = colorStyle("background-color")


  /**
    * The background-image CSS property sets one or more background images on an
    * element. The background images are drawn on stacking context layers on top
    * of each other. The first layer specified is drawn as if it is closest to
    * the user. The borders of the element are then drawn on top of them, and the
    * background-color is drawn beneath them.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/background-image
    */
  lazy val backgroundImage: StyleProp[String] with u.Url[DSP] = urlStyle("background-image")


  /**
    * The background-origin CSS property determines the background positioning
    * area, that is the position of the origin of an image specified using the
    * background-image CSS property.
    * 
    * Note that background-origin is ignored when background-attachment is fixed.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/background-origin
    */
  lazy val backgroundOrigin: StyleProp[String] with s.PaddingBoxSizing = paddingBoxSizingStyle("background-origin")


  /**
    * The background-position CSS property sets the initial position, relative to
    * the background position layer defined by background-origin for each defined
    * background image.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/background-position
    */
  lazy val backgroundPosition: StyleProp[String] = stringStyle("background-position")


  /**
    * The background-repeat CSS property defines how background images are repeated.
    * A background image can be repeated along the horizontal axis, the vertical
    * axis, both, or not repeated at all. When the repetition of the image tiles
    * doesn't let them exactly cover the background, the way adjustments are done
    * can be controlled by the author: by default, the last image is clipped, but
    * the different tiles can instead be re-sized, or space can be inserted
    * between the tiles.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/background-repeat
    */
  lazy val backgroundRepeat: StyleProp[String] = stringStyle("background-repeat")


  /**
    * The background-size CSS property specifies the size of the background
    * images. The size of the image can be fully constrained or only partially in
    * order to preserve its intrinsic ratio.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/background-size
    */
  lazy val backgroundSize: StyleProp[String] with s.BackgroundSize = backgroundSizeStyle("background-size")


  /**
    * The CSS backface-visibility property determines whether or not the back
    * face of the element is visible when facing the user. The back face of an
    * element always is a transparent background, letting, when visible, a mirror
    * image of the front face be displayed.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/backface-visibility
    */
  lazy val backfaceVisibility: StyleProp[String] with s.BackfaceVisibility = backfaceVisibilityStyle("backface-visibility")


  /**
    * The border CSS property is a shorthand property for setting the individual
    * border property values in a single place in the style sheet. border can be
    * used to set the values for one or more of: border-width, border-style,
    * border-color.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border
    */
  lazy val border: StyleProp[String] = stringStyle("border")


  /**
    * The border-top CSS property is a shorthand that sets the values of
    * border-top-color, border-top-style, and border-top-width. These
    * properties describe the top border of elements.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-top
    */
  lazy val borderTop: StyleProp[String] = stringStyle("border-top")


  /**
    * The border-right CSS property is a shorthand that sets the values of
    * border-right-color, border-right-style, and border-right-width. These
    * properties describe the right border of elements.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-right
    */
  lazy val borderRight: StyleProp[String] = stringStyle("border-right")


  /**
    * The border-bottom CSS property is a shorthand that sets the values of
    * border-bottom-color, border-bottom-style, and border-bottom-width. These
    * properties describe the bottom border of elements.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom
    */
  lazy val borderBottom: StyleProp[String] = stringStyle("border-bottom")


  /**
    * The border-left CSS property is a shorthand that sets the values of
    * border-left-color, border-left-style, and border-left-width. These
    * properties describe the left border of elements.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-left
    */
  lazy val borderLeft: StyleProp[String] = stringStyle("border-left")


  /**
    * The border-color CSS property is a shorthand for setting the color of the
    * four sides of an element's border: border-top-color, border-right-color,
    * border-bottom-color, border-left-color.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-color
    */
  lazy val borderColor: StyleProp[String] with s.Color with u.Color[SS, DSP] = colorStyle("border-color")


  /**
    * The border-top-color CSS property sets the color of the top border of an element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-color
    */
  lazy val borderTopColor: StyleProp[String] with s.Color with u.Color[SS, DSP] = colorStyle("border-top-color")


  /**
    * The border-right-color CSS property sets the color of the right border of an element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-right-color
    */
  lazy val borderRightColor: StyleProp[String] with s.Color with u.Color[SS, DSP] = colorStyle("border-right-color")


  /**
    * The border-bottom-color CSS property sets the color of the bottom border of an element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-color
    */
  lazy val borderBottomColor: StyleProp[String] with s.Color with u.Color[SS, DSP] = colorStyle("border-bottom-color")


  /**
    * The border-left-color CSS property sets the color of the left border of an element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-left-color
    */
  lazy val borderLeftColor: StyleProp[String] with s.Color with u.Color[SS, DSP] = colorStyle("border-left-color")


  /**
    * The border-image CSS property draws an image around a given element.
    * It replaces the element's regular border.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-image
    */
  lazy val borderImage: StyleProp[String] with u.Url[DSP] = urlStyle("border-image")


  /**
    * The border-style CSS property is a shorthand property for setting the line
    * style for all four sides of the element's border.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-style
    */
  lazy val borderStyle: StyleProp[String] with s.Line = lineStyle("border-style")


  /**
    * The border-top-style CSS property sets the line style of the top border of a box.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-style
    */
  lazy val borderTopStyle: StyleProp[String] with s.Line = lineStyle("border-top-style")


  /**
    * The border-right-style CSS property sets the line style of the right border of a box.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-right-style
    */
  lazy val borderRightStyle: StyleProp[String] with s.Line = lineStyle("border-right-style")


  /**
    * The border-bottom-style CSS property sets the line style of the bottom border of a box.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-style
    */
  lazy val borderBottomStyle: StyleProp[String] with s.Line = lineStyle("border-bottom-style")


  /**
    * The border-left-style CSS property sets the line style of the left border of a box.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-left-style
    */
  lazy val borderLeftStyle: StyleProp[String] with s.Line = lineStyle("border-left-style")


  /**
    * The border-width CSS property is a shorthand property for setting the width
    * for all four sides of the element's border.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-width
    */
  lazy val borderWidth: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("border-width")


  /**
    * The border-top-width CSS property sets the line width of the top border of a box.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-width
    */
  lazy val borderTopWidth: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("border-top-width")


  /**
    * The border-right-width CSS property sets the line width of the right border of a box.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-right-width
    */
  lazy val borderRightWidth: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("border-right-width")


  /**
    * The border-bottom-width CSS property sets the line width of the bottom border of a box.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-width
    */
  lazy val borderBottomWidth: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("border-bottom-width")


  /**
    * The border-left-width CSS property sets the line width of the left border of a box.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-left-width
    */
  lazy val borderLeftWidth: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("border-left-width")


  /**
    * The border-radius CSS property allows Web authors to define how rounded
    * border corners are. The curve of each corner is defined using one or two
    * radii, defining its shape: circle or ellipse.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-radius
    */
  lazy val borderRadius: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("border-radius")


  /**
    * The border-top-left-radius CSS property sets the rounding of the
    * top-left corner of the element. The rounding can be a circle or an
    * ellipse, or if one of the value is 0 no rounding is done and the corner is
    * square.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-left-radius
    */
  lazy val borderTopLeftRadius: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("border-top-left-radius")


  /**
    * The border-top-right-radius CSS property sets the rounding of the top-right
    * corner of the element. The rounding can be a circle or an ellipse, or if
    * one of the value is 0 no rounding is done and the corner is square.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-top-right-radius
    */
  lazy val borderTopRightRadius: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("border-top-right-radius")


  /**
    * The border-bottom-right-radius CSS property sets the rounding of the
    * bottom-right corner of the element. The rounding can be a circle or an
    * ellipse, or if one of the value is 0 no rounding is done and the corner is
    * square.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-right-radius
    */
  lazy val borderBottomRightRadius: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("border-bottom-right-radius")


  /**
    * The border-bottom-left-radius CSS property sets the rounding of the
    * bottom-left corner of the element. The rounding can be a circle or an
    * ellipse, or if one of the value is 0 no rounding is done and the corner is
    * square.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-bottom-left-radius
    */
  lazy val borderBottomLeftRadius: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("border-bottom-left-radius")


  /**
    * The border-collapse CSS property selects a table's border model. This has
    * a big influence on the look and style of the table cells.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-collapse
    */
  lazy val borderCollapse: StyleProp[String] with s.BorderCollapse = borderCollapseStyle("border-collapse")


  /**
    * The border-spacing CSS property specifies the distance between the borders
    * of adjacent cells (only for the separated borders model). This is equivalent
    * to the cellspacing attribute in presentational HTML, but an optional second
    * value can be used to set different horizontal and vertical spacing.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/border-spacing
    */
  lazy val borderSpacing: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("border-spacing")


  /**
    * The bottom CSS property participates in specifying the position of
    * positioned elements.
    * 
    * For absolutely positioned elements, that is those with position: absolute
    * or position: fixed, it specifies the distance between the bottom margin edge
    * of the element and the bottom edge of its containing block.
    * 
    * For relatively positioned elements, that is those with position: relative,
    * it specifies the distance the element is moved above its normal position.
    * 
    * However, the top property overrides the bottom property, so if top is not
    * auto, the computed value of bottom is the negative of the computed value of
    * top.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/bottom
    */
  lazy val bottom: StyleProp[String] with s.Auto with u.Length[DSP, Int] = lengthAutoStyle("bottom")


  /**
    * The box-shadow CSS property describes one or more shadow effects as a
    * comma-separated list. It allows casting a drop shadow from the frame of
    * almost any element. If a border-radius is specified on the element with a
    * box shadow, the box shadow takes on the same rounded corners. The z-ordering
    * of multiple box shadows is the same as multiple text shadows (the first
    * specified shadow is on top).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/box-shadow
    */
  lazy val boxShadow: StyleProp[String] = stringStyle("box-shadow")


  /**
    * The box-sizing CSS property is used to alter the default CSS box model used
    * to calculate widths and heights of elements. It is possible to use this
    * property to emulate the behavior of browsers that do not correctly support
    * the CSS box model specification.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/box-sizing
    */
  lazy val boxSizing: StyleProp[String] with s.BoxSizing = boxSizingStyle("box-sizing")


  /**
    * The caption-side CSS property positions the content of a table's caption
    * on the specified side (top or bottom).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/caption-side
    */
  lazy val captionSide: StyleProp[String] = stringStyle("caption-side")


  /**
    * The clear CSS property specifies whether an element can be next to floating
    * elements that precede it or must be moved down (cleared) below them.
    * 
    * The clear property applies to both floating and non-floating elements.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/clear
    */
  lazy val clear: StyleProp[String] with s.Clear = clearStyle("clear")


  /**
    * The clip CSS property defines what portion of an element is visible. The
    * clip property applies only to elements with position:absolute.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/clip
    */
  lazy val clip: StyleProp[String] = stringStyle("clip")


  /**
    * The CSS color property sets the foreground color of an element's text
    * content, and its decorations. It doesn't affect any other characteristic of
    * the element; it should really be called text-color and would have been
    * named so, save for historical reasons.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/color
    */
  lazy val color: StyleProp[String] with s.Color with u.Color[SS, DSP] = colorStyle("color")


  /**
    * The columns CSS property is a shorthand property allowing to set both the
    * column-width and the column-count properties at the same time.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/columns
    */
  lazy val columns: StyleProp[String] = stringStyle("columns")


  /**
    * The column-count CSS property describes the number of columns of the element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/column-count
    */
  lazy val columnCount: StyleProp[Int] with s.Auto = autoStyle("column-count")


  /**
    * The column-fill CSS property controls how contents are partitioned into
    * columns. Contents are either balanced, which means that contents in all
    * columns will have the same height or, when using auto, just take up the
    * room the content needs.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/column-fill
    */
  lazy val columnFill: StyleProp[String] = stringStyle("column-fill")


  /**
    * The column-gap CSS property sets the size of the gap between columns for
    * elements which are specified to display as a multi-column element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/column-gap
    */
  lazy val columnGap: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("column-gap")


  /**
    * The column-span CSS property makes it possible for an element to span across
    * all columns when its value is set to `all`. An element that spans more than
    * one column is called a spanning element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/column-span
    */
  lazy val columnSpan: StyleProp[Int] = intStyle("column-span")


  /**
    * The column-width CSS property suggests an optimal column width. This is not
    * a absolute value but a mere hint. Browser will adjust the width of the
    * column around that suggested value, allowing to achieve scalable designs
    * that fit different screen size. Especially in presence of the column-count
    * CSS property which has precedence, to set an exact column width, all Length
    * values must be specified. In horizontal text these are width, column-width,
    * column-gap, and column-rule-width
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/column-width
    */
  lazy val columnWidth: StyleProp[String] with s.Auto with u.Length[DSP, Int] = lengthAutoStyle("column-width")


  /**
    * In multi-column layouts, the column-rule CSS property specifies a straight
    * line, or "rule", to be drawn between each column. It is a convenient
    * shorthand to avoid setting each of the individual column-rule-* properties
    * separately : column-rule-width, column-rule-style and column-rule-color.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule
    */
  lazy val columnRule: StyleProp[String] = stringStyle("column-rule")


  /**
    * The column-rule-color CSS property lets you set the color of the rule drawn
    * between columns in multi-column layouts.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule-color
    */
  lazy val columnRuleColor: StyleProp[String] with s.Color with u.Color[SS, DSP] = colorStyle("column-rule-color")


  /**
    * The column-rule-width CSS property lets you set the width of the rule drawn
    * between columns in multi-column layouts.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule-width
    */
  lazy val columnRuleWidth: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("column-rule-width")


  /**
    * The column-rule-style CSS property lets you set the style of the rule drawn
    * between columns in multi-column layouts.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/column-rule-style
    */
  lazy val columnRuleStyle: StyleProp[String] with s.Line = lineStyle("column-rule-style")


  /**
    * The `content` CSS property is used with the ::before and ::after pseudo-elements
    * to generate content in an element. Objects inserted using the content
    * property are anonymous replaced elements.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/content
    */
  lazy val contentCss: StyleProp[String] with s.None with u.Url[DSP] = urlNoneStyle("content")


  /**
    * The counter-increment CSS property is used to increase the value of CSS
    * Counters by a given value. The counter's value can be reset using the
    * counter-reset CSS property.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/counter-increment
    */
  lazy val counterIncrement: StyleProp[String] = stringStyle("counter-increment")


  /**
    * The counter-reset CSS property is used to reset CSS Counters to a given
    * value.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/counter-reset
    */
  lazy val counterReset: StyleProp[String] = stringStyle("counter-reset")


  /**
    * The cursor CSS property specifies the mouse cursor displayed when the mouse
    * pointer is over an element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/cursor
    */
  lazy val cursor: StyleProp[String] with s.Cursor = cursorStyle("cursor")


  /**
    * Set the direction CSS property to match the direction of the text: rtl for
    * Hebrew or Arabic text and ltr for other scripts. This is typically done as
    * part of the document (e.g., using the dir attribute in HTML) rather than
    * through direct use of CSS.
    * 
    * The property sets the base text direction of block-level elements and the
    * direction of embeddings created by the unicode-bidi property. It also sets
    * the default alignment of text and block-level elements and the direction
    * that cells flow within a table row.
    * 
    * Unlike the dir attribute in HTML, the direction property is not inherited
    * from table columns into table cells, since CSS inheritance follows the
    * document tree, and table cells are inside of the rows but not inside of the
    * columns.
    * 
    * The direction and unicode-bidi properties are the two only properties which
    * are not affected by the all shorthand.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/direction
    */
  lazy val direction: StyleProp[String] with s.Direction = directionStyle("direction")


  /**
    * The display CSS property specifies the type of rendering box used for an
    * element. In HTML, default display property values are taken from behaviors
    * described in the HTML specifications or from the browser/user default
    * stylesheet. The default value in XML is inline.
    * 
    * In addition to the many different display box types, the value none lets
    * you turn off the display of an element; when you use none, all descendant
    * elements also have their display turned off. The document is rendered as
    * though the element doesn't exist in the document tree.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/display
    */
  lazy val display: StyleProp[String] with s.Display = displayStyle("display")


  /**
    * The empty-cells CSS property specifies how user agents should render borders
    * and backgrounds around cells that have no visible content.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/empty-cells
    */
  lazy val emptyCells: StyleProp[String] with s.EmptyCells = emptyCellsStyle("empty-cells")


  /**
    * The flex CSS property is a shorthand property specifying the ability of a
    * flex item to alter its dimensions to fill available space. Flex items can
    * be stretched to use available space proportional to their flex grow factor
    * or their flex shrink factor to prevent overflow.
    * 
    * @see https://css-tricks.com/snippets/css/a-guide-to-flexbox/
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/flex
    */
  lazy val flex: StyleProp[String] = stringStyle("flex")


  /**
    * The CSS flex-basis property specifies the flex basis which is the initial
    * main size of a flex item. The property determines the size of the
    * content-box unless specified otherwise using box-sizing.
    * 
    * @see https://css-tricks.com/snippets/css/a-guide-to-flexbox/
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/flex-basis
    */
  lazy val flexBasis: StyleProp[String] with s.Auto with u.Length[DSP, Int] = lengthAutoStyle("flex-basis")


  /**
    * The CSS flex-direction property specifies how flex items are placed in the
    * flex container defining the main axis and the direction (normal or reversed).
    * 
    * Note that the value row and row-reverse are affected by the directionality
    * of the flex container. If its dir attribute is ltr, row represents the
    * horizontal axis oriented from the left to the right, and row-reverse from
    * the right to the left; if the dir attribute is rtl, row represents the axis
    * oriented from the right to the left, and row-reverse from the left to the
    * right.
    * 
    * @see https://css-tricks.com/snippets/css/a-guide-to-flexbox/
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/flex-direction
    */
  lazy val flexDirection: StyleProp[String] with s.FlexDirection = flexDirectionStyle("flex-direction")


  /**
    * The CSS flex-grow property specifies the flex grow factor of a flex item.
    * 
    * Default value is 0.
    * 
    * @see https://css-tricks.com/snippets/css/a-guide-to-flexbox/
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/flex-grow
    */
  lazy val flexGrow: StyleProp[Double] = doubleStyle("flex-grow")


  /**
    * The CSS flex-shrink property specifies the flex shrink factor of a flex item.
    * 
    * Default value is 1.
    * 
    * @see https://css-tricks.com/snippets/css/a-guide-to-flexbox/
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/flex-shrink
    */
  lazy val flexShrink: StyleProp[Double] = doubleStyle("flex-shrink")


  /**
    * The CSS flex-wrap property specifies whether the children are forced into
    * a single line or if the items can be flowed on multiple lines.
    * 
    * @see https://css-tricks.com/snippets/css/a-guide-to-flexbox/
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/flex-wrap
    */
  lazy val flexWrap: StyleProp[String] with s.FlexWrap = flexWrapStyle("flex-wrap")


  /**
    * The float CSS property specifies that an element should be taken from the
    * normal flow and placed along the left or right side of its container, where
    * text and inline elements will wrap around it. A floating element is one
    * where the computed value of float is not `none`.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/float
    */
  lazy val float: StyleProp[String] with s.Float = floatStyle("float")


  /**
    * The font CSS property is either a shorthand property for setting font-style,
    * font-variant, font-weight, font-size, line-height and font-family, or a way
    * to set the element's font to a system font, using specific keywords.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/font
    */
  lazy val font: StyleProp[String] = stringStyle("font")


  /**
    * The font-family CSS property allows for a prioritized list of font family
    * names and/or generic family names to be specified for the selected element.
    * Unlike most other CSS properties, values are separated by a comma to indicate
    * that they are alternatives. The browser will select the first font on the
    * list that is installed on the computer, or that can be downloaded using the
    * information provided by a @font-face at-rule.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/font-family
    */
  lazy val fontFamily: StyleProp[String] = stringStyle("font-family")


  /**
    * The font-feature-settings CSS property allows control over advanced
    * typographic features in OpenType fonts.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/font-feature-settings
    */
  lazy val fontFeatureSettings: StyleProp[String] = stringStyle("font-feature-settings")


  /**
    * The font-size CSS property specifies the size of the font – specifically
    * the desired height of glyphs from the font. Setting the font size may, in
    * turn, change the size of other items, since it is used to compute the value
    * of em and ex Length units.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/font-size
    */
  lazy val fontSize: StyleProp[String] with s.FontSize = fontSizeStyle("font-size")


  /**
    * The font-size-adjust CSS property sets the size of lower-case letters
    * relative to the current font size (which defines the size of upper-case
    * letters).
    * 
    * This is useful since the legibility of fonts, especially at small sizes, is
    * determined more by the size of lowercase letters than by the size of capital
    * letters. This can cause problems when the first-choice font-family is
    * unavailable and its replacement has a significantly different aspect ratio
    * (the ratio of the size of lowercase letters to the size of the font).
    * 
    * Note: As of Dec 2021, only Firefox supports this
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/font-size-adjust
    */
  lazy val fontSizeAdjust: StyleProp[Double] with s.None = noneStyle("font-size-adjust")


  /**
    * The font-style CSS property allows italic or oblique faces to be selected
    * within a font-family.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/font-style
    */
  lazy val fontStyle: StyleProp[String] with s.FontStyle = fontStyleStyle("font-style")


  /**
    * The font-weight CSS property specifies the weight or boldness of the font.
    * However, some fonts are not available in all weights; some are available
    * only on normal and bold.
    * 
    * Numeric font weights for fonts that provide more than just normal and bold.
    * If the exact weight given is unavailable, then 600-900 use the closest
    * available darker weight (or, if there is none, the closest available
    * lighter weight), and 100-500 use the closest available lighter weight (or,
    * if there is none, the closest available darker weight). This means that for
    * fonts that provide only normal and bold, 100-500 are normal, and 600-900
    * are bold.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/font-weight
    */
  lazy val fontWeight: StyleProp[String] with s.FontWeight = fontWeightStyle("font-weight")


  /**
    * The height CSS property specifies the height of the content area of an
    * element. The content area is inside the padding, border, and margin of the
    * element.
    * 
    * The min-height and max-height properties override height.
    * 
    * @see @see https://developer.mozilla.org/en-US/docs/Web/CSS/height
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/height
    */
  lazy val height: StyleProp[String] with s.Auto with u.Length[DSP, Int] = lengthAutoStyle("height")


  /**
    * This property determines whether an element must create a new stacking context.
    * It is especially helpful when used in conjunction with mix-blend-mode and z-index.
    * 
    * Allowed values: "isolate", "auto"
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/isolation
    */
  lazy val isolation: StyleProp[String] with s.Auto = autoStyle("isolation")


  /**
    * The CSS justify-content property defines how a browser distributes available
    * space between and around elements when aligning flex items in the main-axis
    * of the current line. The alignment is done after the lengths and auto margins
    * are applied, meaning that, if there is at least one flexible element, with
    * flex-grow different than 0, it will have no effect as there won't be any
    * available space.
    * 
    * @see https://css-tricks.com/snippets/css/a-guide-to-flexbox/
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/justify-content
    */
  lazy val justifyContent: StyleProp[String] with s.JustifyContent = justifyContentStyle("justify-content")


  /**
    * The left CSS property specifies part of the position of positioned elements.
    * 
    * For absolutely positioned elements (those with position: absolute or
    * position: fixed), it specifies the distance between the left margin edge of
    * the element and the left edge of its containing block.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/left
    */
  lazy val left: StyleProp[String] with s.Auto with u.Length[DSP, Int] = lengthAutoStyle("left")


  /**
    * The letter-spacing CSS property specifies spacing behavior between text
    * characters.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/letter-spacing
    */
  lazy val letterSpacing: StyleProp[String] with s.Normal = normalStyle("letter-spacing")


  /**
    * On block level elements, the line-height CSS property specifies the minimal
    * height of line boxes within the element.
    * 
    * On non-replaced inline elements, line-height specifies the height that is
    * used in the calculation of the line box height.
    * 
    * On replaced inline elements, like buttons or other input element,
    * line-height has no effect.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/line-height
    */
  lazy val lineHeight: StyleProp[String] with s.Normal with u.Length[DSP, Int] = lengthNormalStyle("line-height")


  /**
    * The list-style CSS property is a shorthand property for setting
    * list-style-type, list-style-image and list-style-position.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/list-style
    */
  lazy val listStyle: StyleProp[String] = stringStyle("list-style")


  /**
    * The list-style-image CSS property sets the image that will be used as the
    * list item marker.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-image
    */
  lazy val listStyleImage: StyleProp[String] with s.None with u.Url[DSP] = urlNoneStyle("list-style-image")


  /**
    * The list-style-position CSS property specifies the position of the marker
    * box in the principal block box.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-position
    */
  lazy val listStylePosition: StyleProp[String] with s.ListStylePosition = listStylePositionStyle("list-style-position")


  /**
    * The list-style-type CSS property sets the marker (such as a disc, character,
    * or custom counter style) of a list item element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/list-style-type
    */
  lazy val listStyleType: StyleProp[String] with s.ListStyleType = listStyleTypeStyle("list-style-type")


  /**
    * The margin CSS property sets the margin for all four sides. It is a
    * shorthand to avoid setting each side separately with the other margin
    * properties: margin-top, margin-right, margin-bottom and margin-left.
    * 
    * Negative values are also allowed.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/margin
    */
  lazy val margin: StyleProp[String] with s.Auto with u.Length[DSP, Int] = lengthAutoStyle("margin")


  /**
    * The margin-top CSS property of an element sets the margin space required on
    * the top of an element. A negative value is also allowed.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/margin-top
    */
  lazy val marginTop: StyleProp[String] with s.Auto with u.Length[DSP, Int] = lengthAutoStyle("margin-top")


  /**
    * The margin-right CSS property of an element sets the margin space required on
    * the right of an element. A negative value is also allowed.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/margin-right
    */
  lazy val marginRight: StyleProp[String] with s.Auto with u.Length[DSP, Int] = lengthAutoStyle("margin-right")


  /**
    * The margin-bottom CSS property of an element sets the margin space required on
    * the bottom of an element. A negative value is also allowed.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/margin-bottom
    */
  lazy val marginBottom: StyleProp[String] with s.Auto with u.Length[DSP, Int] = lengthAutoStyle("margin-bottom")


  /**
    * The margin-left CSS property of an element sets the margin space required on
    * the left of an element. A negative value is also allowed.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/margin-left
    */
  lazy val marginLeft: StyleProp[String] with s.Auto with u.Length[DSP, Int] = lengthAutoStyle("margin-left")


  /**
    * If the value is a URI value, the element pointed to by the URI is used as
    * an SVG mask.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/mask
    */
  lazy val mask: StyleProp[String] with s.None with u.Url[DSP] = urlNoneStyle("mask")


  /**
    * The max-height CSS property is used to set the maximum height of a given
    * element. It prevents the used value of the height property from becoming
    * larger than the value specified for max-height.
    * 
    * max-height overrides height, but min-height overrides max-height.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/max-height
    */
  lazy val maxHeight: StyleProp[String] with s.MinMaxLength with s.None = maxLengthStyle("max-height")


  /**
    * The max-width CSS property is used to set the maximum width of a given
    * element. It prevents the used value of the width property from becoming
    * larger than the value specified for max-width.
    * 
    * max-width overrides width, but min-width overrides max-width.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/max-width
    */
  lazy val maxWidth: StyleProp[String] with s.MinMaxLength with s.None = maxLengthStyle("max-width")


  /**
    * The min-height CSS property is used to set the minimum height of a given
    * element. It prevents the used value of the height property from becoming
    * smaller than the value specified for min-height.
    * 
    * The value of min-height overrides both max-height and height.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/min-height
    */
  lazy val minHeight: StyleProp[String] with s.MinMaxLength with s.Auto = minLengthStyle("min-height")


  /**
    * The min-width CSS property is used to set the minimum width of a given
    * element. It prevents the used value of the width property from becoming
    * smaller than the value specified for min-width.
    * 
    * The value of min-width overrides both max-width and width.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/min-width
    */
  lazy val minWidth: StyleProp[String] with s.MinMaxLength with s.Auto = minLengthStyle("min-width")


  /**
    * This property sets how an element's content should blend with
    * the content of the element's parent and the element's background.
    * 
    * Note: not supported by Chrome on Android and Safari
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/mix-blend-mode
    */
  lazy val mixBlendMode: StyleProp[String] with s.MixBlendMode = mixBlendModeStyle("mix-blend-mode")


  /**
    * The opacity CSS property specifies the transparency of an element, that is,
    * the degree to which the background behind the element is overlaid.
    * 
    * The value applies to the element as a whole, including its contents, even
    * though the value is not inherited by child elements. Thus, an element and
    * its contained children all have the same opacity relative to the element's
    * background, even if the element and its children have different opacities
    * relative to one another.
    * 
    * Using this property with a value different than 1 places the element in a
    * new stacking context.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/opacity
    */
  lazy val opacity: StyleProp[Double] = doubleStyle("opacity")


  /**
    * The orphans CSS property refers to the minimum number of lines in a block
    * container that must be left at the bottom of the page. This property is
    * normally used to control how page breaks occur.
    * 
    * Note: Firefox does not support this property
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/orphans
    */
  lazy val orphans: StyleProp[Int] = intStyle("orphans")


  /**
    * The CSS outline property is a shorthand property for setting one or more of
    * the individual outline properties outline-style, outline-width and
    * outline-color in a single rule. In most cases the use of this shortcut is
    * preferable and more convenient.
    * 
    * Outlines do not take up space, they are drawn above the content.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/outline
    */
  lazy val outline: StyleProp[String] = stringStyle("outline")


  /**
    * The outline-style CSS property is used to set the style of the outline of
    * an element. An outline is a line that is drawn around elements, outside the
    * border edge, to make the element stand out.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/outline-style
    */
  lazy val outlineStyle: StyleProp[String] with s.Line = lineStyle("outline-style")


  /**
    * The outline-width CSS property is used to set the width of the outline of
    * an element. An outline is a line that is drawn around elements, outside the
    * border edge, to make the element stand out.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/outline-width
    */
  lazy val outlineWidth: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("outline-width")


  /**
    * The outline-color CSS property sets the color of the outline of an element.
    * An outline is a line that is drawn around elements, outside the border edge,
    * to make the element stand out.
    * 
    * Note: "invert" is a special outline color you can use for high contrast.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/outline-color
    */
  lazy val outlineColor: StyleProp[String] with s.Color with u.Color[SS, DSP] = colorStyle("outline-color")


  /**
    * The overflow CSS property specifies whether to clip content, render scroll
    * bars or display overflow content of a block-level element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/overflow
    */
  lazy val overflow: StyleProp[String] with s.Overflow = overflowStyle("overflow")


  /**
    * The overflow-x CSS property specifies whether to clip content, render a
    * scroll bar or display overflow content of a block-level element, when it
    * overflows at the left and right edges.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/overflow-x
    */
  lazy val overflowX: StyleProp[String] with s.Overflow = overflowStyle("overflow-x")


  /**
    * The overflow-y CSS property specifies whether to clip content, render a
    * scroll bar, or display overflow content of a block-level element, when it
    * overflows at the top and bottom edges.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/overflow-y
    */
  lazy val overflowY: StyleProp[String] with s.Overflow = overflowStyle("overflow-y")


  /**
    * The overflow-wrap CSS property specifies whether or not the browser should
    * insert line breaks within words to prevent text from overflowing its
    * content box.
    * 
    * Alias for: [[wordWrap]]
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/overflow-wrap
    */
  lazy val overflowWrap: StyleProp[String] with s.OverflowWrap = overflowWrapStyle("overflow-wrap")


  lazy val wordWrap: StyleProp[String] with s.OverflowWrap = overflowWrap


  /**
    * The padding CSS property sets the required padding space on all sides of an
    * element. The padding area is the space between the content of the element
    * and its border. Negative values are not allowed.
    * 
    * The padding property is a shorthand to avoid setting each side separately
    * (padding-top, padding-right, padding-bottom, padding-left).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/padding
    */
  lazy val padding: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("padding")


  /**
    * The padding-top CSS property of an element sets the padding space required
    * on the top of an element. The padding area is the space between the content
    * of the element and its border. Contrary to margin-top values, negative
    * values of padding-top are invalid.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/padding-top
    */
  lazy val paddingTop: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("padding-top")


  /**
    * The padding-right CSS property of an element sets the padding space
    * required on the right side of an element. The padding area is the space
    * between the content of the element and its border. Negative values are not
    * allowed.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/padding-right
    */
  lazy val paddingRight: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("padding-right")


  /**
    * The padding-bottom CSS property of an element sets the height of the padding
    * area at the bottom of an element. The padding area is the space between the
    * content of the element and it's border. Contrary to margin-bottom values,
    * negative values of padding-bottom are invalid.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/padding-bottom
    */
  lazy val paddingBottom: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("padding-bottom")


  /**
    * The padding-left CSS property of an element sets the padding space required
    * on the left side of an element. The padding area is the space between the
    * content of the element and it's border. A negative value is not allowed.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/padding-left
    */
  lazy val paddingLeft: StyleProp[String] with u.Length[DSP, Int] = lengthStyle("padding-left")


  /**
    * The page-break-after CSS property adjusts page breaks after the current
    * element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/page-break-after
    */
  lazy val pageBreakAfter: StyleProp[String] with s.PageBreak = pageBreakStyle("page-break-after")


  /**
    * The page-break-before CSS property adjusts page breaks before the current
    * element.
    * 
    * This properties applies to block elements that generate a box. It won't
    * apply on an empty div that won't generate a box.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/page-break-before
    */
  lazy val pageBreakBefore: StyleProp[String] with s.PageBreak = pageBreakStyle("page-break-before")


  /**
    * The page-break-inside CSS property adjusts page breaks inside the current
    * element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/page-break-inside
    */
  lazy val pageBreakInside: StyleProp[String] with s.PageBreak = pageBreakStyle("page-break-inside")


  /**
    * The perspective CSS property determines the distance between the z=0 plane
    * and the user in order to give to the 3D-positioned element some perspective.
    * Each 3D element with z>0 becomes larger; each 3D-element with z<0 becomes
    * smaller. The strength of the effect is determined by the value of this
    * property.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/perspective
    */
  lazy val perspective: StyleProp[String] with s.None = noneStyle("perspective")


  /**
    * The perspective-origin CSS property determines the position the viewer is
    * looking at. It is used as the vanishing point by the perspective property.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/perspective-origin
    */
  lazy val perspectiveOrigin: StyleProp[String] = stringStyle("perspective-origin")


  /**
    * The CSS property pointer-events allows authors to control under what
    * circumstances (if any) a particular graphic element can become the target
    * of mouse events. When this property is unspecified, the same characteristics
    * of the visiblePainted value apply to SVG content.
    * 
    * In addition to indicating that the element is not the target of mouse events,
    * the value none instructs the mouse event to go "through" the element and
    * target whatever is "underneath" that element instead.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/pointer-events
    */
  lazy val pointerEvents: StyleProp[String] with s.PointerEvents = pointerEventsStyle("pointer-events")


  /**
    * The position CSS property chooses alternative rules for positioning elements,
    * designed to be useful for scripted animation effects.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/position
    */
  lazy val position: StyleProp[String] with s.Position = positionStyle("position")


  /**
    * The quotes CSS property sets how the browser should render quotation marks
    * that are added using the open-quotes or close-quotes values of the CSS
    * content property.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/quotes
    */
  lazy val quotes: StyleProp[String] = stringStyle("quotes")


  /**
    * The resize CSS property sets whether an element is resizable, and if so,
    * in which direction(s).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/resize
    */
  lazy val resize: StyleProp[String] = stringStyle("resize")


  /**
    * The right CSS property specifies part of the position of positioned elements.
    * 
    * For absolutely positioned elements (those with position: absolute or
    * position: fixed), it specifies the distance between the right margin edge
    * of the element and the right edge of its containing block.
    * 
    * The right property has no effect on non-positioned elements.
    * 
    * When both the right CSS property and the left CSS property are defined, the
    * position of the element is overspecified. In that case, the left value has
    * precedence when the container is left-to-right (that is that the right
    * computed value is set to -left), and the right value has precedence when
    * the container is right-to-left (that is that the left computed value is set
    * to -right).
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/right
    */
  lazy val right: StyleProp[String] with s.Auto with u.Length[DSP, Int] = lengthAutoStyle("right")


  /**
    * The table-layout CSS property sets the algorithm used to lay out `<table>`
    * cells, rows, and columns.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/table-layout
    */
  lazy val tableLayout: StyleProp[String] with s.TableLayout = tableLayoutStyle("table-layout")


  /**
    * The text-align CSS property describes how inline content like text is
    * aligned in its parent block element. text-align does not control the
    * alignment of block elements itself, only their inline content.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/text-align
    */
  lazy val textAlign: StyleProp[String] with s.TextAlign = textAlignStyle("text-align")


  /**
    * The text-align-last CSS property describes how the last line of a block or
    * a line, right before a forced line break, is aligned.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/text-align-last
    */
  lazy val textAlignLast: StyleProp[String] with s.TextAlign = textAlignStyle("text-align-last")


  /**
    * The text-decoration CSS property is used to set the text formatting to
    * underline, overline, line-through or blink.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/text-decoration
    */
  lazy val textDecoration: StyleProp[String] with s.TextDecoration = textDecorationStyle("text-decoration")


  /**
    * The text-indent CSS property specifies how much horizontal space should be
    * left before the beginning of the first line of the text content of an element.
    * Horizontal spacing is with respect to the left (or right, for right-to-left
    * layout) edge of the containing block element's box.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/text-indent
    */
  lazy val textIndent: StyleProp[String] = stringStyle("text-indent")


  /**
    * The text-overflow CSS property determines how overflowed content that is
    * not displayed is signaled to the users. It can be clipped, or display an
    * ellipsis ('…', U+2026 HORIZONTAL ELLIPSIS) or a Web author-defined string.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/text-overflow
    */
  lazy val textOverflow: StyleProp[String] with s.TextOverflow = textOverflowStyle("text-overflow")


  /**
    * The text-shadow CSS property adds shadows to text. It accepts a comma-separated
    * list of shadows to be applied to the text and text-decorations of the element.
    * 
    * Each shadow is specified as an offset from the text, along with optional
    * color and blur radius values.
    * 
    * Multiple shadows are applied front-to-back, with the first-specified shadow
    * on top.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/text-shadow
    */
  lazy val textShadow: StyleProp[String] with s.None = noneStyle("text-shadow")


  /**
    * The text-transform CSS property specifies how to capitalize an element's
    * text. It can be used to make text appear in all-uppercase or all-lowercase,
    * or with each word capitalized.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/text-transform
    */
  lazy val textTransform: StyleProp[String] with s.TextTransform = textTransformStyle("text-transform")


  /**
    * The CSS text-underline-position property specifies the position of the
    * underline which is set using the text-decoration property underline value.
    * 
    * This property inherits and is not reset by the text-decoration shorthand,
    * allowing to easily set it globally for a given document.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/text-underline-position
    */
  lazy val textUnderlinePosition: StyleProp[String] with s.TextUnderlinePosition = textUnderlinePositionStyle("text-underline-position")


  /**
    * The top CSS property specifies part of the position of positioned elements.
    * It has no effect on non-positioned elements.
    * 
    * For absolutely positioned elements (those with position: absolute or
    * position: fixed), it specifies the distance between the top margin edge of
    * the element and the top edge of its containing block.
    * 
    * For relatively positioned elements (those with position: relative), it
    * specifies the amount the element is moved below its normal position.
    * 
    * When both top and bottom are specified, the element position is
    * over-constrained and the top property has precedence: the computed value
    * of bottom is set to -top, while its specified value is ignored.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/top
    */
  lazy val top: StyleProp[String] with s.Auto with u.Length[DSP, Int] = lengthAutoStyle("top")


  /**
    * The CSS transform property lets you modify the coordinate space of the CSS
    * visual formatting model. Using it, elements can be translated, rotated,
    * scaled, and skewed according to the values set.
    * 
    * If the property has a value different than none, a stacking context will be
    * created. In that case the object will act as a containing block for
    * position: fixed elements that it contains.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/transform
    */
  lazy val transform: StyleProp[String] = stringStyle("transform")


  /**
    * The transform-origin CSS property lets you modify the origin for
    * transformations of an element. For example, the transform-origin of the
    * rotate() function is the centre of rotation. (This property is applied by
    * first translating the element by the negated value of the property, then
    * applying the element's transform, then translating by the property value.)
    * 
    * Not explicitly set values are reset to their corresponding values.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/transform-origin
    */
  lazy val transformOrigin: StyleProp[String] = stringStyle("transform-origin")


  /**
    * The transform-style CSS property determines if the children of the element
    * are positioned in the 3D-space or are flattened in the plane of the element.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/transform-style
    */
  lazy val transformStyle: StyleProp[String] = stringStyle("transform-style")


  /**
    * The CSS transition property is a shorthand property for transition-property,
    * transition-duration, transition-timing-function, and transition-delay. It
    * allows to define the transition between two states of an element. Different
    * states may be defined using pseudo-classes like :hover or :active or
    * dynamically set using JavaScript.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/transition
    */
  lazy val transition: StyleProp[String] = stringStyle("transition")


  /**
    * The transition-delay CSS property specifies the amount of time to wait
    * between a change being requested to a property that is to be transitioned
    * and the start of the transition effect.
    * 
    * A value of 0s, or 0ms, indicates that the property will begin to animate its
    * transition immediately when the value changes; positive values will delay
    * the start of the transition effect for the corresponding number of seconds.
    * Negative values cause the transition to begin immediately, but to cause the
    * transition to seem to begin partway through the animation effect.
    * 
    * You may specify multiple delays; each delay will be applied to the
    * corresponding property as specified by the transition-property property,
    * which acts as a master list. If there are fewer delays specified than in the
    * master list, missing values are set to the initial value (0s). If there are
    * more delays, the list is simply truncated to the right size. In both case
    * the CSS declaration stays valid.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/transition-delay
    */
  lazy val transitionDelay: StyleProp[String] with u.Time[DSP] = timeStyle("transition-delay")


  /**
    * The transition-duration CSS property specifies the number of seconds or
    * milliseconds a transition animation should take to complete. By default,
    * the value is 0s, meaning that no animation will occur.
    * 
    * You may specify multiple durations; each duration will be applied to the
    * corresponding property as specified by the transition-property property,
    * which acts as a master list. If there are fewer durations specified than in
    * the master list, the user agent repeat the list of durations. If there are
    * more durations, the list is simply truncated to the right size. In both
    * case the CSS declaration stays valid.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/transition-duration
    */
  lazy val transitionDuration: StyleProp[String] with u.Time[DSP] = timeStyle("transition-duration")


  /**
    * The CSS transition-timing-function property is used to describe how the
    * intermediate values of the CSS properties being affected by a transition
    * effect are calculated. This in essence lets you establish an acceleration
    * curve, so that the speed of the transition can vary over its duration.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/transition-timing-function
    */
  lazy val transitionTimingFunction: StyleProp[String] = stringStyle("transition-timing-function")


  /**
    * The transition-property CSS property is used to specify the names of CSS
    * properties to which a transition effect should be applied.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/transition-property
    */
  lazy val transitionProperty: StyleProp[String] = stringStyle("transition-property")


  /**
    * The unicode-bidi CSS property together with the `direction` property relates
    * to the handling of bidirectional text in a document. For example, if a block
    * of text contains both left-to-right and right-to-left text then the
    * user-agent uses a complex Unicode algorithm to decide how to display the
    * text. This property overrides this algorithm and allows the developer to
    * control the text embedding.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/unicode-bidi
    */
  lazy val unicodeBidi: StyleProp[String] = stringStyle("unicode-bidi")


  /**
    * The vertical-align CSS property specifies the vertical alignment of an
    * inline or table-cell box. It does not apply to block-level elements.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/vertical-align
    */
  lazy val verticalAlign: StyleProp[String] with s.VerticalAlign = verticalAlignStyle("vertical-align")


  /**
    * The visibility CSS property shows or hides an element without changing the
    * layout of a document. The property can also hide rows or columns in a `<table>`.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/visibility
    */
  lazy val visibility: StyleProp[String] with s.Visibility = visibilityStyle("visibility")


  /**
    * The width CSS property specifies the width of the content area of an element.
    * The content area is inside the padding, border, and margin of the element.
    * 
    * The min-width and max-width properties override width.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/width
    */
  lazy val width: StyleProp[String] with s.Auto with u.Length[DSP, Int] = lengthAutoStyle("width")


  /**
    * The white-space CSS property is used to to describe how whitespace inside
    * the element is handled.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/white-space
    */
  lazy val whiteSpace: StyleProp[String] with s.WhiteSpace = whiteSpaceStyle("white-space")


  /**
    * The widows CSS property defines how many minimum lines must be left on top
    * of a new page, on a paged media. In typography, a widow is the last line of
    * a paragraph appearing alone at the top of a page. Setting the widows property
    * allows to prevent widows to be left.
    * 
    * On a non-paged media, like screen, the widows CSS property has no effect.
    * 
    * Note: Firefox does not support this property.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/widows
    */
  lazy val widows: StyleProp[Int] = intStyle("widows")


  /**
    * The word-break CSS property specifies whether or not the browser should
    * insert line breaks wherever the text would otherwise overflow its content
    * box.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/word-break
    */
  lazy val wordBreak: StyleProp[String] with s.WordBreak = wordBreakStyle("word-break")


  /**
    * The word-spacing CSS property specifies spacing behavior between tags and
    * words.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/word-spacing
    */
  lazy val wordSpacing: StyleProp[String] with s.Normal = normalStyle("word-spacing")


  /**
    * The z-index CSS property specifies the z-order of an element and its
    * descendants. When elements overlap, z-order determines which one covers the
    * other. An element with a larger z-index generally covers an element with a
    * lower one.
    * 
    * @see https://developer.mozilla.org/en-US/docs/Web/CSS/z-index
    */
  lazy val zIndex: StyleProp[Int] with s.Auto = autoStyle("z-index")


}

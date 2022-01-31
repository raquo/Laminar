package com.raquo.laminar.builders

import com.raquo.domtypes.generic.builders.StylePropBuilder
import com.raquo.laminar.keys.{DerivedStyleProp, StyleProp}
import com.raquo.laminar.modifiers.KeySetter.StyleSetter
import com.raquo.domtypes.generic.defs.styles.{keywords, units}

trait StyleBuilders extends StylePropBuilder[StyleProp, StyleSetter, DerivedStyleProp.Base, Int] {

  // -- Basic style types --

  override protected def stringStyle(key: String): StyleProp[String] =
    new StyleProp[String](key)

  override protected def intStyle(key: String): StyleProp[Int] with units.CalcUnits[DerivedStyleProp.Base] = {
    new StyleProp[Int](key) with units.CalcUnits[DerivedStyleProp.Base]
  }

  override protected def doubleStyle(key: String): StyleProp[Double] with units.CalcUnits[DerivedStyleProp.Base] =
    new StyleProp[Double](key) with units.CalcUnits[DerivedStyleProp.Base]




  // -- Shared custom types --

  override protected def autoStyle[V](key: String): AutoStyle[V] =
    new StyleProp[V](key) with keywords.AutoStyle[StyleSetter[V]]

  override protected def colorStyle(key: String): ColorStyle =
    new StyleProp[String](key) with keywords.ColorStyle[StyleSetter[String]]

  override protected def colorUrlStyle(key: String): ColorStyle with UrlStyle =
    new StyleProp[String](key) with keywords.ColorStyle[StyleSetter[String]] with units.UrlUnits[DerivedStyleProp.Base]

  override protected def flexPositionStyle(key: String): FlexPositionStyle =
    new StyleProp[String](key) with keywords.FlexPositionStyle[StyleSetter[String]]

  override protected def lengthStyle(key: String): LengthStyle =
    new StyleProp[String](key) with units.LengthUnits[DerivedStyleProp.Base, Int]

  override protected def lengthNormalStyle(key: String): LengthStyle with NormalStyle[String] =
    new StyleProp[String](key) with units.LengthUnits[DerivedStyleProp.Base, Int] with keywords.NormalStyle[StyleSetter[String]]

  override protected def lengthAutoStyle(key: String): LengthStyle with AutoStyle[String] =
    new StyleProp[String](key) with units.LengthUnits[DerivedStyleProp.Base, Int] with keywords.AutoStyle[StyleSetter[String]]

  override protected def lineStyle(key: String): LineStyle =
    new StyleProp[String](key) with keywords.LineStyle[StyleSetter[String]]

  override protected def maxLengthStyle(key: String): MaxLengthStyle =
    new StyleProp[String](key) with keywords.MinMaxLengthStyle[StyleSetter[String], DerivedStyleProp.Base, Int] with keywords.NoneStyle[StyleSetter[String]]

  override protected def minLengthStyle(key: String): MinLengthStyle =
    new StyleProp[String](key) with keywords.MinMaxLengthStyle[StyleSetter[String], DerivedStyleProp.Base, Int] with keywords.AutoStyle[StyleSetter[String]]

  override protected def noneStyle[V](key: String): NoneStyle[V] =
    new StyleProp[V](key) with keywords.NoneStyle[StyleSetter[V]]

  override protected def normalStyle[V](key: String): NormalStyle[V] =
    new StyleProp[V](key) with keywords.NormalStyle[StyleSetter[V]]

  override protected def overflowStyle(key: String): OverflowStyle =
    new StyleProp[String](key) with keywords.OverflowStyle[StyleSetter[String]]

  override protected def paddingBoxSizingStyle(key: String): PaddingBoxSizingStyle =
    new StyleProp[String](key) with keywords.PaddingBoxSizingStyle[StyleSetter[String]]

  override protected def pageBreakStyle(key: String): PageBreakStyle =
    new StyleProp[String](key) with keywords.PageBreakStyle[StyleSetter[String]]

  override protected def textAlignStyle(key: String): TextAlignStyle =
    new StyleProp[String](key) with keywords.TextAlignStyle[StyleSetter[String]]

  override protected def timeStyle(key: String): TimeStyle =
    new StyleProp[String](key) with units.TimeUnits[DerivedStyleProp.Base]

  override protected def urlStyle(key: String): UrlStyle =
    new StyleProp[String](key) with units.UrlUnits[DerivedStyleProp.Base]

  override protected def urlNoneStyle(key: String): UrlStyle with NoneStyle[String] =
    new StyleProp[String](key) with units.UrlUnits[DerivedStyleProp.Base] with keywords.NoneStyle[StyleSetter[String]]




  // -- Unique custom types --

  override protected def alignContentStyle(key: String): AlignContentStyle =
    new StyleProp[String](key) with keywords.AlignContentStyle[StyleSetter[String]]

  override protected def backgroundAttachmentStyle(key: String): BackgroundAttachmentStyle =
    new StyleProp[String](key) with keywords.BackgroundAttachmentStyle[StyleSetter[String]]

  override protected def backgroundSizeStyle(key: String): BackgroundSizeStyle =
    new StyleProp[String](key) with keywords.BackgroundSizeStyle[StyleSetter[String], DerivedStyleProp.Base, Int]

  override protected def backfaceVisibilityStyle(key: String): BackfaceVisibilityStyle =
    new StyleProp[String](key) with keywords.BackfaceVisibilityStyle[StyleSetter[String]]

  override protected def borderCollapse(key: String): BorderCollapseStyle =
    new StyleProp[String](key) with keywords.BorderCollapseStyle[StyleSetter[String]]

  override protected def boxSizingStyle(key: String): BoxSizingStyle =
    new StyleProp[String](key) with keywords.BoxSizingStyle[StyleSetter[String]]

  override protected def clearStyle(key: String): ClearStyle =
    new StyleProp[String](key) with keywords.ClearStyle[StyleSetter[String]]

  override protected def cursorStyle(key: String): CursorStyle =
    new StyleProp[String](key) with keywords.CursorStyle[StyleSetter[String]]

  override protected def directionStyle(key: String): DirectionStyle =
    new StyleProp[String](key) with keywords.DirectionStyle[StyleSetter[String]]

  override protected def displayStyle(key: String): DisplayStyle =
    new StyleProp[String](key) with keywords.DisplayStyle[StyleSetter[String]]

  override protected def emptyCellsStyle(key: String): EmptyCellsStyle =
    new StyleProp[String](key) with keywords.EmptyCellsStyle[StyleSetter[String]]

  override protected def flexWrapStyle(key: String): FlexWrapStyle =
    new StyleProp[String](key) with keywords.FlexWrapStyle[StyleSetter[String]]

  override protected def flexDirectionStyle(key: String): FlexDirectionStyle =
    new StyleProp[String](key) with keywords.FlexDirectionStyle[StyleSetter[String]]

  override protected def floatStyle(key: String): FloatStyle =
    new StyleProp[String](key) with keywords.FloatStyle[StyleSetter[String]]

  override protected def fontSizeStyle(key: String): FontSizeStyle =
    new StyleProp[String](key) with keywords.FontSizeStyle[StyleSetter[String], DerivedStyleProp.Base, Int]

  override protected def fontStyleStyle(key: String): FontStyleStyle =
    new StyleProp[String](key) with keywords.FontStyleStyle[StyleSetter[String]]

  override protected def fontWeightStyle(key: String): FontWeightStyle =
    new StyleProp[String](key) with keywords.FontWeightStyle[StyleSetter[String]]

  override protected def justifyContentStyle(key: String): JustifyContentStyle =
    new StyleProp[String](key) with keywords.JustifyContentStyle[StyleSetter[String]]

  override protected def listStylePositionStyle(key: String): ListStylePositionStyle =
    new StyleProp[String](key) with keywords.ListStylePositionStyle[StyleSetter[String]]

  override protected def listStyleTypeStyle(key: String): ListStyleTypeStyle =
    new StyleProp[String](key) with keywords.ListStyleTypeStyle[StyleSetter[String]]

  override protected def overflowWrapStyle(key: String): OverflowWrapStyle =
    new StyleProp[String](key) with keywords.OverflowWrapStyle[StyleSetter[String]]

  override protected def pointerEventsStyle(key: String): PointerEventsStyle =
    new StyleProp[String](key) with keywords.PointerEventsStyle[StyleSetter[String]]

  override protected def positionStyle(key: String): PositionStyle =
    new StyleProp[String](key) with keywords.PositionStyle[StyleSetter[String]]

  override protected def tableLayoutStyle(key: String): TableLayoutStyle =
    new StyleProp[String](key) with keywords.TableLayoutStyle[StyleSetter[String]]

  override protected def textDecorationStyle(key: String): TextDecorationStyle =
    new StyleProp[String](key) with keywords.TextDecorationStyle[StyleSetter[String]]

  override protected def textOverflowStyle(key: String): TextOverflowStyle =
    new StyleProp[String](key) with keywords.TextOverflowStyle[StyleSetter[String]]

  override protected def textTransformStyle(key: String): TextTransformStyle =
    new StyleProp[String](key) with keywords.TextTransformStyle[StyleSetter[String]]

  override protected def textUnderlinePositionStyle(key: String): TextUnderlinePositionStyle =
    new StyleProp[String](key) with keywords.TextUnderlinePositionStyle[StyleSetter[String]]

  override protected def verticalAlignStyle(key: String): VerticalAlignStyle =
    new StyleProp[String](key) with keywords.VerticalAlignStyle[StyleSetter[String], DerivedStyleProp.Base, Int]

  override protected def visibilityStyle(key: String): VisibilityStyle =
    new StyleProp[String](key) with keywords.VisibilityStyle[StyleSetter[String]]

  override protected def whiteSpaceStyle(key: String): WhiteSpaceStyle =
    new StyleProp[String](key) with keywords.WhiteSpaceStyle[StyleSetter[String]]

  override protected def wordBreakStyle(key: String): WordBreakStyle =
    new StyleProp[String](key) with keywords.WordBreakStyle[StyleSetter[String]]
}

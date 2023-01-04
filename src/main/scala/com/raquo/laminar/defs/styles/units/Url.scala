package com.raquo.laminar.defs.styles.units

import com.raquo.laminar.keys.DerivedStyleBuilder

/** @see https://developer.mozilla.org/en-US/docs/Web/CSS/url */
trait Url[DSP[_]] { this: DerivedStyleBuilder[_, DSP] =>

  /** Provide a URL to wrap into the CSS `url()` function. */
  lazy val url: DSP[String] = derivedStyle { s =>
    s"""url(${encodeUrlValue(s)})"""
  }
}

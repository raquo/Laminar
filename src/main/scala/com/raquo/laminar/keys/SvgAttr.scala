package com.raquo.laminar.keys

import com.raquo.airstream.core.Source
import com.raquo.laminar.DomApi
import com.raquo.laminar.api.L.SvgElement
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.modifiers.SimpleKeySetter.SvgAttrSetter
import com.raquo.laminar.modifiers.SimpleKeyUpdater
import com.raquo.laminar.modifiers.SimpleKeyUpdater.SvgAttrUpdater
import com.raquo.laminar.nodes.ReactiveSvgElement

/**
  * This class represents an Svg Element Attribute. Meaning the key that can be set, not the whole a key-value pair.
  *
  * @param localName - Unqualified name, without the namespace prefix
  * @param namespacePrefix - e.g. "xlink". For namespace URI, see `namespaceUri`
  *
  * @tparam V type of values that this Attribute can be set to
  */
class SvgAttr[V](
  val localName: String,
  val codec: Codec[V, String],
  val namespacePrefix: Option[String]
) extends SimpleKey[V, String, ReactiveSvgElement.Base] {

  /** Qualified name, including namespace */
  override val name: String = namespacePrefix.map(_ + ":" + localName).getOrElse(localName)

  val namespaceUri: Option[String] = namespacePrefix.map(SvgAttr.namespaceUri)

  def :=(value: V): SvgAttrSetter[V] = {
    // new KeySetter[SvgAttr[V], V, String, SvgElement](this, value, DomApi.setSvgAttribute)
    new SvgAttrSetter(this, value)
  }

  def <--(values: Source[V]): SvgAttrUpdater[V] = {
    new SimpleKeyUpdater[SvgElement, SvgAttr[V], V](
      key = this,
      values = values.toObservable,
      update = (el, v, _) => DomApi.setSvgAttribute(el, this, v)
    )
  }

}

object SvgAttr {

  // For SVG namespaces info see https://developer.mozilla.org/en-US/docs/Web/SVG/Namespaces_Crash_Course

  final def namespaceUri(namespace: String): String = {
    namespace match {
      case "svg" => svgNamespaceUri
      case "xlink" => xlinkNamespaceUri
      case "xml" => xmlNamespaceUri
      case "xmlns" => xmlnsNamespaceUri
    }
  }

  final val svgNamespaceUri: String = "http://www.w3.org/2000/svg"

  final val xlinkNamespaceUri: String = "http://www.w3.org/1999/xlink"

  final val xmlNamespaceUri: String = "http://www.w3.org/XML/1998/namespace"

  final val xmlnsNamespaceUri: String = "http://www.w3.org/2000/xmlns/"
}

package com.raquo.laminar.keys

import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.domapi.DomApi
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
  override val localName: String,
  override val codec: Codec[V, String],
  val namespacePrefix: Option[String]
) extends SimpleAttr[SvgAttr[V], V, ReactiveSvgElement.Base] {

  /** Qualified name, including namespace */
  override val name: String = namespacePrefix.map(_ + ":" + localName).getOrElse(localName)

  override val namespaceUri: Option[String] = namespacePrefix.map(DomApi.namespaceUri)

  override lazy val maybe: SvgAttr[Option[V]] = {
    new SvgAttr[Option[V]](localName, codec.optAsNull, namespacePrefix)
  }
}

object SvgAttr {

  // For SVG namespaces info see https://developer.mozilla.org/en-US/docs/Web/SVG/Namespaces_Crash_Course

  @deprecated("Moved to DomApi.namespaceUri", "18.0.0-M1")
  final def namespaceUri(namespace: String): String = DomApi.namespaceUri(namespace)

  @deprecated("Moved to DomApi.svgNamespaceUri", "18.0.0-M1")
  final def svgNamespaceUri: String = "http://www.w3.org/2000/svg"

  @deprecated("Moved to DomApi.xlinkNamespaceUri", "18.0.0-M1")
  final def xlinkNamespaceUri: String = "http://www.w3.org/1999/xlink"

  @deprecated("Moved to DomApi.xmlNamespaceUri", "18.0.0-M1")
  final def xmlNamespaceUri: String = "http://www.w3.org/XML/1998/namespace"

  @deprecated("Moved to DomApi.xmlnsNamespaceUri", "18.0.0-M1")
  final def xmlnsNamespaceUri: String = "http://www.w3.org/2000/xmlns/"
}

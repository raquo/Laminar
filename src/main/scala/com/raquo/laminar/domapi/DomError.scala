package com.raquo.laminar.domapi

import org.scalajs.dom

final class DomError(val jsDomException: dom.DOMException)
extends Exception(s"${jsDomException.name}: ${jsDomException.message}")

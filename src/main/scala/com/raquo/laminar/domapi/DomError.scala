package com.raquo.laminar.domapi

import org.scalajs.dom

final class DomError(val domException: dom.DOMException)
extends Exception(s"${domException.name}: ${domException.message}")

package com.raquo.laminar.defs.complex

import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.keys.{CompositeAttr, HtmlAttr}
import com.raquo.laminar.nodes.ReactiveElement

trait ComplexGlobalKeys {

  /**
    * This attribute is a list of the classes of the element.
    * Classes allow CSS and JavaScript to select and access specific elements
    * via the class selectors or functions like the DOM method
    * document.getElementsByClassName
    *
    * See: [[https://developer.mozilla.org/en-US/docs/Web/HTML/Reference/Global_attributes/class @ MDN]]
    */
  val className: CompositeAttr.Base = compositeAttr("class", separator = " ")

  val cls: CompositeAttr.Base = className

  /**
    * The attribute describes the role(s) the current element plays in the
    * context of the document. This can be used, for example,
    * by applications and assistive technologies to determine the purpose of
    * an element. This could allow a user to make informed decisions on which
    * actions may be taken on an element and activate the selected action in a
    * device independent way. It could also be used as a mechanism for
    * annotating portions of a document in a domain specific way (e.g.,
    * a legal term taxonomy). Although the role attribute may be used to add
    * semantics to an element, authors should use elements with inherent
    * semantics, such as p, rather than layering semantics on semantically
    * neutral elements, such as div role="paragraph".
    *
    * See: [[http://www.w3.org/TR/role-attribute/#s_role_module_attributes @ W3.org]]
    */
  lazy val role: CompositeAttr.Base = compositeAttr("role", separator = " ")

  /**
    * This class of attributes, called custom data attributes, allows proprietary
    * information to be exchanged between the HTML and its DOM representation that
    * may be used by scripts. All such custom data are available via the HTMLElement
    * interface of the element the attribute is set on. The HTMLElement.dataset
    * property gives access to them.
    *
    * The `suffix` is subject to the following restrictions:
    *
    * must not start with xml, whatever case is used for these letters;
    * must not contain any semicolon (U+003A);
    * must not contain capital A to Z letters.
    *
    * Note that the HTMLElement.dataset attribute is a StringMap and the name of the
    * custom data attribute data-test-value will be accessible via
    * HTMLElement.dataset.testValue as any dash (U+002D) is replaced by the capitalization
    * of the next letter (camelcase).
    */
  def dataAttr(suffix: String): HtmlAttr[String] = new HtmlAttr(s"data-$suffix", Codec.stringAsIs)

  protected def compositeAttr(name: String, separator: String): CompositeAttr.Base = {
    new CompositeAttr[ReactiveElement.Base](name, separator)
  }
}



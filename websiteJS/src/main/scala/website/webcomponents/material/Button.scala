package website.webcomponents.material

import com.raquo.laminar.api.L._
import com.raquo.laminar.codecs.{BooleanAsAttrPresenceCodec, StringAsIsCodec}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.tags.HtmlTag
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object Button {

  @js.native
  trait RawElement extends js.Object {
    def doThing(): Unit // Note: This is not actually implemented in mwc-button, just an example
  }

  @js.native
  @JSImport("@material/mwc-button", JSImport.Default)
  object RawImport extends js.Object

  // object-s are lazy so you need to actually use them in your code to prevent dead code elimination
  RawImport

  type Ref = dom.html.Element with RawElement
  type ModFunction = Button.type => Mod[ReactiveHtmlElement[Ref]]

  private val tag: HtmlTag[Ref] = htmlTag("mwc-button")

  val id: HtmlProp[String] = idAttr

  val label: HtmlAttr[String]   = htmlAttr("label", StringAsIsCodec)
  val raised: HtmlAttr[Boolean] = htmlAttr("raised", BooleanAsAttrPresenceCodec)
  val icon: HtmlAttr[String]    = htmlAttr("icon", StringAsIsCodec)

  val onMouseOver = new EventProp[dom.MouseEvent]("mouseover")

  object slots {
    def icon(el: HtmlElement): HtmlElement = el.amend(slot := "icon")
  }

  object styles {
    val mdcThemePrimary: StyleProp[String] = styleProp("--mdc-theme-primary")
  }

  def apply(mods: ModFunction*): HtmlElement = {
    tag(mods.map(_(Button)): _*)
  }

}

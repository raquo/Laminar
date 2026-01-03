package website.webcomponents.material

import com.raquo.laminar.api.L._
import com.raquo.laminar.codecs.Codec
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.tags.HtmlTag
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object Slider {

  @js.native
  trait RawElement extends js.Object {
    def layout(): Unit
    def value: Double // is this correct? do we need to define properties twice?
  }

  @js.native
  @JSImport("@material/mwc-slider", JSImport.Default)
  object RawImport extends js.Object

  // object-s are lazy so you need to actually use them in your code to prevent dead code elimination
  RawImport

  type Ref = dom.html.Element with RawElement
  type El = ReactiveHtmlElement[Ref]
  type ModFunction = Slider.type => Mod[El]

  private val tag: HtmlTag[Ref] = htmlTag("mwc-slider")

  val pin: HtmlProp[Boolean]     = htmlProp("pin", Some("pin"), Codec.booleanAsIs)
  val markers: HtmlProp[Boolean] = htmlProp("markers", Some("markers"), Codec.booleanAsIs)
  val value: HtmlProp[Double]     = htmlProp("value", Some("value"), Codec.doubleAsIs)
  val min: HtmlProp[Double]       = htmlProp("min", Some("min"), Codec.doubleAsIs)
  val max: HtmlProp[Double]       = htmlProp("max", Some("max"), Codec.doubleAsIs)
  val step: HtmlProp[Double]      = htmlProp("step", Some("step"), Codec.doubleAsIs)


  val onInput: EventProp[dom.Event]  = eventProp("input")
  val onChange: EventProp[dom.Event] = eventProp("change")

  object styles {
    val mdcThemeSecondary: StyleProp[String] = styleProp("--mdc-theme-secondary")
  }

  def apply(mods: ModFunction*): HtmlElement = {
    tag(mods.map(_(Slider)): _*)
  }

  //def inContext(makeMod: El => ModFunction): Mod[El] = {
  //  L.inContext { thisNode => makeMod(thisNode)(this) }
  //}
}

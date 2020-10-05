package website.webcomponents.material

import com.raquo.domtypes.generic.codecs._
import com.raquo.laminar.api.L._
import com.raquo.laminar.api.L
import com.raquo.laminar.builders.HtmlTag
import com.raquo.laminar.keys.{ReactiveProp, ReactiveStyle}
import com.raquo.laminar.nodes.ReactiveHtmlElement
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

  // object-s are lazy so you need to actually use them in your code
  private val _ = RawImport

  type Ref = dom.html.Element with RawElement
  type El = ReactiveHtmlElement[Ref]
  type ModFunction = Slider.type => Mod[El]

  private val tag = new HtmlTag[Ref]("mwc-slider", void = false)

  val pin = new ReactiveProp("pin", BooleanAsIsCodec)
  val markers = new ReactiveProp("markers", BooleanAsIsCodec)
  val value = new ReactiveProp("value", DoubleAsIsCodec)
  val min = new ReactiveProp("min", DoubleAsIsCodec)
  val max = new ReactiveProp("max", DoubleAsIsCodec)
  val step = new ReactiveProp("step", DoubleAsIsCodec)


  val onInput = new EventProp[dom.Event]("input")
  val onChange = new EventProp[dom.Event]("change")

  object styles {
    import com.raquo.domtypes.generic.keys.Style // Laminar aliases ReactiveStyle as Style, but we want the original underlying type here

    val mdcThemeSecondary = new ReactiveStyle(new Style("--mdc-theme-secondary", "--mdc-theme-secondary"))
  }

  def apply(mods: ModFunction*): HtmlElement = {
    tag(mods.map(_(Slider)): _*)
  }

  //def inContext(makeMod: El => ModFunction): Mod[El] = {
  //  L.inContext { thisNode => makeMod(thisNode)(this) }
  //}
}

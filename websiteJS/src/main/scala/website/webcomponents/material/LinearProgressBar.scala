package website.webcomponents.material

import com.raquo.domtypes.generic.codecs._
import com.raquo.laminar.api.L._
import com.raquo.laminar.builders.HtmlTag
import com.raquo.laminar.keys.ReactiveStyle
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

object LinearProgressBar {

  @js.native
  trait RawElement extends js.Object {
    def open(): Unit
    def close(): Unit
  }

  @js.native
  @JSImport("@material/mwc-linear-progress", JSImport.Default)
  object RawImport extends js.Object

  // object-s are lazy so you need to actually use them in your code to prevent dead code elimination
  RawImport

  type Ref = dom.html.Element with RawElement
  type ModFunction = LinearProgressBar.type => Mod[ReactiveHtmlElement[Ref]]

  private val tag: HtmlTag[Ref] = customHtmlTag("mwc-linear-progress")

  val indeterminate: Prop[Boolean] = customProp("indeterminate", BooleanAsIsCodec)
  val reverse: Prop[Boolean]       = customProp("reverse", BooleanAsIsCodec)
  val closed: Prop[Boolean]        = customProp("closed", BooleanAsIsCodec)
  val progress: Prop[Double]       = customProp("progress", DoubleAsIsCodec)
  val buffer: Prop[Double]         = customProp("buffer", DoubleAsIsCodec)

  object styles {
    val mdcThemePrimary: ReactiveStyle[String] = customStyle("--mdc-theme-primary")
  }

  def apply(mods: ModFunction*): HtmlElement = {
    tag(mods.map(_(LinearProgressBar)): _*)
  }
}

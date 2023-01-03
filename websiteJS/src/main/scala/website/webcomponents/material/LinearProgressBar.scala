package website.webcomponents.material

import com.raquo.laminar.api.L._
import com.raquo.laminar.codecs.{BooleanAsIsCodec, DoubleAsIsCodec}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.tags.HtmlTag
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

  private val tag: HtmlTag[Ref] = htmlTag("mwc-linear-progress")

  val indeterminate: HtmlProp[Boolean] = htmlProp("indeterminate", BooleanAsIsCodec)
  val reverse: HtmlProp[Boolean]       = htmlProp("reverse", BooleanAsIsCodec)
  val closed: HtmlProp[Boolean]        = htmlProp("closed", BooleanAsIsCodec)
  val progress: HtmlProp[Double]       = htmlProp("progress", DoubleAsIsCodec)
  val buffer: HtmlProp[Double]         = htmlProp("buffer", DoubleAsIsCodec)

  object styles {
    val mdcThemePrimary: StyleProp[String] = styleProp("--mdc-theme-primary")
  }

  def apply(mods: ModFunction*): HtmlElement = {
    tag(mods.map(_(LinearProgressBar)): _*)
  }
}

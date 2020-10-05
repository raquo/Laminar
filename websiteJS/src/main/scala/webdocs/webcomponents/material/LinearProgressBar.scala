package webdocs.webcomponents.material

import com.raquo.domtypes.generic.codecs._
import com.raquo.laminar.api.L._
import com.raquo.laminar.builders.HtmlTag
import com.raquo.laminar.keys.{ReactiveHtmlAttr, ReactiveProp, ReactiveStyle}
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

  // object-s are lazy so you need to actually use them in your code
  private val _ = RawImport

  type Ref = dom.html.Element with RawElement
  type ModFunction = LinearProgressBar.type => Mod[ReactiveHtmlElement[Ref]]

  private val tag = new HtmlTag[Ref]("mwc-linear-progress", void = false)

  val indeterminate = new ReactiveProp("indeterminate", BooleanAsIsCodec)
  val reverse = new ReactiveProp("reverse", BooleanAsIsCodec)
  val closed = new ReactiveProp("closed", BooleanAsIsCodec)
  val progress = new ReactiveProp("progress", DoubleAsIsCodec)
  val buffer = new ReactiveProp("buffer", DoubleAsIsCodec)

  object styles {
    import com.raquo.domtypes.generic.keys.Style // Laminar aliases ReactiveStyle as Style, but we want the original underlying type here

    val mdcThemePrimary = new ReactiveStyle(new Style("--mdc-theme-primary", "--mdc-theme-primary"))
  }

  def apply(mods: ModFunction*): HtmlElement = {
    tag(mods.map(_(LinearProgressBar)): _*)
  }
}

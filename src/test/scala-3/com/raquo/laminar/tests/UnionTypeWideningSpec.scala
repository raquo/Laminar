package com.raquo.laminar.tests

import com.raquo.laminar.api.L.*
import com.raquo.laminar.codecs.{Codec, StringAsIsCodec}
import com.raquo.laminar.utils.UnitSpec

/** Test that string literal union types work correctly with the `<--` operator.
  *
  * This tests a fix for Scala 3 type inference widening string literal unions
  * to String when using implicit evidence functions.
  */
class UnionTypeWideningSpec extends UnitSpec {

  it("string literal union types with typed HtmlAttr") {
    // This is the key test case: HtmlAttr typed with union type directly
    // Before the fix, the compiler would widen ThemeVariant to String,
    // then fail to find implicit evidence String => ThemeVariant

    type ThemeVariant = "brand" | "danger" | "neutral" | "success" | "warning"

    def UnionAsStringCodec[U]: Codec[U, String] =
      StringAsIsCodec.asInstanceOf[Codec[U, String]]

    val variant: HtmlAttr[ThemeVariant] =
      htmlAttr("variant", UnionAsStringCodec[ThemeVariant])

    val themeVar = Var[ThemeVariant]("brand")
    val themeSignal: Signal[ThemeVariant] = themeVar.signal

    val el = div(
      variant <-- themeSignal
    )

    mount(el)

    el.ref.getAttribute("variant") shouldBe "brand"

    themeVar.set("success")

    el.ref.getAttribute("variant") shouldBe "success"
  }
}

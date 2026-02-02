package com.raquo.laminar.tests

import com.raquo.laminar.api.L.*
import com.raquo.laminar.codecs.{Codec, StringAsIsCodec}
import com.raquo.laminar.utils.UnitSpec

/** Test that string literal union types work correctly with the `<--` operator.
  *
  * This tests a fix for Scala 3 type inference widening string literal unions
  * to String when using implicit evidence functions.
  */
class StringLiteralTypeWideningSpec extends UnitSpec {

  // Shared codec for union types
  def UnionAsStringCodec[U]: Codec[U, String] =
    Codec.stringAsIs.asInstanceOf[Codec[U, String]]

  type ThemeVariant = "brand" | "danger" | "neutral" | "success" | "warning"

  it("string literal union types with typed HtmlAttr") {
    // Key test case: HtmlAttr typed with union type directly
    // Before the fix, the compiler would widen ThemeVariant to String,
    // then fail to find implicit evidence String => ThemeVariant

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

  it("inline conditional map producing union type") {
    // Test case from @raquo: inline conditional that produces union type
    val variant: HtmlAttr[ThemeVariant] =
      htmlAttr("variant", UnionAsStringCodec[ThemeVariant])

    val boolVar = Var(true)

    val el = div(
      variant <-- boolVar.signal.map(if (_) "neutral" else "warning")
    )

    mount(el)

    el.ref.getAttribute("variant") shouldBe "neutral"

    boolVar.set(false)

    el.ref.getAttribute("variant") shouldBe "warning"
  }

  it("mixed Int | String union types") {
    // Test with non-string-literal unions like zIndex accepts
    val intVar = Var[Int | String](1)

    val el = div(
      zIndex <-- intVar.signal
    )

    mount(el)

    el.ref.style.zIndex shouldBe "1"

    intVar.set("auto")

    el.ref.style.zIndex shouldBe "auto"

    intVar.set(999)

    el.ref.style.zIndex shouldBe "999"
  }

  it("EventStream with union types") {
    val variant: HtmlAttr[ThemeVariant] =
      htmlAttr("variant", UnionAsStringCodec[ThemeVariant])

    val themeBus = new EventBus[ThemeVariant]

    val el = div(
      variant <-- themeBus.events
    )

    mount(el)

    el.ref.getAttribute("variant") shouldBe null

    themeBus.emit("danger")

    el.ref.getAttribute("variant") shouldBe "danger"

    themeBus.emit("success")

    el.ref.getAttribute("variant") shouldBe "success"
  }

  it("Val (constant signal) with union types") {
    val variant: HtmlAttr[ThemeVariant] =
      htmlAttr("variant", UnionAsStringCodec[ThemeVariant])

    val el = div(
      variant <-- Val("brand": ThemeVariant)
    )

    mount(el)

    el.ref.getAttribute("variant") shouldBe "brand"
  }

  it("chained signal transformations with union types") {
    val variant: HtmlAttr[ThemeVariant] =
      htmlAttr("variant", UnionAsStringCodec[ThemeVariant])

    val themeVar = Var[ThemeVariant]("brand")

    // Chain of transformations that preserves union type
    val transformed: Signal[ThemeVariant] = themeVar.signal
      .map(identity)
      .map { v => if v == "brand" then "success" else v }

    val el = div(
      variant <-- transformed
    )

    mount(el)

    el.ref.getAttribute("variant") shouldBe "success"

    themeVar.set("danger")

    el.ref.getAttribute("variant") shouldBe "danger"
  }
}

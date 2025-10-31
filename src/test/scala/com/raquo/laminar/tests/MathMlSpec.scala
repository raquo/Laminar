package com.raquo.laminar.tests

import com.raquo.laminar.api._
import com.raquo.laminar.api.L.mathml._
import com.raquo.laminar.domapi.DomApi
import com.raquo.laminar.utils.UnitSpec

class MathMlSpec extends UnitSpec {

  // #Note JSDOM testing lib does not really support MathML.
  //  We're basically only testing Laminar syntax / compilation here.
  // https://github.com/jsdom/jsdom/issues/3515

  it("quadratic formula") {

    val el =
      mathTag(
        xmlns(DomApi.mathmlNamespaceUri),
        display("block"),
        mrow(
          mi("x"),
          mo(symbols.equalsSign),
          mfrac(
            mrow(
              mrow(
                mo(symbols.minus),
                mi("b"),
              ),
              mo(symbols.plusMinus),
              msqrt(
                mrow(
                  msup(
                    mi("b"),
                    mn(2)
                  ),
                  mo(symbols.minus),
                  mrow(
                    mn(4),
                    mo(symbols.invisibleTimes),
                    mi("a"),
                    mo(symbols.invisibleTimes),
                    mi("c")
                  )
                )
              )
            ),
            mrow(
              mn(2),
              mo(symbols.invisibleTimes),
              mi("a")
            )
          )
        )
      )

    mount(el)

    expectNode(
      mathTag of (
        xmlns is DomApi.mathmlNamespaceUri,
        display is "block",
        mrow of (
          mi of ("x"),
          mo of (symbols.equalsSign),
          mfrac of (
            mrow of (
              mrow of (
                mo of (symbols.minus),
                mi of ("b"),
              ),
              mo of (symbols.plusMinus),
              msqrt of (
                mrow of (
                  msup of (
                    mi of ("b"),
                    mn of ("2")
                  ),
                  mo of (symbols.minus),
                  mrow of (
                    mn of ("4"),
                    mo of (symbols.invisibleTimes),
                    mi of ("a"),
                    mo of (symbols.invisibleTimes),
                    mi of ("c")
                  )
                )
              )
            ),
            mrow of (
              mn of ("2"),
              mo of (symbols.invisibleTimes),
              mi of ("a")
            )
          )
        )
      )
    )
  }

  it("dynamic") {

    val powerVar = L.Var(2)

    val displayStyleVar = L.Var(true)

    val operatorElVar = L.Var(
      mo(symbols.minus)
    )

    val el =
      msqrt(
        mrow(
          displayStyle <-- displayStyleVar,
          msup(
            mi("a"),
            mn(L.text <-- powerVar.signal)
          ),
          L.child <-- operatorElVar.signal.map(identity),
          mi("b")
        )
      )

    mount(el)

    expectNode(
      msqrt of (
        mrow of (
          displayStyle is true,
          msup of (
            mi of ("a"),
            mn of ("2")
          ),
          sentinel,
          mo of (symbols.minus),
          mi of ("b")
        )
      )
    )

    // --

    powerVar.set(3)

    displayStyleVar.set(false)

    operatorElVar.set(
      mo(symbols.plus)
    )

    expectNode(
      msqrt of (
        mrow of (
          displayStyle is false,
          msup of (
            mi of ("a"),
            mn of ("3")
          ),
          sentinel,
          mo of (symbols.plus),
          mi of ("b")
        )
      )
    )

  }
}

package bench

import com.raquo.laminar.api.L.{*, given}

object HeavyCombine03 {

  // ~8-10 Var declarations with different types
  val varStr_03_1 = Var("initial")
  val varInt_03_1 = Var(0)
  val varDbl_03_1 = Var(0.0)
  val varBool_03_1 = Var(false)
  val varStr_03_2 = Var("second")
  val varInt_03_2 = Var(42)
  val varDbl_03_2 = Var(3.14)
  val varBool_03_2 = Var(true)
  val varOpt_03 = Var(Option.empty[String])
  val varList_03 = Var(List.empty[Int])

  // Arity-2 combineWith chains (~5 combinations)
  val combined2a_03: Signal[(String, Int)] = varStr_03_1.signal.combineWith(varInt_03_1.signal)
  val combined2b_03: Signal[(Int, Double)] = varInt_03_1.signal.combineWith(varDbl_03_1.signal)
  val combined2c_03: Signal[(Boolean, String)] = varBool_03_1.signal.combineWith(varStr_03_2.signal)
  val combined2d_03: Signal[(Double, Boolean)] = varDbl_03_2.signal.combineWith(varBool_03_2.signal)
  val combined2e_03: Signal[(String, Boolean)] = varStr_03_1.signal.combineWith(varBool_03_1.signal)

  // Arity-3 combineWith chains (~5 combinations)
  val combined3a_03: Signal[(String, Int, Double)] = varStr_03_1.signal.combineWith(varInt_03_1.signal, varDbl_03_1.signal)
  val combined3b_03: Signal[(Int, Double, Boolean)] = varInt_03_1.signal.combineWith(varDbl_03_1.signal, varBool_03_1.signal)
  val combined3c_03: Signal[(Boolean, String, Int)] = varBool_03_1.signal.combineWith(varStr_03_2.signal, varInt_03_2.signal)
  val combined3d_03: Signal[(String, Int, Boolean)] = varStr_03_2.signal.combineWith(varInt_03_2.signal, varBool_03_2.signal)
  val combined3e_03: Signal[(Double, String, Int)] = varDbl_03_1.signal.combineWith(varStr_03_1.signal, varInt_03_1.signal)

  // Arity-4 combineWith chains (~4 combinations)
  val combined4a_03: Signal[(String, Int, Double, Boolean)] = varStr_03_1.signal.combineWith(varInt_03_1.signal, varDbl_03_1.signal, varBool_03_1.signal)
  val combined4b_03: Signal[(Int, Double, Boolean, String)] = varInt_03_1.signal.combineWith(varDbl_03_1.signal, varBool_03_1.signal, varStr_03_2.signal)
  val combined4c_03: Signal[(Boolean, String, Int, Double)] = varBool_03_1.signal.combineWith(varStr_03_2.signal, varInt_03_2.signal, varDbl_03_2.signal)
  val combined4d_03: Signal[(Double, Boolean, String, Int)] = varDbl_03_1.signal.combineWith(varBool_03_2.signal, varStr_03_1.signal, varInt_03_1.signal)

  // Arity-5 combineWith chains (~3 combinations)
  val combined5a_03: Signal[(String, Int, Double, Boolean, String)] = varStr_03_1.signal.combineWith(varInt_03_1.signal, varDbl_03_1.signal, varBool_03_1.signal, varStr_03_2.signal)
  val combined5b_03: Signal[(Int, Double, Boolean, String, Int)] = varInt_03_1.signal.combineWith(varDbl_03_1.signal, varBool_03_1.signal, varStr_03_2.signal, varInt_03_2.signal)
  val combined5c_03: Signal[(Double, Boolean, String, Int, Double)] = varDbl_03_1.signal.combineWith(varBool_03_1.signal, varStr_03_1.signal, varInt_03_1.signal, varDbl_03_2.signal)

  // Mapped combinations to trigger more type inference
  val mapped2_03: Signal[String] = combined2a_03.map { case (s, i) => s"$s-$i" }
  val mapped3_03: Signal[String] = combined3a_03.map { case (s, i, d) => s"$s-$i-$d" }
  val mapped4_03: Signal[String] = combined4a_03.map { case (s, i, d, b) => s"$s-$i-$d-$b" }
  val mapped5_03: Signal[String] = combined5a_03.map { case (s, i, d, b, s2) => s"$s-$i-$d-$b-$s2" }

  // Chained combineWith (combine results of combines)
  val chained2_03 = combined2a_03.combineWith(combined2b_03)
  val chained3_03 = combined3a_03.combineWith(combined3b_03)
  val chained4_03 =
    chained2_03.combineWith(combined2c_03.combineWith(combined2d_03))

  // Render function using combineWith results
  def render03: HtmlElement = {
    div(
      child.text <-- mapped2_03,
      child.text <-- mapped3_03,
      child.text <-- mapped4_03,
      child.text <-- mapped5_03,
      child.text <-- chained2_03.map(_.toString),
      child.text <-- chained3_03.map(_.toString),

      // Inline combineWith in element context
      child.text <-- varStr_03_1.signal.combineWith(varInt_03_1.signal).map { case (s, i) => s"inline-$s-$i" },
      child.text <-- varStr_03_1.signal.combineWith(varInt_03_1.signal, varDbl_03_1.signal).map { case (s, i, d) => s"inline-$s-$i-$d" },
      child.text <-- varStr_03_1.signal.combineWith(varInt_03_1.signal, varDbl_03_1.signal, varBool_03_1.signal).map { case (s, i, d, b) => s"inline-$s-$i-$d-$b" },
    )
  }
}

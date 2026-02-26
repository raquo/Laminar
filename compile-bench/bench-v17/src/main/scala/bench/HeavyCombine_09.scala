package bench

import com.raquo.laminar.api.L.{*, given}

object HeavyCombine09 {

  // ~8-10 Var declarations with different types
  val varStr_09_1 = Var("initial")
  val varInt_09_1 = Var(0)
  val varDbl_09_1 = Var(0.0)
  val varBool_09_1 = Var(false)
  val varStr_09_2 = Var("second")
  val varInt_09_2 = Var(42)
  val varDbl_09_2 = Var(3.14)
  val varBool_09_2 = Var(true)
  val varOpt_09 = Var(Option.empty[String])
  val varList_09 = Var(List.empty[Int])

  // Arity-2 combineWith chains (~5 combinations)
  val combined2a_09: Signal[(String, Int)] = varStr_09_1.signal.combineWith(varInt_09_1.signal)
  val combined2b_09: Signal[(Int, Double)] = varInt_09_1.signal.combineWith(varDbl_09_1.signal)
  val combined2c_09: Signal[(Boolean, String)] = varBool_09_1.signal.combineWith(varStr_09_2.signal)
  val combined2d_09: Signal[(Double, Boolean)] = varDbl_09_2.signal.combineWith(varBool_09_2.signal)
  val combined2e_09: Signal[(String, Boolean)] = varStr_09_1.signal.combineWith(varBool_09_1.signal)

  // Arity-3 combineWith chains (~5 combinations)
  val combined3a_09: Signal[(String, Int, Double)] = varStr_09_1.signal.combineWith(varInt_09_1.signal, varDbl_09_1.signal)
  val combined3b_09: Signal[(Int, Double, Boolean)] = varInt_09_1.signal.combineWith(varDbl_09_1.signal, varBool_09_1.signal)
  val combined3c_09: Signal[(Boolean, String, Int)] = varBool_09_1.signal.combineWith(varStr_09_2.signal, varInt_09_2.signal)
  val combined3d_09: Signal[(String, Int, Boolean)] = varStr_09_2.signal.combineWith(varInt_09_2.signal, varBool_09_2.signal)
  val combined3e_09: Signal[(Double, String, Int)] = varDbl_09_1.signal.combineWith(varStr_09_1.signal, varInt_09_1.signal)

  // Arity-4 combineWith chains (~4 combinations)
  val combined4a_09: Signal[(String, Int, Double, Boolean)] = varStr_09_1.signal.combineWith(varInt_09_1.signal, varDbl_09_1.signal, varBool_09_1.signal)
  val combined4b_09: Signal[(Int, Double, Boolean, String)] = varInt_09_1.signal.combineWith(varDbl_09_1.signal, varBool_09_1.signal, varStr_09_2.signal)
  val combined4c_09: Signal[(Boolean, String, Int, Double)] = varBool_09_1.signal.combineWith(varStr_09_2.signal, varInt_09_2.signal, varDbl_09_2.signal)
  val combined4d_09: Signal[(Double, Boolean, String, Int)] = varDbl_09_1.signal.combineWith(varBool_09_2.signal, varStr_09_1.signal, varInt_09_1.signal)

  // Arity-5 combineWith chains (~3 combinations)
  val combined5a_09: Signal[(String, Int, Double, Boolean, String)] = varStr_09_1.signal.combineWith(varInt_09_1.signal, varDbl_09_1.signal, varBool_09_1.signal, varStr_09_2.signal)
  val combined5b_09: Signal[(Int, Double, Boolean, String, Int)] = varInt_09_1.signal.combineWith(varDbl_09_1.signal, varBool_09_1.signal, varStr_09_2.signal, varInt_09_2.signal)
  val combined5c_09: Signal[(Double, Boolean, String, Int, Double)] = varDbl_09_1.signal.combineWith(varBool_09_1.signal, varStr_09_1.signal, varInt_09_1.signal, varDbl_09_2.signal)

  // Mapped combinations to trigger more type inference
  val mapped2_09: Signal[String] = combined2a_09.map { case (s, i) => s"$s-$i" }
  val mapped3_09: Signal[String] = combined3a_09.map { case (s, i, d) => s"$s-$i-$d" }
  val mapped4_09: Signal[String] = combined4a_09.map { case (s, i, d, b) => s"$s-$i-$d-$b" }
  val mapped5_09: Signal[String] = combined5a_09.map { case (s, i, d, b, s2) => s"$s-$i-$d-$b-$s2" }

  // Chained combineWith (combine results of combines)
  val chained2_09 = combined2a_09.combineWith(combined2b_09)
  val chained3_09 = combined3a_09.combineWith(combined3b_09)
  val chained4_09 =
    chained2_09.combineWith(combined2c_09.combineWith(combined2d_09))

  // Render function using combineWith results
  def render09: HtmlElement = {
    div(
      child.text <-- mapped2_09,
      child.text <-- mapped3_09,
      child.text <-- mapped4_09,
      child.text <-- mapped5_09,
      child.text <-- chained2_09.map(_.toString),
      child.text <-- chained3_09.map(_.toString),

      // Inline combineWith in element context
      child.text <-- varStr_09_1.signal.combineWith(varInt_09_1.signal).map { case (s, i) => s"inline-$s-$i" },
      child.text <-- varStr_09_1.signal.combineWith(varInt_09_1.signal, varDbl_09_1.signal).map { case (s, i, d) => s"inline-$s-$i-$d" },
      child.text <-- varStr_09_1.signal.combineWith(varInt_09_1.signal, varDbl_09_1.signal, varBool_09_1.signal).map { case (s, i, d, b) => s"inline-$s-$i-$d-$b" },
    )
  }
}

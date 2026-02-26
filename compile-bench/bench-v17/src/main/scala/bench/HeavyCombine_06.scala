package bench

import com.raquo.laminar.api.L.{*, given}

object HeavyCombine06 {

  // ~8-10 Var declarations with different types
  val varStr_06_1 = Var("initial")
  val varInt_06_1 = Var(0)
  val varDbl_06_1 = Var(0.0)
  val varBool_06_1 = Var(false)
  val varStr_06_2 = Var("second")
  val varInt_06_2 = Var(42)
  val varDbl_06_2 = Var(3.14)
  val varBool_06_2 = Var(true)
  val varOpt_06 = Var(Option.empty[String])
  val varList_06 = Var(List.empty[Int])

  // Arity-2 combineWith chains (~5 combinations)
  val combined2a_06: Signal[(String, Int)] = varStr_06_1.signal.combineWith(varInt_06_1.signal)
  val combined2b_06: Signal[(Int, Double)] = varInt_06_1.signal.combineWith(varDbl_06_1.signal)
  val combined2c_06: Signal[(Boolean, String)] = varBool_06_1.signal.combineWith(varStr_06_2.signal)
  val combined2d_06: Signal[(Double, Boolean)] = varDbl_06_2.signal.combineWith(varBool_06_2.signal)
  val combined2e_06: Signal[(String, Boolean)] = varStr_06_1.signal.combineWith(varBool_06_1.signal)

  // Arity-3 combineWith chains (~5 combinations)
  val combined3a_06: Signal[(String, Int, Double)] = varStr_06_1.signal.combineWith(varInt_06_1.signal, varDbl_06_1.signal)
  val combined3b_06: Signal[(Int, Double, Boolean)] = varInt_06_1.signal.combineWith(varDbl_06_1.signal, varBool_06_1.signal)
  val combined3c_06: Signal[(Boolean, String, Int)] = varBool_06_1.signal.combineWith(varStr_06_2.signal, varInt_06_2.signal)
  val combined3d_06: Signal[(String, Int, Boolean)] = varStr_06_2.signal.combineWith(varInt_06_2.signal, varBool_06_2.signal)
  val combined3e_06: Signal[(Double, String, Int)] = varDbl_06_1.signal.combineWith(varStr_06_1.signal, varInt_06_1.signal)

  // Arity-4 combineWith chains (~4 combinations)
  val combined4a_06: Signal[(String, Int, Double, Boolean)] = varStr_06_1.signal.combineWith(varInt_06_1.signal, varDbl_06_1.signal, varBool_06_1.signal)
  val combined4b_06: Signal[(Int, Double, Boolean, String)] = varInt_06_1.signal.combineWith(varDbl_06_1.signal, varBool_06_1.signal, varStr_06_2.signal)
  val combined4c_06: Signal[(Boolean, String, Int, Double)] = varBool_06_1.signal.combineWith(varStr_06_2.signal, varInt_06_2.signal, varDbl_06_2.signal)
  val combined4d_06: Signal[(Double, Boolean, String, Int)] = varDbl_06_1.signal.combineWith(varBool_06_2.signal, varStr_06_1.signal, varInt_06_1.signal)

  // Arity-5 combineWith chains (~3 combinations)
  val combined5a_06: Signal[(String, Int, Double, Boolean, String)] = varStr_06_1.signal.combineWith(varInt_06_1.signal, varDbl_06_1.signal, varBool_06_1.signal, varStr_06_2.signal)
  val combined5b_06: Signal[(Int, Double, Boolean, String, Int)] = varInt_06_1.signal.combineWith(varDbl_06_1.signal, varBool_06_1.signal, varStr_06_2.signal, varInt_06_2.signal)
  val combined5c_06: Signal[(Double, Boolean, String, Int, Double)] = varDbl_06_1.signal.combineWith(varBool_06_1.signal, varStr_06_1.signal, varInt_06_1.signal, varDbl_06_2.signal)

  // Mapped combinations to trigger more type inference
  val mapped2_06: Signal[String] = combined2a_06.map { case (s, i) => s"$s-$i" }
  val mapped3_06: Signal[String] = combined3a_06.map { case (s, i, d) => s"$s-$i-$d" }
  val mapped4_06: Signal[String] = combined4a_06.map { case (s, i, d, b) => s"$s-$i-$d-$b" }
  val mapped5_06: Signal[String] = combined5a_06.map { case (s, i, d, b, s2) => s"$s-$i-$d-$b-$s2" }

  // Chained combineWith (combine results of combines)
  val chained2_06 = combined2a_06.combineWith(combined2b_06)
  val chained3_06 = combined3a_06.combineWith(combined3b_06)
  val chained4_06 =
    chained2_06.combineWith(combined2c_06.combineWith(combined2d_06))

  // Render function using combineWith results
  def render06: HtmlElement = {
    div(
      child.text <-- mapped2_06,
      child.text <-- mapped3_06,
      child.text <-- mapped4_06,
      child.text <-- mapped5_06,
      child.text <-- chained2_06.map(_.toString),
      child.text <-- chained3_06.map(_.toString),

      // Inline combineWith in element context
      child.text <-- varStr_06_1.signal.combineWith(varInt_06_1.signal).map { case (s, i) => s"inline-$s-$i" },
      child.text <-- varStr_06_1.signal.combineWith(varInt_06_1.signal, varDbl_06_1.signal).map { case (s, i, d) => s"inline-$s-$i-$d" },
      child.text <-- varStr_06_1.signal.combineWith(varInt_06_1.signal, varDbl_06_1.signal, varBool_06_1.signal).map { case (s, i, d, b) => s"inline-$s-$i-$d-$b" },
    )
  }
}

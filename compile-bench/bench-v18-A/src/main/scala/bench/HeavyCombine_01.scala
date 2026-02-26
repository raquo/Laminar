package bench

import com.raquo.laminar.api.L.{*, given}

object HeavyCombine01 {

  // ~8-10 Var declarations with different types
  val varStr_01_1 = Var("initial")
  val varInt_01_1 = Var(0)
  val varDbl_01_1 = Var(0.0)
  val varBool_01_1 = Var(false)
  val varStr_01_2 = Var("second")
  val varInt_01_2 = Var(42)
  val varDbl_01_2 = Var(3.14)
  val varBool_01_2 = Var(true)
  val varOpt_01 = Var(Option.empty[String])
  val varList_01 = Var(List.empty[Int])

  // Arity-2 combineWith chains (~5 combinations)
  val combined2a_01: Signal[(String, Int)] = varStr_01_1.signal.combineWith(varInt_01_1.signal)
  val combined2b_01: Signal[(Int, Double)] = varInt_01_1.signal.combineWith(varDbl_01_1.signal)
  val combined2c_01: Signal[(Boolean, String)] = varBool_01_1.signal.combineWith(varStr_01_2.signal)
  val combined2d_01: Signal[(Double, Boolean)] = varDbl_01_2.signal.combineWith(varBool_01_2.signal)
  val combined2e_01: Signal[(String, Boolean)] = varStr_01_1.signal.combineWith(varBool_01_1.signal)

  // Arity-3 combineWith chains (~5 combinations)
  val combined3a_01: Signal[(String, Int, Double)] = varStr_01_1.signal.combineWith(varInt_01_1.signal, varDbl_01_1.signal)
  val combined3b_01: Signal[(Int, Double, Boolean)] = varInt_01_1.signal.combineWith(varDbl_01_1.signal, varBool_01_1.signal)
  val combined3c_01: Signal[(Boolean, String, Int)] = varBool_01_1.signal.combineWith(varStr_01_2.signal, varInt_01_2.signal)
  val combined3d_01: Signal[(String, Int, Boolean)] = varStr_01_2.signal.combineWith(varInt_01_2.signal, varBool_01_2.signal)
  val combined3e_01: Signal[(Double, String, Int)] = varDbl_01_1.signal.combineWith(varStr_01_1.signal, varInt_01_1.signal)

  // Arity-4 combineWith chains (~4 combinations)
  val combined4a_01: Signal[(String, Int, Double, Boolean)] = varStr_01_1.signal.combineWith(varInt_01_1.signal, varDbl_01_1.signal, varBool_01_1.signal)
  val combined4b_01: Signal[(Int, Double, Boolean, String)] = varInt_01_1.signal.combineWith(varDbl_01_1.signal, varBool_01_1.signal, varStr_01_2.signal)
  val combined4c_01: Signal[(Boolean, String, Int, Double)] = varBool_01_1.signal.combineWith(varStr_01_2.signal, varInt_01_2.signal, varDbl_01_2.signal)
  val combined4d_01: Signal[(Double, Boolean, String, Int)] = varDbl_01_1.signal.combineWith(varBool_01_2.signal, varStr_01_1.signal, varInt_01_1.signal)

  // Arity-5 combineWith chains (~3 combinations)
  val combined5a_01: Signal[(String, Int, Double, Boolean, String)] = varStr_01_1.signal.combineWith(varInt_01_1.signal, varDbl_01_1.signal, varBool_01_1.signal, varStr_01_2.signal)
  val combined5b_01: Signal[(Int, Double, Boolean, String, Int)] = varInt_01_1.signal.combineWith(varDbl_01_1.signal, varBool_01_1.signal, varStr_01_2.signal, varInt_01_2.signal)
  val combined5c_01: Signal[(Double, Boolean, String, Int, Double)] = varDbl_01_1.signal.combineWith(varBool_01_1.signal, varStr_01_1.signal, varInt_01_1.signal, varDbl_01_2.signal)

  // Mapped combinations to trigger more type inference
  val mapped2_01: Signal[String] = combined2a_01.map { case (s, i) => s"$s-$i" }
  val mapped3_01: Signal[String] = combined3a_01.map { case (s, i, d) => s"$s-$i-$d" }
  val mapped4_01: Signal[String] = combined4a_01.map { case (s, i, d, b) => s"$s-$i-$d-$b" }
  val mapped5_01: Signal[String] = combined5a_01.map { case (s, i, d, b, s2) => s"$s-$i-$d-$b-$s2" }

  // Chained combineWith (combine results of combines)
  val chained2_01 = combined2a_01.combineWith(combined2b_01)
  val chained3_01 = combined3a_01.combineWith(combined3b_01)
  val chained4_01 =
    chained2_01.combineWith(combined2c_01.combineWith(combined2d_01))

  // Render function using combineWith results
  def render01: HtmlElement = {
    div(
      child.text <-- mapped2_01,
      child.text <-- mapped3_01,
      child.text <-- mapped4_01,
      child.text <-- mapped5_01,
      child.text <-- chained2_01.map(_.toString),
      child.text <-- chained3_01.map(_.toString),

      // Inline combineWith in element context
      child.text <-- varStr_01_1.signal.combineWith(varInt_01_1.signal).map { case (s, i) => s"inline-$s-$i" },
      child.text <-- varStr_01_1.signal.combineWith(varInt_01_1.signal, varDbl_01_1.signal).map { case (s, i, d) => s"inline-$s-$i-$d" },
      child.text <-- varStr_01_1.signal.combineWith(varInt_01_1.signal, varDbl_01_1.signal, varBool_01_1.signal).map { case (s, i, d, b) => s"inline-$s-$i-$d-$b" },
    )
  }
}

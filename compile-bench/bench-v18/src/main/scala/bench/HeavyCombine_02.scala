package bench

import com.raquo.laminar.api.L.{*, given}

object HeavyCombine02 {

  // ~8-10 Var declarations with different types
  val varStr_02_1 = Var("initial")
  val varInt_02_1 = Var(0)
  val varDbl_02_1 = Var(0.0)
  val varBool_02_1 = Var(false)
  val varStr_02_2 = Var("second")
  val varInt_02_2 = Var(42)
  val varDbl_02_2 = Var(3.14)
  val varBool_02_2 = Var(true)
  val varOpt_02 = Var(Option.empty[String])
  val varList_02 = Var(List.empty[Int])

  // Arity-2 combineWith chains (~5 combinations)
  val combined2a_02: Signal[(String, Int)] = varStr_02_1.signal.combineWith(varInt_02_1.signal)
  val combined2b_02: Signal[(Int, Double)] = varInt_02_1.signal.combineWith(varDbl_02_1.signal)
  val combined2c_02: Signal[(Boolean, String)] = varBool_02_1.signal.combineWith(varStr_02_2.signal)
  val combined2d_02: Signal[(Double, Boolean)] = varDbl_02_2.signal.combineWith(varBool_02_2.signal)
  val combined2e_02: Signal[(String, Boolean)] = varStr_02_1.signal.combineWith(varBool_02_1.signal)

  // Arity-3 combineWith chains (~5 combinations)
  val combined3a_02: Signal[(String, Int, Double)] = varStr_02_1.signal.combineWith(varInt_02_1.signal, varDbl_02_1.signal)
  val combined3b_02: Signal[(Int, Double, Boolean)] = varInt_02_1.signal.combineWith(varDbl_02_1.signal, varBool_02_1.signal)
  val combined3c_02: Signal[(Boolean, String, Int)] = varBool_02_1.signal.combineWith(varStr_02_2.signal, varInt_02_2.signal)
  val combined3d_02: Signal[(String, Int, Boolean)] = varStr_02_2.signal.combineWith(varInt_02_2.signal, varBool_02_2.signal)
  val combined3e_02: Signal[(Double, String, Int)] = varDbl_02_1.signal.combineWith(varStr_02_1.signal, varInt_02_1.signal)

  // Arity-4 combineWith chains (~4 combinations)
  val combined4a_02: Signal[(String, Int, Double, Boolean)] = varStr_02_1.signal.combineWith(varInt_02_1.signal, varDbl_02_1.signal, varBool_02_1.signal)
  val combined4b_02: Signal[(Int, Double, Boolean, String)] = varInt_02_1.signal.combineWith(varDbl_02_1.signal, varBool_02_1.signal, varStr_02_2.signal)
  val combined4c_02: Signal[(Boolean, String, Int, Double)] = varBool_02_1.signal.combineWith(varStr_02_2.signal, varInt_02_2.signal, varDbl_02_2.signal)
  val combined4d_02: Signal[(Double, Boolean, String, Int)] = varDbl_02_1.signal.combineWith(varBool_02_2.signal, varStr_02_1.signal, varInt_02_1.signal)

  // Arity-5 combineWith chains (~3 combinations)
  val combined5a_02: Signal[(String, Int, Double, Boolean, String)] = varStr_02_1.signal.combineWith(varInt_02_1.signal, varDbl_02_1.signal, varBool_02_1.signal, varStr_02_2.signal)
  val combined5b_02: Signal[(Int, Double, Boolean, String, Int)] = varInt_02_1.signal.combineWith(varDbl_02_1.signal, varBool_02_1.signal, varStr_02_2.signal, varInt_02_2.signal)
  val combined5c_02: Signal[(Double, Boolean, String, Int, Double)] = varDbl_02_1.signal.combineWith(varBool_02_1.signal, varStr_02_1.signal, varInt_02_1.signal, varDbl_02_2.signal)

  // Mapped combinations to trigger more type inference
  val mapped2_02: Signal[String] = combined2a_02.map { case (s, i) => s"$s-$i" }
  val mapped3_02: Signal[String] = combined3a_02.map { case (s, i, d) => s"$s-$i-$d" }
  val mapped4_02: Signal[String] = combined4a_02.map { case (s, i, d, b) => s"$s-$i-$d-$b" }
  val mapped5_02: Signal[String] = combined5a_02.map { case (s, i, d, b, s2) => s"$s-$i-$d-$b-$s2" }

  // Chained combineWith (combine results of combines)
  val chained2_02 = combined2a_02.combineWith(combined2b_02)
  val chained3_02 = combined3a_02.combineWith(combined3b_02)
  val chained4_02 =
    chained2_02.combineWith(combined2c_02.combineWith(combined2d_02))

  // Render function using combineWith results
  def render02: HtmlElement = {
    div(
      child.text <-- mapped2_02,
      child.text <-- mapped3_02,
      child.text <-- mapped4_02,
      child.text <-- mapped5_02,
      child.text <-- chained2_02.map(_.toString),
      child.text <-- chained3_02.map(_.toString),

      // Inline combineWith in element context
      child.text <-- varStr_02_1.signal.combineWith(varInt_02_1.signal).map { case (s, i) => s"inline-$s-$i" },
      child.text <-- varStr_02_1.signal.combineWith(varInt_02_1.signal, varDbl_02_1.signal).map { case (s, i, d) => s"inline-$s-$i-$d" },
      child.text <-- varStr_02_1.signal.combineWith(varInt_02_1.signal, varDbl_02_1.signal, varBool_02_1.signal).map { case (s, i, d, b) => s"inline-$s-$i-$d-$b" },
    )
  }
}

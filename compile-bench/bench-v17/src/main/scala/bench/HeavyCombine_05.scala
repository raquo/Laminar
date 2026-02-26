package bench

import com.raquo.laminar.api.L.{*, given}

object HeavyCombine05 {

  // ~8-10 Var declarations with different types
  val varStr_05_1 = Var("initial")
  val varInt_05_1 = Var(0)
  val varDbl_05_1 = Var(0.0)
  val varBool_05_1 = Var(false)
  val varStr_05_2 = Var("second")
  val varInt_05_2 = Var(42)
  val varDbl_05_2 = Var(3.14)
  val varBool_05_2 = Var(true)
  val varOpt_05 = Var(Option.empty[String])
  val varList_05 = Var(List.empty[Int])

  // Arity-2 combineWith chains (~5 combinations)
  val combined2a_05: Signal[(String, Int)] = varStr_05_1.signal.combineWith(varInt_05_1.signal)
  val combined2b_05: Signal[(Int, Double)] = varInt_05_1.signal.combineWith(varDbl_05_1.signal)
  val combined2c_05: Signal[(Boolean, String)] = varBool_05_1.signal.combineWith(varStr_05_2.signal)
  val combined2d_05: Signal[(Double, Boolean)] = varDbl_05_2.signal.combineWith(varBool_05_2.signal)
  val combined2e_05: Signal[(String, Boolean)] = varStr_05_1.signal.combineWith(varBool_05_1.signal)

  // Arity-3 combineWith chains (~5 combinations)
  val combined3a_05: Signal[(String, Int, Double)] = varStr_05_1.signal.combineWith(varInt_05_1.signal, varDbl_05_1.signal)
  val combined3b_05: Signal[(Int, Double, Boolean)] = varInt_05_1.signal.combineWith(varDbl_05_1.signal, varBool_05_1.signal)
  val combined3c_05: Signal[(Boolean, String, Int)] = varBool_05_1.signal.combineWith(varStr_05_2.signal, varInt_05_2.signal)
  val combined3d_05: Signal[(String, Int, Boolean)] = varStr_05_2.signal.combineWith(varInt_05_2.signal, varBool_05_2.signal)
  val combined3e_05: Signal[(Double, String, Int)] = varDbl_05_1.signal.combineWith(varStr_05_1.signal, varInt_05_1.signal)

  // Arity-4 combineWith chains (~4 combinations)
  val combined4a_05: Signal[(String, Int, Double, Boolean)] = varStr_05_1.signal.combineWith(varInt_05_1.signal, varDbl_05_1.signal, varBool_05_1.signal)
  val combined4b_05: Signal[(Int, Double, Boolean, String)] = varInt_05_1.signal.combineWith(varDbl_05_1.signal, varBool_05_1.signal, varStr_05_2.signal)
  val combined4c_05: Signal[(Boolean, String, Int, Double)] = varBool_05_1.signal.combineWith(varStr_05_2.signal, varInt_05_2.signal, varDbl_05_2.signal)
  val combined4d_05: Signal[(Double, Boolean, String, Int)] = varDbl_05_1.signal.combineWith(varBool_05_2.signal, varStr_05_1.signal, varInt_05_1.signal)

  // Arity-5 combineWith chains (~3 combinations)
  val combined5a_05: Signal[(String, Int, Double, Boolean, String)] = varStr_05_1.signal.combineWith(varInt_05_1.signal, varDbl_05_1.signal, varBool_05_1.signal, varStr_05_2.signal)
  val combined5b_05: Signal[(Int, Double, Boolean, String, Int)] = varInt_05_1.signal.combineWith(varDbl_05_1.signal, varBool_05_1.signal, varStr_05_2.signal, varInt_05_2.signal)
  val combined5c_05: Signal[(Double, Boolean, String, Int, Double)] = varDbl_05_1.signal.combineWith(varBool_05_1.signal, varStr_05_1.signal, varInt_05_1.signal, varDbl_05_2.signal)

  // Mapped combinations to trigger more type inference
  val mapped2_05: Signal[String] = combined2a_05.map { case (s, i) => s"$s-$i" }
  val mapped3_05: Signal[String] = combined3a_05.map { case (s, i, d) => s"$s-$i-$d" }
  val mapped4_05: Signal[String] = combined4a_05.map { case (s, i, d, b) => s"$s-$i-$d-$b" }
  val mapped5_05: Signal[String] = combined5a_05.map { case (s, i, d, b, s2) => s"$s-$i-$d-$b-$s2" }

  // Chained combineWith (combine results of combines)
  val chained2_05 = combined2a_05.combineWith(combined2b_05)
  val chained3_05 = combined3a_05.combineWith(combined3b_05)
  val chained4_05 =
    chained2_05.combineWith(combined2c_05.combineWith(combined2d_05))

  // Render function using combineWith results
  def render05: HtmlElement = {
    div(
      child.text <-- mapped2_05,
      child.text <-- mapped3_05,
      child.text <-- mapped4_05,
      child.text <-- mapped5_05,
      child.text <-- chained2_05.map(_.toString),
      child.text <-- chained3_05.map(_.toString),

      // Inline combineWith in element context
      child.text <-- varStr_05_1.signal.combineWith(varInt_05_1.signal).map { case (s, i) => s"inline-$s-$i" },
      child.text <-- varStr_05_1.signal.combineWith(varInt_05_1.signal, varDbl_05_1.signal).map { case (s, i, d) => s"inline-$s-$i-$d" },
      child.text <-- varStr_05_1.signal.combineWith(varInt_05_1.signal, varDbl_05_1.signal, varBool_05_1.signal).map { case (s, i, d, b) => s"inline-$s-$i-$d-$b" },
    )
  }
}

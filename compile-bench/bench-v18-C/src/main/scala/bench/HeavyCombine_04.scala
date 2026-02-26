package bench

import com.raquo.laminar.api.L.{*, given}

object HeavyCombine04 {

  // ~8-10 Var declarations with different types
  val varStr_04_1 = Var("initial")
  val varInt_04_1 = Var(0)
  val varDbl_04_1 = Var(0.0)
  val varBool_04_1 = Var(false)
  val varStr_04_2 = Var("second")
  val varInt_04_2 = Var(42)
  val varDbl_04_2 = Var(3.14)
  val varBool_04_2 = Var(true)
  val varOpt_04 = Var(Option.empty[String])
  val varList_04 = Var(List.empty[Int])

  // Arity-2 combineWith chains (~5 combinations)
  val combined2a_04: Signal[(String, Int)] = varStr_04_1.signal.combineWith(varInt_04_1.signal)
  val combined2b_04: Signal[(Int, Double)] = varInt_04_1.signal.combineWith(varDbl_04_1.signal)
  val combined2c_04: Signal[(Boolean, String)] = varBool_04_1.signal.combineWith(varStr_04_2.signal)
  val combined2d_04: Signal[(Double, Boolean)] = varDbl_04_2.signal.combineWith(varBool_04_2.signal)
  val combined2e_04: Signal[(String, Boolean)] = varStr_04_1.signal.combineWith(varBool_04_1.signal)

  // Arity-3 combineWith chains (~5 combinations)
  val combined3a_04: Signal[(String, Int, Double)] = varStr_04_1.signal.combineWith(varInt_04_1.signal, varDbl_04_1.signal)
  val combined3b_04: Signal[(Int, Double, Boolean)] = varInt_04_1.signal.combineWith(varDbl_04_1.signal, varBool_04_1.signal)
  val combined3c_04: Signal[(Boolean, String, Int)] = varBool_04_1.signal.combineWith(varStr_04_2.signal, varInt_04_2.signal)
  val combined3d_04: Signal[(String, Int, Boolean)] = varStr_04_2.signal.combineWith(varInt_04_2.signal, varBool_04_2.signal)
  val combined3e_04: Signal[(Double, String, Int)] = varDbl_04_1.signal.combineWith(varStr_04_1.signal, varInt_04_1.signal)

  // Arity-4 combineWith chains (~4 combinations)
  val combined4a_04: Signal[(String, Int, Double, Boolean)] = varStr_04_1.signal.combineWith(varInt_04_1.signal, varDbl_04_1.signal, varBool_04_1.signal)
  val combined4b_04: Signal[(Int, Double, Boolean, String)] = varInt_04_1.signal.combineWith(varDbl_04_1.signal, varBool_04_1.signal, varStr_04_2.signal)
  val combined4c_04: Signal[(Boolean, String, Int, Double)] = varBool_04_1.signal.combineWith(varStr_04_2.signal, varInt_04_2.signal, varDbl_04_2.signal)
  val combined4d_04: Signal[(Double, Boolean, String, Int)] = varDbl_04_1.signal.combineWith(varBool_04_2.signal, varStr_04_1.signal, varInt_04_1.signal)

  // Arity-5 combineWith chains (~3 combinations)
  val combined5a_04: Signal[(String, Int, Double, Boolean, String)] = varStr_04_1.signal.combineWith(varInt_04_1.signal, varDbl_04_1.signal, varBool_04_1.signal, varStr_04_2.signal)
  val combined5b_04: Signal[(Int, Double, Boolean, String, Int)] = varInt_04_1.signal.combineWith(varDbl_04_1.signal, varBool_04_1.signal, varStr_04_2.signal, varInt_04_2.signal)
  val combined5c_04: Signal[(Double, Boolean, String, Int, Double)] = varDbl_04_1.signal.combineWith(varBool_04_1.signal, varStr_04_1.signal, varInt_04_1.signal, varDbl_04_2.signal)

  // Mapped combinations to trigger more type inference
  val mapped2_04: Signal[String] = combined2a_04.map { case (s, i) => s"$s-$i" }
  val mapped3_04: Signal[String] = combined3a_04.map { case (s, i, d) => s"$s-$i-$d" }
  val mapped4_04: Signal[String] = combined4a_04.map { case (s, i, d, b) => s"$s-$i-$d-$b" }
  val mapped5_04: Signal[String] = combined5a_04.map { case (s, i, d, b, s2) => s"$s-$i-$d-$b-$s2" }

  // Chained combineWith (combine results of combines)
  val chained2_04 = combined2a_04.combineWith(combined2b_04)
  val chained3_04 = combined3a_04.combineWith(combined3b_04)
  val chained4_04 =
    chained2_04.combineWith(combined2c_04.combineWith(combined2d_04))

  // Render function using combineWith results
  def render04: HtmlElement = {
    div(
      child.text <-- mapped2_04,
      child.text <-- mapped3_04,
      child.text <-- mapped4_04,
      child.text <-- mapped5_04,
      child.text <-- chained2_04.map(_.toString),
      child.text <-- chained3_04.map(_.toString),

      // Inline combineWith in element context
      child.text <-- varStr_04_1.signal.combineWith(varInt_04_1.signal).map { case (s, i) => s"inline-$s-$i" },
      child.text <-- varStr_04_1.signal.combineWith(varInt_04_1.signal, varDbl_04_1.signal).map { case (s, i, d) => s"inline-$s-$i-$d" },
      child.text <-- varStr_04_1.signal.combineWith(varInt_04_1.signal, varDbl_04_1.signal, varBool_04_1.signal).map { case (s, i, d, b) => s"inline-$s-$i-$d-$b" },
    )
  }
}

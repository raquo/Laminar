package micro

import com.raquo.laminar.api.L.{*, given}

object MicroCombineWith {

  val v1 = Var("a")
  val v2 = Var(1)
  val v3 = Var(2.0)
  val v4 = Var(true)
  val v5 = Var('x')
  val v6 = Var(List(1))
  val v7 = Var(Option("y"))
  val v8 = Var(0L)

  // Arity 2
  val c2a: Signal[(String, Int)] = v1.signal.combineWith(v2.signal)
  val c2b: Signal[(Int, Double)] = v2.signal.combineWith(v3.signal)
  val c2c: Signal[(Double, Boolean)] = v3.signal.combineWith(v4.signal)
  val c2d: Signal[(Boolean, Char)] = v4.signal.combineWith(v5.signal)
  val c2e: Signal[(Char, List[Int])] = v5.signal.combineWith(v6.signal)
  val c2f: Signal[(List[Int], Option[String])] = v6.signal.combineWith(v7.signal)
  val c2g: Signal[(Option[String], Long)] = v7.signal.combineWith(v8.signal)
  val c2h: Signal[(Long, String)] = v8.signal.combineWith(v1.signal)

  // Arity 3
  val c3a: Signal[(String, Int, Double)] = v1.signal.combineWith(v2.signal, v3.signal)
  val c3b: Signal[(Int, Double, Boolean)] = v2.signal.combineWith(v3.signal, v4.signal)
  val c3c: Signal[(Double, Boolean, Char)] = v3.signal.combineWith(v4.signal, v5.signal)
  val c3d: Signal[(Boolean, Char, List[Int])] = v4.signal.combineWith(v5.signal, v6.signal)
  val c3e: Signal[(Char, List[Int], Option[String])] = v5.signal.combineWith(v6.signal, v7.signal)
  val c3f: Signal[(List[Int], Option[String], Long)] = v6.signal.combineWith(v7.signal, v8.signal)
  val c3g: Signal[(Option[String], Long, String)] = v7.signal.combineWith(v8.signal, v1.signal)
  val c3h: Signal[(Long, String, Int)] = v8.signal.combineWith(v1.signal, v2.signal)

  // Arity 4
  val c4a: Signal[(String, Int, Double, Boolean)] = v1.signal.combineWith(v2.signal, v3.signal, v4.signal)
  val c4b: Signal[(Int, Double, Boolean, Char)] = v2.signal.combineWith(v3.signal, v4.signal, v5.signal)
  val c4c: Signal[(Double, Boolean, Char, List[Int])] = v3.signal.combineWith(v4.signal, v5.signal, v6.signal)
  val c4d: Signal[(Boolean, Char, List[Int], Option[String])] = v4.signal.combineWith(v5.signal, v6.signal, v7.signal)
  val c4e: Signal[(Char, List[Int], Option[String], Long)] = v5.signal.combineWith(v6.signal, v7.signal, v8.signal)
  val c4f: Signal[(List[Int], Option[String], Long, String)] = v6.signal.combineWith(v7.signal, v8.signal, v1.signal)

  // Arity 5
  val c5a: Signal[(String, Int, Double, Boolean, Char)] = v1.signal.combineWith(v2.signal, v3.signal, v4.signal, v5.signal)
  val c5b: Signal[(Int, Double, Boolean, Char, List[Int])] = v2.signal.combineWith(v3.signal, v4.signal, v5.signal, v6.signal)
  val c5c: Signal[(Double, Boolean, Char, List[Int], Option[String])] = v3.signal.combineWith(v4.signal, v5.signal, v6.signal, v7.signal)
  val c5d: Signal[(Boolean, Char, List[Int], Option[String], Long)] = v4.signal.combineWith(v5.signal, v6.signal, v7.signal, v8.signal)

  // Chained combines (tuplez Composition flattens tuples)
  val ch1 = c2a.combineWith(c2c)
  val ch2 = c2b.combineWith(c2d)
  val ch3 = c3a.combineWith(c3b)
  val ch4 = c3c.combineWith(c3d)

  // Mapped
  val m1: Signal[String] = c2a.map { case (s, i) => s"$s:$i" }
  val m2: Signal[String] = c3a.map { case (s, i, d) => s"$s:$i:$d" }
  val m3: Signal[String] = c4a.map { case (s, i, d, b) => s"$s:$i:$d:$b" }
  val m4: Signal[String] = c5a.map { case (s, i, d, b, c) => s"$s:$i:$d:$b:$c" }

  def render: HtmlElement = {
    div(
      child.text <-- m1,
      child.text <-- m2,
      child.text <-- m3,
      child.text <-- m4,
    )
  }
}

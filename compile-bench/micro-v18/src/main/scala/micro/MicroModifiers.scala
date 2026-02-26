package micro

import com.raquo.laminar.api.L.{*, given}

object MicroModifiers {

  val isOpen = Var(false)
  val text = Var("hello")
  val num = Var(0)
  val items = Var(List("a", "b", "c"))

  val clickBus = new EventBus[String]
  val inputBus = new EventBus[String]

  val logObserver: Observer[String] = Observer[String](_ => ())
  val numObserver: Observer[Int] = Observer[Int](_ => ())

  def render: HtmlElement = {
    div(
      // onClick modifiers
      button(onClick --> (_ => isOpen.update(!_)), "Toggle"),
      button(onClick --> (_ => num.update(_ + 1)), "Increment"),
      button(onClick --> (_ => num.set(0)), "Reset"),
      button(onClick.mapTo("clicked") --> clickBus.writer, "Click"),
      button(onClick.mapTo("action") --> logObserver, "Log"),

      // onInput modifiers
      input(typ := "text", onInput.mapToValue --> text.writer),
      input(typ := "text", onInput.mapToValue --> inputBus.writer),
      input(typ := "number", onInput.mapToValue.map(_.toIntOption.getOrElse(0)) --> num.writer),

      // onChange, onKeyDown, onFocus, onBlur
      input(typ := "text", onChange.mapToValue --> text.writer),
      input(typ := "text", onKeyDown.mapToEvent --> (_ => ())),
      input(typ := "text", onFocus --> (_ => ()), onBlur --> (_ => ())),

      // child <-- modifiers
      child.text <-- text.signal,
      child.text <-- num.signal.map(_.toString),
      child.text <-- isOpen.signal.map(_.toString),
      child <-- isOpen.signal.map(open => if (open) div("Open") else div("Closed")),
      child <-- text.signal.combineWith(num.signal).map { case (t, n) => span(s"$t:$n") },

      // children <-- modifiers
      children <-- items.signal.map(_.map(item => li(item))),
      children <-- items.signal.split(identity) { (key, initial, sig) =>
        div(dataAttr("key") := key, child.text <-- sig)
      },

      // cls <-- modifiers
      div(
        cls := "base-class",
        cls <-- isOpen.signal.map(if (_) "open" else "closed"),
        cls <-- text.signal.map(t => s"text-$t"),
      ),

      // disabled <-- modifier
      button(disabled <-- isOpen.signal, "Conditional"),
      button(disabled <-- num.signal.map(_ > 10), "Max 10"),

      // Nested modifiers
      div(
        div(
          div(
            div(
              child.text <-- text.signal.combineWith(num.signal, isOpen.signal).map {
                case (t, n, o) => s"$t-$n-$o"
              },
            ),
          ),
        ),
      ),

      // Seq of modifiers (implicit conversion)
      div(
        List(
          cls := "from-seq",
          tabIndex := 0,
          title := "generated",
        ),
      ),

      // Event delegation patterns
      ul(
        onClick --> (ev => ()),
        onMouseOver --> (_ => ()),
        onMouseOut --> (_ => ()),
        li(cls := "item", "Item 1"),
        li(cls := "item", "Item 2"),
        li(cls := "item", "Item 3"),
        li(cls := "item", "Item 4"),
        li(cls := "item", "Item 5"),
      ),

      // Conditional rendering
      child <-- num.signal.map { n =>
        if (n > 5) div(cls := "high", s"High: $n")
        else if (n > 0) div(cls := "low", s"Low: $n")
        else div(cls := "zero", "Zero")
      },
    )
  }
}

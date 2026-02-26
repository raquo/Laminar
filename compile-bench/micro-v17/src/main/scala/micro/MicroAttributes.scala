package micro

import com.raquo.laminar.api.L.{*, given}

object MicroAttributes {

  val isActive = Var(false)
  val labelText = Var("default")
  val count = Var(0)
  val url = Var("#")

  def render: HtmlElement = {
    div(
      cls := "container",
      idAttr := "main",
      tabIndex := 0,
      title := "Main container",
      dataAttr("testid") := "root",
      role := "main",

      div(
        cls := "header",
        cls <-- isActive.signal.map(if (_) "active" else "inactive"),
        dataAttr("state") <-- isActive.signal.map(_.toString),

        h1(cls := "title", idAttr := "page-title", "Page Title"),
        navTag(
          cls := "nav-bar",
          role := "navigation",
          a(cls := "nav-link", href := "#home", tabIndex := 1, title := "Go home", "Home"),
          a(cls := "nav-link", href := "#about", tabIndex := 2, title := "About us", "About"),
          a(cls := "nav-link", href := "#contact", tabIndex := 3, title := "Contact", "Contact"),
          a(cls := "nav-link", href <-- url.signal, tabIndex := 4, title := "Dynamic", child.text <-- labelText.signal),
        ),
      ),

      form(
        cls := "form",
        idAttr := "main-form",
        input(cls := "input-name", typ := "text", placeholder := "Name", nameAttr := "name", tabIndex := 10, disabled <-- isActive.signal.map(!_)),
        input(cls := "input-email", typ := "email", placeholder := "Email", nameAttr := "email", tabIndex := 11, disabled <-- isActive.signal.map(!_)),
        input(cls := "input-phone", typ := "tel", placeholder := "Phone", nameAttr := "phone", tabIndex := 12),
        input(cls := "input-url", typ := "url", placeholder := "Website", nameAttr := "website", tabIndex := 13),
        input(cls := "input-search", typ := "search", placeholder := "Search...", nameAttr := "search", tabIndex := 14),
        label(cls := "label-check", input(typ := "checkbox", nameAttr := "agree", tabIndex := 15), " I agree"),
        label(cls := "label-radio", input(typ := "radio", nameAttr := "choice", tabIndex := 16), " Option A"),
        label(cls := "label-radio", input(typ := "radio", nameAttr := "choice", tabIndex := 17), " Option B"),
        button(cls := "btn-submit", typ := "submit", tabIndex := 20, disabled <-- isActive.signal, "Submit"),
        button(cls := "btn-reset", typ := "reset", tabIndex := 21, "Reset"),
      ),

      div(
        cls := "card-grid",
        div(cls := "card", idAttr := "c1", dataAttr("idx") := "1", tabIndex := 30, title := "Card 1",
          h3(cls := "card-title", "Card 1"), p(cls := "card-body", "Body 1")),
        div(cls := "card", idAttr := "c2", dataAttr("idx") := "2", tabIndex := 31, title := "Card 2",
          h3(cls := "card-title", "Card 2"), p(cls := "card-body", "Body 2")),
        div(cls := "card", idAttr := "c3", dataAttr("idx") := "3", tabIndex := 32, title := "Card 3",
          h3(cls := "card-title", "Card 3"), p(cls := "card-body", "Body 3")),
        div(cls := "card", idAttr := "c4", dataAttr("idx") := "4", tabIndex := 33, title := "Card 4",
          h3(cls := "card-title", "Card 4"), p(cls := "card-body", "Body 4")),
        div(cls := "card", idAttr := "c5", dataAttr("idx") := "5", tabIndex := 34, title := "Card 5",
          h3(cls := "card-title", "Card 5"), p(cls := "card-body", "Body 5")),
        div(cls := "card", idAttr := "c6", dataAttr("idx") := "6", tabIndex := 35, title := "Card 6",
          h3(cls := "card-title", "Card 6"), p(cls := "card-body", "Body 6")),
      ),

      footerTag(
        cls := "footer",
        role := "contentinfo",
        span(cls := "footer-text", child.text <-- count.signal.map(c => s"Count: $c")),
        a(cls := "footer-link", href := "#privacy", "Privacy"),
        a(cls := "footer-link", href := "#terms", "Terms"),
      ),
    )
  }
}

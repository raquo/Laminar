package bench

import com.raquo.laminar.api.L.{*, given}

object ReactiveDSL01 {

  // State
  val activeTab_01 = Var("home")
  val searchQuery_01 = Var("")
  val isMenuOpen_01 = Var(false)
  val selectedId_01 = Var(Option.empty[String])
  val count_01 = Var(0)
  val items_01 = Var(List("item1", "item2", "item3"))
  val userName_01 = Var("User")
  val isLoading_01 = Var(false)

  // Derived signals
  val filteredItems_01: Signal[List[String]] = items_01.signal.combineWith(searchQuery_01.signal).map {
    case (items, query) => items.filter(_.contains(query))
  }
  val itemCount_01: Signal[String] = filteredItems_01.map(items => s"${items.size} items")
  val isEmptySearch_01: Signal[Boolean] = searchQuery_01.signal.map(_.isEmpty)

  // Event buses
  val clickBus_01 = new EventBus[String]
  val submitBus_01 = new EventBus[Unit]

  // Observers
  val logObserver_01: Observer[String] = Observer[String](msg => ())
  val countObserver_01: Observer[Int] = Observer[Int](n => ())

  // Component functions generating deep element trees
  def navBar_01: HtmlElement = {
    navTag(
      cls := "navbar",
      role := "navigation",
      div(
        cls := "nav-brand",
        a(href := "#", cls := "brand-link", "App01"),
        button(
          cls := "menu-toggle",
          cls <-- isMenuOpen_01.signal.map(if (_) "active" else ""),
          onClick --> (_ => isMenuOpen_01.update(!_)),
          span(cls := "hamburger"),
        ),
      ),
      ul(
        cls := "nav-items",
        cls <-- isMenuOpen_01.signal.map(if (_) "visible" else "hidden"),
        li(cls <-- activeTab_01.signal.map(t => if (t == "home") "active" else ""), a(href := "#home", onClick --> (_ => activeTab_01.set("home")), "Home")),
        li(cls <-- activeTab_01.signal.map(t => if (t == "about") "active" else ""), a(href := "#about", onClick --> (_ => activeTab_01.set("about")), "About")),
        li(cls <-- activeTab_01.signal.map(t => if (t == "contact") "active" else ""), a(href := "#contact", onClick --> (_ => activeTab_01.set("contact")), "Contact")),
        li(cls <-- activeTab_01.signal.map(t => if (t == "help") "active" else ""), a(href := "#help", onClick --> (_ => activeTab_01.set("help")), "Help")),
      ),
    )
  }

  def searchBox_01: HtmlElement = {
    div(
      cls := "search-container",
      label(cls := "search-label", "Search: "),
      input(
        cls := "search-input",
        typ := "text",
        placeholder := "Type to search...",
        value <-- searchQuery_01.signal,
        onInput.mapToValue --> searchQuery_01.writer,
        onKeyDown --> (_ => ()),
        onFocus --> (_ => ()),
        onBlur --> (_ => ()),
      ),
      span(
        cls := "search-count",
        child.text <-- itemCount_01,
      ),
      button(
        cls := "clear-btn",
        disabled <-- isEmptySearch_01,
        onClick --> (_ => searchQuery_01.set("")),
        "Clear",
      ),
    )
  }

  def itemList_01: HtmlElement = {
    div(
      cls := "item-list",
      children <-- filteredItems_01.map { items =>
        items.map { item =>
          div(
            cls := "item-card",
            cls <-- selectedId_01.signal.map(sel => if (sel.contains(item)) "selected" else ""),
            onClick --> (_ => selectedId_01.set(Some(item))),
            onDblClick --> (_ => clickBus_01.emit(item)),
            h3(cls := "item-title", item),
            p(cls := "item-desc", s"Description for $item"),
            div(
              cls := "item-actions",
              button(cls := "btn btn-edit", onClick --> (_ => ()), "Edit"),
              button(cls := "btn btn-delete", onClick --> (_ => ()), "Delete"),
            ),
          )
        }
      },
    )
  }

  def statusBar_01: HtmlElement = {
    footerTag(
      cls := "status-bar",
      span(cls := "status-user", child.text <-- userName_01.signal),
      span(cls := "status-count", child.text <-- count_01.signal.map(_.toString)),
      span(cls := "status-tab", child.text <-- activeTab_01.signal),
      span(
        cls := "status-loading",
        cls <-- isLoading_01.signal.map(if (_) "spinning" else ""),
        child.text <-- isLoading_01.signal.map(if (_) "Loading..." else "Ready"),
      ),
    )
  }

  def contentPanel_01: HtmlElement = {
    mainTag(
      cls := "content",
      sectionTag(
        cls := "panel",
        headerTag(cls := "panel-header", h2("Panel 01")),
        div(
          cls := "panel-body",
          searchBox_01,
          itemList_01,
          div(
            cls := "panel-sidebar",
            child <-- selectedId_01.signal.map {
              case Some(id) => div(cls := "detail", h3("Selected"), p(id))
              case None => div(cls := "detail empty", p("Nothing selected"))
            },
          ),
        ),
        statusBar_01,
      ),
    )
  }

  def render01: HtmlElement = {
    div(
      cls := "app-container",
      idAttr := s"app-01",
      navBar_01,
      contentPanel_01,
      // Extra bindings to increase reactive density
      child.text <-- activeTab_01.signal.combineWith(count_01.signal).map { case (tab, c) => s"$tab:$c" },
      child.text <-- searchQuery_01.signal.combineWith(isLoading_01.signal).map { case (q, l) => s"$q:$l" },
    )
  }
}

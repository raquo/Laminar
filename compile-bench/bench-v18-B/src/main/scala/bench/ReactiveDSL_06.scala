package bench

import com.raquo.laminar.api.L.{*, given}

object ReactiveDSL06 {

  // State
  val activeTab_06 = Var("home")
  val searchQuery_06 = Var("")
  val isMenuOpen_06 = Var(false)
  val selectedId_06 = Var(Option.empty[String])
  val count_06 = Var(0)
  val items_06 = Var(List("item1", "item2", "item3"))
  val userName_06 = Var("User")
  val isLoading_06 = Var(false)

  // Derived signals
  val filteredItems_06: Signal[List[String]] = items_06.signal.combineWith(searchQuery_06.signal).map {
    case (items, query) => items.filter(_.contains(query))
  }
  val itemCount_06: Signal[String] = filteredItems_06.map(items => s"${items.size} items")
  val isEmptySearch_06: Signal[Boolean] = searchQuery_06.signal.map(_.isEmpty)

  // Event buses
  val clickBus_06 = new EventBus[String]
  val submitBus_06 = new EventBus[Unit]

  // Observers
  val logObserver_06: Observer[String] = Observer[String](msg => ())
  val countObserver_06: Observer[Int] = Observer[Int](n => ())

  // Component functions generating deep element trees
  def navBar_06: HtmlElement = {
    navTag(
      cls := "navbar",
      role := "navigation",
      div(
        cls := "nav-brand",
        a(href := "#", cls := "brand-link", "App06"),
        button(
          cls := "menu-toggle",
          cls <-- isMenuOpen_06.signal.map(if (_) "active" else ""),
          onClick --> (_ => isMenuOpen_06.update(!_)),
          span(cls := "hamburger"),
        ),
      ),
      ul(
        cls := "nav-items",
        cls <-- isMenuOpen_06.signal.map(if (_) "visible" else "hidden"),
        li(cls <-- activeTab_06.signal.map(t => if (t == "home") "active" else ""), a(href := "#home", onClick --> (_ => activeTab_06.set("home")), "Home")),
        li(cls <-- activeTab_06.signal.map(t => if (t == "about") "active" else ""), a(href := "#about", onClick --> (_ => activeTab_06.set("about")), "About")),
        li(cls <-- activeTab_06.signal.map(t => if (t == "contact") "active" else ""), a(href := "#contact", onClick --> (_ => activeTab_06.set("contact")), "Contact")),
        li(cls <-- activeTab_06.signal.map(t => if (t == "help") "active" else ""), a(href := "#help", onClick --> (_ => activeTab_06.set("help")), "Help")),
      ),
    )
  }

  def searchBox_06: HtmlElement = {
    div(
      cls := "search-container",
      label(cls := "search-label", "Search: "),
      input(
        cls := "search-input",
        typ := "text",
        placeholder := "Type to search...",
        value <-- searchQuery_06.signal,
        onInput.mapToValue --> searchQuery_06.writer,
        onKeyDown --> (_ => ()),
        onFocus --> (_ => ()),
        onBlur --> (_ => ()),
      ),
      span(
        cls := "search-count",
        child.text <-- itemCount_06,
      ),
      button(
        cls := "clear-btn",
        disabled <-- isEmptySearch_06,
        onClick --> (_ => searchQuery_06.set("")),
        "Clear",
      ),
    )
  }

  def itemList_06: HtmlElement = {
    div(
      cls := "item-list",
      children <-- filteredItems_06.map { items =>
        items.map { item =>
          div(
            cls := "item-card",
            cls <-- selectedId_06.signal.map(sel => if (sel.contains(item)) "selected" else ""),
            onClick --> (_ => selectedId_06.set(Some(item))),
            onDblClick --> (_ => clickBus_06.emit(item)),
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

  def statusBar_06: HtmlElement = {
    footerTag(
      cls := "status-bar",
      span(cls := "status-user", child.text <-- userName_06.signal),
      span(cls := "status-count", child.text <-- count_06.signal.map(_.toString)),
      span(cls := "status-tab", child.text <-- activeTab_06.signal),
      span(
        cls := "status-loading",
        cls <-- isLoading_06.signal.map(if (_) "spinning" else ""),
        child.text <-- isLoading_06.signal.map(if (_) "Loading..." else "Ready"),
      ),
    )
  }

  def contentPanel_06: HtmlElement = {
    mainTag(
      cls := "content",
      sectionTag(
        cls := "panel",
        headerTag(cls := "panel-header", h2("Panel 06")),
        div(
          cls := "panel-body",
          searchBox_06,
          itemList_06,
          div(
            cls := "panel-sidebar",
            child <-- selectedId_06.signal.map {
              case Some(id) => div(cls := "detail", h3("Selected"), p(id))
              case None => div(cls := "detail empty", p("Nothing selected"))
            },
          ),
        ),
        statusBar_06,
      ),
    )
  }

  def render06: HtmlElement = {
    div(
      cls := "app-container",
      idAttr := s"app-06",
      navBar_06,
      contentPanel_06,
      // Extra bindings to increase reactive density
      child.text <-- activeTab_06.signal.combineWith(count_06.signal).map { case (tab, c) => s"$tab:$c" },
      child.text <-- searchQuery_06.signal.combineWith(isLoading_06.signal).map { case (q, l) => s"$q:$l" },
    )
  }
}

package bench

import com.raquo.laminar.api.L.{*, given}

object ReactiveDSL04 {

  // State
  val activeTab_04 = Var("home")
  val searchQuery_04 = Var("")
  val isMenuOpen_04 = Var(false)
  val selectedId_04 = Var(Option.empty[String])
  val count_04 = Var(0)
  val items_04 = Var(List("item1", "item2", "item3"))
  val userName_04 = Var("User")
  val isLoading_04 = Var(false)

  // Derived signals
  val filteredItems_04: Signal[List[String]] = items_04.signal.combineWith(searchQuery_04.signal).map {
    case (items, query) => items.filter(_.contains(query))
  }
  val itemCount_04: Signal[String] = filteredItems_04.map(items => s"${items.size} items")
  val isEmptySearch_04: Signal[Boolean] = searchQuery_04.signal.map(_.isEmpty)

  // Event buses
  val clickBus_04 = new EventBus[String]
  val submitBus_04 = new EventBus[Unit]

  // Observers
  val logObserver_04: Observer[String] = Observer[String](msg => ())
  val countObserver_04: Observer[Int] = Observer[Int](n => ())

  // Component functions generating deep element trees
  def navBar_04: HtmlElement = {
    navTag(
      cls := "navbar",
      role := "navigation",
      div(
        cls := "nav-brand",
        a(href := "#", cls := "brand-link", "App04"),
        button(
          cls := "menu-toggle",
          cls <-- isMenuOpen_04.signal.map(if (_) "active" else ""),
          onClick --> (_ => isMenuOpen_04.update(!_)),
          span(cls := "hamburger"),
        ),
      ),
      ul(
        cls := "nav-items",
        cls <-- isMenuOpen_04.signal.map(if (_) "visible" else "hidden"),
        li(cls <-- activeTab_04.signal.map(t => if (t == "home") "active" else ""), a(href := "#home", onClick --> (_ => activeTab_04.set("home")), "Home")),
        li(cls <-- activeTab_04.signal.map(t => if (t == "about") "active" else ""), a(href := "#about", onClick --> (_ => activeTab_04.set("about")), "About")),
        li(cls <-- activeTab_04.signal.map(t => if (t == "contact") "active" else ""), a(href := "#contact", onClick --> (_ => activeTab_04.set("contact")), "Contact")),
        li(cls <-- activeTab_04.signal.map(t => if (t == "help") "active" else ""), a(href := "#help", onClick --> (_ => activeTab_04.set("help")), "Help")),
      ),
    )
  }

  def searchBox_04: HtmlElement = {
    div(
      cls := "search-container",
      label(cls := "search-label", "Search: "),
      input(
        cls := "search-input",
        typ := "text",
        placeholder := "Type to search...",
        value <-- searchQuery_04.signal,
        onInput.mapToValue --> searchQuery_04.writer,
        onKeyDown --> (_ => ()),
        onFocus --> (_ => ()),
        onBlur --> (_ => ()),
      ),
      span(
        cls := "search-count",
        child.text <-- itemCount_04,
      ),
      button(
        cls := "clear-btn",
        disabled <-- isEmptySearch_04,
        onClick --> (_ => searchQuery_04.set("")),
        "Clear",
      ),
    )
  }

  def itemList_04: HtmlElement = {
    div(
      cls := "item-list",
      children <-- filteredItems_04.map { items =>
        items.map { item =>
          div(
            cls := "item-card",
            cls <-- selectedId_04.signal.map(sel => if (sel.contains(item)) "selected" else ""),
            onClick --> (_ => selectedId_04.set(Some(item))),
            onDblClick --> (_ => clickBus_04.emit(item)),
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

  def statusBar_04: HtmlElement = {
    footerTag(
      cls := "status-bar",
      span(cls := "status-user", child.text <-- userName_04.signal),
      span(cls := "status-count", child.text <-- count_04.signal.map(_.toString)),
      span(cls := "status-tab", child.text <-- activeTab_04.signal),
      span(
        cls := "status-loading",
        cls <-- isLoading_04.signal.map(if (_) "spinning" else ""),
        child.text <-- isLoading_04.signal.map(if (_) "Loading..." else "Ready"),
      ),
    )
  }

  def contentPanel_04: HtmlElement = {
    mainTag(
      cls := "content",
      sectionTag(
        cls := "panel",
        headerTag(cls := "panel-header", h2("Panel 04")),
        div(
          cls := "panel-body",
          searchBox_04,
          itemList_04,
          div(
            cls := "panel-sidebar",
            child <-- selectedId_04.signal.map {
              case Some(id) => div(cls := "detail", h3("Selected"), p(id))
              case None => div(cls := "detail empty", p("Nothing selected"))
            },
          ),
        ),
        statusBar_04,
      ),
    )
  }

  def render04: HtmlElement = {
    div(
      cls := "app-container",
      idAttr := s"app-04",
      navBar_04,
      contentPanel_04,
      // Extra bindings to increase reactive density
      child.text <-- activeTab_04.signal.combineWith(count_04.signal).map { case (tab, c) => s"$tab:$c" },
      child.text <-- searchQuery_04.signal.combineWith(isLoading_04.signal).map { case (q, l) => s"$q:$l" },
    )
  }
}

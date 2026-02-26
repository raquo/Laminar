package bench

import com.raquo.laminar.api.L.{*, given}

object ReactiveDSL08 {

  // State
  val activeTab_08 = Var("home")
  val searchQuery_08 = Var("")
  val isMenuOpen_08 = Var(false)
  val selectedId_08 = Var(Option.empty[String])
  val count_08 = Var(0)
  val items_08 = Var(List("item1", "item2", "item3"))
  val userName_08 = Var("User")
  val isLoading_08 = Var(false)

  // Derived signals
  val filteredItems_08: Signal[List[String]] = items_08.signal.combineWith(searchQuery_08.signal).map {
    case (items, query) => items.filter(_.contains(query))
  }
  val itemCount_08: Signal[String] = filteredItems_08.map(items => s"${items.size} items")
  val isEmptySearch_08: Signal[Boolean] = searchQuery_08.signal.map(_.isEmpty)

  // Event buses
  val clickBus_08 = new EventBus[String]
  val submitBus_08 = new EventBus[Unit]

  // Observers
  val logObserver_08: Observer[String] = Observer[String](msg => ())
  val countObserver_08: Observer[Int] = Observer[Int](n => ())

  // Component functions generating deep element trees
  def navBar_08: HtmlElement = {
    navTag(
      cls := "navbar",
      role := "navigation",
      div(
        cls := "nav-brand",
        a(href := "#", cls := "brand-link", "App08"),
        button(
          cls := "menu-toggle",
          cls <-- isMenuOpen_08.signal.map(if (_) "active" else ""),
          onClick --> (_ => isMenuOpen_08.update(!_)),
          span(cls := "hamburger"),
        ),
      ),
      ul(
        cls := "nav-items",
        cls <-- isMenuOpen_08.signal.map(if (_) "visible" else "hidden"),
        li(cls <-- activeTab_08.signal.map(t => if (t == "home") "active" else ""), a(href := "#home", onClick --> (_ => activeTab_08.set("home")), "Home")),
        li(cls <-- activeTab_08.signal.map(t => if (t == "about") "active" else ""), a(href := "#about", onClick --> (_ => activeTab_08.set("about")), "About")),
        li(cls <-- activeTab_08.signal.map(t => if (t == "contact") "active" else ""), a(href := "#contact", onClick --> (_ => activeTab_08.set("contact")), "Contact")),
        li(cls <-- activeTab_08.signal.map(t => if (t == "help") "active" else ""), a(href := "#help", onClick --> (_ => activeTab_08.set("help")), "Help")),
      ),
    )
  }

  def searchBox_08: HtmlElement = {
    div(
      cls := "search-container",
      label(cls := "search-label", "Search: "),
      input(
        cls := "search-input",
        typ := "text",
        placeholder := "Type to search...",
        value <-- searchQuery_08.signal,
        onInput.mapToValue --> searchQuery_08.writer,
        onKeyDown --> (_ => ()),
        onFocus --> (_ => ()),
        onBlur --> (_ => ()),
      ),
      span(
        cls := "search-count",
        child.text <-- itemCount_08,
      ),
      button(
        cls := "clear-btn",
        disabled <-- isEmptySearch_08,
        onClick --> (_ => searchQuery_08.set("")),
        "Clear",
      ),
    )
  }

  def itemList_08: HtmlElement = {
    div(
      cls := "item-list",
      children <-- filteredItems_08.map { items =>
        items.map { item =>
          div(
            cls := "item-card",
            cls <-- selectedId_08.signal.map(sel => if (sel.contains(item)) "selected" else ""),
            onClick --> (_ => selectedId_08.set(Some(item))),
            onDblClick --> (_ => clickBus_08.emit(item)),
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

  def statusBar_08: HtmlElement = {
    footerTag(
      cls := "status-bar",
      span(cls := "status-user", child.text <-- userName_08.signal),
      span(cls := "status-count", child.text <-- count_08.signal.map(_.toString)),
      span(cls := "status-tab", child.text <-- activeTab_08.signal),
      span(
        cls := "status-loading",
        cls <-- isLoading_08.signal.map(if (_) "spinning" else ""),
        child.text <-- isLoading_08.signal.map(if (_) "Loading..." else "Ready"),
      ),
    )
  }

  def contentPanel_08: HtmlElement = {
    mainTag(
      cls := "content",
      sectionTag(
        cls := "panel",
        headerTag(cls := "panel-header", h2("Panel 08")),
        div(
          cls := "panel-body",
          searchBox_08,
          itemList_08,
          div(
            cls := "panel-sidebar",
            child <-- selectedId_08.signal.map {
              case Some(id) => div(cls := "detail", h3("Selected"), p(id))
              case None => div(cls := "detail empty", p("Nothing selected"))
            },
          ),
        ),
        statusBar_08,
      ),
    )
  }

  def render08: HtmlElement = {
    div(
      cls := "app-container",
      idAttr := s"app-08",
      navBar_08,
      contentPanel_08,
      // Extra bindings to increase reactive density
      child.text <-- activeTab_08.signal.combineWith(count_08.signal).map { case (tab, c) => s"$tab:$c" },
      child.text <-- searchQuery_08.signal.combineWith(isLoading_08.signal).map { case (q, l) => s"$q:$l" },
    )
  }
}

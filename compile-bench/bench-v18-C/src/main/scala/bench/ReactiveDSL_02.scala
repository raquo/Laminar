package bench

import com.raquo.laminar.api.L.{*, given}

object ReactiveDSL02 {

  // State
  val activeTab_02 = Var("home")
  val searchQuery_02 = Var("")
  val isMenuOpen_02 = Var(false)
  val selectedId_02 = Var(Option.empty[String])
  val count_02 = Var(0)
  val items_02 = Var(List("item1", "item2", "item3"))
  val userName_02 = Var("User")
  val isLoading_02 = Var(false)

  // Derived signals
  val filteredItems_02: Signal[List[String]] = items_02.signal.combineWith(searchQuery_02.signal).map {
    case (items, query) => items.filter(_.contains(query))
  }
  val itemCount_02: Signal[String] = filteredItems_02.map(items => s"${items.size} items")
  val isEmptySearch_02: Signal[Boolean] = searchQuery_02.signal.map(_.isEmpty)

  // Event buses
  val clickBus_02 = new EventBus[String]
  val submitBus_02 = new EventBus[Unit]

  // Observers
  val logObserver_02: Observer[String] = Observer[String](msg => ())
  val countObserver_02: Observer[Int] = Observer[Int](n => ())

  // Component functions generating deep element trees
  def navBar_02: HtmlElement = {
    navTag(
      cls := "navbar",
      role := "navigation",
      div(
        cls := "nav-brand",
        a(href := "#", cls := "brand-link", "App02"),
        button(
          cls := "menu-toggle",
          cls <-- isMenuOpen_02.signal.map(if (_) "active" else ""),
          onClick --> (_ => isMenuOpen_02.update(!_)),
          span(cls := "hamburger"),
        ),
      ),
      ul(
        cls := "nav-items",
        cls <-- isMenuOpen_02.signal.map(if (_) "visible" else "hidden"),
        li(cls <-- activeTab_02.signal.map(t => if (t == "home") "active" else ""), a(href := "#home", onClick --> (_ => activeTab_02.set("home")), "Home")),
        li(cls <-- activeTab_02.signal.map(t => if (t == "about") "active" else ""), a(href := "#about", onClick --> (_ => activeTab_02.set("about")), "About")),
        li(cls <-- activeTab_02.signal.map(t => if (t == "contact") "active" else ""), a(href := "#contact", onClick --> (_ => activeTab_02.set("contact")), "Contact")),
        li(cls <-- activeTab_02.signal.map(t => if (t == "help") "active" else ""), a(href := "#help", onClick --> (_ => activeTab_02.set("help")), "Help")),
      ),
    )
  }

  def searchBox_02: HtmlElement = {
    div(
      cls := "search-container",
      label(cls := "search-label", "Search: "),
      input(
        cls := "search-input",
        typ := "text",
        placeholder := "Type to search...",
        value <-- searchQuery_02.signal,
        onInput.mapToValue --> searchQuery_02.writer,
        onKeyDown --> (_ => ()),
        onFocus --> (_ => ()),
        onBlur --> (_ => ()),
      ),
      span(
        cls := "search-count",
        child.text <-- itemCount_02,
      ),
      button(
        cls := "clear-btn",
        disabled <-- isEmptySearch_02,
        onClick --> (_ => searchQuery_02.set("")),
        "Clear",
      ),
    )
  }

  def itemList_02: HtmlElement = {
    div(
      cls := "item-list",
      children <-- filteredItems_02.map { items =>
        items.map { item =>
          div(
            cls := "item-card",
            cls <-- selectedId_02.signal.map(sel => if (sel.contains(item)) "selected" else ""),
            onClick --> (_ => selectedId_02.set(Some(item))),
            onDblClick --> (_ => clickBus_02.emit(item)),
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

  def statusBar_02: HtmlElement = {
    footerTag(
      cls := "status-bar",
      span(cls := "status-user", child.text <-- userName_02.signal),
      span(cls := "status-count", child.text <-- count_02.signal.map(_.toString)),
      span(cls := "status-tab", child.text <-- activeTab_02.signal),
      span(
        cls := "status-loading",
        cls <-- isLoading_02.signal.map(if (_) "spinning" else ""),
        child.text <-- isLoading_02.signal.map(if (_) "Loading..." else "Ready"),
      ),
    )
  }

  def contentPanel_02: HtmlElement = {
    mainTag(
      cls := "content",
      sectionTag(
        cls := "panel",
        headerTag(cls := "panel-header", h2("Panel 02")),
        div(
          cls := "panel-body",
          searchBox_02,
          itemList_02,
          div(
            cls := "panel-sidebar",
            child <-- selectedId_02.signal.map {
              case Some(id) => div(cls := "detail", h3("Selected"), p(id))
              case None => div(cls := "detail empty", p("Nothing selected"))
            },
          ),
        ),
        statusBar_02,
      ),
    )
  }

  def render02: HtmlElement = {
    div(
      cls := "app-container",
      idAttr := s"app-02",
      navBar_02,
      contentPanel_02,
      // Extra bindings to increase reactive density
      child.text <-- activeTab_02.signal.combineWith(count_02.signal).map { case (tab, c) => s"$tab:$c" },
      child.text <-- searchQuery_02.signal.combineWith(isLoading_02.signal).map { case (q, l) => s"$q:$l" },
    )
  }
}

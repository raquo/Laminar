package bench

import com.raquo.laminar.api.L.{*, given}

object ReactiveDSL03 {

  // State
  val activeTab_03 = Var("home")
  val searchQuery_03 = Var("")
  val isMenuOpen_03 = Var(false)
  val selectedId_03 = Var(Option.empty[String])
  val count_03 = Var(0)
  val items_03 = Var(List("item1", "item2", "item3"))
  val userName_03 = Var("User")
  val isLoading_03 = Var(false)

  // Derived signals
  val filteredItems_03: Signal[List[String]] = items_03.signal.combineWith(searchQuery_03.signal).map {
    case (items, query) => items.filter(_.contains(query))
  }
  val itemCount_03: Signal[String] = filteredItems_03.map(items => s"${items.size} items")
  val isEmptySearch_03: Signal[Boolean] = searchQuery_03.signal.map(_.isEmpty)

  // Event buses
  val clickBus_03 = new EventBus[String]
  val submitBus_03 = new EventBus[Unit]

  // Observers
  val logObserver_03: Observer[String] = Observer[String](msg => ())
  val countObserver_03: Observer[Int] = Observer[Int](n => ())

  // Component functions generating deep element trees
  def navBar_03: HtmlElement = {
    navTag(
      cls := "navbar",
      role := "navigation",
      div(
        cls := "nav-brand",
        a(href := "#", cls := "brand-link", "App03"),
        button(
          cls := "menu-toggle",
          cls <-- isMenuOpen_03.signal.map(if (_) "active" else ""),
          onClick --> (_ => isMenuOpen_03.update(!_)),
          span(cls := "hamburger"),
        ),
      ),
      ul(
        cls := "nav-items",
        cls <-- isMenuOpen_03.signal.map(if (_) "visible" else "hidden"),
        li(cls <-- activeTab_03.signal.map(t => if (t == "home") "active" else ""), a(href := "#home", onClick --> (_ => activeTab_03.set("home")), "Home")),
        li(cls <-- activeTab_03.signal.map(t => if (t == "about") "active" else ""), a(href := "#about", onClick --> (_ => activeTab_03.set("about")), "About")),
        li(cls <-- activeTab_03.signal.map(t => if (t == "contact") "active" else ""), a(href := "#contact", onClick --> (_ => activeTab_03.set("contact")), "Contact")),
        li(cls <-- activeTab_03.signal.map(t => if (t == "help") "active" else ""), a(href := "#help", onClick --> (_ => activeTab_03.set("help")), "Help")),
      ),
    )
  }

  def searchBox_03: HtmlElement = {
    div(
      cls := "search-container",
      label(cls := "search-label", "Search: "),
      input(
        cls := "search-input",
        typ := "text",
        placeholder := "Type to search...",
        value <-- searchQuery_03.signal,
        onInput.mapToValue --> searchQuery_03.writer,
        onKeyDown --> (_ => ()),
        onFocus --> (_ => ()),
        onBlur --> (_ => ()),
      ),
      span(
        cls := "search-count",
        child.text <-- itemCount_03,
      ),
      button(
        cls := "clear-btn",
        disabled <-- isEmptySearch_03,
        onClick --> (_ => searchQuery_03.set("")),
        "Clear",
      ),
    )
  }

  def itemList_03: HtmlElement = {
    div(
      cls := "item-list",
      children <-- filteredItems_03.map { items =>
        items.map { item =>
          div(
            cls := "item-card",
            cls <-- selectedId_03.signal.map(sel => if (sel.contains(item)) "selected" else ""),
            onClick --> (_ => selectedId_03.set(Some(item))),
            onDblClick --> (_ => clickBus_03.emit(item)),
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

  def statusBar_03: HtmlElement = {
    footerTag(
      cls := "status-bar",
      span(cls := "status-user", child.text <-- userName_03.signal),
      span(cls := "status-count", child.text <-- count_03.signal.map(_.toString)),
      span(cls := "status-tab", child.text <-- activeTab_03.signal),
      span(
        cls := "status-loading",
        cls <-- isLoading_03.signal.map(if (_) "spinning" else ""),
        child.text <-- isLoading_03.signal.map(if (_) "Loading..." else "Ready"),
      ),
    )
  }

  def contentPanel_03: HtmlElement = {
    mainTag(
      cls := "content",
      sectionTag(
        cls := "panel",
        headerTag(cls := "panel-header", h2("Panel 03")),
        div(
          cls := "panel-body",
          searchBox_03,
          itemList_03,
          div(
            cls := "panel-sidebar",
            child <-- selectedId_03.signal.map {
              case Some(id) => div(cls := "detail", h3("Selected"), p(id))
              case None => div(cls := "detail empty", p("Nothing selected"))
            },
          ),
        ),
        statusBar_03,
      ),
    )
  }

  def render03: HtmlElement = {
    div(
      cls := "app-container",
      idAttr := s"app-03",
      navBar_03,
      contentPanel_03,
      // Extra bindings to increase reactive density
      child.text <-- activeTab_03.signal.combineWith(count_03.signal).map { case (tab, c) => s"$tab:$c" },
      child.text <-- searchQuery_03.signal.combineWith(isLoading_03.signal).map { case (q, l) => s"$q:$l" },
    )
  }
}

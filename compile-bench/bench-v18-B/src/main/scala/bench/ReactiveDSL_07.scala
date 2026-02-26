package bench

import com.raquo.laminar.api.L.{*, given}

object ReactiveDSL07 {

  // State
  val activeTab_07 = Var("home")
  val searchQuery_07 = Var("")
  val isMenuOpen_07 = Var(false)
  val selectedId_07 = Var(Option.empty[String])
  val count_07 = Var(0)
  val items_07 = Var(List("item1", "item2", "item3"))
  val userName_07 = Var("User")
  val isLoading_07 = Var(false)

  // Derived signals
  val filteredItems_07: Signal[List[String]] = items_07.signal.combineWith(searchQuery_07.signal).map {
    case (items, query) => items.filter(_.contains(query))
  }
  val itemCount_07: Signal[String] = filteredItems_07.map(items => s"${items.size} items")
  val isEmptySearch_07: Signal[Boolean] = searchQuery_07.signal.map(_.isEmpty)

  // Event buses
  val clickBus_07 = new EventBus[String]
  val submitBus_07 = new EventBus[Unit]

  // Observers
  val logObserver_07: Observer[String] = Observer[String](msg => ())
  val countObserver_07: Observer[Int] = Observer[Int](n => ())

  // Component functions generating deep element trees
  def navBar_07: HtmlElement = {
    navTag(
      cls := "navbar",
      role := "navigation",
      div(
        cls := "nav-brand",
        a(href := "#", cls := "brand-link", "App07"),
        button(
          cls := "menu-toggle",
          cls <-- isMenuOpen_07.signal.map(if (_) "active" else ""),
          onClick --> (_ => isMenuOpen_07.update(!_)),
          span(cls := "hamburger"),
        ),
      ),
      ul(
        cls := "nav-items",
        cls <-- isMenuOpen_07.signal.map(if (_) "visible" else "hidden"),
        li(cls <-- activeTab_07.signal.map(t => if (t == "home") "active" else ""), a(href := "#home", onClick --> (_ => activeTab_07.set("home")), "Home")),
        li(cls <-- activeTab_07.signal.map(t => if (t == "about") "active" else ""), a(href := "#about", onClick --> (_ => activeTab_07.set("about")), "About")),
        li(cls <-- activeTab_07.signal.map(t => if (t == "contact") "active" else ""), a(href := "#contact", onClick --> (_ => activeTab_07.set("contact")), "Contact")),
        li(cls <-- activeTab_07.signal.map(t => if (t == "help") "active" else ""), a(href := "#help", onClick --> (_ => activeTab_07.set("help")), "Help")),
      ),
    )
  }

  def searchBox_07: HtmlElement = {
    div(
      cls := "search-container",
      label(cls := "search-label", "Search: "),
      input(
        cls := "search-input",
        typ := "text",
        placeholder := "Type to search...",
        value <-- searchQuery_07.signal,
        onInput.mapToValue --> searchQuery_07.writer,
        onKeyDown --> (_ => ()),
        onFocus --> (_ => ()),
        onBlur --> (_ => ()),
      ),
      span(
        cls := "search-count",
        child.text <-- itemCount_07,
      ),
      button(
        cls := "clear-btn",
        disabled <-- isEmptySearch_07,
        onClick --> (_ => searchQuery_07.set("")),
        "Clear",
      ),
    )
  }

  def itemList_07: HtmlElement = {
    div(
      cls := "item-list",
      children <-- filteredItems_07.map { items =>
        items.map { item =>
          div(
            cls := "item-card",
            cls <-- selectedId_07.signal.map(sel => if (sel.contains(item)) "selected" else ""),
            onClick --> (_ => selectedId_07.set(Some(item))),
            onDblClick --> (_ => clickBus_07.emit(item)),
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

  def statusBar_07: HtmlElement = {
    footerTag(
      cls := "status-bar",
      span(cls := "status-user", child.text <-- userName_07.signal),
      span(cls := "status-count", child.text <-- count_07.signal.map(_.toString)),
      span(cls := "status-tab", child.text <-- activeTab_07.signal),
      span(
        cls := "status-loading",
        cls <-- isLoading_07.signal.map(if (_) "spinning" else ""),
        child.text <-- isLoading_07.signal.map(if (_) "Loading..." else "Ready"),
      ),
    )
  }

  def contentPanel_07: HtmlElement = {
    mainTag(
      cls := "content",
      sectionTag(
        cls := "panel",
        headerTag(cls := "panel-header", h2("Panel 07")),
        div(
          cls := "panel-body",
          searchBox_07,
          itemList_07,
          div(
            cls := "panel-sidebar",
            child <-- selectedId_07.signal.map {
              case Some(id) => div(cls := "detail", h3("Selected"), p(id))
              case None => div(cls := "detail empty", p("Nothing selected"))
            },
          ),
        ),
        statusBar_07,
      ),
    )
  }

  def render07: HtmlElement = {
    div(
      cls := "app-container",
      idAttr := s"app-07",
      navBar_07,
      contentPanel_07,
      // Extra bindings to increase reactive density
      child.text <-- activeTab_07.signal.combineWith(count_07.signal).map { case (tab, c) => s"$tab:$c" },
      child.text <-- searchQuery_07.signal.combineWith(isLoading_07.signal).map { case (q, l) => s"$q:$l" },
    )
  }
}

package bench

import com.raquo.laminar.api.L.{*, given}

object ReactiveDSL10 {

  // State
  val activeTab_10 = Var("home")
  val searchQuery_10 = Var("")
  val isMenuOpen_10 = Var(false)
  val selectedId_10 = Var(Option.empty[String])
  val count_10 = Var(0)
  val items_10 = Var(List("item1", "item2", "item3"))
  val userName_10 = Var("User")
  val isLoading_10 = Var(false)

  // Derived signals
  val filteredItems_10: Signal[List[String]] = items_10.signal.combineWith(searchQuery_10.signal).map {
    case (items, query) => items.filter(_.contains(query))
  }
  val itemCount_10: Signal[String] = filteredItems_10.map(items => s"${items.size} items")
  val isEmptySearch_10: Signal[Boolean] = searchQuery_10.signal.map(_.isEmpty)

  // Event buses
  val clickBus_10 = new EventBus[String]
  val submitBus_10 = new EventBus[Unit]

  // Observers
  val logObserver_10: Observer[String] = Observer[String](msg => ())
  val countObserver_10: Observer[Int] = Observer[Int](n => ())

  // Component functions generating deep element trees
  def navBar_10: HtmlElement = {
    navTag(
      cls := "navbar",
      role := "navigation",
      div(
        cls := "nav-brand",
        a(href := "#", cls := "brand-link", "App10"),
        button(
          cls := "menu-toggle",
          cls <-- isMenuOpen_10.signal.map(if (_) "active" else ""),
          onClick --> (_ => isMenuOpen_10.update(!_)),
          span(cls := "hamburger"),
        ),
      ),
      ul(
        cls := "nav-items",
        cls <-- isMenuOpen_10.signal.map(if (_) "visible" else "hidden"),
        li(cls <-- activeTab_10.signal.map(t => if (t == "home") "active" else ""), a(href := "#home", onClick --> (_ => activeTab_10.set("home")), "Home")),
        li(cls <-- activeTab_10.signal.map(t => if (t == "about") "active" else ""), a(href := "#about", onClick --> (_ => activeTab_10.set("about")), "About")),
        li(cls <-- activeTab_10.signal.map(t => if (t == "contact") "active" else ""), a(href := "#contact", onClick --> (_ => activeTab_10.set("contact")), "Contact")),
        li(cls <-- activeTab_10.signal.map(t => if (t == "help") "active" else ""), a(href := "#help", onClick --> (_ => activeTab_10.set("help")), "Help")),
      ),
    )
  }

  def searchBox_10: HtmlElement = {
    div(
      cls := "search-container",
      label(cls := "search-label", "Search: "),
      input(
        cls := "search-input",
        typ := "text",
        placeholder := "Type to search...",
        value <-- searchQuery_10.signal,
        onInput.mapToValue --> searchQuery_10.writer,
        onKeyDown --> (_ => ()),
        onFocus --> (_ => ()),
        onBlur --> (_ => ()),
      ),
      span(
        cls := "search-count",
        child.text <-- itemCount_10,
      ),
      button(
        cls := "clear-btn",
        disabled <-- isEmptySearch_10,
        onClick --> (_ => searchQuery_10.set("")),
        "Clear",
      ),
    )
  }

  def itemList_10: HtmlElement = {
    div(
      cls := "item-list",
      children <-- filteredItems_10.map { items =>
        items.map { item =>
          div(
            cls := "item-card",
            cls <-- selectedId_10.signal.map(sel => if (sel.contains(item)) "selected" else ""),
            onClick --> (_ => selectedId_10.set(Some(item))),
            onDblClick --> (_ => clickBus_10.emit(item)),
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

  def statusBar_10: HtmlElement = {
    footerTag(
      cls := "status-bar",
      span(cls := "status-user", child.text <-- userName_10.signal),
      span(cls := "status-count", child.text <-- count_10.signal.map(_.toString)),
      span(cls := "status-tab", child.text <-- activeTab_10.signal),
      span(
        cls := "status-loading",
        cls <-- isLoading_10.signal.map(if (_) "spinning" else ""),
        child.text <-- isLoading_10.signal.map(if (_) "Loading..." else "Ready"),
      ),
    )
  }

  def contentPanel_10: HtmlElement = {
    mainTag(
      cls := "content",
      sectionTag(
        cls := "panel",
        headerTag(cls := "panel-header", h2("Panel 10")),
        div(
          cls := "panel-body",
          searchBox_10,
          itemList_10,
          div(
            cls := "panel-sidebar",
            child <-- selectedId_10.signal.map {
              case Some(id) => div(cls := "detail", h3("Selected"), p(id))
              case None => div(cls := "detail empty", p("Nothing selected"))
            },
          ),
        ),
        statusBar_10,
      ),
    )
  }

  def render10: HtmlElement = {
    div(
      cls := "app-container",
      idAttr := s"app-10",
      navBar_10,
      contentPanel_10,
      // Extra bindings to increase reactive density
      child.text <-- activeTab_10.signal.combineWith(count_10.signal).map { case (tab, c) => s"$tab:$c" },
      child.text <-- searchQuery_10.signal.combineWith(isLoading_10.signal).map { case (q, l) => s"$q:$l" },
    )
  }
}

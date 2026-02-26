package bench

import com.raquo.laminar.api.L.{*, given}

object ReactiveDSL09 {

  // State
  val activeTab_09 = Var("home")
  val searchQuery_09 = Var("")
  val isMenuOpen_09 = Var(false)
  val selectedId_09 = Var(Option.empty[String])
  val count_09 = Var(0)
  val items_09 = Var(List("item1", "item2", "item3"))
  val userName_09 = Var("User")
  val isLoading_09 = Var(false)

  // Derived signals
  val filteredItems_09: Signal[List[String]] = items_09.signal.combineWith(searchQuery_09.signal).map {
    case (items, query) => items.filter(_.contains(query))
  }
  val itemCount_09: Signal[String] = filteredItems_09.map(items => s"${items.size} items")
  val isEmptySearch_09: Signal[Boolean] = searchQuery_09.signal.map(_.isEmpty)

  // Event buses
  val clickBus_09 = new EventBus[String]
  val submitBus_09 = new EventBus[Unit]

  // Observers
  val logObserver_09: Observer[String] = Observer[String](msg => ())
  val countObserver_09: Observer[Int] = Observer[Int](n => ())

  // Component functions generating deep element trees
  def navBar_09: HtmlElement = {
    navTag(
      cls := "navbar",
      role := "navigation",
      div(
        cls := "nav-brand",
        a(href := "#", cls := "brand-link", "App09"),
        button(
          cls := "menu-toggle",
          cls <-- isMenuOpen_09.signal.map(if (_) "active" else ""),
          onClick --> (_ => isMenuOpen_09.update(!_)),
          span(cls := "hamburger"),
        ),
      ),
      ul(
        cls := "nav-items",
        cls <-- isMenuOpen_09.signal.map(if (_) "visible" else "hidden"),
        li(cls <-- activeTab_09.signal.map(t => if (t == "home") "active" else ""), a(href := "#home", onClick --> (_ => activeTab_09.set("home")), "Home")),
        li(cls <-- activeTab_09.signal.map(t => if (t == "about") "active" else ""), a(href := "#about", onClick --> (_ => activeTab_09.set("about")), "About")),
        li(cls <-- activeTab_09.signal.map(t => if (t == "contact") "active" else ""), a(href := "#contact", onClick --> (_ => activeTab_09.set("contact")), "Contact")),
        li(cls <-- activeTab_09.signal.map(t => if (t == "help") "active" else ""), a(href := "#help", onClick --> (_ => activeTab_09.set("help")), "Help")),
      ),
    )
  }

  def searchBox_09: HtmlElement = {
    div(
      cls := "search-container",
      label(cls := "search-label", "Search: "),
      input(
        cls := "search-input",
        typ := "text",
        placeholder := "Type to search...",
        value <-- searchQuery_09.signal,
        onInput.mapToValue --> searchQuery_09.writer,
        onKeyDown --> (_ => ()),
        onFocus --> (_ => ()),
        onBlur --> (_ => ()),
      ),
      span(
        cls := "search-count",
        child.text <-- itemCount_09,
      ),
      button(
        cls := "clear-btn",
        disabled <-- isEmptySearch_09,
        onClick --> (_ => searchQuery_09.set("")),
        "Clear",
      ),
    )
  }

  def itemList_09: HtmlElement = {
    div(
      cls := "item-list",
      children <-- filteredItems_09.map { items =>
        items.map { item =>
          div(
            cls := "item-card",
            cls <-- selectedId_09.signal.map(sel => if (sel.contains(item)) "selected" else ""),
            onClick --> (_ => selectedId_09.set(Some(item))),
            onDblClick --> (_ => clickBus_09.emit(item)),
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

  def statusBar_09: HtmlElement = {
    footerTag(
      cls := "status-bar",
      span(cls := "status-user", child.text <-- userName_09.signal),
      span(cls := "status-count", child.text <-- count_09.signal.map(_.toString)),
      span(cls := "status-tab", child.text <-- activeTab_09.signal),
      span(
        cls := "status-loading",
        cls <-- isLoading_09.signal.map(if (_) "spinning" else ""),
        child.text <-- isLoading_09.signal.map(if (_) "Loading..." else "Ready"),
      ),
    )
  }

  def contentPanel_09: HtmlElement = {
    mainTag(
      cls := "content",
      sectionTag(
        cls := "panel",
        headerTag(cls := "panel-header", h2("Panel 09")),
        div(
          cls := "panel-body",
          searchBox_09,
          itemList_09,
          div(
            cls := "panel-sidebar",
            child <-- selectedId_09.signal.map {
              case Some(id) => div(cls := "detail", h3("Selected"), p(id))
              case None => div(cls := "detail empty", p("Nothing selected"))
            },
          ),
        ),
        statusBar_09,
      ),
    )
  }

  def render09: HtmlElement = {
    div(
      cls := "app-container",
      idAttr := s"app-09",
      navBar_09,
      contentPanel_09,
      // Extra bindings to increase reactive density
      child.text <-- activeTab_09.signal.combineWith(count_09.signal).map { case (tab, c) => s"$tab:$c" },
      child.text <-- searchQuery_09.signal.combineWith(isLoading_09.signal).map { case (q, l) => s"$q:$l" },
    )
  }
}

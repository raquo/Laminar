package bench

import com.raquo.laminar.api.L.{*, given}

object ReactiveDSL05 {

  // State
  val activeTab_05 = Var("home")
  val searchQuery_05 = Var("")
  val isMenuOpen_05 = Var(false)
  val selectedId_05 = Var(Option.empty[String])
  val count_05 = Var(0)
  val items_05 = Var(List("item1", "item2", "item3"))
  val userName_05 = Var("User")
  val isLoading_05 = Var(false)

  // Derived signals
  val filteredItems_05: Signal[List[String]] = items_05.signal.combineWith(searchQuery_05.signal).map {
    case (items, query) => items.filter(_.contains(query))
  }
  val itemCount_05: Signal[String] = filteredItems_05.map(items => s"${items.size} items")
  val isEmptySearch_05: Signal[Boolean] = searchQuery_05.signal.map(_.isEmpty)

  // Event buses
  val clickBus_05 = new EventBus[String]
  val submitBus_05 = new EventBus[Unit]

  // Observers
  val logObserver_05: Observer[String] = Observer[String](msg => ())
  val countObserver_05: Observer[Int] = Observer[Int](n => ())

  // Component functions generating deep element trees
  def navBar_05: HtmlElement = {
    navTag(
      cls := "navbar",
      role := "navigation",
      div(
        cls := "nav-brand",
        a(href := "#", cls := "brand-link", "App05"),
        button(
          cls := "menu-toggle",
          cls <-- isMenuOpen_05.signal.map(if (_) "active" else ""),
          onClick --> (_ => isMenuOpen_05.update(!_)),
          span(cls := "hamburger"),
        ),
      ),
      ul(
        cls := "nav-items",
        cls <-- isMenuOpen_05.signal.map(if (_) "visible" else "hidden"),
        li(cls <-- activeTab_05.signal.map(t => if (t == "home") "active" else ""), a(href := "#home", onClick --> (_ => activeTab_05.set("home")), "Home")),
        li(cls <-- activeTab_05.signal.map(t => if (t == "about") "active" else ""), a(href := "#about", onClick --> (_ => activeTab_05.set("about")), "About")),
        li(cls <-- activeTab_05.signal.map(t => if (t == "contact") "active" else ""), a(href := "#contact", onClick --> (_ => activeTab_05.set("contact")), "Contact")),
        li(cls <-- activeTab_05.signal.map(t => if (t == "help") "active" else ""), a(href := "#help", onClick --> (_ => activeTab_05.set("help")), "Help")),
      ),
    )
  }

  def searchBox_05: HtmlElement = {
    div(
      cls := "search-container",
      label(cls := "search-label", "Search: "),
      input(
        cls := "search-input",
        typ := "text",
        placeholder := "Type to search...",
        value <-- searchQuery_05.signal,
        onInput.mapToValue --> searchQuery_05.writer,
        onKeyDown --> (_ => ()),
        onFocus --> (_ => ()),
        onBlur --> (_ => ()),
      ),
      span(
        cls := "search-count",
        child.text <-- itemCount_05,
      ),
      button(
        cls := "clear-btn",
        disabled <-- isEmptySearch_05,
        onClick --> (_ => searchQuery_05.set("")),
        "Clear",
      ),
    )
  }

  def itemList_05: HtmlElement = {
    div(
      cls := "item-list",
      children <-- filteredItems_05.map { items =>
        items.map { item =>
          div(
            cls := "item-card",
            cls <-- selectedId_05.signal.map(sel => if (sel.contains(item)) "selected" else ""),
            onClick --> (_ => selectedId_05.set(Some(item))),
            onDblClick --> (_ => clickBus_05.emit(item)),
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

  def statusBar_05: HtmlElement = {
    footerTag(
      cls := "status-bar",
      span(cls := "status-user", child.text <-- userName_05.signal),
      span(cls := "status-count", child.text <-- count_05.signal.map(_.toString)),
      span(cls := "status-tab", child.text <-- activeTab_05.signal),
      span(
        cls := "status-loading",
        cls <-- isLoading_05.signal.map(if (_) "spinning" else ""),
        child.text <-- isLoading_05.signal.map(if (_) "Loading..." else "Ready"),
      ),
    )
  }

  def contentPanel_05: HtmlElement = {
    mainTag(
      cls := "content",
      sectionTag(
        cls := "panel",
        headerTag(cls := "panel-header", h2("Panel 05")),
        div(
          cls := "panel-body",
          searchBox_05,
          itemList_05,
          div(
            cls := "panel-sidebar",
            child <-- selectedId_05.signal.map {
              case Some(id) => div(cls := "detail", h3("Selected"), p(id))
              case None => div(cls := "detail empty", p("Nothing selected"))
            },
          ),
        ),
        statusBar_05,
      ),
    )
  }

  def render05: HtmlElement = {
    div(
      cls := "app-container",
      idAttr := s"app-05",
      navBar_05,
      contentPanel_05,
      // Extra bindings to increase reactive density
      child.text <-- activeTab_05.signal.combineWith(count_05.signal).map { case (tab, c) => s"$tab:$c" },
      child.text <-- searchQuery_05.signal.combineWith(isLoading_05.signal).map { case (q, l) => s"$q:$l" },
    )
  }
}

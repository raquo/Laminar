package bench

import com.raquo.laminar.api.L.{*, given}

object MixedComponent01 {

  // -- State --
  val documents_01 = Var(List.empty[PdfObject])
  val selectedDoc_01 = Var(Option.empty[String])
  val searchTerm_01 = Var("")
  val viewMode_01 = Var("grid")
  val zoomLevel_01 = Var(100)
  val sidebarOpen_01 = Var(true)
  val statusMsg_01 = Var("Ready")
  val pageNum_01 = Var(1)
  val darkMode_01 = Var(false)
  val sortField_01 = Var("id")

  // -- Event buses --
  val actionBus_01 = new EventBus[String]
  val navBus_01 = new EventBus[Int]

  // -- Derived signals (combineWith heavy) --
  val filteredDocs_01: Signal[List[PdfObject]] =
    documents_01.signal.combineWith(searchTerm_01.signal).map { case (docs, term) =>
      if (term.isEmpty) docs else docs.filter(_.label.toLowerCase.contains(term.toLowerCase))
    }

  val sortedDocs_01: Signal[List[PdfObject]] =
    filteredDocs_01.combineWith(sortField_01.signal).map { case (docs, field) =>
      field match {
        case "id" => docs.sortBy(_.id)
        case "label" => docs.sortBy(_.label)
        case _ => docs
      }
    }

  val docCount_01: Signal[String] = sortedDocs_01.map(d => s"${d.size} documents")

  val headerInfo_01: Signal[String] =
    searchTerm_01.signal.combineWith(viewMode_01.signal, zoomLevel_01.signal).map {
      case (q, mode, zoom) => s"Search: $q | View: $mode | Zoom: $zoom%"
    }

  val panelStyle_01: Signal[String] =
    darkMode_01.signal.combineWith(sidebarOpen_01.signal, zoomLevel_01.signal).map {
      case (dark, sidebar, zoom) =>
        val bg = if (dark) "#1e1e2e" else "#fafafa"
        val w = if (sidebar) "calc(100% - 280px)" else "100%"
        s"background:$bg;width:$w;zoom:${zoom}%"
    }

  // -- Renderers using split --
  def renderDocItem_01(id: String, initial: PdfObject, docSignal: Signal[PdfObject]): HtmlElement = {
    div(
      cls := "doc-item",
      cls <-- selectedDoc_01.signal.map(sel => if (sel.contains(id)) "selected" else ""),
      dataAttr("doc-id") := id,
      padding.px := 12,
      margin.px := 4,
      borderRadius.px := 6,
      cursor := "pointer",
      backgroundColor <-- selectedDoc_01.signal.combineWith(darkMode_01.signal).map {
        case (Some(sid), dark) if sid == id => if (dark) "#3d3d5c" else "#e3f2fd"
        case (_, dark) => if (dark) "#2d2d44" else "#ffffff"
      },
      onClick --> (_ => selectedDoc_01.set(Some(id))),

      div(
        display := "flex",
        justifyContent := "space-between",
        alignItems := "center",
        span(
          fontWeight := "600",
          fontSize.px := 14,
          child.text <-- docSignal.map(_.label),
        ),
        span(
          fontSize.px := 11,
          opacity := "0.6",
          child.text <-- docSignal.map(_.id),
        ),
      ),
      div(
        fontSize.px := 12,
        marginTop.px := 4,
        color <-- darkMode_01.signal.map(if (_) "#aaa" else "#666"),
        child.text <-- docSignal.map(d => d.getClass.getSimpleName),
      ),
    )
  }

  def docListPanel_01: HtmlElement = {
    div(
      cls := "doc-list-01",
      display := "flex",
      flexDirection := "column",
      gap := "4px",
      padding.px := 8,
      overflowY := "auto",
      maxHeight.px := 600,
      children <-- sortedDocs_01.split(_.id) { (id, initial, docSignal) =>
        renderDocItem_01(id, initial, docSignal)
      },
    )
  }

  // -- Detail panel with pattern matching --
  def detailPanel_01: HtmlElement = {
    div(
      cls := "detail-panel-01",
      padding.px := 16,
      minWidth.px := 300,
      borderLeft := "1px solid #ddd",
      child <-- selectedDoc_01.signal.combineWith(documents_01.signal).map { case (selOpt, docs) =>
        selOpt.flatMap(id => docs.find(_.id == id)) match {
          case Some(page: PdfPage) => div(
            h3("Page Details"),
            p(s"Page: ${page.pageNum}"),
            p(s"Size: ${page.width} x ${page.height}"),
            p(s"Label: ${page.label}"),
          )
          case Some(text: PdfText) => div(
            h3("Text Details"),
            p(s"Content: ${text.content}"),
            p(s"Font: ${text.fontFamily} ${text.fontSize}px"),
          )
          case Some(img: PdfImage) => div(
            h3("Image Details"),
            p(s"Source: ${img.src}"),
            p(s"Alt: ${img.altText}"),
            p(s"Width: ${img.width}"),
          )
          case Some(table: PdfTable) => div(
            h3("Table Details"),
            p(s"Size: ${table.rows} x ${table.cols}"),
          )
          case Some(obj) => div(
            h3(obj.getClass.getSimpleName),
            p(s"ID: ${obj.id}"),
            p(s"Label: ${obj.label}"),
          )
          case None => div(
            cls := "no-selection",
            padding.px := 40,
            textAlign := "center",
            color := "#999",
            p("Select a document to view details"),
          )
        }
      },
    )
  }

  // -- Toolbar (style-heavy + reactive) --
  def toolbar_01: HtmlElement = {
    div(
      cls := "toolbar-01",
      display := "flex",
      alignItems := "center",
      gap := "12px",
      padding.px := 12,
      backgroundColor <-- darkMode_01.signal.map(if (_) "#2d2d44" else "#f0f0f0"),
      borderBottom := "1px solid #ccc",

      input(
        typ := "text",
        placeholder := "Search documents...",
        padding.px := 8,
        fontSize.px := 14,
        borderRadius.px := 4,
        borderWidth.px := 1,
        borderStyle := "solid",
        borderColor := "#ccc",
        width.px := 200,
        onInput.mapToValue --> searchTerm_01.writer,
      ),

      button(
        padding.px := 8, fontSize.px := 13, borderRadius.px := 4,
        border := "none", cursor := "pointer",
        backgroundColor := "#2196F3", color := "white",
        onClick --> (_ => viewMode_01.update(m => if (m == "grid") "list" else "grid")),
        child.text <-- viewMode_01.signal.map(m => if (m == "grid") "List View" else "Grid View"),
      ),

      button(
        padding.px := 8, fontSize.px := 13, borderRadius.px := 4,
        border := "none", cursor := "pointer",
        backgroundColor := "#4CAF50", color := "white",
        onClick --> (_ => zoomLevel_01.update(z => if (z >= 200) 50 else z + 25)),
        child.text <-- zoomLevel_01.signal.map(z => s"Zoom: $z%"),
      ),

      button(
        padding.px := 8, fontSize.px := 13, borderRadius.px := 4,
        border := "none", cursor := "pointer",
        backgroundColor := "#FF9800", color := "white",
        onClick --> (_ => sidebarOpen_01.update(!_)),
        child.text <-- sidebarOpen_01.signal.map(if (_) "Hide Sidebar" else "Show Sidebar"),
      ),

      button(
        padding.px := 8, fontSize.px := 13, borderRadius.px := 4,
        border := "none", cursor := "pointer",
        backgroundColor := "#9C27B0", color := "white",
        onClick --> (_ => darkMode_01.update(!_)),
        child.text <-- darkMode_01.signal.map(if (_) "Light" else "Dark"),
      ),

      span(
        marginLeft := "auto",
        fontSize.px := 12,
        opacity := "0.7",
        child.text <-- docCount_01,
      ),
    )
  }

  // -- Status bar --
  def statusBar_01: HtmlElement = {
    footerTag(
      cls := "status-01",
      display := "flex",
      justifyContent := "space-between",
      padding.px := 8,
      fontSize.px := 12,
      backgroundColor <-- darkMode_01.signal.map(if (_) "#1a1a2e" else "#e8e8e8"),
      color <-- darkMode_01.signal.map(if (_) "#aaa" else "#666"),
      borderTop := "1px solid #ccc",

      span(child.text <-- statusMsg_01.signal),
      span(child.text <-- headerInfo_01),
      span(child.text <-- pageNum_01.signal.map(p => s"Page $p")),
    )
  }

  // -- Main render --
  def render01: HtmlElement = {
    div(
      cls := "mixed-app-01",
      display := "flex",
      flexDirection := "column",
      minHeight.px := 800,
      fontFamily := "system-ui, sans-serif",
      backgroundColor <-- darkMode_01.signal.map(if (_) "#1e1e2e" else "#fafafa"),
      color <-- darkMode_01.signal.map(if (_) "#e0e0e0" else "#333"),

      toolbar_01,
      div(
        display := "flex",
        flex := "1",
        child <-- sidebarOpen_01.signal.map { open =>
          if (open) div(
            display := "flex",
            width := "100%",
            docListPanel_01,
            detailPanel_01,
          )
          else div(
            width := "100%",
            docListPanel_01,
          )
        },
      ),
      statusBar_01,
    )
  }
}

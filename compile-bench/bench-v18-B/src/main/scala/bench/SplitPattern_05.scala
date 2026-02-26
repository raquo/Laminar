package bench

import com.raquo.laminar.api.L.{*, given}

object SplitPattern05 {

  // State with PdfObject lists
  val objects_05 = Var(List.empty[PdfObject])
  val pages_05 = Var(List.empty[PdfPage])
  val texts_05 = Var(List.empty[PdfText])
  val statusFilter_05 = Var(Option.empty[String])
  val sortAsc_05 = Var(true)

  // Derived signals
  val filteredObjects_05: Signal[List[PdfObject]] =
    objects_05.signal.combineWith(statusFilter_05.signal).map { case (objs, filter) =>
      filter.fold(objs)(f => objs.filter(_.label.contains(f)))
    }

  val sortedObjects_05: Signal[List[PdfObject]] =
    filteredObjects_05.combineWith(sortAsc_05.signal).map { case (objs, asc) =>
      if (asc) objs.sortBy(_.id) else objs.sortBy(_.id).reverse
    }

  // Heavy pattern matching renderer for a single PdfObject
  def renderObject_05(obj: PdfObject): HtmlElement = obj match {
    case PdfPage(id, label, num, w, h) =>
      div(cls := "pdf-page", dataAttr("id") := id, span(s"Page $num"), span(s"${w}x${h}"), p(label))
    case PdfText(id, label, content, fontSize, fontFamily) =>
      div(cls := "pdf-text", dataAttr("id") := id, span(s"Text: $content"), span(s"${fontSize}px $fontFamily"), p(label))
    case PdfImage(id, label, src, altText, w) =>
      div(cls := "pdf-image", dataAttr("id") := id, span(s"Image: $altText"), span(s"width=$w"), p(label))
    case PdfTable(id, label, rows, cols) =>
      div(cls := "pdf-table", dataAttr("id") := id, span(s"Table ${rows}x${cols}"), p(label))
    case PdfHeader(id, label, level, text) =>
      div(cls := "pdf-header", dataAttr("id") := id, span(s"H$level: $text"), p(label))
    case PdfFooter(id, label, text, pageNum) =>
      div(cls := "pdf-footer", dataAttr("id") := id, span(s"Footer p$pageNum: $text"), p(label))
    case PdfLink(id, label, linkHref, title) =>
      div(cls := "pdf-link", dataAttr("id") := id, a(href := linkHref, title), p(label))
    case PdfAnnotation(id, label, note, author) =>
      div(cls := "pdf-annotation", dataAttr("id") := id, span(s"$author: $note"), p(label))
    case PdfBookmark(id, label, target, depth) =>
      div(cls := "pdf-bookmark", dataAttr("id") := id, span(s"Bookmark[$depth]: $target"), p(label))
    case PdfMetadata(id, label, key, value) =>
      div(cls := "pdf-metadata", dataAttr("id") := id, span(s"$key=$value"), p(label))
    case PdfShape(id, label, shapeType, x, y) =>
      div(cls := "pdf-shape", dataAttr("id") := id, span(s"$shapeType at ($x,$y)"), p(label))
    case PdfChart(id, label, chartType, dataPoints) =>
      div(cls := "pdf-chart", dataAttr("id") := id, span(s"$chartType ($dataPoints pts)"), p(label))
    case PdfFormField(id, label, fieldType, required) =>
      div(cls := "pdf-form-field", dataAttr("id") := id, span(s"$fieldType${if (required) "*" else ""}"), p(label))
    case PdfSignature(id, label, signer, timestamp) =>
      div(cls := "pdf-signature", dataAttr("id") := id, span(s"Signed by $signer"), p(label))
    case PdfWatermark(id, label, text, opacity) =>
      div(cls := "pdf-watermark", dataAttr("id") := id, span(s"WM: $text ($opacity)"), p(label))
  }

  // Signal-based renderer for a single PdfObject
  def renderObjectSignal_05(id: String, initial: PdfObject, objSignal: Signal[PdfObject]): HtmlElement = {
    div(
      cls := "object-row",
      dataAttr("key") := id,
      child.text <-- objSignal.map(_.label),
      child <-- objSignal.map(renderObject_05),
    )
  }

  // Main split-based list rendering
  def objectList_05: HtmlElement = {
    div(
      cls := "object-list-05",
      children <-- sortedObjects_05.split(_.id) { (id, initial, objSignal) =>
        renderObjectSignal_05(id, initial, objSignal)
      },
    )
  }

  // Split on pages specifically
  def pageList_05: HtmlElement = {
    div(
      cls := "page-list-05",
      children <-- pages_05.signal.split(_.id) { (id, initial, pageSignal) =>
        div(
          cls := "page-item",
          dataAttr("page-id") := id,
          child.text <-- pageSignal.map(p => s"Page ${p.pageNum}: ${p.label}"),
          span(child.text <-- pageSignal.map(p => s"${p.width}x${p.height}")),
        )
      },
    )
  }

  // Split on texts
  def textList_05: HtmlElement = {
    div(
      cls := "text-list-05",
      children <-- texts_05.signal.split(_.id) { (id, initial, textSignal) =>
        div(
          cls := "text-item",
          dataAttr("text-id") := id,
          child.text <-- textSignal.map(t => s"${t.content} (${t.fontSize}px)"),
          span(child.text <-- textSignal.map(_.fontFamily)),
        )
      },
    )
  }

  // Nested split: objects containing sub-lists
  def nestedSplit_05: HtmlElement = {
    val grouped_05 = sortedObjects_05.map { objs =>
      objs.groupBy(_.getClass.getSimpleName).toList.sortBy(_._1)
    }
    div(
      cls := "nested-split-05",
      children <-- grouped_05.split(_._1) { (groupName, initial, groupSignal) =>
        div(
          cls := "group",
          h3(cls := "group-title", groupName),
          div(
            cls := "group-items",
            children <-- groupSignal.map(_._2).split(_.id) { (id, initial, objSignal) =>
              renderObjectSignal_05(id, initial, objSignal)
            },
          ),
        )
      },
    )
  }

  // Combined render with toolbar
  def render05: HtmlElement = {
    div(
      cls := "split-container-05",
      div(
        cls := "toolbar",
        input(
          typ := "text",
          placeholder := "Filter...",
          onInput.mapToValue --> (v => statusFilter_05.set(if (v.isEmpty) None else Some(v))),
        ),
        button(onClick --> (_ => sortAsc_05.update(!_)), "Toggle Sort"),
        span(child.text <-- sortedObjects_05.map(o => s"${o.size} objects")),
      ),
      objectList_05,
      pageList_05,
      textList_05,
      nestedSplit_05,
    )
  }
}

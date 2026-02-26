package micro

import com.raquo.laminar.api.L.{*, given}

object MicroStyleProps {

  val dark = Var(false)
  val scale = Var(1.0)
  val expanded = Var(false)

  val bgSig: Signal[String] = dark.signal.map(if (_) "#1a1a2e" else "#ffffff")
  val fgSig: Signal[String] = dark.signal.map(if (_) "#e0e0e0" else "#333333")
  val sizeSig: Signal[Int] = scale.signal.map(s => (16 * s).toInt)
  val widthSig: Signal[Int] = expanded.signal.map(if (_) 1200 else 800)

  def render: HtmlElement = {
    div(
      display := "flex",
      flexDirection := "column",
      minHeight.px := 600,
      fontFamily := "system-ui, sans-serif",
      backgroundColor <-- bgSig,
      color <-- fgSig,
      transition := "all 0.3s ease",

      // Toolbar
      div(
        display := "flex",
        alignItems := "center",
        gap := "12px",
        padding.px := 16,
        backgroundColor <-- dark.signal.map(if (_) "#2d2d44" else "#f5f5f5"),
        borderBottom := "1px solid #ddd",
        button(padding.px := 8, fontSize.px := 14, borderRadius.px := 4, border := "none", backgroundColor := "#4CAF50", color := "white", cursor := "pointer", "Toggle"),
        button(padding.px := 8, fontSize.px := 14, borderRadius.px := 4, border := "none", backgroundColor := "#2196F3", color := "white", cursor := "pointer", "Expand"),
        button(padding.px := 8, fontSize.px := 14, borderRadius.px := 4, border := "none", backgroundColor := "#FF9800", color := "white", cursor := "pointer", "Scale"),
      ),

      // Cards container
      div(
        display := "flex",
        flexWrap := "wrap",
        gap := "16px",
        padding.px := 24,
        width.px <-- widthSig,

        // Card 1
        div(
          display := "flex", flexDirection := "column", position := "relative",
          overflow := "hidden", borderRadius.px := 8,
          boxShadow := "0 2px 8px rgba(0,0,0,0.1)", transition := "all 0.3s ease",
          backgroundColor <-- bgSig, color <-- fgSig,
          fontSize.px <-- sizeSig, width.px <-- widthSig.map(_ / 3),
          padding.px <-- scale.signal.map(s => (16 * s).toInt),
          margin.px <-- scale.signal.map(s => (8 * s).toInt),
          borderWidth.px := 1, borderStyle := "solid", borderColor := "#ddd",
          div(display := "flex", justifyContent := "space-between", alignItems := "center",
            padding.px := 12, borderBottom := "1px solid #eee",
            span(fontWeight := "600", fontSize.px := 18, letterSpacing := "0.5px", textTransform := "uppercase", "Card A"),
            span(fontSize.px := 12, opacity := "0.7", fontStyle := "italic", "#1"),
          ),
          div(padding.px := 16, lineHeight := "1.6", minHeight.px := 120,
            maxHeight.px <-- expanded.signal.map(if (_) 500 else 200), overflow := "auto",
            p(margin.px := 0, padding.px := 8, textAlign := "left", "Content A"),
          ),
          div(display := "flex", justifyContent := "flex-end", gap := "8px", padding.px := 12, borderTop := "1px solid #eee",
            button(padding.px := 8, paddingLeft.px := 16, paddingRight.px := 16, borderRadius.px := 4,
              border := "none", backgroundColor := "#2196F3", color := "white", fontWeight := "500", fontSize.px := 14, cursor := "pointer", "Action"),
            button(padding.px := 8, paddingLeft.px := 16, paddingRight.px := 16, borderRadius.px := 4,
              borderWidth.px := 1, borderStyle := "solid", borderColor := "#2196F3",
              backgroundColor := "transparent", color := "#2196F3", fontWeight := "500", fontSize.px := 14, cursor := "pointer", "Cancel"),
          ),
        ),

        // Card 2
        div(
          display := "flex", flexDirection := "column", position := "relative",
          overflow := "hidden", borderRadius.px := 8,
          boxShadow := "0 2px 8px rgba(0,0,0,0.1)", transition := "all 0.3s ease",
          backgroundColor <-- bgSig, color <-- fgSig,
          fontSize.px <-- sizeSig, width.px <-- widthSig.map(_ / 3),
          padding.px <-- scale.signal.map(s => (16 * s).toInt),
          margin.px <-- scale.signal.map(s => (8 * s).toInt),
          borderWidth.px := 1, borderStyle := "solid", borderColor := "#ddd",
          div(display := "flex", justifyContent := "space-between", alignItems := "center",
            padding.px := 12, borderBottom := "1px solid #eee",
            span(fontWeight := "600", fontSize.px := 18, letterSpacing := "0.5px", textTransform := "uppercase", "Card B"),
            span(fontSize.px := 12, opacity := "0.7", fontStyle := "italic", "#2"),
          ),
          div(padding.px := 16, lineHeight := "1.6", minHeight.px := 120,
            maxHeight.px <-- expanded.signal.map(if (_) 500 else 200), overflow := "auto",
            p(margin.px := 0, padding.px := 8, textAlign := "left", "Content B"),
          ),
          div(display := "flex", justifyContent := "flex-end", gap := "8px", padding.px := 12, borderTop := "1px solid #eee",
            button(padding.px := 8, paddingLeft.px := 16, paddingRight.px := 16, borderRadius.px := 4,
              border := "none", backgroundColor := "#4CAF50", color := "white", fontWeight := "500", fontSize.px := 14, cursor := "pointer", "Action"),
            button(padding.px := 8, paddingLeft.px := 16, paddingRight.px := 16, borderRadius.px := 4,
              borderWidth.px := 1, borderStyle := "solid", borderColor := "#4CAF50",
              backgroundColor := "transparent", color := "#4CAF50", fontWeight := "500", fontSize.px := 14, cursor := "pointer", "Cancel"),
          ),
        ),

        // Card 3
        div(
          display := "flex", flexDirection := "column", position := "relative",
          overflow := "hidden", borderRadius.px := 10,
          boxShadow := "0 4px 12px rgba(0,0,0,0.15)", transition := "all 0.3s ease",
          backgroundColor <-- bgSig, color <-- fgSig,
          fontSize.px <-- sizeSig, width.px <-- widthSig.map(_ / 3),
          padding.px <-- scale.signal.map(s => (20 * s).toInt),
          margin.px <-- scale.signal.map(s => (10 * s).toInt),
          borderWidth.px := 2, borderStyle := "solid", borderColor := "#2196F3",
          div(display := "flex", justifyContent := "space-between", alignItems := "center",
            padding.px := 14, borderBottom := "2px solid #2196F3",
            span(fontWeight := "700", fontSize.px := 20, letterSpacing := "1px", textTransform := "uppercase", color := "#2196F3", "Card C"),
            span(fontSize.px := 12, opacity := "0.7", fontStyle := "italic", "#3"),
          ),
          div(padding.px := 20, lineHeight := "1.8", minHeight.px := 150,
            maxHeight.px <-- expanded.signal.map(if (_) 600 else 250), overflow := "auto",
            p(margin.px := 0, padding.px := 10, textAlign := "left", "Content C"),
            p(marginTop.px := 8, fontSize.em := 0.9, opacity := "0.7", "Secondary content"),
          ),
          div(display := "flex", justifyContent := "flex-end", gap := "10px", padding.px := 14, borderTop := "2px solid #2196F3",
            button(padding.px := 10, paddingLeft.px := 20, paddingRight.px := 20, borderRadius.px := 6,
              border := "none", backgroundColor := "#2196F3", color := "white", fontWeight := "600", fontSize.px := 15, cursor := "pointer", "Primary"),
            button(padding.px := 10, paddingLeft.px := 20, paddingRight.px := 20, borderRadius.px := 6,
              borderWidth.px := 2, borderStyle := "solid", borderColor := "#2196F3",
              backgroundColor := "transparent", color := "#2196F3", fontWeight := "600", fontSize.px := 15, cursor := "pointer", "Secondary"),
          ),
        ),
      ),
    )
  }
}

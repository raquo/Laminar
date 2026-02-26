package bench

sealed trait PdfObject {
  def id: String
  def label: String
}

// 15 case classes, each with 3-5 fields
case class PdfPage(id: String, label: String, pageNum: Int, width: Double, height: Double) extends PdfObject
case class PdfText(id: String, label: String, content: String, fontSize: Int, fontFamily: String) extends PdfObject
case class PdfImage(id: String, label: String, src: String, altText: String, width: Double) extends PdfObject
case class PdfTable(id: String, label: String, rows: Int, cols: Int) extends PdfObject
case class PdfHeader(id: String, label: String, level: Int, text: String) extends PdfObject
case class PdfFooter(id: String, label: String, text: String, pageNum: Int) extends PdfObject
case class PdfLink(id: String, label: String, href: String, title: String) extends PdfObject
case class PdfAnnotation(id: String, label: String, note: String, author: String) extends PdfObject
case class PdfBookmark(id: String, label: String, target: String, depth: Int) extends PdfObject
case class PdfMetadata(id: String, label: String, key: String, value: String) extends PdfObject
case class PdfShape(id: String, label: String, shapeType: String, x: Double, y: Double) extends PdfObject
case class PdfChart(id: String, label: String, chartType: String, dataPoints: Int) extends PdfObject
case class PdfFormField(id: String, label: String, fieldType: String, required: Boolean) extends PdfObject
case class PdfSignature(id: String, label: String, signer: String, timestamp: Long) extends PdfObject
case class PdfWatermark(id: String, label: String, text: String, opacity: Double) extends PdfObject

// Enum for status tracking
enum ObjectStatus {
  case Draft, Review, Approved, Published, Archived
}

// Helper types used across benchmark files
case class Coordinates(x: Double, y: Double)
case class Dimensions(width: Double, height: Double)
case class StyleConfig(color: String, bgColor: String, fontSize: Int, fontWeight: String, padding: Int)

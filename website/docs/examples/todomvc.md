---
title: TodoMVC
---

In addition to the code presented below, this example uses a fairly standard TodoMVC stylesheet, `todomvc.css`.

<div class = "mdoc-example mdoc-example-todomvc">

```scala mdoc:js
import com.raquo.laminar.api.L._
import org.scalajs.dom

object TodoMvcApp {

  // This implementation is very loosely based on Outwatch TodoMVC, for comparison see
  // https://github.com/clovellytech/outwatch-examples/tree/master/todomvc/src/main/scala/todomvc


  // --- Models ---

  case class TodoItem(id: Int, text: String, completed: Boolean)


  sealed abstract class Filter(val name: String, val passes: TodoItem => Boolean)

  object ShowAll extends Filter("All", _ => true)

  object ShowActive extends Filter("Active", !_.completed)

  object ShowCompleted extends Filter("Completed", _.completed)

  val filters: List[Filter] = ShowAll :: ShowActive :: ShowCompleted :: Nil


  sealed trait Command

  case class Create(itemText: String) extends Command

  case class UpdateText(itemId: Int, text: String) extends Command

  case class UpdateCompleted(itemId: Int, completed: Boolean) extends Command

  case class Delete(itemId: Int) extends Command

  case object DeleteCompleted extends Command


  // --- State ---

  // Var-s are reactive state variables suitable for both local state and redux-like global stores.
  // Laminar uses my library Airstream as its reactive layer https://github.com/raquo/Airstream

  private val itemsVar = Var(List[TodoItem]())

  private val filterVar = Var[Filter](ShowAll)

  private var lastId = 1 // just for auto-incrementing IDs

  private val commandObserver = Observer[Command] {
    case Create(itemText) =>
      lastId += 1
      if (filterVar.now() == ShowCompleted)
        filterVar.set(ShowAll)
      itemsVar.update(_ :+ TodoItem(id = lastId, text = itemText, completed = false))
    case UpdateText(itemId, text) =>
      itemsVar.update(_.map(item => if (item.id == itemId) item.copy(text = text) else item))
    case UpdateCompleted(itemId, completed) =>
      itemsVar.update(_.map(item => if (item.id == itemId) item.copy(completed = completed) else item))
    case Delete(itemId) =>
      itemsVar.update(_.filterNot(_.id == itemId))
    case DeleteCompleted =>
      itemsVar.update(_.filterNot(_.completed))
  }


  // --- Views ---

  lazy val node: HtmlElement = {
    val $todoItems = itemsVar
      .signal
      .combineWith(filterVar.signal)
      .mapN(_ filter _.passes)  
    div(
      cls("todoapp"),
      div(
        cls("header"),
        h1("todos"),
        renderNewTodoInput,
      ),
      div(
        hideIfNoItems,
        cls("main"),
        ul(
          cls("todo-list"),
          children <-- $todoItems.split(_.id)(renderTodoItem)
        )
      ),
      renderStatusBar
    )
  }

  private def renderNewTodoInput =
    input(
      cls("new-todo"),
      placeholder("What needs to be done?"),
      autoFocus(true),
      inContext { thisNode =>
        // Note: mapTo below accepts parameter by-name, evaluating it on every enter key press
        onEnterPress.mapTo(thisNode.ref.value).filter(_.nonEmpty) -->
          commandObserver.contramap[String] { text =>
            thisNode.ref.value = "" // clear input
            Create(itemText = text)
          }
      }
    )

  // Render a single item. Note that the result is a single element: not a stream, not some virtual DOM representation.
  private def renderTodoItem(itemId: Int, initialTodo: TodoItem, $item: Signal[TodoItem]): HtmlElement = {
    val isEditingVar = Var(false) // Example of local state
    val updateTextObserver = commandObserver.contramap[UpdateText] { updateCommand =>
      isEditingVar.set(false)
      updateCommand
    }
    li(
      cls <-- $item.map(item => Map("completed" -> item.completed)),
      onDblClick.filter(_ => !isEditingVar.now()).mapTo(true) --> isEditingVar.writer,
      children <-- isEditingVar.signal.map[List[HtmlElement]] {
        case true =>
          renderTextUpdateInput(itemId, $item, updateTextObserver) :: Nil
        case false =>
          List(
            renderCheckboxInput(itemId, $item),
            label(child.text <-- $item.map(_.text)),
            button(
              cls("destroy"),
              onClick.mapTo(Delete(itemId)) --> commandObserver
            )
          )
      }
    )
  }

  // Note that we pass reactive variables: `$item` for reading, `updateTextObserver` for writing
  private def renderTextUpdateInput(
    itemId: Int,
    $item: Signal[TodoItem],
    updateTextObserver: Observer[UpdateText]
  ) =
    input(
      cls("edit"),
      defaultValue <-- $item.map(_.text),
      onMountFocus,
      inContext { thisNode =>
        @inline def updateText = UpdateText(itemId, thisNode.ref.value)

        List(
          onEnterPress.mapTo(updateText) --> updateTextObserver,
          onBlur.mapTo(updateText) --> updateTextObserver
        )
      }
    )

  private def renderCheckboxInput(itemId: Int, $item: Signal[TodoItem]) =
    input(
      cls("toggle"),
      typ("checkbox"),
      checked <-- $item.map(_.completed),
      onInput.mapToChecked.map { isChecked =>
        UpdateCompleted(itemId, completed = isChecked)
      } --> commandObserver
    )

  private def renderStatusBar =
    footer(
      hideIfNoItems,
      cls("footer"),
      span(
        cls("todo-count"),
        child.text <-- itemsVar.signal
          .map(_.count(!_.completed))
          .map(pluralize(_, "item left", "items left")),
      ),
      ul(
        cls("filters"),
        filters.map(filter => li(renderFilterButton(filter)))
      ),
      child.maybe <-- itemsVar.signal.map { items =>
        if (items.exists(ShowCompleted.passes)) Some(
          button(
            cls("clear-completed"),
            "Clear completed",
            onClick.map(_ => DeleteCompleted) --> commandObserver
          )
        ) else None
      }
    )

  private def renderFilterButton(filter: Filter) =
    a(
      cls.toggle("selected") <-- filterVar.signal.map(_ == filter),
      onClick.preventDefault.mapTo(filter) --> filterVar.writer,
      filter.name
    )

  // Every little thing in Laminar can be abstracted away
  private def hideIfNoItems: Mod[HtmlElement] =
    display <-- itemsVar.signal.map { items =>
      if (items.nonEmpty) "" else "none"
    }


  // --- Generic helpers ---

  private def pluralize(num: Int, singular: String, plural: String): String =
    s"$num ${if (num == 1) singular else plural}"

  private val onEnterPress = onKeyPress.filter(_.keyCode == dom.ext.KeyCode.Enter)
}


// Render app

render(containerNode, TodoMvcApp.node)
```

</div>

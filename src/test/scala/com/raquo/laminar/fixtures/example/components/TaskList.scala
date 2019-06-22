package com.raquo.laminar.fixtures.example.components

import com.raquo.airstream.eventbus.EventBus
import com.raquo.airstream.signal.Signal
import com.raquo.laminar.api.L._
import com.raquo.laminar.collection.CollectionCommand.Append

class TaskList {

  private val taskDiffBus = new EventBus[ChildrenCommand]

  private val showAddTaskInputBus = new EventBus[Boolean]

  private val $showAddTaskInput = showAddTaskInputBus.events.toSignal(false)

  private var count = 0

  val node: Div = div(
    h1("TaskList"),
    children.command <-- taskDiffBus.events,
    child.maybe <-- maybeNewTask,
    child.maybe <-- maybeNewTaskButton
  )

  def maybeNewTaskButton: Signal[Option[Node]] = {
    $showAddTaskInput.map { showAddTaskInput =>
      val showNewTaskButton = !showAddTaskInput
      if (showNewTaskButton) {
        count += 1
        Some(
          button(
            onClick.map(_ => Append(div("hello"))) --> taskDiffBus,
            //        onClick --> (true, sendTo = $showAddTaskInput),
            "Add task"
          )
        )
      } else {
        None
      }
    }
  }

  def maybeNewTask: Signal[Option[Node]] = {
    $showAddTaskInput.map { showAddTaskInput =>
      if (showAddTaskInput) {
        Some(button(
          onClick.mapTo(false) --> showAddTaskInputBus,
          "Cancel"
        ))
      } else {
        None
      }
    }
  }
}

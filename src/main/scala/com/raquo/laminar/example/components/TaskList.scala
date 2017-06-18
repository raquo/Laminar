package com.raquo.laminar.example.components

import com.raquo.laminar._
import com.raquo.laminar.events._
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.receivers.ChildrenReceiver.{Diff, append}
import com.raquo.laminar.receivers.MaybeChildReceiver.MaybeChildNode
import com.raquo.laminar.tags._
import com.raquo.xstream.XStream
import org.scalajs.dom

// @TODO[API] Create Var and VarStream from XStream's ShamefulStream + MemoryStream?

class TaskList {

  private var $taskDiff = XStream.create[Diff]()

  private val $showAddTaskInput = XStream.create().startWith(false)

  private var count = 0

  val node: ReactiveElement[dom.html.Div] = div(
    h1("TaskList"),
    children <-- $taskDiff,
    maybeChild <-- maybeNewTask,
    maybeChild <-- maybeNewTaskButton
  )

  def maybeNewTaskButton: XStream[MaybeChildNode] = {
    $showAddTaskInput.map { showAddTaskInput =>
      val showNewTaskButton = !showAddTaskInput
      if (showNewTaskButton) {
        count += 1
        Some(
          button(
            onClick --> (append(div("hello")), sendTo = $taskDiff),
            //        onClick --> (true, sendTo = $showAddTaskInput),
            "Add task"
          )
        )
      } else {
        None
      }
    }
  }

  def maybeNewTask: XStream[MaybeChildNode] = {
    $showAddTaskInput.map { showAddTaskInput =>
      if (showAddTaskInput) {
        Some(button(
          onClick --> (false, $showAddTaskInput),
          "Cancel"
        ))
      } else {
        None
      }
    }
  }
}

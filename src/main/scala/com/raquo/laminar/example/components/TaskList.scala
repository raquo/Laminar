package com.raquo.laminar.example.components

import com.raquo.laminar.bundle._
import com.raquo.laminar.collection.CollectionCommand.Append
import com.raquo.laminar.nodes.ReactiveElement
import com.raquo.laminar.receivers.MaybeChildReceiver.MaybeChildNode
import com.raquo.laminar.setters.ChildrenCommandSetter.ChildrenCommand
import com.raquo.laminar.streams.{EventBus, ReactiveVar}
import com.raquo.xstream.XStream
import org.scalajs.dom

// @TODO[API] Create Var and VarStream from XStream's ShamefulStream + MemoryStream?

class TaskList {

  private val taskDiffBus = new EventBus[ChildrenCommand]

  private val $showAddTaskInputBus = new ReactiveVar(false)

  private var count = 0

  val node: ReactiveElement[dom.html.Div] = div(
    h1("TaskList"),
    children.command <-- taskDiffBus.$,
    maybeChild <-- maybeNewTask,
    maybeChild <-- maybeNewTaskButton
  )

  def maybeNewTaskButton: XStream[MaybeChildNode] = {
    $showAddTaskInputBus.$.map { showAddTaskInput =>
      val showNewTaskButton = !showAddTaskInput
      if (showNewTaskButton) {
        count += 1
        Some(
          button(
            onClick().map(_ => Append(div("hello"))) --> taskDiffBus,
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
    $showAddTaskInputBus.$.map { showAddTaskInput =>
      if (showAddTaskInput) {
        Some(button(
          onClick().mapTo(false) --> $showAddTaskInputBus,
          "Cancel"
        ))
      } else {
        None
      }
    }
  }
}

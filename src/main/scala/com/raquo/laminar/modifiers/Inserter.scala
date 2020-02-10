package com.raquo.laminar.modifiers

import com.raquo.airstream.ownership.{Owner, Subscription}
import com.raquo.domtypes.generic.Modifier
import com.raquo.laminar.lifecycle.InsertContext
import com.raquo.laminar.nodes.ReactiveElement

/** If you DO provide initialContext, its parentNode MUST always
  * be the same `element` that you apply this Modifier to.
  */
class Inserter[-El <: ReactiveElement.Base] (
  initialContext: Option[InsertContext[El]],
  insertFn: (InsertContext[El], Owner) => Subscription
) extends Modifier[El] {

  override def apply(element: El): Unit = {
    // @Note we want to remember this even after subscription is deactivated.
    //  Yes, we expect the subscription to re-activate with this initial state
    //  because it would match the state of the DOM upon reactivation.
    val insertContext = initialContext.getOrElse(InsertContext.reservedSpotContext(element))

    ReactiveElement.bindSubscription(element) { mountContext =>
      insertFn(insertContext, mountContext.owner)
    }
  }

  /** Call this to get a copy of Inserter with a context locked to a certain element.
    * We use this to "reserve a spot" for future nodes when a bind(c => inserter) modifier
    * is initialized, as opposed to waiting until subscription is activated.
    *
    * The arrangement is admittedly a bit weird, but is required to build a smooth end user API.
    */
  def withContext(context: InsertContext[El]): Inserter[El] = {
    new Inserter[El](Some(context), insertFn)
  }
}

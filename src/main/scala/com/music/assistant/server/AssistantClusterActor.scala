package com.music.assistant.server

import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor.ActorLogging
import akka.actor.Actor

class AssistantClusterActor extends Actor with ActorLogging {

  val cluster = Cluster(context.system)

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
      classOf[MemberEvent], classOf[UnreachableMember])
  }

  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {

    case MemberUp(member) =>
      println("[INFO] AssistantClusterActor MemberUp")
      log.info("Member is Up: {}", member.address)

    case UnreachableMember(member) =>
      println("[INFO] AssistantClusterActor UnreachableMember")
      log.info("Member detected as unreachable: {}", member)

    case MemberRemoved(member, previousStatus) =>
      println("[INFO] AssistantClusterActor MemberRemoved")
      log.info("Member is Removed: {} after {}", member.address, previousStatus)

    case _: MemberEvent => // ignore
  }

}

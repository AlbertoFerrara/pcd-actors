package it.unipd.math.pcd.actors;

/**
 * Created by Alberto Ferrara.
 */

/**
 *Implementazione di Message, serve per inserire nella Mailbox un messaggio e un sender
 */

public class ConcMessage<T extends Message> {
    private T cMessage;
    private ActorRef<T> cActor;

    public ConcMessage(T mex, ActorRef act){
        cMessage = mex;
        cActor = act;
    }

    public T getcMessage(){
        return cMessage;
    }

    public ActorRef<? extends Message> getcActor(){
        return cActor;
    }
}

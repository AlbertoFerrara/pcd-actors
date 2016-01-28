package it.unipd.math.pcd.actors;

/**
 * Created by Alberto on 28/01/16.
 */
public class ActorRefImpl<T extends Message> implements ActorRef<T> {
    private final ActorSysImp AbsActSys;
    public ActorRefImpl(ActorSysImp a){
        AbsActSys=a;
    }

    //override compareTo per confontare elementi ActorRef
    public int compareTo(ActorRef a){
        if(this == a)
            return 0;
        else
            return -1;
    }

    @Override
    public void send(T message, ActorRef to) {
        AbsActor aux = (AbsActor) AbsActSys.getActor(to);
        aux.gestisciMessaggi(message, this);
    }
}

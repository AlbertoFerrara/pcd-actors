package it.unipd.math.pcd.actors;

/**
 * Created by Alberto on 28/01/16.
 */
public class ActorRefImpl<T extends Message> implements ActorRef<T> {
    /**
     * Campo riferimento a ActorSystem, serve per ottenere l'Actor a partire da un ActoRef
      */
    private final ActorSysImp AbsActSys;

    /**
     * Costruttore della classe
     */
    public ActorRefImpl(ActorSysImp a){
        AbsActSys=a;
    }

    /**
     * Override compareTo per confontare elementi ActorRef
     */
    public int compareTo(ActorRef a){
        if(this == a) return 0;
        else  return -1;
    }

    /**
     *Metodo che serve a mandare un messaggio ad un Actor (tramite invocazione del metodo che gestisce i messaggi di una Mailbox)
     */
    @Override
    public void send(T message, ActorRef to) {
        AbsActor aux = (AbsActor) AbsActSys.getActor(to);
        aux.gestisciMessaggi(message, this);
    }
}

package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

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
     * Override compareTo per confontare elementi ActorRef nella HashMap
     */
    public int compareTo(ActorRef a){
        return hashCode() == a.hashCode() ? 0 : -1;
    }

    /**
     *Metodo che serve a mandare un messaggio ad un Actor (tramite invocazione del metodo che gestisce i messaggi di una Mailbox)
     */
    @Override
    public void send(T message, ActorRef to) throws NoSuchActorException{
        AbsActor aux = (AbsActor) AbsActSys.getActor(to);
        /**
         * Se l'attore esiste e non è stato stoppato
         */
        if(aux != null && aux.stopDone!=true)
            aux.gestisciMessaggi(message, this);
        else
            throw new NoSuchActorException();
    }
}

package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

/**
 * Created by Alberto on 28/01/16.
 */
public final class ActorSysImp extends AbsActorSystem {



    public synchronized void stop(ActorRef<?> actor) throws NoSuchActorException{
        if(containKey(actor)) {
            //casto a abs perche ritorna un actor
            AbsActor aux = (AbsActor) getActor(actor);
            //stoppo il thread che gestisce ricezione nuovi messaggi
            aux.stopHelper();

                while (!aux.stopDone) {
                    try {
                        aux.wait();
                    } catch (InterruptedException ie) {
                        //gestione eccezione
                    }
                }
                removeActor(actor);
        }
        else
            throw new NoSuchActorException();

    }

    public void stop(){
        for(ActorRef<?> act : getSet()){
            stop(act);
        }

    }

    protected ActorRef createActorReference(ActorMode mode){
        //restituisco attore locale se è la modalità richiesta altrimenti eccezione
        if(mode == ActorMode.LOCAL)
            return new ActorRefImpl<>(this);
        else
            throw new IllegalArgumentException();
    }



}

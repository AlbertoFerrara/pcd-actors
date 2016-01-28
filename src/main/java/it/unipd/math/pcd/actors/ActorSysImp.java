package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

/**
 * Created by Alberto on 28/01/16.
 */
public final class ActorSysImp extends AbsActorSystem {

    /**
     * Metodo per stoppare un Actor in particolare, se l'actor non è presente nella mappa solleva eccezione
     */
    public  void stop(ActorRef<?> actor) throws NoSuchActorException{
        /**
         * Metodo per controllare se Actor è presente nella mappa
         */
        if(containKey(actor)) {
            /**
             * Vasto a AbsActor perch+ getActor ritorna un actor
             */
            AbsActor aux = (AbsActor) getActor(actor);
            /**
             * Stoppo il thread che gestisce la ricezione dei nuovi messaggi
             */
            aux.stopHelper();

            /**
             * Sincronizzo sull'Actor per metterlo in attesa affinchè non sia stata settata a false la variabile, ovvero ho svuotato tutta la lista dei messaggi della mailbox
             */
            synchronized (aux) {
                while (!aux.stopDone) {
                    try {
                        aux.wait();
                    } catch (InterruptedException ie) {
                        //gestione dell'eccezione
                    }
                }
                /**
                 * Rimuovo l'attore dalla mappa
                 */
                removeActor(actor);
            }
        }
        else
            throw new NoSuchActorException();

    }

    /**
     * Metodo per ciclare su tutti gli attori presenti e stopparli tutti
     */
    public void stop(){
        for(ActorRef<?> act : getSet()){
            /**
             * Richiamo il metodo stop che ferma l'Actor (che gli viene passato)
             */
            stop(act);
        }

    }

    /**
     * Metodo per creare un Actor, crea solo LOCAL (altrimenti solleva opportuna eccezione)
     */
    protected ActorRef createActorReference(ActorMode mode){
        /**
         * Restituisco attore locale se è la modalità richiesta
         */
        if(mode == ActorMode.LOCAL)
            return new ActorRefImpl<>(this);
        /**
         * Altrimenti eccezione
         */
        else
            throw new IllegalArgumentException();
    }



}

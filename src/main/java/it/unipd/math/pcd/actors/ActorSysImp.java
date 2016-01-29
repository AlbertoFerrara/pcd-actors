package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

/**
 * Created by Alberto on 28/01/16.
 */
public final class ActorSysImp extends AbsActorSystem {

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
            throw new IllegalArgumentException("Tipo attore non supportato");
    }



}

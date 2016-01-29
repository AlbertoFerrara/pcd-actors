/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Riccardo Cardin
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * <p/>
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */

/**
 * Please, insert description here.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
package it.unipd.math.pcd.actors;

import it.unipd.math.pcd.actors.exceptions.NoSuchActorException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A map-based implementation of the actor system.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public abstract class AbsActorSystem implements ActorSystem {

    /**
     * Associates every Actor created with an identifier.
     */
    private Map<ActorRef<?>, Actor<?>> actors;

    /**
     * Costruttore della classe, istanzia la mappa delle associazioni ActorRef-Actor
     */
    public AbsActorSystem(){
        actors = new HashMap<>();
    }

    /**
     * Metodo pubblico per controllare se una chiave (ActorRef) esiste nella mappa
     */
    public boolean containKey(ActorRef a){
        return actors.containsKey(a);
    }

    /**
     * Metodo per rimuovere una coppia ActorRef-Actor dalla mappa
     */
    public void removeActor(ActorRef a){
        actors.remove(a);
    }

    /**
     * Creo una vista sulla mappa sulla quale andrò a scorrere gli elementi
     */
    public Set<ActorRef<?>> getSet(){
        return actors.keySet();
    }

    /**
     * Metodo per ottenere un Actor a partire da ActorRef
     */
    public Actor getActor(ActorRef a) throws NoSuchActorException{
        Actor aux = actors.get(a);
        if(aux == null)
            throw new NoSuchActorException();
        else
            return aux;
    }

    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor, ActorMode mode) {

        // ActorRef instance
        ActorRef<?> reference;
        try {
            // Create the reference to the actor
            reference = this.createActorReference(mode);
            // Create the new instance of the actor
            Actor actorInstance = ((AbsActor) actor.newInstance()).setSelf(reference);
            // Associate the reference to the actor
            actors.put(reference, actorInstance);

        } catch (InstantiationException | IllegalAccessException e) {
            throw new NoSuchActorException(e);
        }
        return reference;
    }

    @Override
    public ActorRef<? extends Message> actorOf(Class<? extends Actor> actor) {
        return this.actorOf(actor, ActorMode.LOCAL);
    }


    /**
     * Metodo per stoppare un Actor in particolare, se l'actor non è presente nella mappa solleva eccezione
     */
    public  void stop(ActorRef<?> actor) throws NoSuchActorException{
        /**
         * Metodo per controllare se Actor è presente nella mappa
         */
        if(containKey(actor)) {
            /**
             * Casto a AbsActor perchè getActor ritorna un actor
             */
            AbsActor aux = (AbsActor) getActor(actor);
            /**
             * Controllo che l'attore non sia già stato stoppato
             */
            if(!aux.stopDone) {

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
            else{
                throw new NoSuchActorException("L'attore è già stato stoppato");
            }
        }
        else
            throw new NoSuchActorException("Attore non trovato");

    }

    /**
     * Metodo per ciclare su tutti gli attori presenti e stopparli tutti
     */
    public void stop(){
        Iterator iter = getSet().iterator();
        while(iter.hasNext()){
            /**
             * Richiamo il metodo stop che ferma l'Actor (che gli viene passato dopo averlo ritornato con l'iteratore)
             */
            stop((ActorRef<?>) iter.next());
        }

    }

    protected abstract ActorRef createActorReference(ActorMode mode);
}
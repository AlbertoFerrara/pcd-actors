package it.unipd.math.pcd.actors;

/**
 * Created by Alberto Ferrara
 */

import java.util.LinkedList;

/**
 * Implementazione della Mailbox
 */
public class ImplMailBox<T extends Message> implements MailBox<T>  {
    /**
     * Garantisco che la Mailbox sia creata una sola volta e che il riferimento non possa mai cambiare
     */
    protected final LinkedList<ConcMessage<T>> listL = new LinkedList<>();

    /**
     * Metodo per inserire un messaggio nella Mailbox
     */
    public void push(T mex, ActorRef<? extends Message> sender){
        /**
         * Sincronizzo su listaL che è la risorsa condivisa
         */
        synchronized (listL) {
            listL.addFirst(new ConcMessage<T>(mex, sender));
        }
    }

    /**
     * Metodo per rimuovere un messaggio dalla Mailbox
     */
    public ConcMessage<? extends Message> pop(){
        /**
         * Sincronizzo su listL che è la risorsa condivisa
         */
        synchronized (listL) {
            /**
             * Restituisco il messaggio rimosso
             */
            return listL.removeLast();
        }
    }

    public boolean isEmpty(){
        return listL.isEmpty();
    }

}

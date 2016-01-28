package it.unipd.math.pcd.actors;

/**
 * Created by Alberto Ferrara
 */

import java.util.LinkedList;

/**
 * Implementazione della mail box
 */
public class ImplMailBox<T extends Message> implements MailBox<T>  {
    //Garantisco che la mail box sia creata una sola volta e che il riferimento non possa mai cambiare
    protected final LinkedList<ConcMessage<T>> listL = new LinkedList<>();

    public void push(T mex, ActorRef<? extends Message> sender){
        //sincronizzo su listaL che è la risorsa condivisa
        synchronized (listL) {
            listL.addFirst(new ConcMessage<T>(mex, sender));
        }
    }

    public ConcMessage<? extends Message> pop(){
        //sincronizzo su listL che è la risorsa condivisa
        synchronized (listL) {
            return listL.removeLast();
        }
    }

    public int getSize(){
        return listL.size();
    }

}

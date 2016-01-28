package it.unipd.math.pcd.actors;

/**
 * Created by Alberto Ferrara
 */

import java.util.ArrayList;

/**
 * Implementazione della mail box
 */
public class ImplMailBox<T extends Message> implements MailBox<T>  {
    //Garantisco che la mail box sia creata una sola volta e che il riferimento non possa mai cambiare
    //Uso arrayList per poter rimuovere un messaggio in qualsiasi punto
    protected final ArrayList<ConcMessage<T>> arrayL = new ArrayList<>();

    public boolean push(T mex, ActorRef<? extends Message> sender){
        //sincronizzo su arrayl che è la risorsa condivisa
        synchronized (arrayL) {
            boolean b = arrayL.add(new ConcMessage<T>(mex, sender));
            return b;
        }
    }

    public ConcMessage<? extends Message> pop(int indice){
        //sincronizzo su arrayl che è la risorsa condivisa
        synchronized (arrayL) {
            return arrayL.remove(indice);
        }
    }

    public int getSize(){
        return arrayL.size();
    }

}

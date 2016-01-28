package it.unipd.math.pcd.actors;

/**
 * Created by Alberto on 27/01/16.
 */

/**
 * Thread che resta in ascolto dei messaggi in ingresso per ogni Actor
 */
public class ActorHelper implements Runnable {

    private final AbsActor act;
    private final MailBox mBox;

    public ActorHelper(AbsActor<? extends Message> a, MailBox<? extends Message> m){
        act = a;
        mBox = m;
    }

    @Override
    public void run() {
        //ciclo finche il thread che sta eseguendo non entra nello stato interrupted
        try{
            while(!Thread.currentThread().isInterrupted()){
                synchronized (mBox){
                    //finchè non ci sono nuovi messaggi nella mail box metto in attesa
                    while(mBox.getSize()==0){
                        mBox.wait();
                    }
                }
                //se sono qui, ho superato la condizione e la dimensione della mailbox>0
                synchronized (this){
                    //rimuovo il nuovo messaggio dalla prima posizione
                    ConcMessage mex = mBox.pop();
                    //setto il mandante del messaggio e lo passo all'actor che deve riceverlo
                    act.sender = mex.getcActor();
                    //setto il messaggio che l'actor deve ricevere
                    act.receive(mex.getcMessage());
                }
            }
        }
        catch (InterruptedException ie){
            //qui andrebbe gestita l'eccezione di thread interrotto
        }
        finally {
            //qui devo gestire il momento in cui il thread viene interrotto ma deve comunque finire di inserire i messaggi
            synchronized (mBox){
                while(mBox.getSize()>0){
                    //rimuovo il nuovo messaggio dalla prima posizione
                    ConcMessage mex = mBox.pop();
                    //setto il mandante del messaggio e lo passo all'actor che deve riceverlo
                    act.sender = mex.getcActor();
                    //setto il messaggio che l'actor deve ricevere
                    act.receive(mex.getcMessage());
                }
            }
            //qui ho svuotato tutta la coda dei messaggi
            synchronized (act){
                act.stopDone = true;
                act.notify();
            }
        }

    }
}

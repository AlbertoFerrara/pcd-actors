package it.unipd.math.pcd.actors;

/**
 * Created by Alberto Ferrara.
 */

/**
 * Definisce un Thread che resta in ascolto dei messaggi in ingresso per ogni Actor
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
        try{
            /**
             * Ciclo potenzialmente all'infinito, finchè il Thread non viene interrotto
             */
            while(true){
                /**
                 * Sicronizzo l'accesso alla mailBox
                 */
                synchronized (mBox){
                    /**
                     * Finchè non ci sono nuovi messaggi nella mailbox metto in attesa e rilascio il lock
                     */
                    while(mBox.getSize()==0){
                        mBox.wait();
                    }
                }
                /**
                 * Se sono qui, ho superato la condizione e la dimensione della mailbox>0. Sincronizzo sull'oggetto
                 */
                synchronized (this){
                    /**
                     * Rimuovo il nuovo messaggio dalla prima posizione e lo salvo
                     */
                    ConcMessage mex = mBox.pop();
                    /**
                     * Setto il mandante del messaggio e lo passo all'Actor che deve riceverlo
                     */
                    act.sender = mex.getcActor();
                    /**
                     * Recupero il messaggio e lo passo all'actor che deve riceverlo
                     */
                    act.receive(mex.getcMessage());
                }
            }
        }
        catch (InterruptedException ie){
            /**
             * Qui andrebbe gestita l'eccezione di thread interrotto
             */
        }
        finally {
            /**
             * Qui devo gestire il momento in cui il thread viene interrotto ma deve comunque finire di rimuovere i messaggi e passarli all'Actor ricevente
             */
            synchronized (mBox){
                while(mBox.getSize()>0){
                    /**
                     * Rimuovo il nuovo messaggio dalla prima posizione
                     */
                    ConcMessage mex = mBox.pop();
                    /**
                     * Setto il mandante del messaggio e lo passo all'Actor che deve riceverlo
                     */
                    act.sender = mex.getcActor();
                    /**
                     * Recupero il messaggio e lo passo all'actor che deve riceverlo
                     */
                    act.receive(mex.getcMessage());
                }
            }
            /**
             * Qui ho svuotato tutta la coda dei messaggi da mandare, sincronizzo sull'attore
             */
            synchronized (act){
                /**
                 * Setto a true la variabile dell'Actor che indica quando l'attore è stoppato
                 */
                act.stopDone = true;
                act.notifyAll();
            }
        }

    }
}

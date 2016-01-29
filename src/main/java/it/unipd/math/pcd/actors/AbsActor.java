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

/**
 * Defines common properties of all actors.
 *
 * @author Riccardo Cardin
 * @version 1.0
 * @since 1.0
 */
public abstract class AbsActor<T extends Message> implements Actor<T> {

    /**
     * Self-reference of the actor
     */
    protected ActorRef<T> self;

    /**
     * Sender of the current message
     */
    protected ActorRef<T> sender;

    /**
     * Sets the self-referece.
     *
     * @param self The reference to itself
     * @return The actor.
     */
    protected final Actor<T> setSelf(ActorRef<T> self) {
        this.self = self;
        return this;
    }

    /**
     * Campo dati privato e finale per evitare modifiche del riferimento, è la mail box del singolo attore
     */
    private final MailBox<T> mailBox = new ImplMailBox<>();

    /**
     * Variabile volatile che uso per capire se l'attore è stato interrotto e se ha finito di gestire tutti i suoi messaggi
     */
    public volatile boolean stopDone = false;

    /**
     * Metodo che gestisce i messaggi inserendoli nella MailBox (dopo aver preso il lock)
     */
    public final void gestisciMessaggi(T mess, ActorRef<T> sender) {
        synchronized (mailBox) {
            if (!stopDone) {
                mailBox.push(mess, sender);
                mailBox.notifyAll();
            }
        }
    }

    public void setStopDoneTrue() throws NoSuchActorException {
        if (stopDone == true)
            throw new NoSuchActorException("Attore già stoppato");
        else
            stopDone = true;
    }

    /**
     * Metodo per fermare il thread di gestione dei messaggi in ingresso, chiamato dal metodo stop dell'actorsystem
     */
    public final void stopHelper() {
        setStopDoneTrue();
    }

    /**
     * Costruttore classe AbsActor, fa partire il thread che gestisce i messaggi in ingresso
     */
    public AbsActor() {
        Thread helper = new Thread(new ActorHelper());
        helper.start();
    }

    /**
     * Classe interna che implementa un runnable, andrà ad essere inserito ed eseguito in un thread creato nel costruttore di AbsActor
     */
    private class ActorHelper implements Runnable {
        public void run() {
            try {
                /**
                 * Ciclo potenzialmente all'infinito
                 */
                while (!stopDone) {
                    /**
                     * Sicronizzo l'accesso alla mailBox
                     */
                    synchronized (mailBox) {
                        /**
                         * Finchè non ci sono nuovi messaggi nella mailbox metto in attesa e rilascio il lock
                         */
                        while (mailBox.isEmpty()) {
                            mailBox.wait();
                        }
                    }
                    /**
                     * Se sono qui, ho superato la condizione e la dimensione della mailbox>0. Sincronizzo sull'oggetto
                     */
                    synchronized (this) {
                        /**
                         * Rimuovo il nuovo messaggio dalla prima posizione e lo salvo
                         */
                        ConcMessage mex = mailBox.pop();
                        /**
                         * Setto il mandante del messaggio e lo passo all'Actor che deve riceverlo
                         */
                        sender = mex.getcActor();
                        /**
                         * Recupero il messaggio e lo passo all'actor che deve riceverlo
                         */
                        receive((T) mex.getcMessage());
                    }
                }
            } catch (InterruptedException ie) {
                /**
                 * Qui andrebbe gestita l'eccezione di thread interrotto
                 */
            } finally {
                /**
                 * Qui devo gestire il momento in cui il thread viene interrotto ma deve comunque finire di rimuovere i messaggi e passarli all'Actor ricevente
                 */
                EmptyMailBox();
                /**
                 * Qui ho svuotato tutta la coda dei messaggi da mandare, sincronizzo sull'attore
                 */
                synchronized (AbsActor.this) {
                    /**
                     * Setto a true la variabile dell'Actor che indica quando l'attore è stoppato
                     */
                    try {
                        setStopDoneTrue();
                        notifyAll();
                    } catch (NoSuchActorException nsa) {

                    }
                }
            }
        }

        /**
         * Metodo sincronizzato per svuotare la MailBox
         */
        private synchronized void EmptyMailBox() {
            while (!mailBox.isEmpty()) {
                /**
                 * Rimuovo il nuovo messaggio dalla prima posizione
                 */
                ConcMessage mex = mailBox.pop();
                /**
                 * Setto il mandante del messaggio e lo passo all'Actor che deve riceverlo
                 */
                sender = mex.getcActor();
                /**
                 * Recupero il messaggio e lo passo all'actor che deve riceverlo
                 */
                receive((T) mex.getcMessage());
            }
        }
    }
}


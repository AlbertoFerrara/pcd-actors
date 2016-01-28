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

    //campo dati privato e finale per evitare modifiche del riferimento, è la mail box del singolo attore
    private final MailBox<T> mailBox= new ImplMailBox<>();

    //variabile che uso per capire se l'attore è stato interrotto e ha finito di gestire tutti i suoi messaggi
    public boolean stopDone = false;

    //thread che gestisce i messaggi in ingresso
    private final Thread helper = new Thread(new ActorHelper(this, mailBox));
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

    public final void gestisciMessaggi(T mess, ActorRef<T> sender){
        synchronized (mailBox){
            mailBox.push(mess, sender);
            mailBox.notifyAll();
        }
    }

    public final void stopHelper(){
        helper.interrupt();
    }

    /**
     * Receive message
     *
     * @override
     */
    public void receive(T message){
        //Il messaggio andrà inserito nella mail list che avrà un accesso sinronizzato per l'inserimento
    }

    //Costruttore classe AbsActor
    public AbsActor() {

        //Faccio partire il thread che gestisce connessioni in ingresso
        helper.start();
    }
}

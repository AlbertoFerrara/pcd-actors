package it.unipd.math.pcd.actors;

/**
 * Created by Alberto Ferrara.
 */
/*
 * Definisce l'interfaccia per la MailBox, definisce la firma dei metodi per inserire e rimuovere un messaggio
 */
public interface MailBox<T extends Message> {
    /*
     * Aggiunge un messaggio alla MailBox
     */
    void push(T mex, ActorRef<? extends Message> sender);

    /*
     * Rimozione messaggio dalla MailBox
     */
    ConcMessage<? extends Message> pop();

    /*
        Ottengo dimensione della mailbox
     */
    public int getSize();

}

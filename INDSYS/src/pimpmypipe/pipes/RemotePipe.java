package pimpmypipe.pipes;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.rmi.MarshalledObject;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.activation.Activatable;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivationID;
import java.rmi.server.UnicastRemoteObject;

import pimpmypipe.interfaces.IOable;

public class RemotePipe<T> extends UnicastRemoteObject implements RemoteIOable<T>{

    private BufferedSyncPipe<T> m_pipe = null;
    private int m_bufSize = 100;
    
    public RemotePipe(int bufLen) throws RemoteException {
        super();
        m_pipe = new BufferedSyncPipe<T>(bufLen);
    }

    public T read() throws RemoteException {
        try {
            return m_pipe.read();
        } catch (StreamCorruptedException e) {
            throw new RemoteException("Error on reading from remote-pipe", e);
        } 
    }

    public void write(T value) throws RemoteException {
        try {
            m_pipe.write(value);
        } catch (StreamCorruptedException e) {
            throw new RemoteException("Error on writing from remote-Pipe", e);
        }
    }
}

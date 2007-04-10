import java.util.Vector;

public interface DispatcherObserver
{
    public Tcb currentTcb();
    
    public Vector getQueue();
}

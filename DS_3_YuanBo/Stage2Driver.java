public class Stage2Driver {
    public static void main(String[] args) {
    	final Stage2Backend backend = new Stage2BackendEchoImpl();
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Stage2GUI.createAndShowGUI(backend);
            }
        });
    }
}

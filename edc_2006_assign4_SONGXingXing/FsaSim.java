
/**
 * @author xingxing SONG
 *
 */



public class FsaSim {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FsaGui gui = new FsaGui();
		Fsa fsa = new FsaImpl() ;
		gui.setFsa(fsa ) ;
		((FsaImpl)fsa).setFsaGui(gui) ;
		
		EventSequence  event = new EventSequenceImpl() ;
		gui.setEventSequence(event ) ;
		((EventSequenceImpl)event).setFsaGui(gui) ;
		
		gui.setVisible( true );
		
		
	}

}

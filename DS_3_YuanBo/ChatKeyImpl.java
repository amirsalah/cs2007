


public class ChatKeyImpl implements ChatKey{
    private ChatImpl chat;
    public ChatKeyImpl(ChatImpl chatIM) {
        chat = chatIM;  
    }

    public boolean amPrivileged() {
        boolean privilege = false;
        try{
        	privilege =chat.isPrivileged(this);
        } 
        catch (Exception e){}
        return privilege;
    }

    
}




public class ChatKeyImpl implements ChatKey{

    private double id;
    private ChatImpl chat;

    public ChatKeyImpl(ChatImpl c) {
        chat = c;  
        id = Math.random();  
    }

   

    public boolean amPrivileged() {
        boolean test = false;
        try{
            test =chat.isPrivileged(this);

        } 
        catch (Exception c)
         {}
        return test;
    }

    
}

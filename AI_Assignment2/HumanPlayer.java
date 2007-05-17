
import java.io.*;

public class HumanPlayer
    implements PilesPlayer
{

    public HumanPlayer()
    {
    }

    public byte[] makeMove(byte abyte0[])
    {
        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in));
        byte abyte1[] = new byte[abyte0.length];
        boolean flag = false;
        while(!flag) 
            try
            {
                System.out.print("Enter the pile to remove from:        ");
                String s = bufferedreader.readLine();
                System.out.print("Enter the number of stones to remove: ");
                String s1 = bufferedreader.readLine();
                int i = Integer.parseInt(s);
                int j = Integer.parseInt(s1);
                if(i - 1 < abyte0.length && j <= abyte0[i - 1] && j != 0)
                {
                    flag = true;
                    for(int k = 0; k < abyte0.length; k++)
                        abyte1[k] = abyte0[k];

                    abyte1[i - 1] = (byte)(abyte0[i - 1] - j);
                } else
                {
                    System.out.println("Sorry, that move is invalid");
                }
            }
            catch(NumberFormatException numberformatexception)
            {
                System.out.println("Please enter numbers!");
            }
            catch(IOException ioexception)
            {
                System.out.println("Caught an IO Exception, retrying input.");
            }
        return abyte1;
    }
}
import java.io.PrintStream;
import java.util.Arrays;

public class SmartNode
{

    public SmartNode(int i)
    {
        fKids = null;
        fNumKids = 0;
        fNumPiles = (byte)i;
        fState = 0;
        fOurID = sLastID;
        sLastID++;
        fPiles = new byte[fNumPiles];
        for(int j = 0; j < fNumPiles; j++)
            fPiles[j] = 0;

    }

    public void makeChildren()
    {
        int i = 1;
        int j = 0;
        int k = 0;
        for(int l = 0; l < fNumPiles; l++)
            fNumKids += fPiles[l];

        fKids = new SmartNode[fNumKids];
        for(int i1 = 0; i1 < fNumKids; i1++)
            fKids[i1] = null;

        for(int j1 = 0; j1 < fNumKids; j1++)
        {
            for(; i > fPiles[j]; i = 1)
                j++;

            int k1 = 0;
            for(int l1 = 0; l1 < fNumPiles; l1++)
                k1 += fPiles[l1];

            k1 -= i;
            byte abyte0[] = new byte[fNumPiles];
            for(int i2 = 0; i2 < fNumPiles; i2++)
                abyte0[i2] = fPiles[i2];

            abyte0[j] = (byte)(fPiles[j] - i);
            Arrays.sort(abyte0);
            boolean flag = false;
            for(int j2 = 0; j2 < j1; j2++)
            {
                boolean flag1 = true;
                if(!childExists(j2))
                    continue;
                for(int l2 = 0; l2 < fNumPiles; l2++)
                    if(fKids[j2].getPile(l2) != abyte0[l2])
                        flag1 = false;

                if(flag1)
                    flag = true;
            }

            if(!flag)
            {
                fKids[j1] = new SmartNode(fNumPiles);
                for(int k2 = 0; k2 < fNumPiles; k2++)
                    fKids[j1].setPile(k2, abyte0[k2]);

            } else
            {
                fKids[j1] = null;
                k++;
            }
            i++;
        }

    }

    public boolean childExists(int i)
    {
        return fKids != null && i < fNumKids && fKids[i] != null;
    }

    public int numChildren()
    {
        return fNumKids;
    }

    public SmartNode getChild(int i)
    {
        return fKids[i];
    }

    public int getState()
    {
        return fState;
    }

    public SmartNode getBestChild()
    {
        int i = 0;
        int j = 0;
        updateState(false);
        for(int k = 0; k < fNumKids; k++)
            if(childExists(k) && fKids[k].getState() > j)
            {
                j = fKids[k].getState();
                i = k;
            }

        return fKids[i];
    }

    public byte updateState(boolean flag)
    {
        if(hasFinished())
        {
            if(flag)
                fState = 0;
            else
                fState = 2;
        } else
        {
            int i = 0;
            byte byte0 = 0;
            if(flag)
                byte0 = 3;
            for(int j = 0; j < fNumKids; j++)
            {
                if(!childExists(j))
                    continue;
                byte byte1 = fKids[j].updateState(!flag);
                if(flag)
                    byte0 = byte1 >= byte0 ? byte0 : byte1;
                else
                    byte0 = byte1 <= byte0 ? byte0 : byte1;
                i++;
            }

            if(i == 0)
                fState = 1;
            else
                fState = byte0;
        }
        return fState;
    }

    public boolean hasFinished()
    {
        int i = 0;
        int j = 0;
        for(int k = 0; k < fNumPiles; k++)
        {
            if(fPiles[k] == 0)
            {
                i++;
                continue;
            }
            if(fPiles[k] != 0)
                j++;
        }

        return j == 0 && i == fNumPiles;
    }

    public boolean willFinish()
    {
        int i = 0;
        int j = 0;
        for(int k = 0; k < fNumPiles; k++)
        {
            if(fPiles[k] == 1)
            {
                i++;
                continue;
            }
            if(fPiles[k] != 0)
                j++;
        }

        return j == 0 && i == 1;
    }

    public byte getPile(int i)
    {
        return fPiles[i];
    }

    public void setPile(int i, byte byte0)
    {
        fPiles[i] = byte0;
    }

    public byte numPiles()
    {
        return fNumPiles;
    }

    public String toString()
    {
        String s = "[ ";
        for(int i = 0; i < fNumPiles; i++)
            s = (new StringBuilder()).append(s).append(Integer.toString(fPiles[i])).toString();

        s = (new StringBuilder()).append(s).append(" : S ").append(Integer.toString(fState)).append(" K ").append(Integer.toString(fNumKids)).append(" (").append(fOurID).append(") ]").toString();
        return s;
    }

    public void dumpTree()
    {
        boolean aflag[] = new boolean[1024];
        for(int i = 0; i < 1024; i++)
            aflag[i] = false;

        aflag[0] = true;
        dumpTree_Inner(1, aflag, 1024);
    }

    public static int getLastID()
    {
        return sLastID;
    }

    private void dumpTree_Inner(int i, boolean aflag[], int j)
    {
        if(i > 1)
        {
            for(int k = 0; k < i - 1; k++)
                if(!aflag[k])
                    System.out.print("|   ");
                else
                    System.out.print("    ");

            if(numChildren() > 0)
                System.out.print("+---+. ");
            else
                System.out.print("+----. ");
        }
        int l = 0;
        for(int i1 = 0; i1 < numChildren(); i1++)
            if(!childExists(i1))
                l++;

        System.out.print(this);
        if(!willFinish())
        {
            if(l > 0)
                System.out.println((new StringBuilder()).append(" (").append(l).append(" null)").toString());
            else
                System.out.println();
            for(int j1 = 0; j1 < numChildren(); j1++)
            {
                if(j1 != numChildren() - 1)
                    aflag[i] = false;
                else
                    aflag[i] = true;
                if(childExists(j1))
                    fKids[j1].dumpTree_Inner(i + 1, aflag, j);
            }

        } else
        {
            System.out.println(" Next move will win.");
        }
    }

    private int fOurID;
    private byte fNumKids;
    private byte fState;
    private byte fNumPiles;
    private byte fPiles[];
    private SmartNode fKids[];
    private static int sLastID = 0;

}

import java.util.Arrays;

public class SmartPlayer
    implements PilesPlayer
{

    public SmartPlayer()
    {
        fFirstMove = true;
    }

    public byte[] makeMove(byte abyte0[])
    {
        if(fFirstMove)
        {
            Queue queue = new Queue();
            fMoveTree = new SmartNode(abyte0.length);
            for(int i = 0; i < abyte0.length; i++)
                fMoveTree.setPile(i, abyte0[i]);

            queue.add(fMoveTree);
            for(; !queue.isEmpty(); queue.tail())
            {
                ((SmartNode)queue.head()).makeChildren();
                for(int j = 0; j < ((SmartNode)queue.head()).numChildren(); j++)
                    if(((SmartNode)queue.head()).childExists(j))
                        queue.add(((SmartNode)queue.head()).getChild(j));

            }

            fFirstMove = false;
        } else
        {
            Arrays.sort(abyte0);
            SmartNode smartnode = null;
            for(int k = 0; k < fMoveTree.numChildren(); k++)
            {
                SmartNode smartnode1 = fMoveTree.getChild(k);
                boolean flag = true;
                if(smartnode1 == null)
                    continue;
                for(int i1 = 0; i1 < smartnode1.numPiles(); i1++)
                    if(smartnode1.getPile(i1) != abyte0[i1])
                        flag = false;

                if(flag)
                    smartnode = smartnode1;
            }

            fMoveTree = smartnode;
        }
        fMoveTree = fMoveTree.getBestChild();
        byte abyte1[] = new byte[abyte0.length];
        for(int l = 0; l < abyte0.length; l++)
            abyte1[l] = fMoveTree.getPile(l);

        return abyte1;
    }

    public String toString()
    {
        return fMoveTree.toString();
    }

    private SmartNode fMoveTree;
    private boolean fFirstMove;
}
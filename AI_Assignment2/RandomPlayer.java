import java.util.Random;

public class RandomPlayer
    implements PilesPlayer
{

    public RandomPlayer()
    {
        fRand = new Random(System.currentTimeMillis());
    }

    public byte[] makeMove(byte abyte0[])
    {
        byte abyte1[] = new byte[abyte0.length];
        for(int i = 0; i < abyte0.length; i++)
            abyte1[i] = abyte0[i];

        int j;
        for(j = (int)(fRand.nextFloat() * (float)abyte0.length); abyte0[j] == 0; j = (int)(fRand.nextFloat() * (float)abyte0.length));
        int k = (int)(fRand.nextFloat() * (float)(abyte0[j] - 1)) + 1;
        abyte1[j] -= k;
        return abyte1;
    }

    private Random fRand;
}
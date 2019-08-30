package konogonka.Tools.NPDM.ACID;

import konogonka.LoperConverter;
import konogonka.RainbowHexDump;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;

/*
NOTE:
KAC is set of 4-byes blocks
Consider them as uInt32 (Read as Little endian)
Look on the tail of each block (low bits). If tail is equals to mask like 0111111 then such block is related to one of the possible sections (KernelFlags etc.)
If it's related to the one of the blocks, then we could pick useful data from this block.
Example:
36 BYES on this section, then 9 blocks with len = 4-bytes each available
1 00-01-02-03
2 04-05-06-07
3 08-09-10-11
4 12-13-14-15
5 16-17-18-19
6 20-21-22-23
7 24-25-26-27
8 28-29-30-31
9 32-33-34-35

Possible patterns are:
Where '+' is useful data; '0' and '1' in low bytes are pattern.
Octal                            | Decimal
++++++++++++++++++++++++++++0111 | 7 <- KernelFlags
+++++++++++++++++++++++++++01111 | 15 <- SyscallMask
+++++++++++++++++++++++++0111111 | 63 <- MapIoOrNormalRange
++++++++++++++++++++++++01111111 | 127 <- MapNormalPage (RW)
++++++++++++++++++++011111111111 | 2+47 <- InterruptPair
++++++++++++++++++01111111111111 | 8191 <- ApplicationType
+++++++++++++++++011111111111111 | 16383 <- KernelReleaseVersion
++++++++++++++++0111111111111111 | 32767 <- HandleTableSize
+++++++++++++++01111111111111111 | 65535 <- DebugFlags
Other masks could be implemented by N in future (?).

Calculation example:
Dec 1 =  00000000000000000000000000000001
00100000000000000000000000000111 & 1 = 1
00010000000000000000000000000011 & 1 = 1
00001000000000000000000000000001 & 1 = 1
00000100000000000000000000000000 & 1 = 0 

TIP: Generate
int j = 0xFFFFFFFF;
for (byte i = 0; i < 16; i++){
    j = (j << 1);
    RainbowHexDump.octDumpInt(~j);
}
 */

public class KernelAccessControlProvider {

    private static final int KERNELFLAGS = 3;
    private static final int SYSCALLMASK = 4;
    private static final int MAPIOORNORMALRANGE = 6;
    private static final int MAPNORMALPAGE_RW = 7;
    private static final int INTERRUPTPAIR = 11;
    private static final int APPLICATIONTYPE = 13;
    private static final int KERNELRELEASEVERSION = 14;
    private static final int HANDLETABLESIZE = 15;
    private static final int DEBUGFLAGS = 16;

    private boolean kernelFlagsAvailable;
    private int kernelFlagCpuIdHi;
    private int kernelFlagCpuIdLo;
    private int kernelFlagThreadPrioHi;
    private int kernelFlagThreadPrioLo;


    KernelAccessControlProvider(byte[] bytes) throws Exception{
        if (bytes.length < 4)
            throw new Exception("ACID-> KernelAccessControlProvider: too small size of the Kernel Access Control");

        int position = 0;
        // Collect all blocks
        for (int i = 0; i < bytes.length / 4; i++) {
            int block = LoperConverter.getLEint(bytes, position);
            position += 4;

            int type = getMinBitCnt(block);

            switch (type){
                case KERNELFLAGS:
                    System.out.println("KERNELFLAGS\t\t"+block+" "+type);
                    kernelFlagsAvailable = true;
                    
                    break;
                case SYSCALLMASK:
                    System.out.println("SYSCALLMASK\t\t"+block+" "+type);
                    
                    break;
                case MAPIOORNORMALRANGE:
                    System.out.println("MAPIOORNORMALRANGE\t\t"+block+" "+type);
                    
                    break;
                case MAPNORMALPAGE_RW:
                    System.out.println("MAPNORMALPAGE_RW\t\t"+block+" "+type);
                    
                    break;
                case INTERRUPTPAIR:
                    System.out.println("INTERRUPTPAIR\t\t"+block+" "+type);
                    
                    break;
                case APPLICATIONTYPE:
                    System.out.println("APPLICATIONTYPE\t\t"+block+" "+type);
                    
                    break;
                case KERNELRELEASEVERSION:
                    System.out.println("KERNELRELEASEVERSION\t"+block+" "+type);
                    
                    break;
                case HANDLETABLESIZE:
                    System.out.println("HANDLETABLESIZE\t\t"+block+" "+type);
                    
                    break;
                case DEBUGFLAGS:
                    System.out.println("DEBUGFLAGS\t\t"+block+" "+type);
                    
                    break;
                default:
                    System.out.println("UNKNOWN\t\t"+block+" "+type);
            }
            RainbowHexDump.octDumpInt(block);
        }
        System.out.println();

        int KernelFlagsHiCpuId; // 7 31-24
        int KernelFlagsLoCpuId; // 7 23-16
        int KernelFlagsHiThreadPrio; // 5 15-10
        int KernelFlagsLoThreadPrio; // 5 9-4
        int SyscallMask;
        int MapIoOrNormalRange;
        int MapNormalPage_RW;
        int InterruptPair;
        int ApplicationType;
        int KernelReleaseVersion;
        int HandleTableSize ;
        int DebugFlags;
    }

    private int getMinBitCnt(int value){
        int minBitCnt = 0;

        while ((value & 1) != 0){
            value >>= 1;
            minBitCnt++;
        }
        return minBitCnt;
    }
}

package konogonka.Tools.NPDM.ACID;

import konogonka.LoperConverter;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/*
NOTE: This implementation is extremely bad for using application as library. Use raw for own purposes.

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

    private static final int    KERNELFLAGS = 3,
                                SYSCALLMASK = 4,
                                MAPIOORNORMALRANGE = 6,
                                MAPNORMALPAGE_RW = 7,
                                INTERRUPTPAIR = 11,
                                APPLICATIONTYPE = 13,
                                KERNELRELEASEVERSION = 14,
                                HANDLETABLESIZE = 15,
                                DEBUGFLAGS = 16;

    // RAW data
    private LinkedList<Integer> rawData;
    // Kernel flags
    private boolean kernelFlagsAvailable;
    private int     kernelFlagCpuIdHi,
                    kernelFlagCpuIdLo,
                    kernelFlagThreadPrioHi,
                    kernelFlagThreadPrioLo;
    // Syscall Masks as index | mask  - order AS IS. [0] = bit5; [1] = bit6
    private LinkedHashMap<Byte, byte[]> syscallMasks; // Index, Mask
    // MapIoOrNormalRange
    private LinkedHashMap<byte[], Boolean> mapIoOrNormalRange; // alt page+num, RO flag
    // MapNormalPage (RW)
    private byte[] mapNormalPage;   // TODO: clarify is possible to have multiple
    // InterruptPair
    private boolean interruptPairAvailable;
    private byte[] irq0,
                    irq1;
    // Application type
    private int applicationType;
    // KernelReleaseVersion
    private boolean isKernelRelVersionAvailable;
    private int kernelRelVersionMajor,
                kernelRelVersionMinor;
    // Handle Table Size
    private int handleTableSize;
    // Debug flags
    private boolean debugFlagsAvailable,
                    canBeDebugged,
                    canDebugOthers;

    KernelAccessControlProvider(byte[] bytes) throws Exception{
        if (bytes.length < 4)
            throw new Exception("ACID-> KernelAccessControlProvider: too small size of the Kernel Access Control");

        rawData = new LinkedList<Integer>();

        syscallMasks = new LinkedHashMap<Byte, byte[]>();
        mapIoOrNormalRange = new LinkedHashMap<byte[], Boolean>();

        int position = 0;
        // Collect all blocks
        for (int i = 0; i < bytes.length / 4; i++) {
            int block = LoperConverter.getLEint(bytes, position);
            position += 4;

            rawData.add(block);

            //RainbowHexDump.octDumpInt(block);

            int type = getMinBitCnt(block);

            switch (type){
                case KERNELFLAGS:
                    kernelFlagsAvailable = true;
                    kernelFlagCpuIdHi = block >> 24;
                    kernelFlagCpuIdLo = block >> 16 & 0b11111111;
                    kernelFlagThreadPrioHi = block >> 10 & 0b111111;
                    kernelFlagThreadPrioLo = block >> 4 & 0b111111;
                    //System.out.println("KERNELFLAGS "+kernelFlagCpuIdHi+" "+kernelFlagCpuIdLo+" "+kernelFlagThreadPrioHi+" "+kernelFlagThreadPrioLo);
                    break;
                case SYSCALLMASK:
                    byte maskTableIndex = (byte) (block >> 29 & 0b111); // declared as byte; max value could be 7; min - 0;
                    byte[] mask = new byte[24];                         // Consider as bit.
                    //System.out.println("SYSCALLMASK ind: "+maskTableIndex);

                    for (int k = 28; k >= 5; k--) {
                        mask[k-5] = (byte) (block >> k & 1);        // Only 1 or 0 possible
                        //System.out.print(mask[k-5]);
                    }
                    //System.out.println();
                    syscallMasks.put(maskTableIndex, mask);
                    break;
                case MAPIOORNORMALRANGE:
                    byte[] altStPgNPgNum = new byte[24];
                    //System.out.println("MAPIOORNORMALRANGE Flag: "+((block >> 31 & 1) != 0));

                    for (int k = 30; k >= 7; k--){
                        altStPgNPgNum[k-7] = (byte) (block >> k & 1);        // Only 1 or 0 possible
                        //System.out.print(altStPgNPgNum[k-7]);
                    }
                    mapIoOrNormalRange.put(altStPgNPgNum, (block >> 31 & 1) != 0);
                    //System.out.println();
                    break;
                case MAPNORMALPAGE_RW:
                    //System.out.println("MAPNORMALPAGE_RW\t");
                    mapNormalPage = new byte[24];
                    for (int k = 31; k >= 8; k--){
                        mapNormalPage[k-8] = (byte) (block >> k & 1);
                        //System.out.print(mapNormalPage[k-8]);
                    }
                    //System.out.println();
                    break;
                case INTERRUPTPAIR:
                    System.out.println("INTERRUPTPAIR found (must (?) appear once, please report if it's not)");
                    interruptPairAvailable = true;
                    irq0 = new byte[10];
                    irq1 = new byte[10];
                    for (int k = 21; k >= 12; k--)
                        irq0[k-12] = (byte) (block >> k & 1);
                    for (int k = 31; k >= 22; k--)
                        irq1[k-22] = (byte) (block >> k & 1);
                    break;
                case APPLICATIONTYPE:
                    applicationType = block >> 14 & 0b111;
                    //System.out.println("APPLICATIONTYPE "+applicationType);
                    break;
                case KERNELRELEASEVERSION:
                    //System.out.println("KERNELRELEASEVERSION\t"+(block >> 19 & 0b111111111111)+"."+(block >> 15 & 0b1111)+".X");
                    isKernelRelVersionAvailable = true;
                    kernelRelVersionMajor = (block >> 19 & 0b111111111111);
                    kernelRelVersionMinor = (block >> 15 & 0b1111);
                    break;
                case HANDLETABLESIZE:
                    handleTableSize = block >> 16 & 0b1111111111;
                    //System.out.println("HANDLETABLESIZE "+handleTableSize);
                    break;
                case DEBUGFLAGS:
                    debugFlagsAvailable = true;
                    canBeDebugged = (block >> 17 & 1) != 0;
                    canDebugOthers = (block >> 18 & 1) != 0;
                    //System.out.println("DEBUGFLAGS "+canBeDebugged+" "+canDebugOthers);
                    break;
                default:
                    System.out.println("UNKNOWN\t\t"+block+" "+type);
            }
        }
        //System.out.println();
    }

    private int getMinBitCnt(int value){
        int minBitCnt = 0;
        while ((value & 1) != 0){
            value >>= 1;
            minBitCnt++;
        }
        return minBitCnt;
    }
    public LinkedList<Integer> getRawData() { return rawData; }
    public boolean isKernelFlagsAvailable() { return kernelFlagsAvailable; }
    public int getKernelFlagCpuIdHi() { return kernelFlagCpuIdHi; }
    public int getKernelFlagCpuIdLo() { return kernelFlagCpuIdLo; }
    public int getKernelFlagThreadPrioHi() { return kernelFlagThreadPrioHi; }
    public int getKernelFlagThreadPrioLo() { return kernelFlagThreadPrioLo; }
    public LinkedHashMap<byte[], Boolean> getMapIoOrNormalRange() { return mapIoOrNormalRange; }
    public byte[] getMapNormalPage() { return mapNormalPage; }
    public boolean isInterruptPairAvailable() { return interruptPairAvailable; }
    public byte[] getIrq0() { return irq0; }
    public byte[] getIrq1() { return irq1; }
    public int getApplicationType() { return applicationType; }
    public boolean isKernelRelVersionAvailable() { return isKernelRelVersionAvailable; }
    public int getKernelRelVersionMajor() { return kernelRelVersionMajor; }
    public int getKernelRelVersionMinor() { return kernelRelVersionMinor;}
    public int getHandleTableSize() { return handleTableSize; }
    public boolean isDebugFlagsAvailable() { return debugFlagsAvailable; }
    public boolean isCanBeDebugged() { return canBeDebugged; }
    public boolean isCanDebugOthers() { return canDebugOthers; }
    public LinkedHashMap<Byte, byte[]> getSyscallMasks() { return syscallMasks; }
}

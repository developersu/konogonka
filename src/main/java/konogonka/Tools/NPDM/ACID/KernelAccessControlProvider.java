package konogonka.Tools.NPDM.ACID;

import konogonka.LoperConverter;
import konogonka.RainbowHexDump;

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
Octal                            | Decimal
00000000000000000000000000000111 | 7 <- KernelFlags
00000000000000000000000000001111 | 15 <- SyscallMask
00000000000000000000000000111111 | 63 <- MapIoOrNormalRange
00000000000000000000000001111111 | 127 <- MapNormalPage (RW)
00000000000000000000011111111111 | 2047 <- InterruptPair
00000000000000000001111111111111 | 8191 <- ApplicationType
00000000000000000011111111111111 | 16383 <- KernelReleaseVersion
00000000000000000111111111111111 | 32767 <- HandleTableSize
00000000000000001111111111111111 | 65535 <- DebugFlags
Other masks could be implemented by N in future (?).

TIP: Generate
int j = 0xFFFFFFFF;
for (byte i = 0; i < 16; i++){
    j = (j << 1);
    RainbowHexDump.octDumpInt(~j);
}
 */

public class KernelAccessControlProvider {

    KernelAccessControlProvider(byte[] bytes) throws Exception{
        if (bytes.length < 4)
            throw new Exception("ACID-> KernelAccessControlProvider: too small size of the Kernel Access Control");
        final int pattrnKernFlags = 7;
        final int pattrnSyscallMsk = 15;
        final int pattrnMapIoNormalRange = 63;
        final int pattrnRw = 127;
        final int pattrnInterrPair = 2047;
        final int pattrnAppType = 8191;
        final int pattrnKernRelVer = 16383;
        final int pattrnHandlTblSize = 32767;
        final int pattrnDbgFlags = 65535;

        RainbowHexDump.hexDumpUTF8(bytes);
        for (int o = 0; o < bytes.length; o += 4) {
            RainbowHexDump.octDumpInt(LoperConverter.getLEint(bytes, o));
        }
        System.out.println();

        RainbowHexDump.octDumpInt(pattrnKernFlags);
        RainbowHexDump.octDumpInt(pattrnSyscallMsk);
        RainbowHexDump.octDumpInt(pattrnMapIoNormalRange);
        RainbowHexDump.octDumpInt(pattrnRw);
        RainbowHexDump.octDumpInt(pattrnInterrPair);
        RainbowHexDump.octDumpInt(pattrnAppType);
        RainbowHexDump.octDumpInt(pattrnKernRelVer);
        RainbowHexDump.octDumpInt(pattrnHandlTblSize);
        RainbowHexDump.octDumpInt(pattrnDbgFlags);


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
}

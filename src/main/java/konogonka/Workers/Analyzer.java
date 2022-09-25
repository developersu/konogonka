/*
    Copyright 2019-2022 Dmitry Isaenko

    This file is part of Konogonka.

    Konogonka is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Konogonka is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Konogonka.  If not, see <https://www.gnu.org/licenses/>.
*/
package konogonka.Workers;

import javafx.concurrent.Task;
import konogonka.ModelControllers.EMsgType;
import konogonka.ModelControllers.LogPrinter;
import libKonogonka.Tools.ISuperProvider;
import libKonogonka.Tools.NCA.NCAProvider;
import libKonogonka.Tools.NPDM.NPDMProvider;
import libKonogonka.Tools.PFS0.PFS0Provider;
import libKonogonka.Tools.RomFs.RomFsProvider;
import libKonogonka.Tools.TIK.TIKProvider;
import libKonogonka.Tools.XCI.XCIProvider;

import java.io.File;
import java.util.HashMap;

// TODO: volatile needed?

public class Analyzer {

    public static Task<NCAProvider> analyzeNCA(File file, HashMap<String, String> keysMap, long offset){
        LogPrinter logPrinter = new LogPrinter();

        return new Task<NCAProvider>(){
            @Override
            protected NCAProvider call() {
                logPrinter.print("\tStart chain: NCA", EMsgType.INFO);
                try {
                    return new NCAProvider(file, keysMap, offset);
                }
                catch (Exception e){
                    logPrinter.print(e.getMessage(), EMsgType.FAIL);
                    return null;
                }
                finally {
                    logPrinter.print("\tEnd chain: NCA", EMsgType.INFO);
                    logPrinter.close();
                }
            }
        };
    }

    public static Task<TIKProvider> analyzeTIK(File file, long offset){
        LogPrinter logPrinter = new LogPrinter();
        return new Task<TIKProvider>() {
            @Override
            protected TIKProvider call() {
                logPrinter.print("\tStart chain: TIK", EMsgType.INFO);
                try{
                    return new TIKProvider(file, offset);
                }
                catch (Exception e){
                    logPrinter.print("\tException: "+e.getMessage(), EMsgType.FAIL);
                    return null;
                }
                finally {
                    logPrinter.print("\tEnd chain: TIK", EMsgType.INFO);
                    logPrinter.close();
                }
            }
        };
    }

    public static Task<NPDMProvider> analyzeNPDM(File file, long offset){
        LogPrinter logPrinter = new LogPrinter();
        return new Task<NPDMProvider>(){
            @Override
            protected NPDMProvider call() {
                logPrinter.print("\tStart chain: NPDM", EMsgType.INFO);
                try{
                    return new NPDMProvider(file, offset);
                }
                catch (Exception e){
                    logPrinter.print("\tException: "+e.getMessage(), EMsgType.FAIL);
                    return null;
                }
                finally {
                    logPrinter.print("\tEnd chain: NPDM", EMsgType.INFO);
                    logPrinter.close();
                }
            }
        };
    }

    public static Task<NPDMProvider> analyzeNPDM(ISuperProvider parentProvider, int fileNo){
        LogPrinter logPrinter = new LogPrinter();
        return new Task<NPDMProvider>(){
            @Override
            protected NPDMProvider call() {
                logPrinter.print("\tStart chain: NPDM [stream]", EMsgType.INFO);
                try{
                    return new NPDMProvider(parentProvider.getProviderSubFilePipedInpStream(fileNo));
                }
                catch (Exception e){
                    logPrinter.print("\tException: "+e.getMessage(), EMsgType.FAIL);
                    return null;
                }
                finally {
                    logPrinter.print("\tEnd chain: NPDM [stream]", EMsgType.INFO);
                    logPrinter.close();
                }
            }
        };
    }

    public static Task<PFS0Provider> analyzePFS0(File file){
        LogPrinter logPrinter = new LogPrinter();
        return new Task<PFS0Provider>(){
            @Override
            protected PFS0Provider call() {
                logPrinter.print("\tStart chain: NSP (PFS0)", EMsgType.INFO);
                try{
                    return new PFS0Provider(file);
                }
                catch (Exception e){
                    logPrinter.print("\tException: "+e.getMessage(), EMsgType.FAIL);
                    return null;
                }
                finally {
                    logPrinter.print("\tEnd chain: NSP (PFS0)", EMsgType.INFO);
                    logPrinter.close();
                }
            }
        };
    }

    public static Task<XCIProvider> analyzeXCI(File file, String xciHdrKey){
        LogPrinter logPrinter = new LogPrinter();
        return new Task<XCIProvider>() {
            @Override
            protected XCIProvider call() {
                logPrinter.print("\tStart chain: XCI", EMsgType.INFO);
                try {
                    return new XCIProvider(file, xciHdrKey);
                } catch (Exception e) {
                    logPrinter.print(e.getMessage(), EMsgType.FAIL);
                    return null;
                } finally {
                    logPrinter.print("\tEnd chain: XCI", EMsgType.INFO);
                    logPrinter.close();
                }
            }
        };
    }

    public static Task<RomFsProvider> analyzeRomFS(File file, long lv6offset){
        LogPrinter logPrinter = new LogPrinter();
        return new Task<RomFsProvider>() {
            @Override
            protected RomFsProvider call() {
                logPrinter.print("\tStart chain: RomFS", EMsgType.INFO);
                try {
                    return new RomFsProvider(file, lv6offset);
                } catch (Exception e) {
                    logPrinter.print(e.getMessage(), EMsgType.FAIL);
                    return null;
                } finally {
                    logPrinter.print("\tEnd chain: RomFS", EMsgType.INFO);
                    logPrinter.close();
                }
            }
        };
    }
}

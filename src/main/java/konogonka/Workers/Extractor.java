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
import konogonka.Controllers.IRowModel;
import konogonka.ModelControllers.EMsgType;
import konogonka.ModelControllers.LogPrinter;
import libKonogonka.Tools.ISuperProvider;

import java.util.List;

public class Extractor extends Task<Void> {

    private final ISuperProvider provider;
    private final List<IRowModel> models;
    private final LogPrinter logPrinter;

    private final String filesDestinationPath;

    public Extractor(ISuperProvider provider, List<IRowModel> models, String filesDestinationPath){
        this.provider = provider;
        this.models = models;
        this.filesDestinationPath = filesDestinationPath;
        this.logPrinter = new LogPrinter();
    }

    @Override
    protected Void call() {
        for (IRowModel model : models) {
            logPrinter.print("\tStart extracting: \n"+ filesDestinationPath +model.getFileName(), EMsgType.INFO);
            try {
                provider.exportContent(filesDestinationPath, model.getNumber());
            } catch (Exception ioe) {
                logPrinter.print("\tExtracting issue\n\t" + ioe.getMessage(), EMsgType.INFO);
            } finally {
                logPrinter.print("\tEnd extracting", EMsgType.INFO);
                logPrinter.close();
            }
        }
        return null;
    }
}
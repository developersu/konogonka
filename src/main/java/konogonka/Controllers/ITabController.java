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
package konogonka.Controllers;

import javafx.fxml.Initializable;
import libKonogonka.Tools.ISuperProvider;

import java.io.File;

public interface ITabController extends Initializable {
    void analyze(File file);
    void analyze(File file, long offset);
    void analyze(ISuperProvider parentProvider, int fileNo) throws Exception;
    void resetTab();
}

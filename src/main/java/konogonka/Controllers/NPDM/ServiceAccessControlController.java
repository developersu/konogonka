/*
    Copyright 2019-2020 Dmitry Isaenko

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
package konogonka.Controllers.NPDM;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServiceAccessControlController {

    @FXML
    private VBox SACPane;

    public void resetTab(){
        SACPane.getChildren().clear();
    }

    public void populateFields(LinkedHashMap<String, Byte> collection){
        resetTab();

        for (Map.Entry entry : collection.entrySet()) {
            Label control = new Label(String.format("0x%02x", (Byte) entry.getValue()));
            Label serviceName = new Label((String) entry.getKey());

            control.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
            serviceName.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));

            SACPane.getChildren().add(new HBox(control, serviceName));
        }
    }
}

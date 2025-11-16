package dev.magadiflo.app.immutability;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RecordUnsafeDemo {

    record UserRecord(String name, List<String> roles) {
    }

    public static void main(String[] args) {
        List<String> externalRoles = new ArrayList<>();
        externalRoles.add("USER");

        UserRecord userRecord = new UserRecord("Milagros", externalRoles);
        log.info("Objeto inicial: {}", userRecord);

        // Parecería que es inmutable... pero
        externalRoles.add("ADMIN"); // modificamos la lista por fuera

        // La clase "inmutable" no lo era en realidad
        log.info("Objeto final: {}", userRecord); // La "inmutabilidad" quedó destruida.
    }
}

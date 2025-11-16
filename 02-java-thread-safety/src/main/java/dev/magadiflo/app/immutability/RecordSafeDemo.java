package dev.magadiflo.app.immutability;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class RecordSafeDemo {

    /**
     * Record realmente inmutable.
     * Se hace copia defensiva de la lista.
     */
    record UserRecord(String name, List<String> roles) {
        UserRecord(String name, List<String> roles) {
            this.name = name;
            this.roles = List.copyOf(roles); // Copia defensiva + lista inmodificable
        }
    }

    public static void main(String[] args) {
        List<String> externalRoles = new ArrayList<>();
        externalRoles.add("USER");

        UserRecord userRecord = new UserRecord("Milagros", externalRoles);
        log.info("Objeto inicial: {}", userRecord);

        externalRoles.add("ADMIN"); // intentamos afectar el record

        log.info("Objeto final: {}", userRecord); // Ahora sí, el record es verdaderamente inmutable.
    }
}

package dev.magadiflo.app.immutability;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefensiveCopyInRecordDemo {

    record UserRecord(String username, Role role) {
        UserRecord {
            role = this.copyRole(role);
        }

        @Override
        public Role role() {
            return this.copyRole(this.role);
        }

        private Role copyRole(Role role) {
            return new Role(role.getName());
        }
    }

    static class Role {
        private String name;

        public Role(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Role{");
            sb.append("name='").append(name).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        Role role = new Role("USER");
        UserRecord record = new UserRecord("Milagros", role);
        log.info("Objeto inicial: {}", record);

        // Modificamos el objeto original.
        role.setName("ADMIN");
        log.info("Objeto final: {}", record);

        // Obtenemos una copia del objeto interno y la modificamos
        record.role().setName("SUPER_ADMIN");
        log.info("Objeto final 2: {}", record);
    }
}

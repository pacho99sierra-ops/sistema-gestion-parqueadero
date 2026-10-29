package app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class Main {

    static final Scanner SCANNER = new Scanner(System.in);

    static final int CAPACIDAD_TOTAL = 10;
    static final int TARIFA_HORA_CARRO = 4000;
    static final int TARIFA_HORA_MOTO = 2000;
    static final int TARIFA_DIA_CARRO = 30000;
    static final int TARIFA_DIA_MOTO = 15000;
    static final int UMBRAL_HORAS_DIA = 8;
    static final int HORA_APERTURA = 8;
    static final int HORA_CIERRE = 22;

    static final DateTimeFormatter FORMATO_FECHA_HORA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    static final String CARRO = "Carro";
    static final String MOTO = "Moto";

    static String[] tiposVehiculo = new String[CAPACIDAD_TOTAL];
    static LocalDateTime[] fechasIngreso = new LocalDateTime[CAPACIDAD_TOTAL];

    static int celdasOcupadas = 0;
    static long recaudoCarros = 0;
    static long recaudoMotos = 0;

    public static void main(String[] args) {
        inicializarArreglos();

        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opcion: ", 1, 5);

            switch (opcion) {
                case 1 -> registrarEntrada();
                case 2 -> registrarSalida();
                case 3 -> reporteOcupacion();
                case 4 -> reporteFinanciero();
                case 5 -> imprimir("Gracias por usar el sistema de gestion.");
                default -> imprimir("Opcion no valida.");
            }
        } while (opcion != 5);
        SCANNER.close();
    }

    static void inicializarArreglos() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            tiposVehiculo[i] = null;
            fechasIngreso[i] = null;
        }
    }
    static void imprimir(String mensaje) {
        System.out.println(mensaje);
    }
    static void mostrarMenu() {
        imprimir("");
        imprimir("=====Parqueadero=====");
        imprimir("1. Registrar entrada");
        imprimir("2. Registrar salida");
        imprimir("3. Reporte de ocupacion");
        imprimir("4. Reporte financiero");
        imprimir("5. Salir");
    }

    static void registrarEntrada(){
        int celda = buscarCeldaDisponibles();
        if (celda == -1) {
            imprimir("El parqueadero esta lleno. No hay celdas disponibles");
            return;
        }
        int tipo = leerEntero("Seleccione el tipo de vehiculo: 1 para carro, 2 para moto. ", 1, 2);

        LocalDateTime ingreso = leerFechaHora("Fecha y hora de ingreso (yyyy-MM-dd HH:mm): ");


        if (tipo == 1) {
            tiposVehiculo[celda] = CARRO;
        } else {
            tiposVehiculo[celda] = MOTO;
        }
        fechasIngreso[celda] = ingreso;
        celdasOcupadas++;

        imprimir(tiposVehiculo[celda] + " registrado en la celda " + (celda + 1) + ".");
    }

    static void registrarSalida() {
        if (celdasOcupadas == 0) {
            imprimir("El parqueadero esta vacio. No hay vehiculos para retirar.");
            return;
        }

        int celda = leerEntero("Numero de celda a liberar (1 a " + CAPACIDAD_TOTAL + "): ", 1, CAPACIDAD_TOTAL);
        int indice = celda - 1;

        if (tiposVehiculo[indice] == null) {
            imprimir("La celda " + celda + " esta vacia.");
            return;
        }

        imprimir("Celda " + celda + ": " + tiposVehiculo[indice]
                + ", ingreso " + fechasIngreso[indice].format(FORMATO_FECHA_HORA));

        LocalDateTime ingreso = fechasIngreso[indice];
        LocalDateTime salida = leerFechaHora("Fecha y hora de salida (yyyy-MM-dd HH:mm): ");

        while (salida.isBefore(ingreso)) {
            imprimir("ERROR: La salida no puede ser anterior al ingreso ("
                    + ingreso.format(FORMATO_FECHA_HORA) + ").");
            salida = leerFechaHora("Fecha y hora de salida (yyyy-MM-dd HH:mm): ");
        }

        String tipo = tiposVehiculo[indice];

        long total = liquidar(celda, tipo, ingreso, salida);

        if (tipo.equals(CARRO)) {
            recaudoCarros += total;
        } else {
            recaudoMotos += total;
        }

        tiposVehiculo[indice] = null;
        fechasIngreso[indice] = null;
        celdasOcupadas--;

    }

    static int buscarCeldaDisponibles() {
        for (int i = 0; i < CAPACIDAD_TOTAL; i++) {
            if (tiposVehiculo[i] == null) {
                return i;
            }
        }
        return -1;
    }

    static int leerEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            imprimir(mensaje);

            if (!SCANNER.hasNextInt()) {
                imprimir("ERROR: Por favor ingrese un numero entero.");
                SCANNER.nextLine();
                continue;
            }

            int valor  = SCANNER.nextInt();
            SCANNER.nextLine();

            if (valor < minimo || valor > maximo) {
                imprimir("ERROR: El valor debe estar entre " + minimo + " y " + maximo + ".");
                continue;
            }
            return valor;
        }
    }

    static int leerDigitos(String texto, int inicio, int cantidad) {
        int valor = 0;
        for (int i = inicio; i < inicio + cantidad; i++) {
            char caracter = texto.charAt(i);
            if (caracter < '0' || caracter > '9') {
                return -1;
            }
            valor = valor * 10 + caracter - '0';
        }
        return valor;
    }

    static boolean formatoFechaHoraValido(String texto) {
        if (texto.length() != 16
                || texto.charAt(4) != '-'
                || texto.charAt(7) != '-'
                || texto.charAt(10) != ' '
                || texto.charAt(13) != ':') {
            return false;
        }

        int anio = leerDigitos(texto, 0, 4);
        int mes = leerDigitos(texto, 5, 2);
        int dia = leerDigitos(texto, 8, 2);
        int hora = leerDigitos(texto, 11, 2);
        int minuto = leerDigitos(texto, 14, 2);

        return anio >= 1 && mes >= 1 && mes <= 12 && dia >= 1 && dia <= 31
                && hora >= HORA_APERTURA && hora <= HORA_CIERRE
                && (hora != HORA_CIERRE || minuto <= 0)
                && minuto >= 0 && minuto <= 59;
    }

    static LocalDateTime leerFechaHora(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String texto = SCANNER.nextLine().trim();
            if (!formatoFechaHoraValido(texto)) {
                imprimir("Fecha u hora inválidas. Use yyyy-MM-dd HH:mm y el horario 08:00-22:00.");
                continue;
            }

            LocalDateTime fecha = LocalDateTime.parse(texto, FORMATO_FECHA_HORA);
            if (!fecha.format(FORMATO_FECHA_HORA).equals(texto)) {
                imprimir("Error: esa fecha no existe en el calendario.");
                continue;
            }

            return fecha;
        }
    }

    static long contarDiasCompletos(LocalDateTime ingreso, LocalDateTime salida) {
        return ChronoUnit.DAYS.between(ingreso, salida);
    }

    static long calcularHorasRestantes(LocalDateTime cursor, LocalDateTime salida) {
        long minutosRestantes = Duration.between(cursor, salida).toMinutes();
        return (minutosRestantes + 59) / 60;
    }

    static long liquidar(int celda, String tipo, LocalDateTime ingreso, LocalDateTime salida) {
        long diasACobrar = contarDiasCompletos(ingreso, salida);
        LocalDateTime cursor = ingreso.plusDays(diasACobrar);
        long horasACobrar = calcularHorasRestantes(cursor, salida);

        if (horasACobrar >= UMBRAL_HORAS_DIA) {
            diasACobrar++;
            horasACobrar = 0;
        }

        int tarifaDia;
        int tarifaHora;
        if (tipo.equals(CARRO)) {
            tarifaDia = TARIFA_DIA_CARRO;
            tarifaHora = TARIFA_HORA_CARRO;
        } else {
            tarifaDia = TARIFA_DIA_MOTO;
            tarifaHora = TARIFA_HORA_MOTO;
        }

        long total = diasACobrar * tarifaDia + horasACobrar * tarifaHora;

        mostrarFactura(celda, tipo, ingreso, salida, diasACobrar, tarifaDia, horasACobrar, tarifaHora, total);
        return total;
    }

    static void mostrarFactura(int celda, String tipo, LocalDateTime ingreso, LocalDateTime salida,
                               long dias, int tarifaDia, long horas, int tarifaHora, long total) {
        imprimir("");
        imprimir("======= FACTURA =======");
        imprimir("Celda liberada: " + celda);
        imprimir("Vehiculo: " + tipo);
        imprimir("Ingreso: " + ingreso.format(FORMATO_FECHA_HORA));
        imprimir("Salida:  " + salida.format(FORMATO_FECHA_HORA));
        imprimir("-----------------------");
        if (dias > 0) {
            imprimir(dias + " dia(s) x $" + tarifaDia + " = $" + (dias * tarifaDia));
        }
        if (horas > 0) {
            imprimir(horas + " hora(s) x $" + tarifaHora + " = $" + (horas * tarifaHora));
        }
        imprimir("-----------------------");
        imprimir("TOTAL A PAGAR: $" + total);
        imprimir("=======================");
    }

    static void reporteOcupacion() {
        int celdasLibres = CAPACIDAD_TOTAL - celdasOcupadas;

        imprimir("");
        imprimir("=== REPORTE DE OCUPACION ===");
        imprimir("Celdas ocupadas: " + celdasOcupadas);
        imprimir("Celdas libres: " + celdasLibres);
    }

    static void reporteFinanciero() {
        long totalGeneral = recaudoCarros + recaudoMotos;

        imprimir("");
        imprimir("=== REPORTE FINANCIERO ===");
        imprimir("Recaudo carros: $" + recaudoCarros);
        imprimir("Recaudo motos: $" + recaudoMotos);
        imprimir("Total general: $" + totalGeneral);
    }


}

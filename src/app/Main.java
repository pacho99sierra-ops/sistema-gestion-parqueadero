package app;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

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
                case 2 -> imprimir("Registro de salida: pendiente.");
                case 3 -> imprimir("Reporte de ocupacion: pendiente.");
                case 4 -> imprimir("Reporte financiero: pendiente.");
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
}

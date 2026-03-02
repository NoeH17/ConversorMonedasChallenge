import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        int opc = 0;
        Scanner input = new Scanner(System.in);
        do {
            System.out.println("Elige una opción: \n1. Hacer conversión\n2. Salir");
            opc = input.nextInt();
            switch (opc) {
                case 1:
                    String codesUrl = "https://v6.exchangerate-api.com/v6/f1f05f395c8a55fc70afd2a4/codes";

                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest requestCodes = HttpRequest.newBuilder().uri(URI.create(codesUrl)).GET().build();
                    HttpResponse<String> responseCodes = client.send(requestCodes, HttpResponse.BodyHandlers.ofString());

                    Gson gson = new Gson();
                    CodesResponse codesObj = gson.fromJson(responseCodes.body(), CodesResponse.class);

                    List<List<String>> rawList = codesObj.getSupported_codes();
                    List<Currency> monedas = new ArrayList<>();

                    for (List<String> item : rawList) {
                        monedas.add(new Currency(item.get(0), item.get(1)));
                    }

                    System.out.println("Selecciona moneda base:");
                    for (int i = 0; i < monedas.size(); i++) {
                        System.out.println((i + 1) + " - " + monedas.get(i).getCode() + " (" + monedas.get(i).getName() + ")");
                    }


                    System.out.println("Elige la moneda base");
                    int optionBase = input.nextInt();
                    var baseCurrency = monedas.get(optionBase - 1).getCode();


                    System.out.println("Elige la moneda objetivo");
                    int optionTarget = input.nextInt();
                    var targetCurrency = monedas.get(optionTarget - 1).getCode();

                    System.out.println("Ingresa la cantidad a convertir de " + monedas.get(optionBase - 1).getName() + " a " + monedas.get(optionTarget - 1).getName());
                    double quantity = input.nextDouble();

                    String pairUrl = "https://v6.exchangerate-api.com/v6/f1f05f395c8a55fc70afd2a4/pair/" + baseCurrency + "/" + targetCurrency + "/" + quantity;
                    HttpClient requestPair = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(pairUrl)).build();

                    HttpResponse<String> responsePair = client.send(request, HttpResponse.BodyHandlers.ofString());

                    PairResponse pairObj = gson.fromJson(responsePair.body(), PairResponse.class);

                    double resultado = pairObj.getConversion_result();
                    System.out.println(quantity + " " + pairObj.getBase_code() + " = " + String.format("%.2f", resultado) + " " + pairObj.getTarget_code());
                    break;

                case 2:
                    System.out.println("\nSaliendo del programa");
                    break;
                default:
                    System.out.println("\nNúmero no válido");
                    break;
            }

        } while (opc != 2);

    }
}

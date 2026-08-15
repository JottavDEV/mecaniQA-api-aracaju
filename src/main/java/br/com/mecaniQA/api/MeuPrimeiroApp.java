package br.com.mecaniQA.api;

import br.com.mecaniQA.api.model.Servico;

public class MeuPrimeiroApp {

    public static void main(String[] args) {


        Servico servico = new Servico();

        servico.setNomeServico("Manutencao Preventiva");



        System.out.println("Meu Primeiro App");

        System.out.println("O Servico se chama:" + servico.getNomeServico());

    }

}

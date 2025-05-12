import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Comercio {
    /** Para inclusão de novos produtos no vetor */
    static final int MAX_NOVOS_PRODUTOS = 10;

    /** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura do teclado */
    static Scanner teclado;

    /** Vetor de produtos cadastrados. Sempre terá espaço para 10 novos produtos a cada execução */
    static Produto[] produtosCadastrados;

    /** Quantidade produtos cadastrados atualmente no vetor */
    static int quantosProdutos;

    /** Quantidade máxima de pedidos que podem ser armazenados no vetor */
    //static final int MAX_PEDIDOS = 10;
    
    /** Vetor de pedidos cadastrados */
    static Fila<Pedido> pedidosCadastrados;
    
    /** Quantidade de pedidos cadastrados atualmente no vetor */
    static int quantPedidos;
    
    static Pilha<Produto> produtosPedidosRecentes=new Pilha<>();


    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa(){
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho(){
        System.out.println("AEDII COMÉRCIO DE COISINHAS");
        System.out.println("===========================");
    }

    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * Perceba que poderia haver uma melhor modularização com a criação de uma classe Menu.
     * @return Um inteiro com a opção do usuário.
    */
    static int menu() {
        cabecalho();
        System.out.println("1 - Listar todos os produtos");
        System.out.println("2 - Procurar e imprimir os dados de um produto");
        System.out.println("3 - Cadastrar novo produto");
        System.out.println("4 - Iniciar novo pedido");
        System.out.println("5 - Fechar pedido");
        System.out.println("6 - Exibir o valor total médio dos N primeiros pedidos");
        System.out.println("7 - Exibir os primeiros pedidos com valor total acima de um determinado valor");
        System.out.println("8 - Exibir os primeiros pedidos que apresentam um determinado produto");
        System.out.println("9 - Listar K produtos mais recentemente vendidos");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }

    /**
     * Lê os dados de um arquivo texto e retorna um vetor de produtos. Arquivo no formato
     * N  (quantiade de produtos) <br/>
     * tipo; descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. Retorna um vetor vazio em caso de problemas com o arquivo.
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @return Um vetor com os produtos carregados, ou vazio em caso de problemas de leitura.
     */
    static Produto[] lerProdutos(String nomeArquivoDados) {
        try{
            Scanner arquivo = new Scanner(new File(nomeArquivoDados));
            int quantidade = Integer.parseInt(arquivo.nextLine());
            produtosCadastrados = new Produto[quantidade+MAX_NOVOS_PRODUTOS];
            
            for (int i = 0; i < quantidade; i++) {
                String linha = arquivo.nextLine();
                produtosCadastrados[i] = Produto.criarDoTexto(linha);
            }
            quantosProdutos = quantidade;
            arquivo.close();
            return produtosCadastrados;
        }catch(IOException exception){
            return null;
        }    
    }

    
    /* Localiza um produto no vetor de cadastrados, a partir do nome, e imprime seus dados. 
    A busca não é sensível ao caso.  Em caso de não encontrar o produto, imprime mensagem padrão */
    static void localizarProdutos(){
        cabecalho();
        String desc;
        System.out.print("Digite o nome do produto a localizar: ");
        desc = teclado.nextLine();
        Produto busca = new ProdutoNaoPerecivel(desc, 1);
        Produto resultado = null;
        for (int i = 0; i < quantosProdutos; i++) {
            if(produtosCadastrados[i].equals(busca))
                resultado = produtosCadastrados[i];
        }
        if(resultado!=null)
            System.out.println(resultado);
        else
            System.out.println("Produto não encontrado.");
    }

    /** Lista todos os produtos cadastrados, numerados, um por linha */
    public static void listarTodosOsProdutos(){
        cabecalho();
        System.out.println("\nPRODUTOS CADASTRADOS:");
        for (int i = 0; i < produtosCadastrados.length; i++) {
            if(produtosCadastrados[i]!=null)
                System.out.println(String.format("%02d - %s", (i+1),produtosCadastrados[i].toString()));
        }
    }

    /**
     * Rotina de cadastro de um novo produto: pergunta ao usuário o tipo do produto, lê os dados correspondentes,
     * cria o objeto adequado de acordo com o tipo, inclui no vetor. Este método pode ser feito com um nível muito 
     * melhor de modularização. As diversas fases da lógica poderiam ser encapsuladas em outros métodos. 
     * Uma sugestão de melhoria mais significativa poderia ser o uso de padrão Factory Method para criação dos objetos.
     */
    public static void cadastrarProduto(){
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        int tipo;
        double precoCusto;
        double margemLucro;
        String desc;
        LocalDate dataValidade;

        cabecalho();
        System.out.println("Cadastro de novo produto:");
        System.out.println("1 - Não perecível (padrão)");
        System.out.println("2 - Perecível");
        System.out.print("Digite o tipo desejado: ");
        tipo = Integer.parseInt(teclado.nextLine());
        if(tipo!=2)
            tipo = 1;
        System.out.print("\nDescrição do produto: ");
        desc = teclado.nextLine();
        System.out.print("Preço de custo: R$ ");
        precoCusto = Double.parseDouble(teclado.nextLine());
        System.out.print("Margem de lucro: ");
        margemLucro = Double.parseDouble(teclado.nextLine());
        if(tipo==2){
            System.out.print("Data de validade (dd/mm/aaaa): ");
            dataValidade = LocalDate.parse(teclado.nextLine(), formatoData);
            produtosCadastrados[quantosProdutos] = new ProdutoPerecivel(desc, precoCusto, margemLucro, dataValidade);
        }
        else
            produtosCadastrados[quantosProdutos] = new ProdutoNaoPerecivel(desc, precoCusto, margemLucro);
        
        quantosProdutos++;

        System.out.println(desc+" cadastrado com sucesso. Total de "+quantosProdutos+" produtos cadastrados");
    }

    /**
     * Salva os dados dos produtos cadastrados no arquivo csv informado. Sobrescreve todo o conteúdo do arquivo.
     * @param nomeArquivo Nome do arquivo a ser gravado.
     */
    public static void salvarProdutos(String nomeArquivo){
        try {
            FileWriter arquivoSaida = new FileWriter(nomeArquivo, Charset.forName("UTF-8"));
            arquivoSaida.append(quantosProdutos+"\n");
            for (int i = 0; i < produtosCadastrados.length; i++) {
                if(produtosCadastrados[i] != null)
                    arquivoSaida.append(produtosCadastrados[i].gerarDadosTexto()+"\n");
            }
            arquivoSaida.close();    
            System.out.println("Arquivo "+nomeArquivo+" salvo.");
        } catch (IOException e) {
            System.out.println("Problemas no arquivo "+nomeArquivo+". Tente novamente");
        }  
    }

    /** 
     * Inicia um novo pedido.
     * Permite ao usuário escolher e incluir produtos no pedido.
     * @return O novo pedido
    */
    public static Pedido iniciarPedido() {
        Pedido novo = null;
        int opcao = -1;
        int indexProd = 0;
        int qtdeProdutosNoPedido = 0;
        
        novo = new Pedido();
        do {
            System.out.println("Informe o código do produto desejado ou 0 para Sair):");
            listarTodosOsProdutos();
            System.out.println("Código: ");
            indexProd = Integer.parseInt(teclado.nextLine());
            if(indexProd > 0 && indexProd <= produtosCadastrados.length) {
                if(produtosCadastrados[indexProd - 1] != null) {
                    qtdeProdutosNoPedido = novo.incluirProduto(produtosCadastrados[indexProd-1]);
                    System.out.println("Produto " + indexProd + " incluido no pedido.");
                    if(qtdeProdutosNoPedido == MAX_NOVOS_PRODUTOS) {
                        System.out.println("Atingida a quantidade máxima de produtos por pedido.");
                        System.out.println("Por favor, finalize o pedido.");
                        opcao = 0;
                        pausa();
                    }
                } else {
                    System.out.println("Código de produto inválido");
                }
            } else {
                opcao = 0;
            }
        } while(opcao != 0); 

        return novo;
    }

    /**
     * Finaliza um pedido, momento no qual ele deve ser armazenado em um vetor de pedidos.
     * @param pedido O pedido que deve ser finalizado.
     */
    public static void finalizarPedido(Pedido pedido) {
        if(pedido != null) {
            pedidosCadastrados.enfileirar(pedido);
            quantPedidos++;
        } else {
            System.out.println("Pedido vazio.");
        }

        
    }

     /**
     * Salva os dados dos pedidos cadastrados no arquivo csv informado. Sobrescreve todo o conteúdo do arquivo, se ele já existir.
     * São gravados no arquivo-texto os resumos dos pedidos, um em cada linha do arquivo.
     * @param nomeArquivo Nome do arquivo a ser gravado.
     */
    public static void salvarPedidos(String nomeArquivo) {
        try {
            FileWriter arquivoSaida = new FileWriter(nomeArquivo, Charset.forName("UTF-8"));
            arquivoSaida.append(quantPedidos + "\n");
        
            while(!pedidosCadastrados.vazia())
                arquivoSaida.append(pedidosCadastrados.desenfileirar().resumo()+"\n");
            
            arquivoSaida.close();    
            System.out.println("Arquivo "+nomeArquivo+" salvo.");
        } catch (IOException e) {
            System.out.println("Problemas no arquivo "+nomeArquivo+". Tente novamente");
        }  
    }


    public static void exibirValorMedioPedidos(int quantidade) {
        if (pedidosCadastrados.vazia()) {
            System.out.println("Não há pedidos cadastrados.");
            return;
        }
    
        double valorMedio = pedidosCadastrados.calcularValorMedio(Pedido::getValorTotal, quantidade);
        System.out.printf("O valor total médio dos %d primeiros pedidos é: R$ %.2f%n", quantidade, valorMedio);
    }

    public static void exibirPedidosComValorAcima(double valor, int quantidade) {
        if (pedidosCadastrados.vazia()) {
            System.out.println("Não há pedidos cadastrados.");
            return;
        }
    
        Lista<Pedido> pedidosFiltrados = pedidosCadastrados.filtrar(p -> p.getValorTotal() > valor, quantidade);
        if (pedidosFiltrados.vazia()) {
            System.out.println("Nenhum pedido encontrado com valor acima de R$ " + valor);
        } else {
            System.out.println("Pedidos com valor acima de R$ " + valor + ":");
            for (int i = 0; i < pedidosFiltrados.tamanho(); i++) {
                System.out.println(pedidosFiltrados.remover(0).resumo());
            }
        }
    }

    public static void exibirPedidosComProduto(String nomeProduto, int quantidade) {
        if (pedidosCadastrados.vazia()) {
            System.out.println("Não há pedidos cadastrados.");
            return;
        }
    
        Lista<Pedido> pedidosFiltrados = pedidosCadastrados.filtrar(
            p -> p.contemProduto(nomeProduto), quantidade
        );
    
        if (pedidosFiltrados.vazia()) {
            System.out.println("Nenhum pedido encontrado contendo o produto: " + nomeProduto);
        } else {
            System.out.println("Pedidos contendo o produto \"" + nomeProduto + "\":");
            for (int i = 0; i < pedidosFiltrados.tamanho(); i++) {
                System.out.println(pedidosFiltrados.remover(0).resumo());
            }
        }
    }



    public static void listarProdutosPedidosRecentes(int K) {
        if (produtosPedidosRecentes.vazia()) {
            System.out.println("Não há produtos vendidos recentemente.");
            return;
        }
    
        Pilha<Produto> subPilha = produtosPedidosRecentes.subPilha(K);
        System.out.println("Produtos mais recentemente vendidos:");
        while (!subPilha.vazia()) {
            System.out.println(subPilha.desempilhar());
        }
    }

    public static void main(String[] args) {
		
        teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "produtos.txt";
        String nomeArquivoPedidos = "dadosPedidos.csv";
        produtosCadastrados = lerProdutos(nomeArquivoDados);
        pedidosCadastrados = new Fila<>();
        Pedido pedido = null;
        
        int opcao = -1;
      
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> listarTodosOsProdutos();
                case 2 -> localizarProdutos();
                case 3 -> cadastrarProduto();
                case 4 -> pedido = iniciarPedido();
                case 5 -> finalizarPedido(pedido);
                case 6 -> {
        System.out.print("Digite a quantidade de pedidos para calcular o valor médio: ");
        int quantidade = Integer.parseInt(teclado.nextLine());
        exibirValorMedioPedidos(quantidade);
    }
    case 7 -> {
        System.out.print("Digite o valor mínimo dos pedidos: ");
        double valor = Double.parseDouble(teclado.nextLine());
        System.out.print("Digite a quantidade de pedidos a verificar: ");
        int quantidade = Integer.parseInt(teclado.nextLine());
        exibirPedidosComValorAcima(valor, quantidade);
    }
    case 8 -> {
        System.out.print("Digite o nome do produto a buscar nos pedidos: ");
        String nomeProduto = teclado.nextLine();
        System.out.print("Digite a quantidade de pedidos a verificar: ");
        int quantidade = Integer.parseInt(teclado.nextLine());
        exibirPedidosComProduto(nomeProduto, quantidade);
    }



                case 9 -> {
                    System.out.print("Digite a quantidade de produtos vendidos recentemente: ");
                    int K = Integer.parseInt(teclado.nextLine());
                    listarProdutosPedidosRecentes(K);
                }
                case 0 -> {
                    System.out.println("Saindo do sistema...");
        
            
            pausa();
        }while(opcao != 0);       

    }}}}
        salvarProdutos(nomeArquivoDados);
        salvarPedidos(nomeArquivoPedidos);
        teclado.close();    
    }
}
}}}}}}}}}}}
